package controllers.front;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.common.entity.t_activity_prize;
import models.common.entity.t_information;
import models.common.entity.t_participation;
import models.common.entity.t_select_photos;
import models.common.entity.t_select_theme;
import models.common.entity.t_user;
import models.common.entity.t_user_vote;
import models.common.entity.t_vote_activity;

import org.apache.commons.lang.StringUtils;

import common.utils.StrUtil;
import play.cache.Cache;

import com.shove.Convert;
import com.shove.security.Encrypt;

import play.libs.Codec;
import play.mvc.Scope.Session;
import common.annotation.SimulatedCheck;
import common.constants.ConfConst;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.captcha.CaptchaUtil;
import common.utils.file.FileUtil;
import services.common.ActivityPrizeService;
import services.common.InformationService;
import services.common.ParticipationService;
import services.common.SelectPhotosService;
import services.common.SelectThemeService;
import services.common.SupervisorService;
import services.common.UserService;
import services.common.UserVoteService;
import services.common.VoteActivityService;
import services.core.InvestService;
import controllers.common.FrontBaseController;

/**
 * 前台-活动控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2016年5月17日
 */
public class ActivityCtrl extends FrontBaseController {
	
	protected static VoteActivityService voteActivityService = Factory.getService(VoteActivityService.class);
	
	protected static ParticipationService participationService = Factory.getService(ParticipationService.class);
	
	protected static UserVoteService userVoteService = Factory.getService(UserVoteService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ActivityPrizeService activityPrizeService = Factory.getService(ActivityPrizeService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static  SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected static SelectThemeService selectThemeService = Factory.getService(SelectThemeService.class);
	
	protected static SelectPhotosService selectPhotosService = Factory.getService(SelectPhotosService.class);


	/**
	 *
	 * @description 前台-活动首页
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void frontActivityPre() {
		String currPageStr = params.get("currPage");
		String searchTitle = params.get("searchTitle");
		renderArgs.put("searchTitle",searchTitle);
		int currPage = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if(searchTitle==null) {
			searchTitle = "";
		}
		
		//排序
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0-上传时间；3-票数升序；4-票数降序
		if (orderType != 3 && orderType != 4 ) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		Boolean flag = false;
		
		/* 访问量加1 */
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);		
		if(voteActivity == null) {
			flag = true;
			FrontHomeCtrl.frontHomePre();
		}
		voteActivity.visit_num = voteActivity.visit_num+1;
		voteActivity.save();
		
		/* 报名人数 */
		int partNum = participationService.queryTotalParticipations(voteActivity.id);
		
		/* 投票总数 */
		int voteNum = userVoteService.queryTotalUserVotes(voteActivity.id);
		
		/* 投票排行榜 */
		List<t_participation> voteRankList = participationService.queryVoteRankList(voteActivity.id);
		
		/* 相册列表 */
		PageBean<t_participation> pageBean = participationService.queryParticipationsByPage(currPage, 9, orderType, voteActivity.id, searchTitle);
		
		/* 最新投票 */
		t_user_vote votes = userVoteService.queryVoteByTime(voteActivity.id);
		
		render(partNum, voteNum, voteRankList, pageBean, flag, voteActivity, votes);
	}
	
	/**
	 *
	 * @description 前台-活动首页-分页列表
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void activityListPre() {
		String currPageStr = params.get("currPage");
		String searchTitle = params.get("searchTitle");
		int currPage = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if(searchTitle==null) {
			searchTitle = "";
		}
		
		//排序
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0-上传时间；3-票数升序；4-票数降序
		if (orderType != 3 && orderType != 4 ) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		Boolean flag = false;
		
		
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);		
		if(voteActivity == null) {
			flag = true;
			render(flag);
		}
		
		/* 相册列表 */
		PageBean<t_participation> pageBean = participationService.queryParticipationsByPage(currPage, 9, orderType, voteActivity.id, searchTitle);
		
