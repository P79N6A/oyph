package models.common.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 实体:前台栏目
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月29日
 */
@Entity
public class t_column extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 栏目编号 */
	public String column_no;
	
	/** 栏目标识 */
	public String column_key;
	
	/** 栏目名称(前台显示用,可变) */
	public String name;

	/** 后台显示名称(不可变) */
	public String back_name;
}
