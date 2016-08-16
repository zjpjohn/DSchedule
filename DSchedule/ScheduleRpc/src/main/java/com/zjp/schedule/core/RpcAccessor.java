package com.zjp.schedule.core;

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
 * Date: 2016/8/9
 * Time: 10:08
 */

public interface RpcAccessor {

    /**
     * 设置ip
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
     * 启动监听
     *
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * 停止监听
     *
     * @throws IOException
     */
    void stop() throws IOException;

    /**
     * 设置请求处理
     *
     * @param processor
     */
    void setProcessor(RpcProcessor processor);
}
