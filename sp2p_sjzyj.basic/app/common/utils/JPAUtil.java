package common.utils;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import play.Logger;
import play.db.helper.JpaHelper;
import play.db.jpa.JPA;


public class JPAUtil {
	/**
	 * 开启事务
	 * @return
	 */
	public static void transactionBegin(){
		boolean flag = JPA.em().getTransaction().isActive();
		if(!flag){
			JPA.em().getTransaction().begin();
		}		
	}
	
	/**
	 * 事务提交
	 */
	public static void transactionCommit(){
		boolean isMarkedRollBack = JPA.em().getTransaction().getRollbackOnly();
		
		if (!isMarkedRollBack) {
			JPA.em().getTransaction().commit();
		}
		
		boolean isActive = JPA.em().getTransaction().isActive();
		
		if(!isActive){
			JPA.em().getTransaction().begin();
		}
	}
	
	public static List<Map<String, Object>> getList(ResultInfo error, String sql, Object... params) {
		Query query = createNativeQuery(sql, params);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			error.code = -1;
			error.msg = "数据库异常";
			
			return null;
		}
	}
	
	public static Query createNativeQuery(String sql, Object... params) {
		Query query = JPA.em().createNativeQuery(sql);
		int index = 0;
		
		for (Object param : params) {
			query.setParameter(++index, param);
		}
		
		return query;
	}
	
	/**
	 * 执行增、删、改语句
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int executeUpdate(ResultInfo error, String sql, Object... params) {
		Query query = JpaHelper.execute(sql, params);
		int rows = 0;
		
		try {
			rows = query.executeUpdate();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			Logger.info(e.getMessage());
			error.code = -1;
			error.msg = "数据库异常";
			
			return 0;
		}
		
		if (rows < 1) {
			error.code = -11;
			error.msg = "数据未更新";
		}
		
		return rows;
	}
	
}
