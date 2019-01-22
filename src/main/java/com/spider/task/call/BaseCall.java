package com.spider.task.call;
import com.tomcong.config.Configuration;
import com.tomcong.jdbc.JdbcTemplate;
import com.tomcong.service.BaseService;

import java.util.concurrent.Callable;
public abstract class BaseCall implements Runnable {
    public JdbcTemplate t = new BaseService().getJdbcTemplate("web");
    public final static int debug = Configuration.getInt("model.debug",1);
    public abstract void call();
    public abstract void deal();
    public abstract void notifyError(String msg);
    public abstract void notifyFinish();
    public final String table ="pdd_goods_json";
    public void run() {
        try{
            call();
        }catch (RuntimeException e){
            notifyError(e.getMessage());
        }

    }

}
