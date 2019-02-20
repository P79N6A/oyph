package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_risk_pic;
import daos.base.BaseDao;
/**
 * 
 * RiskPicDao
 * @author lihuijun
 * @createDate 2017年4月27日
 */
public class RiskPicDao extends BaseDao<t_risk_pic>{
	
	protected RiskPicDao() {
	}
	/**
	 * 通过报告的id 得到图片
	 * @param riskId
	 * @param type
	 * @return
	 * @createDate 2017年5月2日
	 * @author lihuijun
	 */
	public List<t_risk_pic> getRiskPicByRiskId(long riskId,Integer type){
		if(type==null){ 
			return this.findListByColumn("risk_id=?",riskId);
		}
		else{
			return this.findListByColumn("risk_id=? and type=?",riskId, type);
		}
	}
	
	/**
	 * 通过riskId删除图片
	 * @param riskId
	 * @return
	 * @createDate 2017年5月2日
	 * @author lihuijun
	 */
	public int deletePicsByRiskId(long riskId){
		String sql = "delete from t_risk_pic where risk_id =:riskId";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("riskId", riskId);
		return super.deleteBySQL(sql, args);
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
		String sql = " SELECT t.* FROM t_risk_pic t WHERE t.type = 6  AND t.risk_id=:riskId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("riskId", riskId);
		
		return this.findListBySQL(sql, condition);
	}
	
}
