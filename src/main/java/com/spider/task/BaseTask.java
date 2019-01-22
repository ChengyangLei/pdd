package com.spider.task;
import com.tomcong.config.Configuration;
import com.tomcong.util.DataRow;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public abstract class BaseTask{
    public final static int debug = Configuration.getInt("model.debug",1);
    public final static String spiderRestUrl=debug==1?"http://localhost:8000/beibei/spider":"http://114.67.93.230/beibei/spider";
    public final static String spiderV1RestUrl=debug==1?"http://localhost:8000/beibei/spiderV1":"http://114.67.93.230/beibei/spiderV1";
    public final static String parserRestUrl =debug==1?"http://localhost:8000/beibei/parse":"http://114.67.93.230/beibei/parse";
    public final static String ipRestUrl =debug==1?"http://www.benliubao.com:10000/ip/anon/ip":"http://114.67.93.230/ip/show";

}
