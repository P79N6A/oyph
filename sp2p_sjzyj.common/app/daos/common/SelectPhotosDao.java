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
public class SelectPhotosDao extends BaseDao<t_select_photos>{

	protected SelectPhotosDao() {}
	
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
	public PageBean<t_select_photos> pageOfConsultantBack(int currPage, int pageSize) {
		String sql = "SELECT * FROM t_select_photos ORDER BY order_time DESC";
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
	public List<t_select_photos> queryConsultantsFront() {
		String sql = "SELECT * FROM t_select_photos ORDER BY order_time DESC";
		
		return this.findListBySQL(sql, null);
	}
	


}
