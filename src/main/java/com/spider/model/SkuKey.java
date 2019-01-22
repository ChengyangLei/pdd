package com.spider.model;
import java.io.Serializable;
public class SkuKey  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	/*	*/
	public SkuKey(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "SkuKey [key=" + key + ", value=" + value + "]";
	}
	public SkuKey(){
		
	}

}
