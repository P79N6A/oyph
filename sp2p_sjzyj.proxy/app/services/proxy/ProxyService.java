package services.proxy;

import common.utils.Factory;
import daos.proxy.ProxyDao;
import models.proxy.entity.t_proxy;
import services.base.BaseService;

/**
 * 代理商业务
 * @author Niu Dongfeng
 */
public class ProxyService extends BaseService<t_proxy> {
	
	protected ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	protected ProxyService() {
		super.basedao = proxyDao;
	}

	
	
	
}
