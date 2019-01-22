package com.spider.task;
import com.spider.service.IpService;
public class IpValidateTask implements Runnable {
    private IpService service = IpService.getInstance();
    @Override
    public void run() {
        while (service.getFlag()){
            service.run();
            try {
                Thread.sleep(60*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
