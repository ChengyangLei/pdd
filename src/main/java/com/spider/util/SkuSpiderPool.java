package com.spider.util;
import com.tomcong.config.Configuration;
import java.util.ArrayList;
import java.util.List;
public class SkuSpiderPool {
    private  final static  int debug = Configuration.getInt("model.debug",1);
    static List<String> ips = new ArrayList<String>();
    static int pos = 0;
    static {
        if(debug==1){
            ips.add("http://localhost:5000/sku/detail");
        }else{
            ips.add("http://www.benliubao.com:5000/sku/detail");
            ips.add("http://www.taojing666.net:5000/sku/detail");
            ips.add("http://114.67.81.12:5000/sku/detail");
            ips.add("http://114.67.81.10:5000/sku/detail");
            ips.add("http://114.67.81.17:5000/sku/detail");
            ips.add("http://114.67.88.46:5000/sku/detail");
            ips.add("http://114.67.85.28:5000/sku/detail");
            ips.add("http://114.67.75.143:5000/sku/detail");
            ips.add("http://114.67.75.142:5000/sku/detail");
            ips.add("http://114.67.95.22:5000/sku/detail");
        }
    }
    public static String getRestUrl(){
        /*
        if(pos==Integer.MAX_VALUE)pos=0;
        pos++;
        return ips.get(pos%ips.size());
        */
        return "http://114.67.94.86/sku/detail";
    }
}
