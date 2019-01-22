package com.spider.task.call;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.SpiderInfo;
import com.spider.model.UploadImg;
import com.spider.service.UploadImgService;
import com.spider.task.GoodsParserTask;
import com.spider.util.AliOssUtil;
import com.spider.util.SkuParser;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class DetailCall extends BaseCall {
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public DetailCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    public DetailCall(long buyUrlId){
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }


    public void call(){
        try{
            deal();
        }catch (RuntimeException e){
            notifyError(e.getMessage());
        }finally {
            threadSignal.countDown();
        }
    }
    public void clearSkuDesc(long buyUrlId){
        t.update("delete from t_sku_desc where buy_url_id =?", new Object[]{buyUrlId});
    }

    @Override
    public void deal() {
        String json = t.queryString("select json from "+table+" where buy_url_id=?",new Object[]{buyUrlId});
        clearSkuDesc(buyUrlId);
        SpiderInfo info =JSONObject.parseObject(json,SpiderInfo.class);
        String desc = info.getDesc();
        List<String> detailImgs = SkuParser.parseDetailImgs(desc);
        if(detailImgs==null||detailImgs.size()==0){
            notifyError("商品无详情图");
            return ;
        }
        insertHtml(desc, buyUrlId);
        t.update("delete from t_detail_img where buy_url_id=? ",new Object[]{buyUrlId});
        int len =detailImgs.size();
        int loop =len/5;
        if(detailImgs.size()%5>0)loop++;
        int scount =0;
        for(int i=0;i<loop;i++){
            int max =5*(i+1)<len?5*(i+1):len;
            List<String> imgs =detailImgs.subList(i*5,max);
            scount+=this.insertDetailImgs(imgs);
        }
        if(scount==0){
            notifyError("商品详情图片插入异常");
            return ;
        }else {
            notifyFinish();
        }

    }

    public void notifyError(String msg){
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_DETAIL_STATE,msg,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state =? where buy_url_id=?",new Object[]{GoodsParserTask.OTHER_STATE,buyUrlId});
    }

    public int insertDetailImgs(List<String> detailImgs){
        Map<String,UploadImg> aliMap = AliOssUtil.batchUploadDetailImg(detailImgs,buyUrlId);
        if(aliMap==null||aliMap.size()==0){
            return 0;
        }
        /*
        if(aliMap==null||aliMap.size()==0)return 0;
        List<String> jdImgs = new ArrayList<String>();
        for(Iterator<String> it = aliMap.keySet().iterator(); it.hasNext();){
            jdImgs.add(aliMap.get(it.next()).getUrl());
        }
        Map<String,UploadResult> jdMap = JdOssClientUtil.batchUploadImgToJd(jdImgs);
        if(jdMap==null||jdMap.size()==0)return 0;
        */
        List<UploadImg> list = new ArrayList<UploadImg>();
        for(Iterator<String> it = aliMap.keySet().iterator();it.hasNext();){
            String img = it.next();
            UploadImg uploadImg = aliMap.get(img);
            uploadImg.setBuyUrlId(buyUrlId);
            uploadImg.setState(2);
            uploadImg.setStatus(3);
            uploadImg.setUrl(img);
            try{
                UploadImgService.getInstance().saveDetail(uploadImg);
                list.add(uploadImg);
            }catch (JdbcException e){
                e.fillInStackTrace();
            }
        }
        return list.size();
    }
    public void insertHtml(String html,long buyUrlId){
        if(StringHelper.isEmpty(html)){
            notifyError("详情信息为空!");
            return;
        }
        DataRow form = new DataRow();
        form.set("buy_url_id", buyUrlId);
        form.set("create_date",new Date());
        form.set("html_desc", html);
        t.insert("t_sku_desc", form);
    }

    public static void main(String[] args) {
        DetailCall call = new DetailCall(4251269796l);
        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
