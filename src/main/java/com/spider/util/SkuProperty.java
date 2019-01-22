package com.spider.util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SkuProperty implements Comparable<SkuProperty>{
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(@NotNull SkuProperty o) {
        if(this.key.equals("颜色")||key.contains("颜色"))return -1;
        return 0;
    }

    @Override
    public String toString() {
        return "SkuProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public static void main(String[] args) {
        SkuProperty s1 = new SkuProperty();
        s1.setKey("a");
        s1.setValue("a");
        SkuProperty s2 = new SkuProperty();
        s2.setKey("颜色");
        s2.setValue("a");
        SkuProperty s3 = new SkuProperty();
        s3.setKey("尺码");
        s3.setValue("a");
        SkuProperty[] sp = new SkuProperty[3];
        sp[0]=s1;
        sp[1]=s2;
        sp[2]=s3;
        Arrays.sort(sp);
        System.out.println(Arrays.toString(sp));
    }
}
