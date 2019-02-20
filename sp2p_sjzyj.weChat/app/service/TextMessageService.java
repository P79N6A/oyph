package service;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import dao.TextMessageDao;
import models.entity.t_wechat_text_message;
import services.base.BaseService;

public class TextMessageService extends BaseService<t_wechat_text_message> {
	
	private TextMessageDao textMessageDao;
	
	protected TextMessageService() {
		textMessageDao = Factory.getDao(TextMessageDao.class);
		super.basedao = textMessageDao;
	}
	
	/**
	 *  分页查询文本回复列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param keywords 关键词
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月22日
	 */
	public PageBean<t_wechat_text_message> pageOfTextList(int currPage, int pageSize, String keywords) {
		
		return textMessageDao.pageOfTextList(currPage, pageSize, keywords);
	}
	
	/**
	 * 微信文本回复 上下架
	 *
	 * @param id  id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月22日
	 */
	public boolean isUseText(long id, boolean isUse) {
		
		int row = textMessageDao.isUseText(id, isUse);
		if(row<=0){

			return false;
		}
		
		return true;
	}
	
	/**
	 * 微信文本回复 
	 *
	 * @param keywords 关键词
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年6月04日
	 */
	public String queryContent(String keywords) {
		
		String content = null;
		
		List<t_wechat_text_message> texts = textMessageDao.queryTextByUse(1);
		if(texts != null && texts.size()>0) {
			for (int i = 0; i < texts.size(); i++) {
				if(keywords.equals(texts.get(i).keywords)) {
					content = texts.get(i).content;
					break;
				}
			}
		}
		
		if(content != null) {
			return content;
		}
		
		List<t_wechat_text_message> textss = textMessageDao.queryTextByUse(0);
		if(textss != null && textss.size()>0) {
			for (int i = 0; i < textss.size(); i++) {
				if((textss.get(i).keywords).contains(keywords)) {
					content = textss.get(i).content;
					break;
				}
			}
		}
		
		return content;
		
	}

}
