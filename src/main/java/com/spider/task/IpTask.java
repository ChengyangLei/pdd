package com.spider.task;
import com.alibaba.fastjson.JSONObject;
import com.spider.util.HttpClientUtil;
import com.spider.util.Message;
import com.tomcong.util.StringHelper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.List;
public class IpTask extends BaseTask {
    private List<String> list;
    private String proxyIp;

    public IpTask(List<String> list, String proxyIp) {
        this.list = list;
        this.proxyIp = proxyIp;
    }

    public String call() throws Exception {
        String[] arr = proxyIp.split(":");
        if(arr==null||arr.length<2)return null;
        try {
            String result = get(new HttpHost(arr[0], Integer.parseInt(arr[1])));
            if (StringHelper.isNotEmpty(result)) {
                Message msg = JSONObject.parseObject(result,Message.class);
                if(msg.getCode()==0)list.add(proxyIp);
            }
        }catch (Exception e){
        }
        return null;
    }

    private static  String get(HttpHost proxy) throws Exception{
        HttpClient client = null;
        HttpGet get = new HttpGet(ipRestUrl);
        String result = "";
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            customReqConf.setConnectTimeout(10*1000);
            customReqConf.setSocketTimeout(10*1000);
            customReqConf.setProxy(proxy);
            get.setConfig(customReqConf.build());
            HttpResponse res = null;
            // 执行 Http 请求.
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(128);
            cm.setDefaultMaxPerRoute(128);
            client = HttpClients.custom().setConnectionManager(cm).build();
            res = client.execute(get);
            result = IOUtils.toString(res.getEntity().getContent(), "utf-8");
        } finally {
            get.releaseConnection();
            if (client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            System.out.println(get(new HttpHost("58.218.198.172",15542)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
