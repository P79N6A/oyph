package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_cust_org;

/**
 * 客户关联方信息表Dao接口
 *
 * @ClassName: CustOrgDao

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午9:54:43
 */
public class CustOrgDao extends BaseDao<t_cust_org> {

	protected CustOrgDao () {
		
	}
	
	/**
	 * 清空表数据
	 *
	 * @Title: truncateTable
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午11:10:02
	 */
	public void truncateTable() {
		String sql = " truncate table t_cust_org";
		this.deleteBySQL(sql, null);
	}
	
	/**
	 * 查询表数据
	 *
	 * @Title: findCustOrg
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_cust_org>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午11:19:11
	 */
	public List<t_cust_org> findCustOrg () {
		String sql = "select * from t_cust_org";
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	
}
