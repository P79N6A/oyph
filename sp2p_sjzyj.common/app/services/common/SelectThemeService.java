package services.common;

import java.util.Date;
import java.util.List;

import models.common.entity.t_select_theme;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.ConsultantDao;
import daos.common.SelectThemeDao;


/**
 * 优选照片
 *
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */
public class SelectThemeService extends BaseService<t_select_theme>{

	protected static SelectThemeDao selectThemeDao = Factory.getDao(SelectThemeDao.class);
	
	protected SelectThemeService(){
		selectThemeDao = Factory.getDao(SelectThemeDao.class);
		super.basedao = selectThemeDao;
	}
	
	/**
	 * 添加优选主题图片
	 *
	 * @param selectPhotos 优选图片
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public boolean addSelectPhotos(t_select_theme selectPhotos) {
		selectPhotos.time = new Date();
		
		return selectThemeDao.save(selectPhotos);
	}

	
	/**
	 * 优选图片 查询(列表)
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public PageBean<t_select_theme> pageOfConsultantBack(int currPage,int pageSize) {
		
		return selectThemeDao.pageOfConsultantBack(currPage,pageSize);
	}
	
	/**
	 * 优选图片查询 不分页 
	 *
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public List<t_select_theme> queryConsultantsFront() {
		
		return selectThemeDao.queryConsultantsFront();
	}
	
	
	/**
	 * 优选主题  阅读次数次数增加
	 *
	 * @param id 资讯id
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2015年12月28日
	 */
	public boolean addReadCount(long id) {
		int row = selectThemeDao.addReadCount(id);
		if(row<=0){
			return false;
		}
		
		return true;
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
	public boolean updateConsultant(long id,Date orderTime, String name,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,
			String codeSize, String codeFormat) {
		int row = selectThemeDao.updateConsultant(id, orderTime, name, imageUrl,
				imageResolution, imageSize, imageFormat, codeUrl,
				codeResolution, codeSize, codeFormat);
		
		if(row <= 0){
			return false;
		}
		
		return true;
	}
	
	
}
