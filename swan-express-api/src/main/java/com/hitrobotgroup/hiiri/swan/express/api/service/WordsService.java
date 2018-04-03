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

import com.hitrobotgroup.hiiri.swan.express.api.dto.WordsDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;

import java.util.Collection;
import java.util.List;

public interface WordsService {

    Long create(WordsDTO dto);

    List<WordsDO> findByWordIds(Collection<Long> wordIds);

    /**
     * 当word没有被引用则删除
     *
     * @param wordIds
     * @return
     */
    Boolean deleteBatch(Collection<Long> wordIds);

    List<WordsDO> findByWordsText(String[] words);

    WordsDO findByText(String word);

    List<WordsDO> findAll();

    Boolean update(WordsDTO dto);

    List<Long> updateBatch(Iterable<WordsDTO> dtos);
}
