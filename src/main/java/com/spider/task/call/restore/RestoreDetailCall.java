package com.spider.task.call.restore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.SpiderInfo;
import com.spider.model.UploadImg;
import com.spider.model.UploadResult;
import com.spider.service.SkuService;
import com.spider.service.UploadImgService;
import com.spider.task.GoodsParserTask;
import com.spider.task.call.BaseCall;
import com.spider.util.AliOssUtil;
import com.spider.util.JdOssClientUtil;
import com.spider.util.SkuParser;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RestoreDetailCall extends BaseCall {
    private SkuService service = SkuService.getInstance();
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public RestoreDetailCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    public RestoreDetailCall(long buyUrlId){
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

    @Override
    public void deal() {
        String[] imgs = t.queryStringArray("select url1 from t_detail_img where buy_url_id=?",new Object[]{buyUrlId});
        if(imgs!=null&&imgs.length>0){
            String html = createDetailDiv(imgs);
            insertHtml(html);
        }

    }

    public void notifyError(String msg){
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_DETAIL_STATE,msg,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state =? where buy_url_id=?",new Object[]{GoodsParserTask.OTHER_STATE,buyUrlId});
    }

    private static String createDetailDiv(String[] detailImgArray) {
        StringBuffer div = new StringBuffer("<div><ul>");
        for(int i =0;i<detailImgArray.length;i++){
            String img = detailImgArray[i];
            if(!img.startsWith("http"))img =String.format("http:%s",img);
            div.append("<li style='margin:0 auto'><img src='").append(img).append("'></li>");
        }
        div.append("</ul></div>");
        return div.toString();
    }
    public void insertHtml(String html){
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
        RestoreDetailCall call = new RestoreDetailCall(4251269796l);
        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
