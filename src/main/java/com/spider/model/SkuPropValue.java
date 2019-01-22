package com.spider.model;

public class SkuPropValue {
	  private String name;
	  private String img;
	  private String valueId;
	  private long fid=0;
	  private String url;
	  private int state;
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getValueId() {
		return valueId;
	}
	public void setValueId(String valueId) {
		this.valueId = valueId;
	}
	public long getFid() {
		return fid;
	}
	public void setFid(long fid) {
		this.fid = fid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public SkuPropValue(String name, String img, String valueId, long fid) {
		this.name = name;
		this.img = img;
		this.valueId = valueId;
		this.fid = fid;
	}
	
	public SkuPropValue() {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "SkuPropValue [fid=" + fid + ", img=" + img + ", name=" + name
				+ ", valueId=" + valueId + "]";
	}
	
	  

}
