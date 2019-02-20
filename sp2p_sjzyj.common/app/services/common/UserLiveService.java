package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.UserLiveDao;
import models.common.entity.t_user_live;
import services.base.BaseService;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class UserLiveService extends BaseService<t_user_live> {

	protected UserLiveDao userLiveDao = Factory.getDao(UserLiveDao.class);
	
	protected UserLiveService() {
		this.basedao = userLiveDao;
	}
	
	/**
	 * 查询居住信息
	 * @return
	 */
	public List<t_user_live> queryByAccount() {
		
		return userLiveDao.queryByAccount();
	}
	/**
	 * 
	 * @Title: insertUserLive
	 * 
	 * @description 保存居住信息返回成功返回true
	 * @param @param userLive
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-下午4:39:45
	 */
	public boolean insertUserLive(t_user_live userLive) {
		
		return userLiveDao.save(userLive);
	}
}