		render(flag, pageBean);
	}
	
	/**
	 *
	 * @description 前台-活动首页
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void activityIndexPre() {
		String currPageStr = params.get("currPage");
		String searchTitle = params.get("searchTitle");
		int currPage = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if(searchTitle==null) {
			searchTitle = "";
		}
		
		//排序
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0-上传时间；3-票数升序；4-票数降序
		if (orderType != 3 && orderType != 4 ) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		Boolean flag = false;
		
		/* 访问量加1 */
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);		
		if(voteActivity == null) {
			flag = true;
			render(flag);
		}
		voteActivity.visit_num = voteActivity.visit_num+1;
		voteActivity.save();
		
		/* 报名人数 */
		int partNum = participationService.queryTotalParticipations(voteActivity.id);
		
		/* 投票总数 */
		int voteNum = userVoteService.queryTotalUserVotes(voteActivity.id);
		
		/* 投票排行榜 */
		List<t_participation> voteRankList = participationService.queryVoteRankList(voteActivity.id);
		
		/* 相册列表 */
		PageBean<t_participation> pageBean = participationService.queryParticipationsByPage(currPage, 9, orderType, voteActivity.id, searchTitle);
		
		/* 最新投票 */
		t_user_vote votes = userVoteService.queryVoteByTime(voteActivity.id);
		
		render(partNum, voteNum, voteRankList, pageBean, flag, voteActivity,votes);
	}
	
	/**
	 *
	 * @description 前台-活动介绍
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void activityIntroductionPre() {
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);
		
		render(voteActivity);
	}

	/**
	 *
	 * @description 前台-我的信息
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void myInformationPre() {
		/* 判断用户是否登陆 */
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		
		t_user user = userService.findByID(userId);
		
		Boolean flag = false;
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);
		
		t_participation parts = participationService.queryTotalParticipationsByUserId(voteActivity.id, userId);
		if(parts == null) {
			flag = true;
			render(flag, user, voteActivity);
		}
		
		render(flag, parts, user, voteActivity);
	}

	/**
	 *
	 * @description 前台-中奖公布
	 *
	 * @author liuyang
	 * @createDate 2016年5月22日
	 */
	public static void awardPublishPre() {
		/* 判断用户是否登陆 */
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		t_user user = userService.findByID(userId);
		
		boolean flag = false;
		int zhi = 1;
		
		//活动对象
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);
		
		t_participation parts = participationService.queryTotalParticipationsByUserId(voteActivity.id, userId);
		if(parts == null) {
			flag = true;
			render(user, flag);
		}
		int zhis =parts.getStatus().code;
		Date newTime = new Date();
		if(newTime.getTime()>=voteActivity.end_time.getTime() && zhis==1) {
			zhi = 2;
			List<t_activity_prize> activityPrizes = new ArrayList<>();
			List<t_activity_prize> prizes = activityPrizeService.queryActivityPrize(voteActivity.id);
			/*for (int i = 0; i < prizes.size(); i++) {
				t_activity_prize pri = prizes.get(i);
				if(pri.floor_num<=parts.orderNum && pri.upper_num>=parts.orderNum) {
					activityPrizes.add(pri);
				}else if(pri.floor_num<=parts.orderNum && pri.upper_num==-1) {
					activityPrizes.add(pri);
				}else if(pri.upper_num == -1 && pri.floor_num == -1) {
					activityPrizes.add(pri);
				}
			}*/
			for (int i = 0; i < prizes.size(); i++) {
				t_activity_prize pri = prizes.get(i);
				if(pri.floor_num==1999 && parts.poll_num>=1999) {
					activityPrizes.add(pri);
				}else if(pri.floor_num==1314 && parts.poll_num>=1314 && parts.poll_num<1999) {
					activityPrizes.add(pri);
				}else if(pri.floor_num==999 && parts.poll_num>=999 && parts.poll_num<1314) {
					activityPrizes.add(pri);
				}else if(pri.floor_num==521 && parts.poll_num>=521 && parts.poll_num<999) {
					activityPrizes.add(pri);
				}else if(pri.floor_num==215 && parts.poll_num>=215 && parts.poll_num<521) {
					activityPrizes.add(pri);
				}
			}
			render(user, flag, zhi, activityPrizes, parts);
		}else if(parts.getStatus().code!=1) {
			zhi = 3;
			render(user, flag, zhi);
		}else {
			render(user, flag, zhi);
		}
	}
	
	/**
	 * 上传广告图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author liuyang
	 * @createDate 2017年5月22日
	 */
	@SimulatedCheck
	public static void updatePhoto(File imgFile, String fileName){
		/* 判断用户是否登陆 */
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadFrontImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		String filename = (String) fileInfo.get("staticFileName");
		
		result.obj = filename;
		
		renderJSON(result);
	}
	
	/**
	 * 前台-参加活动-保存修改照片信息
	 */
	public static void addUsers(String imageUrl, boolean flag, int status, String name, String slogan, String description, long activityId, long Ids) {
		/* 判断用户是否登陆 */
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		
		t_participation parts = new t_participation();
		if(flag == false) {
			parts = participationService.findByID(Ids);
		}			
		parts.img_url = imageUrl;
		parts.status = 0;
		parts.description = description;
		parts.name = name;
		parts.slogan = slogan;
		parts.user_id = userId;
		parts.poll_num = 0;
		parts.time = new Date();
		parts.activity_id = activityId;
		parts.save();		
		
		frontActivityPre();
	}
	
	/**
	 * 前台-首页-点击投票
	 * @param participationId
	 */
	public static void submitTouPiao(long participationId) {		
		long userId = getCurrUser().id;
		
		ResultInfo error = new ResultInfo();
		
		//得到活动对象
		t_vote_activity voteActivity = voteActivityService.queryVoteActivityByType(1);
		if(voteActivity == null){
			error.code = -1;
			error.msg = "失败";
			renderJSON(error);
		}
		int voteNum = voteActivity.vote_num;
		int nums = 0;
		
		Date newTime = new Date();
		if(newTime.getTime()<voteActivity.end_time.getTime()) {
			List<t_user_vote> votes = userVoteService.queryVoteByUserId(userId);
			t_user_vote vote = new t_user_vote();
			if(votes.size() == 0){
				vote.participation_id = participationId;
				vote.user_id = userId;
				vote.time = new Date();
				vote.activity_id = voteActivity.id;
				vote.save();
				participationService.updateByIds(participationId);
				error.code = 1;
				error.msg = "成功";
				renderJSON(error);
			}else {
				for (int i = 0; i < votes.size(); i++) {System.out.println(votes.size());
					t_user_vote vot = votes.get(i);
					if(vot.time.getYear()==newTime.getYear() && vot.time.getMonth()==newTime.getMonth() && vot.time.getDate()==newTime.getDate()) {
						nums++;
					}
				}
				
				if(voteNum>nums) {
					vote.participation_id = participationId;
					vote.user_id = userId;
					vote.time = new Date();
					vote.activity_id = voteActivity.id;
					vote.save();
					participationService.updateByIds(participationId);
					error.code = 1;
					error.msg = "成功";
					renderJSON(error);
				}else {
					error.code = -2;
					error.msg = "失败";
					renderJSON(error);
				}
			}
		}else {
			error.code = -1;
			error.msg = "失败";
			renderJSON(error);
		}
	}
	
	/**
	 * 注册页面
	 * 
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月5日
	 *
	 */
	public static void registerActivityPre(){
		/* 验证码 */
		String randomId = Codec.UUID();
		
		/* 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		/* 获取平台协议标题 */
		t_information platformRegister = informationService.findRegisterDeal();
		
		/*cps 邀请码 推广 */
		String recommendCode = Encrypt.decrypt3DES(params.get("recommendCode"), ConfConst.ENCRYPTION_KEY_DES);
		
		String extensionCode = params.get("extensionCode");
		
		render(randomId, platformRegister,recommendCode,extensionCode);
	}
	
	/**
	 * 用户注册
	 * @description 
	 * 
	 * @param mobile 手机号码
	 * @param password 密码
	 * @param confirmPassword 确认密码
	 * @param randomId 验证码密文
	 * @param code 验证码
	 * @param smsCode 短信验证码
	 * @param recommendCode 邀请码
	 * @param scene 场景
	 * @param readandagree 协议勾选
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年7月3日
	 */
	public static void registering(String mobile, String password, String randomId, 
			String code, String smsCode, String recommendCode, String scene, boolean readandagree,String extensionCode) {
		ResultInfo result = new ResultInfo();
		
		/* 回显数据 */
		flash.put("mobile", mobile);
		flash.put("code", code);
		flash.put("smsCode", smsCode);
		flash.put("recommendCode", recommendCode);
		flash.put("extensionCode", extensionCode);
		
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			registerActivityPre();
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号不符合规范");
			
			registerActivityPre();
		}
		
		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");
	
			registerActivityPre();
		}
		
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			registerActivityPre();
		}
		
		/* 根据randomid取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);
		
		/* 验证码失效 */
		if (RandCode == null) {
			flash.error("验证码失效");
			
			registerActivityPre();
		}
		
		/* 图形验证码验证 */
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");
	
				registerActivityPre();
			}
		}
		
		/* 短信验证码验证 */
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			registerActivityPre();
		}
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			registerActivityPre();
		}
		String codeStr = obj.toString();
		
		/** 短信验证码 验证 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				registerActivityPre();
			}
		}
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
	
			registerActivityPre();
		}
		
		int flagOfRecommendCode = 0;
		/* 验证邀请码 */
		if(StringUtils.isNotBlank(recommendCode)){
			
			if (StrUtil.isMobileNum(recommendCode)) {// CPS邀请码是用户的手机号
				if(common.constants.ModuleConst.EXT_CPS){ //CPS模块是否存在
					/* 判断手机号码是否存在 */
					flagOfRecommendCode = userService.isMobileExists(recommendCode) ? 1:-1;
				}
			} else {
				if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {// 财富圈邀请码
					service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
					ResultInfo result2 = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
					if (result2.code == 1) {
						flagOfRecommendCode = 2;
					}
				}
			}
			if (flagOfRecommendCode < 0) {
				flash.error("推广码不存在");
	
				registerActivityPre();
			}
		}
		
		long supervisorId =0;
		extensionCode = "T97";
		if(StringUtils.isNotBlank(extensionCode)){
			supervisorId = supervisorService.findSupervisorByExtension(extensionCode);		
			if(supervisorId<=0){
				flash.error("业务邀请码不存在");
	
				registerActivityPre();
			}
		}
		
		/* 协议是否勾选 */
		if(!readandagree){
			flash.error("协议未勾选");
	
			registerActivityPre();
		}
		
		/* 自动生成用户名 */
		String userName = userService.userNameFactory(mobile);
		
		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result =  userService.registering(userName, mobile, password, Client.PC, getIp(),supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			registerActivityPre();
		}else{
		
			//ext_redpacket 新用户红包数据生成 start
			if(ModuleConst.EXT_REDPACKET){
			
			t_user user = (t_user) result.obj;
			
			services.ext.redpacket.RedpacketService redpacketService = Factory.getService(services.ext.redpacket.RedpacketService.class);
			ResultInfo result2 = redpacketService.creatRedpacket(user.id);  //此处不能使用result
			if(result2.code < 1){
				LoggerUtil.info(true, "注册成功，生成红包相关数据时出错，数据回滚，%s", result2.msg);
				
				flash.error(result2.msg);
				registerActivityPre();
			} 
		}
			
			//ext_redpacket 老用户红包数据生成 start
			if(ModuleConst.EXT_REDPACKET){
				if (flagOfRecommendCode == 1){
					
					ResultInfo results = userService.findUserInfoByMobile(recommendCode);
	 				t_user user = (t_user) results.obj;
					services.ext.redpacket.RedpacketService redpacketService = Factory.getService(services.ext.redpacket.RedpacketService.class);
					ResultInfo result2 = redpacketService.creatRedpackets(user.id);  //此处不能使用result
					if(result2.code < 1){
						LoggerUtil.info(true, "注册成功，生成红包相关数据时出错，数据回滚，%s", result2.msg);
						
						flash.error(result2.msg);
						registerActivityPre();
					} 
				}				
			}

			
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					
					flash.error(result3.msg);
					registerActivityPre();
				}
			}
			//end
			
			// cps账户开户
			if(ModuleConst.EXT_CPS){
				t_user user = (t_user) result.obj;
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				
				ResultInfo result4 = null;
				if (flagOfRecommendCode == 1){
					result4 = cpsService.createCps(recommendCode, user.id);
				}else {
					result4 = cpsService.createCps(null, user.id);
				}
						
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					
					flash.error(result4.msg);
					registerActivityPre();
				}
			}
			//end
			
			// 财富圈账号数据创建
			if(ModuleConst.EXT_WEALTHCIRCLE){
				t_user user = (t_user) result.obj;
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result5 = null ; 
						
				if (flagOfRecommendCode == 2){
					result5 = wealthCircleService.creatWealthCircle(recommendCode, user.id);
				}else {
					result5 = wealthCircleService.creatWealthCircle(null, user.id);
				}
				if(result5.code < 1){
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);
					
					flash.error(result5.msg);
					registerActivityPre();
				}
			}
			//end
			
			/* 清除缓存中的验证码 */
			String encryString = Session.current().getId();
	    	Object cache = Cache.get(mobile + encryString + scene);
	    	if(cache != null){
	    		Cache.delete(mobile + encryString + scene);
	    		Cache.delete(mobile + scene);
	    	}
			
			registerSuccessPre();
		}
	}
	
	
	
	/**
	 * 注册成功页面
	 *
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月15日
	 */
	public static void registerSuccessPre() {
		
		render();
	}
	

	/**
	 * 前台-红包,加息券活动首页
	 *
	 *
	 * @author LiuPengwei
	 * @createDate 2017年8月1日
	 */
	
	public static void RegisterActivityPre() {
		
		/** 前台数据  */
		int is_statistics_show = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW), 0);
		renderArgs.put("is_statistics_show", is_statistics_show);
		
		render();
	}


	/**
	 * 前台-红包活动首页
	 *
	 *
	 * @author LiuPengwei
	 * @createDate 2017年8月1日
	 */
	
	public static void RedActivityPre() {
		
		/**  投资排行榜  */
	
		List<Map<String, Object>> queryActivityInvestList = investService.queryActivityInvestList();
		if (queryActivityInvestList != null && queryActivityInvestList.size() > 0) {
			renderArgs.put("ActivityInvestList", queryActivityInvestList);
		}
		
		render();
	}
	
	
	/**
	 * 前台-分享首页
	 *
	 *
	 * @author LiuPengwei
	 * @createDate 2018年1月4日
	 */
		
	public static void SharingPhotoIndexPre(long themeId) {
		//阅读次数增加
		selectThemeService.addReadCount(themeId);
		
		
		t_select_theme selectTheme = selectThemeService.findByID(themeId);
		
		render(themeId,selectTheme);
	}
	

	/**
	 * 前台-分享界面
	 *
	 *
	 * @author LiuPengwei
	 * @createDate 2018年1月4日
	 */
	
	public static void SharingPhotoPre(long themeId) {
				
		List<t_select_photos> selectPhotosList = selectPhotosService.findListByColumn("theme_id = ?", themeId);
		int count = selectPhotosList.size();
		
		t_select_theme selectTheme = selectThemeService.findByID(themeId);
		
		String firstUrl = selectPhotosList.get(0).image_url;
		
		render(selectPhotosList, count, selectTheme, firstUrl);
	}

}
