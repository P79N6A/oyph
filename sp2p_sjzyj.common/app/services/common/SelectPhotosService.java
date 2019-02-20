package services.common;

import java.util.Date;
import java.util.List;

import models.common.entity.t_select_photos;
import models.common.entity.t_select_theme;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.ConsultantDao;
import daos.common.SelectPhotosDao;
import daos.common.SelectThemeDao;


/**
 * 优选照片
 *
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */
public class SelectPhotosService extends BaseService<t_select_photos>{

	protected static SelectPhotosDao selectPhotosDao = Factory.getDao(SelectPhotosDao.class);
	
	protected SelectPhotosService(){
		selectPhotosDao = Factory.getDao(SelectPhotosDao.class);
		super.basedao = selectPhotosDao;
	}
	
	/**
	 * 添加优选图片
	 *
	 * @param selectPhotos 优选图片
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public boolean addSelectPhotoss(t_select_photos selectPhotos) {
		selectPhotos.time = new Date();
		
		return selectPhotosDao.save(selectPhotos);
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
	public PageBean<t_select_photos> pageOfConsultantBack(int currPage,int pageSize) {
		
		return selectPhotosDao.pageOfConsultantBack(currPage,pageSize);
	}
	
	/**
	 * 优选图片查询 不分页 
	 *
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public List<t_select_photos> queryConsultantsFront() {
		
		return selectPhotosDao.queryConsultantsFront();
	}
	
	
	/**
	 * 删除 优选图片  （根据id删除）
	 *
	 * @param id 图片id
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月4日
	 */
	public boolean delPhotos(long id) {
		int row = selectPhotosDao.delete(id);
		if(row<=0){
			return false;
		}
		return true;
	}
	
}

