package com.spider.service;
import java.util.*;
import com.spider.model.UploadImg;
import com.spider.model.UploadResult;
import com.spider.util.AliOssUtil;
import com.spider.util.JdOssClientUtil;
import com.spider.util.Message;
import com.spider.util.ViewUtil;
import com.tomcong.jdbc.exception.JdbcException;
import com.tomcong.util.DataRow;
import com.tomcong.util.StringHelper;
public class UploadImgService extends ProjectBaseService {
	private static UploadImgService service;
	public static UploadImgService getInstance() {
		if (service == null) {
			service = new UploadImgService();
		}
		return service;
	}
	public void saveDetail(UploadImg uploadImg){
		   if(uploadImg==null)return;
		   uploadImg.setStatus(3);
		   uploadImg.setState(2);
		   DataRow data = new DataRow();
		   if(uploadImg.getId()!=0)data.set("id", uploadImg.getId());
		   if(StringHelper.isNotEmpty(uploadImg.getUrl()))data.set("url1", uploadImg.getUrl());
		   if(StringHelper.isNotEmpty(uploadImg.getAliImg()))data.set("url2", uploadImg.getAliImg());
		   if(StringHelper.isNotEmpty(uploadImg.getJdImg()))data.set("url3", uploadImg.getJdImg());
		   if(uploadImg.getFileSize()!=0)data.set("file_size", uploadImg.getFileSize());
		   if(uploadImg.getOrgSize()!=0)data.set("org_size", uploadImg.getOrgSize());
		   if(0!=uploadImg.getBuyUrlId())data.set("buy_url_id", uploadImg.getBuyUrlId());
		   if(uploadImg.getStatus()!=0)data.set("status", uploadImg.getStatus());
		   if(uploadImg.getState()!=0)data.set("state", uploadImg.getState());
		   data.set("w",uploadImg.getW());
           data.set("h",uploadImg.getH());
           data.set("create_date", new Date());
           if(StringHelper.isEmpty(uploadImg.getUrl()))throw new JdbcException("url1 miss");
           if(0==uploadImg.getBuyUrlId())throw new JdbcException("buy_url_id miss");
           if(uploadImg.getStatus()==0)throw new JdbcException("status miss");
           t.insert("t_detail_img", data);
	}
	public void saveSwipe(UploadImg uploadImg){
		   if(uploadImg==null)return;
		   uploadImg.setStatus(3);
		   uploadImg.setState(2);
		   DataRow data = new DataRow();
		   if(uploadImg.getId()!=0)data.set("id", uploadImg.getId());
		   if(StringHelper.isNotEmpty(uploadImg.getUrl()))data.set("url1", uploadImg.getUrl());
		   if(StringHelper.isNotEmpty(uploadImg.getAliImg()))data.set("url2", uploadImg.getAliImg());
		   if(StringHelper.isNotEmpty(uploadImg.getJdImg()))data.set("url3", uploadImg.getJdImg());
		   if(uploadImg.getFileSize()!=0)data.set("file_size", uploadImg.getFileSize());
		   if(uploadImg.getOrgSize()!=0)data.set("org_size", uploadImg.getOrgSize());
		   if(0!=uploadImg.getBuyUrlId())data.set("buy_url_id", uploadImg.getBuyUrlId());
		   if(uploadImg.getStatus()!=0)data.set("status", uploadImg.getStatus());
		   if(uploadImg.getState()!=0)data.set("state", uploadImg.getState());
			   data.set("create_date", new Date());
			   if(StringHelper.isEmpty(uploadImg.getUrl()))throw new JdbcException("url1 miss");
			   if(0==uploadImg.getBuyUrlId())throw new JdbcException("buy_url_id miss");
			   if(uploadImg.getStatus()==0)throw new JdbcException("status miss");
			   t.insert("t_swipe_img", data);
	}
	
