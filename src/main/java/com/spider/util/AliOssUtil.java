package com.spider.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.spider.model.UploadImg;
import com.spider.model.UploadResult;
import com.spider.service.LoggerService;

/**
 * 阿里云图片操作工具类
 * @author 高聪
 * 2016-11-01
 */
public class AliOssUtil {
	public static String IMG_ENDPOINT = "http://imgstoreforjd.img-cn-hangzhou.aliyuncs.com";
    static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    static String accessKeyId = "LTAI7Eb7g92QRJxe";
    static String accessKeySecret = "wrtXQEkzBFJr4a9a7Kqt5nX0U42h7b";
    static String bucketName = "imgstoreforjd";
    public static String  picHost ="http://114.67.94.112";
    private static LoggerService service = LoggerService.getInstance();
    /**
     * 图片上传最低尺寸
     */
    static long minPicSize = 5*1024;
    public static UploadResult upload(String url,String filename){
    	return upload(url,filename,0);
    }
    private static UploadResult upload(String url, String filename, int errorCount){
        if(errorCount>=3)return new UploadResult("上传失败3次以上");
        Map<String,String> params = new HashMap<String,String>();
        params.put("url", url);
        params.put("filename", filename);
        try {
            String restUrl = String.format("%s/ali",picHost);
            String resp = HttpClientUtil.postParameters(restUrl, params);
			if(resp.contains("Bad Gateway")){
                service.notifyPicError(url,filename,resp);
            	return  upload(url,filename,++errorCount);
			}
            UploadResult result =  JSON.parseObject(resp, new TypeReference<UploadResult>() {});
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            service.notifyPicError(url,filename,e.getMessage());
			return  upload(url,filename,++errorCount);
        }
    	 
    }
    private static UploadResult cut(String url, String filename,int h, int errorCount){
        if(errorCount>=3)return new UploadResult("上传失败3次以上");
        Map<String,String> params = new HashMap<String,String>();
        params.put("url", url);
        params.put("filename", filename);
        params.put("w","750");
        params.put("h",String.valueOf(h));
        try {
            String restUrl = String.format("%s/cutDetail",picHost);
            String resp = HttpClientUtil.postParameters(restUrl, params);
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(url,filename,resp);
                return  cut(url,filename,h,++errorCount);
            }
            UploadResult result =  JSON.parseObject(resp, new TypeReference<UploadResult>() {});
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            service.notifyPicError(url,filename,e.getMessage());
            return  cut(url,filename,h,++errorCount);
        }

    }

	public static UploadResult cut(String url, String filename,int h){
        return  cut(url,filename,h,0);

	}

	public static UploadResult resize(String url,String filename){
		return resize(url,filename,0);
	}

	public static UploadResult resize(String url,String filename,int errorCount){
		if(errorCount>=3)return null;
		Map<String,String> params = new HashMap<String,String>();
		params.put("url", url);
		params.put("filename", filename);
		try {
			String restUrl = String.format("%s/resize",picHost);
			String resp = HttpClientUtil.postParameters(restUrl, params);
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(url,filename,resp);
                return resize(url,filename,++errorCount);
            }
			UploadResult result =  JSON.parseObject(resp, new TypeReference<UploadResult>() {});
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
            service.notifyPicError(url,filename,e.getMessage());
			return resize(url,filename,++errorCount);
		}
	}


    public static Map<String,UploadImg> batchUploadMainOrSkuImg(Collection<String> skuImgs, long buyUrlId) {
        Map<String,UploadImg> map = new HashMap<String,UploadImg>();
        for(String img:skuImgs){
            UploadImg uploadImg = uploadMainOrSkuImg(img,buyUrlId);
            if(uploadImg!=null)map.put(img,uploadImg);
        }
        return map;
    }
    private static UploadImg uploadMainOrSkuImg(String img,long buyUrlId){
        try {
            return uploadMainOrSkuImg(img,buyUrlId,0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static UploadImg uploadDetail(String img,long buyUrlId){
        try {
            return uploadDetail(img,buyUrlId,0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static UploadImg uploadMainOrSkuImg(String img,long buyUrlId,int errorCount) throws Exception {
        String restUrl = String.format("%s/beibei/uploadMainOrSku?buyUrlId=%d",picHost,buyUrlId);
        StringBuffer url = new StringBuffer(restUrl);
        url.append("&url=").append(URLEncoder.encode(img,"utf-8"));
        System.out.println(url);
        String resp = HttpClientUtil.get(url.toString());
        if(resp.contains("Bad Gateway")){
            service.notifyPicError(img.toString(),String.valueOf(buyUrlId),resp);
            return uploadMainOrSkuImg(img,buyUrlId,++errorCount);
        }
        return JSONObject.parseObject(resp,UploadImg.class);
    }
    private static UploadImg uploadDetail(String img,long buyUrlId,int errorCount) throws Exception{
        String restUrl = String.format("%s/beibei/uploadDetail?buyUrlId=%d",picHost,buyUrlId);
        StringBuffer url = new StringBuffer(restUrl);
        url.append("&url=").append(URLEncoder.encode(img,"utf-8"));
        System.out.println(url);
        String resp = HttpClientUtil.get(url.toString());
        if(resp.contains("Bad Gateway")){
            service.notifyPicError(img.toString(),String.valueOf(buyUrlId),resp);
            return uploadDetail(img,buyUrlId,++errorCount);
        }
        return JSONObject.parseObject(resp,UploadImg.class);
    }
    private static Map<String,UploadImg> batchUploadMainOrSkuImg(Collection<String> imgs,long buyUrlId,int errorCount) throws UnsupportedEncodingException {
        if(errorCount>3)return null;
        Map<String,UploadImg> results = new HashMap<String,UploadImg>();
        String restUrl = String.format("%s/beibei/batchUploadMainOrSku?buyUrlId=%d",picHost,buyUrlId);
        StringBuffer url = new StringBuffer(restUrl);
        for(String img:imgs){
            url.append("&urls=").append(URLEncoder.encode(img,"utf-8"));
        }
        try {
            System.out.println(url);
            String resp = HttpClientUtil.get(url.toString());
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(imgs.toString(),String.valueOf(buyUrlId),resp);
                return batchUploadMainOrSkuImg(imgs,buyUrlId,++errorCount);
            }
            JSONObject root = JSONObject.parseObject(resp);
            for(String img:imgs){
                if(root.containsKey(img)){
                    String resultJson =root.getString(img);
                    try{
                        UploadImg result = JSONObject.parseObject(resultJson,UploadImg.class);
                        if(result!=null)results.put(img,result);
                    }catch (RuntimeException e){
                        e.fillInStackTrace();
                    }
                }
            }
            return results;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            service.notifyPicError(imgs.toString(),String.valueOf(buyUrlId),e.getMessage());
            return batchUploadMainOrSkuImg(imgs,buyUrlId,++errorCount);
        }
    }
    public static Map<String,UploadImg> batchUploadDetailImg(Collection<String> detailImgs, long buyUrlId) {
        Map<String,UploadImg> map = new HashMap<String,UploadImg>();
        for(String img:detailImgs){
            UploadImg uploadImg = uploadDetail(img,buyUrlId);
            if(uploadImg!=null)map.put(img,uploadImg);
        }
        return map;
    }

    private static Map<String,UploadImg> batchUploadDetailImg(Collection<String> imgs,long buyUrlId,int errorCount) throws UnsupportedEncodingException {
        if(errorCount>3)return null;
        Map<String,UploadImg> results = new HashMap<String,UploadImg>();
        String restUrl = String.format("%s/beibei/batchUploadDetail?buyUrlId=%d",picHost,buyUrlId);
        StringBuffer url = new StringBuffer(restUrl);
        for(String img:imgs){
            url.append("&urls=").append(URLEncoder.encode(img,"utf-8"));
        }
        try {
            System.out.println(url);
            String resp = HttpClientUtil.get(url.toString());
            if(resp.contains("Bad Gateway")){
                service.notifyPicError(imgs.toString(),String.valueOf(buyUrlId),resp);
                return batchUploadDetailImg(imgs,buyUrlId,++errorCount);
            }
            JSONObject root = JSONObject.parseObject(resp);
            for(String img:imgs){
                if(root.containsKey(img)){
                    String resultJson =root.getString(img);
                    try{
                        UploadImg result = JSONObject.parseObject(resultJson,UploadImg.class);
                        if(result!=null)results.put(img,result);
                    }catch (RuntimeException e){
                        e.fillInStackTrace();
                    }
                }
            }
            return results;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            service.notifyPicError(imgs.toString(),String.valueOf(buyUrlId),e.getMessage());
            return batchUploadDetailImg(imgs,buyUrlId,++errorCount);
        }
    }

    public static void main(String[] args) {
    	 UploadResult result = cut("https://img.alicdn.com/imgextra/i2/20003478/TB2MAirgvBNTKJjSszcXXbO2VXa_!!20003478.jpg","xx/xx.jpg",770,0);
    	 System.err.println(result);
    }
	
}
