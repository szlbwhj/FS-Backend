package com.hitrobotgroup.hiiri.swan.express.service.mail;

import com.hitrobotgroup.hiiri.swan.express.api.dto.EmailDTO;
import com.hitrobotgroup.hiiri.swan.express.api.service.MailService;
import com.hitrobotgroup.hiiri.swan.express.service.mail.queue.MailQueue;
import com.hitrobotgroup.hiiri.swan.express.service.mail.util.Constants;
import com.hitrobotgroup.hiiri.swan.express.service.mail.util.MailUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

//import org.springframework.data.redis.core.RedisTemplate;

@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;//执行者
    @Autowired
    public Configuration configuration;//freemarker
    @Autowired
    private SpringTemplateEngine templateEngine;//thymeleaf
    @Value("${spring.mail.username}")
    public String USER_NAME;//发送者

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    @Override
    public void send(EmailDTO mail) throws Exception {
        logger.info("发送邮件：{}", mail.getContent());
        MailUtil mailUtil = new MailUtil();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(USER_NAME);
        message.setTo(mail.getEmail());
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent().toString());
        mailUtil.start(mailSender, message);
    }

    @Override
    public void sendHtml(EmailDTO mail) throws Exception {
        MailUtil mailUtil = new MailUtil();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(USER_NAME);
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        helper.setText(
                "<html><body><img src=\"cid:springcloud\" ></body></html>",
                true);
        // 发送图片
        File file = ResourceUtils.getFile("classpath:static"
                + Constants.SF_FILE_SEPARATOR + "image"
                + Constants.SF_FILE_SEPARATOR + "springcloud.png");
        helper.addInline("springcloud", file);
        // 发送附件
        file = ResourceUtils.getFile("classpath:static"
                + Constants.SF_FILE_SEPARATOR + "file"
                + Constants.SF_FILE_SEPARATOR + "file.txt");
        helper.addAttachment("测试附件", file);
        mailUtil.startHtml(mailSender, message);
    }

    @Override
    public void sendFreemarker(EmailDTO mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(USER_NAME);
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("content", mail.getContent());
        Template template = configuration.getTemplate(mail.getTemplate() + ".flt");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(
                template, model);
        helper.setText(text, true);
        mailSender.send(message);
    }

    @Override
    public void sendThymeleaf(EmailDTO mail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(USER_NAME);
        helper.setTo(mail.getEmail());
        helper.setSubject(mail.getSubject());
        Context context = new Context();
        context.setVariable("news", mail.getContent());
        String text = templateEngine.process(mail.getTemplate(), context);
        helper.setText(text, true);
        mailSender.send(message);
    }

    @Override
    public void sendQueue(EmailDTO mail) throws Exception {
        MailQueue.getMailQueue().produce(mail);
    }

    @Override
    public void sendRedisQueue(EmailDTO mail) throws Exception {
		redisTemplate.convertAndSend("mail",mail);
    }
}
