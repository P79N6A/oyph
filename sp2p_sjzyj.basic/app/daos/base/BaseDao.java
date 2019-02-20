package daos.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import play.db.jpa.Model;

import com.shove.Convert;
import com.shove.JSONUtils;

import common.utils.LoggerUtil;
import common.utils.PageBean;

/**
 * 底层操作数据接口的具体实现(调用父类GenericDaoImpl进行实现)
 *
 * @param <T> 泛型，play.db.jpa.Model的子类(数据库实体,即带@Entity注解)
 * @description 在本类中默认的:泛型“B”表示Bean(和实体类似，但是没有对应的table和view)
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月3日
 */
public class BaseDao<T extends Model> extends GenericDao<T> {
	
	/** 数据库异常标志 */
	public static final int ERROR = -99;
	
	/**
	 * 添加或修改一个实体到数据库中
	 *
	 * @param t 实体
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public boolean save(T t) {
		try {
			super._save(t);
			return true;
		} catch (Exception e) {
			LoggerUtil.error(true, e, "添加或修改数据时出错。【T:%s】", entityClass);
			return false;
		}
	}
	
	/**
	 * 添加或者修改一个实体到数据库并返回实体对象
	 * @param t
	 * @return
	 */
	public T saveT(T t) {
		try {
			
			return super._saveT(t) ;
		} catch (Exception e) {
			LoggerUtil.error(true, e, "添加或修改数据时出错。【T:%s】", entityClass);
			return null ;
		}
	}
	
	/**
	 * 根据实体的ID删除数据
	 *
	 * @param id 实体的主键ID
	 * @return 返回删除数据的行数(报错时返回-99)
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public int delete(Long id) {
		try {
			return super._delete(id);
		} catch (Exception e) {
			LoggerUtil.error(true, e, "根据id删除数据时出错。【T:%s, ID:%s】", entityClass, id);
			return ERROR;
		}
	}
	
	/**
	 * 通过sql语句删除数据库数据
	 * 
	 * @param sql 本地SQL
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 删除数据的行数(报错时返回-99)
	 * 
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年11月4日
	 */
	public int deleteBySQL(String sql, Map<String,Object> args) {
		try {
			return super._executSQL(sql, args);
		} catch (Exception e) {
			LoggerUtil.error(true, e, "根据SQL删除数据时出错。【T:%s;sql:%s;args:%s】", entityClass, sql, args);
			return ERROR;
		}
	}

