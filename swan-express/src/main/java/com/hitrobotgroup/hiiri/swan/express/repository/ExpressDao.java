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

import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpressDao extends JpaRepository<ExpressDO, Long> {
    void deleteByWordId(Long wordId);

    void deleteByWordIdIn(List<Long> wordIds);

    List<ExpressDO> findByWordId(Long wordId);
}
