/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/29    Create	
 */
package com.hitrobotgroup.hiiri.swan.express.service;

import com.google.common.collect.Maps;
import com.hitrobotgroup.hiiri.swan.express.api.dto.EmailDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.SettingDO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;
import com.hitrobotgroup.hiiri.swan.express.api.service.PushNewsService;
import com.hitrobotgroup.hiiri.swan.express.service.mail.MailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class PushNewsServiceImpl implements PushNewsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Long PERIOD_MILLISECOND = 20000L;
    @Autowired
    ExpressServiceImpl expressService;
    @Autowired
    SettingServiceImpl settingService;
    @Autowired
    WordsServiceImpl wordsService;
    @Autowired
    MailServiceImpl mailService;

    @Scheduled(cron = "0 0 6,14 * * ?")
    private void push() {
        logger.info("task push news  start...");
        List<SettingDO> settingDOList = settingService.findAll();
        settingDOList.forEach(item -> {
            genNews4Mail(item);
        });
    }

    public void triggerPush() {
        this.push();
    }

    @Override
    public void genNews4Mail(String weiXinId) {
        SettingDO item = settingService.getInfoDOByWeiXinId(weiXinId);
        if (item == null)
            throw new RuntimeException("WeXinId setting 不存在");
        genNews4Mail(item);
    }

    private void genNews4Mail(SettingDO item) {
        Long[] wordIds = Arrays.stream(item.getWordIds().split(","))
                .map(v -> Long.valueOf(v)).toArray(Long[]::new);
        List<WordsDO> wordsDOList = wordsService.findByWordIds(Arrays.asList(wordIds));
        Map<String, List<ExpressDO>> wordExpress = Maps.newHashMap();
        wordsDOList.forEach(w -> {
            boolean mustRefresh = (w.getRTime() == null ? true :
                    w.getRTime().before(new Date(System.currentTimeMillis() + PERIOD_MILLISECOND)));
            if (mustRefresh) {
                List<ExpressDO> expressDOList = expressService.queryNewsOnline(w.getText());
                List<ExpressDO> mergedList = expressService.merge(w.getId(), expressDOList);
                wordExpress.putIfAbsent(w.getText(), mergedList);
            } else {
                List<ExpressDO> dbList = expressService.getListByWordId(w.getId());
                wordExpress.putIfAbsent(w.getText(), dbList);
            }
        });
        EmailDTO mail = new EmailDTO();
        mail.setEmail(new String[]{item.getEmail()});
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mail.setSubject("快讯 " + sdf.format(d));
        mail.setContent(wordExpress);
        mail.setTemplate("news");
        try {
//            mailService.sendThymeleaf(mail);
            mailService.sendQueue(mail);
//            mailService.sendRedisQueue(mail);
        } catch (Exception e) {
            logger.error("投递失败[" + item.getEmail() + "]: " + e.getMessage());
        }
    }
}
