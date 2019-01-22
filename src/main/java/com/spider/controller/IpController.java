package com.spider.controller;
import com.spider.service.IpService;
import com.spider.util.Message;
import com.spider.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Controller
@RequestMapping("ip")
public class IpController {
    private IpService service = IpService.getInstance();
    @RequestMapping("get")
    @ResponseBody
    public Message get(int plat){
         String result = service.getIp(plat);
         return ViewUtil.successObj(result);
    }
    @RequestMapping("show")
    @ResponseBody
    public Message show(HttpServletRequest request){
        String ip = String.format("%s:%d",request.getRemoteAddr(),request.getRemotePort());
        return ViewUtil.successObj(ip);
    }
    @RequestMapping("kuai")
    @ResponseBody
    public Message kuai(int page){
        List<String> list = service.getKuaiProxy(page);
        return ViewUtil.successObj(list);
    }
    @RequestMapping("xc/nn")
    @ResponseBody
    public Message xcnn(int page){
        List<String> list = null;
        try {
            list = service.xcnn(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        return ViewUtil.successObj(list);
    }
    @RequestMapping("xc/nt")
    @ResponseBody
    public Message xcnt(int page){
        List<String> list = null;
        try {
            list = service.xcnt(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        return ViewUtil.successObj(list);
    }

    @RequestMapping("xc/wn")
    @ResponseBody
    public Message xcwn(int page){
        List<String> list = null;
        try {
            list = service.xcwn(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        return ViewUtil.successObj(list);
    }

    @RequestMapping("xc/wt")
    @ResponseBody
    public Message xcwt(int page){
        List<String> list = null;
        try {
            list = service.xcwt(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        return ViewUtil.successObj(list);
    }
    @RequestMapping("qiyun")
    @ResponseBody
    public Message qiyun(int page){
        List<String> list = null;
        try {
            list = service.qiyun(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ViewUtil.errorMsg(e.getMessage());
        }
        return ViewUtil.successObj(list);
    }
    @RequestMapping("merge")
    @ResponseBody
    public Message merge(@RequestParam(value = "ips")List<String> ips,int plat){
        List<String> list = service.merge(ips,plat);
        return ViewUtil.successObj(list);

    }

}
