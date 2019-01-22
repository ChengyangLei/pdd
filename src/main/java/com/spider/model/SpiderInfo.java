package com.spider.model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class SpiderInfo {
    private Set<String> mainImgs = new HashSet<String>();
    private String title;
    private List<SkuPrice> prices = new ArrayList<SkuPrice>();
    private List<SkuKey> keys = new ArrayList<SkuKey>();
    private List<SkuProp> props = new ArrayList<SkuProp>();
    private long cid;
    private long fid;
    private long sid;
    private String desc;
    private String brand;

    public Set<String> getMainImgs() {
        return mainImgs;
    }

    public void setMainImgs(Set<String> mainImgs) {
        this.mainImgs = mainImgs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SkuPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SkuPrice> prices) {
        this.prices = prices;
    }

    public List<SkuKey> getKeys() {
        return keys;
    }

    public void setKeys(List<SkuKey> keys) {
        this.keys = keys;
    }

    public List<SkuProp> getProps() {
        return props;
    }

    public void setProps(List<SkuProp> props) {
        this.props = props;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

}
