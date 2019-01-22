package com.spider.task.call;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.SpiderInfo;
import com.spider.model.UploadImg;
import com.spider.service.UploadImgService;
import com.spider.task.GoodsParserTask;
import com.spider.util.AliOssUtil;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.StringHelper;
import java.util.*;
import java.util.concurrent.CountDownLatch;
public class MainPicCall extends BaseCall {
    private UploadImgService service = UploadImgService.getInstance();
    private long buyUrlId;
    private CountDownLatch threadSignal;
    public MainPicCall(long buyUrlId, CountDownLatch threadSignal) {
        this.buyUrlId = buyUrlId;
        this.threadSignal = threadSignal;
    }
    public MainPicCall(long buyUrlId){
        this.buyUrlId = buyUrlId;
        this.threadSignal = new CountDownLatch(1);
    }

    @Override
    public void call(){
        try{
            deal();
        }finally {
            threadSignal.countDown();
        }
    }

    @Override
    public void deal() {
        String json = t.queryString("select json from "+table+" where buy_url_id=?",new Object[]{buyUrlId});
        SpiderInfo info = JSONObject.parseObject(json,SpiderInfo.class);
        service.clearMainImgs(buyUrlId);
        Set<String> imgs = info.getMainImgs();
        List<String> filterImgs = new ArrayList<String>();
        for(String img:imgs){
            if(!service.existsMainSwipeWhiteImg(img))filterImgs.add(img);
        }
        if(filterImgs.size()<5){
            notifyError("主图,轮播图,白底图图片名和库里的图片重复,去重后低于5张");
            return;
        }
        /*
        if(filterImgs.size()>10)filterImgs= filterImgs.subList(0,10);
        Map<String,UploadResult> map = null;
        if(filterImgs.size()<=5){
            map =AliOssUtil.batchUploadMainImg(filterImgs,buyUrlId);
        }else{
            map =AliOssUtil.batchUploadMainImg(filterImgs.subList(0,5),buyUrlId);
            if(map!=null){
                map.putAll(AliOssUtil.batchUploadMainImg(filterImgs.subList(5,filterImgs.size()),buyUrlId));
            }
        }
        if(map==null||map.size()<5){
            notifyError("主图,轮播图,白底图批量上传阿里失败");
            return ;
        }
        List<String> jdImgList = new ArrayList<String>();
        for(Iterator<String> it = map.keySet().iterator();it.hasNext();){
            String img = it.next();
            UploadResult uploadResult = map.get(img);
            jdImgList.add(uploadResult.getUrl());
        }
        Map<String,UploadResult> jdMap = null;
        if(jdImgList.size()<=5){
            jdMap =JdOssClientUtil.batchUploadImgToJd(jdImgList);
        }else{
            jdMap =JdOssClientUtil.batchUploadImgToJd(jdImgList.subList(0,5));
            if(jdMap!=null){
                jdMap.putAll(JdOssClientUtil.batchUploadImgToJd(jdImgList.subList(5,jdImgList.size())));
            }
        }
        if(jdMap==null||jdMap.size()<5){
            notifyError("主图,轮播图,白底图批量上传京东失败");
            return;
        }
        */
        List<UploadImg> uploadImgs = new ArrayList<UploadImg>();
        Map<String,UploadImg> map = AliOssUtil.batchUploadMainOrSkuImg(filterImgs,buyUrlId);
        /* */
        for(Iterator<String> it = map.keySet().iterator();it.hasNext();){
            String img = it.next();
            UploadImg uploadImg = map.get(img);
            uploadImg.setBuyUrlId(buyUrlId);
            uploadImg.setState(2);
            uploadImg.setStatus(3);
            uploadImg.setUrl(img);
            uploadImgs.add(uploadImg);
        }
        if(uploadImgs.size()<5){
            notifyError("主图,轮播图,白底图批量上传京东失败");
            return;
        }
        StringBuffer erorrMsg = new StringBuffer();
        List<UploadImg> okUploadImgs = new ArrayList<UploadImg>();
        List<String> marks = new ArrayList<String>();
        for(UploadImg uploadImg:uploadImgs){
            String url = uploadImg.getUrl();
            long orgSize = uploadImg.getOrgSize();
            long fileSize = uploadImg.getFileSize();
            String img = service.existsMainSwipeWhiteImg(orgSize,fileSize);
            if(StringHelper.isNotEmpty(img)){
                String msg = String.format("-待上传图片[%s]和库里的图片[%s]相同-",url,img);
                erorrMsg.append(msg);
            }else {
                String mark = String.format("%d-%d",orgSize,fileSize);
                if(!marks.contains(mark)){
                    okUploadImgs.add(uploadImg);
                    marks.add(mark);
                }
            }
        }
        if(okUploadImgs.size()<5){
            notifyError(String.format("上传后校验图片大小后不足5张,附加错误[%s]",erorrMsg.toString()));
            return;
        }
        UploadImg mainImg =okUploadImgs.get(0);
        try {
            service.saveMain(mainImg);
            okUploadImgs.remove(0);
        }catch (JdbcException e){
            notifyError("主图上传失败["+e.getMessage()+"]");
            return;
        }
        UploadImg whiteImg = okUploadImgs.get(okUploadImgs.size()-1);
        try {
            service.saveWhite(whiteImg);
            okUploadImgs.remove(okUploadImgs.size()-1);
        }catch (JdbcException e){
            notifyError("白底图上传失败["+e.getMessage()+"]");
            return ;
        }
        int pos =0;
        erorrMsg = new StringBuffer();
        for(UploadImg uploadImg:okUploadImgs){
            try {
                service.saveSwipe(uploadImg);
                pos++;
            }catch (JdbcException e){
                erorrMsg.append(String.format("-%s-",e.getMessage()));
                return;
            }
        }
        if(pos<3){
            notifyError("轮播图上传失败["+erorrMsg+"]");
            return;
        }
        notifyFinish();
    }

    @Override
    public void notifyError(String msg) {
        t.update("update "+table+" set state =?,msg=? where buy_url_id=?",new Object[]{GoodsParserTask.ERROR_MAIN_STATE,msg,buyUrlId});
    }

    @Override
    public void notifyFinish() {
        t.update("update "+table+" set state = ? where buy_url_id=? ",new Object[]{GoodsParserTask.PROP_STATE,buyUrlId});
    }

    public static void main(String[] args) {
        MainPicCall mainPicCall = new MainPicCall(3735797834l);
        try {
            mainPicCall.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
