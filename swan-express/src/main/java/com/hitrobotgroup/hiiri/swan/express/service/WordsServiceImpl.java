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
package com.hitrobotgroup.hiiri.swan.express.service;

import com.google.common.collect.Lists;
import com.hitrobotgroup.hiiri.swan.express.api.dto.WordsDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;
import com.hitrobotgroup.hiiri.swan.express.api.service.WordsService;
import com.hitrobotgroup.hiiri.swan.express.repository.WordsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("wordsService")
public class WordsServiceImpl implements WordsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WordsDao wordsDao;

    @Override
    public Long create(WordsDTO dto) {
        Optional<WordsDO> doOptional = wordsDao.findByText(dto.getText());
        if (doOptional.isPresent())
            return doOptional.get().getId();
        WordsDO wordsDO = WordsDO.builder().build();
        BeanUtils.copyProperties(dto, wordsDO);
        wordsDao.save(wordsDO);
        return wordsDO.getId();
    }

    @Override
    public Boolean deleteBatch(Collection<Long> wordIds) {
        List<WordsDO> wordsDOList = wordsDao.findByIdIn(wordIds);
        wordsDOList.removeIf(v -> (v.getRefCount() - 1) > 0 ? true : false);
        wordsDao.deleteInBatch(wordsDOList);
        return true;
    }

    @Override
    public List<WordsDO> findByWordIds(Collection<Long> wordIds) {
        List<WordsDO> wordsDOList = wordsDao.findByIdIn(wordIds);
        return wordsDOList;
    }

    @Override
    public List<WordsDO> findByWordsText(String[] words) {
        ArrayList<String> texts = Lists.newArrayList(words);
        List<WordsDO> wordsDOList = wordsDao.findByTextIn(texts);
        return wordsDOList;
    }

    @Override
    public WordsDO findByText(String word) {
        Optional<WordsDO> wordsDO = wordsDao.findByText(word);
        return wordsDO.orElse(null);
    }

    @Override
    public List<WordsDO> findAll() {
        return wordsDao.findAll();
    }

    @Override
    public Boolean update(WordsDTO dto) {
        if (dto == null)
            throw new RuntimeException("WordsDTO IS NULL");
        WordsDO wordsDO = WordsDO.builder().build();
        BeanUtils.copyProperties(dto, wordsDO);
        wordsDao.save(wordsDO);
        return true;
    }

    @Override
    public List<Long> updateBatch(Iterable<WordsDTO> dtos) {
        List<Long> idList = Lists.newArrayList();
        dtos.forEach(v -> {
            WordsDO wordsDO = WordsDO.builder().build();
            BeanUtils.copyProperties(v, wordsDO);
            wordsDao.save(wordsDO);
            idList.add(wordsDO.getId());
        });
        return idList;
    }
}
