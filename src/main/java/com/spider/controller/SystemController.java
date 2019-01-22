package com.spider.controller;
import com.spider.service.SpiderTestService;
import com.spider.util.HttpClientUtil;
import com.spider.util.Message;
import com.spider.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
@Controller
@RequestMapping("system")
public class SystemController {
    @RequestMapping("shutdown")
    @ResponseBody
    public Message shutdown(){
        try {
            HttpClientUtil.get("http://localhost:6000/system/close");
            Thread.sleep(10*1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ViewUtil.successMsg();
    }
    @RequestMapping("close")
    @ResponseBody
    public Message close(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5);
                        System.exit(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ViewUtil.successMsg();
    }
    @RequestMapping("update")
    @ResponseBody
    public Message update(){
        try {
            Process process = Runtime.getRuntime().exec("TortoiseProc.exe /command:update /path C:\\tb_spider");
            try {
                Thread.sleep(60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Runtime.getRuntime().exec("cmd /k start C:\\tb_spider\\tb.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ViewUtil.successMsg();
    }
    @RequestMapping("restore")
    @ResponseBody
    public Message table(String table){
        new SpiderTestService().restoreGoods(table);
        return ViewUtil.successMsg();
    }

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("cmd /k start C:\\tb_spider\\tb.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