	/**
	 * 通过sql语句更新数据库数据
	 *
	 * @param sql 本地SQL
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 更新的数据行数(报错时返回-99)
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public int updateBySQL(String sql,Map<String,Object> args) {
		try {
			return super._executSQL(sql,args);
		} catch (Exception e) {
			LoggerUtil.error(true, e, "根据SQL更新数据时出错。【T:%s;sql:%s;args:%s】", entityClass, sql, args);
			return ERROR;
		}
	}
	
	/**
	 * 执行更新操作的sql语句
	 *
	 * @param sql 本地SQL
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 返回执行的调速(报错时返回-99)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public int executSQL(String sql, Map<String,Object> args) {
		try {
			return super._executSQL(sql,args);
		} catch (Exception e) {
			LoggerUtil.error(true, e, "根据SQL更新数据时出错。【T:%s;sql:%s;args:%s】", entityClass, sql, args);
			return ERROR;
		}
	}

	/**
	 * 根据实体的ID的查找实体
	 *
	 * @param id 实体的id（对应数据表的id主键字段）
	 * @return 返回查找到的实体对象
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public T findByID(Long id) {
		try {
			return super._findByID(id);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据ID查找对象时出错【T:%s,id:%s】", entityClass, id);
			return null;
		}
	}
	

	/**
	 * 
	 * 根据条件查询实体(多个实体，只返回第一个)
	 *
	 * @param condition 条件     
	 * @param params 参数数组
	 * @return 返回所有符合的实体
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 *
	 */
	public T findByColumn(String condition, Object... params) {
		List<T> list = findListByColumn(condition, params);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 
	 * 根据条件查询所有符合的实体
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return 返回所有符合的实体
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 *
	 */
	public List<T> findListByColumn(String condition, Object... params) {
		try {
			return _findListByColumn(condition, params);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据条件查询所有符合的实体，【T:%s;condition:%s;params:%s】", entityClass, condition, Arrays.toString(params));
			return null;
		}
	}
	
	/**
	 * 通过条件进行分页查询实体（带搜索条件的不建议使用该方法）
	 * 注意:如果该分页带有（或可能有）搜索条件，推荐使用pageOfBySQL
	 *
	 * @param currPage 页码
	 * @param pageSize 分页大小
	 * @param condition 条件
	 * @param params 参数数组
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月22日
	 */
	public PageBean<T> pageOfByColumn(int currPage, int pageSize, String condition, Object... params) {
		PageBean<T> page = new PageBean<T>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = countByColumn(condition, params);;
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
		
		List<T> listOfT = null;
		try {
			listOfT = _findListByColumnLimit((currPage - 1) * pageSize, pageSize, condition, params);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过条件进行分页查询实体。【T:%s;condition:%s;params:%s】", entityClass, Arrays.toString(params));
		}
		
		page.page = listOfT;
 		
		return page;
	}
	
	/**
	 * 使用本地SQL查找实体(如果有多个，只会返回一个)
	 *
	 * @param sql 本地SQL(要求返回该实体的所有属性,@Transient注解的除外，建议用select * from)
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 查找到符合要求的实体
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public T findBySQL(String sql,Map<String,Object> args) {
		List<T> list = findListBySQL(sql,args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	

	/**
	 * 根据本地sql查找所有符合的实体
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 所有符合条件的实体
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<T> findListBySQL(String sql, Map<String,Object> args) {
		try {
			return super._findListBySQL(sql,args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据本地sql查找所有符合的实体【T:%s,SQL:%s】",entityClass,sql);
			
			return null;
		}
	}
	
	/**
	 * 通过SQL进行分页查询实体
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页显示的记录数
	 * @param countSQL 统计总记录的SQL语句
	 * @param querySQL 查询的SQL语句
	 * @param conditionArgs SQL语句的条件(sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)),此时要求<b>countSQL和querySQL的参数一致<b>
	 * @return 如果记录数为0,则page.page为null
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public PageBean<T> pageOfBySQL(int currPage, int pageSize, String countSQL, String querySQL, Map<String, Object> conditionArgs) {
		PageBean<T> page = new PageBean<T>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = findSingleIntBySQL(countSQL, 0, conditionArgs);
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
		
		List<T> listOfT = null;
		try {
			listOfT = _findListBySQLLimit(querySQL,conditionArgs,(currPage - 1) * pageSize,pageSize);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过SQL进行分页查询实体【T:%s,SQL:%s】",entityClass,querySQL);
			
		}
		
		page.page = listOfT;
 		
		return page;
	}
	
	/**
	 * 使用本地HQL查找实体(如果有多个，只会返回一个)
	 *
	 * @param hql HQL语句(如果语句中有实体名称，请使用T.class.getName()的方式，或者直接使用表名替换)
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 符合条件的实体
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public T findByHQL(String hql, Map<String,Object> args) {
		List<T> list = findListByHQL(hql, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	
	/**
	 * 根据hql查找所有符合的实体
	 *
	 * @param hql hql语句(建议使用 "select t from "+T.class.getName()+" t ")
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 所有符合的实体的列表
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<T> findListByHQL(String hql, Map<String,Object> args) {
		try {
			return super._findListByHQL(hql,args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据Hql查找所有符合的实体【T:%s,HQL:%s】", entityClass, hql);
			return null;
		}
	}
	
	/**
	 * 查找实体类表中的所有实体
	 *
	 * @return 返回该表的所有实体对象
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<T> findAll() {
		try {
			return super._findAll();
		} catch (Exception e) {
			LoggerUtil.error(false, e, "查找实体类表中的所有实体【T:%s】",entityClass);
			return null;
		}
	}
	
	/**
	 * 查找实体类表中的所有实体,分页
	 *
	 * @param currPage 页码
	 * @param pageSize 分页大小
	 * @param condition 条件
	 * @param params 参数数组
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月22日
	 */
	public PageBean<T> pageOfAll(int currPage, int pageSize) {
		PageBean<T> page = new PageBean<T>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = countALL();
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
		
		List<T> listOfT = null;
		try {
			listOfT = _findAllLimit((currPage - 1) * pageSize,pageSize);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "分页查询所有实体【T:%s】",entityClass);
			
		}
		
		page.page = listOfT;
 		
		return page;
	}
	
