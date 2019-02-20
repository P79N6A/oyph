package services.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_bank_quota;
import models.common.entity.t_column;
import models.ext.experience.entity.t_experience_bid_setting;
import play.cache.Cache;
import services.base.BaseService;

import common.interfaces.ICacheable;
import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.BankQuotaDao;
import daos.common.ColumnDao;
import daos.common.ConversionDao;

/**
 * 银行限额管理
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年10月31日
 */
public class BankQuotaService extends BaseService<t_bank_quota>  {

	protected BankQuotaDao bankQuotaDao;
	
	protected BankQuotaService() {
		bankQuotaDao = Factory.getDao(BankQuotaDao.class);
		super.basedao = bankQuotaDao;
	}
	
	
	/**
	 * 查询所有银行充值限额信息
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月3日
	 */
	public List<Map<String, Object>> queryBankQuotaInfo(){
		
		List<Map<String, Object>> list = bankQuotaDao.queryBankQuotaInfo();
		
		return list;
	}
	
	
	
	/**
	 * 修改某个银行的单笔充值限额和每日充值限额
	 *
	 * @param single_quota 单笔充值限额
	 * @param day_quota 每日充值限额
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月4日
	 */
	public ResultInfo saveBankQuota(int single_quota, int day_quota, long bankQuotaId) {
		ResultInfo result = new ResultInfo();
		
		t_bank_quota bank_quota = bankQuotaDao.findByColumn("id= ?", bankQuotaId);
		bank_quota.setSingle_quota(single_quota);
		bank_quota.setDay_quota(day_quota);
		
		boolean res = bankQuotaDao.save(bank_quota);
		if (!res){
			result.code = -1;
			result.msg = "修改银行限额失败";
		}
		result.code = 1;
		result.msg = "修改成功!";
		
		return result;
	}
	
	
	
}
