package com.spider;
import com.alibaba.fastjson.JSONObject;
import com.spider.service.GoodsService;
import com.spider.util.HttpClientUtil;
import com.spider.util.Message;
import com.tomcong.jdbc.JdbcTemplate;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
import java.util.Date;
import java.util.List;
public class PicMarkTask implements Runnable {
    private JdbcTemplate t = GoodsService.getInstance().getJdbcTemplate("web");
    private final static int threadSize = 10;
    private int start = 0;
    private int tail;
    private String table ="t_main_img";
    public PicMarkTask(int tail){
        this.tail = tail;
    }
    @Override
    public void run() {
        List<DataRow> list = scan();
        while(list!=null&&list.size()>0){
            for(DataRow data:list){
                String img = data.getString("url1");
                String mark = produceFingerPrint(img);
                if(StringHelper.isNotEmpty(mark)){
                    DataRow form = new DataRow();
                    form.set("buy_url_id",data.getLong("buy_url_id"));
                    form.set("img",img);
                    form.set("mark",mark);
                    form.set("create_date",new Date());
                    t.insert("t_pic_mark",form);
                }
            }
            start+=list.size();
            System.out.println(String.format("Thread%d-start%d",tail,start));
            list = scan();
        }

    }
    private List<DataRow> scan(){
        try {
            String sql = String.format("select buy_url_id,url1 from %s where mod(id,%d)=%d and url3 is not null and url3 not in (select img from t_pic_mark) limit %d,100", table,threadSize,tail,0);
            return t.query(sql);
        }catch (JdbcException e){
            return null;
        }
    }
    private String produceFingerPrint(String url){
        return produceFingerPrint(url,0);
    }
    private String produceFingerPrint(String url,int errorCount){
        if(errorCount>3)return null;
        String restUrl =String.format("http://114.67.94.86/pic/mark?url=%s",url);
        //String restUrl =String.format("http://localhost:5000/pic/mark?url=%s",url);
        try {
            String result = HttpClientUtil.get(restUrl);
            Message msg = JSONObject.parseObject(result,Message.class);
            if(msg==null)return produceFingerPrint(url,++errorCount);
            if(msg.getCode()==0)return msg.getData().toString();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return produceFingerPrint(url,++errorCount);
        }
    }

    public static void main(String[] args) {
         for(int i=0;i<threadSize;i++){
             new Thread(new PicMarkTask(i)).start();
         }
    }
}
