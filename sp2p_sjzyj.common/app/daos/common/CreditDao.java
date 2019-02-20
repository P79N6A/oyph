package daos.common;

import java.util.List;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_credit;

/**
 * 信用等级Dao
 * 
 * @author liuyang
 * @createDate 2018.01.23
 */
public class CreditDao extends BaseDao<t_credit> {

	protected CreditDao() {}
	/**
	 * 
	 * @Title: findPageOfCredit
	 * 
	 * @description 分页查询信用等级
	 * @param @param currPage
	 * @param @param pageSize
	 * @return PageBean<t_credit>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:21:35
	 */
	public PageBean<t_credit> findPageOfCredit(int currPage,int pageSize){
		
		String sql = "SELECT * FROM t_credit";
		String countSql = "SELECT COUNT(id) FROM t_credit";
		
		return pageOfBySQL(currPage, pageSize, countSql, sql, null);
	}
}
