package com.zjp.schedule.entity;

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
 * Time: 9:51
 */

public class Response {

    //响应id
    private long responseId;
    //响应异常
    private Throwable error;
    //响应结果
    private Object result;

    public Response() {
    }

    public Response(Builder builder) {
        this.responseId = builder.responseId;
        this.error = builder.error;
        this.result = builder.result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        //响应id
        private long responseId;
        //响应异常
        private Throwable error;
        //响应结果
        private Object result;

        public Builder responseId(long responseId) {
            this.responseId = responseId;
            return this;
        }

        public Builder error(Throwable error) {
            this.error = error;
            return this;
        }

        public Builder result(Object result) {
            this.result = result;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    public long getResponseId() {
        return responseId;
    }

    public Throwable getError() {
        return error;
    }

    public Object getResult() {
        return result;
    }
}
