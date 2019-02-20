package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.UserProfessionDao;
import models.common.entity.t_user;
import models.common.entity.t_user_profession;
import services.base.BaseService;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class UserProfessionService extends BaseService<t_user_profession> {

	protected UserProfessionDao userProfessionDao = Factory.getDao(UserProfessionDao.class);
	
	protected UserProfessionService() {
		this.basedao = userProfessionDao;
	}
	
	/**
	 * 查询职业信息
	 * @return
	 */
	public List<t_user_profession> queryByAccount() {
		
		return userProfessionDao.queryByAccount();
	}
	/**
	 * 
	 * @Title: insertUserPro
	 * 
	 * @description	保存职业 信息成功返回true
	 * @param  userProfession
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-下午4:37:40
	 */
	public boolean insertUserPro(t_user_profession userProfession) {
		
		return userProfessionDao.save(userProfession);
	}


}
