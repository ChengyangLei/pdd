package com.spider.service;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.util.Date;
public class LoggerService extends ProjectBaseService{
	private static LoggerService service;
	public static LoggerService getInstance() {
		if (service == null) {
			service = new LoggerService();
		}
		return service;
	}
	public void notifyPicError(String url, String filename, String resp) {
		if(StringHelper.isEmpty(resp))return;
		if(resp.length()>1000)resp = resp.substring(0,1000);
		DataRow form = new DataRow();
		form.set("url",url);
		form.set("filename",filename);
		form.set("create_date",new Date());
		form.set("msg",resp);
		try{
			t.insert("t_pic_error",form);
		}catch (JdbcException e){
			e.fillInStackTrace();
		}
	}


    public void notifyProxyError(String proxyIp) {
	    try{
			t.update("update t_proxy_ip set state=1 where ip=? ",new Object[]{proxyIp});
        }catch (JdbcException e){
	        e.fillInStackTrace();
        }
    }

    public String nextId(String proxyIp){
	    return t.queryString("select ip from t_proxy_ip where ip !=? and state =0 order by error_count,create_date desc limit 0,1",new Object[]{proxyIp});
    }
}
