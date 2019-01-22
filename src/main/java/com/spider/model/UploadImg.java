package com.spider.model;

public class UploadImg {
	private long id;
	/**
	 * 原始图片
	 */
	private String url;
	/**
	 * 阿里的图片,url2,为商品的原始图片
	 */
	private  String aliImg;
	/**
	 * 京东的图片,为商品的压缩图片，也是发布图片
	 */
	private String jdImg;
	/**
	 * 阿里云图片aliImg的大小
	 */
	private long orgSize;
	/**
	 * 京东云图片jdImg的大小
	 */
	private long fileSize;
	private long buyUrlId;
	private int status =1;
	private int state;
	private int w=0;
	private int h = 0;
	/**
	 * 图片指纹,同一商品内若orgSize和fileSize大小相同,则认为是同一张图片,忽略不计
	 */
	private String mark;
	public String getAliImg() {
		return aliImg;
	}
	public void setAliImg(String aliImg) {
		this.aliImg = aliImg;
	}
	public String getJdImg() {
		return jdImg;
	}
	public void setJdImg(String jdImg) {
		this.jdImg = jdImg;
	}
	public long getOrgSize() {
		return orgSize;
	}
	public void setOrgSize(long orgSize) {
		this.orgSize = orgSize;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * 一条图片信息数据的指纹,若图片大小一样,并且压缩大小也一样则认为是相同的图片
	 * @return
	 */
	public String getMark() {
		return String.valueOf(orgSize)+"-"+String.valueOf(fileSize);
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public UploadImg(String aliImg, String jdImg, long orgSize, long fileSize) {
		this.aliImg = aliImg;
		this.jdImg = jdImg;
		this.orgSize = orgSize;
		this.fileSize = fileSize;
	}
	public UploadImg() {
		// TODO Auto-generated constructor stub
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public long getBuyUrlId() {
		return buyUrlId;
	}

	public void setBuyUrlId(long buyUrlId) {
		this.buyUrlId = buyUrlId;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}
}
