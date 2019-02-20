package services.common;

import java.util.Date;
import java.util.List;

import common.interfaces.ICacheable;
import common.utils.Factory;
import common.utils.PageBean;
import daos.common.CostDao;
import models.common.bean.CostBean;
import models.common.entity.t_cost;
import services.base.BaseService;

/**
 * 前台栏目设置的service的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月29日
 */
public class CostService extends BaseService<t_cost> {
	
	protected CostDao costDao = null;
	
	protected CostService() {
		this.costDao = Factory.getDao(CostDao.class);
		
		super.basedao = costDao;
	}
	
	/**
	 * 费用账户 列表查询
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年03月14日
	 */
	public PageBean<t_cost> pageOfCosts(int currPage, int pageSize) {
		
		return costDao.pageOfCosts(currPage, pageSize);
	}
	
	/**
	 * 费用账户列表查询
	 * 
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public List<CostBean> queryCosts(String beginTime, String endTime) {
		
		return costDao.queryCosts(beginTime, endTime);
	}
	
	/**
	 * 费用账户列表查询
	 * 
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public double countWithdraw(String beginTime, String endTime) {
		
		return costDao.countWithdraw(beginTime, endTime);
	}
	
}
