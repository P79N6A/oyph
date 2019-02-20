package services.common;

import java.util.Date;
import java.util.List;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import common.enums.IsUse;
import common.enums.Target;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.AdvertisementDao;
import daos.common.NoticeSettingDao;
import daos.common.TemplateNoticeDao;
import services.base.BaseService;


public class AdvertisementService extends BaseService<t_advertisement>{

	protected static AdvertisementDao adsvertisementDao = Factory.getDao(AdvertisementDao.class); 
	
	protected AdvertisementService() {
		super.basedao = this.adsvertisementDao;
	}
	
	/**
	 * 添加广告图片
	 *
	 * @param ads 广告图片
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addAdvertisement(t_advertisement ads) {
		ads.time = new Date();
		ads.setIs_use(IsUse.USE);
	
		return adsvertisementDao.save(ads);
	}

	/**
	 * 删除广告图片
	 *
	 * @param id 广告图片id 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean delAdvertisement(long id) {
		int row = adsvertisementDao.delete(id);
		if(row<=0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 编辑广告图片
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
	 * @param target 链接打开方式 1-_self  2-_blank 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean updateAdvertisement(long id,String name, Date orderTime, t_advertisement.Location location,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String url, int target) {
		int row = adsvertisementDao.updateAdvertisement(id,name, orderTime,location, imageUrl,
				imageResolution, imageSize, imageFormat,url, target);
		if(row<=0){
			return false;
		}
		
		return true;
	}

	/**
	 * 广告图片上下架  
	 *
	 * @param id 图片id  
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean updateAdvertisementIsUse(long id, boolean isUse) {
		int row = adsvertisementDao.updateAdvertisementIsUse(id, isUse);
		if(row<=0){

			return false;
		}
		
		return true;
	}
	
	/**
	 * 获取前台首页平台优势图片路径
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public t_advertisement findFourprotectPic() {
		List<t_advertisement> fourprotectList = adsvertisementDao.queryAdvertisementFront(Location.PLAT_ADVANTAGE_ADS, 1);
		if (fourprotectList == null || fourprotectList.size() == 0) {
			
			return null;
		}
	
		return fourprotectList.get(0);
	}
	
	/**
	 * 获取前台登录底图
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public String findLoginPic() {
		List<t_advertisement> loginPicList = adsvertisementDao.queryAdvertisementFront(Location.LOGIN_BOTTOM_PIC, 1);
		if (loginPicList == null || loginPicList.size() == 0) {
			
			return null;
		}
	
		return loginPicList.get(0).image_url;
	}
	
	/**
	 * 不分页 ，查询广告图片列表  (过滤下架图片，按排序时间降序查出)
	 *
	 * @param location 位置 
	 * @param limit 限制查询条数
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月18日
	 */
	public List<t_advertisement> queryAdvertisementFront(Location location,int limit) {
		
		return adsvertisementDao.queryAdvertisementFront(location, limit);
	}
	
	/**
	 * 分页查询 ， 查询广告图片 列表 
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页的条数
	 * @param location 位置  当location为null时表示查询所有
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_advertisement> pageOfAdvertisementBack(int currPage,int pageSize, t_advertisement.Location location) {

		return adsvertisementDao.pageOfAdvertisementBack(currPage, pageSize, location);
	}

}
