package services.activity.shake;

import java.math.BigDecimal;
import java.util.List;

import common.utils.Factory;
import daos.activity.shake.AwardTurnplateDao;
import models.activity.shake.entity.t_award_turnplate;
import models.app.bean.ReturnMoneyPlanBean;
import services.base.BaseService;

public class AwardTurnplateService extends BaseService<t_award_turnplate>{
	
	private AwardTurnplateDao awardTurnplateDao = Factory.getDao(AwardTurnplateDao.class);
	
	
	/**
	 * 
	 * @Title: findAllOrderByAward
	 * 
	 * @description 根据概率排序，来抽奖
	 * @param @return
	 * @return List<t_award_turnplate>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月24日-下午4:28:30
	 */
	public List<t_award_turnplate> findAllOrderByAward() {
		
		return awardTurnplateDao.findAllOrderByAward();
	}
	/**
	 * 
	 * @Title: findById
	 * 
	 * @description 通过id查询对象，来修改概率 
	 * @param id
	 * @return t_award_turnplate    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月26日-下午1:34:47
	 */
	public t_award_turnplate findById(long id){
		return awardTurnplateDao.findByID(id);
	}
	/**
	 * 
	 * @Title: findAwardAll
	 * 
	 * @description 奖品管理列表显示
	 * @return List<t_award_turnplate>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月26日-下午1:35:55
	 */
	public List<t_award_turnplate> findAwardAll() {
		
		return awardTurnplateDao.findAll();
	}
	/**
	 * 
	 * @Title: findByProb
	 * 
	 * @description 查询该概率是否重复 
	 * @param  prob
	 * @return t_award_turnplate    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-上午11:08:01
	 */
	public t_award_turnplate findByProb(BigDecimal prob) {
		
		return awardTurnplateDao.findByProb(prob);
	}
	
}
