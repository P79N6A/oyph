package services.common;

import java.util.List;

import models.common.entity.t_credit_level;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.ResultInfo;

import daos.common.CreditLevelDao;

/**
 * 信用等级service实现
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
public class CreditLevelService extends BaseService<t_credit_level> {

	protected CreditLevelDao creditLevelDao = Factory.getDao(CreditLevelDao.class);

	protected CreditLevelService() {
		super.basedao = this.creditLevelDao;
	}

	/**
	 * 获取信用等级列表
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public List<t_credit_level> queryCreditLevels() {

		return findAll();
	}

	/**
	 * 修改信用等级
	 * 
	 * @param id 信用等级id
	 * @param name 信用等级名称
	 * @param imageUrl 图片URL
	 * @param imageResolution 图片分辨率
	 * @param imageSize 图片大小
	 * @param imageFormat 图片格式
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public ResultInfo updateCreditLevel(long id, String name, String imageUrl,
			String imageResolution, String imageSize, String imageFormat) {
		ResultInfo result = new ResultInfo();
		int i = creditLevelDao.updateCreditLevel(id, name, imageUrl,
				imageResolution, imageSize, imageFormat);
		if (i < 0) {
			result.code = -1;
			result.msg ="信用等级信息修改失败";
			
			return result;
		}
		result.code = 1;
		result.msg ="信用等级信息修改成功";
		
		return result;
	}

	/**
	 * 查询信用等级对象
	 * 
	 * @param id
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public t_credit_level findCreditLevel(long id) {

		return findByID(id);
	}

	/**
	 * 根据信用积分计算信用等级
	 * 
	 * @param creditScore
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public t_credit_level queryLevelByCreditScore(int creditScore) {

		List<t_credit_level> creditLevels = queryCreditLevels();
		t_credit_level level = new t_credit_level();
		
		/* 匹配符合用户信用积分的信用等级对象 */
		for (t_credit_level creditLevel : creditLevels) {
			if (creditLevel.min_credit_score <= creditScore && creditLevel.max_credit_score >= creditScore) {
				
				level = creditLevel;
			}else if (creditLevel.max_credit_score == 0 && creditLevel.min_credit_score <= creditScore) {
				
				level = creditLevel;
			}else if(creditLevel.min_credit_score == 0 && creditLevel.max_credit_score >= creditScore) {
				
				level = creditLevel;
			}
		}
		
		return level;
	}
	
}
