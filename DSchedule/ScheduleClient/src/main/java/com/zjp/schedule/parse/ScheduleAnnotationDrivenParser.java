package com.zjp.schedule.parse;

import com.zjp.schedule.processor.ScheduleAnnotationDrivenProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

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
 * Module Desc:com.zjp.schedule.parse
 * User: zjprevenge
 * Date: 2016/8/9
 * Time: 23:28
 */

public class ScheduleAnnotationDrivenParser extends AbstractSingleBeanDefinitionParser {

    private static final String ZK_RUL = "zkUrl";

    private static final String APP_NAME = "appName";

    /**
     * 获取bean的类型
     *
     * @param element
     * @return
     */
    @Override
    protected Class<?> getBeanClass(Element element) {
        return ScheduleAnnotationDrivenParser.class;
    }

    /**
     * 获取bean的类名
     *
     * @param element
     * @return
     */
    @Override
    protected String getBeanClassName(Element element) {
        return ScheduleAnnotationDrivenProcessor.class.getName();
    }

    /**
     * 从xml中解析bean
     *
     * @param element
     * @param builder
     */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String zkUrl = element.getAttribute(ZK_RUL);
        String appName = element.getAttribute(APP_NAME);

        if (StringUtils.isBlank(zkUrl)) {
            throw new RuntimeException(String.format("element[%s],attribute[%s] required", ZK_RUL, zkUrl));
        }
        if (StringUtils.isBlank(appName)) {
            throw new RuntimeException(String.format("element[%s],attribute[%s] required", APP_NAME, appName));
        }
        //将参数传递给processor
        builder.addPropertyValue(ZK_RUL, zkUrl);
        builder.addPropertyValue(APP_NAME, appName);
    }


    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
}
