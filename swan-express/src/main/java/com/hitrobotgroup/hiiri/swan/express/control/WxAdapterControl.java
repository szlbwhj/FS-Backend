/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/27    Create	
 */
package com.hitrobotgroup.hiiri.swan.express.control;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class WxAdapterControl {

    @GetMapping(value = "/FVAovBRsi0.txt")
    void getTokenFile(HttpServletResponse response) throws IOException {
        Resource resource = new ClassPathResource("weixin/FVAovBRsi0.txt");
        response.setHeader("Content-Type", "application/text;charset=UTF-8");
        byte[] out = new byte[resource.getInputStream().available()];
        resource.getInputStream().read(out);
        response.getOutputStream().write(out);
    }
}
