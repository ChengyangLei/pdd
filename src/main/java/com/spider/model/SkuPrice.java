package com.spider.model;
import java.io.Serializable;

import com.tomcong.util.DataRow;
/**
 * sku价格信息
 */
public class SkuPrice implements Serializable {
	private static final long serialVersionUID = 1L;
	private double OriginalPrice;
	private int skuQuantity;
	private long skuid;
	private String pvs;
	private double price;
	private int state;
	private String valueId;
	private String img;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public double getOriginalPrice() {
		return OriginalPrice;
	}

	public void setOriginalPrice(double originalPrice) {
		OriginalPrice = originalPrice;
	}

	public int getSkuQuantity() {
		return skuQuantity;
	}

	public void setSkuQuantity(int skuQuantity) {
		this.skuQuantity = skuQuantity;
	}

	public long getSkuid() {
		return skuid;
	}

	public void setSkuid(long skuid) {
		this.skuid = skuid;
	}

	public String getPvs() {
		return pvs;
	}

	public void setPvs(String pvs) {
		this.pvs = pvs;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getValueId() {
		return valueId;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public static void main(String[] args) {
		DataRow form = new DataRow();
		form.set("A",1);
		System.err.println(form);
	}
	

}
