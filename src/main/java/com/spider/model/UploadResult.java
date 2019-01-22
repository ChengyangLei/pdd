package com.spider.model;

public class UploadResult {
    private int code;
    private String url;
    private String msg;
    private long filesize;
    private int w;
    private int h;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public UploadResult(String msg) {
		this.code=-1;
		this.msg = msg;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public UploadResult() {
	}

    @Override
    public String toString() {
        return "UploadResult{" +
                "code=" + code +
                ", url='" + url + '\'' +
                ", msg='" + msg + '\'' +
                ", filesize=" + filesize +
                ", w=" + w +
                ", h=" + h +
                '}';
    }
}
