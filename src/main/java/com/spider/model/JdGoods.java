package com.spider.model;
import java.io.Serializable;
/**
 * 京东搜索页,每个京东商品对象
 * @author gc
 *
 */
public class JdGoods implements Serializable {
	private static final long serialVersionUID = 1L;
	private long shopId;
	  private String shopName;
	  private long skuId;
	  private double price;
	  private String transport;
	  private String img;
	  private int commentNum;
	  private String url;
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


	public long getSkuId() {
		return skuId;
	}
	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getTransport() {
		return transport;
	}
	public void setTransport(String transport) {
		this.transport = transport;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public JdGoods(long skuId) {
		this.skuId = skuId;
	}


	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	@Override
	public String toString() {
		return "JdGoods [commentNum=" + commentNum + ", img=" + img + ", price="
				+ price + ", shopId=" + shopId
				+ ", shopName=" + shopName +  ", skuId=" + skuId + ", transport="
				+ transport + ", url=" + url + "]";
	}

	  

}
