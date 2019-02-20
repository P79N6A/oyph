package controllers.back.activity.shake;

import java.util.List;

import common.utils.Factory;
import controllers.common.FrontBaseController;
import models.activity.shake.entity.t_big_wheel;
import play.db.jpa.JPABase;
import services.activity.shake.BigWheelService;


public class BigWheelCtrl extends FrontBaseController {
	protected static BigWheelService bigWheelService = Factory.getService(BigWheelService.class);
	/**
	 * 
	 * @Title: getBigWheel
	 * @description: 查询进行中的大转盘活动 
	 *
	 * @return    
	 * @return List<t_big_wheel>   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-上午10:13:52
	 */
	public static  List<t_big_wheel> getBigWheel(){	
		return  bigWheelService.getBigWheel();
		
	}
	
}
