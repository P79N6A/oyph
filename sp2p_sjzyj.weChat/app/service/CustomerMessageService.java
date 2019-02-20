package service;

import common.utils.Factory;
import common.utils.PageBean;
import dao.CustomerMessageDao;
import models.entity.t_wechat_customer;
import services.base.BaseService;

/**
 * 微信客服service实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2018年07月04日
 */
public class CustomerMessageService extends BaseService<t_wechat_customer> {

	private CustomerMessageDao customerMessageDao;
	
	protected CustomerMessageService() {
		customerMessageDao = Factory.getDao(CustomerMessageDao.class);
		super.basedao = customerMessageDao;
		
	}
	
	/**
	 *  分页查询客服信息列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param name 姓名
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月05日
	 */
	public PageBean<t_wechat_customer> pageOfCustomerList(int currPage, int pageSize, String name) {
		
		return customerMessageDao.pageOfCustomerList(currPage, pageSize, name);
	}
	
	public int updateKf (Long customerId,String nickName,String kfAccount) {
		
		return customerMessageDao.updateKf(customerId, nickName, kfAccount);
	}
	
	public boolean saveCustomer (t_wechat_customer customer) {
		
		return customerMessageDao.saveCustomer(customer);
	}
}
