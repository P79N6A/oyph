package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.ServiceMonthDao;
import models.common.entity.t_service_month;
import services.base.BaseService;

/**
 * 客服月记录service
 * @author liuyang
 *
 */
public class ServiceMonthService extends BaseService<t_service_month> {

	protected ServiceMonthDao serviceMonthDao = Factory.getDao(ServiceMonthDao.class);
	
	protected ServiceMonthService() {
		super.basedao = serviceMonthDao;
	}
	
	/**
	 *  分页查询，客服月统计列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userId 用户id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月13日
	 */
	public PageBean<t_service_month> pageOfServiceMonthList(int currPage, int pageSize, int year, int month) {
		
		return serviceMonthDao.pageOfServiceMonthList(currPage, pageSize, year, month);
	}
	
	/**
	 *  根据年月查询实体类
	 *
	 * @param year 年
	 * @param month 月
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月15日
	 */
	public t_service_month queryByTime(int year, int month) {
		
		return serviceMonthDao.queryByTime(year, month);
	}
}
