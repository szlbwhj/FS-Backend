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

import com.hitrobotgroup.hiiri.swan.express.api.dto.ExpressDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;

import java.util.List;

public interface ExpressService {

    String URL_PROXY_DOMAIN = "https://www.shangyuekeji.com/v1/api/express/proxy";
//    String URL_PROXY_DOMAIN = "http://localhost:8099/v1/api/express/proxy";
    List<ExpressDO> merge(Long wordId, List<ExpressDO> dtoList);

    Boolean destroyByWordId(Long wordId);

    Boolean destroyByWordIds(List<Long> wordIds);

    List<ExpressDO> getListByWordId(Long wordId);

    List<ExpressDO> queryNewsOnline(String word);
}
