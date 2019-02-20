package com.shovesoft.sp2p.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.UserMessage;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_notice_setting_user;
import models.common.entity.t_template_notice;

import org.junit.Test;

import services.common.NoticeService;

import com.shove.gateway.sms.EimsSMS;
import com.shovesoft.sp2p.BaseUnit;

import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.EventDao;


public class NoticeServiceUnit extends BaseUnit {
	/** 日期格式:yyyyMMddHHmmss */
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	
	@Test
	public void test2(){
		
		EventDao eventDao = Factory.getDao(EventDao.class);
		String sql = "select * from t_event_supervisor where operation in (:operation)";
		Map<String, Object> condition = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		list.add(0);
		list.add(1);
		list.add(5);
		condition.put("operation", list);
		List<t_event_supervisor> lists = eventDao.findListBySQL(sql, condition);
		System.out.println(lists.size());
		
	}
	
	/**
	 * 测试发送站内消息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	@Test
	public void test_sendSysNotice(){
		
		Long user_id = 10L;
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", "lS13968574858");
		param.put("amount", 1000.00);
		param.put("balance", 1230.00);
		param.put("date", new Date());
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
//		boolean flag = noticeService.sendSysNotice(user_id, NoticeScene.REGISTER_SUCC, param);//只发短信
		boolean flag = noticeService.sendSysNotice(user_id, NoticeScene.RECHARGE_SUCC, param);//站内信，短信，邮件都发
//		boolean flag = noticeService.sendSysNotice(user_id, NoticeScene.BID_APPLAY_SUCC, param);//站内信，短信，邮件都发
		if(flag){
			System.out.println("发送成功");
		} else {
		
			System.out.println("发送失败");
		}
		
	}
	
	/**
	 * 短信发送测试
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月19日
	 */
	@Test
	public void test_sendSMS(){
		
		Long user_id = 10L;
		Long supervisor_id = 2L;
		String content = "测试的短信尼日欧诺个顺丰军绿色的疯狂谁的疯了快圣诞节发牢骚的饭局"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
		if (noticeService.sendSMS(user_id, supervisor_id, content)) {
			System.out.println("发送成功");
		} else {
			System.out.println("发送失败");
		}
		
	}
	
	/**
	 * 测试系统发送短信给用户
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	@Test
	public void test_sendSysSMS() {
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
		Long user_id = 10L;
		String content = "测试的短信尼日欧诺个顺丰军绿色的疯狂谁的疯了快圣诞节发牢骚的饭局"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		if (noticeService.sendSysSMS(user_id, content)) {
			System.out.println("发送成功");
		} else {
			System.out.println("发送失败");
		}
		
	}
	
	@Test
	public void test(){
		String smsAccount = "Test";
		String smsPassword = "23423409_0xdv_-o4";
		String mobile = "18823858481";
		String content = "测试的内容";
		String result = EimsSMS.send(smsAccount, smsPassword, content, mobile);
		System.out.println(result);
	}
	
	/**
	 * 测试管理员发送消息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月19日
	 */
	@Test
	public void test_sendMsg(){
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
		Long user_id = 2L;
		Long supervisor_id = 3L;
		String title = "标题:测试的消息"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		String content = "测试的消息尼日欧诺个顺丰军绿色的疯狂谁的疯了快圣诞节发牢骚的饭局"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		
		if (noticeService.sendMsg(user_id, supervisor_id, title, content)) {
			System.out.println("发送成功");
		} else {
			System.out.println("发送失败");
		}
	}
	
	
	/**
	 * 测试发送邮件给管理员
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	@Test
	public void test_sendEmail(){
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
		Long user_id = 28L;
		Long supervisor_id = 3L;
		String title = "标题:测试的邮件"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		String content = "测试的邮件尼日欧诺个顺丰军绿色的疯狂谁的疯了快圣诞节发牢骚的饭局"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		
		
		if (noticeService.sendEmail(user_id, supervisor_id, title, content).code > 1) {
			System.out.println("发送成功");
		} else {
			System.out.println("发送失败");
		}
		
	}
	
	/**
	 * 测试系统发送邮件给用户
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月21日
	 */
	@Test
	public void test_sendSysEmail(){
		NoticeService noticeService = Factory.getService(NoticeService.class);
		
		Long user_id = 29L;//()
		String title = "标题:测试的邮件"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		String content = "测试的邮件尼日欧诺个顺丰军绿色的疯狂谁的疯了快圣诞节发牢骚的饭局"+DateUtil.dateToString(new Date(), yyyyMMddHHmmss);
		
		ResultInfo result = null;
		result = noticeService.sendSysEmail(user_id, title, content);
		
		if (result.code > 0) {
			System.out.println("发送成功");
		} else if(result.code == 0) {
			System.out.println("用户还没有绑定邮箱");
		} else {
			System.out.println("发送失败");
		}
	}
	
