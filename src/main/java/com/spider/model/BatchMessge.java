package com.spider.model;
import com.spider.util.Message;
import java.util.List;
public class BatchMessge {
    private List<Message> successList;
    private List<Message> errorList;
    private List<Long> timeOutList;
    private int scount;
    private int ecount;

    public List<Message> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<Message> successList) {
        this.successList = successList;
    }

    public List<Message> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Message> errorList) {
        this.errorList = errorList;
    }

    public int getScount() {
        return scount;
    }

    public void setScount(int scount) {
        this.scount = scount;
    }

    public int getEcount() {
        return ecount;
    }

    public void setEcount(int ecount) {
        this.ecount = ecount;
    }

    public List<Long> getTimeOutList() {
        return timeOutList;
    }

    public void setTimeOutList(List<Long> timeOutList) {
        this.timeOutList = timeOutList;
    }
}
