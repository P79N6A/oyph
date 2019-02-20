package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;

import common.enums.IsUse;
import common.utils.PageBean;

import daos.base.BaseDao;


public class AdvertisementDao extends BaseDao<t_advertisement> {

	protected AdvertisementDao() {}
	
	/**
	 *  分页查询，查询广告图片列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param location 图片位置   如果为null表示查询所有
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_advertisement> pageOfAdvertisementBack(int currPage,int pageSize, t_advertisement.Location location) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_advertisement");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_advertisement");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if(location != null ){
			sql.append(" WHERE location=:location");
			sqlCount.append(" WHERE location=:location");
			condition.put("location", location.code);
		}
		sql.append(" ORDER BY order_time DESC");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 * 不分页 ，查询广告图片列表  (过滤下架图片，按排序时间降序查出)
	 *
	 * @param location 位置 
	 * @param limit 查询条数
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月18日
	 */
	public List<t_advertisement> queryAdvertisementFront(Location location,int limit) {
		String sql="SELECT * FROM t_advertisement WHERE location =:location AND is_use=:is_use ORDER BY order_time DESC LIMIT :limit";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("location", location.code);
		condition.put("is_use", IsUse.USE.code);
		condition.put("limit", limit);
		
		return this.findListBySQL(sql, condition);
	}

	/**
	 * 编辑 广告图片
	 *
	 * @param id 图片id
	 * @param name 名称
	 * @param orderTime 排序时间
	 * @param location 所在位置 
	 * @param imageUrl 图片路径
	 * @param imageResolution 图片分辨率
	 * @param imageSize 图片大小 
	 * @param imageFormat 图片格式
	 * @param url 广告链接
	 * @param target 链接打开方式 1-_self 2-_blank 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int updateAdvertisement(long id,String name, Date orderTime, t_advertisement.Location location,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String url, int target) {
		/**
		 UPDATE t_advertisement
		 SET name = :name,
			 order_time =: orderTime,
			 location =: location,
			 image_url =: imageUrl,
			 image_resolution =: imageResolution,
			 image_size =: imageSize,
			 image_format =: imageFormat,
			 url =: url,
			 target =: target
		WHERE
			id =: id
		 */
		
		String sql="UPDATE t_advertisement SET name=:name, order_time=:orderTime, location=:location,image_url=:imageUrl,image_resolution=:imageResolution,image_size=:imageSize,image_format=:imageFormat,url=:url,target=:target WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("name", name);
		condition.put("orderTime", orderTime);
		condition.put("location", location.code);
		condition.put("imageUrl", imageUrl);
		condition.put("imageResolution", imageResolution);
		condition.put("imageSize", imageSize);
		condition.put("imageFormat", imageFormat);
		condition.put("url", url);
		condition.put("target", target);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 广告图片 上下架
	 *
	 * @param id 广告图片id
	 * @param isUse 上下架  true：上架     false：下架
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int updateAdvertisementIsUse(long id, boolean isUse) {
		String sql="UPDATE t_advertisement SET is_use=:isUse WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", isUse);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}

}
