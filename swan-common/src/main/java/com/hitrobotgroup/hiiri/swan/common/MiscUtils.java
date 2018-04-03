/*
 * Copyright (C) 2016-2017 HIIRI Inc.All Rights Reserved. 
 * 
 * ProjectName：swan
 * 
 * Description：
 * 
 * History：
 * Version    Author        Date        Operation 
 * 1.0	      wuhj      2018/3/22    Create	
 */
package com.hitrobotgroup.hiiri.swan.common;

import java.net.MalformedURLException;
import java.net.URL;

public class MiscUtils {
    public static String stringToUnicodeExceptASICII(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {

            char c = string.charAt(i);  // 取出每一个字符
            if ((c >= 0x4e00) && (c <= 0x9fbb)) {
                unicode.append("\\u" + Integer.toHexString(c));// 转换为unicode
            } else {
                unicode.append(c);
            }
        }
        return unicode.toString();
    }

    public static String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {

            char c = string.charAt(i);  // 取出每一个字符
            if ((c >= 0x4e00) && (c <= 0x9fbb)) {

                unicode.append("\\u" + Integer.toHexString(c));// 转换为unicode
            } else {

            }
        }
        return unicode.toString();
    }

    public static String unicodeToString(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);// 转换出每一个代码点
            string.append((char) data);// 追加成string
        }
        return string.toString();
    }

    public static URL getBase(URL url) {
        if (url == null) {
            return null;
        } else {
            String file = url.getFile();
            if (file != null) {
                int var2 = file.lastIndexOf(47);
                if (var2 != -1) {
                    file = file.substring(0, var2 + 1);
                }

                try {
                    return new URL(url.getProtocol(), url.getHost(), url.getPort(), file);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }

            return url;
        }
    }
}