	public void saveWhite(UploadImg uploadImg){
		   if(uploadImg==null)return;
		   uploadImg.setStatus(3);
		   uploadImg.setState(2);
		   DataRow data = new DataRow();
		   if(uploadImg.getId()!=0)data.set("id", uploadImg.getId());
		   if(StringHelper.isNotEmpty(uploadImg.getUrl()))data.set("url1", uploadImg.getUrl());
		   if(StringHelper.isNotEmpty(uploadImg.getAliImg()))data.set("url2", uploadImg.getAliImg());
		   if(StringHelper.isNotEmpty(uploadImg.getJdImg()))data.set("url3", uploadImg.getJdImg());
		   if(uploadImg.getFileSize()!=0)data.set("file_size", uploadImg.getFileSize());
		   if(uploadImg.getOrgSize()!=0)data.set("org_size", uploadImg.getOrgSize());
		   if(0!=uploadImg.getBuyUrlId())data.set("buy_url_id", uploadImg.getBuyUrlId());
		   if(uploadImg.getStatus()!=0)data.set("status", uploadImg.getStatus());
		   if(uploadImg.getState()!=0)data.set("state", uploadImg.getState());
		   if(uploadImg.getId()==0){
			   data.set("create_date", new Date());
			   if(StringHelper.isEmpty(uploadImg.getUrl()))throw new JdbcException("url1 miss");
			   if(0==uploadImg.getBuyUrlId())throw new JdbcException("buy_url_id miss");
			   if(uploadImg.getStatus()==0)throw new JdbcException("status miss");
			   t.insert("t_white_img", data);
		   }else{
			   data.set("edit_date", new Date());
			   t.update("t_white_img", data, "id", uploadImg.getId());
		   }
	}

	
	public void saveMain(UploadImg uploadImg){
		   if(uploadImg==null)return;
		   uploadImg.setStatus(3);
		   uploadImg.setState(2);
		   DataRow data = new DataRow();
		   if(uploadImg.getId()!=0)data.set("id", uploadImg.getId());
		   if(StringHelper.isNotEmpty(uploadImg.getUrl()))data.set("url1", uploadImg.getUrl());
		   if(StringHelper.isNotEmpty(uploadImg.getAliImg()))data.set("url2", uploadImg.getAliImg());
		   if(StringHelper.isNotEmpty(uploadImg.getJdImg()))data.set("url3", uploadImg.getJdImg());
		   if(uploadImg.getFileSize()!=0)data.set("file_size", uploadImg.getFileSize());
		   if(uploadImg.getOrgSize()!=0)data.set("org_size", uploadImg.getOrgSize());
		   if(0!=uploadImg.getBuyUrlId())data.set("buy_url_id", uploadImg.getBuyUrlId());
		   if(uploadImg.getStatus()!=0)data.set("status", uploadImg.getStatus());
		   if(uploadImg.getState()!=0)data.set("state", uploadImg.getState());
		   if(uploadImg.getId()==0){
			   data.set("create_date", new Date());
			   if(StringHelper.isEmpty(uploadImg.getUrl()))throw new JdbcException("url1 miss");
			   if(0==uploadImg.getBuyUrlId())throw new JdbcException("buy_url_id miss");
			   if(uploadImg.getStatus()==0)throw new JdbcException("status miss");
			   t.insert("t_main_img", data);
		   }else{
			   data.set("edit_date", new Date());
			   t.update("t_main_img", data, "id", uploadImg.getId());
		   }
	}

