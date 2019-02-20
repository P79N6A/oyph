package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_user_profession;
import daos.base.BaseDao;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class UserProfessionDao extends BaseDao<t_user_profession> {

	protected UserProfessionDao() {}
	
	/**
	 * 查询职业信息
	 * @return
	 */
	public List<t_user_profession> queryByAccount() {
		
		String sql="SELECT * FROM t_user_profession p inner join t_user_info u on p.user_id = u.user_id WHERE u.is_account =:account";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("account", 1);
		
		return this.findListBySQL(sql, condition);
	}
}
