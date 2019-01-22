package com.spider.service;
import com.alibaba.fastjson.JSONObject;
import com.spider.task.IpTask;
import com.spider.util.*;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.DateHelper;
import com.tomcong.util.StringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class IpService extends ProjectBaseService{
	private static IpService service;
	public static String ip;
	public static IpService getInstance() {
		if (service == null) {
			service = new IpService();
		}
		return service;
	}
    static {
        ip =ProxyUtil.getLocalIp();
    }
	public String getIp(int plat) {
		if(plat==1)return getAbsIp();
		if(plat==2)return getZiMaIp();
		return null;
	}
	public boolean getFlag(){
	    return debug==1||debug==0&&"114.67.88.45".equals(IpService.ip);
    }

	private String getZiMaIp() {
		List<String> list = ZmProxyUtil.getIpList();
		for(String ip:list){
			int count = t.queryInt("select count(id) from t_proxy_ip where ip=?",new Object[]{ip});
			if(count>0)continue;
			DataRow form = new DataRow();
			form.set("ip",ip);
			form.set("create_date",new Date());
			form.set("plat",2);
			try {
				t.insert("t_proxy_ip", form);
			}catch (JdbcException e){
				e.fillInStackTrace();
			}
		}
		return list.toString();
	}
	private boolean save(String proxyIp,int plat){
        int count = t.queryInt("select count(id) from t_proxy_ip where ip=?",new Object[]{proxyIp});
        if(count>0)return false;
        DataRow form = new DataRow();
        form.set("ip",proxyIp);
        form.set("create_date",new Date());
        form.set("plat",1);
        t.insert("t_proxy_ip",form);
        return true;
    }

	private String getAbsIp() {
		String result = AbsProxyUtil.spider("http://www.benliubao.com:10000/ip/anon/ip");
		if(StringHelper.isNotEmpty(result)){
			try{
				Message msg = JSONObject.parseObject(result,Message.class);
				if(msg.getCode()==0){
					 String ip =msg.getData().toString();
					 int count = t.queryInt("select count(id) from t_proxy_ip where ip=?",new Object[]{ip});
					 if(count>0)return ip;
                     DataRow form = new DataRow();
					 form.set("ip",ip);
					 form.set("create_date",new Date());
					 form.set("plat",1);
					 t.insert("t_proxy_ip",form);
					 return ip;
				}
			}catch (RuntimeException e){

			}
		}
		return null;
	}

    /**
     * 旗云代理
     * @param page
     * @return
     */
    public List<String> qiyun(int page) {
        String url = String.format("http://www.qydaili.com/free/?page=%d",page);
        String html =AbsProxyUtil.spider(url);
        if(StringHelper.isEmpty(html))return null;
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementById("content");
        if(ele==null)return null;
        Elements trs = ele.select("table.table-bordered>tbody>tr");
        if(trs==null||trs.size()==0)return null;
        List<String> proxyIpList = new ArrayList<String>();
        System.out.println(proxyIpList);
        for(int i=0;i<trs.size();i++){
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            if(tds==null||tds.size()==0)continue;
            String host = tds.get(0).text();
            String port = tds.get(1).text();
            String proxyIp = String.format("%s:%s",host,port);
            proxyIpList.add(proxyIp);
        }
        return validate(proxyIpList,1);
    }

    /**
     * 快代理
     * @param page
     * @return
     */
    public List<String> getKuaiProxy(int page) {
        String url = String.format("https://www.kuaidaili.com/free/inha/%d/",page);
        String html =AbsProxyUtil.spider(url);
        if(StringHelper.isEmpty(html))return null;
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementById("list");
        if(ele==null)return null;
        Elements trs = ele.select("table>tbody>tr");
        if(trs==null||trs.size()==0)return null;
        List<String> proxyIpList = new ArrayList<String>();
        for(int i=0;i<trs.size();i++){
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            if(tds==null||tds.size()==0)continue;
            String host = tds.get(0).text();
            String port = tds.get(1).text();
            String proxyIp = String.format("%s:%s",host,port);
            proxyIpList.add(proxyIp);
        }
        return validate(proxyIpList,3);
    }

    /**
     * 西刺代理(国内高匿)
     * @param page
     * @return
     */
    public List<String> xc(String url,int page){
        String html = null;
        try {
            html = HttpClientUtil.get(url,"utf-8",null,30000,30000,null, HeaderUtil.generateRandomHead());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if(StringHelper.isEmpty(html))return null;
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementById("ip_list");
        if(ele==null)return null;
        Elements trs = ele.select("tr");
        if(trs==null||trs.size()==0)return null;
        List<String> proxyIpList = new ArrayList<String>();
        for(int i=0;i<trs.size();i++){
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            if(tds==null||tds.size()==0)continue;
            String host = tds.get(1).text();
            String port = tds.get(2).text();
            String proxyIp = String.format("%s:%s",host,port);
            proxyIpList.add(proxyIp);
        }
        return validate(proxyIpList,4);

    }
    public List<String> merge(List<String> ips,int plat) {
        return validate(ips,plat);
    }
    public List<String> xcnn(int page) throws Exception {
        String url = String.format("https://www.xicidaili.com/nn/%d",page);
        return xc(url,page);

    }
    public List<String> xcnt(int page) {
        String url = String.format("https://www.xicidaili.com/nt/%d",page);
        return xc(url,page);
    }

    public List<String> xcwn(int page) {
        String url = String.format("https://www.xicidaili.com/wn/%d",page);
        return xc(url,page);
    }

    public List<String> xcwt(int page) {
        String url = String.format("https://www.xicidaili.com/wt/%d",page);
        return xc(url,page);
    }


    private List<String> validate(List<String> proxyIpList,int plat) {
        if(proxyIpList==null||proxyIpList.size()==0)return null;
        List<String> list = new ArrayList<String>();
        List<IpTask> tasks = new ArrayList<IpTask>();
        for(String proxyIp:proxyIpList){
            tasks.add(new IpTask(list,proxyIp));
        }
        List<String> results = new ArrayList<String>();
        for(String proxyIp:list){
            if(save(proxyIp,plat))results.add(proxyIp);
        }
        return results;

    }

    /**
     * 校验ip的有效性
     */
    public void run(){
        int pos = 0;
        String sql= "select ip from t_proxy_ip order by create_date limit ?,10";
        String[] ips = t.queryStringArray(sql,new Object[]{pos});
        while(ips!=null&&ips.length>0){
            List<String> list = new ArrayList<String>();
            List<IpTask> tasks = new ArrayList<IpTask>();
            for(String proxyIp:ips){
                tasks.add(new IpTask(list,proxyIp));
            }
            for(String ip:ips){
                if(!list.contains(ip)){
                    t.update("delete from t_proxy_ip where ip=?",new Object[]{ip});
                    System.out.println(String.format("[%s]删除过期失效ip[%s]", DateHelper.formatDate(new Date()),ip));
                }else {
                    t.update("update t_proxy_ip set sysn_date =now() where ip=?",new Object[]{ip});
                }
            }
            pos+=ips.length;
            ips = t.queryStringArray(sql,new Object[]{pos});
        }

    }
    public String[] getRecentAbledIp(int size) {
        return t.queryStringArray("select ip from t_proxy_ip where state = 0 order by error_count asc,create_date desc limit 0,?",new Object[]{size});
    }
    public void doGatherXc() {
        for(int i=0;i<3;i++){
            gather("xc/nn",i);
        }

        for(int i=0;i<3;i++){
            gather("xc/nt",i);
        }

        for(int i=0;i<3;i++){
            gather("xc/wn",i);
        }

        for(int i=0;i<3;i++){
            gather("xc/wt",i);
        }

    }
    private void gather(String url,int page){
        try {
            HttpClientUtil.get(String.format("http://114.67.93.230/ip/%s?page=%d",url,page));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doGatherKuai() {
        for(int i=0;i<3;i++){
            gather("kuai",i);
        }

    }

    public void doGatherQiyun() {
        for(int i=0;i<3;i++){
            gather("qiyun",i);
        }
    }

    public static void main(String[] args) {
        IpService service = IpService.getInstance();
        service.doGatherXc();
    }



}
