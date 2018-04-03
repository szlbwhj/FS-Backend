/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/20    Create	
 */
package com.hitrobotgroup.hiiri.swan.express.mapper;

import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ExpressMapper {

    @Select("select * from tb_push_express where id =#{id}")
    ExpressDO findById(String id);

    @Select("select * from tb_push_express where setting_id =#{settingId}")
    ExpressDO findBySettingId(String settingId);

    @Delete("deleteBatch from tb_push_express where setting_id =#{settingId}")
    int deleteBySettingId(String settingId);
}
