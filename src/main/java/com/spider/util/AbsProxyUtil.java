package com.spider.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
public class AbsProxyUtil {
    public static String spider(String targetUrl) {
        // 代理服务器
        String proxyServer = "http-dyn.abuyun.com";
        int proxyPort = 9020;

        // 代理隧道验证信息
        String proxyUser = "H90Q24J30ZHLS5BD";
        String proxyPass = "A19E1CE7D150C033";

        try {
            URL url = new URL(targetUrl);

            Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPass));

            // 创建代理服务器地址对象
            InetSocketAddress addr = new InetSocketAddress(proxyServer, proxyPort);
            // 创建HTTP类型代理对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

            // 设置通过代理访问目标页面
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

            // 解析返回数据
            byte[] response = readStream(connection.getInputStream());
            String result = new String(response);
            return result;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }
/**
 * 将输入流转换成字符串
 *
 * @param inStream
 * @return
 * @throws Exception
 */
        public static byte[] readStream(InputStream inStream) throws IOException {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;

            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();

            return outSteam.toByteArray();
        }

    public static void main(String[] args) {
       // System.out.println(spider("https://mobile.yangkeduo.com/goods.html?goods_id=4488160247&thumbnail=%2F%2Ft00img.yangkeduo.com%2Fgoods%2Fimages%2F2018-11-25%2F866a18c25eaaf517b8ce8f4f7f2848b3.jpeg%3FimageMogr2%2Fsharpen%2F1%257CimageView2%2F2%2Fw%2F1300%2Fq%2F70%2Fformat%2Fwebp&refer_"));
        for(int i=0;i<1;i++){
            System.out.println(spider("https://mobile.yangkeduo.com/goods.html?goods_id=4601270779&thumbnail=%2F%2Ft00img.yangkeduo.com%2Fgoods%2Fimages%2F2018-11-30%2F4d204b29f94692c6e1e6f3a984136064.jpeg%3FimageMogr2%2Fsharpen%2F1%257CimageView2%2F2%2Fw%2F1300%2Fq%2F70%2Fformat%2Fwebp&refer_"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    }
