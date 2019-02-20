package controllers.back.activity.shake;


import common.utils.Factory;
import common.utils.PageBean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.NEW;

import common.utils.Factory;

import controllers.common.BackBaseController;

import services.activity.shake.TurnAwardRecordService;

import daos.activity.shake.AwardTurnplateDao;
import models.activity.shake.entity.t_award_turnplate;
import models.activity.shake.entity.t_big_wheel;
import models.activity.shake.entity.t_turn_award_record;
import services.activity.shake.AwardTurnplateService;
import services.activity.shake.BigWheelService;
/**
 * 
 *
 * @ClassName: BigWheelActivityCtrl
 *
 * @description 大转盘活动后台控制器
 *
 * @author LiuHangjing
 * @createDate 2018年10月26日-下午2:01:20
 */

public class BigWheelActivityCtrl extends BackBaseController{
	
	private static TurnAwardRecordService turnAwardRecordService = Factory.getService(TurnAwardRecordService.class);
	
	private static AwardTurnplateService awardTurnplateService = Factory.getService(AwardTurnplateService.class);
	
	private static BigWheelService bigWheelService = Factory.getService(BigWheelService.class);
	/**
	 * 
	 * @Title: AwardManagerPre
	 * @description 跳转大转盘奖品管理列表显示页面（后台）
	 * void
	 * @author liuhangjing
	 * @createDate 2018年10月24日 下午2:50:16
	 */
	public static void awardManagerListPre(){
		
		List<t_award_turnplate> awardTurnplate =  awardTurnplateService.findAwardAll();
		render(awardTurnplate);
	}
	/**
	 * 
	 * @Title: editProb
	 * 
	 * @description 修改奖品概率(后台)
	 * @param id
	 * @param prob
	 * @return Integer    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-下午4:09:01
	 */
	public static Integer editProb(long id,BigDecimal prob){
	
		//通过id查询到当前对象
		t_award_turnplate awardTurnplate = awardTurnplateService.findById(id);
		Date time = new Date();
		if(awardTurnplate == null){
			flash.error("编辑失败，请重新操作");
			awardManagerListPre();
		}
		String probs = params.get("prob");
		BigDecimal dprobs = new BigDecimal(probs);
		//判断概率是否存在
		if(isExistProb(dprobs)){
			flash.error("概率不能重复,请重新输入");
			return 1;

		}else{
			
			awardTurnplate.setProb(dprobs);
		}
		awardTurnplate.setTime(time);
 		awardTurnplate.save();
 		flash.success("编辑成功！");
		return 0;
	}
	
	
	/**
	 * 
	 * @Title: isExistProb
	 * 
	 * @description  异步判断概率是否存在
	 * @param prob
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-上午10:07:51
	 */
	public static boolean isExistProb(BigDecimal prob){
		BigDecimal bigDecimal  = BigDecimal.valueOf(0L);
		if(prob.compareTo(bigDecimal)==0){
			return false;
		}
		t_award_turnplate award_turnplate = awardTurnplateService.findByProb(prob);
		/** 如果不存在返回false */
		if(award_turnplate==null){
			return false;
		}
		/** 如果存在返回true */
		return true;
	}
	/**
	 * 
	 * @Title: winningInfoPre
	 *
	 * @description 修改到期状态并显示所有中奖人信息
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页返回记录数
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月26日
	 */
	public static void winningInfoPre(int currPage, int pageSize){
		/** 修改到期状态  */
		turnAwardRecordService.updateEndStatus();
		/** 查询所有中奖人信息 */
        PageBean<t_turn_award_record> pageBean = turnAwardRecordService.listOfTurn(currPage, pageSize);
		render(pageBean);
	}
	/**
	 * 
	 * @Title: editGrant
	 * 
	 * @description 修改奖品发放状态 
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月8日-下午5:30:32
	 */
	public static void editGrant(long id){
		/**通过id查询到当前对象*/
		t_turn_award_record awardRecord = turnAwardRecordService.findById(id);
		if(awardRecord == null){
			flash.error("编辑失败，请重新操作");
			awardManagerListPre();
		}
		String is_grant = params.get("is_grant");
		awardRecord.is_grant = Integer.parseInt(is_grant);
		awardRecord.save();
		flash.success("编辑成功");
		winningInfoPre(1,10);
		
	}
	/**
	 * 
	 * @Title: activitySettingPre
	 * @description: 大转盘活动设置列表显示
	 *    
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:14:36
	 */
	public static void activitySettingPre(int currPage, int pageSize){
		PageBean<t_big_wheel> pageBean = bigWheelService.listOfBigWheel(currPage, pageSize);
		
		render(pageBean);
	}
	/**
	 * 
	 * @Title: toAddActivitySettingPre
	 * @description: 进入添加大转盘活动设置页面
	 *    
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:22:58
	 */
	public static void toAddActivitySettingPre(){
		render();
	}
	/**
	 * 
	 * @Title: addActivitySetting
	 * @description: 添加活动设置
	 *
	 * @param bigWheel    
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:30:09
	 */
	public static void addActivitySetting(t_big_wheel bigWheel){
		bigWheel.save();
		flash.success("添加成功");
		activitySettingPre(1,10);
	}
	/**
	 * 
	 * @Title: toEditActivitySettingPre
	 * @description: 进入修改页面
	 *
	 * @param id    
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:33:14
	 */
	public static void toEditActivitySettingPre(long id){
		t_big_wheel bigWheel = bigWheelService.findById(id);
		if(bigWheel == null){
			error404();
		}
		render(bigWheel);
	}
	/**
	 * 
	 * @Title: editActivitySetting
	 * @description: 修改活动设置
	 *
	 * @param bigWheel    
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:35:19
	 */
	public static void editActivitySetting(t_big_wheel bigWheel){
		bigWheel.save();
		flash.success("编辑成功");
		activitySettingPre(1,10);
	}
	
}
