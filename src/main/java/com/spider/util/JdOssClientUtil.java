package com.spider.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spider.model.UploadResult;
import com.spider.service.LoggerService;
import com.tomcong.util.StringHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.spider.util.AliOssUtil.picHost;

public class JdOssClientUtil {
    private static HttpClient client = null;
    static String accessKey =  "1E8C35335E184E54FAA00E603132C044";
    static String secreteKey = "41BDCC09480E10998CB73F5E00F3D13C";
    // endpoint以华北-北京为例，其它region请按实际情况填写
    static String endPoint = "oss.cn-east-2.jcloudcs.com";
    static String prefix = "http://beibei.oss.cn-east-2.jcloudcs.com/";
    static String bucketName = "beibei";
    private static  LoggerService service = LoggerService.getInstance();
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }
    public static UploadResult doUpload(String url, String filename){
        return doUpload(url,filename,0);
    }
    private static UploadResult doUpload(String url, String filename,int errorCount){
        if(errorCount>=3)return null;
        Map<String,String> params = new HashMap<String,String>();
        params.put("url", url);
        params.put("filename", filename);
        try {
            String resp = HttpClientUtil.postParameters(picHost+"/jd", params);
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(url,filename,resp);
                return  doUpload(url,filename,++errorCount);
            }
            UploadResult result =  JSON.parseObject(resp, new TypeReference<UploadResult>() {});
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static UploadResult doUpload(byte[] bs,String key){
        Map<String, String> headers = new HashMap<String, String>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateStr = sdf.format(new Date());
        headers.put("Date",dateStr);
        String token = buildToken(dateStr,key);
        if(StringHelper.isEmpty(token))return null;
        String auth = buildAuth(token);
        headers.put("Authorization",auth);
        return HttpClientUtil.putPic(bs,headers,prefix+key);
    }

    private static String buildAuth(String token) {
        return new StringBuffer("jingdong ").append(accessKey).append(":").append(token).toString();
    }

    private static String buildToken(String date_str, String filename) {
        StringBuffer msgBuffer = new StringBuffer("PUT\nimage/jpeg\n");
        msgBuffer.append(date_str).append("\n/").append(bucketName).append("/").append(filename);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secreteKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            byte[] bs = mac.doFinal(msgBuffer.toString().getBytes("UTF-8"));
            return new String(Base64.encodeBase64(bs), "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    public static Map<String,UploadResult> batchUploadImgToJd(Collection<String> imgs) {
        try {
            return batchUploadImgToJd(imgs,0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String,UploadResult> batchUploadImgToJd(Collection<String> imgs, int errorCount) throws UnsupportedEncodingException {
        if(errorCount>3)return null;
        Map<String,UploadResult> results = new HashMap<String,UploadResult>();
        String restUrl = String.format("%s/upload/batchUploadImgToJd",picHost);
        StringBuffer url = new StringBuffer(restUrl);
        int pos =0;
        for(String img:imgs){
            if(pos==0){
                url.append("?");
            }else{
                url.append("&");
            }
            pos++;
            url.append("urls=").append(URLEncoder.encode(img,"utf-8"));
        }
        try {
            System.out.println(url);
            String resp = HttpClientUtil.get(url.toString());
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(imgs.toString(),"",resp);
                return batchUploadImgToJd(imgs,++errorCount);
            }
            JSONObject root = JSONObject.parseObject(resp);
            for(String img:imgs){
                if(root.containsKey(img)){
                    String resultJson =root.getString(img);
                    try{
                        UploadResult result = JSONObject.parseObject(resultJson,UploadResult.class);
                        if(result!=null)results.put(img,result);
                    }catch (RuntimeException e){
                        e.fillInStackTrace();
                    }
                }
            }
            return results;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.fillInStackTrace();
            service.notifyPicError(imgs.toString(),"",e.getMessage());
            return batchUploadImgToJd(imgs,++errorCount);
        }
    }

    public static void main(String[] args) {
        Set<String> urls = new HashSet<>();
        urls.add("http://t00img.yangkeduo.com/goods/images/2018-07-23/802e6c4188c6dcc1d50348393ad81e8f.jpeg?imageMogr2/strip%7CimageView2/2/w/1300/q/80");
        urls.add("http://t00img.yangkeduo.com/goods/images/2018-07-23/0526619cfec0f4e41643c4a9da889886.jpeg?imageMogr2/strip%7CimageView2/2/w/1300/q/80");
        urls.add("http://t00img.yangkeduo.com/goods/images/2018-07-23/f323ef9d27fb1c3bfccf03b7a9afe221.jpeg?imageMogr2/strip%7CimageView2/2/w/1300/q/80");
        urls.add("http://t00img.yangkeduo.com/goods/images/2018-09-03/1849d630f85bfebdbfbcc2456dad7011.jpeg?imageMogr2/strip%7CimageView2/2/w/1300/q/80");
        urls.add("http://t00img.yangkeduo.com/goods/images/2018-07-23/e46e04a6b504a67ef27a0385b9b56364.jpeg?imageMogr2/strip%7CimageView2/2/w/1300/q/80");
        long t1 = System.currentTimeMillis();
        Map<String,UploadResult> map = batchUploadImgToJd(urls);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        System.out.println(JSONObject.toJSONString(map, SerializerFeature.PrettyFormat));
    }


}
