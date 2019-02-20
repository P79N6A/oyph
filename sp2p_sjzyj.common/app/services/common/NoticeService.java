package services.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.UserMessage;
import models.common.entity.t_email;
import models.common.entity.t_email_sending;
import models.common.entity.t_mass_email;
import models.common.entity.t_mass_email_sending;
import models.common.entity.t_mass_sms;
import models.common.entity.t_mass_sms_sending;
import models.common.entity.t_message;
import models.common.entity.t_message_user;
import models.common.entity.t_notice_setting_user;
import models.common.entity.t_sms;
import models.common.entity.t_sms_jy_count;
import models.common.entity.t_sms_sending;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.cache.Cache;
import services.base.BaseService;

import com.shove.gateway.sms.EimsSMS;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.interfaces.ICacheable;
import common.utils.DateUtil;
import common.utils.EmailUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.JYAPIUtil;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.StrUtil;
import daos.common.EmailDao;
import daos.common.EmailSendingDao;
import daos.common.MassEmailDao;
import daos.common.MassEmailSendingDao;
import daos.common.MassSmsDao;
import daos.common.MassSmsSendingDao;
import daos.common.MessageDao;
import daos.common.MessageUserDao;
import daos.common.NoticeSettingDao;
import daos.common.SmsDao;
import daos.common.SmsSendingDao;
import daos.common.TemplateNoticeDao;