	// 查找元素：T-数据库实体, List<T>-数据库实体集合, PageBean<T>-数据库实体分页         end  
	
	
	// 查找元素：单值, 单值集合      begin 
	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public Object findSingleBySQL(String sql, Map<String,Object> args) {
		List<Object> list = findListSingleBySQL(sql, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值(int) 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public int findSingleIntBySQL(String sql, int defaultValue, Map<String,Object> args) {
		Object o = findSingleBySQL(sql, args);
		return o==null?defaultValue:Convert.strToInt(o.toString(), defaultValue);
	}
	
	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值(long) 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public long findSingleLongBySQL(String sql, long defaultValue, Map<String,Object> args) {
		Object o = findSingleBySQL(sql, args);
		return o==null?defaultValue:Convert.strToLong(o.toString(), defaultValue);
	}
	
	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值(double) 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public double findSingleDoubleBySQL(String sql, double defaultValue, Map<String,Object> args) {
		Object o = findSingleBySQL(sql, args);
		return o==null?defaultValue:Convert.strToDouble(o.toString(), defaultValue);
	}
	
	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值(boolean) 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public boolean findSingleBooleanBySQL(String sql, boolean defaultValue, Map<String,Object> args) {
		Object o = findSingleBySQL(sql, args);
		return o==null?defaultValue:Convert.strToBoolean(o.toString(), defaultValue);
	}
	
	/**
	 * 根据本地sql查找单个数据(如果有多个，将只会返回一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值(String) 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public String findSingleStringBySQL(String sql, String defaultValue, Map<String,Object> args) {
		Object o = findSingleBySQL(sql, args);
		return o==null?defaultValue:o.toString();
	}
	
	/**
	 * 根据本地sql查找单列数据
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 符合要求的所有单值
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<Object> findListSingleBySQL(String sql, Map<String,Object> args) {
		try {
			return super._findListSingleBySQL(sql, args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据本地sql查找单列数据。【T:%s;SQL:%s;args:%s】", entityClass, sql, args);
			return null;
		}
	}

	/**
	 * 根据本地hql查找单个数据(如果有多个，将只会返回一个)
	 * 
	 * @param hql hql语句
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return 单值
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public Object findSingleByHQL(String hql, Map<String,Object> args) {		
		List<Object> list = findListSingleByHQL(hql, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 根据hql查找单列数据
	 *
	 * @param hql hql语句
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<Object> findListSingleByHQL(String hql, Map<String,Object> args) {		
		try {
			return super._findListSingleByHQL(hql, args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据hql查找单列数据。【T:%s;HQL:%s;args:%s】", entityClass, hql, args);
			return null;
		}
	}
	// 查找元素：单值, 单值集合      end 
	
	
	// 查找元素：Map<String, Object>, List<Map<String, Object>>, PageBean<Map<String, Object>>    begin 
	/**
	 * 通过本地SQL返回一个Map对象(如果有多个返回值，将返回其中的一个)
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return Map对象（其中Map的key为列名，例如"select id,name from table"，返回的Map中key分别为id和name。）
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public Map<String, Object> findMapBySQL(String sql, Map<String,Object> args) {
		List<Map<String, Object>> list = findListMapBySQL(sql, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 通过本地SQL返回一列Map对象
	 *
	 * @param sql 本地sql
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return List<Map<String,Object>> Map对象列表（其中Map的key为列名，例如"select id,name from table"，返回的Map中key分别为id和name。）
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<Map<String, Object>> findListMapBySQL(String sql, Map<String,Object> args) {
		try {
			return super._findListMapBySQL(sql, args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过本地SQL返回一列Map对象。【T:%s;SQL:%s;args:%s】", entityClass, sql, args);
			return null;
		}
	}
	
	/**
	 * 通过SQL进行分页查询Map对象
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页显示的记录数
	 * @param countSQL 统计总记录的SQL语句
	 * @param querySQL 查询的SQL语句
	 * @param conditionArgs SQL语句的条件(sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)),此时要求<b>countSQL和querySQL的参数一致<b>
	 * @return 如果记录数为0,则page.page为null
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public PageBean<Map<String, Object>> pageOfMapBySQL(int currPage, int pageSize, String countSQL, String querySQL, Map<String, Object> conditionArgs) {
		PageBean<Map<String, Object>> page = new PageBean<Map<String, Object>>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = findSingleIntBySQL(countSQL, 0, conditionArgs);
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
 		
 		List<Map<String, Object>> listOfMaps=null;
		try {
			listOfMaps = _findListMapBySQLLimit(querySQL,conditionArgs,(currPage - 1) * pageSize,pageSize);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过SQL进行分页查询Map【SQL:%s】",querySQL);
		}
		
 		page.page = listOfMaps;
 		
		return page;
	}
	
	/**
	 * 通过HQL返回一个Map对象(如果有多个返回值，将返回其中的一个)
	 *
	 * @param hql HQL语句，要求使用new Map()语句,例如:<br>select new Map(_key as key,_value as value,id as id) from "+SystemOption.class.getName()。同时由于hibernate会对HQL进行翻译，所以要求使用 as关键字定义查询的列名称
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 * @return Map<String,Object> Map对象（其中Map的key为列名）
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public Map<String, Object> findMapByHQL(String hql, Map<String,Object> args) {
		List<Map<String, Object>> list = findListMapByHQL(hql, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 通过HQL返回一列Map对象
	 *
	 * @param hql HQL语句，要求使用new Map()语句,例如:<br>select new Map(_key as key,_value as value,id as id) from "+SystemOption.class.getName()
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return List<Map<String,Object>> Map对象列表（其中Map的key为列名，由于hibernate会对HQL进行翻译，所以要求使用 as关键字定义查询的列名称）
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public List<Map<String, Object>> findListMapByHQL(String hql, Map<String,Object> args) {
		try {
			return super._findListMapByHQL(hql,args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过HQL返回一列Map对象。【T:%s;HQL:%s;args:%s】", entityClass, hql, args);
			return null;
		}
	}
	// 查找元素：Map<String, Object>, List<Map<String, Object>>, PageBean<Map<String, Object>>    end 
	
	
	// 查找元素：Bean, List<Bean>, PageBean<Bean>    begin 
	/**
	 * 通过本地SQL查找指定的自定义展示视图对象（Bean），如果有多个只会返回其中某一个
	 *
	 * @param <B> 这里的bean要求是Entiry(且带有ID)，并且返回的字段必须和自定义驱动一致，如果有冗余字段，使用(@Transient)注解
	 * @param sql 本地SQL
	 * @param clazz 展示Bean的class对象
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 符合要求的展示对象bean
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public <B> B findBeanBySQL(String sql, Class<B> clazz, Map<String,Object> args) {
		List<B> list = findListBeanBySQL(sql, clazz, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 通过本地SQL查找所有符合的Bean(不分页查询)
	 *
	 * @param <B> 这里的bean要求是Entiry(且带有ID)，并且返回的字段必须和自定义驱动一致，如果有冗余字段，使用(@Transient)注解
	 * @param sql 本地SQL
	 * @param clazz bean
	 * @param args sql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 所有符合要求的自定义驱动对象的列表
	 *                 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public <B> List<B> findListBeanBySQL(String sql, Class<B> clazz, Map<String,Object> args) {
		try {
			return super._findListBeanBySQL(sql, clazz, args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过本地SQL查找所有符合的Bean.【B:%s;SQL:%s;args:%s】", clazz, sql, args);
			return null;
		}
	}
	

	/**
	 * 通过SQL进行分页查询Driver
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页显示的记录数
	 * @param countSQL 统计总记录的SQL语句
	 * @param querySQL 查询的SQL语句
	 * @param conditionArgs SQL语句的条件(sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)),此时要求<b>countSQL和querySQL的参数一致<b>
	 * @return 如果记录数为0,则page.page为null
	 * 
	 *
	 * @author DaiZhengmiao
	 * @param <B>
	 * @createDate 2015年12月16日
	 */
	public <B> PageBean<B> pageOfBeanBySQL( int currPage, int pageSize, String countSQL, String querySQL, Class<B> clazz, Map<String, Object> conditionArgs) {
		PageBean<B> page = new PageBean<B>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = findSingleIntBySQL(countSQL,0, conditionArgs);
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
 		
 		List<B> lists = null;
		try {
			lists = _findListBeanBySQLLimit(querySQL, clazz, conditionArgs, (currPage - 1) * pageSize, pageSize);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过SQL进行分页查询Bean【【B:%s;countSQL:%s;querySQL:%s;conditionArgs:%s】", clazz, countSQL, querySQL, conditionArgs);
		}
		
 		page.page = lists;
 				
		return page;
	}
	

	/**
	 * 通过HQL查找指定的Bean,如果有多个只会返回其中某一个
	 *
	 * @param <B> 这里Bean只是一个普通的jave对象
	 * @param hql HQL查询语句，要求使用new B()的形式，且clazz要求有对应的构造函数，例如：<br>"select new "+B.class.getName()+"(_key as _key,_value as _value) from "+B.class.getName(),则要求B含有B(String _key,Stirng _vlaue)的构造函数
	 * @param clazz 驱动对象的class对象
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 驱动对象，如果有多个只会返回其中的某一个
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public <B> B findBeanByHQL(String hql, Class<B> clazz, Map<String,Object> args) {
		List<B> list = findListBeanByHQL(hql, clazz, args);
		if(list == null || list.size() <= 0){
			return null;
		}
		return list.get(0);
	}
	

	/**
	 * 通过HQL进行分页查询自定义bean(统计语句仍旧是sql，只有查询语句是hql)
	 *
	 * @description 在调用本方法返回bean时，注意在service接口处备注清楚返回bean中的哪些字段以免给调用该接口的程序员造成困扰
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页显示的记录数
	 * @param countSQL 统计总记录的SQL语句
	 * @param queryHQL HQL查询语句，要求使用new B()的形式，且clazz要求有对应的构造函数，例如：<br>"select new "+B.class.getName()+"(_key as _key,_value as _value) from "+B.class.getName(),则要求B含有B(String _key,Stirng _vlaue)的构造函数
	 * @param clazz 自定义bean的class
	 * @param conditionArgs SQL语句的条件(sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)),此时要求<b>countSQL和querySQL的参数一致<b>
	 * @return 如果记录数为0,则page.page为null
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月31日11:49:41
	 */
	public <B> PageBean<B> pageOfBeanByHQL(int currPage, int pageSize, String countSQL, String queryHQL, Class<B> clazz, Map<String, Object> conditionArgs) {
		PageBean<B> page = new PageBean<B>();
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		
		page.currPage = currPage;
		page.pageSize = pageSize;
		
 		int count = findSingleIntBySQL(countSQL,0, conditionArgs);
 		page.totalCount = count;
 		if(count == 0){
 			
 			return page;
 		}
 		
 		List<B> lists = null;
		try {
			lists = _findListBeanByHQLLimit(queryHQL, clazz, conditionArgs, (currPage - 1) * pageSize, pageSize);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过HQL进行分页查询Bean【【B:%s;countSQL:%s;queryHQL:%s;conditionArgs:%s】", clazz, countSQL, queryHQL, conditionArgs);
		}
		
 		page.page = lists;
 				
		return page;
	}

	/**
	 * 通过HQL查找所有符合的Bean
	 *
	 * @param <D> 这里的驱动对象只是一个普通的jave对象,
	 * @param hql HQL查询语句，要求使用new B()的形式，且clazz要求有对应的构造函数，例如：<br>"select new "+B.class.getName()+"(_key as _key,_value as _value) from "+B.class.getName(),则要求B含有B(String _key,Stirng _vlaue)的构造函数
	 * @param clazz 驱动对象的class对象
	 * @param args hql的条件参数,没有请用null。sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)
	 * @return 所有符合要求的自定义驱动对象的列表
	 * 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public <B> List<B> findListBeanByHQL(String hql, Class<B> clazz, Map<String,Object> args) {
		try {
			return super._findListBeanByHQL(hql,clazz,args);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "通过HQL查找所有符合的Bean.【B:%s;HQL:%s;args:%s】", clazz, hql, args);
			return null;
		}
	}
	// 查找元素：Bean, List<Bean>, PageBean<Bean>    begin 
	
	/**
	 * 根据条件计数
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月22日
	 */
	public int countByColumn(String condition, Object... params) {
		try {
			return _countByColumn(condition, params);
		} catch (Exception e) {
			LoggerUtil.error(false, e, "根据条件计数。【T:%s;condition:%s;params:%s】", entityClass, condition, Arrays.toString(params));
			return 0;
		}
	}
	
	/**
	 * 根据SQL计数
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月22日
	 */
	public int countBySQL(String sql, Map<String, Object> args) {
		return findSingleIntBySQL(sql, 0, args);
	}
	
	/**
	 * 计数全部
	 *
	 * @param condition 条件
	 * @param params 参数数组
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月22日
	 */
	public int countALL() {
		try {
			return _countALL();
		} catch (Exception e) {
			LoggerUtil.error(false, e, "计数全部。【T:%s】", entityClass);
			return 0;
		}
	}
}
