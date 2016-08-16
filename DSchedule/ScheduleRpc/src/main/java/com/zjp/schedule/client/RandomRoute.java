package com.zjp.schedule.client;

import com.zjp.schedule.core.ServiceRoute;
import com.zjp.schedule.discovery.DistributeMetaInfo;
import com.zjp.schedule.entity.MachineInfo;

import java.util.List;

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
 * Module Desc:com.zjp.schedule.client
 * User: zjprevenge
 * Date: 2016/8/10
 * Time: 19:00
 */

public class RandomRoute implements ServiceRoute {

    /**
     * 选取指定的机器执行任务
     *
     * @param appName
     * @return
     */
    public MachineInfo select(String appName) {
        List<MachineInfo> machineInfos = DistributeMetaInfo.getMachineInfo(appName);
        int k = (int) (Math.random() * machineInfos.size());
        return machineInfos.get(k);
    }
}
