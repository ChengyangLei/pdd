package com.spider.service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spider.model.UploadImg;
import com.spider.model.UploadResult;
import com.spider.util.AliOssUtil;
import com.spider.util.HttpClientUtil;
import com.spider.util.Message;
import com.tomcong.jdbc.JdbcTemplate;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 测试爬虫结果
 */
public class SpiderTestService extends ProjectBaseService {
    /**
     * 从生产库获取1000条测试数据,并且插入到测试库运行测试
     * @return
     */
    public void testJsonList() throws UnsupportedEncodingException {
        JdbcTemplate p = this.getJdbcTemplate("web");
        List<DataRow>  list = p.query("select * from jd_goods_json where buy_url_id in (select buy_url_id from t_goods where oss_state=1 and state=1 and pic_state>0 ) limit 0,1000");
        for(DataRow data:list){
              String keywords = URLEncoder.encode(data.getString("keywords"),"utf-8");
              long fid = data.getLong("fid");
              long cid = data.getLong("cid");
              long userId = data.getLong("userId");
              int goodsType = data.getInt("goods_type");
              String buyUrlId = data.getString("buy_url_id");
              testJson(keywords,buyUrlId,fid,cid,userId,goodsType);
        }

    }
    public void testJson(String keywords,String buyUrlId,long fid,long cid,long userId,int goodsType){
        String url = String.format("http://localhost:5000/beibei/spider?buyUrlId=%s&keywords=%s&fid=%d&cid=%d&userId=%d&goodsType=%d",buyUrlId,keywords,fid,cid,userId,goodsType);
        try {
            String result = HttpClientUtil.get(url);
            Message msg = JSONObject.parseObject(result, Message.class);
            if(msg.getCode()!=0){
                System.out.println(msg.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    public void restoreDetailImg(){
        String sql="select id,url3,w,h from t_detail_img where state =2 and w>800 and url3 is not null limit 0,100";
        List<DataRow> list = t.query(sql);
        while(list!=null&&list.size()>0){
            for(DataRow data:list){
                int w = data.getInt("w");
                int h = data.getInt("h");
                String url = data.getString("url3");
                try {
                    url = URLEncoder.encode(url,"utf-8");
                    int newH =750*h/w;
                    String restUrl =String.format("%s/cutDetail?w=%d&h=%d&url=%s", AliOssUtil.picHost,750,newH,url);
                    try {
                        UploadResult result = JSONObject.parseObject(HttpClientUtil.get(restUrl),UploadResult.class);
                        if(result!=null&&result.getCode()==0){
                            t.update("update t_detail_img set w=750,h=?,url3=?,file_size=? where id=?",new Object[]{newH,result.getUrl(),result.getFilesize(),data.getLong("id")});
                        }else{
                            t.update("update t_detail_img set state =5 where id=?",new Object[]{data.getLong("id")});
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            list = t.query(sql);
        }

    }
    public boolean restoreSingleSwipe(long buyUrlId){
        List<DataRow> list = t.query("select url3,hash from t_swipe_img where buy_url_id=?",new Object[]{buyUrlId});
        if(list==null||list.size()<3)return false;
            List<DataRow> forms = new ArrayList<DataRow>();
            Set<String> marks = new HashSet<String>();
            for (DataRow data : list) {
                DataRow form = new DataRow();
                String mark = data.getString("hash");
                int count = t.queryInt("select count(*) from t_pic_mark where mark=?", new Object[]{mark});
                if (count > 0) return false;
                int size = getCharNum(mark);
                if (size <= 5) return false;
                form.set("mark",mark);
                form.set("buy_url_id",data.getLong("buy_url_id"));
                form.set("img",data.getString("url3"));
                form.set("create_date",new Date());
                forms.add(form);
                marks.add(mark);
            }
            if(marks.size()<3)return false;
            try {
                for (DataRow form : forms) {
                    t.insert("t_pic_mark", form);
                }
            }catch (JdbcException e){
                e.fillInStackTrace();
            }
            return true;
    }

    public void restoreSwipeMark(){
        String sql="select buy_url_id from t_goods where v=0 and oss_state=1 and pic_state>0 limit 0,10";
        long[] buyUrlIds = t.queryLongArray(sql);
        while(buyUrlIds!=null&&buyUrlIds.length>0){
            for(long buyUrlId:buyUrlIds){
                if(restoreSingleSwipe(buyUrlId)){
                    t.update("update t_goods set v=1 where buy_url_id=?",new Object[]{buyUrlId});
                }else{
                    t.update("update t_goods set pic_state=-2 where buy_url_id=?",new Object[]{buyUrlId});
                }
            }
            buyUrlIds = t.queryLongArray(sql);
        }
    }


    public int getCharNum(String mark){
        Set<String> set = new HashSet<String>();
        for(int i=0;i<mark.length();i++){
            set.add(String.valueOf(mark.charAt(i)));
        }
        return set.size();
    }

    public void select(long buyUrlId){
         String mainHash = t.queryString("select hash from t_main_img where buy_url_id=?",new Object[]{buyUrlId});
         Set<String> mainSet = new HashSet<String>();
         for(int i=0;i<mainHash.length();i++){
             mainSet.add(String.valueOf(mainHash.charAt(i)));
         }
        System.out.println(String.format("mainSet-%d",mainSet.size()));
         String whiteHash = t.queryString("select hash from t_white_img where buy_url_id=?",new Object[]{buyUrlId});
        Set<String> whiteSet = new HashSet<String>();
        for(int i=0;i<whiteHash.length();i++){
            whiteSet.add(String.valueOf(mainHash.charAt(i)));
        }
        System.out.println(String.format("whiteSet-%d",mainSet.size()));
         String[] swipeHashArray = t.queryStringArray("select hash from t_main_img where buy_url_id=?",new Object[]{buyUrlId});
         for(String swipeHash:swipeHashArray){
             Set<String> swipeSet = new HashSet<String>();
             for(int i=0;i<swipeHash.length();i++){
                 swipeSet.add(String.valueOf(swipeHash.charAt(i)));
             }
             System.out.println(String.format("swipeSet-%d-%s",mainSet.size(),swipeHash));
         }

    }

    public void restorePropImg(){
        String sql="select buy_url_id,prop_id,id from t_sku_prop  where f_id=0 and type=2";
        List<DataRow> list = t.query(sql);
        if(list!=null&&list.size()>0){
            for(DataRow data:list){
                long buyUrlId = data.getLong("buy_url_id");
                long propId = data.getLong("prop_id");
                long id = data.getLong("id");
                DataRow form = t.queryMap("select prop_id,id from t_sku_prop where f_id=0 and buy_url_id=? and prop_id!=?",new Object[]{buyUrlId,propId});
                t.update("update t_sku_prop set prop_id=?,value_id=? where id=?",new Object[]{
                        form.getLong("prop_id"),form.getLong("prop_id"),id});
                t.update("update t_sku_prop set prop_id=?,value_id=? where id=?",new Object[]{
                        propId,propId,form.getLong("id")});
            }
        }
    }

    public void restoreGoods(String table){
        String sql="select * from "+table+" limit 0,100";
        List<DataRow> list = t.query(sql);
        while(list!=null&&list.size()>0){
            for(DataRow data:list){
                long id = data.getLong("id");
                data.remove("id");
                t.insert(table+"_copy",data);
                t.delete(table,"id",id);
            }
            list = t.query(sql);

        }
    }

    public static void main(String[] args) {
        SpiderTestService s = new SpiderTestService();
        s.restorePropImg();

    }
}
