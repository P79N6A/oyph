package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_email_sending;
import models.common.entity.t_sms_sending;
import daos.base.BaseDao;


/**
 * 系统发送SMS临时表的dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
public class SmsSendingDao extends BaseDao<t_sms_sending> {

	private static final int TRY_TIMES_MAX = 4;
	
	protected SmsSendingDao() {
	}
	
	/**
	 * 查询没有最近没有发送的几条(尝试次数少于4次)
	 *
	 * @param num
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月1日
	 */
	public List<t_sms_sending> queryLastUnsended(int num){
		String querySQL = "select *  from t_sms_sending where is_send=:is_send and try_times < "+TRY_TIMES_MAX+" limit :num";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", false);
		condition.put("num", num);
		
		return findListBySQL(querySQL, condition);
	}
	
	/**
	 * 删除所有已经发送的记录
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月23日
	 */
	public int deleteSended(){
		String excuSQL = "delete from t_sms_sending where is_send=:is_send or try_times= "+TRY_TIMES_MAX;
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_send", true);
		
		return deleteBySQL(excuSQL, condition);
	}
	
	/**
	 * 指定手机号查询没有发送的特殊短息条数
	 * @param mobile 手机号
	 * @param is_send 短息是否发送
	 * @param maxcount 特殊短息的最大发送条数  【4条】
	 * @author guoShiJie
	 * @createDate 2108.7.24
	 * */
	public Integer querySpecialNotSendingCountByMobileAndMaxCount (String mobile,boolean is_send,Integer maxcount) {
		String sql = "select count(*) from t_sms_sending where is_send = :isSend and mobile = :phone and maxCount = :count and try_times < "+TRY_TIMES_MAX;
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("isSend", is_send);
		condition.put("phone", mobile);
		condition.put("count", maxcount);
		return this.countBySQL(sql, condition);
	}
	/**
	 * 指定手机号码查询没有发送的短息条数
	 * @param mobile 手机号
	 * @param is_send 短息石是否发送
	 * @author guoShiJie
	 * @createDate 2018.7.25
	 * */
	public Integer queryNotSendingCountByMobile (String mobile,boolean is_send) {
		String sql = "select count(*) from t_sms_sending where is_send = :isSend and mobile = :phone and try_times < "+TRY_TIMES_MAX;
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("isSend", is_send);
		condition.put("phone", mobile);
		return this.countBySQL(sql, condition);
	}
}
