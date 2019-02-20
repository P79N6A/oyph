package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.InformationDao;
import daos.common.JdecardDao;
import models.common.entity.t_jdecard;
import services.base.BaseService;

public class JdecardService extends BaseService<t_jdecard>{

	protected static JdecardDao jdecardDao = Factory.getDao(JdecardDao.class);
	
	protected JdecardService(){
		super.basedao = jdecardDao;
	}
	
	/**
	 * 
	 * @Title: pageOfNewTask
	 * 
	 * @description 分页显示E卡中奖记录
	 * @param currPage
	 * @param pageSize
	 * @return PageBean<t_jdecard>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午2:19:45
	 */
	public PageBean<t_jdecard> pageOfNewTask(String mobile,int currPage, int pageSize) {
		
		return jdecardDao.pageOfNewTask(mobile,currPage,pageSize);
	}

	/**
	 * 
	 * @Title: findBymobile
	 *
	 * @description 根据mobile和denomination查询t_jdecard表中是否存在记录
	 *
	 * @param @param mobile
	 * @param @param denomination
	 * @param @return 
	 * 
	 * @return t_jdecard    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年2月18日
	 */
	public t_jdecard findjdEByuserId (Long user_id,int denomination){
		
		return jdecardDao.findjdEByuserId(user_id, denomination);
	}
	
	/**
	 * 
	 * @Title: pageOfNewTaskAll
	 *
	 * @description 分页显示所有个人中奖信息
	 *
	 * @param @param mobile
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @return 
	 * 
	 * @return PageBean<t_jdecard>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年2月18日
	 */
    public PageBean<t_jdecard> pageOfNewTaskAll(Long user_id,int currPage, int pageSize) {
		
		return jdecardDao.pageOfNewTaskAll(user_id,currPage,pageSize);
	}
}
