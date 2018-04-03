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
package com.hitrobotgroup.hiiri.swan.express.service;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;
import com.google.common.collect.Lists;
import com.hitrobotgroup.hiiri.swan.common.MiscUtils;
import com.hitrobotgroup.hiiri.swan.express.api.dto.WordsDTO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.ExpressDO;
import com.hitrobotgroup.hiiri.swan.express.api.entity.WordsDO;
import com.hitrobotgroup.hiiri.swan.express.api.service.ExpressService;
import com.hitrobotgroup.hiiri.swan.express.repository.ExpressDao;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("expressService")
public class ExpressServiceImpl implements ExpressService {

    public static final String PROXY_FORMAT_REDIRECT_URL = ExpressService.URL_PROXY_DOMAIN + "?url=%s";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ExpressDao expressDao;

    @Autowired
    WordsServiceImpl wordsService;

    @Override
    @Transactional
    public List<ExpressDO> merge(Long wordId, List<ExpressDO> doList) {
        List<WordsDO> wordsDOList = wordsService.findByWordIds(Lists.newArrayList(wordId));
        List<ExpressDO> lastNews = expressDao.findByWordId(wordId);
        List<ExpressDO> newestNews = Lists.newArrayList();
        doList.forEach(do1 -> {
            //>0  旧闻
            boolean none = lastNews.stream().noneMatch(v -> v.getTitle().equals(do1.getTitle()) ? true : false);
            if (none) {
                do1.setWordId(wordId);
                newestNews.add(do1);
            }
        });
        if (newestNews.size() > 0) {//新闻有更新则删除所哟旧闻添加新闻
            expressDao.deleteByWordId(wordId);
            expressDao.saveAll(newestNews);
        }
        WordsDO wordsDO = wordsDOList.get(0);
        wordsDO.setRTime(new Date(System.currentTimeMillis()));
        WordsDTO wordsDTO = WordsDTO.builder().build();
        BeanUtils.copyProperties(wordsDO, wordsDTO);
        wordsService.update(wordsDTO);
        return newestNews;
    }

    @Override
    public Boolean destroyByWordId(Long wordId) {
        expressDao.deleteByWordId(wordId);
        return true;
    }

    @Override
    public Boolean destroyByWordIds(List<Long> wordIds) {
        expressDao.deleteByWordIdIn(wordIds);
        return true;
    }

    @Override
    public List<ExpressDO> getListByWordId(Long wordId) {
        return expressDao.findByWordId(wordId);
    }

    @Override
    public List<ExpressDO> queryNewsOnline(String word) {
        return getSogouExpresses(word);
    }

    /**
     * 搜狗新闻
     *
     * @param word
     * @return
     */
    private List<ExpressDO> getSogouExpresses(String word) {
        String QUERY_URL = "http://news.sogou.com/news?mode=1&manual=&time=0&sort=1&dr=1";
        String QUERY_STR_KEY_WORD = "query";
        HttpResponse<String> response;
        try {
            String wordEncode = URLEncoder.encode(word, "GBK");
            response = Unirest.get(QUERY_URL).queryString(QUERY_STR_KEY_WORD, word)
                    .header("accept", "*/*").header("accept-encoding", "gzip, deflate, sdch, br")
                    .header("accept-language", "zh-CN,zh;q=0.8")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360EE")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException("UnirestException: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException: " + e.getMessage());
        }
        Document doc = Jsoup.parse(response.getBody());
        JXDocument jxDocument = new JXDocument(doc);
        List<JXNode> nodes;
        try {
            nodes = jxDocument.selN("//*[@class=vrwrap]");
        } catch (XpathSyntaxErrorException e) {
            throw new RuntimeException("XpathSyntaxErrorException: " + e.getMessage());
        }
        List<ExpressDO> dtoList = new ArrayList<>();
        nodes.forEach(v -> {
            try {
                List<JXNode> textList = v.sel("div//h3//a//text()");
                List<JXNode> hrefList = v.sel("div//h3//a//@href");
                List<JXNode> sourceList = v.sel("div//div//div//p[1]//text()");
                List<JXNode> snippetList = v.sel("div//div//div//p[2]//span//text()");
                if (textList.size() > 0 && hrefList.size() > 0 && sourceList.size() > 0 && snippetList.size() > 0) {
                    String text = textList.get(0).toString();
                    String href = hrefList.get(0).toString();
                    String source = sourceList.get(0).toString();
                    String snippet = snippetList.get(0).toString();
                    dtoList.add(ExpressDO.builder().title(text)
                            .link(href)
                            .link4little(String.format(PROXY_FORMAT_REDIRECT_URL, href))
                            .source(source).snippet(snippet).cTime(new Date()).build());
                }
            } catch (XpathSyntaxErrorException e) {
                throw new RuntimeException("XpathSyntaxErrorException: " + e.getMessage());
            }
        });
        return dtoList;
    }

