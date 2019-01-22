package com.spider.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tomcong.util.StringHelper;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class ProxyUtil {
    public final static  String url ="http://http.tiqu.alicdns.com/getip3?num=1&type=2&pro=&city=0&yys=0&port=1&time=1&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=2&regions=";
    public final static int maxErrorCount =5;
    public static HttpHost getProxyIp(){
        return getProxyIp(0);
    }
    private static HttpHost getProxyIp(int errorCount){
        if(errorCount>maxErrorCount)return null;
        try {
            String result =HttpClientUtil.get(url);
            if(StringHelper.isEmpty(result))return null;
            JSONObject obj = JSON.parseObject(result);
            if(obj.getIntValue("code")!=0){
                Thread.sleep(2*1000);
                return getProxyIp(++errorCount);
            }else{
                JSONObject root =obj.getJSONArray("data").getJSONObject(0);
                return new HttpHost(root.getString("ip"),root.getIntValue("port"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getProxyIp(++errorCount);
        }
    }
    public static String getLocalIp(){
        try {
            String result = HttpClientUtil.get("http://www.benliubao.com:10000/ip/anon/ip",getProxyIp());
            Message msg = JSONObject.parseObject(result,Message.class);
            return msg.getData().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
    static String appKey="38471464";
    static String secret ="788d13b37505105beb8f581fe7e595b2";
    static String proxyUrl ="s5.proxy.mayidaili.com";
    static int port =8123;
    public static String getAuthHeader(){
        // 创建参数表
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("app_key", appKey);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//使用中国时间，以免时区不同导致认证错误
        paramMap.put("timestamp", format.format(new Date()));
        // 对参数名进行排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 拼接有序的参数名-值串
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(secret);
        for(String key : keyArray){
            stringBuilder.append(key).append(paramMap.get(key));
        }

        stringBuilder.append(secret);
        String codes = stringBuilder.toString();

        // MD5编码并转为大写， 这里使用的是Apache codec
        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();

        paramMap.put("sign", sign);

        // 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接
        return "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
    }
    public static Document parseUrl(String url) throws IOException {
        return  Jsoup.connect(url).proxy(proxyUrl, port, null).header("Proxy-Authorization", getAuthHeader()).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
    }
    public static Document parseUrl(String url,String referrer) throws IOException {
        return  Jsoup.connect(url).proxy(proxyUrl, port, null).referrer(referrer).header("Proxy-Authorization", getAuthHeader()).followRedirects(true).validateTLSCertificates(false).timeout(10000).get();
    }


    public static void main(String[] args) {
        try {
            System.out.println(URLEncoder.encode("36.22.64.29:4740","utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
