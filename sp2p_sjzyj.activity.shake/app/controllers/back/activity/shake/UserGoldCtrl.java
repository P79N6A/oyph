package controllers.back.activity.shake;

import java.util.Date;

import common.utils.Factory;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import models.activity.shake.entity.t_user_gold;
import services.activity.shake.UserGoldService;


public class UserGoldCtrl extends FrontBaseController {
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	/**
	 * 
	 *
	 * @Title: saveUserGold
	
	 * @description: 注册成功后金币表插入数据
	 *
	 * @param gold
	 * @param share_num
	 * @param Time 
	   
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月25日-下午5:32:38
	 */
	public static t_user_gold saveUserGold(long user_id){
		t_user_gold usergold = new t_user_gold();
		usergold.user_id = user_id;
		usergold.gold++;
		//usergold.share_num =share_num;
		usergold.Time = new Date();
		
		return usergold.save();
	
	}
	
	/**
	 * 
	 * @Title: saveUserInfo
	 *
	 * @description 查询t_user_gold表中是否有登陆人信息，没有信息则插入
	 *
	 * @param @param user_id登陆人Id
	 * @param @return 
	 * 
	 * @return t_user_gold    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年11月6日
	 */
	public static t_user_gold saveUserInfo(long user_id){
		/** 为登陆人在t_user_gold表中创建一条数据 */
		t_user_gold usergold = new t_user_gold();
		usergold.user_id = user_id;
		usergold.gold = 0;
		usergold.share_num = 0;
		usergold.Time = new Date();
		
		return usergold.save();
	
	}
	
	
	
}
