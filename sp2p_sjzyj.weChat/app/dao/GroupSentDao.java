package dao;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.entity.t_wechat_group_sent;
import models.entity.t_wechat_image_message;

public class GroupSentDao extends BaseDao<t_wechat_group_sent> {

	protected GroupSentDao() {}
	
	/**
	 *  分页查询群发回复列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param title 标题
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月29日
	 */
	public PageBean<t_wechat_group_sent> pageOfGroupList(int currPage, int pageSize, String title) {
		
		StringBuffer sql = new StringBuffer("SELECT * FROM t_wechat_group_sent");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_wechat_group_sent");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(title != null) {
			sql.append(" where title like :title");
			sqlCount.append(" where title like :title");
			condition.put("title", "%"+title+"%");
		}
		
		sql.append(" ORDER BY id");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 * 群发回复 上下架
	 *
	 * @param id  id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月29日
	 */
	public int isUseGroup(long id, boolean isUse) {
		String sql = "UPDATE t_wechat_group_sent SET is_use=:isUse WHERE id=:id";		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", isUse);
		condition.put("id", id);
	
		return this.updateBySQL(sql, condition);
	}
	
}
