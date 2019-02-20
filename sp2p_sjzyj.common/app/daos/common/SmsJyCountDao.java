package daos.common;

import java.util.Date;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.common.entity.t_sms_jy_count;

public class SmsJyCountDao extends BaseDao<t_sms_jy_count>{

	protected SmsJyCountDao () {}
	
	/**
	 * 查询
	 * @author guoShiJie
	 * @param mobile 手机号
	 * */
	public t_sms_jy_count querySmsCountByMobile (String mobile) {
		return this.findByColumn(" mobile = ? ", mobile);
	}
	
	
}
