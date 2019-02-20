package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_consultant;

import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 理财顾问
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
public class ConsultantDao extends BaseDao<t_consultant> {

	protected ConsultantDao() {}
	
	/**
	 * 查询理财顾问 列表查询 分页查询
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public PageBean<t_consultant> pageOfConsultantBack(int currPage, int pageSize) {
		String sql = "SELECT * FROM t_consultant ORDER BY order_time DESC";
		String sqlCount = "SELECT COUNT(id) FROM t_consultant";

		return this.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
	}
	
	/**
	 * 理财顾问 查询前若干条，不分页 
	 *
	 * @return 
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public List<t_consultant> queryConsultantsFront() {
		String sql = "SELECT * FROM t_consultant ORDER BY order_time DESC";
		
		return this.findListBySQL(sql, null);
	}
	
	/**
	 * 编辑 理财顾问 
	 *
	 * @param id 理财顾问id
	 * @param orderTime 排序时间 
	 * @param name 姓名
	 * @param imageUrl 头像路径
	 * @param imageResolution 头像分辨率 
	 * @param imageSize 头像大小
	 * @param imageFormat 头像格式 
	 * @param codeUrl 二维码路径 
	 * @param codeResolution 二维码分辨率
	 * @param codeSize 二维码大小
	 * @param codeFormat 二维码格式
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int updateConsultant(long id, Date orderTime, String name,String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,String codeSize, String codeFormat) {
		/**
		 UPDATE t_consultant
		 SET order_time =: orderTime,
			 name =:name,
			 image_url =: imageUrl,
			 image_resolution =: imageResolution,
			 image_size =: imageSize,
			 image_format =: imageFormat,
			 code_url =: codeUrl,
			 code_resolution =: codeResolution,
			 code_size =: codeSize,
			 code_format =: codeFormat
		WHERE
			 id =: id
		 */
		String sql = "UPDATE t_consultant SET order_time=:orderTime,name=:name,image_url=:imageUrl,image_resolution=:imageResolution,image_size=:imageSize,image_format=:imageFormat,code_url=:codeUrl,code_resolution=:codeResolution,code_size=:codeSize,code_format=:codeFormat WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("orderTime", orderTime);
		condition.put("name", name);
		condition.put("imageUrl", imageUrl);
		condition.put("imageResolution", imageResolution);
		condition.put("imageSize", imageSize);
		condition.put("imageFormat", imageFormat);
		condition.put("codeUrl", codeUrl);
		condition.put("codeResolution", codeResolution);
		condition.put("codeSize", codeSize);
		condition.put("codeFormat", codeFormat);
		condition.put("id", id);
		
		return  super.updateBySQL(sql, condition);
	}

}
