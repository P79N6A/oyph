package daos.common;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_message_user;
import daos.base.BaseDao;

/**
 *  用户接收系统消息表Dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class MessageUserDao extends BaseDao<t_message_user> {

	protected MessageUserDao() {
	}

	/**
	 * 删除某个用户的某条站内信
	 *
	 * @param userId 用户的id
	 * @param msgId 站内信(msg)的id【不是t_message_user的id，而是message_id】
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public boolean deleteUserMsg(long userId, long msgId) {
		String sql = "DELETE FROM t_message_user WHERE user_id=:user_id AND message_id=:message_id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("message_id", msgId);
		
		int a = super.deleteBySQL(sql, condition);
		
		return a >= 1;
	}
	
}
