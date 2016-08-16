package com.zjp.schedule.registry;

import com.zjp.schedule.core.ZkClient;
import com.zjp.schedule.entity.MachineInfo;
import com.zjp.schedule.entity.MetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ©¥©¥©¥©¥©¥©¥ÄÏÎÞ°¢ÃÖÍÓ·ð©¥©¥©¥©¥©¥©¥
 * ¡¡¡¡¡¡©³©·¡¡¡¡¡¡©³©·
 * ¡¡¡¡©³©¿©ß©¥©¥©¥©¿©ß©·
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©¥¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡©×©¿¡¡©»©×¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©ß¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©»©¥©·¡¡¡¡¡¡©³©¥©¿
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§stay hungry stay foolish
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§Code is far away from bug with the animal protecting
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©»©¥©¥©¥©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©Ç©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©³©¿
 * ¡¡¡¡¡¡¡¡©»©·©·©³©¥©×©·©³©¿
 * ¡¡¡¡¡¡¡¡¡¡©§©Ï©Ï¡¡©§©Ï©Ï
 * ¡¡¡¡¡¡¡¡¡¡©»©ß©¿¡¡©»©ß©¿
 * ©¥©¥©¥©¥©¥©¥ÃÈÃÈßÕ©¥©¥©¥©¥©¥©¥
 * Module Desc:com.zjp.schedule.registry
 * User: zjprevenge
 * Date: 2016/8/9
 * Time: 12:39
 * zookeeper×¢²áÖÐÐÄ
 */

public class ServerRegistry {
    private static final Logger log = LoggerFactory.getLogger(ServerRegistry.class);

    private ZkClient zkClient;

    private static ServerRegistry instance;

    public static ServerRegistry getInstance(String zkAddress) {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new ServerRegistry(zkAddress);
                }
            }
        }
        return instance;
    }

    private ServerRegistry() {
    }

    private ServerRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
        this.zkClient = ZkClient.init(zkAddress, null);
    }


    private String zkAddress;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }


    /**
     * ×¢²á·þÎñ
     *
     * @param metaInfo
     * @param machineInfo
     */
    public void registerServer(MetaInfo metaInfo, MachineInfo machineInfo) throws Exception {
        zkClient.registerServer(metaInfo, machineInfo);
    }

}
