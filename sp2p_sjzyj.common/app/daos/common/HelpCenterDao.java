package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 帮助中心
 *
 * @author liudong
 * @createDate 2016年1月9日
 */
public class HelpCenterDao extends BaseDao<t_help_center> {

	protected HelpCenterDao() {}
	
	/**
	 * 帮助中心 列表查询
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param column 栏目,当为null表示查询所有
	 * @param helpTitle 问题标题
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public PageBean<t_help_center> pageOfHelpCenterBack(int currPage,int pageSize,Column column,String helpTitle) {
		StringBuffer sql=new StringBuffer("SELECT * FROM t_help_center WHERE 1=1 ");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_help_center WHERE 1=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (column != null) {
			sql.append(" AND column_no=:columnNo");
			sqlCount.append(" AND column_no=:columnNo");
			condition.put("columnNo", column.code);
		}
		
		/* 按问题标题搜索  */
		if (StringUtils.isNotBlank(helpTitle)) {
			sql.append(" AND title like :title");
			sqlCount.append(" AND title like :title");
			condition.put("title", "%"+helpTitle+"%");
		}
		
		sql.append(" ORDER BY order_time DESC");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 * 帮助中心 列表查询 (过滤掉未上架的信息)
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param column 栏目,当为null表示查询所有
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public PageBean<t_help_center> pageOfHelpCenterFront(int currPage,int pageSize,Column column) {
		StringBuffer sql=new StringBuffer("SELECT * FROM t_help_center WHERE is_use=TRUE");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_help_center WHERE is_use=TRUE");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (column != null) {
			sql.append(" AND column_no=:columnNo");
			sqlCount.append(" AND column_no=:columnNo");
			condition.put("columnNo", column.code);
		}
		sql.append(" ORDER BY order_time DESC");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}

	/**
	 * 更新 帮助中心内容
	 *
	 * @param id 帮助中心id
	 * @param columnNo 栏目 
	 * @param title 标题
	 * @param content 内容答案
	 * @param orderTime 排序时间
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public int upadteHelpCenter(long id, t_help_center.Column columnNo, String title, String content, Date orderTime) {
		String sql="UPDATE t_help_center SET column_no=:columnNo,title=:title,content=:content,order_time=:orderTime WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("columnNo", columnNo.code);
		condition.put("title", title);
		condition.put("content", content);
		condition.put("orderTime", orderTime);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 上下架
	 * 
	 * @param id 帮助中心id
	 * @param isUse true上架   false下架 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public int upadteHelpCenterIsUse(long id,boolean isUse) {
		String sql="UPDATE t_help_center SET is_use=:isUse WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", isUse);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}

	

}