/**
 * 系统消息Service接口(包括SMS,MSG,Email)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class NoticeService extends BaseService<t_message_user> implements ICacheable {
	
	protected MessageUserDao messageUserDao = null;
	protected MessageDao messageDao = Factory.getDao(MessageDao.class);
	
	protected SmsDao smsDao = Factory.getDao(SmsDao.class);
	protected SmsSendingDao smsSendingDao = Factory.getDao(SmsSendingDao.class);
	
	protected EmailDao emailDao = Factory.getDao(EmailDao.class);
	protected EmailSendingDao emailSendingDao = Factory.getDao(EmailSendingDao.class);
	
	protected NoticeSettingDao noticeSettingDao = Factory.getDao(NoticeSettingDao.class); 
	protected TemplateNoticeDao templateNoticeDao = Factory.getDao(TemplateNoticeDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static MassSmsSendingDao massSmsSendingDao = Factory.getDao(MassSmsSendingDao.class);
	protected static MassSmsDao massSmsDao = Factory.getDao(MassSmsDao.class);
	
	protected static MassEmailSendingDao massEmailSendingDao = Factory.getDao(MassEmailSendingDao.class);
	protected static MassEmailDao massEmailDao = Factory.getDao(MassEmailDao.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	protected NoticeService() {
		this.messageUserDao = Factory.getDao(MessageUserDao.class);
		
		super.basedao = this.messageUserDao;
	}
	
	
	
	/**
	 * 系统自动给用户发送消息
	 *
	 * @description NoticeScene中的code对应的模板类(t_template_notice)中scene字段。<br><br>
	 * 				亲爱的user_name：恭喜您充值成功！充值amount元，平台账户余额为balance元。则parame中参数key为user_name,amount,balance<br><br>
	 * 				其中短信和邮件都是非实时的，只会往t_sms_sending和t_email_sending表中插入数据，然后会在定时任务中发送真正的短信和邮件给用户<br><br>
	 * 				<b>一旦判定消息发送失败，要求手动调用回滚的方法自行数据回滚<b><br><br>
	 *
	 * @param userId 接收消息的用户id
	 * @param scene 其中通知邮件和推广活动是没有模板的，需要调用其他的方法
	 * @param content 消息场景中对应的参数
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月19日
	 */
	public boolean sendSysNotice(long userId, NoticeScene scene, Map<String, Object> sceneParame) {
		//将double和Date类型转换成指定的格式数据类型
		Map<String, String> param_format = StrUtil.mapToMap(sceneParame);
		
		//获取用户在某个场景下的消息发送状况
		t_notice_setting_user userSetting = this.findUserNoticeSetting(userId, scene);
		
		//场景的标题
		String title = scene.value;
		
		//该场景在数据库中的模板
		List<t_template_notice> templates = this.queryTemplatesByScene(scene);
		
		if (templates == null || templates.size() == 0) {
			return false;
		}
		boolean flag = true;

		for (t_template_notice template : templates) {
			NoticeType type = template.getType();
			/*if (type.equals(NoticeType.SMS)) {
				if (userSetting.sms) {
					//发送短信
					String content = StrUtil.replaceByMap(template.content,param_format);
					if (!sendSysSMS(userId, content)) {
						flag = false;
						LoggerUtil.info(false, "系统短信发送失败");
						break;
					}

				}
			} else*/ if (type.equals(NoticeType.MSG)) {
				if (userSetting.msg) {
					//发送站内信
					String content = StrUtil.replaceByMap(template.content, param_format);
					if (!sendMsg(userId, 0L, title, content)) {
						flag = false;
						LoggerUtil.info(false, "系统站内消息发送失败");
						break;
					}
				}
			} else if (type.equals(NoticeType.EMAIL)) {
				if (userSetting.email) {
					//发送邮件
					String content = StrUtil.replaceByMap(template.content, param_format);
					ResultInfo result = sendSysEmail(userId, title, content);
					if (result.code < 0) {
						flag = false;
						LoggerUtil.info(false, "系统邮件发送失败");
						break;
					}
				}
			}
		}
		
		return flag;
	}

	/**
	 * 收到给用户发送消息
	 *
	 * @param userId 接收消息的用户id
	 * @param scene 其中通知邮件和推广活动是没有模板的，需要调用其他的方法
	 * @param content 消息场景中对应的参数
	 * @param type   消息类型
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月30日14:24:18
	 */
	public boolean sendUserNotice(long userId, NoticeScene scene, Map<String, Object> sceneParame, NoticeType type) {
		//将double和Date类型转换成指定的格式数据类型
		Map<String, String> param_format = StrUtil.mapToMap(sceneParame);
		
		//获取用户在某个场景下的消息发送状况
		t_notice_setting_user userSetting = this.findUserNoticeSetting(userId, scene);
		
		//场景的标题
		String title = scene.value;
		
		//该场景在数据库中的模板
		List<t_template_notice> templates = this.queryTemplatesByScene(scene);
		
		if (templates == null || templates.size() == 0) {
			return false;
		}
		boolean flag = true;

		for (t_template_notice template : templates) {
			
			/*if (type.equals(NoticeType.SMS)) {
				if (userSetting.sms) {
					//发送短信
					String content = StrUtil.replaceByMap(template.content,param_format);
					if (!sendSysSMS(userId, content)) {
						flag = false;
						LoggerUtil.info(false, "系统短信发送失败");
						break;
					}

				}
			} else*/ if (type.equals(NoticeType.MSG)) {
				if (userSetting.msg) {
					//发送站内信
					String content = StrUtil.replaceByMap(template.content, param_format);
					if (!sendMsg(userId, 0L, title, content)) {
						flag = false;
						LoggerUtil.info(false, "系统站内消息发送失败");
						break;
					}
				}
			} else if (type.equals(NoticeType.EMAIL)) {
				if (userSetting.email) {
					//发送邮件
					String content = StrUtil.replaceByMap(template.content, param_format);
					ResultInfo result = sendSysEmail(userId, title, content);
					if (result.code < 0) {
						flag = false;
						LoggerUtil.info(false, "系统邮件发送失败");
						break;
					}
				}
			}
		}
		
		return flag;
	}
	
	
	
	
	
	
	/**
	 * 系统发送短信给用户(非实时)
	 *
	 * @description 非实时，只是往t_sms_sending表中插入一条数据，然后定时器会根据这张表中的数据发送真正的短信给用户
	 *
	 * @param userId 接收短信的用户的id
	 * @param content 短信的内容
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	public boolean sendSysSMS(long userId, String content) {
		t_user user = userService.findByID(userId);
		
		t_sms_sending smsSending = new t_sms_sending();
		smsSending.time = new Date();
		smsSending.mobile = user.mobile;
		smsSending.content = content;
		smsSending.is_send = false;
		smsSending.try_times = 0;
		
		boolean flag = smsSendingDao.save(smsSending);
		
		return flag;
	}
	
	/**
	 * 群发短信给用户(非实时)
	 *
	 * @description 非实时，只是往t_mass_sms_sending表中插入一条数据，然后定时器会根据这张表中的数据发送真正的短信给用户
	 *
	 * @param member_type 会员类型
	 * @param content 短信的内容
	 * @param scene 场景
	 * @param supervisorId 管理员id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月21日
	 */
	public ResultInfo sendMassSMS(int member_type,String content,NoticeScene scene,long supervisorId) {
		ResultInfo result = new ResultInfo();
		//查询收信人手机号
		List<Map<String, Object>> userList = userInfoService.queryUserNoticeSmsSetting(member_type, scene);
		if(userList == null || userList.size() ==0){
			result.code = -1;
			result.msg = "收信人手机号为空,群发短信失败";
			
			return result;
		}
		
		int num=0;
		for(Map<String, Object> map: userList){
			t_mass_sms_sending massSmsSending = new t_mass_sms_sending();
			massSmsSending.time = new Date();
			massSmsSending.content = content;
			massSmsSending.is_send = false;
			massSmsSending.try_times = 0;
			massSmsSending.mobile = map.get("mobile")+"";
			
			boolean flag = massSmsSendingDao.save(massSmsSending);
			if(flag ){
				num++;
			}
		}
		
		//若i==0，则本次群发短信失败
		if(num==0){
			result.code = -1;
			result.msg = "群发短信失败";
			
			return result;
		}
		
		//将发送数据添加备份到t_mass_sms
		t_mass_sms mass_sms = new t_mass_sms();
		mass_sms.time = new Date();
		mass_sms.supervisor_id = supervisorId;
		mass_sms.setMember_type(t_user_info.MemberType.getEnum(member_type));
		mass_sms.num = num;
		mass_sms.content = content;
		boolean massFlag = massSmsDao.save(mass_sms);
		if(!massFlag){
			result.code = -1;
			result.msg = "群发短信失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 群发邮件给用户(非实时)
	 *
	 * @description 非实时，只是往t_mass_email_sending表中插入一条数据，然后定时器会根据这张表中的数据发送真正的邮件给用户
	 *
	 * @param member_type 会员类型
	 * @param content 短信的内容
	 * @param supervisorId 管理员id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年04月05日
	 */
	public ResultInfo sendMassEamil(int member_type,String title,String content,NoticeScene scene,long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		//查询收件人邮箱地址
		List<Map<String, Object>> userList = userInfoService.queryUserNoticeEmailSetting(member_type, scene);
		if(userList == null || userList.size() ==0){
			result.code = -1;
			result.msg = "收件邮箱为空,群发短信失败";
			
			return result;
		}
		
		int num=0;
		for(Map<String, Object> map: userList){
			t_mass_email_sending massEmailSending = new t_mass_email_sending();
			massEmailSending.time = new Date();
			massEmailSending.title = title;
			massEmailSending.content = content;
			massEmailSending.is_send = false;
			massEmailSending.try_times = 0;
			massEmailSending.email = map.get("email")+"";
			boolean flag = massEmailSendingDao.save(massEmailSending);
			if(flag ){
				num++;
			}
		}
		
		//若i==0，则本次群发邮件失败
		if(num==0){
			result.code = -1;
			result.msg = "群发邮件失败";
			
			return result;
		}
		
		//将邮件内容备份到t_mass_email
		t_mass_email mass_email = new t_mass_email();
		mass_email.time = new Date();
		mass_email.supervisor_id = supervisorId;
		mass_email.setMember_type(t_user_info.MemberType.getEnum(member_type));
		mass_email.title = title;
		mass_email.content = content;
		mass_email.num = num;
		boolean massFlag = massEmailDao.save(mass_email);
		if(!massFlag){
			result.code = -1;
			result.msg = "群发邮件失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
	
		return result;
	}
	

	/**
	 * 系统发送邮件给用户(如果用户没有绑定邮箱，会返回一个0的code值)
	 *
	 * @description 非实时，只是往t_email_sending表中插入一条数据，然后定时器会根据这张表中的数据发送真正的邮件给用户
	 *
	 * @param userId 接收邮件的用户的id
	 * @param title 邮件的标题
	 * @param content 邮件的内容
	 * @return 返回码:<br>
	 * 			1:成功<br>
	 * 			0:用户没有绑定邮箱<br>
	 * 		
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	public ResultInfo sendSysEmail(long userId, String title, String content) {
		ResultInfo result = new ResultInfo();
		
		t_user_info user = userInfoService.findUserInfoByUserId(userId);
		if (user == null) {
			result.code = -1;
			result.msg = "用户不存在!";
			
			return result;
		}
		
		if(StringUtils.isBlank(user.email)){
			result.code = 0;
			result.msg = "该用户还没有绑定邮箱!";
			
			return result;
		}
		
		t_email_sending email_sending = new t_email_sending();
		email_sending.time = new Date();
		email_sending.email= user.email;
		email_sending.title = title;
		email_sending.content = content;
		email_sending.is_send = false;
		email_sending.try_times = 0;
		
		boolean flag = emailSendingDao.save(email_sending);
		if ( !flag ) {
			result.code = -99;
			result.msg = "保存邮件信息时抛异常!";
			
			return result;
		} else {
			result.code = 1;
			result.msg = "成功!";
			
			return result;
		}
	}
	
	
	/**
	 * 管理员发送短信
	 *
	 * @param userId 接收短信的用户的id
	 * @param supervisorId 发送短信的用户id(如果是系统自己发送出去的，该值为0L)
	 * @param content 发送短信的内容
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月19日
	 */
	public boolean sendSMS(long userId,long supervisorId, String content) {
		t_user user = userService.findByID(userId);
		String mobile = user.mobile;
		
		return sendSMS(mobile, supervisorId, content);
	}


	/**
	 * 管理员发送一个站内信给用户(包括系统自动发送的，自动发送的id为0)
	 *
	 * @param userId 接收站内信的用户id
	 * @param supervisorId 发送站内信的管理员的id(如果是系统自动发送的，这个id为0L)
	 * @param title 站内信的标题
	 * @param content 站内信的内容
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月19日
	 */
	public boolean sendMsg(long userId,long supervisorId, String title, String content) {
		Date now = new Date();
		
		t_message message = new t_message();
		message.time = now;
		message.supervisor_id = supervisorId;
		message.title = title;
		message.content = content;
		
		boolean flagOfMessage = messageDao.save(message);
		if ( !flagOfMessage ) {
			//消息发送失败
			return false;
		}
		
		t_message_user userMessage = new t_message_user();
		userMessage.time = now;
		userMessage.user_id = userId;
		userMessage.message_id = message.id;
		userMessage.is_read = false;
		
		boolean flagOfUserMessage = messageUserDao.save(userMessage);
		if( !flagOfUserMessage ){
			//消息发送失败
			return false;
		}
		
		return true;
	}
	/**
	 * 群发消息
	 * 
	 * @param member_type 会员类型 -1:全部会员  0:新会员  1：理财会员 2：借款会员
	 * @param supervisorId 发送站内信的管理员的id(如果是系统自动发送的，这个id为0L)
	 * @param title 站内信的标题
	 * @param content 站内信的内容
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月31日
	 *
	 */
	public ResultInfo sendMassMsg(int member_type,long supervisorId, String title, String content, NoticeScene scene) {
		ResultInfo result = new ResultInfo();
		
		Date now = new Date();
		
		t_message message = new t_message();
		message.time = now;
		message.supervisor_id = supervisorId;
		message.title = title;
		message.content = content;
		
		boolean flagOfMessage = messageDao.save(message);
		if ( !flagOfMessage ) {
			result.code = -1;
			result.msg = "群发消息失败";
			
			return result;
		}
	
		//查询所要发送的会员
		List<Map<String, Object>> userInfoList = userInfoService.queryUserNoticeMsgSetting(member_type,scene);
		if(userInfoList == null || userInfoList.size() ==0){
			result.code = -1;
			result.msg = "群发消息失败";
			
			return result;
		}
		
		int i=0;
		for(Map<String, Object> map: userInfoList){
			t_message_user userMessage = new t_message_user();
			userMessage.time = now;
			userMessage.message_id = message.id;
			userMessage.is_read = false;
			userMessage.user_id = Long.valueOf(map.get("user_id")+"");
			
			boolean flagOfUserMessage = messageUserDao.save(userMessage);
			if(flagOfUserMessage ){
				i++;
			}
		}
		
		//若i==0，则本次群发消息失败
		if(i==0){
			result.code = -1;
			result.msg = "群发消息失败";
			
			return result;
		}

		result.code = 1;
		result.msg = "群发消息成功";
		
		return result;
	}


	/**
	 * 管理员发送一个邮件给用户(如果用户没有绑定邮件，返回值code将为0)
	 *
	 * @param userId 接收邮件的用户id
	 * @param supervisorId 发送邮件的管理员id(如果为系统自动发送的，这个值设置0L)
	 * @param title 邮件的标题
	 * @param content 邮件的内容(支持HTML格式的邮件)
	 * @return 返回码:<br>
	 * 			1:成功<br>
	 * 			0:用户没有绑定邮箱<br>
	 * 			
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	public ResultInfo sendEmail(long userId, long supervisorId, String title, String content) {
		ResultInfo result = new ResultInfo();
		
		t_user_info user = userInfoService.findUserInfoByUserId(userId);
		if (user == null) {
			result.code = -1;
			result.msg = "用户不存在!";
			
			return result;
		}
		
		if(StringUtils.isBlank(user.email)){
			result.code = 0;
			result.msg = "该用户还没有绑定邮箱!";
			
			return result;
		}
		
		result = sendEmail(user.email, supervisorId, title, content);
		
		return result;
	}
	
	/**
	 * 发送短信任务
	 * 
	 * @description 定时任务，根据t_sms_sending发送邮件给用户，并插入一条记录到t_sms表中<br>
	 * 				同时为了避免业务量过大，一次性最多发送<strong> 30 </strong>条
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public void sendSMSTask() {
		List<t_sms_sending> smsSendings = smsSendingDao.queryLastUnsended(50);
		
		if (smsSendings == null || smsSendings.size() == 0) {
			return;
		}
		
		Date send_time = new Date();
		for (t_sms_sending smsSending : smsSendings) {
			JPAUtil.transactionBegin();
			Logger.info("发送短息的手机号：【%s】", smsSending.mobile);
			boolean flag = smsJyCountService.isSend(smsSending.mobile, smsSending.maxCount);
			if (!flag) {
				continue;
			}
			boolean isSended = this.sendSMS(smsSending.mobile, 0L, smsSending.content, smsSending.authorization,smsSending.maxCount);
			if (isSended) {
				smsSending.is_send = true;
				smsSending.try_times += 1;
				smsSending.send_time = send_time;
				smsSendingDao.save(smsSending);
				smsJyCountService.updateNoticeSmsCount(smsSending.maxCount, smsSending.mobile);
			}else {
				smsSending.is_send = false;
				smsSending.try_times += 1;
				smsSending.send_time = send_time;
				smsSendingDao.save(smsSending);
			}
			JPAUtil.transactionCommit();
		}
	}

	/**
	 * 群发短信任务
	 * 
	 * @description 定时任务，根据t_mass_sms_sending发送邮件给用户，并插入一条记录到t_mass_sms表中<br>
	 * 				同时为了避免业务量过大，一次性最多发送<strong> 30 </strong>条
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public void sendMassSMSTask() {
		List<t_mass_sms_sending> massSmsSendings = massSmsSendingDao.queryLastUnsendedMassSms(100);
		if (massSmsSendings == null || massSmsSendings.size() == 0) {

			return;
		}
		
		Date send_time = new Date();
		for (t_mass_sms_sending massSending : massSmsSendings) {
			JPAUtil.transactionBegin();
			boolean isSended = this.sendSMSMass(massSending.mobile, massSending.content);
			if (isSended) {
				massSending.is_send = true;
				massSending.try_times += 1;
				massSending.send_time = send_time;
				massSmsSendingDao.save(massSending);
			}else {
				massSending.is_send = false;
				massSending.try_times += 1;
				massSending.send_time = send_time;
				massSmsSendingDao.save(massSending);
			}
			JPAUtil.transactionCommit();
		}
	}

	/**
	 * 发送邮件任务
	 * 
	 * @description 定时任务，根据t_email_sending发送邮件给用户，并插入一条记录到t_email表中<br>
	 * 				同时为了避免业务量过大，一次性最多发送<strong> 30 </strong>条
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public void sendEmailTask() {
		ResultInfo result = null;
		
		List<t_email_sending> emailSendings = emailSendingDao.queryLastUnsended(40);;
		if (emailSendings == null || emailSendings.size() == 0) {

			return;
		}
		
		Date send_time = new Date();
		
		for (t_email_sending emailSending : emailSendings) {
			JPAUtil.transactionBegin();
			result = sendEmail(emailSending.email, 0l, emailSending.title, emailSending.content);
			if (result.code == 1) {
				emailSending.is_send = true;
				emailSending.try_times += 1;
				emailSending.send_time = send_time;
				emailSendingDao.save(emailSending);
			} else {
				emailSending.is_send = false;
				emailSending.try_times += 1;
				emailSending.send_time = send_time;
				emailSendingDao.save(emailSending);
			}
			JPAUtil.transactionCommit();
		}
	}
	
	/**
	 * 群发邮件任务
	 * 
	 * @description 定时任务，根据t_mass_email_sending发送邮件给用户，并插入一条记录到t_mass_email表中<br>
	 * 				同时为了避免业务量过大，一次性最多发送<strong> 40 </strong>条
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public void sendMassEmailTask() {
		ResultInfo result = null;
		
		List<t_mass_email_sending> massEmailSendings = massEmailSendingDao.queryLastUnsendedMassEmail(40);;
		if (massEmailSendings == null || massEmailSendings.size() == 0) {

			return;
		}
		
		Date send_time = new Date();
		
		for (t_mass_email_sending massEmail : massEmailSendings) {
			JPAUtil.transactionBegin();
			result = sendEmailMass(massEmail.email, massEmail.title, massEmail.content);
			if (result.code == 1) {
				massEmail.is_send = true;
				massEmail.try_times += 1;
				massEmail.send_time = send_time;
				massEmailSendingDao.save(massEmail);
			} else {
				massEmail.is_send = false;
				massEmail.try_times += 1;
				massEmail.send_time = send_time;
				massEmailSendingDao.save(massEmail);
			}
			JPAUtil.transactionCommit();
		}
	}

	/**
	 * 更改绑定邮箱(发送新的绑定邮件给新绑定的邮箱)
	 *
	 * @param email 更改后的邮箱地址
	 * @param userName 用户的名称
	 * @param bindEmailUrl 绑定邮箱的地址
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public ResultInfo sendReBindEmail(String email,String user_name,String bind_email_Url) {
		ResultInfo result = new ResultInfo();
		
		t_template_notice template = findByNoticeTypeAScene(NoticeScene.BIND_EMAIL, NoticeType.EMAIL);
		if (template == null) {
			result.code = -1;
			result.msg = "模板不存在";
			
			return result;
		}
		
		String bind_email_button = "<a href=\""+ bind_email_Url +"\" >确定</a>";
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("user_name", user_name);
		param.put("bind_email_button", bind_email_button);
		param.put("bind_email_Url", bind_email_Url);
		String title = NoticeScene.BIND_EMAIL.value;
		String content = StrUtil.replaceByMap(template.content,param);

		String emailWebsite = settingService.findSettingValueByKey(SettingKey.EMAIL_WEBSITE);
		String mailAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_ACCOUNT);
		String mailPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_PASSWORD);
		
		boolean flag = EmailUtil.sendHtmlEmail(emailWebsite, mailAccount, mailPassword, email, title, content);
		
		if( !flag ){
			result.code = -2;
			result.msg = "邮件发送失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "邮件发送成功!";
		
		return result;
	}
	
	/**
	 * 删除所有已经发送的临时短信数据
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月23日
	 */
	public void deleteSmsSending() {
		smsSendingDao.deleteSended();
		massSmsSendingDao.deleteMassSended();
	}


	/**
	 * 删除所有已经发送的临时邮件数据
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月23日
	 */
	public void deleteEmailSending() {
		emailSendingDao.deleteSended();
		massEmailSendingDao.deleteMassSended();
	}
	
	/**
	 * 删除某个用户的某条站内信
	 *
	 * @param userId 用户的id
	 * @param msgId 站内信(msg)的id【不是t_message_user的id，而是message_id】
	 * @return obj中显示的该消息的是否已读
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public ResultInfo deleteUserMsg(long userId, long msgId) {
		ResultInfo result = new ResultInfo();
		t_message_user messageUser = messageUserDao.findByColumn(" user_id=? AND message_id=? ", userId,msgId);
		result.obj = messageUser.is_read;
		boolean isDeleted = messageUserDao.deleteUserMsg(userId,msgId);
		if (!isDeleted) {
			result.code = -1;
			result.msg = "删除失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "消息发送成功";
		return result;
	}
	
	
	/**
	 * 添加或者更新用户在某个场景下的消息接收设置
	 * 
	 * @description 如果用户在某个场景下已经设置了是否接收，则更新数据<br>如果用户在该场景下没有设置，则添加数据
	 *
	 * @param userId 用户的id
	 * @param scene 场景
	 * @param type 消息的类型(sms，msg,email)
	 * @param flag true表示接收，false表示不接收
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public boolean saveOrUpdateUserNoticeSetting(long userId, NoticeScene scene, NoticeType type, boolean flag) {
		List<t_notice_setting_user> settings = noticeSettingDao.findListByColumn("user_id=?1 and scene=?2", userId,scene.code);
		if (settings == null || settings.size() == 0) {
			//不存在,添加数据
			t_notice_setting_user setting = new t_notice_setting_user();
			setting.time = new Date();
			setting.user_id = userId;
			setting.setScene(scene);
			
			if( type.equals(NoticeType.SMS) ){
				setting.sms = flag;
			} 
			
			if(type.equals(NoticeType.MSG)){
				setting.msg = flag;
			}
			
			if(type.equals(NoticeType.EMAIL)){
				setting.email = flag;
			}
			
			return noticeSettingDao.save(setting);
		} else {
			//存在更新
			t_notice_setting_user setting = settings.get(0);
			if( type.equals(NoticeType.SMS) ){
				setting.sms = flag;
			} 
			if(type.equals(NoticeType.MSG)){
				setting.msg = flag;
			}
			if(type.equals(NoticeType.EMAIL)){
				setting.email = flag;
			}
			
			return noticeSettingDao.save(setting);
		}
	}

	/**
	 * 更新消息模板的内容
	 *
	 * @param templateId 消息模板的id
	 * @param content 模板更新后的内容
	 * @return 
	 * 			
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public ResultInfo updateTemplateNotice(long templateId, String content) {
		ResultInfo result = new ResultInfo();
		
		t_template_notice template = templateNoticeDao.findByID(templateId);
		if (template == null) {
			result.code = ResultInfo.ERROR_404;
			result.msg = "模板不存在!";
			
			return result;
		}
		
		template.content= content;
		
		boolean flag = templateNoticeDao.save(template);
		if( !flag ){
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "消息模板更新不成功!";
			
			return result;
		} else {
			addAFlushCache();
			
			result.code = 1;
			result.msg = "消息模板更新成功!";
			
			return result;
		}
	}
	
	
	/**
	 * 标记某个用户某条站内信为已读
	 *
	 * @param userId 用户的id
	 * @param msgId 站内信(msg)的id【不是t_message_user的id，而是message_id】
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public boolean updateUserMsgStatus(long userId, long msgId) {
		t_message_user userMsg = messageUserDao.findByColumn("user_id=? AND message_id=?", userId,msgId);
		if (userMsg == null) {
			return false;
		}

		userMsg.is_read = true;
		return messageUserDao.save(userMsg);
	}

	/**
	 * 标记某个用户所有站内信为已读
	 *
	 * @param userId 用户的id
	 * @return
	 *
	 * @author songjia
	 * @createDate 2016年03月29日
	 */
	public int updateUserAllMsgStatus(long userId) {
		String sql = "update t_message_user set is_read = true where user_id = :userId and is_read = false";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		return messageUserDao.updateBySQL(sql, param);
	}


	/**
	 * 查询用户在某个场景下的消息接收设置
	 *
	 * @param userId 用户的id
	 * @param scene 场景
	 * @return 不会出现为null的情况(但是可能出现id为null的情况)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public t_notice_setting_user findUserNoticeSetting(long userId,NoticeScene scene) {
		t_notice_setting_user settings = null;
		
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			settings = noticeSettingDao.findByColumn("user_id=? and scene=?", userId, scene.code);
			if (settings != null) {
				return settings;
			}

			settings = new t_notice_setting_user();
			settings.user_id = userId;
			settings.setScene(scene);

			return settings;
		} else {
			settings = new t_notice_setting_user();
			settings.user_id = userId;
			settings.setScene(scene);
			settings.sms = scene.sms;
			settings.msg = scene.msg;
			settings.email = scene.email;
			
			return settings;
		}
	}

	/**
	 * 根据templateid查找对应的模板
	 *
	 * @param templateId 消息模板的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public t_template_notice findByTemplateId(long templateId) {
		List<t_template_notice> templates = this.getCache();
		if (templates == null || templates.size() == 0) {
			
			return null;
		}
		
		t_template_notice temp = null;
		for (t_template_notice template : templates) {
			if (template.id.longValue() == templateId) {
				temp = template;
			}
		}
		
		return temp;
	}
	
	/**
	 * 统计某个用户未读消息(站内信)的数量
	 *
	 * @param userId 用户的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public int countUserUnreadMSGs(long userId) {
		int count = messageUserDao.countByColumn("user_id=? AND is_read=?", userId,false);
		return count;
	}

	/**
	 * 查询用户在所有可设置场景下的消息接收设置
	 *
	 * @description 可配置的消息场景指的是NoticeScene枚举中maskable值为true的场景
	 * 
	 * @param userId 用户的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public List<t_notice_setting_user> queryAllNoticSettingsByUser(long userId) {
		List<t_notice_setting_user> settings = new ArrayList<t_notice_setting_user>();
		List<NoticeScene> scenes = NoticeScene.getMaskableScenes();
		
		for (NoticeScene scene : scenes) {
			t_notice_setting_user setting = this.findUserNoticeSetting(userId,scene);
			settings.add(setting);
		}
		
		return settings;
	}
	
	/**
	 * 根据场景查找该场景对应的模板
	 *
	 * @param scene 消息场景
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public List<t_template_notice> queryTemplatesByScene(NoticeScene scene) {
		List< t_template_notice> list = null;
		
		List<t_template_notice> templates = this.getCache();
		if (templates == null || templates.size() == 0) {
			
			return list;
		}
		
		list  = new ArrayList<t_template_notice>();
		for (t_template_notice template : templates) {
			if (scene.equals(template.getScene())) {
				list.add(template);
			}
		}
		
		return list;
	}

	/**
	 * 分页查询某个用户的所有的消息
	 *
	 * @param userId 用户的id
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的数据量
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月26日
	 */
	public PageBean<UserMessage> pageOfAllUserMessages( int currPage,int pageSize,long userId) {
		String countSQL = "SELECT count(m.id) FROM t_message m INNER JOIN t_message_user mu ON mu.message_id = m.id WHERE mu.user_id=:user_id";
		String querySQL = "SELECT m.id as id,m.supervisor_id as supervisor_id,m.time as time,m.title as title,m.content as content,mu.user_id as user_id,mu.is_read as is_read FROM t_message m INNER JOIN t_message_user mu ON mu.message_id = m.id WHERE mu.user_id=:user_id ORDER BY m.id DESC";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		
		PageBean<UserMessage> page = messageDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL,UserMessage.class,condition);
		
		return page;
	}

	private t_template_notice findByNoticeTypeAScene(NoticeScene scene,NoticeType type){
		t_template_notice temp = null;
		
		List<t_template_notice> templates = this.getCache();
		if (templates == null || templates.size() == 0) {
			
			return null;
		}
		
		for (t_template_notice template : templates) {
			if (scene.equals(template.getScene()) && type.equals(template.getType())) {
				temp = template;
			}
		}
		
		return temp;
	}
	
	private boolean sendSMS(String mobile,long supervisorId, String content) {
		
		//String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		//String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);
		try {
			/* 发送短信验证码 */
			if(ConfConst.IS_SMS_REALSEND){
				//EimsSMS.send(smsAccount, smsPassword, content, mobile);
				//创蓝短息接口
				//SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, "30min", "", true);
				
				//焦云短息接口
				/*String result = JYSMSUtil.sendPost(content, "http://api.cloudfoci.com/sms.php");
				JSONObject jsonStr = JSONObject.fromObject(result);
				String code = jsonStr.getString("code");
				if (!code.equals("100")) {
					LoggerUtil.error(false,"管理员发送短信失败");
					
					return false;
				}*/
			}
		} catch (Exception e) {
			LoggerUtil.error(false, e, "管理员发送短信失败:【mobile:%s,content:%s】", mobile,content);
			
			return false;
		}
		t_sms sms = new t_sms();
		sms.time = new Date();
		sms.supervisor_id = supervisorId;
		sms.mobile = mobile;
		sms.content = content;
		
		boolean flag = smsDao.save(sms);
		
		return flag;
		
	}
	
	/**
	 * 发送短信(群发短信用到 )
	 * 
	 * @param supervisorId
	 * @param mobile
	 * @param content
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月7日
	 *
	 */
	private boolean sendSMSMass(String mobile,String content) {
		
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);
		try {
			/* 系统定时器是否发送短信给用户 */
			if(ConfConst.IS_SMS_REALSEND){
				//EimsSMS.send(smsAccount, smsPassword, content, mobile);
				SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, "30min", "", true);

			}
		} catch (Exception e) {
			LoggerUtil.error(false, e, "管理员发送短信失败:【mobile:%s,content:%s】", mobile,content);
			
			return false;
		}
		
		return true;
	}
	/**
	 * author:lihuijun
	 * date:2017-02-09
	 * @param mobile
	 * @param content
	 * @return
	 */
	public boolean sendMsg(String mobile,String content){
		return sendSMSMass( mobile,content);
	}
	
	private ResultInfo sendEmail(String toEmail, long supervisorId, String title, String content) {
		ResultInfo result = new ResultInfo();
		
		String emailWebsite = settingService.findSettingValueByKey(SettingKey.EMAIL_WEBSITE);
		String mailAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_ACCOUNT);
		String mailPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_PASSWORD);
		
		boolean flag = EmailUtil.sendHtmlEmail(emailWebsite, mailAccount, mailPassword, toEmail, title, content);
		if( !flag ){
			result.code = -2;
			result.msg = "邮件发送失败!";
			
			return result;
		}
		
		t_email email = new t_email();
		email.time = new Date();
		email.supervisor_id = supervisorId;
		email.email = toEmail;
		email.title = title;
		email.content = content;
		
		if ( !emailDao.save(email) ) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "保存邮件信息时抛异常!";
			
			return result;
		} else {
			result.code = 1;
			result.msg = "邮件发送成功!";
			
			return result;
		}
	}
	
	/**
	 * 群发邮件用到 
	 * 
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月7日
	 *
	 */
	private ResultInfo sendEmailMass(String toEmail, String title, String content) {
		ResultInfo result = new ResultInfo();
		
		String emailWebsite = settingService.findSettingValueByKey(SettingKey.EMAIL_WEBSITE);
		String mailAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_ACCOUNT);
		String mailPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_PASSWORD);
		
		boolean flag = EmailUtil.sendHtmlEmail(emailWebsite, mailAccount, mailPassword, toEmail, title, content);
		if( !flag ){
			result.code = -2;
			result.msg = "邮件发送失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "邮件发送成功!";
			
		return result;
		
	}
	
	@Override
	public void addAFlushCache() {
		List<t_template_notice> templates = templateNoticeDao.findAll();
		Cache.safeSet(t_template_notice.class.getName(), templates, Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public List<t_template_notice> getCache() {
		List<t_template_notice> templates = (List<t_template_notice>) Cache.get(t_template_notice.class.getName());
		if (templates == null || templates.size() == 0) {
			
			addAFlushCache();
			templates = (List<t_template_notice>) Cache.get(t_template_notice.class.getName());
		}
		
		return templates;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(t_template_notice.class.getName());
	}

	
	/**
	 * 用户未读消息总数查询
	 *
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月7日
	 */
	public int countUnreadMsg(long userId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		return messageDao.countBySQL("select count(id) from t_message_user where user_id = :userId and is_read = false", condition);
	}
	
	public boolean sendSysNotice(long userId, NoticeScene scene, Map<String, Object> sceneParame,JYSMSModel model) {
		//将double和Date类型转换成指定的格式数据类型
		Map<String, String> param_format = StrUtil.mapToMap(sceneParame);
		t_user user = t_user.findById(userId);
		//获取用户在某个场景下的消息发送状况
		t_notice_setting_user userSetting = this.findUserNoticeSetting(userId, scene);
		
		//场景的标题
		String title = scene.value;
		
		//该场景在数据库中的模板
		List<t_template_notice> templates = this.queryTemplatesByScene(scene);
		
		if (templates == null || templates.size() == 0) {
			return false;
		}
		boolean flag = true;

		for (t_template_notice template : templates) {
			NoticeType type = template.getType();
			/*if (type.equals(NoticeType.SMS)) {
				if (userSetting.sms) {
					//发送短信
					String content = StrUtil.replaceByMap(template.content,param_format);
					if (!sendSysSMS(userId, content)) {
						flag = false;
						LoggerUtil.info(false, "系统短信发送失败");
						break;
					}

				}
			} else */
			if (type.equals(NoticeType.MSG)) {
				if (userSetting.msg) {
					//发送站内信
					String content = StrUtil.replaceByMap(template.content, param_format);
					if (!sendMsg(userId, 0L, title, content)) {
						flag = false;
						LoggerUtil.info(false, "系统站内消息发送失败");
						break;
					}
				}
			} else if (type.equals(NoticeType.EMAIL)) {
				if (userSetting.email) {
					//发送邮件
					String content = StrUtil.replaceByMap(template.content, param_format);
					ResultInfo result = sendSysEmail(userId, title, content);
					if (result.code < 0) {
						flag = false;
						LoggerUtil.info(false, "系统邮件发送失败");
						break;
					}
				}
			}
		}
		if (model != null) {
			if (model.flag) {
				String content = JYSMSUtil.getMessageVaule(user.mobile, model.tplId, param_format);
				String authorization = JYSMSUtil.getMessageVauleJson(user.mobile, model.tplId, param_format);
				Integer count = smsJyCountService.judgeMaxCount(model.tplId);
				
				if (!sendSysSMS(userId, content,authorization,count)) {
					flag = false;
					LoggerUtil.info(false, "系统短信发送失败");
				}
			}
		}
		return flag;
	}
	/**
	 * 焦云发送短息(群发)
	 * @author guoShiJie
	 * @createDate 2018.7.16
	 * */
	public boolean sendJYMess (String mobile, Map<String,String> content ,JYSMSModel jymodel) {
		boolean flag = true;
		String authorization = null;
		try {
			Map<String,Object> map = new HashMap<String,Object>();
			if (content != null && content.size() > 0) {
				for (String element : content.keySet()) {
					map.put(element, content.get(element));
				}
			}
			/*if (ConfConst.IS_SMS_REALSEND) {
				if (jymodel.flag) {
					flag = JYSMSUtil.sendMessage(mobile, jymodel.tplId, content);
				}
			}*/
			if (jymodel != null) {
				if (jymodel.flag) {
					String con = JYSMSUtil.getMessageVaule(mobile, jymodel.tplId, content);
					authorization =  JYSMSUtil.getMessageVauleJson(mobile, jymodel.tplId, content);
					Integer count = smsJyCountService.judgeMaxCount(jymodel.tplId);
					if (!sendSysSMS(mobile, con,authorization,count)) {
						flag = false;
					}
				}
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**(焦云)
	 * 系统发送短信给用户(非实时)
	 * @description 非实时，只是往t_sms_sending表中插入一条数据，然后定时器会根据这张表中的数据发送真正的短信给用户
	 *
	 * @param userId 接收短信的用户的id
	 * @param content 短信的内容
	 * @param authorization api认证
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年7月19日
	 */
	public boolean sendSysSMS(long userId, String content,String authorization,Integer count) {
		t_user user = userService.findByID(userId);
		
		t_sms_sending smsSending = new t_sms_sending();
		smsSending.time = new Date();
		smsSending.mobile = user.mobile;
		smsSending.content = content;
		smsSending.authorization = authorization;
		smsSending.is_send = false;
		smsSending.try_times = 0;
		smsSending.maxCount = count;
		boolean flag = smsSendingDao.save(smsSending);
		
		return flag;
	}
	
	public boolean sendSysSMS(String mobile, String content,String authorization,Integer count) {
		
		t_sms_sending smsSending = new t_sms_sending();
		smsSending.time = new Date();
		smsSending.mobile = mobile;
		smsSending.content = content;
		smsSending.authorization = authorization;
		smsSending.is_send = false;
		smsSending.try_times = 0;
		smsSending.maxCount = count;
		boolean flag = smsSendingDao.save(smsSending);
		
		return flag;
	}
	/**
	 * 焦云发送短信
	 * @description 发送短信，并向t_sms表中添加数据
	 * @author guoShiJie
	 * @createDate 2018.7.19
	 * */
	private boolean sendSMS(String mobile,long supervisorId, String content,String authorization,Integer type) {
		
		try {
			/* 发送短信验证码 */
			if(ConfConst.IS_SMS_REALSEND){
				
				//焦云短息接口
				String result = JYSMSUtil.sendPost(content, "http://121.201.72.233:8081/sms.php",authorization);
				JSONObject jsonStr = JSONObject.fromObject(result);
				String code = jsonStr.getString("code");
				if (!code.equals("100")) {
					LoggerUtil.error(false,"管理员发送短信失败");
					
					return false;
				}
			}
		} catch (Exception e) {
			LoggerUtil.error(false, e, "管理员发送短信失败:【mobile:%s,content:%s,authorization:%s】", mobile,content,authorization);
			
			return false;
		}
		t_sms sms = new t_sms();
		sms.time = new Date();
		sms.supervisor_id = supervisorId;
		sms.mobile = mobile;
		sms.content = content;
		sms.authorization = authorization;
		sms.maxCount = type;
		boolean flag = smsDao.save(sms);
		
		return flag;
		
	}
}
