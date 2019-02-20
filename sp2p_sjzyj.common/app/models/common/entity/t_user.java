package models.common.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import models.core.entity.t_invest;
import models.entity.t_teams;
import models.main.bean.TeamRule;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Security;
import common.utils.TimeUtil;
import daos.core.InvestDao;
import play.db.jpa.Model;

/**
 * 用户表(注册、登录信息)
 *  
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月15日
 */

@Entity
public class t_user extends Model {
	
	public t_user(){
	}
	
	public t_user(Date time, String name, String mobile, String password) {
		super();
		this.time = time;
		this.name = name;
		this.mobile = mobile;
		this.password = password;
	}

	/** 创建时间 */
	public Date time;

	/** 用户名 */
	public String name;

	/** 手机号码 */
	public String mobile;

	/** 登录密码 */
	public String password;

	/** 连续登录失败次数 */
	public int password_continue_fails;

	/** 密码连续错误被锁定 */
	public boolean is_password_locked;

	/** 密码连续错误被锁定时间 */
	public Date password_locked_time;

	/** 是否允许登录(锁定) */
	public boolean is_allow_login;

	/** 后台管理员执行锁定的时间 */
	public Date lock_time;

	/** 登录次数 */
	public int login_count;

	/** 上次登录时间 */
	public Date last_login_time;

	/** 上次登录ip */
	public String last_login_ip;
	
	
	public long supervisor_id;
	
	public long proxy_salesMan_id;

	/** 上次登录入口(枚举)：1 pc 2 app 3 wechat */
	private int last_login_client;
	
	public Client getLast_login_client() {
		Client client = Client.getEnum(last_login_client);
		return client;
	}

	public void setLast_login_client(Client last_login_client) {
		this.last_login_client = last_login_client.code;
	}
	
	private static t_user t;
	public static  t_user getT(){
		if(t==null){
			t=new t_user();
		}
		return t;
	}
	
	@Transient
	public int months;
	
	public int getMonths() {
		return months;
	}
	public void setMonths(int months) {
		this.months = months;
	}
	
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	/**app加密ID*/
	@Transient
	public String appSign;
	
	public String getAppSign() {
		
		return Security.addSign(this.id, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	@Transient
	public double investAmount;
	public double getInvestAmount(){
		Double amount = new Double(0);
		amount = t_invest.find("select sum(amount) from t_invest where user_id=? and transfer_status=0  ", this.id).first();
		return amount==null?0:amount;
	}
	
	@Transient
	public double monthAmount;
	public double getMonthAmount(){
		Double amount = new Double(0);
		
		Calendar date = Calendar.getInstance(); 
		int year = date.get(Calendar.YEAR);
	    int month = (date.get(Calendar.MONTH))+1;
	    int years = date.get(Calendar.YEAR);
	    int montht = (date.get(Calendar.MONTH))+2;
	    if((month==1 &&t.months==1) || (month==2 && t.months==2) || (month==3 && t.months==3)){
	    	month = 12;
	    	year = year-1;
	    	montht = 1;
	    }else if((month==1 && t.months==2) || (month==2 && t.months==3)) {
	    	month = 11;
	    	year = year-1;
	    	montht = 12;
	    }else if(month==1 && t.months==3) {
	    	month = 10;
	    	year = year-1;
	    	montht = 11;
	    }else{
	    	month = month - t.months;
	    	montht = montht - t.months;
	    }
	    Date sdate = new Date();
	    Date sdates = new Date();
	    sdate = TimeUtil.strToDate(year+"-"+month+"-01 00:00:00");
	    sdates = TimeUtil.strToDate(years+"-"+montht+"-01 00:00:00");
	    
	    InvestDao invest = common.utils.Factory.getDao(InvestDao.class);
	    amount = invest.findZhi(this.id, sdate, sdates);
		
		return amount==null?0:amount;
	}
	
	@Transient
	public double monthCommission;
	public double getMonthCommission(){
		if(this.monthAmount <=0){
			return 0;
		}else{
			return TeamRule.queryAmount(this.monthAmount/10000);
		}
	}
	
	@Transient
	public double totalCommission;
	public double getTotalCommission(){
		if(this.investAmount <=0){
			return 0;
		}else{
			return TeamRule.queryAmount(this.investAmount/10000);
		}
	}
	
	@Transient
	public String realName;
	public String getRealName(){
		t_user_info info = t_user_info.find("user_id=?", this.id).first();
		if(info!=null){
			return info.reality_name;
		}else{
			return "";
		}
		
	}
	
	
	
	
}
