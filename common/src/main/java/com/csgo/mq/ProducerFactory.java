package com.csgo.mq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.csgo.autoconfigure.properties.MqProperties;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
public class ProducerFactory {

    private final DefaultListableBeanFactory beanFactory;
    private final ThreadPoolExecutor callbackExecutor;

    public ProducerFactory(DefaultListableBeanFactory beanFactory, MqProperties mqProperties) {
        this.beanFactory = beanFactory;
        for (ProducerBean producer : beanFactory.getBeansOfType(ProducerBean.class).values()) {
            Properties properties = mqProperties.createProperties();
            properties.setProperty(PropertyKeyConst.GROUP_ID, producer.getClass().getAnnotation(Group.class).value());
            properties.setProperty(PropertyKeyConst.ConsumeThreadNums, "50");
            producer.setProperties(properties);
        }
        this.callbackExecutor = new ThreadPoolExecutor(
                2,
                5,
                1L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void start() {
        for (ProducerBean producer : beanFactory.getBeansOfType(ProducerBean.class).values()) {
            producer.start();
            producer.setCallbackExecutor(callbackExecutor);
        }
    }

    public void shutdown() {
        for (ProducerBean producerBean : beanFactory.getBeansOfType(ProducerBean.class).values()) {
            producerBean.shutdown();
        }
    }
}
