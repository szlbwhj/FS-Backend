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
public class ExpressDTO implements Serializable {
    private static final long serialVersionUID = 696556081560334850L;
    private Long id;
    private String title;
    private String source;
    private String snippet;
    private String link;
    private Date cTime;
    private String word;
}
