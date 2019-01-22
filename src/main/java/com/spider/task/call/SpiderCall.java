package com.spider.task.call;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.*;
import com.spider.task.GoodsParserTask;
import com.spider.util.RedisUtil;
import com.spider.util.SkuProperty;
import com.tomcong.util.StringHelper;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class SpiderCall extends BaseCall {
    private Long buyUrlId;
    private CountDownLatch threadSignal;
    public SpiderCall(long buyUrlId) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }
    public SpiderCall(long buyUrlId, CountDownLatch threadSignal) {
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
        if(exists(buyUrlId)){
            notifyFinish();
            return ;
        }
        String jsData = RedisUtil.getJson(buyUrlId);
        SpiderInfo info = parse(jsData);
        if(info==null) return ;
        saveJson(buyUrlId, info);
    }

    private void saveJson(long buyUrlId,SpiderInfo info){
        String jsonText = JSONObject.toJSONString(info);
        t.update("update "+table+" set json=?,title=? ,state=?,msg=null where buy_url_id=? ",new Object[]{jsonText,info.getTitle(), GoodsParserTask.MAIN_STATE,buyUrlId});
    }
    private boolean exists(long buyUrlId){
        return t.queryInt("select count(*) from t_goods where pic_state>=0 and buy_url_id=?",new Object[]{buyUrlId})>0;
    }

    @Override
    public void notifyError(String msg) {
        t.update("update pdd_goods_json set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_SPIDER_STATE,msg,buyUrlId});
    }


    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state =? where buy_url_id=?",new Object[]{GoodsParserTask.SUCCESS_STATE,buyUrlId});
    }

    private SpiderInfo parse(String jsData) {
        if(StringHelper.isEmpty(jsData)){
            notifyLackDataError();
            return null;
        }
        jsData =jsData.trim();
        SpiderInfo info = new SpiderInfo();
        JSONObject root = null;
        try {
            root = JSON.parseObject(jsData);
        }catch (RuntimeException e){
               notifyError(e.getMessage());
               return null;
        }
        if(root==null||!root.getBoolean("isFinishInitLoading")||!root.containsKey("initDataObj")){
            notifyError("无法加载js数据信息");
            return null;
        }
        root = root.getJSONObject("initDataObj");
        //如果需要跳转登录页,说明爬虫被反
        if(root.containsKey("needLogin")&&root.getBoolean("needLogin")){
            notifyError("needLogin");
            return null;
        }
        JSONObject goodsJson = root.getJSONObject("goods");
        if(goodsJson==null){
            notifyError("empty goods");
            return null;
        }
        String title = goodsJson.getString("goodsName");
        //求取标题信息
        if(StringHelper.isEmpty(title)){
            notifyError("无法抓取标题信息");
            return null;
        }
        info.setTitle(title);
        //求关键词信息
        JSONArray keyArray = root.getJSONArray("goodsProperty");
        List<SkuKey> keyList = new ArrayList<SkuKey>();
        if(keyArray!=null&&keyArray.size()>0){
            for(int i=0;i<keyArray.size();i++){
                JSONObject keyObj = keyArray.getJSONObject(i);
                String key = keyObj.getString("key");
                JSONArray values = keyObj.getJSONArray("values");
                if(values!=null&&values.size()>0){
                    SkuKey skuKey = new SkuKey();
                    skuKey.setKey(key);
                    skuKey.setValue(values.getString(0));
                    keyList.add(skuKey);
                }
            }
        }else {
            SkuKey skuKey = new SkuKey();
            skuKey.setKey("风格 ");
            skuKey.setValue("默认");
            keyList.add(skuKey);
        }
        info.setKeys(keyList);
        JSONArray detailImgArray = goodsJson.getJSONArray("detailGallery");
        if(detailImgArray==null||detailImgArray.size()==0){
            notifyError("没有详情图");
            return null;
        }
        //求详情信息
        String desc =  createDetailDiv(detailImgArray);;

        if(StringHelper.isEmpty(desc)){
            notifyError("无法抓取详情信息");
            return null;
        }
        info.setDesc(desc);
        //求主图
        JSONArray mainImgArray =goodsJson.getJSONArray("topGallery");
        if(mainImgArray==null||mainImgArray.size()==0||mainImgArray.size()<5){
            notifyError("主图不存在或低于5张");
            return null;
        }
        Set<String> mainImgs = new HashSet<String>();
        for(int i=0;i<mainImgArray.size();i++){
            String img = mainImgArray.getString(i);
            if(!img.startsWith("http"))img=String.format("http:%s",img);
            mainImgs.add(img);
        }
        if(mainImgs.size()<5){
            notifyError("主图去重后低于5张");
            return null;
        }
        info.setMainImgs(mainImgs);
        //求属性信息
        List<SkuProp> propList = new ArrayList<SkuProp>();
        Map<String,SkuProp> propMap = new HashMap<String,SkuProp>();
        Map<String,SkuPropValue> propValueMap = new HashMap<String,SkuPropValue>();
        List<SkuPrice> priceList = new ArrayList<SkuPrice>();
        JSONArray skuArray = goodsJson.getJSONArray("skus");
        if(skuArray==null||skuArray.size()==0){
            notifyError("无法抓取sku信息");
            return null;
        }
        for(int i=0;i<skuArray.size();i++){
            JSONObject skuObj = skuArray.getJSONObject(i);
            JSONArray  propArray = skuObj.getJSONArray("specs");
            if(propArray==null&&propArray.size()==0){
                notifyError("无法抓取sku属性信息");
                return null;
            }
            SkuProperty[] skuProperties = new SkuProperty[propArray.size()];
            for(int k=0;k<propArray.size();k++){
                SkuProperty skuProperty = new SkuProperty();
                JSONObject propObj =propArray.getJSONObject(k);
                String key = propObj.getString("spec_key");
                String value = propObj.getString("spec_value");
                skuProperty.setKey(key);
                skuProperty.setValue(value);
                skuProperties[k]=skuProperty;
            }
            Arrays.sort(skuProperties);
            SkuPrice skuPrice = new SkuPrice();
            skuPrice.setSkuid(skuObj.getLongValue("skuID"));
            skuPrice.setPrice(skuObj.getDoubleValue("normalPrice"));
            skuPrice.setSkuQuantity(skuObj.getIntValue("quantity"));
            skuPrice.setOriginalPrice(skuObj.getDoubleValue("groupPrice"));
            String img = skuObj.getString("thumbUrl");
            if(!img.startsWith("http"))img=String.format("http:%s",img);
            StringBuffer pvs = new StringBuffer();
            for(int j=0;j<skuProperties.length;j++){
                SkuProperty skuProperty =skuProperties[j];
                String propName = skuProperty.getKey();
                String propValueName = skuProperty.getValue();
                SkuPropValue spv = null;
                SkuProp skuProp = null;
                if(!propMap.containsKey(propName)){
                    String propIdStr = String.format("%s%d",buyUrlId,propMap.size());
                    long propId = Long.parseLong(propIdStr);
                    skuProp=new SkuProp();
                    skuProp.setPropId(propId);
                    skuProp.setPropName(propName);
                    List<SkuPropValue> values = new ArrayList<SkuPropValue>();
                    spv = new SkuPropValue();
                    String valueId = String.format("%s0",propIdStr);
                    spv.setValueId(valueId);
                    spv.setName(propValueName);
                    if(j==0) {
                        spv.setImg(img);
                    }else {
                        pvs.append(";");
                    }
                    if(!propValueMap.containsKey(propValueName))propValueMap.put(propValueName,spv);
                    values.add(spv);
                    skuProp.setValues(values);
                    propMap.put(propName,skuProp);
                    pvs.append(String.format("%s:%s",propIdStr,valueId));
                }else{
                    skuProp = propMap.get(propName);
                    List<SkuPropValue> values = skuProp.getValues();
                    if(propValueMap.containsKey(propValueName)){
                        spv = propValueMap.get(propValueName);
                    }else{
                        spv = new SkuPropValue();
                        String valueId = String.format("%d%d",skuProp.getPropId(),values.size());
                        spv.setValueId(valueId);
                        spv.setName(propValueName);
                        if(j==0){
                            spv.setImg(img);
                        }else {
                            pvs.append(";");
                        }
                        propValueMap.put(propValueName,spv);
                        values.add(spv);
                        skuProp.setValues(values);
                        propMap.put(propName,skuProp);
                    }
                    pvs.append(";").append(String.format("%d:%s",skuProp.getPropId(),spv.getValueId()));
                }
                if(j==0){
                    skuPrice.setValueId(String.format("%d:%s",skuProp.getPropId(),spv.getValueId()));
                    skuPrice.setImg(spv.getImg());
                }
            }
            skuPrice.setPvs(pvs.toString());
            priceList.add(skuPrice);
        }
        if(propValueMap.size()==0||propMap.size()==0){
            notifyError("无法抓取sku属性信息");
            return null;
        }
        if(priceList.size()==0){
            notifyError("sku价格信息校验后为空");
            return null;
        }
        info.setPrices(priceList);
        //求取sku属性信息
        for(Iterator<String> it = propMap.keySet().iterator();it.hasNext();){
            propList.add(propMap.get(it.next()));
        }
        info.setProps(propList);
        return info;
    }

    private void notifyLackDataError() {
        t.update("update "+table+" set state =6,msg=? where buy_url_id=?",new Object[]{"redis中暂无数据",buyUrlId});
    }

    /**
     * 为拼多多创建详情节点div
     * @param detailImgArray
     * @return
     */
    private static String createDetailDiv(JSONArray detailImgArray) {
        StringBuffer div = new StringBuffer("<div><ul>");
        for(int i =0;i<detailImgArray.size();i++){
            JSONObject imgObj = detailImgArray.getJSONObject(i);
            String img = imgObj.getString("url");
            if(!img.startsWith("http"))img =String.format("http:%s",img);
            div.append("<li style='margin:0 auto'><img src='").append(img).append("'></li>");
        }
        div.append("</ul></div>");
        return div.toString();
    }

    public static void main(String[] args) {
        SpiderCall call = new SpiderCall(3844503114l);
        try {
            call.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
