package daos.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import com.shove.Convert;

import play.db.jpa.JPA;
import play.db.jpa.Model;
import common.utils.GenericsUtils;


/**
 * 数据库操作入口，提供了基本的对数据的操作(对JPA的包装)
 *
 * @param <T> 泛型，play.db.jpa.Model的子类(数据库实体,即带@Entity注解)
 * @description  在本类中默认的:泛型“T”表示实体,泛型“B”表示Bean(和实体类似，但是没有对应的table和view)<br>
 *               同时本类中提供了很多包访问权限(default)的方法，是因为不希望在dao中直接使用这些方法，而使用BaseDao中提供的已经封装好的方法。
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月3日
 */
public class GenericDao<T extends Model> {

	/** DAO所管理的Entity类型 */
	protected Class<T> entityClass;
	
	public GenericDao() {
		entityClass = GenericsUtils.getSuperClassGenricType(getClass());
	}
	
	/**
	 * 返回当前的EntityManager对象
	 *
	 * @return
	 * @description 不希望直接调用EntityManager,而使用本类提供的其他已经封装好的方法
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	EntityManager getEm() {
		
		return JPA.em();
	}
	
	void _save(T t) throws Exception {
		Model model = (Model)t;
		model.save();
	}
	
	T _saveT(T t) throws Exception {
		Model model = (Model)t;
		return model.save();
	}
	
	int _delete(Long id) throws Exception{
		Query query = getEm().createQuery("DELETE FROM "+entityClass.getName()+" WHERE id=:id");
		query.setParameter("id", id);
		int row = query.executeUpdate();
		if (row > 0) {
			getEm().clear();
		}
		return row;
	}

	int _executSQL(String sql, Map<String,Object> args) throws Exception{
		Query query = getEm().createNativeQuery(sql);
		setParameter(query, args);
		int row = query.executeUpdate();
		if (row > 0) {
			getEm().clear();
		}
		return row;
	}
	
	T _findByID(Long id) throws Exception {
		if (id == null) {
			return null;
		}
		T t = getEm().find(entityClass, id);
		return t;
	}
	
	List<T> _findListByColumn(String condition, Object... params) {
		StringBuffer buffer = new StringBuffer(" SELECT t FROM ").append(entityClass.getName()).append(" t ");
		if (StringUtils.isNotBlank(condition)) {
			buffer.append(" WHERE ");
			buffer.append(condition);
		}
		Query query = getEm().createQuery(buffer.toString(), entityClass);
		setParameter(query, params);
		return query.getResultList();
	}
	
	List<T> _findListByColumnLimit(int firstResult,int maxResult, String condition, Object... params) throws Exception {
		StringBuffer buffer = new StringBuffer("SELECT t FROM ").append(entityClass.getName()).append(" t ");
		if (StringUtils.isNotBlank(condition)) {
			buffer.append(" WHERE ");
			buffer.append(condition);
		}
		Query query = getEm().createQuery(buffer.toString(), entityClass);
		setParameter(query, params);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}

	List<T> _findListBySQL(String sql, Map<String,Object> args) throws Exception {
		Query query = getEm().createNativeQuery(sql, entityClass);
		setParameter(query, args);
		return query.getResultList();
	}
	
	List<T> _findListBySQLLimit(String sql, Map<String,Object> args, int firstResult,int maxResult) throws Exception {
		Query query = getEm().createNativeQuery(sql, entityClass);
		setParameter(query, args);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}
	
	List<T> _findListByHQL(String sql, Map<String,Object> args) throws Exception {
		Query query = getEm().createQuery(sql, entityClass);
		setParameter(query, args);
		return query.getResultList();
	}
	
	List<T> _findAll() throws Exception{
		StringBuffer buffer = new StringBuffer("SELECT t FROM ").append(entityClass.getName()).append(" t ");
		Query query = getEm().createQuery(buffer.toString(), entityClass);
		return query.getResultList();
	}
	
	List<T> _findAllLimit(int currPage, int maxResult) throws Exception{
		StringBuffer buffer = new StringBuffer("SELECT t FROM ").append(entityClass.getName()).append(" t ");
		Query query = getEm().createQuery(buffer.toString(), entityClass);
		query.setFirstResult(currPage);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}
	
	List<Object> _findListSingleBySQL(String sql, Map<String, Object> args) throws Exception {
		Query query = null;
		query = getEm().createNativeQuery(sql);
		setParameter(query, args);
		return query.getResultList();
	}
	
	List<Object> _findListSingleByHQL(String hql, Map<String, Object> args) throws Exception {
		Query query = null;
		query = getEm().createQuery(hql);
		setParameter(query, args);
		return query.getResultList();
	}

	List<Map<String, Object>> _findListMapBySQL(String sql, Map<String,Object> args) throws Exception {
		Query query = getEm().createNativeQuery(sql);
		setParameter(query, args);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}
	
	List<Map<String, Object>> _findListMapBySQLLimit(String sql, Map<String,Object> args, int firstResult,int maxResult) throws Exception {
		Query query = getEm().createNativeQuery(sql);
		setParameter(query, args);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> list = query.getResultList();
		return list;
	}

	List<Map<String, Object>> _findListMapByHQL(String hql, Map<String,Object> args) throws Exception {
		Query query = getEm().createQuery(hql,Map.class);
		setParameter(query, args);
		return query.getResultList();
	}
	
	<B> List<B> _findListBeanBySQL(String sql,Class<B> clazz,Map<String,Object> args) throws Exception {
		Query query = getEm().createNativeQuery(sql,clazz);
		setParameter(query, args);
		return query.getResultList();
	}
	
	<B> List<B> _findListBeanBySQLLimit(String sql, Class<B> clazz, Map<String,Object> args, int firstResult, int maxResult) throws Exception {
		Query query = getEm().createNativeQuery(sql, clazz);
		setParameter(query, args);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}
	
	<B> List<B> _findListBeanByHQL(String hql,Class<B> clazz,Map<String,Object> args) throws Exception{
		Query query = getEm().createQuery(hql,clazz);
		setParameter(query, args);
		return query.getResultList();
	}
	
	<B> List<B> _findListBeanByHQLLimit(String hql, Class<B> clazz, Map<String,Object> args, int firstResult, int maxResult) throws Exception {
		Query query = getEm().createQuery(hql, clazz);
		setParameter(query, args);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}
	
	int _countByColumn(String condition, Object... params) throws Exception {
		StringBuffer buffer = new StringBuffer("SELECT COUNT(t) FROM ").append(entityClass.getName()).append(" t ");
		if (StringUtils.isNotBlank(condition)) {
			buffer.append(" WHERE ");
			buffer.append(condition);
		}
		Query query = getEm().createQuery(buffer.toString());
		setParameter(query, params);
		List<Object> list = query.getResultList();
		if (list == null || list.size() <= 0 || list.get(0) == null) {
			return 0;
		}
		return Convert.strToInt(list.get(0).toString(), 0);
	}
	
	int _countALL() throws Exception {
		StringBuffer buffer = new StringBuffer("SELECT COUNT(t) FROM ").append(entityClass.getName()).append(" t ");
		Query query = getEm().createQuery(buffer.toString());
		List<Object> list = query.getResultList();
		if (list == null || list.size() <= 0 || list.get(0) == null) {
			return 0;
		}
		return Convert.strToInt(list.get(0).toString(), 0);
	}

	/**
	 * 给查询设置参数
	 *
	 * @param query 
	 * @param args sql条件为id=:id的形式,禁止使用id=?,传入的参数为map.put("id",xx)。
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	protected void setParameter(Query query,Map<String,Object> args) {
		if ( (args != null) && (args.keySet() != null) && (args.keySet().size() > 0) ) {
			Set<Map.Entry<String, Object>> entries = ((Map) args).entrySet();
			for (Map.Entry<String, Object> entry : entries) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * 给查询设置参数
	 *
	 * @param query 
	 * @param params sql条件为id=?的形式,禁止使用id=:id
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	protected void setParameter(Query query, Object... params) {
		if (params == null || params.length <= 0) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i+1, params[i]);
		}
	}
}
