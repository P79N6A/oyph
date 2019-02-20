package models.common.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 具体权限表(id不自增，作为code)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@Entity
public class t_right_action extends Model {

	/** 权限栏目表(关联t_right) */
	public Long right_id;
	
	/** 权限路径 */
	public String action;
	
	/** 权限描述 */
	public String description;
	
}
