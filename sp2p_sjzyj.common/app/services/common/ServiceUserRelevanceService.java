package services.common;

import common.utils.Factory;
import daos.common.ServiceUserRelevanceDao;
import models.common.entity.t_service_user_relevance;
import services.base.BaseService;

public class ServiceUserRelevanceService extends BaseService<t_service_user_relevance> {

	protected ServiceUserRelevanceDao serviceUserRelevanceDao = Factory.getDao(ServiceUserRelevanceDao.class);
	
	protected ServiceUserRelevanceService() {
		super.basedao = serviceUserRelevanceDao;
	}
	
	/**
	 *  查询客服服务的客服数量
	 *
	 * @param serviceId 客服id
	 * @param type 类型
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月15日
	 */
	public int queryCountByUserId(long serviceId, int type) {
		
		return serviceUserRelevanceDao.queryCountByUserId(serviceId, type);
	}
}
