package com.weibo.moniter;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSender {
private JavaMailSender mailSender; 
private String mailSenderFrom;//发件人名片
private String mailSenderTo;//默认手机人
public void setMailSenderTo(String mailSenderTo) {
	this.mailSenderTo = mailSenderTo;
}
@Value("pangjuntao@bimoku.com")
public void setMailSenderFrom(String mailSenderFrom) {
	this.mailSenderFrom = mailSenderFrom;
}
public void setMailSender(JavaMailSender mailSender) {
	this.mailSender = mailSender;
}	
	public void sendByText(MailModel model) {
		System.out.println("开始发送邮件通知...");
		SimpleMailMessage mail1 = new SimpleMailMessage();
		mail1.setFrom(mailSenderFrom);// 发送人名片
		mail1.setTo(mailSenderTo);// 收件人邮箱
		mail1.setSubject(model.getSubject());// 邮件主题
		mail1.setSentDate(new Date());// 邮件发送时间
		
		mail1.setText(model.getContent());
		try {
			mailSender.send(mail1);
		} catch (Exception e) {
			System.out.println("发送失败"+e.toString());
			return;
		}
		System.out.println("发送成功");
	}
	public void send(MailModel model) {
		System.out.println("开始发送邮件通知...");
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mail1;
		try {
			mail1 = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mail1.setFrom(new InternetAddress("\"" + MimeUtility.encodeText("来自微博抓取程序Service") + "\" <"+mailSenderFrom+">"));// 发送人名片
			mail1.setTo(mailSenderTo);// 收件人邮箱
			mail1.setSubject(model.getSubject());// 邮件主题
			mail1.setSentDate(new Date());// 邮件发送时间
			mail1.setText(model.getContent());
			try {
				mailSender.send(mimeMessage);
			} catch (Exception e) {
				System.out.println("发送失败"+e.toString());
				return;
			}
		} catch (MessagingException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("发送成功");
	}
}

