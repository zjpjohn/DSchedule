package com.zjp.schedule.processor;

import com.zjp.schedule.annotation.Schedule;
import com.zjp.schedule.core.Exporter;
import com.zjp.schedule.core.SpringContextHolder;
import com.zjp.schedule.entity.MachineInfo;
import com.zjp.schedule.entity.MetaInfo;
import com.zjp.schedule.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.InetAddress;

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
 * Module Desc:com.zjp.schedule.processor
 * User: zjprevenge
 * Date: 2016/8/9
 * Time: 23:48
 */

public class ScheduleAnnotationDrivenProcessor implements ApplicationContextAware, BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ScheduleAnnotationDrivenProcessor.class);

    //rpc端口12345
    private static final int port = 12345;

    private String zkUrl;

    private String appName;

    private RpcServer rpcServer;

    public String getZkUrl() {
        return zkUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
        try {
            rpcServer = new RpcServer(getHostIp(), port, new Exporter() {
                public Object findService(String className) {
                    return SpringContextHolder.getBean(className);
                }
            });
            rpcServer.setZkServer(zkUrl);
        } catch (Exception e) {
            log.error("init rpcServer error:{}", e);
            throw new RuntimeException("init rpcServer error", e);
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            processSchedule(bean);
        } catch (Exception e) {
            log.error("process schedule error:{}", e);
        }
        return bean;
    }

    /**
     * 对所有标注@Schedule注解的bean进行解析并注册到zookeeper
     *
     * @param bean
     */
    public void processSchedule(Object bean) throws Exception {
        Component component = bean.getClass().getAnnotation(Component.class);
        if (component != null) {
            String className = component.value();
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Schedule annotation = method.getAnnotation(Schedule.class);
                if (annotation != null) {
                    String methodName = method.getName();
                    String jobName = annotation.name();
                    String cronExpression = annotation.cron();
                    MetaInfo metaInfo = new MetaInfo();
                    metaInfo.setAppName(appName);
                    metaInfo.setClassName(className);
                    metaInfo.setMethodName(methodName);
                    metaInfo.setJob(jobName);
                    metaInfo.setCron(cronExpression);
                    MachineInfo machineInfo = new MachineInfo();
                    machineInfo.setAppName(appName);
                    machineInfo.setHost(getHostIp());
                    machineInfo.setPort(port);
                    //将任务注册至zookeeper中
                    rpcServer.registry(metaInfo, machineInfo);
                }
            }
        }
    }

    /**
     * 获取主机地址
     *
     * @return
     * @throws Exception
     */
    private String getHostIp() throws Exception {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.setApplicationContext(applicationContext);
    }
}
