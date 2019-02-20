package models.ext.cps.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 实体:用户CPS账户
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
@Entity
public class t_cps_account extends Model {
    
	/** 创建时间 */
	public Date time;
	
	/** 用户id */
	public Long user_id;
	
	/** 用户cps账户余额 */
	public double balance;
}
