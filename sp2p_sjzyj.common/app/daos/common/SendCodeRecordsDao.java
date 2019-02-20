package daos.common;

import models.common.entity.t_send_code;
import daos.base.BaseDao;

public class SendCodeRecordsDao extends BaseDao<t_send_code> {

	protected SendCodeRecordsDao() {}
	
	/**
	 * 清空短信发送记录表
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年2月29日
	 *
	 */
	public int deleteSendCodeRecords(){
		String sql = "TRUNCATE t_send_code";
		
		return deleteBySQL(sql, null);
	}
}
