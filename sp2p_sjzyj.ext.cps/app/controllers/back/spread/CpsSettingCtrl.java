package controllers.back.spread;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import services.ext.cps.CpsService;

import com.shove.Convert;
import common.constants.ext.CpsSettingKey;
import common.utils.Factory;
import common.utils.StrUtil;

import controllers.common.BackBaseController;

/**
 * 后台-推广-CPS-CPS规则
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsSettingCtrl extends BackBaseController {

	protected static CpsService cpsService = Factory.getService(CpsService.class);
	
	/**
	 * 后台-推广-CPS-CPS规则
	 * 
	 * @rightID 704001
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月15日
	 */
	public static void toCpsSettingPre(){
		
		String rebate_reg = cpsService.findSettingValueByKey(CpsSettingKey.REBATE_REG);
		String rebate_invest = cpsService.findSettingValueByKey(CpsSettingKey.REBATE_INVEST);
		String discount_invest = cpsService.findSettingValueByKey(CpsSettingKey.DISCOUNT_INVEST);
		
		renderArgs.put("rebate_reg", rebate_reg);
		renderArgs.put("rebate_invest", rebate_invest);
		renderArgs.put("discount_invest", discount_invest);
		
		render();
	}
	
	/**
	 * 后台-推广-CPS-CPS规则-编辑CPS规则
	 *
	 * @rightID 704002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月15日
	 */
	public static void editCpsSetting() {
		checkAuthenticity();
		
		if (!valid_editCpsSetting()) {

			toCpsSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(CpsSettingKey.REBATE_REG, params.get("rebate_reg"));
		settings.put(CpsSettingKey.REBATE_INVEST, params.get("rebate_invest"));
		settings.put(CpsSettingKey.DISCOUNT_INVEST, params.get("discount_invest"));
		
		cpsService.updateSettings(settings);
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.SPREAD_EDIT_CPS, null);
		
		flash.success("CPS规则设置成功");
		
		toCpsSettingPre();
	}
	
	/** editCpsSetting()的数据校验方法 */
	private static boolean valid_editCpsSetting() {
		
		int rebate_reg = Convert.strToInt(params.get("rebate_reg"), -1);
		String rebate_invest = params.get("rebate_invest");
		String discount_invest = params.get("discount_invest");

		if (rebate_reg < 0 || rebate_reg > 100) {
			flash.error("注册一次性返佣金额输入有误");

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
