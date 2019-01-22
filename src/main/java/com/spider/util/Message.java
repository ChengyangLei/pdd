package com.spider.util;
public class Message {
	private int code ;
	private String msg;
	private Object data;
	public Message(){}
	public Message(String msg){
		this.code=-1;
		this.msg=msg;
	}
	public Message(String msg,int code){
		this.code=code;
		this.msg=msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String toString() {
		return "Message [code=" + code + ", data=" + data + ", msg=" + msg
				+ "]";
	}
	

}
