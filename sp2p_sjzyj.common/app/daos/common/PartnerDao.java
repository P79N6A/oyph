package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_partner;

import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 合作伙伴 dao
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
public class PartnerDao extends BaseDao<t_partner> {

	protected PartnerDao() {}

	/**
	 * 合作伙伴查询 列表查询
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public PageBean<t_partner> pageOfPartnerBack(int currPage, int pageSize) {
		String sql = "SELECT * FROM t_partner ORDER BY order_time DESC";
		String sqlCount = "SELECT COUNT(id) FROM t_partner";

		return this.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
	}

	/**
	 * 获得合作伙伴前若干条  根据id逆序获取
	 *
	 * @param limit 查询条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public List<t_partner> queryPartnersFront(int limit) {
		String sql = "SELECT * FROM t_partner ORDER BY order_time DESC LIMIT :limit";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("limit", limit);
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 合作伙伴 信息更新
	 *
	 * @param id  
	 * @param name 名称
	 * @param orderTime 排序时间  
	 * @param imageUrl 图片路径
	 * @param imageResolution 图片分辨率  
	 * @param imageSize 图片大小
	 * @param imageFormat 图片格式  
	 * @param url 合作伙伴链接
	 * @param target 打开方式 _self _blank  
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int updatePartner(long id, String name, Date orderTime,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String url, int target) {
		String sql="UPDATE t_partner SET name=:name,order_time=:orderTime,image_url=:imageUrl,image_resolution=:imageResolution,"
				+ "image_size=:imageSize,image_format=:imageFormat,url=:url,target=:target WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("orderTime", orderTime);
		condition.put("imageUrl", imageUrl);
		condition.put("imageResolution", imageResolution);
		condition.put("imageSize", imageSize);
		condition.put("imageFormat", imageFormat);
		condition.put("url", url);
		condition.put("target", target);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}


}
