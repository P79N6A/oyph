package services.common;

import models.common.entity.t_risk_suggest;
import services.base.BaseService;
import common.utils.Factory;
import daos.common.RiskSuggestDao;


public class RiskSuggestService extends BaseService<t_risk_suggest>{

protected static RiskSuggestDao riskSuggestDao = Factory.getDao(RiskSuggestDao.class);
	
	protected RiskSuggestService(){
		super.basedao = riskSuggestDao;
	}
	/**
	 * 
	 * @Title: insertSuggest
	 * 
	 * @description 保存风控建议，成功返回true
	 * @param @param suggest
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-下午5:01:53
	 */
	public boolean insertSuggest(t_risk_suggest suggest) {
		
		return riskSuggestDao.save(suggest);
	}
	
}
