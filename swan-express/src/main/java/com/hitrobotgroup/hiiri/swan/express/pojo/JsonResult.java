/*
 *
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved.
 *
 * FileName：RobotCloud
 *
 * Description：
 *
 * History：
 * Version    Author            Date              Operation
 * 1.0	      xuzs         2017/4/18 11:42	        Create
 */
package com.hitrobotgroup.hiiri.swan.express.pojo;

import lombok.Data;

@Data
public class JsonResult {

    /**
     * 方法执行状态
     */
    private boolean isOk = false;

    /**
     * 执行成功返回值
     */
    private Object value = null;

    /**
     * 错误信息
     */
    private Object errorResponse = null;

    public JsonResult(boolean isOk, Object value) {
        this.isOk = isOk;
        this.value = value;
        this.errorResponse = "no error!";
    }

    public JsonResult(Exception e) {
        this.isOk = false;
        this.value = "";
        this.errorResponse = e.getMessage();
    }


    public JsonResult(boolean isOk, Object value, Object errorResponse) {
        this.isOk = isOk;
        this.value = value;
        this.errorResponse = errorResponse;
    }
}
