package com.spider.util;
import com.alibaba.fastjson.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class UserSpider {
    //校验数据,校验失败记录日志给用户自己查阅
    public static Message validate(String jdUrl,int goodsType,long userId,String keywords,long fid,long cid){
        try {
            Long buyUrlId = parseBuyUrlId(jdUrl);
            if(buyUrlId==null)return ViewUtil.errorMsg("非法链接");
            jdUrl = URLEncoder.encode(jdUrl,"utf-8");
            keywords = URLEncoder.encode(keywords,"utf-8");
            String url = String.format("http://114.67.94.86/beibei/spider?keywords=%s&buyUrlId=%d&fid=%d&cid=%d&userId=%d&goodsType=%d",keywords,buyUrlId,fid,cid,userId,goodsType);
            try {
                String result = HttpClientUtil.get(url);
                Message msg = JSONObject.parseObject(result,Message.class);
                return msg;
            } catch (Exception e) {
                e.printStackTrace();
                return ViewUtil.errorMsg(e.getMessage());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }

    }
    //分解数据(数据校验完成后分解),校验成功提示用户,失败则记录日志
    public static Message parseJd(long buyUrlId){
        String url =String.format("http://114.67.94.86/beibei/parseJd?buyUrlId=%d",buyUrlId);
        String result = null;
        try {
            result = HttpClientUtil.get(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        Message msg = JSONObject.parseObject(result,Message.class);
        return msg;
    }
    private static Long parseBuyUrlId(String url) {
        Pattern p = Pattern.compile("\\/\\d+.html");
        Matcher m =p.matcher(url);
        if(m.find()){
            String buyUrlId =  m.group(0).substring(1);
            buyUrlId =buyUrlId.replace(".html","");
            return Long.parseLong(buyUrlId);
        }
        return null;
    }
}
