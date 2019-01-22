package com.spider.task.call;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.SkuKey;
import com.spider.model.SpiderInfo;
import com.spider.service.SkuService;
import com.spider.task.GoodsParserTask;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class OtherCall extends BaseCall {
    private SkuService service = SkuService.getInstance();
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public OtherCall(long buyUrlId) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }
    public OtherCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    @Override
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
        DataRow jsonRow = t.queryMap("select json,keywords,fid,cid,user_id,goods_type,buy_url from "+table+" where buy_url_id=?",new Object[]{buyUrlId});
        String json = jsonRow.getString("json");
        service.clearSkuKey(buyUrlId);
        SpiderInfo info = JSONObject.parseObject(json,SpiderInfo.class);
        List<SkuKey> keys = new ArrayList<SkuKey>();
        if(info.getKeys()!=null){
            for(SkuKey key:info.getKeys()){
                if(StringHelper.isNotEmpty(key.getKey())&&StringHelper.isNotEmpty(key.getValue()))keys.add(key);
            }
        }
        insertSkuKeys(keys, buyUrlId);
        //插入商品信息
        t.update("delete from t_goods where buy_url_id=?",new Object[]{buyUrlId});
        DataRow form = new DataRow();
        String pddUrl = jsonRow.getString("buy_url");
        form.set("name", jsonRow.getString("keywords"));
        form.set("buy_url_id",buyUrlId);
        form.set("buy_url",pddUrl);
        form.set("url",pddUrl);
        form.set("state",1);
        form.set("title",info.getTitle());
        form.set("t_catalog1",jsonRow.getInt("fid"));
        form.set("t_catalog3",jsonRow.getInt("cid"));
        form.set("create_time",new Date());
        form.set("user_id", jsonRow.getInt("user_id"));
        form.set("goods_type", jsonRow.getInt("goods_type"));
        form.set("pic_state", 0);
        form.set("oss_state", 1);
        form.set("main_count", 5);
        form.set("sku_count", t.queryInt("select count(id) from t_sku_img where buy_url_id=?",new Object[]{buyUrlId}));
        form.set("detail_count", t.queryInt("select count(id) from t_detail_img where buy_url_id=?",new Object[]{buyUrlId}));
        t.insert("t_goods", form);
        notifyFinish();
    }

    @Override
    public void notifyError(String msg) {
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_OTHER_STATE,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state =? where buy_url_id=?",new Object[]{GoodsParserTask.SUCCESS_STATE,buyUrlId});
    }

    private void insertSkuKeys(List<SkuKey> keys, long buyUrlId) {
        if(keys==null||keys.size()==0){
            keys = new ArrayList<SkuKey>();
            SkuKey key = new SkuKey();
            key.setKey("风格");
            key.setValue("默认");
            keys.add(key);
        }
        DataRow form = new DataRow();
        form.set("buy_url_id", buyUrlId);
        for(SkuKey key:keys){
            String name = key.getKey().length()>200?key.getKey().substring(0, 200):key.getKey();
            String value = key.getValue().length()>400?key.getValue().substring(0, 400):key.getValue();
            form.set("keyname", name);
            form.set("keyvalue", value);
            form.set("type",1);
            t.insert("t_sku_keys", form);
        }
    }

    public static void main(String[] args) {
        OtherCall call = new OtherCall(85326179);
        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
