package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_user_live;
import daos.base.BaseDao;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class UserLiveDao extends BaseDao<t_user_live> {

	protected UserLiveDao() {}
	
	/**
	 * 查询居住信息
	 * @return
	 */
	public List<t_user_live> queryByAccount() {

		String sql="SELECT * FROM t_user_live l inner join t_user_info u on l.user_id = u.user_id  WHERE u.is_account =:account";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("account", 1);
		
		return this.findListBySQL(sql, condition);
	}
}
