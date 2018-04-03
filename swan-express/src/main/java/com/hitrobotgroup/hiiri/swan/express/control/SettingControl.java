/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/27    Create	
 */
package com.hitrobotgroup.hiiri.swan.express.control;

import com.hitrobotgroup.hiiri.swan.express.api.dto.SettingDTO;
import com.hitrobotgroup.hiiri.swan.express.pojo.JsonResult;
import com.hitrobotgroup.hiiri.swan.express.service.PushNewsServiceImpl;
import com.hitrobotgroup.hiiri.swan.express.service.SettingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/v1/api/setting")
public class SettingControl {
    @Autowired
    SettingServiceImpl settingService;
    @Autowired
    PushNewsServiceImpl pushNewsService;

    @RequestMapping(value = "update", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult update(@RequestParam("words") String[] words, @RequestParam("email") String email,
                      @RequestParam("weixin") String weixinId, Date ptime) {
        try {
            SettingDTO dto = settingService.getInfoByWeiXinId(weixinId);
            if (dto == null) {
                dto = SettingDTO.builder().email(email).weiXinId(weixinId).words(words).build();
                settingService.create(dto);
            } else {
                dto.setWords(words).setEmail(email);
                settingService.update(dto);
            }
            return new JsonResult(true, null);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }


    @RequestMapping(value = "destroy", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult destroy(@RequestParam("weixin") String weiXinId) {
        try {
            settingService.destroyByWeiXinId(weiXinId);
            return new JsonResult(true, null);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }

    @RequestMapping(value = "getInfo", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult getInfo(@RequestParam("weixin") String weiXinId) {
        try {
            SettingDTO dto = settingService.getInfoByWeiXinId(weiXinId);
            return new JsonResult(true, dto);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }

    @RequestMapping(value = "mailLatestNews", method = RequestMethod.POST)
    JsonResult mailNewestNews(@RequestParam("weixin") String weiXinId) {
        try {
            pushNewsService.genNews4Mail(weiXinId);
            return new JsonResult(true, null);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }

    @RequestMapping(value = "triggerPushNewsAll", method = RequestMethod.POST)
    JsonResult triggerPushNewsAll() {
        try {
            pushNewsService.triggerPush();
            return new JsonResult(true, null);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }
}
