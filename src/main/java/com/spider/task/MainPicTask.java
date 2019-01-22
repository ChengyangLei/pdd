package com.spider.task;
import com.spider.task.call.MainPicCall;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.concurrent.CountDownLatch;
/**
 * 上传商品的主图,轮播图,属性图
 */
public class MainPicTask extends GoodsParserTask {
    @Override
    /**
     * 状态7的商品为待分解主图的商品
     */
    public List<DataRow> scan() {
        return t.query("select buy_url_id from pdd_goods_json where state = ? limit 0,5",new Object[]{MAIN_STATE});
    }

    @Override
    public void deal(List<DataRow> list) {
        CountDownLatch threadSignal = new CountDownLatch(list.size());
        for(DataRow data:list){
            MainPicCall call = new MainPicCall(data.getLong("buy_url_id"),threadSignal);
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
        MainPicTask task = new MainPicTask();
        task.run();
    }
}
