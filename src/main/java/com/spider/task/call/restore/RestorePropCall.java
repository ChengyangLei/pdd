package com.spider.task.call.restore;

import com.alibaba.fastjson.JSONObject;
import com.spider.model.*;
import com.spider.service.SkuService;
import com.spider.service.UploadImgService;
import com.spider.task.GoodsParserTask;
import com.spider.task.call.BaseCall;
import com.spider.util.AliOssUtil;
import com.spider.util.JdOssClientUtil;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.jdbc.session.SessionParams;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RestorePropCall extends BaseCall {
    private UploadImgService service = UploadImgService.getInstance();
    private SkuService skuService = SkuService.getInstance();
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public RestorePropCall(long buyUrlId) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }
    public RestorePropCall(long buyUrlId, CountDownLatch threadSignal) {
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
    public void deal() {}


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
        List<UploadImg> list = new ArrayList<UploadImg>();
        List<String> marks = new ArrayList<String>();
        for(Iterator<String> it = aliMap.keySet().iterator();it.hasNext();){
            String img = it.next();
            UploadImg uploadImg = aliMap.get(img);
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

    public static void main(String[] args) {
        RestorePropCall propCall = new RestorePropCall( 130412278);
        try {
            propCall.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
