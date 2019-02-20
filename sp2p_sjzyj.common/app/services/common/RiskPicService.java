package services.common;

import java.util.List;

import models.common.entity.t_risk_pic;
import services.base.BaseService;
import common.utils.Factory;
import daos.common.RiskPicDao;


public class RiskPicService extends BaseService<t_risk_pic>{

	protected static RiskPicDao riskPicDao = Factory.getDao(RiskPicDao.class);
	
	protected RiskPicService(){
		super.basedao = riskPicDao;
	}
	
	/**
	 * 通过报告的id 得到报告图片
	 * @param riskId
	 * @param type
	 * @return
	 * @createDate 2017年5月2日
	 * @author lihuijun
	 */
	public List<t_risk_pic> getRiskPicByRiskId(long riskId,Integer type){
		
		return riskPicDao.getRiskPicByRiskId(riskId, type);
	}
	
	public int deletePicsByRiskId(long riskId){
		
		return riskPicDao.deletePicsByRiskId(riskId);
	}
	
	/**
	 * 
	 * @Title: findRiskPicByriskId
	 *
	 * @description 添加借款人征信报告图片(type = '6')为征信报告类型
	 *
	 * @param @param riskId
	 * @param @return 
	 * 
	 * @return List<t_risk_pic>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月14日
	 */
	public List <t_risk_pic> findRiskPicByriskId (long riskId) {
		
		return riskPicDao.findRiskPicByriskId(riskId);
	}
	
}
