package com.spider.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.spider.util.Message;
import com.spider.util.ViewUtil;
import com.tomcong.config.Configuration;
import com.tomcong.jdbc.JdbcTemplate;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.jdbc.session.SessionParams;
import com.tomcong.service.BaseService;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
import org.apache.http.HttpHost;

public class ProjectBaseService  extends BaseService{
	  int debug = Configuration.getInt("model.debug",1);
	 JdbcTemplate t = this.getJdbcTemplate("web");
	 protected final static int Q=80;
	 /**
	  * 主图,白底图,轮播图类型
	  */
	 protected final int SWIPE_IMG_TYPE =1;
	 /**
	  * 属性图类型
	  */
	 protected final int SKU_IMG_TYPE =3;
	 /**
	  * 详情图类型
	  */
	 protected final int DETAIL_IMG_TYPE =2;
	 String generateSwipeImgName(long buyUrlId){
		 return new StringBuffer().append(buyUrlId).append("-").append(SWIPE_IMG_TYPE).append("-").append(generateRandomWords()).append(".jpg").toString();
	 }
	 String generateSkuImgName(long buyUrlId){
		 return new StringBuffer().append(buyUrlId).append("-").append(SKU_IMG_TYPE).append("-").append(generateRandomWords()).append(".jpg").toString();
	 }
	 String generateDetailImgName(long buyUrlId){
		 return new StringBuffer().append(buyUrlId).append("-").append(DETAIL_IMG_TYPE).append("-").append(generateRandomWords()).append(".jpg").toString();
	 }
	 public String generateRandomWords(){
		    return System.currentTimeMillis() +""+ (new Random().nextInt(899) + 100);
		  }
	 protected  String formatPicUrlToLowerQuality(String url,int q){		 
		  if(url.contains("@"))url = url.substring(0, url.indexOf("@"));
		  url +="@"+ q+"q";
		  return url;
	}	
	 
	 protected  String formatBigPicTo50Quality(String url){
		  return formatPicUrlToLowerQuality(url,50);
	 }
	 protected String formatSwipeOrSkuPicUrlToLowerQuality(String url){		 
		  if(url.contains("@"))url = url.substring(0, url.indexOf("@"));
		  url += "@"+Q+"q";
		  return url;
	}
	public Message parseRuntime(RuntimeException e){
	 	String msg = e.getMessage();
	 	if(StringHelper.isEmpty(msg))msg = e.getCause().getMessage();
	 	return ViewUtil.errorMsg(msg);
	}
	public void saveSessionParam(List<SessionParams> sessions){
		if(sessions!=null&&sessions.size()!=0){
			String[]  sqlArray = new String[sessions.size()];
			List<Object[]> argList = new ArrayList<Object[]>(sessions.size());
			for(int i=0;i<sessions.size();i++){
				sqlArray[i]=sessions.get(i).getSql();
				argList.add(sessions.get(i).getArgs());
			}
			t.batchUpdateSql(sqlArray, argList);
		}
	}


    public void insertProxyLogger(HttpHost proxy) {
		try{
			DataRow form = new DataRow();
			form.set("ip",proxy.getHostName());
			form.set("port",proxy.getPort());
			form.set("create_date",new Date());
			this.getJdbcTemplate("web").insert("t_proxy_ip",form);
		}catch (JdbcException e){
			e.fillInStackTrace();
		}
    }
    public String parseJdbcExceptionMsg(JdbcException e){
        String errorMsg = e.getMessage();
        if(StringHelper.isEmpty(errorMsg))errorMsg=e.getCause().getMessage();
        return errorMsg;
    }
	public void notifyPicError(String url, String filename, String resp) {
		if(resp.length()>1000)resp = resp.substring(0,1000);
		DataRow form = new DataRow();
		form.set("url",url);
		form.set("filename",filename);
		form.set("create_date",new Date());
		form.set("msg",resp);
		try{
			this.getJdbcTemplate("web").insert("t_pic_error",form);
		}catch (JdbcException e){
			e.fillInStackTrace();
		}
	}
}