    /**
     * 谷歌新闻
     *
     * @param word
     * @return
     */
    private List<ExpressDO> getGoogleExpresses(String word) {
        String QUERY_URL = "https://google.90h6.cn:1668/alerts/preview";//"https://www.google.com/alerts/preview";
        String QUERY_STR_KEY_PARAM = "params";
        String QUERY_STR_VALUE_PARAM = "[null,[null,null,null,[null,\"%s\",\"com\",[null,\"zh-CN\",\"US\"]" +
                ",null,null,null,0,1],null,3,[[null,1,\"user@example.com\",[null,null,10]" +
                ",2,\"zh-Hans-US\",null,null,null,null,null,\"0\",null," +
                "null,\"AB2Xq4hcilCERh73EFWJVHXx-io2lhh1EhC8UD8\"]]],0]";
        String QUERY_STR_KEY_HL = "hl";
        String QUERY_STR_VALUE_HL = "zh-CN";
//        Unirest.setProxy(new HttpHost("127.0.0.1", 1080));
        HttpResponse<String> response;
        try {
            response = Unirest.get(QUERY_URL).queryString(QUERY_STR_KEY_HL, QUERY_STR_VALUE_HL)
                    .queryString(QUERY_STR_KEY_PARAM, String.format(QUERY_STR_VALUE_PARAM, MiscUtils.stringToUnicodeExceptASICII(word)))
                    .header("accept", "*/*").header("accept-encoding", "gzip, deflate, sdch, br")
                    .header("accept-language", "zh-CN,zh;q=0.8")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360EE")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException("UnirestException: " + e.getMessage());
        }
        Document doc = Jsoup.parse(response.getBody());
        JXDocument jxDocument = new JXDocument(doc);
        List<JXNode> nodes;
        try {
            nodes = jxDocument.selN("//html//body//ul//li//ol//li");
        } catch (XpathSyntaxErrorException e) {
            throw new RuntimeException("XpathSyntaxErrorException: " + e.getMessage());
        }
        List<ExpressDO> dtoList = new ArrayList<>();
        nodes.forEach((JXNode v) -> {
            try {
                String text = v.sel("h4//a//text()").get(0).toString();
                String href = v.sel("h4//a//@href").get(0).toString();
                List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(href, Charset.defaultCharset());
                NameValuePair valuePair = nameValuePairs.stream().filter(nameValuePair -> {
                    if (nameValuePair.getName().equals("url"))
                        return true;
                    return false;
                }).findFirst().orElse(new NameValuePair() {
                    @Override
                    public String getName() {
                        return "name";
                    }

                    @Override
                    public String getValue() {
                        return "https://wwww.shangyuekeji.com";
                    }
                });
                String source = v.sel("h4//div//text()").get(0).toString();
                String snippet = v.sel("div//span//text()").get(0).toString();
                dtoList.add(ExpressDO.builder().title(text)
                        .link(valuePair.getValue())
                        .link4little(String.format(PROXY_FORMAT_REDIRECT_URL, valuePair.getValue()))
                        .source(source).cTime(new Date()).snippet(snippet).build());
            } catch (XpathSyntaxErrorException e) {
                throw new RuntimeException("XpathSyntaxErrorException: " + e.getMessage());
            }
        });
        return dtoList;
    }
}
