package services.common;

import common.utils.Factory;
import daos.common.RiskRecordDao;
import models.common.entity.t_risk_handle_record;
import services.base.BaseService;

/**
 * 
 * RiskRecordService
 * @author lihuijun
 * @createDate 2017年6月16日
 */
public class RiskRecordService extends BaseService<t_risk_handle_record> {
	
	protected static RiskRecordDao riskRecordDao = Factory.getDao(RiskRecordDao.class);
	
	protected RiskRecordService(){
		super.basedao=riskRecordDao;
	}
	/**
	 * 
	 * @Title: insertRecord
	 * 
	 * @description 保存一条风控操作记录，成功返回true 
	 * @param  record
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-下午5:04:02
	 */
	public boolean insertRecord(t_risk_handle_record record) {
		
		return riskRecordDao.save(record);
	}
	
}
