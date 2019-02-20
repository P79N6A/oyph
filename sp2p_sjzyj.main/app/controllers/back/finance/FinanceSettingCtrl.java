package controllers.back.finance;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;

import com.shove.Convert;
import common.constants.SettingKey;
import common.utils.StrUtil;

import controllers.common.BackBaseController;

/**
 * 后台-财务-理财设置-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class FinanceSettingCtrl extends BackBaseController {
	
	/**
	 * 后台-财务-理财设置
	 *
	 * @rightID 508001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void toFinanceSettingPre() {

		//提现手续费
		String withdraw_fee_point = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_POINT);
		String withdraw_fee_rate = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_RATE);
		String withdraw_fee_min = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_MIN);
		
		String withdraw_amount_max = settingService.findSettingValueByKey(SettingKey.WITHDRAW_AMOUNT_MAX);
		String withdraw_amount_min = settingService.findSettingValueByKey(SettingKey.WITHDRAW_AMOUNT_MIN);
		
		//转让服务费
		String transfer_fee_rate = settingService.findSettingValueByKey(SettingKey.TRANSFER_FEE_RATE);
	
		//最低充值金额
		String recharge_amount_min = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN);
		String recharge_amount_max = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX);
		
		//账单到期提醒时间
		String bill_expires = settingService.findSettingValueByKey(SettingKey.BILL_EXPIRES);
		
		renderArgs.put("withdraw_fee_point", withdraw_fee_point);
		renderArgs.put("withdraw_fee_rate", withdraw_fee_rate);
		renderArgs.put("withdraw_fee_min", withdraw_fee_min);
		
		renderArgs.put("withdraw_amount_min", withdraw_amount_min);
		renderArgs.put("withdraw_amount_max", withdraw_amount_max);
		
		renderArgs.put("transfer_fee_rate", transfer_fee_rate);
		
		renderArgs.put("recharge_amount_min", recharge_amount_min);
		renderArgs.put("recharge_amount_max", recharge_amount_max);
		
		renderArgs.put("bill_expires", bill_expires);
		
		render();
	}
	
	/**
	 * 后台-财务-理财设置-修改提现设置
	 *
	 * @rightID 508002
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static void editWithdrawSetting() {
		checkAuthenticity();
		
		if (!valid_editWithdrawSetting()) {

			toFinanceSettingPre();
		}

		int withdraw_amount_min = Convert.strToInt(params.get("withdraw_amount_min"), -1);
		if (withdraw_amount_min < 0 || withdraw_amount_min >= 10000) {
			flash.error("最低提现金额必须小于10000");

			toFinanceSettingPre();
		}
		
		int withdraw_amount_max = Convert.strToInt(params.get("withdraw_amount_max"), -1);
		if (withdraw_amount_max < 10000 || withdraw_amount_max >= 100000000) {
			flash.error("最高提现金额必须大于或等于10000，小于100000000");
			
			toFinanceSettingPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.WITHDRAW_AMOUNT_MIN, params.get("withdraw_amount_min"));
		settings.put(SettingKey.WITHDRAW_AMOUNT_MAX, params.get("withdraw_amount_max"));
		settings.put(SettingKey.WITHDRAW_FEE_POINT, params.get("withdraw_fee_point"));
		settings.put(SettingKey.WITHDRAW_FEE_RATE, params.get("withdraw_fee_rate"));
		settings.put(SettingKey.WITHDRAW_FEE_MIN, params.get("withdraw_fee_min"));

		settingService.updateSettings(settings);

		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FINANCESETTING_WITHDRAW, null);

		flash.success("提现设置成功");
		
		toFinanceSettingPre();
	}

	/**
	 * 后台-财务-理财设置-修改债权转让设置
	 *
	 * @rightID 508003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static void editTransferSetting() {
		checkAuthenticity();
		
		String transfer_fee_rate = params.get("transfer_fee_rate");
		if ((!StrUtil.isOneDouble(transfer_fee_rate)) || (StrUtil.isNumLess(transfer_fee_rate, 0.1f)) || (StrUtil.isNumMore(transfer_fee_rate, 99.9f))) {
			flash.error("你输入的含有非法参数");

			toFinanceSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.TRANSFER_FEE_RATE, params.get("transfer_fee_rate"));
		settingService.updateSettings(settings);

		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FINANCESETTING_TRANSER, null);

		flash.success("转让设置成功");
		
		toFinanceSettingPre();
	}
	
	/**
	 * 后台-财务-理财设置-修改充值设置
	 * 
	 * @rightID 508004
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static void editRechargeSetting() {
		checkAuthenticity();
		
		int recharge_amount_min = Convert.strToInt(params.get("recharge_amount_min"), -1);
		if (recharge_amount_min < 0 || recharge_amount_min >= 10000) {
			flash.error("最低充值金额必须小于10000");

			toFinanceSettingPre();
		}
		
		int recharge_amount_max = Convert.strToInt(params.get("recharge_amount_max"), -1);
		if (recharge_amount_max < 10000 || recharge_amount_max >= 100000000) {
			flash.error("最高充值金额必须大于或等于10000，小于100000000");
			
			toFinanceSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.RECHARGE_AMOUNT_MIN, params.get("recharge_amount_min"));
		settings.put(SettingKey.RECHARGE_AMOUNT_MAX, params.get("recharge_amount_max"));
		settingService.updateSettings(settings);

		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FINANCESETTING_RECHARGE, null);

		flash.success("充值设置成功");
		
		toFinanceSettingPre();
	}
	

	/**
	 * 后台-财务-理财设置-修改账单催收设置
	 *
	 * @rightID 508005
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static void editExpiresSetting() {
		checkAuthenticity();
		
		int bill_expires = Convert.strToInt(params.get("bill_expires"), -1);
		if (bill_expires < 0 || bill_expires > 30) {
			flash.error("到期提醒天数不超过30天");

			toFinanceSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.BILL_EXPIRES, params.get("bill_expires"));
		settingService.updateSettings(settings);

		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.FINANCESETTING_EXPIRES, null);

		flash.success("催收设置成功");
		
		toFinanceSettingPre();
	}
	
	/** editWithdrawSetting()的数据校验方法 */
	private static boolean valid_editWithdrawSetting() {
		int withdraw_fee_point = Convert.strToInt(params.get("withdraw_fee_point"), -1);
		String withdraw_fee_rate = params.get("withdraw_fee_rate");
		int withdraw_fee_min = Convert.strToInt(params.get("withdraw_fee_min"), -1);
		if (withdraw_fee_point < 0 || withdraw_fee_point > 99999999) {
			flash.error("提现手续费起点为不超过8位正整数");

			return false;
		}
		
		if( (!StrUtil.isOneDouble(withdraw_fee_rate)) || (StrUtil.isNumLess(withdraw_fee_rate, 0.1f)) || (StrUtil.isNumMore(withdraw_fee_rate, 99.9f)) ){
			flash.error("提现手续费率为带一位小数且不超过100");
			
			return false;
		}
		
		if (withdraw_fee_min < 0 || withdraw_fee_min > 999) {
			flash.error("最低手续费不超过3位正整数");

			return false;
		}
		
		return true;
	}
}
