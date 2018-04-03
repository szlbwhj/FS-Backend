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
package com.hitrobotgroup.hiiri.swan.express.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "tb_push_express")
public class ExpressDO implements Serializable {

    private static final long serialVersionUID = 6408699521447636453L;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @Column(name = "source", nullable = false, length = 20)
    private String source;
    @Transient
    private String snippet;
    @Column(name = "link", nullable = false)
    private String link;
    @Transient
    private String link4little;
    @Column(name = "c_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date cTime;
    @Column(name = "word_id", nullable = false)
    private Long wordId;
}
