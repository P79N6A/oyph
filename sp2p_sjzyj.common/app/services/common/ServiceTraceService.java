package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.ServiceTraceDao;
import models.common.entity.t_service_trace;
import services.base.BaseService;

/**
 * 用户追踪service
 * @author liuyang
 *
 */
public class ServiceTraceService extends BaseService<t_service_trace> {

	protected ServiceTraceDao serviceTraceDao = Factory.getDao(ServiceTraceDao.class);
	
	protected ServiceTraceService() {
		this.basedao = serviceTraceDao;
	}
	
	/**
	 *  分页查询，会员列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userId 用户id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月12日
	 */
	public PageBean<t_service_trace> pageOfUserTraceList(long userId, int currPage, int pageSize) {
		
		return serviceTraceDao.pageOfUserTraceList(userId, currPage, pageSize);
	}
}
