package com.zjp.schedule.discovery;

import com.zjp.schedule.core.*;
import com.zjp.schedule.distribute.DistributeJob;
import com.zjp.schedule.entity.MachineInfo;
import com.zjp.schedule.entity.MetaInfo;
import com.zjp.schedule.entity.Request;
import com.zjp.schedule.transport.NettyRpcConnector;
import org.pinae.rafiki.task.Task;
import org.pinae.rafiki.task.TaskGroup;
import org.pinae.rafiki.trigger.impl.CronTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
 * Date: 2016/8/9
 * Time: 18:08
 */

public class ServiceDiscovery {
    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);

    private static ServiceDiscovery instance;

    private ZkClient zkClient;

    private ServiceRoute route;

    private String zkUrl;

    public ServiceDiscovery(String zkUrl, ServiceRoute route) {
        this.zkUrl = zkUrl;
        this.zkClient = ZkClient.init(zkUrl, Config.ZK_CLIENT_PATH);
        this.route = route;
    }

    /**
     * 服务发现，加载服务并监听服务
     */
    public void discover() {
        zkClient.build(new DataLoaderHandle(),
                new DataMonitorHandle(),
                new ServerMonitorHandle());
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public String getZkUrl() {
        return zkUrl;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }

    /**
     * 加载数据
     */
    private class DataLoaderHandle implements DataLoadHandle {

        /**
         * 装载数据
         *
         * @param metaInfos    元数据集合
         * @param machineInfos 机器数据集合
         */
        public void handle(List<MetaInfo> metaInfos, List<MachineInfo> machineInfos) {
            //装载机器信息
            for (MachineInfo machineInfo : machineInfos) {
                String appName = machineInfo.getAppName();
                DistributeMetaInfo.putMachineInfo(appName, machineInfo);
            }
            //装载数据信息
            for (MetaInfo metaInfo : metaInfos) {
                final String key = new StringBuilder(metaInfo.getAppName())
                        .append(":")
                        .append(metaInfo.getClass())
                        .append(":")
                        .append(metaInfo.getMethodName())
                        .toString();
                Task task = new Task();
                //设置分组
                task.setGroup(new TaskGroup(metaInfo.getAppName()));
                //设置任务名称
                task.setName(key);
                //设置作业
                task.setJob(new DistributeJob(zkUrl,
                        metaInfo.getAppName(),
                        metaInfo.getClassName(),
                        metaInfo.getClassName()) {
                    @Override
                    public boolean call() {
                        /**
                         *任务执行，主要是进行远程调用
                         */
                        rpcRoute(getAppName(), getRequest());
                        return true;
                    }

                    /**
                     * job名称
                     * @return
                     */
                    public String getName() {
                        return key;
                    }
                });
                //设置触发器
                try {
                    task.setTrigger(new CronTrigger(metaInfo.getCron()));
                    DistributeMetaInfo.putTask(key, metaInfo.getAppName(), task);
                } catch (Exception e) {
                    log.error("load data error:{}", e);
                }
            }
        }
    }

    /**
     * 监听数据节点
     */
    private class DataMonitorHandle implements DataHandle {

        /**
         * 监听数据节点变化
         *
         * @param data
         * @param action
         */
        public void handle(Map<String, Object> data, ZKNodeAction action) {
            try {
                Integer type = (Integer) data.get("type");
                if (type == 0) {
                    MetaInfo metaInfo = (MetaInfo) data.get("metaInfo");
                    final String key = new StringBuilder(metaInfo.getAppName())
                            .append(":")
                            .append(metaInfo.getClass())
                            .append(":")
                            .append(metaInfo.getMethodName())
                            .toString();

                    switch (action) {
                        case NODE_ADDED://节点新增
                            Task task = new Task();
                            //设置分组
                            task.setGroup(new TaskGroup(metaInfo.getAppName()));
                            //设置任务名称
                            task.setName(key);
                            //设置作业
                            task.setJob(new DistributeJob(zkUrl,
                                    metaInfo.getAppName(),
                                    metaInfo.getClassName(),
                                    metaInfo.getClassName()) {
                                @Override
                                public boolean call() {
                                    /**
                                     *任务执行，主要是进行远程调用
                                     */
                                    rpcRoute(getAppName(), getRequest());
                                    return true;
                                }

                                /**
                                 * job名称
                                 * @return
                                 */
                                public String getName() {
                                    return key;
                                }
                            });
                            //设置触发器
                            task.setTrigger(new CronTrigger(metaInfo.getCron()));
                            DistributeMetaInfo.putTask(key, metaInfo.getAppName(), task);
                            break;
                        case NODE_REMOVED://节点删除
                            DistributeMetaInfo.removeTask(key);
                            break;
                        case NODE_UPDATED:
                            Task newTask = new Task();
                            //设置分组
                            newTask.setGroup(new TaskGroup(metaInfo.getAppName()));
                            //设置任务名称
                            newTask.setName(key);
                            //设置作业
                            newTask.setJob(new DistributeJob(zkUrl,
                                    metaInfo.getAppName(),
                                    metaInfo.getClassName(),
                                    metaInfo.getClassName()) {
                                /**
                                 * 任务执行，主要是进行远程调用
                                 * @return
                                 */
                                @Override
                                public boolean call() {
                                    rpcRoute(getAppName(), getRequest());
                                    return true;
                                }

                                /**
                                 *job名称
                                 * @return
                                 */
                                public String getName() {
                                    return key;
                                }
                            });
                            //设置触发器
                            newTask.setTrigger(new CronTrigger(metaInfo.getCron()));
                            //更新任务
                            DistributeMetaInfo.update(key, metaInfo.getAppName(), newTask);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("handle zk message error:{}", e);
            }
        }
    }

    /**
     * 将请求路由至指定服务
     *
     * @param appName
     * @param request
     */
    public void rpcRoute(String appName, Request request) {
        //路由到指定机器
        MachineInfo machineInfo = route.select(appName);
        //创建rpc连接
        NettyRpcConnector connector = new NettyRpcConnector();
        connector.setHost(machineInfo.getHost());
        connector.setPort(machineInfo.getPort());
        try {
            //启动连接
            connector.init();
            //rpc远程调用
            connector.invoke(request);
        } catch (IOException e) {
            log.error("rpc invoke error:{}", e);
        } finally {
            try {
                //关闭连接
                connector.stop();
            } catch (IOException e) {
                log.error("stop connector error:{}", e);
            }
        }
    }

    /**
     * 机器节点监听
     */
    private class ServerMonitorHandle implements DataHandle {
        /**
         * 监听server节点变化
         *
         * @param data
         * @param action
         */
        public void handle(Map<String, Object> data, ZKNodeAction action) {
            Integer type = (Integer) data.get("type");
            if (type == 1) {
                MachineInfo machineInfo = (MachineInfo) data.get("machineInfo");
                String appName = machineInfo.getAppName();
                switch (action) {
                    case NODE_ADDED:
                        DistributeMetaInfo.putMachineInfo(appName, machineInfo);
                        break;
                    case NODE_REMOVED:
                        DistributeMetaInfo.removeMachineInfo(appName, machineInfo);
                        break;
                    case NODE_UPDATED:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
