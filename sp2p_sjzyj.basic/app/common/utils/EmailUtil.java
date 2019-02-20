package common.utils;

import java.util.List;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.shove.Convert;

import common.constants.SettingKey;
import play.Logger;
import services.common.SettingService;

public class EmailUtil {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	/**
	 * 发送HMTL格式的邮件
	 *
	 * @param emailWebsite 邮件服务器地址
	 * @param mailAccount 发送邮件的账号
	 * @param mailPassword 发送邮件的密码 
	 * @param toEmail 接收邮件的地址
	 * @param title 邮件的标题
	 * @param content 邮件的内容
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static boolean sendHtmlEmail(String emailWebsite,String mailAccount,String mailPassword,String toEmail,String title,String content){
		boolean flag = false;
		
		try {
			
			HtmlEmail sendEmail = new HtmlEmail();
			
			sendEmail.setHostName(emailWebsite);
			sendEmail.setAuthentication(mailAccount, mailPassword);
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("from email "+mailAccount);
			}
			
			sendEmail.setFrom(mailAccount);
			
			sendEmail.addTo(toEmail);
			
			sendEmail.setSubject(title);
			sendEmail.setCharset("utf-8");
			sendEmail.setHtmlMsg(content);
			sendEmail.send();
			flag = true;
		} catch (EmailException e) {
			e.printStackTrace();
			LoggerUtil.error(false, "发送邮件时失败【toEmail:%s,title:%s】", toEmail,title);
		}

		return flag;
	}

	/**
	 * 发送HMTL格式的邮件(带附件)
	 *
	 * @param emailWebsite 邮件服务器地址
	 * @param mailAccount 发送邮件的账号
	 * @param mailPassword 发送邮件的密码 
	 * @param toEmail 接收邮件的地址
	 * @param title 邮件的标题
	 * @param content 邮件的内容
	 * @param attachmentPath 附件的路径
	 * @param attachmentName 附件的名称
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月2日
	 */
	public static boolean sendHtmlEmail(String emailWebsite,String mailAccount,String mailPassword,String toEmail,String title,String content,String attachmentPath,String attachmentName){
		boolean flag = false;
		try {
			
			EmailAttachment attachment = new EmailAttachment();  
			attachment.setPath(attachmentPath);
            attachment.setDisposition(EmailAttachment.ATTACHMENT);  
            attachment.setName(attachmentName);  
			
			HtmlEmail sendEmail = new HtmlEmail();
			
			sendEmail.setHostName(emailWebsite);
			sendEmail.setAuthentication(mailAccount, mailPassword);
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("from email "+mailAccount);
			}
			
			sendEmail.setFrom(mailAccount);
			
			sendEmail.addTo(toEmail);
			
			sendEmail.setSubject(title);
			sendEmail.setCharset("utf-8");
			sendEmail.setHtmlMsg(content);
			sendEmail.attach(attachment);
			sendEmail.send();
			flag = true;
		} catch (EmailException e) {
			LoggerUtil.error(false, "发送邮件时失败【toEmail:%s,title:%s】", toEmail,title);
		}

		return flag;
	}
	
	
	public static boolean sendHtmlEmail(String emailWebsite,String mailAccount,String mailPassword,List<String> toEmails,String title,String content){
		boolean flag = false;
		
		try {
			
			HtmlEmail sendEmail = new HtmlEmail();
			
			sendEmail.setHostName(emailWebsite);
			sendEmail.setAuthentication(mailAccount, mailPassword);
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("from email "+mailAccount);
			}
			
			sendEmail.setFrom(mailAccount);
			
			for (String email : toEmails) {
				sendEmail.addTo(email);
			}
			
			sendEmail.setSubject(title);
			sendEmail.setCharset("utf-8");
			sendEmail.setMsg(content);
			sendEmail.send();
			flag = true;
		} catch (EmailException e) {
			LoggerUtil.error(false, "发送邮件时失败【toEmail:%s,title:%s】", toEmails,title);
		}

		return flag;
	}
	
	/**
	 * 防拦截html转换代码
	 */
	public static final String HTML_YING = "html_y"; // '
	public static final String HTML_JING = "html_j"; // #
	public static final String HTML_IMG = "html_i"; // <img
	public static final String HTML_A = "html_a";  // <a
	public static final String HTML_FRAME = "html_f"; // <frame
	
	public static String replaceAllHTML(String content) {
		if(null == content || content.length() < 1)
			return content;
		
		return content.replaceAll(EmailUtil.HTML_YING, "'").replaceAll(EmailUtil.HTML_JING, "#").replaceAll(
				EmailUtil.HTML_IMG, "<img").replaceAll(EmailUtil.HTML_A, "<a")
				.replaceAll(EmailUtil.HTML_FRAME, "<frame");
	}
	
}
