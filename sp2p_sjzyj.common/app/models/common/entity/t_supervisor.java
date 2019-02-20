package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:后台管理员
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月15日
 */
@Entity
public class t_supervisor extends Model {

	/** 添加时间 */
	public Date time;
	
	/** 管理员账号 */
	public String name;
	
	/** 真实姓名 */
	public String reality_name;
	
	/** 手机 */
	public String mobile;
	
	/** 录登密码 */
	public String password;
	
	/** 
	 * 锁定状态
		0-未锁定
		2-管理员直接锁定
	 */
	private int lock_status;
	
	/** 登录次数 */
	public long login_count;
	
	/** 上次登录时间 */
	public Date last_login_time;
	
	/** 上次登录ip */
	public String last_login_ip;
	
	/** 创建者id */
	public long creater_id;
	
	/** U盾密钥 */
	public String ukey;
	
	
	/**
	 * 推广码
	 */
	public String  extension;
	
	/** id验签加密后的字符串 */
	@Transient
	public String sign;
	
	public t_supervisor(){}
	
	public t_supervisor(Long id,String name,int lock_status){
		this.id = id;
		this.name = name;
		this.lock_status = lock_status;
	}
	
	public LockStatus getLock_status() {
		LockStatus status = LockStatus.getEnum(this.lock_status);
		
		return status;
	}

	public void setLock_status(LockStatus lock_status) {
		this.lock_status = lock_status.code;
	}

	public String getSign() {
		String signID = Security.addSign(id, Constants.SUPERVISOR_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

	/**
	 * 枚举:管理员锁定状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	public enum LockStatus {
		
		/** 0:正常 */
		STATUS_0_NORMAL(0,"正常"),
		
		/** 2:管理员直接锁定 */
		STATUS_2_LOCKEDBYSYS(2,"锁定");
		
		public int code;
		public String value;
		
		
		private LockStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static LockStatus getEnum(int code) {
			LockStatus[] statuies = LockStatus.values();
			for (LockStatus stat : statuies) {
				if (stat.code == code) {

					return stat;
				}
			}

			return STATUS_2_LOCKEDBYSYS;
		}
	}
	
}

