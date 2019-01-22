package com.spider.task.restore;

import com.spider.task.GoodsParserTask;
import com.spider.task.call.DetailCall;
import com.spider.task.call.restore.RestoreDetailCall;
import com.tomcong.util.DataRow;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RestoreDetailTask extends GoodsParserTask {
    @Override
    public List<DataRow> scan() {
        return t.query("select buy_url_id from t_goods where pic_state=2 AND buy_url_id not in (select buy_url_id from t_sku_desc) limit 0,10");
    }
    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
            RestoreDetailCall call = new RestoreDetailCall(data.getLong("buy_url_id"),threadSignal);
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
        RestoreDetailTask task = new RestoreDetailTask();
        task.run();
    }

}
