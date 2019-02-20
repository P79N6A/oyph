package controllers.back.activity.shake;

import java.util.List;

import org.codehaus.groovy.control.StaticImportVisitor;


import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import controllers.common.BackBaseController;
import daos.activity.shake.ShakeRecordDao;
import models.activity.shake.entity.t_display_status;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;
import net.sf.json.JSONObject;
import services.activity.shake.DisplayStatusService;
import services.activity.shake.ShakeActivityService;
import services.activity.shake.ShakeRecordService;
import services.activity.shake.ShakeSetService;


/**
 * 后台-活动-摇一摇活动
 * 
 * @author niu
 * @create 2017-12-08
 */
public class ShakeActivityCtrl extends BackBaseController {
	
	
	protected static ShakeActivityService shakeActivityService = Factory.getService(ShakeActivityService.class);
	
	protected static ShakeSetService shakeSetService = Factory.getService(ShakeSetService.class);

	protected static ShakeRecordService shakeRecordService = Factory.getService(ShakeRecordService.class);

	protected static DisplayStatusService displayStatusService = Factory.getService(DisplayStatusService.class);
	/**
	 * 分页查询摇一摇活动
	 * 
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public static void shakeActivityListPre(int showType, int currPage, int pageSize) {
		
		PageBean<t_shake_activity> pageBean = shakeActivityService.listOfShakeActivity(currPage, pageSize);
		t_display_status status = displayStatusService.findDisplayStatus();
		render(pageBean, showType, status);
	}
	
	/**
	 * 
	 * @param showTtype
	 */
	public static void shakeActivitySetPre(int showType, long activityId) {
		
		if (activityId <= 0) {
			shakeActivityListPre(0, 1, 10);
		}
		
		//红包设置
		List<t_shake_set> redPacketList = shakeSetService.listOfShakeSet(1, activityId);
		
		//加息券设置
		List<t_shake_set> addRateTicketList = shakeSetService.listOfShakeSet(2, activityId);
		
		//积分设置
		List<t_shake_set> mallList = shakeSetService.listOfShakeSet(3, activityId);
		
		//中奖率设置
		List<t_shake_set> winRateList = shakeSetService.listOfShakeSet(4, activityId);
		
		//一等奖设置
		List<t_shake_set> prizeOneList = shakeSetService.listOfShakeSet(5, activityId);
		
		//二等奖设置
		List<t_shake_set> prizeTwoList = shakeSetService.listOfShakeSet(6, activityId);
		
		//三等奖设置
		List<t_shake_set> prizeThereList = shakeSetService.listOfShakeSet(7, activityId);
		
		//纪念奖设置
		List<t_shake_set> prizeFourList = shakeSetService.listOfShakeSet(8, activityId);
		
		t_shake_activity activity =  shakeActivityService.findByID(activityId);
		
		
		render(showType, redPacketList, addRateTicketList, mallList, winRateList, prizeOneList, prizeTwoList, prizeThereList, prizeFourList, activityId, activity);
	}
	
	/**
	 * 
	 * @param showType
	 */
	public static void shakeActivityRecordPre(int showType, long activityId, int winner, int currPage, int pageSize) {

		
		if (activityId <= 0) {
			shakeActivityListPre(0, 1, 10);
		}
		
		PageBean<t_shake_record> pageBean = shakeRecordService.pageOfShakeRecord(activityId, winner, currPage, pageSize);
		
		render(showType, pageBean, activityId);
	}
	
