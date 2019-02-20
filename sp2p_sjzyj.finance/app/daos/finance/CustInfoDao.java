package daos.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_cust_info;

/**
 * 客户基本信息Dao接口
 *
 * @ClassName: CustInfoDao

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午9:49:37
 */
public class CustInfoDao extends BaseDao<t_cust_info> {

	protected CustInfoDao () {
		
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
	 * @createDate 2018年10月6日-下午5:35:15
	 */
	public void truncateTable() {
		String sql = " truncate table t_cust_info";
		this.deleteBySQL(sql, null);
	}
	
	/**
	 * 插入客户基本信息
	 *
	 * @Title: saveCustInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return boolean    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午8:37:05
	 */
	public boolean saveCustInfo () {
		String sql = "insert into t_cust_info (cust_id, cust_nam, id_type, id_no, cust_adde_desc, exp_date, sts_flg) (select user_id, reality_name, papers_type, id_number, communication_address, exp_date, sts_flg from t_user_info)";
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return updateBySQL(sql, condition) > 0?true : false;
	}
	
	/**
	 * 查询客户基本信息
	 *
	 * @Title: findCustInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_cust_info>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午8:41:21
	 */
	public List<t_cust_info> findCustInfo () {
		String sql = "select * from t_cust_info";
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	
	
}
