package com.csgo.framework.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.csgo.framework.model.HealthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author admin
 */
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class HeartbeatController implements ApplicationListener<ApplicationReadyEvent> {

    private static HealthVO health = null;

    @GetMapping("/heartbeat")
    public R<String> heartbeat() {
        if (health != null) {
            return R.ok(String.format("health, time:%s port:%s", health.getRunDateTime(), health.getPort()));
        }
        return R.ok("health");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        String port = context.getEnvironment().getProperty("server.port", "8080");

        HealthVO vo = new HealthVO();
        vo.setRunDateTime(DateUtil.now());
        vo.setPort(port);
        health = vo;
    }

}
