package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_product;

import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 借款产品DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class ProductDao extends BaseDao<t_product> {

	protected ProductDao() {}

	/**
	 * 更新产品状态
	 *
	 * @param id 产品id
	 * @param isUse 上下架
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public int updateProductStatus(long id, boolean isUse) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("is_use", isUse);
		map.put("id", id);
		
		return this.updateBySQL("UPDATE t_product SET is_use=:is_use WHERE id=:id", map);
	}

	/**
	 * 分页查询借款产品列
	 *
	 * @param pageSize 页大小
	 * @param currPage 当前页
	 * @param status 借款产品状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月17日
	 */
	public PageBean<t_product> pageOfProduct(int pageSize, int currPage, Boolean status) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(id) FROM t_product ");
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_product ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		if(status != null){
			countSQL.append(" WHERE is_use=:status");
			querySQL.append(" WHERE is_use=:status");
			conditionArgs.put("status", status);
		}
		querySQL.append(" ORDER BY order_time DESC");
				
		return super.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), conditionArgs);
	}

	/**
	 * 根据产品名称获取产品
	 *
	 * @param name 借款产品名称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public List<t_product> queryProductByName(String name) {
		
		String sql = "SELECT * FROM t_product WHERE name=:name";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("name", name);
		
		return findListBySQL(sql, params);
	}

	/**
	 * 前台-借款-返回上架状态产品列表，只取前10个
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public List<Map<String, Object>> queryProductIsUse() {
		
		String sql = "SELECT id, name FROM t_product WHERE is_use=:is_use ORDER BY order_time DESC LIMIT 10";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("is_use", true);
		
		return super.findListMapBySQL(sql, params);
	}
	
	/* liuyang beign 2017-1-9--------------------------- */
	/**
	 * 根据所有产品类型
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年1月9日
	 */
	public List<t_product> queryProducts() {	
		return findAll();
	}
	/* liuyang end 2017-1-9----------------------------- */
}
