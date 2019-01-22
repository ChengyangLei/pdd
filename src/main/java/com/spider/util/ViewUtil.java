package com.spider.util;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
public class ViewUtil {
	public static Message errorMsg(String msg){
		  return new Message(msg);
	}
	public static Message errorMsg(String msg,int code){
		  return new Message(msg,code);
	}
	public static Message successMsg(){
		return new Message();
	}
	public static Message successObj(Object obj){
		Message message =  new Message();
		message.setData(obj);
		return message;
	}
	public static Message successObj(Object obj,String msg){
		Message message =  new Message();
		message.setData(obj);
		message.setMsg(msg);
		return message;
	}
	public static void printJsonObj(HttpServletResponse resp,Object obj){
		resp.setContentType("text/json; charset=utf-8");
		try {
			resp.getWriter().print(JSON.toJSON(obj));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void printStr(HttpServletResponse resp,String obj){
		resp.setContentType("text/html; charset=utf-8");
		try {
			resp.getWriter().print(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void printJson(HttpServletResponse resp,Message message){
		resp.setContentType("text/json; charset=utf-8");
		try {
			resp.getWriter().print(JSON.toJSON(message));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void printSuccessDefaultJson(HttpServletResponse resp){
		resp.setContentType("text/json; charset=utf-8");
		try {
			resp.getWriter().print(JSON.toJSON(new Message()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void printErrorJson(HttpServletResponse resp,String msg){
		resp.setContentType("text/json; charset=utf-8");
		try {
			resp.getWriter().print(JSON.toJSON(new Message(msg)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printErrorJson(HttpServletResponse resp,String msg,int code){
		resp.setContentType("text/json; charset=utf-8");
		try {
			resp.getWriter().print(JSON.toJSON(new Message(msg,code)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Message formatToObject(String result){
		return JSON.parseObject(result, new TypeReference<Message>() {});
	}
}
