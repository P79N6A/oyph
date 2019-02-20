package services.common;

import java.util.Date;
import java.util.List;

import models.common.entity.t_consultant;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.ConsultantDao;

/**
 * 理财顾问
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
public class ConsultantService extends BaseService<t_consultant> {

	protected static ConsultantDao consultantDao = Factory.getDao(ConsultantDao.class);
	
	protected ConsultantService(){
		consultantDao = Factory.getDao(ConsultantDao.class);
		super.basedao = consultantDao;
	}
	
	/**
	 * 添加理财顾问 
	 *
	 * @param consultant 理财顾问
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addConsultant(t_consultant consultant) {
		consultant.time = new Date();
		
		return consultantDao.save(consultant);
	}

	/**
	 * 删除 理财顾问  （根据id删除）
	 *
	 * @param id 理财顾问id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean delConsultant(long id) {
		int row = consultantDao.delete(id);
		if(row<=0){
			return false;
		}
		return true;
	}
	
	/**
	 * 编辑理财顾问 
	 *
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
	public boolean updateConsultant(long id,Date orderTime, String name,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,
			String codeSize, String codeFormat) {
		int row = consultantDao.updateConsultant(id, orderTime, name, imageUrl,
				imageResolution, imageSize, imageFormat, codeUrl,
				codeResolution, codeSize, codeFormat);
		
		if(row <= 0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 理财顾问 查询(列表)
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_consultant> pageOfConsultantBack(int currPage,int pageSize) {
		
		return consultantDao.pageOfConsultantBack(currPage,pageSize);
	}
	
	/**
	 * 理财顾问 查询 不分页 
	 *
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public List<t_consultant> queryConsultantsFront() {
		
		return consultantDao.queryConsultantsFront();
	}
	
}
