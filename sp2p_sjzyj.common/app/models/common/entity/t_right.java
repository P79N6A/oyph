package models.common.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 权限表(ID作为code，不自增)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
@Entity
public class t_right extends Model {

	/** 权限栏目名称 */
	public String name;
	
	/** 权限栏目描述 */
	public String description;
}
