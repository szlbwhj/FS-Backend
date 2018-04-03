/*
 *
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：RobotCloud
 * 
 * Description：
 * 
 * History：
 * Version    Author            Date              Operation 
 * 1.0	      xuzs         2017/4/21 11:45	        Create	
 */
package com.hitrobotgroup.hiiri.swan.express.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * 实体与库配置类
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.**.repository"})
@EntityScan(basePackages = "com.**.entity")
public class JpaConfiguration {

    /**
     * 事务异常
     *
     * @return 返回
     */
    @Bean
    PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
