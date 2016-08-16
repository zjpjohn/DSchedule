# DSchedule
基于zookeeper、rpc的分布式调度系统


在分布式系统中，如何解决多台机上部署的同一个定时任务在同一个时间点上只执行一次。
这就需要将单机的定时任务扩展至多机。
当前已知的实现方案有多种：
 1.将定时任务进行集中管理，在指定的机器上执行，如生成定时任务执行的jar上传至指定机器进行执行
 2.定时任务还是分由多台机器执行，只是通过调度中心控制定时任务执行。

本系统采用第二种方式，本系统的设计要点如下：
  
  1.利用zookeeper进行定时任务的注册与监听，
 
  2.通过rpc来控制各个要执行的任务
  
  3.调度中心根据zookeeper注册的任务，统一在调度中心生成定时任务，调度中心根据定时任务时间表达式，
 从该任务可执行的机器列表中选择一台机器进行执行

使用方式：
 
1. 引入 spring namespace:
     
xmlns:config="http://www.springframework.org/schema/schedule"

2. 添加schema location:
    
http://www.springframework.org/schema/schedule
     http://www.springframework.org/schema/schedule/spring-schedule-0.0.1.xsd

3. spring xml配置

<!-- 开启@Schedule注解,并注册至zookeeper中-->
<schedule:annotation-driven zkUrl="ip:port" appName="xxxx" />

4.  在使用类中进行注解配置


@Component("DemoSchedule")

public class DemoSchedule{


   @Schedule(name="",cron="")
   public void schedule(){

   }
}
