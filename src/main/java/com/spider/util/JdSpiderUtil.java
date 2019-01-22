package com.spider.util;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.JdMainSeach;
import com.spider.service.GoodsService;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.spider.model.JdGoods;
import com.spider.model.JdGoodsDetail;
import com.tomcong.util.StringHelper;

public class JdSpiderUtil {
    private static GoodsService service = GoodsService.getInstance();
	public static int count =0;
	public  static Message jdSearchNoProxy(String key,int psort,int lowPrice,int highPrice,int page,int commentVal){
		count++;
		if(page<0)page=1;
		String url=formatUrl(key,psort,lowPrice,highPrice);
		if(StringHelper.isEmpty(url))return null;
		Document doc = getHtmlNoProxy(url);
		if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
		Element element =doc.getElementById("J_goodsList");
		if(element==null){
			return ViewUtil.errorMsg(doc.data());
		}
		Elements goodsEles = doc.select("ul >li.gl-item");
		if(goodsEles==null||goodsEles.size()==0)return ViewUtil.errorMsg(doc.data());
		List<Long> skuIdList = new ArrayList<Long>();
		List<JdGoods> list = parseGoodsListByUrl(goodsEles,commentVal);
		if(list==null||list.size()==0){
			return ViewUtil.errorMsg("解析["+url+"]商品数据错误,返回数据"+doc.data());
		}
		for(JdGoods jdGoods:list)skuIdList.add(jdGoods.getSkuId());
		String loadUrl=url.replace("/Search", "/s_new.php");
        loadUrl =String.format("%s&s=29&scrolling=y&show_items=",loadUrl);
		doc =getHtmlNoProxy(loadUrl,url);
		if(doc==null)ViewUtil.errorMsg("解析["+loadUrl+"]异步加载商品数据错误,返回数据"+doc.data());
		element =doc.getElementById("J_goodsList");
		if(element==null){
			return ViewUtil.errorMsg(doc.data());
		}
		goodsEles = doc.select("ul >li.gl-item");
		if(goodsEles==null||goodsEles.size()==0){
			Message msg = ViewUtil.successObj(list);
			msg.setMsg(doc.data());
			return msg;
		}
		List<JdGoods> loadList = parseGoodsListByLoadUrl(doc,commentVal);
		if(loadList!=null){
			for(JdGoods jdGoods:loadList){
				if(!skuIdList.contains(jdGoods.getSkuId()))list.add(jdGoods);
			}
		}
		System.err.println("第["+page+"]["+count+"]页共抓取分页数据["+list.size()+"]个");
		count--;
		return ViewUtil.successObj(list);
	}
	/**
	 * 爬取京东搜索页数据(首页数据)
	 */
	public  static Message seachByKey(String key,int psort,int lowPrice,int highPrice,int commentVal){
        JdMainSeach seach = new JdMainSeach();
		count++;
		String url=formatUrl(key,psort,lowPrice,highPrice);
		if(StringHelper.isEmpty(url))return null;
		HttpHost proxy = ProxyUtil.getProxyIp();
		long t1 = System.currentTimeMillis();
		Document doc = getHtml(url,proxy);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
		Element element =doc.getElementById("J_goodsList");
		if(element==null){
			return ViewUtil.errorMsg(doc.data());
		}
		//查询分页信息
        String pageStr = doc.getElementById("J_resCount").text();
		int totalPage = parsePage(pageStr);
		if(totalPage==0)return ViewUtil.errorMsg("查无信息");
		seach.setTotalPage(totalPage);
        String baseUrl = doc.select("div.crumbs-first>a").attr("href");
        if(StringHelper.isEmpty(baseUrl))baseUrl=doc.select("a.crumb-select-item").attr("href");
        baseUrl = filterBaseUrl(baseUrl);
		if(StringHelper.isEmpty(baseUrl))return ViewUtil.errorMsg("无法获取baseUrl信息");
		seach.setBaseUrl(baseUrl);
		Elements goodsEles = doc.select("ul >li.gl-item");
		if(goodsEles==null||goodsEles.size()==0)return ViewUtil.errorMsg(doc.data());
		List<JdGoods> list = parseGoodsListByUrl(goodsEles,commentVal);
		if(list==null||list.size()==0){
			return ViewUtil.errorMsg("解析["+url+"]商品数据错误,返回数据"+doc.data());
		}
		String loadUrl=url.replace("/Search", "/s_new.php");
		loadUrl =String.format("%s&page=2&s=29&scrolling=y&show_items=",loadUrl);
		for(int i=0;i<list.size();i++){
		    if(i>0)loadUrl=String.format("%s,",loadUrl);
            loadUrl = String.format("%s%d",loadUrl,list.get(i).getSkuId());
        }
		doc =getHtml(loadUrl,url,proxy);
		if(doc==null)return ViewUtil.errorMsg("解析["+loadUrl+"]异步加载商品数据错误,返回数据");
		goodsEles = doc.select("li.gl-item");
		if(goodsEles==null||goodsEles.size()==0){
			Message msg = ViewUtil.successObj(list);
			msg.setMsg(doc.data());
			return msg;
		}
		list.addAll(parseGoodsListByLoadUrl(goodsEles,commentVal));
		System.err.println("第[1]["+count+"]页共抓取分页数据["+list.size()+"]个");
		count--;
		seach.setList(list);
		return ViewUtil.successObj(seach);
	}

