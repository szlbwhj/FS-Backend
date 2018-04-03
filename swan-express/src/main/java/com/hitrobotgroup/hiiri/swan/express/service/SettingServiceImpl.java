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
import com.google.common.primitives.Longs;
import com.hitrobotgroup.hiiri.swan.express.api.dto.SettingDTO;
import com.hitrobotgroup.hiiri.swan.express.api.dto.WordsDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.SettingDO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;
import com.hitrobotgroup.hiiri.swan.express.api.service.SettingService;
import com.hitrobotgroup.hiiri.swan.express.repository.SettingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("settingService")
public class SettingServiceImpl implements SettingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SettingDao settingDao;
    @Autowired
    WordsServiceImpl wordsService;
    @Autowired
    ExpressServiceImpl expressService;

    @Override
    public String create(SettingDTO settingDTO) {
        SettingDO entity = SettingDO.builder().cTime(new Date()).build();
        BeanUtils.copyProperties(settingDTO, entity);
        settingDao.save(entity);
        bindWords(entity.getId(), settingDTO.getWords());
        return entity.getId();
    }

    @Override
    public Boolean update(SettingDTO settingDTO) {
        Optional<SettingDO> doOptional = settingDao.findByWeiXinId(settingDTO.getWeiXinId());
        if (!doOptional.isPresent())
            return false;
        SettingDO settingDO = doOptional.get();
        settingDO.setUTime(new Date());
        settingDO.setPTime(settingDTO.getPTime());
        settingDO.setEmail(settingDTO.getEmail());
        settingDao.save(settingDO);
        bindWords(settingDO.getId(), settingDTO.getWords());
        return true;
    }

    @Override
    public Boolean destroy(String settingId) {
        settingDao.deleteById(settingId);
        return true;
    }

    @Override
    public Boolean destroyByWeiXinId(String weiXinId) {
        Optional<SettingDO> optional = settingDao.findByWeiXinId(weiXinId);
        List<Long> waitDeleteWords = Lists.newArrayList();
        List<WordsDTO> waitUpdateWords = Lists.newArrayList();
        if (optional.isPresent()) {
            SettingDO settingDO = optional.get();
            List<Long> wordIds;
            if (!StringUtils.isEmpty(settingDO.getWordIds())) {
                wordIds = Longs.asList(Arrays.stream(settingDO.getWordIds().split(","))
                        .mapToLong(value -> Long.valueOf(value)).toArray());
            } else {
                wordIds = Lists.newArrayList();
            }
            List<WordsDO> wordsDOS = wordsService.findByWordIds(wordIds);
            wordsDOS.forEach(v -> {
                if (v.getRefCount() - 1 <= 0) {
                    waitDeleteWords.add(v.getId());
                } else {
                    WordsDTO dto = WordsDTO.builder().build();
                    BeanUtils.copyProperties(v, dto);
                    dto.setRefCount(v.getRefCount() - 1);
                    waitUpdateWords.add(dto);
                }
            });
            wordsService.deleteBatch(waitDeleteWords);
            expressService.destroyByWordIds(waitDeleteWords);
            wordsService.updateBatch(waitUpdateWords);
            settingDao.deleteByWeiXinId(weiXinId);
        }
        return true;
    }

    @Override
    public Boolean bindWords(String settingId, String[] words) {
        Optional<SettingDO> optional = settingDao.findById(settingId);
        if (!optional.isPresent())
            throw new RuntimeException("绑定关键词失败，settingId 不存在");
        SettingDO settingDO = optional.get();
        List<Long> wordIds;
        if (!StringUtils.isEmpty(settingDO.getWordIds())) {
            wordIds = Longs.asList(Arrays.stream(settingDO.getWordIds().split(","))
                    .mapToLong(value -> Long.valueOf(value)).toArray());
        } else {
            wordIds = Lists.newArrayList();
        }
        List<Long> waitDeleteWords = Lists.newArrayList();
        List<WordsDTO> waitUpdateWords = Lists.newArrayList();
        //处理旧的关键词
        List<WordsDO> wordsDOS = wordsService.findByWordIds(wordIds);
        String[] oldWords = wordsDOS.stream().map(v -> v.getText()).toArray(String[]::new);
        wordsDOS.forEach(v -> {
            if (Arrays.stream(words).noneMatch(w -> w.equals(v.getText()))) {//如果不在新词中
                if ((v.getRefCount() - 1) <= 0) {//如果引用计数为0 则删除word及相关快讯
                    waitDeleteWords.add(v.getId());
                } else {//否则更新refCount
                    WordsDTO dto = WordsDTO.builder().build();
                    BeanUtils.copyProperties(v, dto);
                    dto.setRefCount(v.getRefCount() - 1);
                    waitUpdateWords.add(dto);
                }
            } else {
                WordsDTO dto = WordsDTO.builder().build();
                BeanUtils.copyProperties(v, dto);
                waitUpdateWords.add(dto);
            }
        });
        //处理新的关键词
        List<Long> newWordIds = Lists.newArrayList();
        Arrays.stream(words).forEach(s -> {
            WordsDO wordsDO = wordsService.findByText(s);
            if (wordsDO == null) {//新词，添加word 并更新引用计数,；
                WordsDTO dto = WordsDTO.builder().cTime(new Date()).text(s).refCount(1).build();
                waitUpdateWords.add(dto);
            } else {
                if (Arrays.stream(oldWords).noneMatch(w -> w.equals(s))) {//旧的词且不包含在当前setting中则
                    newWordIds.add(wordsDO.getId());
                    WordsDTO dto = WordsDTO.builder().build();
                    BeanUtils.copyProperties(wordsDO, dto);
                    dto.setRefCount(dto.getRefCount() + 1);
                    waitUpdateWords.add(dto);
                }
            }
        });
        wordsService.deleteBatch(waitDeleteWords);
        expressService.destroyByWordIds(waitDeleteWords);
        List<Long> wordIdList = wordsService.updateBatch(waitUpdateWords);
        settingDO.setWordIds(StringUtils.arrayToCommaDelimitedString(wordIdList.toArray()));
        settingDao.save(settingDO);
        return true;
    }


    @Override
    public SettingDTO getInfoByWeiXinId(String weiXinId) {
        Optional<SettingDO> settingDO = settingDao.findByWeiXinId(weiXinId);
        final SettingDTO[] dto = new SettingDTO[1];
        settingDO.ifPresent(settingDO1 -> {
            List<Long> wordIds;
            if (!StringUtils.isEmpty(settingDO1.getWordIds())) {
                wordIds = Longs.asList(Arrays.stream(settingDO1.getWordIds().split(","))
                        .mapToLong(value -> Long.valueOf(value)).toArray());
            } else {
                wordIds = Lists.newArrayList();
            }
            List<WordsDO> wordsDOS = wordsService.findByWordIds(wordIds);
            String[] words = wordsDOS.stream().map(v -> v.getText()).toArray(String[]::new);
            dto[0] = SettingDTO.builder().words(words).build();
            BeanUtils.copyProperties(settingDO1, dto[0]);
        });
        return dto[0];
    }

    @Override
    public SettingDO getInfoDOByWeiXinId(String weiXinId) {
        Optional<SettingDO> settingDO = settingDao.findByWeiXinId(weiXinId);
        return settingDO.orElse(null);
    }

    @Override
    public List<SettingDO> findAll() {
        return settingDao.findAll();
    }
}
