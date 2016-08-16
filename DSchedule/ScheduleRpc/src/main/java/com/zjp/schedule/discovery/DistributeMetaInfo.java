package com.zjp.schedule.discovery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zjp.schedule.entity.MachineInfo;
import org.pinae.rafiki.task.Task;
import org.pinae.rafiki.task.TaskContainer;

import java.util.List;
import java.util.Map;

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
 * Module Desc:com.zjp.schedule.discovery
 * User: zjprevenge
 * Date: 2016/8/11
 * Time: 1:41
 */

public class DistributeMetaInfo {

    private static ArrayListMultimap<String, MachineInfo> machineInfos = ArrayListMultimap.create();

    //任务列表
    private static Map<String, Task> taskMap = Maps.newHashMap();

    //任务容器
    private static TaskContainer container = new TaskContainer();


    /**
     * 存储机器信息
     *
     * @param appName
     * @param machineInfo
     */
    public static synchronized void putMachineInfo(String appName, MachineInfo machineInfo) {
        machineInfos.put(appName, machineInfo);
    }

    /**
     * 获取指定应用的机器列表
     *
     * @param appName
     * @return
     */
    public static List<MachineInfo> getMachineInfo(String appName) {
        return machineInfos.get(appName);
    }


    /**
     * 删除指定的机器
     *
     * @param appName
     * @param machineInfo
     */
    public static synchronized void removeMachineInfo(String appName, MachineInfo machineInfo) {
        machineInfos.remove(appName, machineInfo);
    }

    /**
     * 添加指定任务
     *
     * @param key
     * @param task
     */
    public static synchronized void putTask(String key, String group, Task task) throws Exception {
        taskMap.put(key, task);
        addTask(task, group);
    }

    /**
     * 更新任务
     *
     * @param key
     * @param appName
     * @param task
     * @throws Exception
     */
    public static synchronized void update(String key, String appName, Task task) throws Exception {
        taskMap.put(key, task);
        updateTask(key, appName, task);
    }

    /**
     * 删除指定任务的列表
     *
     * @param key
     */
    public static synchronized void remove(String key) throws Exception {
        removeTask(key);
        taskMap.remove(key);
    }

    /**
     * 停止任务
     *
     * @param name 任务名
     * @throws Exception
     */
    public static void pauseTask(String name) throws Exception {
        container.pauseTask(name);
    }

    /**
     * 停止任务
     *
     * @param name  任务名称
     * @param group 任务分组
     * @throws Exception
     */
    public static void pauseTask(String name, String group) throws Exception {
        container.pauseTask(name, group);
    }

    /**
     * 从容器中删除任务
     *
     * @param name 任务名
     */
    public static void removeTask(String name) throws Exception {
        container.removeTask(name);
    }

    /**
     * 从容器中删除任务
     *
     * @param name  任务名
     * @param group 分组
     * @throws Exception
     */
    public static void removeTask(String name, String group) throws Exception {
        container.removeTask(name, group);
    }

    /**
     * 添加任务至容器
     *
     * @param task  任务
     * @param group 分组
     * @throws Exception
     */
    public static void addTask(Task task, String group) throws Exception {
        container.addTask(task, group);
    }

    /**
     * 更新指定任务
     * 先删除，再添加
     *
     * @param key
     * @param group
     * @param task
     * @throws Exception
     */
    public static void updateTask(String key, String group, Task task) throws Exception {
        container.removeTask(key, group);
        container.addTask(task, group);
    }

    /**
     * 根据任务名查询任务
     *
     * @param name
     * @return
     */
    public static Task queryTask(String name) {
        return taskMap.get(name);
    }

    /**
     * 根据任务分组查询组内任务
     *
     * @param group
     * @return
     */
    public static List<Task> queryTaskByGroup(String group) {
        List<Task> tasks = Lists.newArrayList();
        for (Task task : taskMap.values()) {
            if (task.getGroup().getName().equals(group)) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public static void removeAllTask(String group) throws Exception {
        container.removeGroup(group);
    }

    /**
     * 启动所有任务
     *
     * @throws Exception
     */
    public void start() throws Exception {
        container.start();
    }

    /**
     * 启动指定的任务
     *
     * @param name
     * @throws Exception
     */
    public void start(String name) throws Exception {
        container.startTask(name);
    }

    /**
     * 启动指定分组下的任务
     *
     * @param name
     * @param group
     * @throws Exception
     */
    public void start(String name, String group) throws Exception {
        container.startTask(name, group);
    }
}
