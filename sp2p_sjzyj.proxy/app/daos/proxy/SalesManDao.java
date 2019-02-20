package daos.proxy;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.base.BaseDao;
import models.proxy.entity.t_proxy_salesman;

/**
 * 业务员表
 * @author Niu Dongfeng
 */
public class SalesManDao extends BaseDao<t_proxy_salesman> {

	protected SalesManDao() {} 
	
	/**
	 * 添加业务员
	 * 
	 * @param saleName		业务员真实姓名
	 * @param saleMobile	业务员手机号
	 * @param salePwd		业务员密码
	 * @param saleStatus	业务员状态
	 * @param type			业务员类型：1业务员， 2代理商
	 * @param proxyId		代理商 id
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public t_proxy_salesman addSalesMan(String saleName, String saleMobile, String salePwd, int saleStatus, int type, long proxyId) {
		
		t_proxy_salesman salesMan = new t_proxy_salesman();
		
		salesMan.time = new Date();
		salesMan.sale_name = saleName;
		salesMan.sale_mobile = saleMobile;
		salesMan.sale_pwd = salePwd;
		salesMan.sale_status = saleStatus;
		salesMan.type = type;
		salesMan.proxy_id = proxyId;
		
		return salesMan.save();
	}
	
	/**
	 * 修改手机号
	 * 
	 * @param salesManId	业务员Id
	 * @param mobile		业务员手机号
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public boolean updateMobile(long salesManId, String mobile) {
		
		String sql = "UPDATE t_proxy_salesman SET sale_mobile = :mobile WHERE id = :salesManId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("salesManId", salesManId);
		condition.put("mobile", mobile);
		
		return updateBySQL(sql, condition) == 1 ? true : false;
	}
	
	/**
	 * 修改密码
	 * 
	 * @param salesManId	业务员Id
	 * @param password		业务员新密码
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public boolean updatePassWord(long salesManId, String password) {
		
		String sql = "UPDATE t_proxy_salesman SET sale_pwd = :password WHERE id = :salesManId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("salesManId", salesManId);
		condition.put("password", password);
		
		return updateBySQL(sql, condition) == 1 ? true : false;
	}
	
	/**
	 * 修改业务员状态
	 * 
	 * @param salesManId	业务员Id
	 * @param saleStatus	业务员状态：1正常2锁定
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public boolean updateStatus(long salesManId, int saleStatus) {
		
		String sql = "UPDATE t_proxy_salesman SET sale_status = :saleStatus WHERE id = :salesManId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("salesManId", salesManId);
		condition.put("saleStatus", saleStatus);
		
		return updateBySQL(sql, condition) == 1 ? true : false;
	}
	
	/**
	 * 分页查询代理商下面的业务员
	 * 
	 * @param proxyId
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public PageBean<t_proxy_salesman> pageOfSalesManByProxyId(long proxyId, int currPage, int pageSize, String saleName, String saleMobile) {
		
		StringBuffer querySql = new StringBuffer("SELECT * FROM t_proxy_salesman t WHERE t.proxy_id = :proxyId");
		StringBuffer countSql = new StringBuffer("SELECT COUNT(t.id) FROM t_proxy_salesman t WHERE t.proxy_id = :proxyId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", proxyId);
		
		if (saleName != null && !"".equals(saleName)) {
			querySql.append(" AND t.sale_name LIKE :saleName ");
			countSql.append(" AND t.sale_name LIKE :saleName ");
			
			condition.put("saleName", "%" + saleName + "%");
		}
		
		if (saleMobile != null && !"".equals(saleMobile)) {
			querySql.append(" AND t.sale_mobile LIKE :saleMobile ");
			countSql.append(" AND t.sale_mobile LIKE :saleMobile ");
			
			condition.put("saleMobile", "%" + saleMobile + "%");
		}
		
		return pageOfBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), condition);
	}
	
}
