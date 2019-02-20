package daos.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.TimeUtil;
import daos.base.BaseDao;
import models.common.entity.t_pact;
import models.finance.bean.UserInfoBean;
import models.finance.entity.t_max_ten_user_information;

/**
 * 
 *@ClassName: MaxTenUserInformationDao
 *
 * @description (P05 河北省P2P机构最大十家客户信息表)Dao层
 *
 * @author yaozijun
 * @createDate 2018年10月5日
 */
public class MaxTenUserInformationDao extends BaseDao<t_max_ten_user_information> {
	
	/**
	 * 
	 * @Title: truncateTable
	 *
	 * @description 清空表中的数据
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public int truncateTable(Date time) {
		/** 获取当前时间 为防止第一次上报错误，第二次上报是数据冲突，当第二次上报时先删除第一次上报的数据，然后重新上传。 */
		String record = TimeUtil.dateToStrDate(time);
		/** 根据当前时间查询数据 */ 
		String sql = " DELETE FROM t_max_ten_user_information  where time = :time";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", record);
		
		return this.deleteBySQL(sql, condition);
	}
	
	/**
	 * 
	 * @Title: findListByType
	 *
	 * @description 查询十家各自的期末余额
	 *
	 * @param @return 
	 * 
	 * @return List<t_max_ten_user_information>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public List<t_max_ten_user_information> findListByType() {
		String sql = " SELECT tb.*, IFNULL(SUM(tb.repayment_corpus),0) AS debt FROM t_bill tb INNER JOIN t_bid t ON t.id = tb.user_id WHERE tb.status IN (0, 1) GROUP BY tb.user_id  ORDER BY IFNULL(SUM(tb.repayment_corpus), 0) desc  LIMIT 10 ";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 
	 * @Title: listAll
	 *
	 * @description 查询新表的所有数据
	 *
	 * @param @return 
	 * 
	 * @return List<t_max_ten_user_information>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public List <t_max_ten_user_information> listAll (Date time) {
		/** 时间参数，判断时间，保证每次获取十条信息 */
		String record = TimeUtil.dateToStrDate(time);
		String sql = " SELECT t.* FROM t_max_ten_user_information t where time = :time";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", record);
		
		return this.findListBySQL(sql, condition);
	}
	
}
