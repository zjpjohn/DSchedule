package com.zjp.schedule.entity;

import java.io.Serializable;

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
 * Module Desc:com.zjp.schedule.entity
 * User: zjprevenge
 * Date: 2016/8/9
 * Time: 9:42
 */

public class Request implements Serializable {
    //请求id
    private long requestId;
    //请求类名
    private String className;
    //请求方法名
    private String methodName;

    public Request() {
    }

    public Request(Builder builder) {
        this.requestId = builder.requestId;
        this.className = builder.className;
        this.methodName = builder.methodName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        //请求id
        private long requestId;
        //请求类名
        private String className;
        //请求方法名
        private String methodName;

        public Builder requestId(long requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public long getRequestId() {
        return requestId;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
