package com.spider.model;
import java.io.Serializable;
/**
 * 京东关键词搜索对象
 */
public class JdSearchDto implements Serializable {
    private String key;
    private int psort;
    private int lowPrice;
    private int highPrice;
    private int commentVal;
    private int page;
    private String baseUrl;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPsort() {
        return psort;
    }

    public void setPsort(int psort) {
        this.psort = psort;
    }

    public int getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(int lowPrice) {
        this.lowPrice = lowPrice;
    }

    public int getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(int highPrice) {
        this.highPrice = highPrice;
    }

    public int getCommentVal() {
        return commentVal;
    }

    public void setCommentVal(int commentVal) {
        this.commentVal = commentVal;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
