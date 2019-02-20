package service;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import dao.ImageMessageDao;
import models.entity.t_wechat_image_message;
import models.entity.t_wechat_text_message;
import services.base.BaseService;

public class ImageMessageService extends BaseService<t_wechat_image_message> {
	
	private ImageMessageDao imageMessageDao;
	
	protected ImageMessageService() {
		imageMessageDao = Factory.getDao(ImageMessageDao.class);
		super.basedao = imageMessageDao;
		
	}
	
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
		
		return imageMessageDao.pageOfImageList(currPage, pageSize, keywords);
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
	public boolean isUseImage(long id, boolean isUse) {
		
		int row = imageMessageDao.isUseImage(id, isUse);
		if(row<=0){

			return false;
		}
		
		return true;
		
	}
	
	/**
	 * 微信图片回复 
	 *
	 * @param keywords 关键词
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年6月04日
	 */
	public String queryContent(String keywords) {

		String urls = null;
		
		List<t_wechat_image_message> images = imageMessageDao.queryImageByUse(1);
		if(images != null && images.size()>0) {
			for (int i = 0; i < images.size(); i++) {
				if(keywords.equals(images.get(i).keywords)) {
					urls = images.get(i).img_url;
					break;
				}
			}
		}
		
		if(urls != null) {
			return urls;
		}
		
		List<t_wechat_image_message> imagess = imageMessageDao.queryImageByUse(0);
		if(imagess != null && imagess.size()>0) {
			for (int i = 0; i < imagess.size(); i++) {
				if((imagess.get(i).keywords).contains(keywords)) {
					urls = imagess.get(i).img_url;
					break;
				}
			}
		}
		
		return urls;
	}

}
