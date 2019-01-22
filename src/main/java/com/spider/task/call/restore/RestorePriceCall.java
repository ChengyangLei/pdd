package com.spider.task.call.restore;

import com.alibaba.fastjson.JSONObject;
import com.spider.model.SkuPrice;
import com.spider.model.SpiderInfo;
import com.spider.service.SkuService;
import com.spider.task.GoodsParserTask;
import com.spider.task.call.BaseCall;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.jdbc.session.SessionParams;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RestorePriceCall extends BaseCall {
    private SkuService service = SkuService.getInstance();
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public RestorePriceCall(long buyUrlId) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }
    public RestorePriceCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    @Override
    public void call(){
        try{
            deal();
        }finally {
            threadSignal.countDown();
        }
    }

    @Override
    public void deal() {
        String json = t.queryString("select json from "+table+" where buy_url_id=?",new Object[]{buyUrlId});
        SpiderInfo info = JSONObject.parseObject(json,SpiderInfo.class);
        service.clearPrice(buyUrlId);
        List<SkuPrice> list = info.getPrices();
        if(list==null||list.size()==0){
            notifyError("价格信息异常");
            return ;
        }
        List<SessionParams> sessionParams = getInsertSkuPricesSession(list,buyUrlId);
        try {
            saveSessionParam(sessionParams);
            notifyFinish();
        }catch (JdbcException e){
            notifyError(e.getMessage());
        }
    }

    @Override
    public void notifyError(String msg) {
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_PRICE_STATE,msg,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        System.out.println(String.format("数据[%d]修复成功...",buyUrlId));
    }

    public List<SessionParams> getInsertSkuPricesSession(
            List<SkuPrice> priceList,long buyUrlId) {
        // TODO Auto-generated method stub
        List<SessionParams> sessions = new ArrayList<SessionParams>();
        Date date = new Date();
        for(SkuPrice price:priceList){
            String pvs = price.getPvs();
            if(pvs.startsWith(";"))pvs=pvs.substring(1,pvs.length());
            if(pvs.endsWith(";"))pvs =pvs.substring(0,pvs.length()-1);
            if(StringHelper.isEmpty(pvs))continue;
            String valueId = price.getValueId();
            String img = price.getImg();
            DataRow form = new DataRow();
            form.set("pvs", pvs);
            form.set("sku_id",price.getSkuid());
            form.set("sku_quantity",price.getSkuQuantity());
            form.set("original_price",price.getOriginalPrice());
            form.set("price",price.getPrice());
            form.set("edit_date", date);
            form.set("edit_stamp", date.getTime());
            form.set("buy_url_id",buyUrlId);
            form.set("type",1);
            if(StringHelper.isNotEmpty(valueId)){
                form.set("value_id",valueId);
                String url = t.queryString("select url3 from t_sku_img where buy_url_id =? and url1=?",new Object[]{buyUrlId,img});
                if(StringHelper.isEmpty(url)){
                    form.set("state",1);
                }else {
                    form.set("state",0);
                    form.set("img", url);
                }
            }else{
                form.set("state",1);
            }
            sessions.add(t.getInsertSql("t_sku_detail", form));
        }
        return sessions;
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
        RestorePriceCall call = new RestorePriceCall(1553671);
        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
