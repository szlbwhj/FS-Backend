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
package com.hitrobotgroup.hiiri.swan.express.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "tb_push_setting")
public class SettingDO implements Serializable {
    private static final long serialVersionUID = 8692706227163606143L;

    @Id
    @GeneratedValue(generator = "gen_uuid")
    @GenericGenerator(name = "gen_uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, length = 40)
    private String id;
    @Column(name = "weixin", nullable = false, length = 40, unique = true)
    private String weiXinId;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "word_ids", length = 100)
    private String wordIds;
    @Column(name = "c_time", nullable = false)
    private Date cTime;
    @Column(name = "u_time")
    private Date uTime;
    @Column(name = "p_time")
    private Date pTime;
}
