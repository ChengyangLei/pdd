package com.spider.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 芝麻代理
 */
public class ZmProxyUtil {
    /**
     * 辽宁省代号
     */
    private final static int lnProNum = 210000;
    /**
     * 江苏省代号
     */
    private final static int JsProNum = 320000;
    /**
     * 四川省代号
     */
    private final static int SqProNum = 510000;
    /**
     * 山东省代号
     */
    private final static int SdProNum = 370000;
    /**
     * 浙江省代号
     */
    private final static int ZjProNum = 330000;
    /**
     * 江西省代号
     */
    private final static int JxProNum = 360000;
    /**
     * 上海省代号
     */
    private final static int ShProNum = 310000;
    /**
     * 湖南省代号
     */
    private final static int HuNProNum = 430000;
    /**
     * 安徽省代号
     */
    private final static int AhProNum = 340000;
    /**
     * 云南省代号
     */
    private final static int YnProNum = 340000;
    /**
     * 福建省代号
     */
    private final static int FjProNum = 350000;
    /**
     * 河南省代号
     */
    private final static int HeNProNum = 410000;
    /**
     * 山西省代号
     */
    private final static int SxProNum = 140000;
    /**
     * 广东省代号
     */
    private final static int GdProNum = 440000;
    /**
     * 北京市代号
     */
    private final static int BjCityNum = 110000;
    /**
     * 甘肃省代号
     */
    private final static int GsPropNum = 620000;
    /**
     * 河北省代号
     */
    private final static int HeBPropNum = 130000;
    /**
     * 湖北省代号
     */
    private final static int HuBPropNum = 420000;
    /**
     * 黑龙江省代号
     */
    private final static int HljPropNum = 230000;
    /**
     * 重庆市代号
     */
    private final static int CqCityNum = 500000;
    /**
     * 陕西省代号
     */
    private final static int SxPropNum = 610000;
    /**
     *
     */
    private static List<Integer> proNums = new ArrayList<Integer>();
    static {
        proNums.add(lnProNum);
        proNums.add(JsProNum);
        proNums.add(SqProNum);
        proNums.add(SdProNum);
        proNums.add(ZjProNum);

        proNums.add(JxProNum);
        proNums.add(ShProNum);
        proNums.add(HuNProNum);
        proNums.add(AhProNum);
        proNums.add(YnProNum);

        proNums.add(FjProNum);
        proNums.add(HeNProNum);
        proNums.add(SxProNum);
        proNums.add(GdProNum);
        proNums.add(BjCityNum);

        proNums.add(GsPropNum);
        proNums.add(HeBPropNum);
        proNums.add(HuBPropNum);
        proNums.add(HljPropNum);
        proNums.add(CqCityNum);

        proNums.add(SxPropNum);
    }
    /**
     *获取不同省份的ip池
     */
    public static List<String> getIpList(){
        List<String> list = new ArrayList<String>();
        for(Integer proNum:proNums){
            String ip = getProxyIp(proNum);
            if(StringHelper.isNotEmpty(ip))list.add(ip);
        }
        return list;
    }
    /**
     * 获取随机ip
     */
    public static String getRandom(){
          int pos = new Random().nextInt(proNums.size());
          return getProxyIp(proNums.get(pos));
    }
    //http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=37206&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=
    /**
     * 获取某一省份的ip
     */
    public static String getProxyIp(int propNum){
        String url =String.format("http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=%d&city=0&yys=0&port=1&pack=37206&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=",propNum);
        try {
            String result = HttpClientUtil.get(url);
            if (StringHelper.isEmpty(result)) return null;
            JSONObject obj = JSON.parseObject(result);
            if (obj.getIntValue("code") != 0) {
                return null;
            } else {
                JSONArray arr = obj.getJSONArray("data");
                if(arr!=null&&arr.size()>0){
                    JSONObject root = arr.getJSONObject(0);
                    String ip = String.format("%s:%s",root.getString("ip"), root.getIntValue("port"));
                    return ip;
                }
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getRandom());
    }
}
