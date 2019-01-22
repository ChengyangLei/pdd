package com.spider.util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.spider.model.SkuKey;
import com.spider.model.SkuPrice;
import com.spider.model.SkuProp;
import com.spider.model.SkuPropValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tomcong.service.BaseService;
import com.tomcong.util.StringHelper;
public class SkuParser {
	public static Set<String> parseMainImgs(JSONArray array) {
		// TODO Auto-generated method stub
		Set<String> set = new HashSet<String>();
		for(Iterator<Object> it =array.iterator();it.hasNext();){
			set.add(it.next().toString());
		}
		return set;
	}
   /**
    * 解析详情图信息
    * @param descr
    * @return
    */
	public static List<String> parseDetailImgs(String descr) {
		// TODO Auto-generated method stub
		Document doc = Jsoup.parse(descr);
		List<String> detailList = new ArrayList<String>();
		Elements as =doc.getElementsByTag("a");
		//清除带有超链接的模块
		List<Element> invalidAs = new ArrayList<Element>();
		for(Element a:as){
			String a_href =a.attr("href");

			if(StringHelper.isNotEmpty(a_href)&&!a_href.equalsIgnoreCase("#"))invalidAs.add(a);
		}
		for(Element a:invalidAs)a.remove();
		doc.select("area").remove();
		Elements detailImgs =doc.getElementsByTag("img");
		if(detailImgs!=null&&detailImgs.size()>0){
			for(Element d:detailImgs){
				if(!validateDetailImgElement(d))continue;
				String w =d.attr("width");
				if(w!=null){
					 if(w.contains("px"))w=w.replace("px", "");
					 if(w.contains("pt"))w=w.replace("pt", "");
					 if(StringHelper.isNotEmpty(w)){
						 try{
							 int width = Integer.parseInt(w);
							 if(width<30)continue;
							 }catch(NumberFormatException e1){
								 
							 }
					 }		
				}
				String img =d.attr("src");
				if(StringHelper.isEmpty(img))img=d.attr("data-url");
				if(StringHelper.isNotEmpty(img)&&!img.equalsIgnoreCase("null")&&!detailList.contains(img)){
					img = img.replaceAll("\"","");
					if(!img.startsWith("http")){
					    img = String.format("http:%s",img);
                    }
					detailList.add(img);
				}
			}
		}
		return detailList;
	}
	public static boolean validateDetailImgElement(Element d) {
		// TODO Auto-generated method stub
		while(d!=null){
			d=d.parent();
			if(d!=null){
				String style = d.attr("style");
				if(StringHelper.isNotEmpty(style)&&(style.contains("width:0")||style.contains("height:0")))return false;
				if("a".equals(d.tagName()))return false;
			}
			
		} 
		return true;
	}
	public static String filterDetailImg(String img){
		if(StringHelper.isNotEmpty(img)&&img.contains("img")&&!img.contains("?p=")&&!img.contains("assets.alicdn.com")){
			if(!img.startsWith("http:")&&!img.startsWith("https:"))img ="http:"+img;
			return img;
		}
		return null;
	}
	public static List<SkuProp> parseSkuProp(JSONArray array) {
		// TODO Auto-generated method stub
		List<SkuProp> list = new ArrayList<SkuProp>();
		for(int i =0;i<array.size();i++){
			 JSONObject propObject = (JSONObject) array.get(i);
			 SkuProp skuProp = new SkuProp();
			 skuProp.setPropName(propObject.getString("propName"));
			 List<SkuPropValue> values = parseSkuPropValue(propObject.getJSONArray("values"));
			 if(values!=null&&values.size()>0){
					 for(SkuPropValue value:values){
						 String img = value.getImg();
					 }
				 skuProp.setValues(values);
			 }
			 
			 list.add(skuProp);
		}
		return list;
	}
	private static List<SkuPropValue> parseSkuPropValue(JSONArray array) {
		// TODO Auto-generated method stub
		if(array==null||array.size()==0)return null;
		List<SkuPropValue> list = new ArrayList<SkuPropValue>();
		for(int i =0;i<array.size();i++){
			JSONObject propValueObject = (JSONObject) array.get(i);
			SkuPropValue skuPropValue = new SkuPropValue();
			String img = propValueObject.getString("img");
			String valueId = propValueObject.getString("valueId");
			String name = propValueObject.getString("name");
			if(StringHelper.isEmpty(valueId)&&StringHelper.isEmpty(name))continue;
			if(StringHelper.isNotEmpty(img)&&!img.equalsIgnoreCase("null")){
				skuPropValue.setImg(img);
			}
			skuPropValue.setName(name);
			skuPropValue.setValueId(valueId);
			list.add(skuPropValue);
		}
		return list;
	}
	public static Set<String> parseSkuImgs(List<SkuProp> skuList) {
		// TODO Auto-generated method stub
		 Set<String> set = new HashSet<String>();
		 if(skuList==null||skuList.size()==0)return set;
		 for(SkuProp skuProp:skuList){
			 for(SkuPropValue value:skuProp.getValues()){
				 String img = value.getImg();
				 if(StringHelper.isNotEmpty(img)&&!"null".equalsIgnoreCase(img)){
					 if(!img.startsWith("http"))img ="http:"+img;
					 set.add(img);
				 }
			 }
		 }
		return set;
	}
	public static List<SkuKey> parseSkuKey(JSONArray keys) {
		// TODO Auto-generated method stub
		List<SkuKey> list = new ArrayList<SkuKey>();
		if(keys==null||keys.size()==0)return list;
		for(int i=0;i<keys.size();i++){
			 JSONObject obj = keys.getJSONObject(i);
			 String name = obj.getString("name");
			 String values = obj.getString("values");
			 if(isEmpty(name)&&isEmpty(values))continue;
			 SkuKey skuKey = new SkuKey(name,values);
			 list.add(skuKey);
		}
		return list;
	}
	private static boolean isEmpty(String str){
		return StringHelper.isEmpty(str)||str.equalsIgnoreCase("null");
	}
	public static List<SkuPrice> parseSkuPrice(JSONArray skuPriceArray) {
		// TODO Auto-generated method stub
		if(skuPriceArray==null||skuPriceArray.size()==0)return null;
		List<SkuPrice> list = new ArrayList<SkuPrice>();
		for(int i=0;i<skuPriceArray.size();i++){
			SkuPrice price = new SkuPrice();
			JSONObject obj = skuPriceArray.getJSONObject(i);
			price.setSkuid(Long.parseLong(obj.getString("skuid")));
			price.setOriginalPrice(Double.parseDouble(obj.getString("OriginalPrice")));
			price.setPvs(obj.getString("pvs"));
			price.setSkuQuantity(Integer.parseInt(obj.getString("skuQuantity")));
			price.setPrice(Double.parseDouble(obj.getString("price")));
			list.add(price);		
		}
		return list;
	}
public static void main(String[] args) {
	String descr =new BaseService().getJdbcTemplate("web").queryString("select html_desc from t_sku_desc where buy_url_id = '11300061003'");
	System.err.println(descr);
	List<String> imgs = SkuParser.parseDetailImgs(descr);
	for(String img:imgs)System.err.println(img);
	
}
}
