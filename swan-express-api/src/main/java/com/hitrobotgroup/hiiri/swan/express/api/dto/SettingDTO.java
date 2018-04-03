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
package com.hitrobotgroup.hiiri.swan.express.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SettingDTO implements Serializable {
    private static final long serialVersionUID = 6773569311572418672L;
    private String weiXinId;
    private String email;
    private String[] words;
    private Date pTime;
}
