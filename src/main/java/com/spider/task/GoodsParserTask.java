package com.spider.task;
import com.spider.service.UploadImgService;
import com.tomcong.config.Configuration;
import com.tomcong.jdbc.JdbcTemplate;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public abstract class GoodsParserTask   extends TimerTask {
    UploadImgService service = UploadImgService.getInstance();
    public JdbcTemplate t = service.getJdbcTemplate("web");
    public final String table  = Configuration.getString("model.table","pdd_goods_json");
    /**
     * 商品json数据待采集状态
     */
    public final static int SPIDER_STATE =1;
    /**
     * 商品json校验失败
     */
    public final  static  int ERROR_SPIDER_STATE =-1;
    /**
     * 主图待处理流程状态
     */
    public final static int MAIN_STATE =7;
    public final static int ERROR_MAIN_STATE =-7;
    /**
     * 属性图待处理流程状态
     */
    public final static int PROP_STATE =8;
    /**
     * 属性信息插入异常
     */
    public final static int ERROR_PROP_STATE =-8;
    /**
     * 价格信息待处理流程状态
     */
    public final static int PRICE_STATE =2;
    public final static int ERROR_PRICE_STATE =-2;
    /**
     * 详情信息和详情图处理流程
     */
    public final static int DETAIL_STATE =3;
    /**
     * 详情图信息异常
     */
    public final static int ERROR_DETAIL_STATE = -3;
    /**
     * 前三步流程走完后的其他流程
     */
    public final static int OTHER_STATE =4;
    public final static int ERROR_OTHER_STATE =-4;
    /**
     * 最终成功状态
     */
    public final static int SUCCESS_STATE =5;
    /**
     * 扫描需要处理的数据
     * @return
     */
    public abstract List<DataRow> scan();
    /**
     * 处理单项商品
     */
    public abstract void deal(List<DataRow> list);
    /**
     * 休眠
     */
    public void sleep(int seconds){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        while(true){
            List<DataRow> list = null;
            try {
                list = scan();
            }catch (JdbcException e){
                e.fillInStackTrace();
            }
            if(list==null||list.size()==0){
                sleep(60);
            }else {
                deal(list);
            }
        }
    }


}
