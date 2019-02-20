package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_audit_subject;

import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 审核科目表的DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class AuditSubjectDao extends BaseDao<t_audit_subject> {

	protected AuditSubjectDao() {}

	/**
	 * 通过一组id查询获取审核科目对象列
	 *
	 * @param ids 审核科目的id集
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public List<t_audit_subject> findAuditSubject(List<Integer> ids) {
		
		String querySQL = "SELECT * FROM t_audit_subject WHERE id IN(:ids)";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		
		return super.findListBySQL(querySQL, map);
		
	}
	
	/**
	 * 后台-风控-审核科目的分页查询
	 *
	 * @param currPage 当前页
	 * @param pageSize 页大小
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public PageBean<t_audit_subject> pageOfAuditSubject(int currPage, int pageSize){
		
		String countSQL = "SELECT COUNT(id) FROM t_audit_subject ";
		String querySQL = "SELECT * FROM t_audit_subject ";
			
		return super.pageOfBySQL(currPage, pageSize, countSQL, querySQL, null);
	}
	
}
