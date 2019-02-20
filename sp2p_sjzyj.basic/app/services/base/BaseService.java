package services.base;

import java.util.List;
import java.util.Map;

import play.db.jpa.Model;
import daos.base.BaseDao;

/**
 * 公共的Service接口的具体实现，需要在具体的commonService中拓展该类，无法实例化化该对象
 *
 * @param <T> 泛型，play.db.jpa.Model的子类(数据库实体,即带@Entity注解)
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月3日
 */
public abstract class BaseService<T extends Model> {

	protected BaseDao basedao;

	/**
	 * 根据实体的ID的查找实体
	 *
	 * @param id 实体的id（对应数据表的id主键字段）
	 * @return 实体
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public T findByID(long id) {
		return (T) basedao.findByID(id);
	}
	
	/**
	 * 根据条件查询实体（有多个，只返回其中一个,多个时取第一个）
	 * <br>条件格式：以“?”作为占位符的where语句，
	 * <br>示例：
	 * <br>condition: "name like ? OR mobile like ?"
	 * <br>params: "%"+key+"%", "%"+key+"%"
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public T findByColumn(String condition, Object... params) {
		return (T) basedao.findByColumn(condition, params);
	}
	
	/**
	 * 根据条件查询所有符合的实体，支持排序
	 * <br>条件格式：以“?”作为占位符的where语句，
	 * <br>示例：
	 * <br>condition: "name like ? OR mobile like ? order by "
	 * <br>params: "%"+key+"%", "%"+key+"%"
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return 返回所有符合的实体
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<T> findListByColumn(String condition, Object... params) {
		return basedao.findListByColumn(condition, params);
	}

	/**
	 * 返回实体类表中的所有实体
	 *
	 * @return 返回该表的所有实体对象
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<T> findAll() {
		return basedao.findAll();
	}
	
	//liuyang begin 2016-12-30-----------------------------
	/**
	 * 修改实体类表中的属性
	 * @param sql
	 * @param args
	 * @createDate 2016年12月30日
	 */
	public void updateBySupervisorId(String sql,Map<String,Object> args){		
		basedao.updateBySQL(sql, args);
	}
	//liuyang begin 2016-12-30-----------------------------
}
