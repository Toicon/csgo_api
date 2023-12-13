package com.csgo.autoconfigure;

import com.csgo.autoconfigure.properties.MqProperties;
import com.csgo.mq.ConsumerFactory;
import com.csgo.mq.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MqProperties.class)
public class MqAutoConfiguration {

    @Autowired
    private MqProperties mqProperties;
    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @ConditionalOnProperty(prefix = "mq.consumer", name = "enable", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerFactory consumerFactory() {
        return new ConsumerFactory(beanFactory, mqProperties);
    }

    @ConditionalOnProperty(prefix = "mq.producer", name = "enable", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerFactory producerFactory() {
        return new ProducerFactory(beanFactory, mqProperties);
    }

}
