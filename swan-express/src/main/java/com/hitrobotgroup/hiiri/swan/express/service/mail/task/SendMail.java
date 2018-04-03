package com.hitrobotgroup.hiiri.swan.express.service.mail.task;

import org.springframework.stereotype.Component;

/**
 * 统计失败邮件定时重新发送
 *
 */
@Component("sendMail")
public class SendMail {
	//@Scheduled(cron = "0/5 * * * * ?")
	public void sendMail() {
		System.out.println("同步开始123");
	}
}
