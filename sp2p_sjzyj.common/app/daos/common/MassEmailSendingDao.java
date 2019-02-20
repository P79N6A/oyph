package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import models.common.entity.t_email_sending;
import models.common.entity.t_mass_email_sending;
import daos.base.BaseDao;

/**
 * 群发邮件的临时表dao
 * 
 * @author liudong
 * @createDate 2016年4月8日
 */
public class MassEmailSendingDao extends BaseDao<t_mass_email_sending> {

	protected MassEmailSendingDao(){
		
	}
	
	/**
	 * 删除群发邮件表中 已经发送的记录
	 * 
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public int deleteMassSended(){
		String excuSQL = "DELETE FROM t_mass_email_sending WHERE is_send=:is_send OR try_times= :try_times ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		condition.put("try_times", Constants.TRY_EMAIL_TIMES);
		
		return deleteBySQL(excuSQL, condition);
	}
	
	/**
	 * 
	 * 群发邮件、选择没有发送成功的邮件
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月9日
	 *
	 */
	public List<t_mass_email_sending> queryLastUnsendedMassEmail(int num){
		String querySQL = "SELECT * FROM t_mass_email_sending WHERE is_send=:is_send AND try_times <= :try_times LIMIT :num ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("try_times", Constants.TRY_EMAIL_TIMES);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
}
