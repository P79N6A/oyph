package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_consultant;
import models.common.entity.t_select_photos;
import models.common.entity.t_select_theme;
import common.utils.PageBean;

import daos.base.BaseDao;

/**
 * 优选照片主题
 *
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */
public class SelectThemeDao extends BaseDao<t_select_theme>{

	protected SelectThemeDao() {}
	
	/**
	 * 查询理财顾问 列表查询 分页查询
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public PageBean<t_select_theme> pageOfConsultantBack(int currPage, int pageSize) {
		String sql = "SELECT * FROM t_select_theme ORDER BY order_time DESC";
		String sqlCount = "SELECT COUNT(id) FROM t_select_theme";

		return this.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
	}
	
	/**
	 * 理财顾问 查询前若干条，不分页 
	 *
	 * @return 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public List<t_select_theme> queryConsultantsFront() {
		String sql = "SELECT * FROM t_select_theme ORDER BY order_time DESC";
		
		return this.findListBySQL(sql, null);
	}
	
	
	/**
	 * 阅读优选主题  次数增加
	 *
	 * @param id 主题id 
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月4日
	 */
	public int addReadCount(long id) {
		String sql="UPDATE t_select_theme SET read_count = (read_count + 1) WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}
	
	
	/**
	 * 编辑优选主题 
	 *
	 * @param orderTime 排序时间 
	 * @param name 标题
	 * @param imageUrl 竖图路径
	 * @param imageResolution 竖图分辨率 
	 * @param imageSize 竖图大小
	 * @param imageFormat 竖图格式 
	 * @param codeUrl 横图路径 
	 * @param codeResolution 横图分辨率
	 * @param codeSize 横图大小
	 * @param codeFormat 横图格式
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月5日
	 */
	public int updateConsultant(long id, Date orderTime, String name,String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,String codeSize, String codeFormat) {
		/**
		 UPDATE t_select_theme
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
		String sql = "UPDATE t_select_theme SET order_time=:orderTime,name=:name,image_url=:imageUrl,image_resolution=:imageResolution,image_size=:imageSize,image_format=:imageFormat,code_url=:codeUrl,code_resolution=:codeResolution,code_size=:codeSize,code_format=:codeFormat WHERE id=:id";
		
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
