package com.hitrobotgroup.hiiri.swan.express.service.mail.queue;

import com.hitrobotgroup.hiiri.swan.express.api.dto.EmailDTO;
import com.hitrobotgroup.hiiri.swan.express.api.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费队列
 */
@Component
public class ConsumeMailQueue {
    private static final Logger logger = LoggerFactory.getLogger(ConsumeMailQueue.class);
    @Autowired
    MailService mailService;

    @PostConstruct
    public void startThread() {
        ExecutorService e = Executors.newFixedThreadPool(2);// 两个大小的固定线程池
        e.submit(new PollMail(mailService));
        e.submit(new PollMail(mailService));
    }

    class PollMail implements Runnable {
        MailService mailService;

        public PollMail(MailService mailService) {
            this.mailService = mailService;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    EmailDTO mail = MailQueue.getMailQueue().consume();
                    if (mail != null) {
                        logger.info("剩余邮件总数:{}", MailQueue.getMailQueue().size());
                        mailService.sendThymeleaf(mail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PreDestroy
    public void stopThread() {
        logger.info("destroy");
    }
}
