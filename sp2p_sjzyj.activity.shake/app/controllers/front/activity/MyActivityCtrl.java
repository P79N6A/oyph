package controllers.front.activity;

import java.util.List;

import common.utils.Factory;
import controllers.common.FrontBaseController;
import models.activity.shake.entity.t_big_wheel;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_record;
import models.app.bean.ActivityListBean;
import services.activity.shake.BigWheelService;
import services.activity.shake.ShakeActivityService;

public class MyActivityCtrl extends FrontBaseController{
	
	protected static  ShakeActivityService shankeActivityService = Factory.getService(ShakeActivityService.class);
	protected static BigWheelService bigWheelService = Factory.getService(BigWheelService.class);
	/**  讴业活动  */
	public static void SyllabusPre() {
		
//		List<t_shake_activity> shankeActivity = shankeActivityService.findAll();
		/** 查询正在进行中的大转盘活动 */
//		List<t_big_wheel> bigWheelList = bigWheelService.getBigWheel();
		List<ActivityListBean> bigWheelList = bigWheelService.findListActivityBean();
//		renderArgs.put("shankeActivity", shankeActivity);
		renderArgs.put("bigWheelList", bigWheelList);
		
		render();
	}
	
   
}