	/**
	 * 测试"添加或者更新用户在某个场景下的邮件接收设置"
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	@Test
	public void test_saveOrUpdateUserNoticeSetting(){
		
		Long user_id = 2L;
		NoticeScene scene = NoticeScene.RECHARGE_SUCC;
		NoticeType type = NoticeType.SMS;
		boolean flag= false;
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		boolean flagOf = noticeService.saveOrUpdateUserNoticeSetting(user_id, scene, type, flag);
		if(flagOf){
			System.out.println("更新成功");
		} else {
			System.out.println("更新不成功");
		}
	}
	
	/**
	 * 查询用户在某个场景下的消息接收设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	@Test
	public void test_findUserNoticeSetting() {
		NoticeService noticeService = Factory.getService(NoticeService.class);
		t_notice_setting_user setting = noticeService.findUserNoticeSetting(2L, NoticeScene.RECHARGE_SUCC);
		System.out.println(setting);
	}
	
	/**
	 * 测试查询用户在所有场景下的消息接收设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	@Test
	public void test_queryAllNoticSettingsByUser(){
		NoticeService noticeService = Factory.getService(NoticeService.class);
		List<t_notice_setting_user> settings = noticeService.queryAllNoticSettingsByUser(2L);
		
		for(t_notice_setting_user setting : settings){
			System.out.println(setting);
		}
	}
	
	@Test
	public void test_queryTemplatesByScene() {
		try {
			NoticeService noticeService = Factory.getService(NoticeService.class);
			
			List<t_template_notice> templates = noticeService.queryTemplatesByScene(NoticeScene.BID_APPLAY_SUCC);
			System.out.println(templates.size());
		} catch (Exception e) {
			LoggerUtil.error(false, e, "");
		}

	}

	@Test
	public void test_sendReBindEmail(){
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		ResultInfo result = noticeService.sendReBindEmail("daizhengmiao@eims.com.cn","戴征淼", "http://192.168.4.57:9000/demo");
		
		System.out.println(result);
		
	}
	
	
	/**
	 * 测试:分页查询所有的消息(站内信)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	@Test
	public void test_pageOfAllUserMessages(){
	}
	/**
	 * 统计用户未读的站内信消息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	@Test
	public void test_countUserUnreadMSGs(){
		long userId = 10l;
		NoticeService noticeService = Factory.getService(NoticeService.class);
		int a = noticeService.countUserUnreadMSGs(userId);
		System.out.println(a);
	}
	
	/**
	 * 测试定时发送邮件任务(按照t_email_sending表中数据发送邮件给用户)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	@Test
	public void test_sendEmailTask(){
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendEmailTask();
		System.out.println("发哦少女玩车 ");
	}
	
	/**
	 * 测试定时发送短信任务(按照t_sms_sending表中数据发送邮件给用户)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	@Test
	public void test_sendSMSTask(){
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendSMSTask();
		System.out.println("发哦少女玩车 ");
	}
}
