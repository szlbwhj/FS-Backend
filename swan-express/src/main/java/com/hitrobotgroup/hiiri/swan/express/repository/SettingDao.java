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
package com.hitrobotgroup.hiiri.swan.express.repository;

import com.hitrobotgroup.hiiri.swan.express.api.entity.SettingDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SettingDao extends JpaRepository<SettingDO, String> {

    Optional<SettingDO> findByWeiXinId(String weiXinId);

    @Transactional
    void deleteByWeiXinId(String weiXinId);
}
