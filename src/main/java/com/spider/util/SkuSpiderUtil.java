package com.spider.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spider.model.*;
import com.spider.service.GoodsService;
import com.spider.service.LoggerService;
import com.tomcong.util.StringHelper;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SkuSpiderUtil {
    private static LoggerService service = LoggerService.getInstance();

    /**
     * 查询拼多多商品详情页的所有信息
     * @param url
     * @return
     */
    public static SpiderInfo spiderInfoWithProxy(String url, String proxyIp){
        String buyUrlId = parseBuyUrlId(url);
        if(StringHelper.isEmpty(buyUrlId))throw new RuntimeException(String.format("[%s]非法的链接格式",url));
        String html = null;
        SpiderInfo info = null;
        /**/
        html = RedisUtil.get(Long.parseLong(buyUrlId));
        if(StringHelper.isNotEmpty(html)){
            try {
                info = spiderInfo(Jsoup.parse(html), buyUrlId,null);
                return info;
            }catch (RuntimeException e){
                RedisUtil.del(Long.parseLong(buyUrlId));
            }
        }
        if(StringHelper.isEmpty(proxyIp)){
            info = spiderInfoWithProxy(url,buyUrlId,null);
        }else {
            info =  spiderInfoWithProxy(url,buyUrlId,proxyIp);
        }
        if(info!=null)RedisUtil.setObject(Long.parseLong(buyUrlId),html);
        return info;

    }

    public static SpiderInfo spiderRedis(String url){
        String buyUrlId = parseBuyUrlId(url);
        if(StringHelper.isEmpty(buyUrlId))throw new RuntimeException(String.format("[%s]非法的链接格式",url));
        String html = null;
        SpiderInfo info = null;
        /**/
        html = RedisUtil.get(Long.parseLong(buyUrlId));
        if(StringHelper.isNotEmpty(html)){
            try {
                info = spiderInfo(Jsoup.parse(html), buyUrlId,null);
                return info;
            }catch (RuntimeException e){
                RedisUtil.del(Long.parseLong(buyUrlId));
            }
        }else {
            GoodsService.getInstance().rollback(buyUrlId);
        }
        return null;

    }
    private static Document getDoc(String url, String proxyIp) {
        if(StringHelper.isEmpty(proxyIp))return getDoc(url);
        String[] arr =proxyIp.split(":");
        String host =String.format("%s",arr[0]);
        int port = Integer.parseInt(arr[1]);
            try {
                String html = HttpClientUtil.get(url,new HttpHost(host,port));
                return Jsoup.parse(html);
            } catch (Exception e) {
                service.notifyProxyError(proxyIp);
            }
            return null;
    }
    private static Document getDoc(String url){
        String html =AbsProxyUtil.spider(url);
        if(StringHelper.isEmpty(html))return null;
        return Jsoup.parse(html);
    }

    public static SpiderInfo spiderInfo(Document doc, String buyUrlId, String proxyIp){
        if(doc==null)return null;
        String url = doc.baseUri();
        SpiderInfo info = new SpiderInfo();
        Elements jsEles = doc.select("script");
        if(jsEles==null||jsEles.size()==0){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法抓取script数据信息",url));
        }
        String jsData=null;
        for(int i=0;i<jsEles.size();i++){
            String jsContent = jsEles.get(i).html();
            if(jsContent.contains("window.rawData=")){
                int pos = jsContent.indexOf("window.rawData=")+"window.rawData=".length();
                jsData =jsContent.substring(pos);
                break;
            }
        }
        if(StringHelper.isEmpty(jsData)){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法抓取js数据信息",url));
        }
        jsData =jsData.replace("\\n","");
        if(jsData.contains("\"needLogin\":true")){
            if(StringHelper.isEmpty(proxyIp))throw new RuntimeException("needLogin");
            service.notifyProxyError(proxyIp);
            doc = getDoc(url);
            return spiderInfo(doc,buyUrlId,null);
        }
        if(jsData.endsWith(";"))jsData=jsData.substring(0,jsData.length()-1);
        jsData =jsData.trim();
        JSONObject root = null;
        try {
            root = JSON.parseObject(jsData);
        }catch (RuntimeException e){

        }
        if(root==null||!root.getBoolean("isFinishInitLoading")||!root.containsKey("initDataObj")){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法加载js数据信息",url));
        }
        root = root.getJSONObject("initDataObj");
        //如果需要跳转登录页,说明爬虫被反
        if(root.containsKey("needLogin")&&root.getBoolean("needLogin")){
            if(StringHelper.isEmpty(proxyIp))throw new RuntimeException("needLogin");
            service.notifyProxyError(proxyIp);
            doc = getDoc(url);
            return spiderInfo(doc,buyUrlId,null);
        }
        JSONObject goodsJson = root.getJSONObject("goods");
        if(goodsJson==null){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("请求地址[%s]json结果异常[%s]",url,JSON.toJSONString(root,SerializerFeature.PrettyFormat)));
        }
        String title = goodsJson.getString("goodsName");
        //求取标题信息
        if(StringHelper.isEmpty(title)){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法抓取标题信息",url));
        }
        info.setTitle(title);
        //求关键词信息
        JSONArray keyArray = root.getJSONArray("goodsProperty");
        if(keyArray!=null&&keyArray.size()>0){
            List<SkuKey> keyList = new ArrayList<SkuKey>();
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
            info.setKeys(keyList);
        }
        //求详情信息
        Elements liEls = doc.select("li.gd-item");
        String desc = null;
        if(liEls==null||liEls.size()==0){
            //throw new RuntimeException(String.format("[%s]无法抓取详情信息",url));
            JSONArray detailImgArray = goodsJson.getJSONArray("detailGallery");
            if(detailImgArray!=null||detailImgArray.size()>0){}
            desc = createDetailDiv(detailImgArray);
        }else{
            desc =liEls.first().parent().parent().html();
        }

        if(StringHelper.isEmpty(desc)){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法抓取详情信息",url));
        }
        info.setDesc(desc);
        //求主图
        JSONArray mainImgArray =goodsJson.getJSONArray("topGallery");
        if(mainImgArray==null||mainImgArray.size()==0){
            service.notifyProxyError(proxyIp);
            throw new RuntimeException(String.format("[%s]无法抓取主图信息",url));
        }
        Set<String> mainImgs = new HashSet<String>();
        for(int i=0;i<mainImgArray.size();i++){
            String img = mainImgArray.getString(i);
            if(!img.startsWith("http"))img=String.format("http:%s",img);
            mainImgs.add(img);
        }
        if(mainImgs.size()<5){
            throw new RuntimeException(String.format("[%s]主图信息低于5张",url));
        }
        info.setMainImgs(mainImgs);
        //求属性信息
        List<SkuProp> propList = new ArrayList<SkuProp>();
        Map<String,SkuProp> propMap = new HashMap<String,SkuProp>();
        Map<String,SkuPropValue> propValueMap = new HashMap<String,SkuPropValue>();
        List<SkuPrice> priceList = new ArrayList<SkuPrice>();
        JSONArray skuArray = goodsJson.getJSONArray("skus");
        if(skuArray==null||skuArray.size()==0)throw new RuntimeException(String.format("[%s]无法抓取sku信息",url));
        for(int i=0;i<skuArray.size();i++){
            JSONObject skuObj = skuArray.getJSONObject(i);
            JSONArray  propArray = skuObj.getJSONArray("specs");
            if(propArray==null&&propArray.size()==0)continue;
            SkuPrice skuPrice = new SkuPrice();
            skuPrice.setSkuid(skuObj.getLongValue("skuID"));
            skuPrice.setPrice(skuObj.getDoubleValue("normalPrice"));
            skuPrice.setSkuQuantity(skuObj.getIntValue("quantity"));
            skuPrice.setOriginalPrice(skuObj.getDoubleValue("groupPrice"));
            String img = skuObj.getString("thumbUrl");
            if(!img.startsWith("http"))img=String.format("http:%s",img);
            StringBuffer pvs = new StringBuffer();
            for(int j=0;j<propArray.size();j++){
                JSONObject propObj =propArray.getJSONObject(j);
                String propName = propObj.getString("spec_key");
                String propValueName = propObj.getString("spec_value");
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
                    if(j==0) spv.setImg(img);
                    if(!propValueMap.containsKey(propValueName))propValueMap.put(propValueName,spv);
                    values.add(spv);
                    skuProp.setValues(values);
                    propMap.put(propName,skuProp);
                    pvs.append(";").append(String.format("%s:%s",propIdStr,valueId));
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
                        if(j==0)spv.setImg(img);
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
            skuPrice.setPvs(pvs.substring(1));
            priceList.add(skuPrice);
        }
        if(propValueMap.size()==0||propMap.size()==0)throw new RuntimeException(String.format("[%s]无法抓取属性信息",url));
        if(priceList.size()==0)throw new RuntimeException(String.format("[%s]无法抓取sku价格信息",url));
        info.setPrices(priceList);
        //求取sku属性信息
        for(Iterator<String> it = propMap.keySet().iterator();it.hasNext();){
            propList.add(propMap.get(it.next()));
        }
        info.setProps(propList);
        RedisUtil.setObject(Long.parseLong(buyUrlId),doc.toString());
        return info;
    }

    public static SpiderInfo spiderInfoWithProxy(String url, String buyUrlId, String proxyIp){
        Document doc = getDoc(url,proxyIp);
        return spiderInfo(doc,buyUrlId,proxyIp);

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



    public static String parseBuyUrlId(String url) {
        Pattern p = Pattern.compile("\\?goods_id=\\d+&");
        Matcher m =p.matcher(url);
        if(m.find()){
            String buyUrlId =  m.group(0);
            buyUrlId =buyUrlId.replace("?goods_id=","");
            buyUrlId =buyUrlId.replace("&","");
            return buyUrlId;
        }
        return null;
    }

    /**
     * 查询拼多多关键词信息
     * @param doc
     * @return
     */
    private static List<SkuKey> parseSkuKey(Document doc) {
         List<SkuKey> list = new ArrayList<SkuKey>();
         Elements eles = doc.select("div.goods-details>div.goods-details-attr>div.attr-item");
         if(eles==null||eles.size()==0)return list;
         for(int i=0;i<eles.size();i++){
             Element ele = eles.get(i);
             String k = ele.select("div.attr-key").text();
             String v = ele.select("div.attr-value").text();
             if(StringHelper.isNotEmpty(k)&&StringHelper.isNotEmpty(v)){
                 list.add(new SkuKey(k.trim(),v.trim()));
             }
         }
         return list;
    }

    public static void run(long buyUrlId,String proxyIp) throws Exception {
        String url =service.getJdbcTemplate("web").queryString("select buy_url from pdd_goods_json where buy_url_id=?",new Object[]{buyUrlId});
        System.out.println(proxyIp);
        Map<String,String> headers = HeaderUtil.generateRandomHead();
        headers.put("host","mobile.yangkeduo.com");
        headers.put("Upgrade-Insecure-Requests","1");
        String html = HttpClientUtil.get(url,"utf-8",new HttpHost(proxyIp.split(":")[0],Integer.parseInt(proxyIp.split(":")[1])),30*1000,30*1000,null,headers);
        if(StringHelper.isEmpty(html))return;
        SpiderInfo info = spiderInfo(Jsoup.parse(html),String.valueOf(buyUrlId),null);
        if(info!=null){
            System.out.println(JSON.toJSONString(info, SerializerFeature.PrettyFormat));
            try {
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        String proxyIp = ZmProxyUtil.getRandom();
        System.out.println(proxyIp);
        try {
            run(265907276,proxyIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
