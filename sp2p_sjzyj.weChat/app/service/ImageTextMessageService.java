package service;

import common.utils.Factory;
import common.utils.PageBean;
import dao.ImageTextMessageDao;
import models.entity.t_wechat_image_text_message;
import services.base.BaseService;

public class ImageTextMessageService extends BaseService<t_wechat_image_text_message> {
	
	private ImageTextMessageDao imageTextMessageDao;
	
	protected ImageTextMessageService() {
		imageTextMessageDao = Factory.getDao(ImageTextMessageDao.class);
		super.basedao = imageTextMessageDao;
	}
	
	/**
	 *  分页查询图文回复列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param keywords 关键词
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月24日
	 */
	public PageBean<t_wechat_image_text_message> pageOfTextImageList(int currPage, int pageSize, String keywords) {
		
		return imageTextMessageDao.pageOfTextImageList(currPage, pageSize, keywords);
	}
	
	/**
	 * 微信图文回复 上下架
	 *
	 * @param id  id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月24日
	 */
	public boolean isUseImage(long id, boolean isUse) {
		
		int row = imageTextMessageDao.isUseImage(id, isUse);
		
		if(row<=0){

			return false;
		}
		
		return true;
	}

}
