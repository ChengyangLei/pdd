package com.spider.model;

import java.io.Serializable;

public class JdGoodsDetail implements Serializable{
	private String cate1;
	  private String cate2;
	  private String cate3;
	  private double shopScore;
	  private String brand="";
	  private String articleNum="";
	  private String title;
	  private String cateUrl;
	  private long venId;
	  private int up;
	  private String shopType;
	  private String appId;
	  private long shopId;
	  private String shopName;
	  private String shopDescr;
	  private String transport;
	public String getCate1() {
		return cate1;
	}
	public void setCate1(String cate1) {
		this.cate1 = cate1;
	}
	public String getCate2() {
		return cate2;
	}
	public void setCate2(String cate2) {
		this.cate2 = cate2;
	}
	public String getCate3() {
		return cate3;
	}
	public void setCate3(String cate3) {
		this.cate3 = cate3;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getArticleNum() {
		return articleNum;
	}
	public void setArticleNum(String articleNum) {
		this.articleNum = articleNum;
	}
	
	public double getShopScore() {
		return shopScore;
	}
	public void setShopScore(double shopScore) {
		this.shopScore = shopScore;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCateUrl() {
		return cateUrl;
	}

	public void setCateUrl(String cateUrl) {
		this.cateUrl = cateUrl;
	}

	public long getVenId() {
		return venId;
	}

	public void setVenId(long venId) {
		this.venId = venId;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public JdGoodsDetail(String cate1, String cate2, String cate3) {
		this.cate1 = cate1;
		this.cate2 = cate2;
		this.cate3 = cate3;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getShopDescr() {
		return shopDescr;
	}

	public void setShopDescr(String shopDescr) {
		this.shopDescr = shopDescr;
	}
}
