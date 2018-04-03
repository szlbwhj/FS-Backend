package com.hitrobotgroup.hiiri.swan.express.api.service;

import com.hitrobotgroup.hiiri.swan.express.api.dto.EmailDTO;

public interface MailService {
    /**
     * 纯文本
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年7月20日 更新日志
     * 2017年7月20日  科帮网 首次创建
     */
    public void send(EmailDTO mail) throws Exception;

    /**
     * 富文本
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年7月20日 更新日志
     * 2017年7月20日  科帮网 首次创建
     */
    public void sendHtml(EmailDTO mail) throws Exception;

    /**
     * 模版发送 freemarker
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年7月20日 更新日志
     * 2017年7月20日  科帮网 首次创建
     */
    public void sendFreemarker(EmailDTO mail) throws Exception;

    /**
     * 模版发送 thymeleaf
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年7月20日 更新日志
     * 2017年7月20日  科帮网 首次创建
     */
    public void sendThymeleaf(EmailDTO mail) throws Exception;

    /**
     * 队列
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年8月4日 更新日志
     * 2017年8月4日  科帮网 首次创建
     */
    public void sendQueue(EmailDTO mail) throws Exception;

    /**
     * Redis 队列
     *
     * @param mail
     * @throws Exception void
     * @Author 科帮网
     * @Date 2017年8月13日 更新日志
     * 2017年8月13日  科帮网 首次创建
     */
    public void sendRedisQueue(EmailDTO mail) throws Exception;
}
