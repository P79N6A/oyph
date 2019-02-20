package services.common;

import common.utils.Factory;
import daos.common.ssqUserDao;
import models.common.entity.t_disclosure;
import models.common.entity.t_ssq_user;
import services.base.BaseService;

public class ssqUserService extends BaseService<t_ssq_user>{

	protected static ssqUserDao ssquserDao = Factory.getDao(ssqUserDao.class);
	
	
	/**
	 * 保存上上签用户证书申请任务编号和状态
	 * 
	 * @param taskId
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日11:22:29
	 */
	public boolean userSealTaskId(long userId , String mobile, String taskId ,int status) {

		t_ssq_user ssquser = new t_ssq_user();
		ssquser.user_id = userId;
		ssquser.account = mobile;
		ssquser.seal = taskId ;
		ssquser.application_status = status ; //处理
		
		return ssquserDao.save(ssquser);
	}
	
	/**
	 * 修改上上签用户证书申请任务编号和状态
	 * 
	 * @param taskId
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日11:22:29
	 */
	public boolean updateUserSealTaskId(t_ssq_user ssquser , String taskId ,int status) {
		
		ssquser.seal = taskId ;
		ssquser.application_status = status ; 
		
		return ssquserDao.save(ssquser);
	}
	
	/**
	 * 查询上上签注册成功用户
	 * @param userId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年4月18日17:28:01
	 */
	public t_ssq_user findByUserId(long userId) {
		
		return ssquserDao.findByUserId(userId);
	}
	
	/**
	 * 查询上上签用户
	 * @param userId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年4月18日17:28:01
	 */
	public t_ssq_user findssqByUserId(long userId) {
		
		return ssquserDao.findssqByUserId(userId);
	}
	
	
	/**
	 * 查询上上签用户
	 * @param userId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年4月18日17:28:01
	 */
	public t_ssq_user findByAccount(String account) {
		
		return ssquserDao.findByAccount(account);
	}
	
}

	
