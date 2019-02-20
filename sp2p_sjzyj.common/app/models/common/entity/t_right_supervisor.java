package models.common.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 管理员-权限表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@Entity
public class t_right_supervisor extends Model {

	/** 管理员id */
	public Long supervisor_id;
	
	/** 权限id(t_right) */
	public Long right_id;
}
