package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.CreditDao;
import models.common.entity.t_credit;
import services.base.BaseService;

/**
 * 信用等级Service
 *
 * @description 
 *
 * @author Liuyang
 * @createDate 2018年01月23日
 */
public class CreditService extends BaseService<t_credit> {

	protected static CreditDao creditDao = Factory.getDao(CreditDao.class);
	
	protected CreditService() {
		
		super.basedao = this.creditDao;
	}
	/**
	 * 
	 * @Title: findPageOfCredit
	 * 
	 * @description 分页显示等级管理
	 * @param @param currPage
	 * @param @param pageSize
	 * @return PageBean<t_credit>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:25:00
	 */
	public PageBean<t_credit> findPageOfCredit(int currPage,int pageSize){
		
		return creditDao.findPageOfCredit(currPage, pageSize);
	}
}