	public UploadImg doUploadSwipe(String img,long buyUrlId) {
		// TODO Auto-generated method stub
			String filename = generateSwipeImgName(buyUrlId);
			UploadResult result = AliOssUtil.resize(img, filename);
			//上传阿里失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			String picUrl = result.getUrl();
			long orgSize = result.getFilesize();
			String shortUrl = formatSwipeOrSkuPicUrlToLowerQuality(picUrl);
			result = JdOssClientUtil.doUpload(shortUrl, filename);
			//上传京东失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			long fileSize = result.getFilesize();
			if (fileSize > 500 * 1024) {
				shortUrl = formatBigPicTo50Quality(picUrl);
				result = JdOssClientUtil.doUpload(shortUrl, filename);
				if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())||result.getFilesize() > 500 * 1204)
						return null;
					fileSize = result.getFilesize();
				}
				String jdImg = result.getUrl();
				System.err.println("成功上传["+buyUrlId+"]的轮播图图片["+jdImg+"]");
				UploadImg uploadImg = new UploadImg(picUrl, jdImg, orgSize,fileSize);
				uploadImg.setBuyUrlId(buyUrlId);
				uploadImg.setUrl(img);
				return uploadImg;
	}

	/**
	 * 上传属性图
	 * @param img(图片原始地址)
	 * @param buyUrlId(商品的标识id)
	 * @return
	 */
	public UploadImg doUploadSku(String img,long buyUrlId) {
		// TODO Auto-generated method stub
			String filename = this.generateSkuImgName(buyUrlId);
			UploadResult result = AliOssUtil.resize(img, filename);
			//上传阿里失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			String picUrl = result.getUrl();
			long orgSize = result.getFilesize();
			String shortUrl = formatSwipeOrSkuPicUrlToLowerQuality(picUrl);
			result = JdOssClientUtil.doUpload(shortUrl, filename);
			//上传京东失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			long fileSize = result.getFilesize();
			if (fileSize > 500 * 1024) {
				shortUrl = formatBigPicTo50Quality(picUrl);
				result = JdOssClientUtil.doUpload(shortUrl, filename);
				if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())||result.getFilesize() > 500 * 1204)
						return null;
					fileSize = result.getFilesize();
				}
				String jdImg = result.getUrl();
				UploadImg uploadImg = new UploadImg(picUrl, jdImg, orgSize,fileSize);
				uploadImg.setBuyUrlId(buyUrlId);
				uploadImg.setUrl(img);
				return uploadImg;
	}
	
	public UploadImg doUploadDetail(String img,long buyUrlId) {
		// TODO Auto-generated method stub
			String filename = this.generateDetailImgName(buyUrlId);
			UploadResult result = AliOssUtil.upload(img, filename);
			//上传阿里失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			if(result.getW()<=20||result.getH()<=20)return null;
			if(result.getFilesize()<5*1024)return null;
            int w = result.getW();
            int h =result.getH();
            if(w>800){
                 h = (int)(750*h/w);
                 result =AliOssUtil.cut(img,filename,h);
            }
			String picUrl = result.getUrl();
			long orgSize = result.getFilesize();
			String shortUrl = formatPicUrlToLowerQuality(picUrl, 50);
			result = JdOssClientUtil.doUpload(shortUrl, filename);
			//上传京东失败
			if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())) {
				return null;
			}
			long fileSize = result.getFilesize();
			if (fileSize > 500 * 1024) {
				shortUrl = formatBigPicTo50Quality(picUrl);
				result = JdOssClientUtil.doUpload(shortUrl, filename);
				if (result == null || result.getCode() != 0|| StringHelper.isEmpty(result.getUrl())||result.getFilesize() > 500 * 1204)
						return null;
					fileSize = result.getFilesize();
            }

				String jdImg = result.getUrl();
				UploadImg uploadImg = new UploadImg(picUrl, jdImg, orgSize,fileSize);
				uploadImg.setBuyUrlId(buyUrlId);
				uploadImg.setUrl(img);
                uploadImg.setW(result.getW());
				uploadImg.setH(result.getH());
				return uploadImg;
	}

    /**
     * 批量插入主图,要求主图必须有5张以上,并且成功上传5张
     * @param imgs(图片地址集合)
     * @param buyUrlId(商品id)
     * @return
     */
    public Message insertMainImgs(List<String> imgs, long buyUrlId){
        //首先查询该商品是否已经有主图
        String mainImg = t.queryString("select url1 from t_main_img where buy_url_id=?",new Object[]{buyUrlId});
        long mid =0;
        if(StringHelper.isNotEmpty(mainImg)){
            mid = buyUrlId;
            if(imgs.contains(mainImg)){
				imgs.remove(mainImg);
			}else{
            	imgs.remove(0);
			}
        }else{
            mainImg =imgs.get(0);
            //查询该张主图是否已经存在
            mid = t.queryLong("select buy_url_id from t_main_img where url1=?",new Object[]{mainImg});
            if(mid>0)return ViewUtil.errorMsg(String.format("商品主图[%s]已经在另外的商品[%d]的主图中",mainImg,mid));
        }
        //查询商品是否已经有白底图
        String whiteImg = t.queryString("select url1 from t_white_img where buy_url_id=?",new Object[]{buyUrlId});
        long wid = 0;
        if(StringHelper.isNotEmpty(whiteImg)){
            wid = buyUrlId;
			if(imgs.contains(whiteImg)){
				imgs.remove(whiteImg);
			}else{
				imgs.remove(imgs.size()-1);
			}
        }else{
            whiteImg = imgs.get(imgs.size()-1);
            wid = t.queryLong("select buy_url_id from t_white_img where url1=?",new Object[]{whiteImg});
            if(wid>0)return ViewUtil.errorMsg(String.format("商品白底图[%s]已经在另外的商品[%d]的白底图中",whiteImg,wid));
        }
        //获取图片初级指纹(原图大小和压缩图大小)
        Map<String,String> marks =new HashMap<String,String>();
        UploadImg result = null;
        if(mid==0){
            result = this.doUploadSwipe(mainImg, buyUrlId);
            if(result==null)return ViewUtil.errorMsg(String.format("主图[%s]上传失败",mainImg));
            String existsMainImg = existsMainImg(result.getOrgSize(),result.getFileSize());
            if(StringHelper.isNotEmpty(existsMainImg))ViewUtil.errorMsg(String.format("主图[%s]无法上传,和库里[%s]图片相同",mainImg,existsMainImg));
            try{
                this.saveMain(result);
                marks.put(result.getMark(),result.getUrl());
                imgs.remove(mainImg);
            }catch(JdbcException e){
                return ViewUtil.errorMsg(String.format("主图[%s]上传异常[%s]",mainImg,parseJdbcExceptionMsg(e)));
            }
        }
        if(wid==0){
            result = this.doUploadSwipe(whiteImg, buyUrlId);
            if(result==null)return ViewUtil.errorMsg(String.format("白底图[%s]上传失败",whiteImg));
            if(marks.containsKey(result.getMark()))return ViewUtil.errorMsg(String.format("白底图[%s]和主图[%s]属于同一张图片",whiteImg,mainImg));
            String existsWhiteImg = existsWhiteImg(result.getOrgSize(),result.getFileSize());
            if(StringHelper.isNotEmpty(existsWhiteImg))ViewUtil.errorMsg(String.format("白底图[%s]无法上传,和库里[%s]图片相同",whiteImg,existsWhiteImg));
            try{
                this.saveWhite(result);
                marks.put(result.getMark(),result.getUrl());
                imgs.remove(whiteImg);
            }catch(JdbcException e){
                return ViewUtil.errorMsg(String.format("白底图[%s]上传异常[%s]",whiteImg,parseJdbcExceptionMsg(e)));
            }
        }
        int pos = 2;
        StringBuffer info = new StringBuffer();
        for(String img:imgs){
            if(pos>=5)return ViewUtil.successMsg();
            long sid = t.queryLong("select buy_url_id from t_swipe_img where url1=? ", new Object[]{img});
            if(sid>0){
                if(sid!=buyUrlId){
                    info.append(String.format("商品轮播图[%s]已经在其他商品[%d]中存在",img,sid));
                }else{
                    pos++;
                }
            }else{
                result = this.doUploadSwipe(img, buyUrlId);
                if(result==null){
                    info.append(String.format(",轮播图[%s]上传失败",img));
                }else{
                    if(marks.containsKey(result.getMark())){
                        info.append(String.format(",轮播图[%s]和轮播图[%s]属于同一张图片",img,marks.get(result.getMark())));
                    }else{
                        DataRow swipeRow = existsSwipeImg(result.getOrgSize(),result.getFileSize());
                        if(swipeRow!=null){
                            String existsSwipeImg = swipeRow.getString("url1");
                            long bid = swipeRow.getLong("buy_url_id");
                            if(!existsSwipeImg.equals(img)){
                                if(bid==buyUrlId){
                                    pos++;
                                }else{
                                    info.append(String.format("轮播图[%s]无法上传,和库里[%s]图片相同",img,existsSwipeImg));
                                }
                            }else{
                                if(bid==buyUrlId){
                                    pos++;
                                }else{
                                    info.append(String.format("轮播图[%s]无法上传,该图片已经被[%d]的商品引用",img,bid));
                                }
                            }
                        }else{
                            try{
                                this.saveSwipe(result);
                                pos++;
                                marks.put(result.getMark(),result.getUrl());
                            }catch(JdbcException e){
                                info.append(String.format(",上传轮播图[%s]出现数据库异常[%s]",img,this.parseJdbcExceptionMsg(e)));
                            }
                        }

                    }
                }
            }
        }
        if(pos<5)return ViewUtil.errorMsg(info.toString());
        return ViewUtil.successMsg();
    }



	public void clearMainImgs(long buyUrlId) {
		t.update("delete from t_main_img where buy_url_id =?",new Object[]{buyUrlId});
		//t.update("delete from t_swipe_img where buy_url_id =?",new Object[]{buyUrlId});
		t.update("delete from t_white_img where buy_url_id =?",new Object[]{buyUrlId});
		long[] ids = t.queryLongArray("select id from t_swipe_img where buy_url_id =?",new Object[]{buyUrlId});
		if(ids!=null&&ids.length>0){
			for(long id:ids){
				t.delete("t_swipe_img","id",id);
			}
		}
	}

    public String existsMainImg(long orgSize, long fileSize) {
        return t.queryString("select url1 from t_main_img where org_size=? and  file_size=?" ,new Object[]{orgSize,fileSize});
    }
    public String existsWhiteImg(long orgSize, long fileSize) {
        return t.queryString("select url1 from t_white_img where  org_size=? and  file_size=?" ,new Object[]{orgSize,fileSize});
    }
    public DataRow existsSwipeImg(long orgSize, long fileSize) {
        return t.queryMap("select url1,buy_url_id from t_swipe_img where org_size=? and  file_size=?" ,new Object[]{orgSize,fileSize});
    }
    public Map<String,String> insertDetailImgs(List<String> detailImgs, long buyUrlId) {
		// TODO Auto-generated method stub
        this.clearDetailImg(buyUrlId);
		Map<String,String> map = new HashMap<String, String>();
		for(String img:detailImgs){
			UploadImg result = this.doUploadDetail(img, buyUrlId);
			if(result==null)continue;
			try{
				this.saveDetail(result);
				map.put(img,result.getJdImg());
				System.out.println(String.format("成功插入详情图[%s]",result.getJdImg()));
			}catch(JdbcException e){
				e.fillInStackTrace();
			}
		}
		return map;
	}

    private void clearDetailImg(long buyUrlId) {
        t.update("delete from t_detail_img where buy_url_id=?",new Object[]{buyUrlId});
    }

    private boolean compare(String url, String img) {
	    int pos = url.indexOf(".com/");
	    String a=url;
	    String b = img;
	    if(pos!=-1)a=a.substring(pos+".com/".length());
	    pos = img.indexOf(".com/");
	    if(pos!=-1)b=b.substring(pos+".com/".length());
	    return a.equals(b);
    }


    public Map<String,String> testSkuImgs(List<String> skuImgs, long buyUrlId) {
        // TODO Auto-generated method stub
        Map<String,String> map = new HashMap<String, String>();
        for(String img:skuImgs){
            UploadImg result = this.doUploadSku(img, buyUrlId);
            if(result==null){
                continue;
            }
            map.put(img,result.getMark());
        }
        return map;
    }
	public boolean existsMainSwipeWhiteImg(String img) {
		int count = t.queryInt("select count(id) from t_main_img where url1=?",new Object[]{img});
		if(count>0)return true;
		count = t.queryInt("select count(id) from t_swipe_img where url1=?",new Object[]{img});
		if(count>0)return true;
		count = t.queryInt("select count(id) from t_white_img where url1=?",new Object[]{img});
		return count>0;
	}
	public String existsMainSwipeWhiteImg(long orgSize,long fileSize){
		 String url1=t.queryString("select url1 from t_main_img where org_size=? and file_size=?",new Object[]{orgSize,fileSize});
		 if(StringHelper.isNotEmpty(url1))return url1;
		 url1 = t.queryString("select url1 from t_swipe_img where org_size=? and file_size=?",new Object[]{orgSize,fileSize});
		 if(StringHelper.isNotEmpty(url1))return url1;
		 return t.queryString("select url1 from t_white_img where org_size=? and file_size=?",new Object[]{orgSize,fileSize});
	}

    public static void main(String[] args) {
        System.out.println(UploadImgService.getInstance().compare("http://gd2.alicdn.com/imgextra/i2/709982809/O1CN01M2mBQq1WcZ1Ynlnci_!!709982809.jpg","http://gd3.alicdn.com/imgextra/i2/709982809/O1CN01M2mBQq1WcZ1Ynlnci_!!709982809.jpg"));
    }



}
