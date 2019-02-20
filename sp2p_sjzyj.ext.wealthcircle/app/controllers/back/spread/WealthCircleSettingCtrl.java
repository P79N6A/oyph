package controllers.back.spread;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import service.ext.wealthcircle.WealthCircleService;

import com.shove.Convert;
import common.constants.ext.WealthCircleSettingKey;
import common.utils.Factory;
import common.utils.StrUtil;

import controllers.common.BackBaseController;

/**
 * 后台-推广-财富圈-财富圈规则
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月1日
 */
public class WealthCircleSettingCtrl extends BackBaseController {

	protected static WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class);
	
	/**
	 * 后台-推广-财富圈-财富圈规则
	 * 
	 * @rightID 706001
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
    public static void toWealthCircleSettingPre() {
        
    	String initial_amount = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.INITIAL_AMOUNT);
		String rebate_invest = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.REBATE_INVEST);
		String discount_invest = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.DISCOUNT_INVEST);
		
		renderArgs.put("initial_amount", initial_amount);
		renderArgs.put("rebate_invest", rebate_invest);
		renderArgs.put("discount_invest", discount_invest);
		
		render();
    }

    /**
     * 后台-推广-财富圈-财富圈规则-编辑财富圈规则
     * 
     * @rightID 706002
     *
     * @author DaiZhengmiao
     * @createDate 2016年4月6日
     */
    public static void editWealthCircleSetting(){
    	checkAuthenticity();
    	
    	if(!valid_editWealthCircleSetting()){
			
    		toWealthCircleSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(WealthCircleSettingKey.INITIAL_AMOUNT, params.get("initial_amount"));
		settings.put(WealthCircleSettingKey.REBATE_INVEST, params.get("rebate_invest"));
		settings.put(WealthCircleSettingKey.DISCOUNT_INVEST, params.get("discount_invest"));
		
		wealthCircleService.updateSettings(settings);
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.SPREAD_EDIT_WEALTHCIRCLE, null);
		
		flash.success("财富圈规则设置成功");
		
		toWealthCircleSettingPre();
    	
    }
    
    /** editWealthCircleSetting数据校验方法 */
    public static boolean valid_editWealthCircleSetting(){
    	
    	int initial_amount = Convert.strToInt(params.get("initial_amount"), -1);
		String rebate_invest = params.get("rebate_invest");
		String discount_invest = params.get("discount_invest");

		if (initial_amount < 1000 || initial_amount > 99999999) {
			flash.error("理财赠送邀请码金额输入有误");

			return false;
		}
		if( (!StrUtil.isOneDouble(rebate_invest)) || (StrUtil.isNumLess(rebate_invest, 0.0f)) || (StrUtil.isNumMore(rebate_invest, 10f)) ){
			flash.error("理财持续返佣千分比输入有误");
			
			return false;
		}
		
		if( (!StrUtil.isOneDouble(discount_invest)) || (StrUtil.isNumLess(discount_invest, 0.0f)) || (StrUtil.isNumMore(discount_invest, 10f)) ){
			flash.error("理财手续费折扣输入有误");
			
			return false;
		}
		
		return true;
    	
    }
}
