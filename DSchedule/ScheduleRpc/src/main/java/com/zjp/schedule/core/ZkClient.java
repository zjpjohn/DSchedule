package com.zjp.schedule.core;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.PathChildrenCache;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheEvent;
import com.netflix.curator.framework.recipes.cache.PathChildrenCacheListener;
import com.netflix.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import com.netflix.curator.retry.RetryNTimes;
import com.zjp.schedule.entity.MachineInfo;
import com.zjp.schedule.entity.MetaInfo;
import com.zjp.schedule.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * Time: 18:11
 */

public class ZkClient {
    private static final Logger log = LoggerFactory.getLogger(ZkClient.class);
    private static final int ZK_RETRY_TIMES = Integer.MAX_VALUE;
    private static final int ZK_RETRY_WAIT_TIME_MS = 5000;
    private CuratorFramework curator;
    private PathChildrenCache dataNodeCache;
    private PathChildrenCache serverNodeCache;

    private String dataPath;
    private String serverPath;
    private static ZkClient zkClient;
    private ExecutorService pool;

    private String zkUrl;

    private ZkClient(String zkUrl, String namespace) {
        this.zkUrl = zkUrl;
        this.curator = CuratorFrameworkFactory
                .builder()
                .connectString(zkUrl)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(2000)
                .canBeReadOnly(false)
                .namespace(namespace)
                .retryPolicy(new RetryNTimes(ZK_RETRY_TIMES, ZK_RETRY_WAIT_TIME_MS))
                .build();
        curator.start();

    }

    public synchronized static ZkClient init(String zkUrl, String namespace) {
        if (zkClient == null) {
            zkClient = new ZkClient(zkUrl, namespace);
        }
        return zkClient;
    }

    public ZkClient build(DataLoadHandle dataLoadHandle, DataHandle dataHandle, DataHandle serverHandle) {
        dataPath = Config.ZK_REGISTRY_DATA_PATH;
        serverPath = Config.ZK_REGISTRY_SERVER_PATH;
        dataNodeCache = new PathChildrenCache(curator, dataPath, true);
        serverNodeCache = new PathChildrenCache(curator, serverPath, true);

        pool = Executors.newFixedThreadPool(2);
        //将zk数据装载到内存中
        loadData(dataLoadHandle);

        try {
            //监听数据节点
            addWatcher(dataNodeCache, dataHandle, 0);
            //监听server节点
            addWatcher(serverNodeCache, serverHandle, 1);
        } catch (Exception e) {
            log.error("watcher the node error:{}", e);
        }
        return this;
    }

    /**
     * 进行初始化数据装载
     *
     * @param dataLoadHandle
     */
    public void loadData(DataLoadHandle dataLoadHandle) {
        try {
            List<MetaInfo> metaInfos = Lists.newArrayList();
            List<MachineInfo> machineInfos = Lists.newArrayList();
            //获取元数据信息
            List<String> list = curator.getChildren().forPath(Config.ZK_REGISTRY_DATA_PATH);
            for (String path : list) {
                byte[] data = curator.getData().forPath(path);
                MetaInfo metaInfo = JsonUtils.parseJson(MetaInfo.class, new String(data, Charsets.UTF_8));
                metaInfos.add(metaInfo);
            }
            //获取机器信息
            List<String> machinePaths = curator.getChildren().forPath(Config.ZK_REGISTRY_SERVER_PATH);
            for (String path : machinePaths) {
                byte[] data = curator.getData().forPath(path);
                MachineInfo machineInfo = JsonUtils.parseJson(MachineInfo.class, new String(data, Charsets.UTF_8));
                machineInfos.add(machineInfo);
            }
            dataLoadHandle.handle(metaInfos, machineInfos);
        } catch (Exception e) {
            log.error("load data error:{}", e);
        }

    }

