package services.common;

import java.util.Date;
import java.util.List;

import common.enums.JYSMSModel;
import common.utils.DateUtil;
import common.utils.Factory;
import daos.common.SmsJyCountDao;
import daos.common.SmsSendingDao;
import models.common.entity.t_sms_jy_count;
import play.Logger;
import services.base.BaseService;

public class SmsJyCountService extends BaseService<t_sms_jy_count>{
	protected static SmsSendingDao smsSendingDao= Factory.getDao(SmsSendingDao.class);
	private SmsJyCountDao smsJyCountDao = Factory.getDao(SmsJyCountDao.class);
	
	protected SmsJyCountService () {
		this.basedao = this.smsJyCountDao;
	}

	/**
	 * 查询手机号的t_sms_jy_count 数据
	 * */
	public t_sms_jy_count querySmsCountByMobile (String mobile) {
		if (mobile == null) {
			return null;
		}
		t_sms_jy_count jysms = smsJyCountDao.querySmsCountByMobile(mobile);
		if (jysms != null) {
			return jysms;
		}
		t_sms_jy_count jy = new t_sms_jy_count();
		jy.special_count = 0;
		jy.mobile = mobile;
		jy.total_count = 0;
		jy.time = new Date();
		return smsJyCountDao.saveT(jy);
	}
	
	/**
	 * 通知类短息添加
	 * @author guoShiJie
	 * @param count 一天内短息发送条数的最大限制数量
	 * @param mobile 手机号
	 * @param type 短息发送类型(0验证码1通知)
	 * */
	public boolean updateNoticeSmsCount (Integer maxcount ,String mobile) {
		if (mobile == null) {
			return false;
		}
		
		Date today = new Date();
		t_sms_jy_count jysms = querySmsCountByMobile(mobile);
		if (jysms == null) {
			return false;
		}
		if (DateUtil.isSameDay(jysms.time, today)) {
			if (maxcount == 4) {
				if (jysms.special_count >= maxcount) {
					return false;
				}
				int special = jysms.special_count + 1;
				jysms.special_count = special;
				int total = jysms.total_count + 1;
				jysms.total_count = total;
				jysms.time = today;
				return smsJyCountDao.save(jysms);
			}
			if (maxcount == 10) {
				if (jysms.total_count >= maxcount) {
					return false;
				}
				int total = jysms.total_count + 1;
				jysms.total_count = total;
				jysms.time = today;
				return smsJyCountDao.save(jysms);
			}
			
			return false;
		}
		if (maxcount == 4) {
			jysms.special_count = 1;
			jysms.total_count = 1;
			jysms.time = today;
			
			return smsJyCountDao.save(jysms);
		}
		if (maxcount == 10) {
			jysms.special_count = 0;
			jysms.total_count = 1;
			jysms.time = today;
			
			return smsJyCountDao.save(jysms);
		}
		return false;
	}
	
