package jobs;

import java.util.List;
import java.util.UUID;

import com.shove.Convert;

import models.core.entity.t_bid;
import models.ext.redpacket.entity.t_red_packet_user;
import payment.impl.PaymentProxy;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import services.common.SettingService;
import services.core.BidService;
import services.core.BillService;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;
import common.constants.ConfConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;

/**
 * 每29分钟检查一次是否流标
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年1月23日
 */

public class CheckBidIsFlow  {

	protected static BillService billService = Factory.getService(BillService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public void doJob() {
	    BidService bidService = Factory.getService(BidService.class);
		
		/** 不支持自动流标的接口，请注释掉这个定时任务 */
	    if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	    	Logger.info("--------------检查是否流标,开始---------------------");
		}
		
		
		t_bid bid = bidService.queryBidIsFlow();
		
		if(bid == null){
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("--------------没有标要执行流标---------------------");
			}
			
			return;
		}
		JPAPlugin.closeTx(false);
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("标的ID：" + bid.id);
		}
		
		try{
			JPAPlugin.startTx(false);

			ResultInfo result = new ResultInfo();

			//业务订单号
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_FAILED);
			
			//托管，流标
			if(ConfConst.IS_TRUST){
				/** 先冲正，后流标 */
				result = bidService.redPacketCorrectBid(bid.id);
				if (result.code > 0) {
					
					result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_FAILED, ProjectStatus.FAIL, null);
					if(result.code > 0){
						
						result = bidService.doBidFlow(businessSeqNo, bid);
					}	
				}
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				
			}
			
		}catch(Exception e){
			
			LoggerUtil.error(true, "自动流标失败编号(投资中->流标)：%s，原因：%s", bid.id, e.getMessage());
		}finally{
			JPAPlugin.closeTx(false);
			LoggerUtil.error(false, "自动流标事务正常关闭，id = %s ", bid.id);
		}
		
		JPAPlugin.startTx(false);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------检查是否流标,结束---------------------");
		}
		
	}
	
}
