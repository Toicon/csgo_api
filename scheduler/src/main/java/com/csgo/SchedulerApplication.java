package com.csgo;

import com.csgo.framework.util.AppUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.echo.framework.database.mybatis.EnableMyBatis;

/**
 * @author admin
 */
@SpringBootApplication
@EnableMyBatis(basePackages = {"com.csgo.mapper", "com.csgo.modular.*.mapper"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
@EnableRedisHttpSession
@EnableCaching
public class SchedulerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SchedulerApplication.class);
        ConfigurableApplicationContext context = application.run(args);

        AppUtil.logApplicationStartup(context.getEnvironment());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SchedulerApplication.class);
    }
}