    /**
     * 监听节点数据变化进行处理
     *
     * @param nodeCache
     * @param dataHandle
     * @param type
     */
    public void addWatcher(PathChildrenCache nodeCache, final DataHandle dataHandle, final int type) throws Exception {
        nodeCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework,
                                   PathChildrenCacheEvent event) throws Exception {
                String data = new String(event.getData().getData(), Charsets.UTF_8);
                Map<String, Object> map = Maps.newHashMap();
                if (type == 0) {
                    map.put("metaInfo", JsonUtils.parseJson(MetaInfo.class, data));
                    map.put("type", 0);
                } else if (type == 1) {
                    map.put("machineInfo", JsonUtils.parseJson(MachineInfo.class, data));
                    map.put("type", 1);
                }
                switch (event.getType()) {
                    case CHILD_ADDED:
                        dataHandle.handle(map, ZKNodeAction.NODE_ADDED);
                        break;
                    case CHILD_UPDATED:
                        dataHandle.handle(map, ZKNodeAction.NODE_UPDATED);
                        break;
                    case CHILD_REMOVED:
                        dataHandle.handle(map, ZKNodeAction.NODE_REMOVED);
                        break;
                    default:
                        break;
                }
            }
        }, pool);
        dataNodeCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    /**
     * 获取路径下的数据
     *
     * @param path 路径
     * @return
     */
    public String getData(String path) {
        byte[] data = null;
        try {
            data = curator.getData().forPath(path);
        } catch (Exception e) {
            log.error("get data from zk error:{}", e);
        }
        return data == null ? null : new String(data, Charsets.UTF_8);
    }

    /**
     * 注册服务值zookeeper
     *
     * @param metaInfo
     * @param machineInfo
     */
    public void registerServer(MetaInfo metaInfo, MachineInfo machineInfo) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(metaInfo.getAppName()), "appName must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(metaInfo.getClassName()), "className must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(metaInfo.getMethodName()), "methodName must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(metaInfo.getJob()), "job must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(metaInfo.getCron()), "cron must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(machineInfo.getAppName())
                && StringUtils.isNotBlank(machineInfo.getHost())
                && StringUtils.isNotBlank(machineInfo.getPort() + ""), "node must not be empty...");
        createServer(metaInfo, machineInfo);
    }

    /**
     * 注册客户端机器信息
     *
     * @param className  类名
     * @param methodName 方法名
     * @param node       节点机器信息
     */
    public void registerClient(String appName, String className, String methodName, String node) {
        Preconditions.checkArgument(StringUtils.isNotBlank(appName), "appName must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(className), "className must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(methodName), "methodName must not be empty...");
        Preconditions.checkArgument(StringUtils.isNotBlank(node), "node must not be empty...");
        createClient(appName, className, methodName, node);
    }

    /**
     * 创建客户端连接信息
     *
     * @param className  类名
     * @param methodName 方法名
     * @param node       机器信息
     */
    public void createClient(String appName, String className, String methodName, String node) {

        String path = Config.ZK_CLIENT_PATH + "/" + appName + ":" + className + ":" + methodName;
        //存储元数据
        String metaPath = path + "/meta";
        createNode(metaPath, appName + ":" + className + ":" + metaPath, CreateMode.PERSISTENT);
        //存储机器信息
        String machinePath = path + "/machine/" + node;
        createNode(machinePath, node, CreateMode.EPHEMERAL);
        //存储所数据信息
        String lockPath = path + "/lock";
        createNode(lockPath, null, CreateMode.PERSISTENT);
    }

    /**
     * 注册server 信息
     *
     * @param metaInfo
     * @param machineInfo
     */
    public void createServer(MetaInfo metaInfo, MachineInfo machineInfo) throws Exception {
        String metaPath = new StringBuilder(Config.ZK_REGISTRY_DATA_PATH)
                .append("/")
                .append(metaInfo.getAppName())
                .append(":")
                .append(metaInfo.getClassName())
                .append(":")
                .append(metaInfo.getMethodName())
                .toString();
        //存放metaInfo信息
        createNode(metaPath, JsonUtils.toJson(metaInfo), CreateMode.PERSISTENT);
        String machinePath = new StringBuilder(Config.ZK_REGISTRY_SERVER_PATH)
                .append("/")
                .append(machineInfo.getAppName())
                .append(":")
                .append(machineInfo.getHost())
                .append(":")
                .append(machineInfo.getPort())
                .toString();
        //存放机器节点信息
        createNode(machinePath, JsonUtils.toJson(machineInfo), CreateMode.EPHEMERAL);
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       数据
     * @param createMode 模式
     */
    private void createNode(String path, String data, CreateMode createMode) {
        try {
            Stat stat = curator.checkExists().forPath(path);
            if (stat == null) {
                curator.create()
                        .creatingParentsIfNeeded()
                        .withMode(createMode)
                        .forPath(path, data.getBytes());
            }
        } catch (Exception e) {
            log.error("create node error:{}", e);
        }
    }

    /**
     * 获取共享信号量
     *
     * @param path
     * @param count
     * @return
     */
    public InterProcessSemaphoreV2 getLock(String path, int count) {
        return new InterProcessSemaphoreV2(curator, path, count);
    }
}
