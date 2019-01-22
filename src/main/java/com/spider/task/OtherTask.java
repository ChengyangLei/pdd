package com.spider.task;
import com.spider.task.call.OtherCall;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.concurrent.CountDownLatch;
public class OtherTask extends GoodsParserTask {
    @Override
    public List<DataRow> scan() {
        return t.query("select buy_url_id,json from pdd_goods_json where state = ? limit 0,10",new Object[]{OTHER_STATE});
    }

    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
            OtherCall call = new OtherCall(data.getLong("buy_url_id"),threadSignal);
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
        new OtherTask().run();
    }
}
