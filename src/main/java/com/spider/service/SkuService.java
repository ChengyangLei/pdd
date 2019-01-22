package com.spider.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.spider.model.SkuKey;
import com.spider.model.SkuPrice;
import com.spider.model.SkuProp;
import com.spider.model.SkuPropValue;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.jdbc.session.SessionParams;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
public class SkuService extends ProjectBaseService{
	private static SkuService service;
	public static SkuService getInstance() {
		if (service == null) {
			service = new SkuService();
		}
		return service;
	}
	public List<SessionParams> getInsertSkuPricesSession(
			List<SkuPrice> priceList,long buyUrlId) {
		// TODO Auto-generated method stub
		clearPrice(buyUrlId);
		List<SessionParams> sessions = new ArrayList<SessionParams>();
		Date date = new Date();
		for(SkuPrice price:priceList){
			String pvs = price.getPvs();
			if(StringHelper.isEmpty(pvs))continue;
			String valueId = price.getValueId();
			String img = price.getImg();
			DataRow form = new DataRow();
			form.set("pvs", pvs);
			form.set("sku_id",price.getSkuid());
			form.set("sku_quantity",price.getSkuQuantity());
			form.set("original_price",price.getOriginalPrice());
			form.set("price",price.getPrice());	
			form.set("edit_date", date);
			form.set("edit_stamp", date.getTime());		
			form.set("buy_url_id",buyUrlId);
			form.set("type",3);
			if(StringHelper.isNotEmpty(valueId)){
				form.set("value_id",valueId);
                String url = t.queryString("select url3 from t_sku_img where buy_url_id =? and url1=?",new Object[]{buyUrlId,img});
				form.set("img", url);
			}else{
				form.set("state",1);
			}
			sessions.add(t.getInsertSql("t_sku_detail", form));
		}
		return sessions;
	}

	public List<SessionParams> getInsertSkuKeysSession(List<SkuKey> keys, long buyUrlId){
		   if(keys==null||keys.size()==0)throw new JdbcException("null keys");
		   clearSkuKey(buyUrlId);
		   List<SessionParams> sessions = new ArrayList<SessionParams>();
		   DataRow form = new DataRow();
		   form.set("buy_url_id", buyUrlId);
		   for(SkuKey key:keys){
			      String name = key.getKey().length()>200?key.getKey().substring(0, 200):key.getKey();
			      String value = key.getValue().length()>400?key.getValue().substring(0, 400):key.getValue();
			      form.set("keyname", name);
			      form.set("keyvalue", value);
			      form.set("type",3);
			      sessions.add(t.getInsertSql("t_sku_keys", form));
		   }
		   return sessions;
	}


	public void clearSkuKey(long buyUrlId) {
		// TODO Auto-generated method stub
		t.update("delete from t_sku_keys where buy_url_id =?", new Object[]{buyUrlId});
	}


	public void clearPrice(long buyUrlId) {
		// TODO Auto-generated method stub
		t.update("delete from t_sku_detail where buy_url_id =?", new Object[]{buyUrlId});
	}
	public static void main(String[] args) {
	}
	
	
}
