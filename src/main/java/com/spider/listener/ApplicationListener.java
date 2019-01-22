package com.spider.listener;
import com.spider.task.*;
import com.tomcong.config.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
@WebListener
public class ApplicationListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if(Configuration.getInt("model.debug",1)==0){
            new Thread(new SpiderTask()).start();
            new Thread(new MainPicTask()).start();
            new Thread(new PropTask()).start();
            new Thread(new PriceTask()).start();
            new Thread(new DetailTask()).start();
            new Thread(new OtherTask()).start();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}