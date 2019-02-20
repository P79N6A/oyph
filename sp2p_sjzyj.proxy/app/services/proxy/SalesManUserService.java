package services.proxy;

import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import daos.proxy.SalesManUserDao;
import models.proxy.bean.SalesManUserBean;
import models.proxy.entity.t_proxy_user;
import services.base.BaseService;

/**
 * 推广会员业务
 * @author Niu Dongfeng
 */
public class SalesManUserService extends BaseService<t_proxy_user> {

	protected SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class);
	
	protected SalesManUserService() {
		super.basedao = salesManUserDao;
	}
	
	/**
	 * 分页查询业务员下会员
	 * 
	 * @param salesManId
	 * @param currPage
	 * @param pageSize
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public PageBean<SalesManUserBean> pageOfSalesManUsers(long salesManId, int currPage, int pageSize, String userName, String userMobile, int orderType, int orderValue) {
		
		return salesManUserDao.pageOfSalesManUsers(salesManId, currPage, pageSize, userName, userMobile, orderType , orderValue);
	}
	
	/**
	 * 添加业务员下会员
	 * @param userId
	 * @return
	 */
	public boolean insertUser(long userId) {
		
		t_proxy_user user = new t_proxy_user();
		
		user.cur_invest_amount = 0.00;
		user.total_invest_amount = 0.00;
		user.user_id = userId;
		
		return user.save() == null ? false : true;
	}
	
	
}
