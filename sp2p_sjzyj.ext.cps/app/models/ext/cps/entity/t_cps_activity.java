package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:CPS活动表
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年06月12日
 */
@Entity
public class t_cps_activity extends Model {

	public String title;
	
	public Date begin_time;
	
	public Date end_time;
	
	/** is_use '0-下架\r\n1-上架'  */
	private boolean is_use;
	
	public double integral_ratio;
	
	public Integer cutoff_time;
	
	public int register_type;
	
	public int first_type;
	
	public Date time;
	
	public int account_type;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.ADS_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(Date begin_time) {
		this.begin_time = begin_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}

	public double getIntegral_ratio() {
		return integral_ratio;
	}

	public void setIntegral_ratio(double integral_ratio) {
		this.integral_ratio = integral_ratio;
	}

	public Integer getCutoff_time() {
		return cutoff_time;
	}

	public void setCutoff_time(Integer cutoff_time) {
		this.cutoff_time = cutoff_time;
	}

	public int getRegister_type() {
		return register_type;
	}

	public void setRegister_type(int register_type) {
		this.register_type = register_type;
	}

	public int getFirst_type() {
		return first_type;
	}

	public void setFirst_type(int first_type) {
		this.first_type = first_type;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
}
