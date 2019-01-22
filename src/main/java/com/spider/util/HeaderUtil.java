package com.spider.util;

import java.util.*;

public class HeaderUtil {
    private static List<String> agents = new ArrayList<String>();
    static {
        agents.add("Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
        agents.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
        agents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; rv:11.0) like Gecko)");
        agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1");
        agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070309 Firefox/2.0.0.3");
        agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20070803 Firefox/1.5.0.12");
        agents.add("Opera/9.27 (Windows NT 5.2; U; zh-cn)");
        agents.add("Mozilla/5.0 (Macintosh; PPC Mac OS X; U; en) Opera 8.0");
        agents.add("Opera/8.0 (Macintosh; PPC Mac OS X; U; en)");
        agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.12) Gecko/20080219 Firefox/2.0.0.12 Navigator/9.0.0.6");
        agents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0)");
        agents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
        agents.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C; .NET4.0E)");
        agents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Maxthon/4.0.6.2000 Chrome/26.0.1410.43 Safari/537.1");
        agents.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C; .NET4.0E; QQBrowser/7.3.9825.400)");
        agents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
        agents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.92 Safari/537.1 LBBROWSER");
        agents.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser");
        agents.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/3.0 Safari/536.11");
    }
    private static String connection = "Keep-Alive";
    private static String head_accept ="text/html";
    private static String head_accept_language ="en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3";
    public static Map<String,String> generateRandomHead(){
        Map<String,String> map = new HashMap<String,String>();
        String agent = agents.get(new Random().nextInt(agents.size()));
        map.put("Connection",connection);
        map.put("Accept",head_accept);
        map.put("Accept-Language",head_accept_language);
        map.put("User-Agent",agent);
        return map;
    }
    public static String generateRandomAngent(){
        return agents.get(new Random().nextInt(agents.size()));

    }
}