	/**
	 * 保存摇一摇活动
	 * 
	 * @param name  活动名称
	 * @param ctime 活动时间
	 * @return 添加成功返回true,添加失败返回flase.
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public static void saveShakeActivity(String name, int ctime) {
		
		//活动参数验证
		if (name == null || "".equals(name)) {
			flash.error("请输入活动名称");
			
			saveShakeActivityPre();
		}
		
		if (ctime <= 0) {
			flash.error("请输入活动时间");
			
			saveShakeActivityPre();
		}
		
		//保存活动
		if (!shakeActivityService.saveShakeActivity(name, ctime)) {
			flash.error("摇一摇活动添加失败");
			
			saveShakeActivityPre();
		}
	
		flash.success("摇一摇活动添加成功");
		
		shakeActivityListPre(0, 1, 10);
	}
	
	/**
	 * 保存摇一摇活动准备
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public static void saveShakeActivityPre() {
		render();
	}
	
	/**
	 * 保存摇一摇活动设置
	 * 
	 * @param type			设置类型
	 * @param activityId	活动Id
	 * @param amount		奖项大小
	 * @param number		奖项数量
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-09
	 */
	public static void saveShakeSet(int type, long activityId, double amount, String prize_name,int number, double use_rule) {
		//根据不同的奖项进行判断
		if (type < 4 && type > 0) {
			if (type <= 0 || activityId <= 0  || number <= 0 || amount <= 0 || use_rule <=0) {
				flash.error("输入有误，请从新输入！");
				shakeActivitySetPre(2, activityId);
			}
		}else{
			
			if (type <= 0 || activityId <= 0  || number <= 0 || prize_name == null ) {
				flash.error("输入有误，请从新输入！");
				shakeActivitySetPre(2, activityId);
			}
		}
		
		
		if (type == 2) {
			amount = amount * 100;
		}
		
		if (shakeSetService.saveShakeSet(type, activityId, (int)amount, prize_name, number, use_rule)) {
			flash.success("设置成功");
			shakeActivitySetPre(2, activityId);
		}
		
		flash.error("设置失败！");
		shakeActivitySetPre(2, activityId);
	}
	
	/**
	 * 通过活动设置ID删除活动设置
	 * 
	 * @param shakeSetId
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-09
	 */
	public static void deleteShakeSet(long shakeSetId, long activityId) {
		
		if (shakeSetId <= 0) {
			flash.error("活动设置不存在");
			shakeActivitySetPre(2, activityId);
		}
		
		if (shakeSetService.deleteShakeSet(shakeSetId, activityId)) {
			flash.success("设置删除成功");
			shakeActivitySetPre(2, activityId);
		}
		
		flash.error("设置删除失败！");
		shakeActivitySetPre(2, activityId);	
	}
	
	/**
	 * 生成活动奖项
	 * 
	 * @param activityId 活动ID
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public static void saveShakeRecord(long activityId) {
		
		if (shakeActivityService.updateActivityStatus(activityId, 2)) {
			flash.success("活动奖项已设置成功");
			shakeActivitySetPre(2, activityId);
		}
		
		LoggerUtil.info(true, "", "");
		
		flash.error("活动奖项生成失败");
		shakeActivitySetPre(2, activityId);
	}
	
	/**
	 * 生成活动中奖率
	 * 
	 * @param activityId
	 * @param number
	 * @param shakeTime
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public static void saveShakeActivityWinRate(long activityId, int number, int shakeTime) {
		
		if (number == 0 || shakeTime == 0) {
			flash.error("人数或者摇一摇时间不能为0");
			shakeActivitySetPre(2, activityId);
		}
		
		if (shakeActivityService.saveShakeActivityWinRate(activityId, number, shakeTime)) {
			flash.success("中奖率设置成功");
			shakeActivitySetPre(2, activityId);
		}
		
		flash.error("中奖率设置失败");
		shakeActivitySetPre(2, activityId);
	}
	
	/**
	 * 开始活动
	 * 
	 * @param activityId
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public static void startActivity(long activityId) { 
		
		if (activityId <= 0) {
			renderText("活动不存在");
		}
		
		if (shakeActivityService.startActivity(activityId)) {
			renderText("活动已开始");
		}
		
		renderText("活动开始失败");
	}
	
	/**
	 * 结束活动
	 * 
	 * @param activityId
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public static void endActivity(long activityId) {
		if (activityId <= 0) {
			renderText("活动不存在");
		}
		if (shakeActivityService.endActivity(activityId)) {
			renderText("活动已结束");
		}
		
		renderText("活动结束失败");
		
	}

	/**
	 * 
	 * @Title: updateDisplayStatus
	 * 
	 * @description 修改个人中奖信息显示状态
	 * @param @param display_status
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月7日-上午10:46:48
	 */
	public static void updateDisplayStatus(){
		ResultInfo result = new ResultInfo();
		int num = displayStatusService.updateDisplayStatus();
		
		t_display_status status = displayStatusService.findDisplayStatus();
		if (num >= 1) {
			result.code = 0;
			result.msg = "修改成功";
			result.obj = status.display_status;	
			
		}else{
			result.code = -1;
			result.msg = "修改失败";
			result.obj = status.display_status;
			
		}
		renderJSON(result);
		
	}
}
