package services.common;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.SendCodeRecordsDao;
import models.common.entity.t_send_code;
import services.base.BaseService;

public class SendCodeRecordsService extends BaseService<t_send_code> {

	protected SendCodeRecordsDao sendCodeRecordsDao = Factory.getDao(SendCodeRecordsDao.class);
	
	protected SendCodeRecordsService(){
		super.basedao = sendCodeRecordsDao;
	}
	
	/**
	 * 添加短信发送记录
	 * @param mobile
	 * @param ip
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年2月29日
	 *
	 */
	public boolean addSendCodeRecords(String mobile, String ip) {
		t_send_code sendCodeRecords = new t_send_code();
		sendCodeRecords.time = new Date();
		sendCodeRecords.mobile = mobile;
		sendCodeRecords.ip = ip;
		
		return sendCodeRecordsDao.save(sendCodeRecords);
	}
	
	/***
	 * 根据手机号码查询短信发送记录
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年2月29日
	 *
	 */
	public List<t_send_code> querySendRecordsByMobile(String mobile){
		List<t_send_code> recordsList = findListByColumn("mobile = ?", mobile);
		
		return recordsList;
	}
	
	/**
	 * 根据IP地址查询手机短信发送记录
	 * @param ip
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年2月29日
	 *
	 */
	public List<t_send_code> querySendRecordsByIp(String ip){
		List<t_send_code> recordsList = findListByColumn("ip = ?", ip);
		
		return recordsList;
	}
	
	/**
	 * 清除手机发送短信记录
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年2月29日
	 *
	 */
	public ResultInfo delSendRecords(){
		ResultInfo result = new ResultInfo();
		int i = sendCodeRecordsDao.deleteSendCodeRecords();
		if(i < 0){
			result.code = -1;
			result.msg = "清除手机短信验证码发送记录表失败";
			
			return result;
		}
		result.code = 1;
		
		return result;
	}
}
