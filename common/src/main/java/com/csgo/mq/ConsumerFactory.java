package com.csgo.mq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.aliyun.openservices.ons.api.bean.BatchConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.csgo.autoconfigure.properties.MqProperties;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author admin
 */
public class ConsumerFactory {

    private final DefaultListableBeanFactory beanFactory;

    public ConsumerFactory(DefaultListableBeanFactory beanFactory, MqProperties mqProperties) {
        this.beanFactory = beanFactory;
        Map<String, BatchMessageListener> beanMap = beanFactory.getBeansOfType(BatchMessageListener.class);
        Map<String, List<BatchMessageListener>> groupMap = new HashMap<>();
        for (Map.Entry<String, BatchMessageListener> entry : beanMap.entrySet()) {
            BatchMessageListener listener = entry.getValue();
            String group = listener.getClass().getAnnotation(Group.class).value();
            if (!groupMap.containsKey(group)) {
                groupMap.put(group, new ArrayList<>());
            }
            groupMap.get(group).add(listener);
        }
        for (Map.Entry<String, List<BatchMessageListener>> entry : groupMap.entrySet()) {
            BatchConsumerBean batchConsumerBean = new BatchConsumerBean();
            Properties properties = mqProperties.createProperties();
            properties.setProperty(PropertyKeyConst.GROUP_ID, entry.getKey());
            //将消费者线程数固定为20个 20为默认值
            properties.setProperty(PropertyKeyConst.ConsumeThreadNums, "20");
            batchConsumerBean.setProperties(properties);

            Map<Subscription, BatchMessageListener> subscriptionTable = new HashMap<>();
            for (BatchMessageListener listener : entry.getValue()) {
                Topic topic = listener.getClass().getAnnotation(Topic.class);
                Subscription subscription = new Subscription();
                subscription.setTopic(topic.value());
                subscriptionTable.put(subscription, listener);
            }
            batchConsumerBean.setSubscriptionTable(subscriptionTable);
            beanFactory.registerSingleton(entry.getKey() + "BatchConsumerBean", batchConsumerBean);
        }
    }

    public void start() {
        for (BatchConsumerBean batchConsumerBean : beanFactory.getBeansOfType(BatchConsumerBean.class).values()) {
            batchConsumerBean.start();
        }
    }

    public void shutdown() {
        for (BatchConsumerBean batchConsumerBean : beanFactory.getBeansOfType(BatchConsumerBean.class).values()) {
            batchConsumerBean.shutdown();
        }
    }
}
