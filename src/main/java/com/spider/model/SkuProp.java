package com.spider.model;
import java.util.ArrayList;
import java.util.List;
public class SkuProp {
	private long propId=0;
	private String propName;
	private List<SkuPropValue> values = new ArrayList<SkuPropValue>();
	public long getPropId() {
		return propId;
	}
	public void setPropId(long propId) {
		this.propId = propId;
	}
	
	public SkuProp(long propId) {
		this.propId = propId;
	}
	public SkuProp() {
		// TODO Auto-generated constructor stub
	}


	public List<SkuPropValue> getValues() {
		return values;
	}
	public void setValues(List<SkuPropValue> values) {
		this.values = values;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	@Override
	public String toString() {
		return "SkuProp [propId=" + propId + ", propName="
				+ propName + ", values=" + values + "]";
	}
	
	
	
	

}
