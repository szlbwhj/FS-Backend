package com.hitrobotgroup.hiiri.swan.express.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Email封装类
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EmailDTO implements Serializable {
    private static final long serialVersionUID = -6067451269151421923L;
    //必填参数
    private String[] email;//接收方邮件
    private String subject;//主题
    private Object content;//邮件内容
    //选填
    private String template;//模板
    private HashMap<String, String> kvMap;// 自定义参数
}
