/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/21    Create	
 */
package com.hitrobotgroup.hiiri.swan.express.api.service;

import com.hitrobotgroup.hiiri.swan.express.api.dto.SettingDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.SettingDO;

import java.util.List;

public interface SettingService {

    String create(SettingDTO settingDTO);

    Boolean update(SettingDTO settingDTO);

    Boolean destroy(String settingId);

    Boolean destroyByWeiXinId(String weiXinId);

    Boolean bindWords(String settingId, String[] words);

    SettingDTO getInfoByWeiXinId(String weiXinId);

    SettingDO getInfoDOByWeiXinId(String weiXinId);

    List<SettingDO> findAll();
}
