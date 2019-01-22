package com.spider.util;
import java.util.ArrayList;
import java.util.List;
public class IpPool {
	static List<String> ips = new ArrayList<String>();
	static int pos = -1;
	static{
		 ips.add("http://www.benliubao.com:8000");
		 ips.add("http://www.taojing666.net:8000");
		 ips.add("http://114.67.95.22:8000");
		 ips.add("http://114.67.81.12:8000");
		 ips.add("http://114.67.81.10:8000");
		 ips.add("http://114.67.81.17:8000");
		 ips.add("http://114.67.88.45:8000");
		 ips.add("http://114.67.75.143:8000");
		 
	}
	static String getRestUrl(){
		  pos++;
		  if(pos==Integer.MAX_VALUE)pos=0;
		  return ips.get(pos%ips.size());
	}
	
	static String get(int p){
		return ips.get(p%ips.size());
	}
	public static void main(String[] args) {
		 for(int i = 0;i<100;i++){
			 String url = getRestUrl();
			 System.err.println("["+url+"]-"+pos);
		 }
	}

}
