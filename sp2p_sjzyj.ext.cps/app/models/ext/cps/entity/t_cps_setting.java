package models.ext.cps.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

/**
 * 实体:CPS设置
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
@Entity
public class t_cps_setting extends Model {
	
	/** 键 */
	public String _key;
	
	/** 值 */
	public String _value;
	
	/** 描述 */
	public String description;
}
