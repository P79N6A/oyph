package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import services.common.UserFundService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;

/**
 * 用户信息显示bean
 * 用于显示用户基本信息、资金相关信息、理财信息等
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月31日
 */
@Entity
public class ShowUserInfo {

	@Id
	@GeneratedValue
	public long id;
	
	/** 操作时间 */
	public Date time; 
	
	/** 用户名 */
	public String name;
	
	/** 手机号码 */
	public String mobile;
	
	/** 邮箱 */
	public String email;
	
	/** 可用余额 */
	public double balance;
	
	/** 冻结资金 */
	public double freeze;
	
	/** 登录次数 */
	public int login_count;
	
	/** 最后登录时间 */
	public Date last_login_time;
	
	/** 是否允许登录 */
	public boolean is_allow_login;
	
	/** 待还 */
	public double no_repayment;
	
	/** 待收 */
	public double no_receive;
	
	/** 累计借款 */
	public double borrow_total;
	
	/** 累计投资 */
	public double invest_total;
	
	public ShowUserInfo(){}
	
	public ShowUserInfo(Long id, Date time, String name, String mobile,
			String email, double balance, double freeze, int login_count,
			Date last_login_time, boolean is_allow_login, double no_repayment,
			double no_receive, double borrow_total, double invest_total) {
		super();
		this.id = id;
		this.time = time;
		this.name = name;
		this.mobile = mobile;
		this.email = email;
		this.balance = balance;
		this.freeze = freeze;
		this.login_count = login_count;
		this.last_login_time = last_login_time;
		this.is_allow_login = is_allow_login;
		this.no_repayment = no_repayment;
		this.no_receive = no_receive;
		this.borrow_total = borrow_total;
		this.invest_total = invest_total;
	}

	@Transient
	public String sign;//加密ID

	public String getSign() {
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**
	 * 用户资金防篡改字段状态。true-正常；false-异常
	 */
	@Transient
	public boolean fund_sign_status;
	public boolean getFund_sign_status() {
		ResultInfo result = Factory.getService(UserFundService.class).userFundSignCheck(this.id);
		if (result.code < 1) {
			return false;
		}
		
		return true;
	}
	
}
