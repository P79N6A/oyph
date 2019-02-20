package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;

import models.common.entity.t_email_sending;
import daos.base.BaseDao;

/**
 *  系统发送email临时表的dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class EmailSendingDao extends BaseDao<t_email_sending> {

	private static final int TRY_TIMES_MAX = 4;
	protected EmailSendingDao() {
	}
	
	/**
	 * 删除所有已经发送的记录
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月23日
	 */
	public int deleteSended(){
		String excuSQL = "delete from t_email_sending where is_send=:is_send or try_times= "+TRY_TIMES_MAX;
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		
		return deleteBySQL(excuSQL, condition);
	}

	/**
	 * 查询没有最近没有发送的几条(尝试次数少于4次)
	 *
	 * @param num
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月25日
	 */
	public List<t_email_sending> queryLastUnsended(int num){
		String querySQL = "select *  from t_email_sending where is_send=:is_send and try_times < "+TRY_TIMES_MAX+" limit :num";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
}
