package dao;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.entity.t_wechat_customer;

public class CustomerMessageDao extends BaseDao<t_wechat_customer> {

	protected CustomerMessageDao() {}
	
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
		
		StringBuffer sql = new StringBuffer("SELECT * FROM t_wechat_customer");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_wechat_customer");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(name != null) {
			if (!name.isEmpty()) {
				
				sql.append(" where supervisor_id in ( select id from t_supervisor where reality_name like :name ) ");
				sqlCount.append(" where supervisor_id in ( select id from t_supervisor where reality_name like :name ) ");
				condition.put("name", "%"+name+"%");
			}
		}
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	public int updateKf (Long customerId,String nickName,String kfAccount) {
		StringBuffer sql = new StringBuffer("upate t_wechat_customer set kf_account = :kfAccount , kf_nick = :nickName where id = :customerId");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("kfAccount", kfAccount);
		condition.put("nickName", nickName);
		condition.put("customerId", customerId);
		
		return this.updateBySQL(sql.toString(), condition);
	}
	
	public boolean saveCustomer (t_wechat_customer customer) {
		
		return this.save(customer);
	}
}
