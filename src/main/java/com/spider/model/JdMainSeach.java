package com.spider.model;
import java.io.Serializable;
import java.util.List;
/**
 * 京东关键词搜素第一页
 */
public class JdMainSeach implements Serializable{
    private int totalPage;
    private String baseUrl;
    private List<JdGoods> list;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<JdGoods> getList() {
        return list;
    }

    public void setList(List<JdGoods> list) {
        this.list = list;
    }
}
