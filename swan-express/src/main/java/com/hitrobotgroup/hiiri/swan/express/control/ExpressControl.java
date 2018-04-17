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
package com.hitrobotgroup.hiiri.swan.express.control;

import com.hitrobotgroup.hiiri.swan.common.MiscUtils;
import com.hitrobotgroup.hiiri.swan.common.readability.snacktory.HtmlFetcher;
import com.hitrobotgroup.hiiri.swan.common.readability.snacktory.JResult;
import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;
import com.hitrobotgroup.hiiri.swan.express.api.service.ExpressService;
import com.hitrobotgroup.hiiri.swan.express.mapper.ExpressMapper;
import com.hitrobotgroup.hiiri.swan.express.pojo.JsonResult;
import com.hitrobotgroup.hiiri.swan.express.service.ExpressServiceImpl;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.tika.parser.txt.CharsetDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/v1/api/express")
public class ExpressControl {

    @Autowired
    ExpressMapper mapper;
    @Autowired
    ExpressServiceImpl expressService;

    @RequestMapping(value = "queryNewsOnline", method = {RequestMethod.POST, RequestMethod.GET})
    JsonResult queryNewsOnline(@RequestParam("word") String word) {
        try {
            List<ExpressDO> dtoList = expressService.queryNewsOnline(word);
            return new JsonResult(true, dtoList);
        } catch (Exception ex) {
            return new JsonResult(false, ex.getMessage());
        }
    }

    /**
     * raw
     *
     * @param url
     * @param request
     * @param response
     */
    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    void proxyReadable(String url, HttpServletRequest request, HttpServletResponse response) {
        try {
//            HttpResponse<InputStream> httpResponse = Unirest.get(url)
//                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
//                            "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360EE")
//                    .asBinary();
//            byte[] out = new byte[httpResponse.getRawBody().available()];
//            httpResponse.getRawBody().read(out);
//            CharsetDetector detector = new CharsetDetector();
//            detector.setText(out);
//            String encoding = detector.detect().getName();
            final URL url_form = new URL(url);
            HtmlFetcher fetcher = new HtmlFetcher();
            // set cache. e.g. take the map implementation from google collections:
            // fetcher.setCache(new MapMaker().concurrencyLevel(20).maximumSize(count).
            // expireAfterWrite(minutes, TimeUnit.MINUTES).makeMap();
            JResult res = fetcher.fetchAndExtract(url, 10000, true);
            if (StringUtils.isEmpty(res.getTextDOC()) || res.getTextDOC().length() <= 300) {
                proxyRaw(url, request, response);
                return;
            }
            String body = getWeiXinBody(url_form, res.getTextDOC());
            response.getOutputStream().write(body.getBytes(Charset.forName("GBK")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * raw
     *
     * @param url
     * @param request
     * @param response
     */
    @RequestMapping(value = "proxyRaw", method = RequestMethod.GET)
    void proxyRaw(String url, HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpResponse<InputStream> httpResponse = Unirest.get(url)
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360EE")
                    .asBinary();

            final URL url_form = new URL(url);
            boolean isHtml = httpResponse.getHeaders().get("Content-Type").stream()
                    .allMatch(s -> s.startsWith("text/htm") ? true : false);
            response.setStatus(httpResponse.getStatus());

            if (isHtml) {
                byte[] out = new byte[httpResponse.getRawBody().available()];
                httpResponse.getRawBody().read(out);
                CharsetDetector detector = new CharsetDetector();
                detector.setText(out);
                String encoding = detector.detect().getName();
                String body = new String(out, Charset.forName(encoding));
                body = getWeiXinBody(url_form, body);
                response.getOutputStream().write(body.getBytes(Charset.forName(encoding)));
            } else {
                byte[] out = new byte[httpResponse.getRawBody().available()];
                httpResponse.getRawBody().read(out);
                response.getOutputStream().write(out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * mercury parser
     *
     * @param url
     * @param request
     * @param response
     */
    @RequestMapping(value = "proxyNN", method = RequestMethod.GET)
    void proxy(String url, HttpServletRequest request, HttpServletResponse response) {
        try {
            final URL url_form = new URL(url);
            HttpResponse<InputStream> httpResponse = Unirest.get(url)
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360EE")
                    .asBinary();
            boolean isHtml = httpResponse.getHeaders().get("Content-Type").stream()
                    .allMatch(s -> s.startsWith("text/htm") ? true : false);
            if (isHtml) {
                HttpResponse<JsonNode> jsonRsp = Unirest.get("https://mercury.postlight.com/parser")
                        .queryString("url", url)
                        .header("x-api-key", "5fQxc1QGNfkH05XM36LKkmJ6mdJ1CrM6gvLOhTHt")
                        .asJson();
                response.setStatus(jsonRsp.getStatus());
                byte[] out = new byte[httpResponse.getRawBody().available()];
                httpResponse.getRawBody().read(out);
                CharsetDetector detector = new CharsetDetector();
                detector.setText(out);
                String encoding = detector.detect().getName();
                String body = new String(jsonRsp.getBody().getObject().getString("content").getBytes(),
                        Charset.forName("UTF-8"));
                body = getWeiXinBody(url_form, body);
                response.getOutputStream().write(body.getBytes(Charset.forName(encoding)));
            } else {
                response.setStatus(httpResponse.getStatus());
                byte[] out = new byte[httpResponse.getRawBody().available()];
                httpResponse.getRawBody().read(out);
                response.getOutputStream().write(out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getWeiXinBody(URL url_form, String body) {
        final String URL_NOT_HTML_REQUEST = ExpressService.URL_PROXY_DOMAIN + "Raw";
        body = body.replaceAll("http://", URL_NOT_HTML_REQUEST + "?url=http://")
                .replaceAll("src=\"//", "src=\"" + URL_NOT_HTML_REQUEST + "?url=" + url_form.getProtocol() + "://")
                .replaceAll("src=\"/", "src=\"" + URL_NOT_HTML_REQUEST + "?url=" + url_form.getProtocol() + "://"
                        + url_form.getHost() + "/")
                .replaceAll("src=\"(?!(htt(p|ps)))", "src=\"" + URL_NOT_HTML_REQUEST + "?url=" + MiscUtils.getBase(url_form))
                .replaceAll("href=\"http://", "href=\"" + URL_NOT_HTML_REQUEST + "?url=http://")
                .replaceAll("href=\"//", "href=\"" + URL_NOT_HTML_REQUEST + "?url=" + url_form.getProtocol() + "://")
                .replaceAll("href=\"/", "href=\"" + URL_NOT_HTML_REQUEST + "?url=" + url_form.getProtocol() + "://"
                        + url_form.getHost() + "/")
                .replaceAll("href=\"(?!(htt(p|ps)))", "href=\"" + URL_NOT_HTML_REQUEST + "?url=" + MiscUtils.getBase(url_form));
        return body;
    }
}
