/**
 * CopyRight (C) 2016-2017 HIIRI Inc.All Rights Reserved.
 * <p>
 * FileName  swan
 * <p>
 * Description
 * <p>
 * History:
 * Version     Author                 Date
 * 1.0         fanmx            2018/3/27  15:25
 */
package com.hitrobotgroup.hiiri.swan.express.control;

import com.alibaba.fastjson.JSONObject;
import com.hitrobotgroup.hiiri.swan.express.pojo.JsonResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/v1/api/weixin")
public class WeixinApiControl {

    private static String url = "https://api.weixin.qq.com/sns/jscode2session";
    private static String appId = "wx5d35120aeca69f75";
    private static String appSecret = "775042d74503c4c8bbdc2024320b953b";
    private static Logger logger = LoggerFactory.getLogger(WeixinApiControl.class);
    private static HttpClient client = HttpClientBuilder.create().build();

    public static JSONObject invokeService(String queryCase) {
        HttpPost request = new HttpPost(url);
        logger.info(queryCase);
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(queryCase);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        reqEntity.setContentType("application/x-www-form-urlencoded");
        request.setEntity(reqEntity);
        JSONObject jsonResult = null;
        try {
            HttpResponse response = client.execute(request);
            String strResult = EntityUtils.toString(response.getEntity());
            jsonResult = JSONObject.parseObject(strResult);
            logger.info("result:" + jsonResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }


    @RequestMapping(value = "getLoginMsg", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult getLoginMsg(String code) {
        try {
            String queryCase = "appid=" + appId + "&secret=" + appSecret + "&grant_type=authorization_code&js_code=" + code;
            JSONObject jsonObject = invokeService(queryCase);
            return new JsonResult(true, jsonObject);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }
}
