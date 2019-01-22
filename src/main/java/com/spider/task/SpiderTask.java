package com.spider.task;
import com.spider.task.call.SpiderCall;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.concurrent.CountDownLatch;
public class SpiderTask extends GoodsParserTask {
    @Override
    public List<DataRow> scan() {
        return t.query("select buy_url_id from pdd_goods_json where state = ? and buy_url_id>0 limit 0,10",new Object[]{SPIDER_STATE});
    }

    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
            SpiderCall call = new SpiderCall(data.getLong("buy_url_id"),threadSignal);
            Thread thread = new Thread(call);
            thread.start();
        }
        try {
            threadSignal.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 等待所有子线程执行完
    }

    public static void main(String[] args) {
        SpiderTask task = new SpiderTask();
        task.run();
    }


}
