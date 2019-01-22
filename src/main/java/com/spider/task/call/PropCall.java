package com.spider.task.call;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.*;
import com.spider.service.SkuService;
import com.spider.service.UploadImgService;
import com.spider.task.GoodsParserTask;
import com.spider.util.AliOssUtil;
import com.spider.util.JdOssClientUtil;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.jdbc.session.SessionParams;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class PropCall  extends BaseCall {
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public PropCall(long buyUrlId) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }
    public PropCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    @Override
    public void call() {
        try{
            deal();
        }finally {
            threadSignal.countDown();
        }
    }

    @Override
    public void deal() {
        String json = t.queryString("select json from "+table+" where buy_url_id=?",new Object[]{buyUrlId});
        SpiderInfo info =  JSONObject.parseObject(json,SpiderInfo.class);
        clearProp(buyUrlId);
        clearSkuImgs(buyUrlId);
        List<SkuProp> propList = info.getProps();
        if(propList==null||propList.size()==0){
            notifyError("属性为空");
            return;
        }
        Date date = new Date();
        Set<String> skuImgs = new HashSet<String>();
        //保持批量插入在一个事务里
        List<SessionParams> sessions = new ArrayList<SessionParams>();
        for(SkuProp skuProp:propList){
            List<SkuPropValue> values = skuProp.getValues();
            if(values==null||values.size()==0)continue;
            long propId =skuProp.getPropId();
            String propName=skuProp.getPropName();
            DataRow form = new DataRow();
            form.set("prop_name", propName);
            form.set("buy_url_id",buyUrlId);
            form.set("f_id",0);
            form.set("prop_id", propId);
            form.set("value_id",propId);
            form.set("create_date",date);
            sessions.add(t.getInsertSql("t_sku_prop", form));
            for(SkuPropValue value:values){
                DataRow row = new DataRow();
                String valueId = value.getValueId();
                row.set("f_id",propId);
                row.set("buy_url_id",buyUrlId);
                row.set("prop_id",valueId);
                row.set("name",propName);
                row.set("prop_name",value.getName());
                if(StringHelper.isNotEmpty(value.getImg())&&!value.getImg().equalsIgnoreCase("null")){
                    String img = value.getImg();
                    if(!skuImgs.contains(img)){
                        row.set("state",0);
                        skuImgs.add(img);
                    }else {
                        row.set("state",1);
                    }
                    row.set("img",img);
                }
                row.set("value_id",String.format("%d:%s",propId,valueId));
                row.set("create_date",date);
                sessions.add(t.getInsertSql("t_sku_prop", row));
            }
        }
        if(skuImgs.size()==0){
            notifyError("属性图为空");
            return;
        }
        List<String> imgs = new ArrayList<String>();
        for(String img:skuImgs)imgs.add(img);
        try {
            saveSessionParam(sessions);
        }catch (JdbcException e){
            notifyError(e.getMessage());
            return;
        }
        List<UploadImg> uploadImgs = new ArrayList<UploadImg>();
        int len = imgs.size();
        int loop = len/5;
        if(len%5>0)loop++;
        for(int i=0;i<loop;i++){
            int max = (i+1)*5<len?(i+1)*5:len;
            uploadImgs.addAll(batchUpload(imgs.subList(i*5,max),buyUrlId));
        }

        if(uploadImgs==null||uploadImgs.size()==0){
            notifyError("属性图批量上传失败");
            return ;
        }
        Map<String,String> skuImgMap = new HashMap<String,String>();
        for(UploadImg uploadImg:uploadImgs){
            String url = existsSkuImg(buyUrlId,uploadImg.getOrgSize(),uploadImg.getFileSize());
            if(StringHelper.isNotEmpty(url))continue;
            try{
                saveSku(uploadImg);
                skuImgMap.put(uploadImg.getUrl(),uploadImg.getJdImg());
            }catch (Exception e){
                e.fillInStackTrace();
            }
        }
        try {
            callbackProp(buyUrlId, skuImgMap);
            notifyFinish();
        }catch (JdbcException e){
            notifyError(e.getMessage());
        }

    }
    public String existsSkuImg(long buyUrlId, long orgSize, long fileSize) {
        return t.queryString("select url1 from t_sku_img where buy_url_id=? and org_size=? and  file_size=?" ,new Object[]{buyUrlId,orgSize,fileSize});
    }
    public void saveSku(UploadImg uploadImg){
        if(uploadImg==null)return;
        uploadImg.setStatus(3);
        uploadImg.setState(2);
        DataRow data = new DataRow();
        if(uploadImg.getId()!=0)data.set("id", uploadImg.getId());
        if(StringHelper.isNotEmpty(uploadImg.getUrl()))data.set("url1", uploadImg.getUrl());
        if(StringHelper.isNotEmpty(uploadImg.getAliImg()))data.set("url2", uploadImg.getAliImg());
        if(StringHelper.isNotEmpty(uploadImg.getJdImg()))data.set("url3", uploadImg.getJdImg());
        if(uploadImg.getFileSize()!=0)data.set("file_size", uploadImg.getFileSize());
        if(uploadImg.getOrgSize()!=0)data.set("org_size", uploadImg.getOrgSize());
        if(0!=uploadImg.getBuyUrlId())data.set("buy_url_id", uploadImg.getBuyUrlId());
        if(uploadImg.getStatus()!=0)data.set("status", uploadImg.getStatus());
        if(uploadImg.getState()!=0)data.set("state", uploadImg.getState());
        data.set("create_date", new Date());
        if(StringHelper.isEmpty(uploadImg.getUrl()))throw new JdbcException("url1 miss");
        if(0==uploadImg.getBuyUrlId())throw new JdbcException("buy_url_id miss");
        if(uploadImg.getStatus()==0)throw new JdbcException("status miss");
        t.insert("t_sku_img", data);

    }

    @Override
    public void notifyError(String msg) {
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_PROP_STATE,msg,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state =?,msg='' where buy_url_id=?",new Object[]{GoodsParserTask.PRICE_STATE,buyUrlId});
    }

    /**
     * 回填prop的冗余字段url
     * @param buyUrlId
     */
    private void callbackProp(long buyUrlId,Map<String,String> skuImgMap) {
        List<DataRow> list = t.query("select id,img from t_sku_prop where buy_url_id=? and state=0 and img is not null",new Object[]{buyUrlId});
        for(DataRow data:list){
            String img = data.getString("img");
            if(skuImgMap.containsKey(img)){
                t.update("update t_sku_prop set url=? where id=?",new Object[]{skuImgMap.get(img),data.getLong("id")});
            }else {
                t.update("update t_sku_prop set state=? where id=?",new Object[]{1,data.getLong("id")});
            }
        }
       ;
    }

    /**
     * 批量上传sku图片
     * @param skuImgs
     * @return
     */
    private List<UploadImg> batchUpload(List<String> skuImgs,long buyUrlId) {
        Map<String,UploadImg> aliMap = AliOssUtil.batchUploadMainOrSkuImg(skuImgs,buyUrlId);
        if(aliMap==null||aliMap.size()==0)return null;
        /*
        List<String> jdImgs = new ArrayList<String>();
        for(Iterator<String> it = aliMap.keySet().iterator();it.hasNext();){
            jdImgs.add(aliMap.get(it.next()).getUrl());
        }
        Map<String,UploadResult> jdMap = JdOssClientUtil.batchUploadImgToJd(jdImgs);
        if(jdMap==null||jdMap.size()==0)return null;
        */
        List<UploadImg> list = new ArrayList<UploadImg>();
        List<String> marks = new ArrayList<String>();
        for(Iterator<String> it = aliMap.keySet().iterator();it.hasNext();){
            String img = it.next();
            UploadImg uploadImg = aliMap.get(img);
            uploadImg.setBuyUrlId(buyUrlId);
            uploadImg.setState(2);
            uploadImg.setStatus(3);
            uploadImg.setUrl(img);
            String mark = uploadImg.getMark();
            if(marks.contains(mark))continue;
            marks.add(mark);
            list.add(uploadImg);
        }
        return list;
    }

    public void saveSessionParam(List<SessionParams> sessions){
        if(sessions!=null&&sessions.size()!=0){
            String[]  sqlArray = new String[sessions.size()];
            List<Object[]> argList = new ArrayList<Object[]>(sessions.size());
            for(int i=0;i<sessions.size();i++){
                sqlArray[i]=sessions.get(i).getSql();
                argList.add(sessions.get(i).getArgs());
            }
            t.batchUpdateSql(sqlArray, argList);
        }
    }

    public void clearProp(long buyUrlId) {
        // TODO Auto-generated method stub
        long[] ids = t.queryLongArray("select id from t_sku_prop where buy_url_id=?", new Object[]{buyUrlId});
        t.update("delete from t_sku_prop where buy_url_id =?", new Object[]{buyUrlId});
        if(ids!=null&&ids.length>0){
            for(long id:ids){
                t.delete("t_sku_prop","id",id);
            }
        }
    }
    public void clearSkuImgs(long buyUrlId) {
        long[] ids = t.queryLongArray("select id from t_sku_img where buy_url_id =?",new Object[]{buyUrlId});
        if(ids!=null&&ids.length>0){
            for(long id:ids){
                t.delete("t_sku_img","id",id);
            }
        }
    }

    public static void main(String[] args) {
        PropCall propCall = new PropCall( 2804718380l);
        try {
            propCall.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
