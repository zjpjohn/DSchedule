package com.zjp.schedule.distribute;

import com.netflix.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import com.zjp.schedule.core.Config;
import com.zjp.schedule.core.ZkClient;
import com.zjp.schedule.entity.Request;
import org.pinae.rafiki.job.Job;
import org.pinae.rafiki.job.JobException;

import java.util.UUID;

/**
 * ━━━━━━南无阿弥陀佛━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━萌萌哒━━━━━━
 * Module Desc:com.zjp.schedule.distribute
 * User: zjprevenge
 * Date: 2016/8/10
 * Time: 16:59
 */

public abstract class DistributeJob implements Job {

    private InterProcessSemaphoreV2 lock;
    private ZkClient zkClient;
    private String zkUrl;
    private String appName;
    private String className;
    private String methodName;
    private Request request;

    public DistributeJob() {
    }

    public DistributeJob(String zkUrl, String appName, String className, String methodName) {
        this.setZkUrl(zkUrl);
        this.setAppName(appName);
        this.setClassName(className);
        this.setMethodName(methodName);
        this.request = Request.builder()
                .requestId(UUID.randomUUID().timestamp())
                .className(className)
                .methodName(methodName)
                .build();
        init();
    }

    public void init() {
        zkClient = ZkClient.init(getZkUrl(), Config.ZK_CLIENT_PATH);
        lock = zkClient.getLock(Config.ZK_CLIENT_PATH + "/" + getAppName() +
                ":" + getClassName() + ":" + getMethodName() + "/lock", 1);

    }

    public String getZkUrl() {
        return zkUrl;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Request getRequest() {
        return request;
    }

    public boolean execute() throws JobException {
        try {
            //获取锁，获得锁时执行业务操作
            if (lock.acquire() != null) {
                return call();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public abstract boolean call();
}
