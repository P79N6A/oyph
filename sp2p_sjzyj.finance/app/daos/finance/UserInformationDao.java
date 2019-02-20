package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_user_information;

/**
 * 
 * @ClassName: UserInformationDao
 *
 * @description (P04 河北省P2P机构客户信息表)Dao层
 *
 * @author yaozijun
 *
 * @createDate 2018年10月12日
 */
public class UserInformationDao extends BaseDao<t_user_information> {
	
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
	public void truncateTable() {
		String sql = " truncate table t_user_information";
		this.deleteBySQL(sql, null);
	}
	
	/**
	 * 
	 * @Title: listAll
	 *
	 * @description 查询t_user_information所有数据 
	 *
	 * @param @return 
	 * 
	 * @return List<t_user_information>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月15日
	 */
	public List <t_user_information> listAll () {
		String sql = " select * from t_user_information";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
}