	/**
	 * 判断是否符合发送短息
	 * 1min条
	 * 1小时4条
	 * 1天十条
	 * @param mobile 手机号
	 * @param maxcount 短息模板的最大发送数量
	 * */
	public boolean isSend (String mobile,Integer maxcount) {
		
		Date today = new Date();
		if (mobile == null || maxcount == null) {
			return false;
		}
		t_sms_jy_count jysms = querySmsCountByMobile(mobile);
		if (jysms == null) {
			return false;
		}
		
		if (!DateUtil.isSameDay(jysms.time, today)) {
			Long day = DateUtil.getDatePoor(today, jysms.time).get("day");
			Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
			if (day >= 1 || hour >= 2) {
				return true;
			}
			return false;
		}
		if (DateUtil.isSameDay(jysms.time, today)) {
			if (maxcount == 4) {
				if (jysms.special_count >= maxcount) {
					return false;
				}
			}
			if (maxcount == 10) {
				if (jysms.total_count >= maxcount) {
					return false;
				}
			}
			
			for (int i = jysms.total_count+1;i <= 10; i++) {
				if (i == 1) {
					return true;
				}else if (i == 2) {
					Long sec = DateUtil.getDatePoor(today, jysms.time).get("sec");
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (sec >= 30 || min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 3) {
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 4) {
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 5) {
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (hour >= 1) {
						return true;
					}
				}else if (i == 6) {
					Long sec = DateUtil.getDatePoor(today, jysms.time).get("sec");
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (sec >= 30 || min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 7) {
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 8) {
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (min >= 1 || hour >= 1) {
						return true;
					}
				}else if (i == 9) {
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (hour >= 1) {
						return true;
					}
				}else if (i == 10) {
					Long min = DateUtil.getDatePoor(today, jysms.time).get("min");
					Long hour = DateUtil.getDatePoor(today, jysms.time).get("hour");
					if (min >= 15 || hour >= 1) {
						return true;
					}
				}
				return false;
			}
		}
		
		return false;
	}
	/**从JYSMSModel枚举类中获取
	 * 判断短息发送的最大条数
	 * @author guoShiJie
	 * @createDate 2018.7.24
	 * */
	public static Integer judgeMaxCount (String number) {
		
		if (
				number.equals("2213") || number.equals("2216") 
				|| number.equals("2234") || number.equals("2333") 
				|| number.equals("2354") || number.equals("2366") 
				|| number.equals("3335") || number.equals("2201")
			) {
			return 10;
		};
		
		if (
				number.equals("2348") || number.equals("2351") 
				|| number.equals("2357") || number.equals("2363") 
				|| number.equals("2378") || number.equals("2453")
			) {
			return 4;
		};
		
		return 0;
	}
	
	/**判断短息是否还能发送出去（条件：特殊短息 < 4条；短息总条数 < 10条）
	 * 同一手机号  发送成功的短息条数 【t_sms_jy_count表】+ 即将发送的短息条数【本地生成的短息记录条数（未发送）】t_sms_sending表记录
	 * @param mobile 手机号码
	 * @param is_send 短息是否发送
	 * @param maxcount 短息最大的发送条数
	 * @author guoShiJie
	 * @createDate 2018.7.24
	 * */
	public Boolean smsSpecialCount (String mobile,Boolean is_send,Integer maxcount) {
		if (mobile == null || is_send == null || maxcount == null) {
			return false;
		}
		Integer special = 0;
		Integer total = smsTotalCount(mobile,false);
		Integer notcount = smsSendingDao.querySpecialNotSendingCountByMobileAndMaxCount(mobile, is_send,4);
		t_sms_jy_count jycount = querySmsCountByMobile(mobile);
		if (jycount == null) {
			return false;
		}
		if (jycount != null){
			special = notcount + jycount.special_count;
		}
		if (special == 4 && total < 10 && maxcount == 10) {
			return true;
		}
		if (special < 4 && total < 10 ) {
			return true;
		}
		return false;
		
	}
	/**
	 *判断是否还能生成短息模板
	 *@param mobile 手机号
	 *@param model JYSMSModel 模板类
	 *@author cteateDate 2018.7.27
	 * */
	public Boolean judgeIsSend (String mobile,JYSMSModel model) {
		Date today = new Date();
		if (mobile == null || model == null) {
			return false;
		}
		t_sms_jy_count jycount = querySmsCountByMobile(mobile);
		if (jycount == null) {
			return false;
		}
		Integer maxcount = judgeMaxCount(model.tplId);
		if (!DateUtil.isSameDay(jycount.time, today)) {
			return true;
		}
		if (DateUtil.isSameDay(jycount.time, today)) {
			if (smsSpecialCount(mobile,false,maxcount)) {
				if (isSend(mobile, maxcount)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 短息总数 10条
	 * 同一手机号  发送成功的短息条数【t_sms_jy_count表】 + 即将发送的短息条数【本地生成的短息记录条数（未发送）】t_sms_sending表记录
	 * @author guoShiJie
	 * @param mobile 手机号
	 * @param is_send 短息是否发送
	 * @createDate 2018.7.25
	 * */
	public Integer smsTotalCount (String mobile,Boolean is_send) {
		if (mobile == null || is_send == null) {
			return 0;
		}
		Integer local = 0;
		Integer notcount = smsSendingDao.queryNotSendingCountByMobile(mobile, is_send);
		t_sms_jy_count jycount = querySmsCountByMobile(mobile);
		if (jycount != null) {
			local = notcount + jycount.total_count;
		}
		
		return local;
		
	}
	
	/**
	 * 实时向t_sms_jy_count表中添加短息发送成功的数据[应用于短息验证码的发送]
	 * @author guoShiJie
	 * @createDate 2018.7.25
	 * */
	public Boolean updateCount (String mobile,JYSMSModel model,Boolean isSend) {
		
		if (mobile == null || model == null ||isSend == null) {
			return false;
		}
		if (isSend) {
			Integer maxcount = judgeMaxCount(model.tplId);
			
			return updateNoticeSmsCount(maxcount,mobile);
		}
		
		return false;
	}
}
