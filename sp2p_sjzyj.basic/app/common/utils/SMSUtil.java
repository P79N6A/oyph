package common.utils;

import java.util.Random;

import play.Logger;
import play.cache.Cache;
import services.common.SettingService;

import com.bcloud.msg.http.HttpSender;
import com.shove.Convert;
import com.shove.gateway.sms.EimsSMS;

import common.constants.SettingKey;

/**
 * 短信验证码工具类
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月22日
 */
public class SMSUtil {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	
	/**
	 * 短信验证码
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 * @param smsAccount 短信账号
	 * @param smsPassword 短信密码
	 * @param mobile 手机号码
	 * @param content 短信模板
	 * @param effectiveTime 有效时长
	 * @param scene 短信发送场景
	 * @param isSend 是否发送邮件：测试环境不发送，正式环境需发送
	 */
	public static void sendCode(String smsAccount,String smsPassword,String mobile,String content,
			String effectiveTime, String scene ,boolean isSend) {
		int randomCode = (new Random()).nextInt(899999) + 100000;// 最大值位999999
		String SMScontent = null;
		/* 判断验证码在有效期内是否存在于缓存中 */
		Object cache = Cache.get(mobile + scene);
    	if(cache != null){
    		SMScontent = content.replace("sms_code", cache.toString());
    	}else {
    		SMScontent = content.replace("sms_code", randomCode+"");
    		Cache.set(mobile + scene, randomCode + "", effectiveTime);
		}
    	
    	
    	/* 是否发送短信 */
		if(isSend){
			sendSMS(smsAccount, smsPassword, mobile, SMScontent);
			//EimsSMS.send(smsAccount, smsPassword, SMScontent, mobile);
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(SMScontent);
		}
	}
	
	/**
	 * 发送短信
	 * @param smsAccount
	 * @param smsPassword
	 * @param mobile
	 * @param SMScontent
	 */
	public static void sendSMS(String smsAccount,String smsPassword,String mobile,String SMScontent){
		String url = "http://smssh1.253.com/msg/";// 应用地址
    	boolean needstatus = false;// 是否需要状态报告，需要true，不需要false
    	String product = null;// 产品ID
		String extno = null;// 扩展码
		try {
			HttpSender.batchSend(url, smsAccount, smsPassword, mobile, SMScontent, needstatus, product, extno) ;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("发送短信失败");
		}
	}
	
	
	public static void main(String[] args) {
		
		sendSMS("13824467807", "Vcom#2018", "13667287523", "1234567894564");
		
	}
	
}
