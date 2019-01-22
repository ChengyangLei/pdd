package com.spider.controller;
import com.spider.model.SpiderInfo;
import com.spider.service.UploadImgService;
import com.spider.util.Message;
import com.spider.util.PicTailUtil;
import com.spider.util.SkuSpiderUtil;
import com.spider.util.ViewUtil;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.StringHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RequestMapping("pic")
@Controller
public class PicController {
    private UploadImgService service = UploadImgService.getInstance();
    @RequestMapping(value="uploadMainImg",method = RequestMethod.POST)
    @ResponseBody
    public Message uploadMainImg(@RequestParam(value="imgs")List<String> imgs,long buyUrlId){
         if(imgs==null||imgs.size()<5)return ViewUtil.errorMsg("主图不足5张");
        long t1 =System.currentTimeMillis();
        try {
            Message msg = service.insertMainImgs(imgs, buyUrlId);
            long t2 =System.currentTimeMillis();
            msg.setMsg(String.format("%dms",t2-t1));
            return msg;
        }catch(JdbcException e){
            String msg = e.getMessage();
            if(StringHelper.isEmpty(msg))msg=e.getCause().getMessage();
            return ViewUtil.errorMsg(msg);
        }
    }

    @RequestMapping("testSkuImg")
    @ResponseBody
    public Message testSkuImg(@RequestParam(value="imgs")List<String> imgs,long buyUrlId){
        if(imgs==null||imgs.size()==0)return ViewUtil.errorMsg("图片为空!");
        long t1 =System.currentTimeMillis();
        try {
            Map<String, String> map = service.testSkuImgs(imgs,buyUrlId);
            long t2 =System.currentTimeMillis();
            return ViewUtil.successObj(map,String.format("%dms",t2-t1));
        }catch(JdbcException e){
            String msg = e.getMessage();
            if(StringHelper.isEmpty(msg))msg=e.getCause().getMessage();
            return ViewUtil.errorMsg(msg);
        }
    }
    @RequestMapping("mark")
    @ResponseBody
    public Message getPicMark(String url){
        long t1 = System.currentTimeMillis();
        String mark = PicTailUtil.produceFingerPrint(url);
        long t2 = System.currentTimeMillis();
        return ViewUtil.successObj(mark,String.format("%dms",t2-t1));
    }
}
