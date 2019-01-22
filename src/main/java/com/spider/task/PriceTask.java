package com.spider.task;
import com.spider.task.call.PriceCall;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.concurrent.CountDownLatch;
public class PriceTask extends GoodsParserTask {
    @Override
    public List<DataRow> scan() {
        return t.query("select buy_url_id,json from "+table+" where state = ? limit 0,10",new Object[]{PRICE_STATE});
    }

    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
            PriceCall call = new PriceCall(data.getLong("buy_url_id"),threadSignal);
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
        PriceTask task = new PriceTask();
        task.run();
    }

}
