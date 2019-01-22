package com.spider.task;
import com.spider.service.IpService;
public class IpGatherTask implements Runnable {
    private IpService service = IpService.getInstance();
    @Override
    public void run() {
        while(service.getFlag()){
            service.doGatherXc();
            service.doGatherKuai();
            service.doGatherQiyun();
            try {
                Thread.sleep(3*60*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
