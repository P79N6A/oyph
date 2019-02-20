package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.entity.t_wechat_image_message;

/**
 * 微信图片回复Dao接口
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月21日
 */
public class ImageMessageDao extends BaseDao<t_wechat_image_message> {

	protected ImageMessageDao() {}
	
	/**
	 *  分页查询图片回复列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param keywords 关键词
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public PageBean<t_wechat_image_message> pageOfImageList(int currPage, int pageSize, String keywords) {
		
		StringBuffer sql = new StringBuffer("SELECT * FROM t_wechat_image_message");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_wechat_image_message");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(keywords != null) {
			sql.append(" where keywords like :keywords");
			sqlCount.append(" where keywords like :keywords");
			condition.put("keywords", "%"+keywords+"%");
		}
		
		sql.append(" ORDER BY id");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 * 微信图片回复 上下架
	 *
	 * @param id  id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public int isUseImage(long id, boolean isUse) {
		String sql = "UPDATE t_wechat_image_message SET is_use=:isUse WHERE id=:id";		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", isUse);
		condition.put("id", id);
	
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 微信图片回复
	 *
	 * @param matchs 匹配类型
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年6月04日
	 */
	public List<t_wechat_image_message> queryImageByUse(int matchs) {
		String sql = "SELECT * FROM t_wechat_image_message where is_use=1 and matchs=:matchs";		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("matchs", matchs);
		
		return this.findListBySQL(sql, condition);
	}
}
