package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import models.common.entity.t_mass_sms_sending;
import models.common.entity.t_sms_sending;
import daos.base.BaseDao;

/**
 * 群发短信临时表dao
 * 
 * @author liudong
 * @createDate 2016年4月8日
 */
public class MassSmsSendingDao extends BaseDao<t_mass_sms_sending> {
	
	protected MassSmsSendingDao() {
		
	}
	
	/**
	 * 查询没有最近没有发送成功的几条
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<t_mass_sms_sending> queryLastUnsendedMassSms(int num){
		String querySQL = "SELECT *  FROM t_mass_sms_sending WHERE is_send=:is_send AND try_times < :try_times limit :num";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("try_times", Constants.TRY_SMS_TIMES);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
	
	/**
	 * 删除群发短信中已经发送或者发送失败次数达到上限的短信
	 * 
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public int deleteMassSended(){
		String excuSQL = "DELETE FROM t_mass_sms_sending WHERE is_send=:is_send OR try_times= :try_times ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		condition.put("try_times", Constants.TRY_SMS_TIMES);
		
		return deleteBySQL(excuSQL, condition);
	}
}
