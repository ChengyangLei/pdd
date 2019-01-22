package com.spider.task.restore;
import com.spider.task.GoodsParserTask;
import com.spider.task.call.PropCall;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.concurrent.CountDownLatch;
public class RestorePropTask extends GoodsParserTask {
    @Override
    public List<DataRow> scan() {
        return t.query("select buy_url_id,json from pdd_goods_json where state = ? limit 0,10",new Object[]{PROP_STATE});
        //return t.query("select DISTINCT(buy_url_id) from t_sku_img GROUP BY buy_url_id,url2 having count(id)>1");
    }

    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
           PropCall call = new PropCall(data.getLong("buy_url_id"),threadSignal);
            Thread thread = new Thread(call);
            thread.start();
        }
        try {
            threadSignal.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 等待所有子线程执行完
        System.gc();// 打印结束标记
    }

    public static void main(String[] args) {
        RestorePropTask task = new RestorePropTask();
        task.run();
    }

}
