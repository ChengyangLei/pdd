package com.spider.service;
import com.alibaba.fastjson.JSON;
import com.spider.model.*;
import com.spider.task.GoodsParserTask;
import com.spider.util.*;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
public class GoodsService extends ProjectBaseService {
	private static GoodsService service;
	public static GoodsService getInstance() {
		if (service == null) {
			service = new GoodsService();
		}
		return service;
	}

    private boolean hasKeys(long buyUrlId) {
	    return t.queryInt("select count(*) from t_sku_keys where buy_url_id=?",new Object[]{buyUrlId})>0;
    }

	public void setInvalidJson(long buyUrlId,String msg){
        t.update("update pdd_goods_json set state=?,msg=? where buy_url_id=? ",new Object[]{GoodsParserTask.ERROR_SPIDER_STATE,msg,buyUrlId});
    }
    public boolean exists(long buyUrlId){
	    return t.queryInt("select count(*) from t_goods where pic_state>=0 and buy_url_id=?",new Object[]{buyUrlId})>0;
    }

	public Message spiderV1(long buyUrlId,String proxyIp) {
		DataRow form = t.queryMap("select * from pdd_goods_json where buy_url_id=?",new Object[]{buyUrlId});
		if(form==null)return ViewUtil.errorMsg("无法查询到pdd_goods_json的数据");
		String url = form.getString("buy_url");
		int count = t.queryInt("select count(*) from t_goods where buy_url_id=? and pic_state>0 ",new Object[]{buyUrlId});
		if(count>0){
            setInvalidJson(buyUrlId,"商品已经入库,无法重复操作!");
			return ViewUtil.errorMsg("商品已经入库,无法重复操作!");
		}
        SpiderInfo info = null;
        try {
            info = SkuSpiderUtil.spiderInfoWithProxy(url,proxyIp);
        }catch (RuntimeException e){
            String msg = e.getMessage();
            setInvalidJson(buyUrlId,e.getMessage());
            return ViewUtil.errorMsg(e.getMessage());
        }
		if(info==null)return ViewUtil.errorMsg("采集商品信息失败!");
        String json = JSON.toJSONString(info);
        if(StringHelper.isEmpty(json))return ViewUtil.errorMsg("json为空");
        return ViewUtil.successObj(info);
	}
	Message setInvalid(long buyUrlId,String msg){
		return ViewUtil.errorMsg(String.format("京东buy_url_id为[%d]的数据校验失败:%s",buyUrlId,msg));
	}



    public void rollback(String buyUrlId) {
	    t.update("update pdd_goods_json set code =1 where buy_url_id=?",new Object[]{buyUrlId});
    }
    public void notifyParserFinish(Long id) {
        t.update("update pdd_goods_json set state =? where buy_url_id=?",new Object[]{GoodsParserTask.SUCCESS_STATE,id});
    }
    public static void main(String[] args) {
		GoodsService s = GoodsService.getInstance();
		long t1 = System.currentTimeMillis();
        System.out.println(s.spiderV1(223165187,null));
        long t2 = System.currentTimeMillis();
        System.out.println();

    }



}
