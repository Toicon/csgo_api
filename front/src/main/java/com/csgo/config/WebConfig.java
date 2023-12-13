package com.csgo.config;

import com.csgo.web.intecepter.AppInterceptor;
import com.csgo.web.intecepter.LoginInterceptor;
import com.csgo.web.support.SessionIdHeaderProcessor;
import com.csgo.web.support.SiteContext;
import com.echo.framework.platform.App;
import com.echo.framework.platform.interceptor.InterceptorCollector;
import com.echo.framework.platform.interceptor.session.SessionIdProcessor;
import com.echo.framework.platform.interceptor.session.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * @author admin
 */
@Configuration
@EnableAsync
public class WebConfig {

    @Autowired
    private App app;

    @Bean("asyncExecutor")
    public AsyncTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Async-");
        executor.setMaxPoolSize(10);

        // 设置拒绝策略
        executor.setRejectedExecutionHandler((r, executor1) -> {
        });
        return executor;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SiteContext siteContext() {
        return new SiteContext();
    }

    @Bean
    public InterceptorCollector interceptorCollector() {
        InterceptorCollector collector = new InterceptorCollector();
        collector.addInterceptor(loginInterceptor());
        return collector;
    }

    @Bean
    public InterceptorCollector appInterceptorCollector() {
        InterceptorCollector collector = new InterceptorCollector();
        collector.addInterceptor(appInterceptor());
        collector.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return collector;
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public AppInterceptor appInterceptor() {
        return new AppInterceptor();
    }

    @ConditionalOnProperty(name = "portal.app.session.type")
    @Bean("sessionInterceptor")
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor(app.getSession());
    }

    @Bean("sessionIdProcessor")
    public SessionIdProcessor sessionIdProcessor() {
        return new SessionIdHeaderProcessor(app.getSession());
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
