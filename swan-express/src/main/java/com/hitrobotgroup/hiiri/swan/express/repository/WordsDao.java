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

import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordsDao extends JpaRepository<WordsDO, Long> {
    Optional<WordsDO> findByText(String text);

    List<WordsDO> findByTextIn(Collection<String> texts);

    List<WordsDO> findByIdIn(Collection<Long> ids);
}
