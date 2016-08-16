package com.zjp.schedule.core;

import com.zjp.schedule.entity.Request;
import com.zjp.schedule.entity.Response;

import java.io.IOException;

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
 * Module Desc:com.zjp.schedule.core
 * User: zjprevenge
 * Date: 2016/8/11
 * Time: 18:00
 */

public interface RpcConnector {

    /**
     * 请求rpc服务
     *
     * @param request
     * @return
     * @throws IOException
     */
    Response invoke(Request request) throws IOException;

    /**
     * 设置host
     *
     * @param host
     */
    void setHost(String host);

    /**
     * 设置端口
     *
     * @param port
     */
    void setPort(int port);

    /**
     * 启动
     *
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * 停止
     *
     * @throws IOException
     */
    void stop() throws IOException;
}
