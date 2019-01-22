package com.spider.controller;
import com.spider.service.GoodsService;
import com.spider.util.AbsProxyUtil;
import com.spider.util.Message;
import com.spider.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
@Controller
@RequestMapping("beibei")
/**
 * 贝贝分解拼多多商品源
 */
public class GoodsController {
    private GoodsService service = GoodsService.getInstance();
    @RequestMapping("spiderV1")
    @ResponseBody
    public Message spiderV1(long buyUrlId,@RequestParam(required = false,defaultValue = "") String proxyIp){
        long t1 = System.currentTimeMillis();
        Message msg = service.spiderV1(buyUrlId,proxyIp);
        long t2 = System.currentTimeMillis();
        if(msg.getCode()==0)msg.setMsg(String.valueOf(t2-t1));
        return msg;
    }
    @RequestMapping("abs")
    @ResponseBody
    public Message abs(String url){
        return ViewUtil.successObj(AbsProxyUtil.spider(url));
    }


}
