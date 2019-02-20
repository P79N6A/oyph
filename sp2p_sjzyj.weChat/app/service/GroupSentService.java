package service;

import common.utils.Factory;
import common.utils.PageBean;
import dao.GroupSentDao;
import models.entity.t_wechat_group_sent;
import services.base.BaseService;

public class GroupSentService extends BaseService<t_wechat_group_sent> {
	
	private GroupSentDao groupSentDao;
	
	protected GroupSentService() {
		
		groupSentDao = Factory.getDao(GroupSentDao.class);
		super.basedao = groupSentDao;
	}
	
	/**
	 *  分页查询群发回复列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param title 标题
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月29日
	 */
	public PageBean<t_wechat_group_sent> pageOfGroupList(int currPage, int pageSize, String title) {
		
		return groupSentDao.pageOfGroupList(currPage, pageSize, title);
	}
	
	/**
	 * 群发回复 上下架
	 *
	 * @param id  id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月29日
	 */
	public boolean isUseGroup(long id, boolean isUse) {
		
		int row = groupSentDao.isUseGroup(id, isUse);
		
		if(row<=0) {
			return false;
		}
		return true;
	}

}
