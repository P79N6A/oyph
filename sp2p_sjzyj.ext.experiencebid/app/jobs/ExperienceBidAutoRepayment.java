package jobs;

import java.util.List;

import models.ext.experience.entity.t_experience_bid;

import com.google.zxing.Result;
import com.shove.Convert;

import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import service.ext.experiencebid.ExperienceBidService;
import services.common.SettingService;

/**
 * 每天检查一次体验标还款，执行时间22：00
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年2月19日
 */

public class ExperienceBidAutoRepayment {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public static void doJob() {
		if(ModuleConst.EXT_EXPERIENCEBID){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("--------------------体验标自动还款,开始--------------------");
			}
			
			ResultInfo result = new ResultInfo();
			
			ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
			
			//检查还款时间是今天的
			List<t_experience_bid> experienceBids = experienceBidService.queryExperienceBidForRepayment();
			if(experienceBids == null || experienceBids.size()==0){
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("今天没有到期还款的体验标");
					Logger.info("--------------------体验标自动还款,结束--------------------");
				}
				
				return;
			}
			
			JPAPlugin.closeTx(false);
			for(t_experience_bid bid : experienceBids){
				
				try{
					
					JPAPlugin.startTx(false);
					result = experienceBidService.experienceBidAutoRepayment(bid);
					if(result.code < 1){
						LoggerUtil.info(true, "体验标id：%s,自动还款失败,原因：", bid.id, result.msg);
					}else{
						LoggerUtil.info(false, "体验标id：%s,自动还款成功", bid.id);
					}
					
				}catch(Exception e){
					LoggerUtil.info(true, "体验标id：%s,自动还款失败,原因：", bid.id, e.getMessage());
				}finally{
					JPAPlugin.closeTx(false);
				}
				
			}
			
			JPAPlugin.startTx(false);
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("--------------------体验标自动还款,结束--------------------");
			}
			
		}
	}
	
}
