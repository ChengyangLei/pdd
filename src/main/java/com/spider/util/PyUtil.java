package com.spider.util;
import com.tomcong.util.StringHelper;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
public class PyUtil {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String url ="https://mobile.yangkeduo.com/goods2.html?goods_id=3378525662&ts=1543989855980&page_from=24&_wv=1&refer_share_id=74393bdb079241fead7040797a51201b&refer_share_uid=&refer_share_channel=qq&_wvx=10";
        String buyUrlId = SkuSpiderUtil.parseBuyUrlId(url);
        String key = URLEncoder.encode(url,"utf-8");
        Jedis jedis = RedisUtil.getJedis();
        String value = jedis.get(buyUrlId);
        System.out.println(value);
        if(StringHelper.isNotEmpty(value))jedis.del(buyUrlId);
        String cmd = String.format("C:\\Python35\\python C:\\Python35\\spider.py %s %s",key,buyUrlId);
        System.out.println(cmd);
        try {
            Runtime.getRuntime().exec(cmd);

        } catch (IOException e) {
            e.printStackTrace();
        }
        value = jedis.get(buyUrlId);
        int seconds = 0;
        while(StringHelper.isEmpty(value)&&seconds<10){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            seconds++;
            value = jedis.get(key);
        }
        //https%3A%2F%2Fmobile.yangkeduo.com%2Fgoods2.html%3Fgoods_id%3D3378525662%26ts%3D1543989855980%26page_from%3D24%26_wv%3D1%26refer_share_id%3D74393bdb079241fead7040797a51201b%26refer_share_uid%3D%26refer_share_channel%3Dqq%26_wvx%3D10
        System.out.println(String.format("%d:%s",seconds,value));
    }
}
