package daos.activity.shake;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.shake.entity.t_award_turnplate;

public class AwardTurnplateDao extends BaseDao<t_award_turnplate>{
	/**
	 * 
	 * @Title: findAllOrderByAward
	 * 
	 * @description 根据概率从低到高排序奖品
	 * @param @return 
	 * @return List<t_award_turnplate>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月24日-下午4:27:51
	 */
	public List<t_award_turnplate> findAllOrderByAward() {
		String sql = "select * from t_award_turnplate order by prob asc";
		
		return findListBySQL(sql, null);
		
	}
	/**
	 * 
	 * @Title: findByProb
	 * 
	 * @description  查看该概率是否重复
	 * @param prob
	 * @return t_award_turnplate    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-上午11:08:34
	 */
	public t_award_turnplate findByProb(BigDecimal prob) {
		String sql = "select * from t_award_turnplate where prob =:prob";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("prob", prob);
		
		return findBySQL(sql, condition);
	}
	
}
