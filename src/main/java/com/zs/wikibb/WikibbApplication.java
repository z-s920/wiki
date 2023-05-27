package com.zs.wikibb;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.zs.wikibb.mapper")
//@ComponentScan(("com.zs"),("")) 支持多个包扫描
@SpringBootApplication
//@SpringBootApplication包含@ComponentScan
@EnableScheduling
//异步 同步：sync
@EnableAsync
public class WikibbApplication {

    private static final Logger LOG = LoggerFactory.getLogger(WikibbApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WikibbApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