    private static int parsePage(String pageStr) {
	    pageStr = pageStr.replace("+","");
	    if(pageStr.contains("万"))return 100;
	    return Integer.parseInt(pageStr)/60;
    }

    private static String filterBaseUrl(String baseUrl) {
        baseUrl =baseUrl.replace("search?","");
        int pos = baseUrl.indexOf("&ds=");
        if(pos!=-1)baseUrl=baseUrl.substring(0,pos);
        try {
            baseUrl = URLDecoder.decode(baseUrl,"utf-8");
            return URLEncoder.encode(baseUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  static Message seachByUrl(String baseUrl,int page,int commentVal){
        count++;
        String url=formatByBaseUrl(baseUrl,page);
        if(StringHelper.isEmpty(url))return null;
        HttpHost proxy = ProxyUtil.getProxyIp();
        Document doc = getHtml(url,proxy);
        if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
        Element element =doc.getElementById("J_goodsList");
        if(element==null){
            return ViewUtil.errorMsg(doc.data());
        }
        Elements goodsEles = doc.select("ul >li.gl-item");
        if(goodsEles==null||goodsEles.size()==0)return ViewUtil.errorMsg(doc.data());
        List<JdGoods> list = parseGoodsListByUrl(goodsEles,commentVal);
        if(list==null||list.size()==0){
            return ViewUtil.errorMsg("解析["+url+"]商品数据错误,返回数据"+doc.data());
        }
        String loadUrl=url.replace("/Search", "/s_new.php");
        loadUrl =loadUrl.replace(String.format("&page=%d",2*page-1),String.format("&page=%d",page+1));
        for(int i=0;i<list.size();i++){
            if(i>0)loadUrl=String.format("%s,",loadUrl);
            loadUrl = String.format("%s%d",loadUrl,list.get(i).getSkuId());
        }
        System.out.println(loadUrl);
        doc =getHtml(loadUrl,url,proxy);
        if(doc==null)ViewUtil.errorMsg("解析["+loadUrl+"]异步加载商品数据错误,返回数据"+doc.data());
        goodsEles = doc.select("li.gl-item");
        if(goodsEles==null||goodsEles.size()==0){
            Message msg = ViewUtil.successObj(list);
            msg.setMsg(doc.data());
            return msg;
        }
        list.addAll(parseGoodsListByLoadUrl(goodsEles,commentVal));
        System.err.println("第["+page+"]["+count+"]页共抓取分页数据["+list.size()+"]个");
        count--;
        return ViewUtil.successObj(list);
    }
	private static List<JdGoods> parseGoodsListByLoadUrl(Document doc,int commentVal){
		List<JdGoods> list = new ArrayList<JdGoods>();
		Elements goodsEle = doc.select("ul >li.gl-item");
		try {
			for(int i=0;i<goodsEle.size();i++){
				try{
				JdGoods goods = parseLoadJdGoods(goodsEle.get(i));
				if(goods!=null){
					int cNum = goods.getCommentNum();
					if(cNum<commentVal)continue;
					list.add(goods);
				}
				}catch(RuntimeException e){
					e.fillInStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	private static List<JdGoods> parseGoodsListByUrl(Elements goodsEles,int commentVal){
		List<JdGoods> list = new ArrayList<JdGoods>();
		for(int i=0;i<goodsEles.size();i++){
			try{
			JdGoods goods = parseJdGoods(goodsEles.get(i));
			if(goods!=null){
				int cNum = goods.getCommentNum();
				if(cNum<commentVal)continue;
				list.add(goods);
			}
			}catch(RuntimeException e){
				e.fillInStackTrace();
			}
		}
		return list;
	}
    private static List<JdGoods> parseGoodsListByLoadUrl(Elements goodsEles,int commentVal){
        List<JdGoods> list = new ArrayList<JdGoods>();
        for(int i=0;i<goodsEles.size();i++){
            try{
                JdGoods goods = parseJdGoods(goodsEles.get(i));
                if(goods!=null){
                    int cNum = goods.getCommentNum();
                    if(cNum<commentVal)continue;
                    list.add(goods);
                }
            }catch(RuntimeException e){
                e.fillInStackTrace();
            }
        }
        return list;
    }
	private static int parseNum(String cn) {
		// TODO Auto-generated method stub
		if(cn.contains("+"))cn =cn.replace("+", "");
		int per = 1;
		if(cn.contains("万")){
			per=10000;
			cn =cn.replace("万","");
		}
		double  p =Double.parseDouble(cn);
		return (int)(p*per);
	}
	private static JdGoods parseLoadJdGoods(Element element) {
		// TODO Auto-generated method stub
		long skuId = Long.parseLong(element.attr("data-sku"));
		JdGoods goods = new JdGoods(skuId);
		String shopStr =element.select("div.p-shop>").attr("data-shopid");
		//String shopLink =element.select("div.p-shop>span>a").attr("href");
		if(StringHelper.isNotEmpty(shopStr)){
			try{
			long shopId =Long.parseLong(shopStr);
			goods.setShopId(shopId);
			}catch(NumberFormatException e){		
			}
		}
		String shopName = element.select("div.p-shop>span>a").attr("title");
		goods.setShopName(shopName);
		String transport = element.select("div.p-icons>i").get(0).text();
		if(!transport.contains("京东")){
			transport="非京配";
		}else{
			transport="京配";
		}
		goods.setTransport(transport);
		double price = Double.parseDouble(element.select("div.p-price>strong>i").text());
		goods.setPrice(price);
		String url = element.select("div.p-img>a").attr("href");
		if(!url.startsWith("http"))url=String.format("http:%s", url);
		goods.setUrl(url);
		String img = element.select("div.p-img>a>img").attr("source-data-lazy-img");
		if(StringHelper.isEmpty(img)){
			img =element.select("div.p-img>a>img").attr("src");
		}
		if(!img.startsWith("http"))img=String.format("http:%s", img);
		goods.setImg(img);
		String commentNum =element.select("div.p-commit>strong>a").text();
		goods.setCommentNum(parseNum(commentNum));
		return goods;
	}
	private static JdGoods parseJdGoods(Element element) {
		// TODO Auto-generated method stub
		long skuId = Long.parseLong(element.attr("data-sku"));
		JdGoods goods = new JdGoods(skuId);
		String shopLink =element.select("div.p-shop>span>a").attr("href");
        String shopIdStr = element.attr("jdzy_shop_id");
        long shopId = 0;
        if(StringHelper.isNotEmpty(shopIdStr)){
            try {
                shopId = Long.parseLong(shopIdStr);
                goods.setShopId(shopId);
            }catch (NumberFormatException e){

            }
        }else {
            if(StringHelper.isNotEmpty(shopLink)){
                int start = shopLink.indexOf("index-")+"index-".length();
                int end = shopLink.indexOf(".html");
                try{
                    shopId =Long.parseLong(shopLink.substring(start,end));
                    if(shopId==0){
                        shopId =Long.parseLong(element.select("div.p-shop>span>em").attr("data-shopid"));
                    }
                    if(shopId==0){
                        shopId = Long.parseLong(element.select("div.j-sku-item").attr("jdzy_shop_id"));
                    }
                    goods.setShopId(shopId);
                }catch(NumberFormatException e){
                }
            }
        }

		String shopName = element.select("div.p-shop>span>a").attr("title");
		goods.setShopName(shopName);
		String transport = element.select("div.p-icons>i").text();
		if(!transport.contains("京东")){
			transport="非京配";
		}else{
			transport="京配";
		}
		goods.setTransport(transport);
		try {
			double price = Double.parseDouble(element.select("div.p-price>strong>i").text());
			goods.setPrice(price);
		}catch (NumberFormatException e){

		}
		String url = element.select("div.p-img>a").attr("href");
		if(!url.startsWith("http"))url=String.format("http:%s", url);
		goods.setUrl(url);
		String img = element.select("div.p-img>a>img").attr("source-data-lazy-img");
		if(StringHelper.isEmpty(img)){
			img =element.select("div.p-img>a>img").attr("src");
			if(StringHelper.isEmpty(img))img = element.select("div.p-img>a>img").attr("data-lazy-img");
		}
		if(!img.startsWith("http"))img=String.format("http:%s", img);
		goods.setImg(img);
		String commentNum =element.select("div.p-commit>strong>a").text();
		if(StringHelper.isNotEmpty(commentNum))goods.setCommentNum(parseNum(commentNum));
		return goods;
	}

	private static JdGoodsDetail getJdGoodsDetail(String url) {
		// TODO Auto-generated method stub
		Document doc = getHtml(url,null);
		Elements as =doc.getElementById("crumb-wrap").select("div.item>a");
		if(as!=null&&as.size()>=3){
			String cate1 = as.get(0).text();
			String cate2 = as.get(1).text();
			String cate3 = as.get(2).text();
			JdGoodsDetail detail = new JdGoodsDetail(cate1,cate2,cate3);
			String brand =doc.getElementById("parameter-brand").select("li>a").text();
			detail.setBrand(brand);
			Elements lis = doc.select("ul.p-parameter-list>li");
			if(lis!=null&&lis.size()>0){
				 for(int i=0;i<lis.size();i++){
					 Element li = lis.get(i);
					 String title = li.attr("title");
					 if(title.equals("货号")){
						 String articleNum = li.text().replace("货号", "");
						 articleNum=articleNum.replace(":", "").trim();
						 detail.setArticleNum(articleNum);
					 }
				 }
			}
			return detail;
		}
		return null;
	}
	public static Document getHtml(String url,HttpHost proxy){
        try {
            String html = HttpClientUtil.get(url, null, null, 30*1000,30*1000);
            Document doc = Jsoup.parse(html);
            if(doc!=null){
                Element element =doc.getElementById("J_goodsList");
                if(element!=null)return doc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(proxy!=null){
            try {
                System.out.println(String.format("%s:%d",proxy.getHostName(),proxy.getPort()));
                service.insertProxyLogger(proxy);
                Map<String,String> headers = HeaderUtil.generateRandomHead();
                headers.put("Referer","https://www.jd.com/");
                String result = HttpClientUtil.postFormWithProxy(url,proxy,headers,30*1000,30*1000);
                Document doc =  Jsoup.parse(result);
                if(validate(doc))return doc;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
	}

	public static Document getShopHtml(String url,HttpHost proxy){
		try {
			String html = HttpClientUtil.postForm(url, null, HeaderUtil.generateRandomHead(), 30*1000,30*1000);
			Document doc = Jsoup.parse(html);
			if(doc!=null){
				Elements elements = doc.select("li.jSubObject");
				if(elements!=null&&elements.size()>0)return doc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(proxy!=null){
			try {
				System.out.println(String.format("%s:%d",proxy.getHostName(),proxy.getPort()));
				service.insertProxyLogger(proxy);
				Map<String,String> headers = HeaderUtil.generateRandomHead();
				headers.put("Referer","https://www.jd.com/");
				String result = HttpClientUtil.postFormWithProxy(url,proxy,headers,30*1000,30*1000);
				Document doc =  Jsoup.parse(result);
				if(validate(doc))return doc;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public static Document getHtmlNoProxy(String url){
	    /**/
		try {
			String html = HttpClientUtil.postForm(url, null, HeaderUtil.generateRandomHead(), 30*1000,30*1000);
			return Jsoup.parse(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static boolean validate(Document doc){
        if(doc==null)return false;
        Element element =doc.getElementById("J_goodsList");
        if(null!=element)return true;
        return doc.select("li.gl-item").size()>0;
    }
	public static Document getHtml(String url,String referer,HttpHost proxy){
        Map<String,String> headers = HeaderUtil.generateRandomHead();
        headers.put("Referer",referer);
        String result = null;
        try {
            result = HttpClientUtil.postFormWithProxy(url,null,headers,30*1000,30*1000);
            Document doc = Jsoup.parse(result);
            if(validate(doc))return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
	    /**/
			if(proxy!=null){
				try {
					service.insertProxyLogger(proxy);
                    result = HttpClientUtil.postFormWithProxy(url,proxy,headers,30*1000,30*1000);
                    Document doc = Jsoup.parse(result);
                    if(validate(doc))return doc;

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			return null;
	}
	public static Document getHtmlNoProxy(String url,String referrer){
	    /**/
		Map<String,String> headers = HeaderUtil.generateRandomHead();
		headers.put("Referer",referrer);
		try {
			String html = HttpClientUtil.postForm(url, null, headers, 30*1000,30*1000);
			return Jsoup.parse(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//1.价格倒序,2:价格升序,3:销量排序,4:评论排序,0:综合排序,价格区间
	public static String formatUrl(String key,int psort,int lowPrice,int highPrice){
		try {
			key = URLEncoder.encode(key, "utf-8");
			String searchUrl= String.format("https://search.jd.com/Search?keyword=%s&enc=utf-8&wq=%s&psort=%d", key,key,psort);
			if(lowPrice<=0){
				if(highPrice>0)searchUrl+="&ev=exprice_0-"+highPrice+"^";
			}else{
				if(highPrice>0){
					searchUrl+="&ev=exprice_"+lowPrice+"-"+highPrice+"^";
				}else{
					searchUrl+="&ev=exprice_"+lowPrice+"gt^";
				}
			}
			return searchUrl;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}

    public static String formatByBaseUrl(String baseUrl,int page){
        if(page<1)page=1;
            String searchUrl= String.format("https://search.jd.com/Search?%s&page=%d", baseUrl,page*2-1);
            return searchUrl;
    }
	public static Message jdSearchByUrlNoProxy(String url,int commentVal) {
		if(StringHelper.isEmpty(url))return null;
		Document doc = getHtmlNoProxy(url);
		if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
		Element element =doc.getElementById("plist");
		if(element==null){
			return ViewUtil.errorMsg(doc.data());
		}
        Elements goodsEles = element.select("ul >li.gl-item >div.j-sku-item");
		if(goodsEles==null||goodsEles.size()==0)return ViewUtil.errorMsg(doc.data());
		List<JdGoods> list = parseGoodsListByUrl(goodsEles,commentVal);
		if(list==null||list.size()==0){
			return ViewUtil.errorMsg("解析["+url+"]商品数据错误,返回数据"+doc.data());
		}
		count--;
        try {
            return ViewUtil.successObj(filterGoodsByComment(list,commentVal));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if(StringHelper.isEmpty(msg))msg =e.getCause().getMessage();
            return ViewUtil.errorMsg(msg);
        }

	}
    public static Message spiderShopAll(String url,int commentVal) {
        if(StringHelper.isEmpty(url))return null;
        Document doc = getShopHtml(url,null);
        if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
        return spiderShop(doc,commentVal);

    }
    public static Message spiderShop(Document doc,int commentVal){
	    /* */
        Elements elements = doc.select("li.jSubObject");
        if(elements==null||elements.size()==0)return ViewUtil.errorMsg("解析结果为空!");
        List<JdGoods> list = new ArrayList<JdGoods>();
        for(int i=0;i<elements.size();i++){
              Element element = elements.get(i);
              String skuIdStr = element.select("span.e-attention").attr("data-id");
              if(StringHelper.isEmpty(skuIdStr))continue;
              long skuId;
              try{
                  skuId = Long.parseLong(skuIdStr.trim());
              }catch (NumberFormatException e){
                  continue;
              }
              JdGoods jdGoods = new JdGoods(skuId);
              String goodsUrl = element.select("div.jItem>div.jPic>a").attr("href");
              if(StringHelper.isNotEmpty(goodsUrl)){
                  if(!goodsUrl.startsWith("http"))goodsUrl=String.format("http:%s",goodsUrl);
                  jdGoods.setUrl(goodsUrl);
              }
              String img = element.select("div.jItem>div.jPic>a>img").attr("src");
              if(StringHelper.isNotEmpty(img)){
                  if(!img.startsWith("http"))img= String.format("http:%s",img);
                  jdGoods.setImg(img);
              }

            list.add(jdGoods);
        }
        try {
            list = filterGoodsByComment(list,commentVal);
            if(list==null||list.size()==0)return ViewUtil.errorMsg("查无数据");
            /*
            for(JdGoods jdGoods:list){
                SkuPrice skuPrice = SkuSpiderUtil.getSkuPrice(jdGoods.getSkuId());
                if(skuPrice!=null){
                    jdGoods.setPrice(skuPrice.getPrice());
                }
            }
            */
            return ViewUtil.successObj(list);
        } catch (Exception e) {
            String msg = e.getMessage();
            if(StringHelper.isEmpty(msg))msg = e.getCause().getMessage();
            return ViewUtil.errorMsg(msg);
        }
    }

	/**
	 * 根据接口过滤商品
	 * @param list
	 * @param commentVal
	 * @return
	 */
	private static List<JdGoods> filterGoodsByComment(List<JdGoods> list, int commentVal) throws Exception {
		StringBuffer skuStr = new StringBuffer();
		for(JdGoods jdGoods:list){
			skuStr.append(",").append(jdGoods.getSkuId());
		}
		String restUrl = String.format("https://club.jd.com/comment/productCommentSummaries.action?my=pinglun2&referenceIds=%s",skuStr.substring(1));
		String result = HttpClientUtil.get(restUrl);
		if(StringHelper.isEmpty(result))throw new RuntimeException("无法查询评论数目");
		JSONObject obj = JSONObject.parseObject(result);
		if(obj==null||!obj.containsKey("CommentsCount"))throw new RuntimeException(String.format("查询评论信息失败[%s]",result));
		JSONArray skuArray = obj.getJSONArray("CommentsCount");
		Map<Long,Integer> skuMap = new HashMap<Long, Integer>(60);
		List<JdGoods> goodsList = new ArrayList<JdGoods>(60);
		for(int i=0;i<skuArray.size();i++){
			JSONObject commentObj = skuArray.getJSONObject(i);
            Long skuId = commentObj.getLong("SkuId");
			Integer commentCount = commentObj.getInteger("CommentCount");
			if(commentCount>=commentVal)skuMap.put(skuId,commentCount);
		}
		for(JdGoods jdGoods:list){
            Long skuId = jdGoods.getSkuId();
			if(skuMap.containsKey(skuId)){
                jdGoods.setCommentNum(skuMap.get(skuId));
				goodsList.add(jdGoods);
			}
		}
		return goodsList;
	}

	public static Message jdSearchByUrl(String url,int commentVal) {
		if(StringHelper.isEmpty(url))return null;
		Document doc = getHtml(url,null);
		if(doc==null)return ViewUtil.errorMsg("解析["+url+"]商品数据错误");
		Element element =doc.getElementById("plist");
		if(element==null){
			return ViewUtil.errorMsg(doc.data());
		}
		Elements goodsEles = element.select("ul >li.gl-item >div.j-sku-item");
		if(goodsEles==null||goodsEles.size()==0)return ViewUtil.errorMsg(doc.data());
		List<JdGoods> list = parseGoodsListByUrl(goodsEles,commentVal);
		if(list==null||list.size()==0){
			return ViewUtil.errorMsg("解析["+url+"]商品数据错误,返回数据"+doc.data());
		}
		count--;
        try {
            return ViewUtil.successObj(filterGoodsByComment(list,commentVal));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if(StringHelper.isEmpty(msg))msg =e.getCause().getMessage();
            return ViewUtil.errorMsg(msg);
        }
	}
	//String key,int psort,int lowPrice,int highPrice,int page,int commentVal
	public static void main(String[] args) throws IOException {
		try {
			String cmd = "ping ";
			String param ="127.0.0.1";
			Process child = Runtime.getRuntime().exec(cmd+param);
			// 获得ping的输出
			InputStream child_in = child.getInputStream();
			int c;
			while ((c = child_in.read()) != -1) {
				//   System.out.println("kkk");
				System.out.print((char)c);
			}
			child_in.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}


//https://search.jd.com/s_new.php?keyword=%E6%94%B6%E7%BA%B3%E7%AE%B1&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E6%94%B6%E7%BA%B3%E7%AE%B1&psort=4&ev=exprice_0-100%5E&stock=1&page=2&s=27
}
