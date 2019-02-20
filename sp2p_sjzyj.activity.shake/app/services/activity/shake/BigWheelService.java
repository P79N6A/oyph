package services.activity.shake;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.activity.shake.BigWheelDao;

import models.activity.shake.entity.t_big_wheel;
import models.app.bean.ActivityListBean;
import services.base.BaseService;

public class BigWheelService extends BaseService<t_big_wheel>{
	protected static BigWheelDao bigWheelDao = Factory.getDao(BigWheelDao.class);
	protected BigWheelService(){
		
	}
	
	/**
	 * 
	 * @Title: getBigWheel
	 * @description: 查询进行中的大转盘活动 
	 *
	 * @return    
	 * @return List<t_big_wheel>   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-上午10:14:00
	 */
	public List<t_big_wheel> getBigWheel(){
		return bigWheelDao.findBigWheel();
		
	}
	/**
	 * 
	 * @Title: listOfBigWheel
	 * @description: 分页显示活动设置列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @return    
	 * @return PageBean<t_big_wheel>   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月13日-上午9:47:32
	 */
	public PageBean<t_big_wheel> listOfBigWheel(int currPage, int pageSize) {
		
		return bigWheelDao.pageOfByBigWheel(currPage, pageSize);
	}
	/**
	 * 
	 * @Title: findById
	 * @description: 通过id查询该条信息
	 *
	 * @param id
	 * @return    
	 * @return t_big_wheel   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月13日-上午9:47:51
	 */
	public t_big_wheel findById(long id) {
		
		return bigWheelDao.findById(id);
	}
	/**
	 * 
	 * @Title: findListActivityBean
	 * 
	 * @description pc端活动显示
	 * @param @return
	 * @return List<ActivityListBean>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月13日-上午10:14:26
	 */
	public List<ActivityListBean> findListActivityBean(){
		return bigWheelDao.findListOfActivityBean();
	}
	/**
	 * 
	 * @Title: findListActivityAppBean
	 * 
	 * @description app端列表显示
	 * @param @return
	 * @return List<ActivityListBean>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月13日-上午10:14:44
	 */
	public List<ActivityListBean> findListActivityAppBean(){
		return bigWheelDao.findListOfActivityAppBean();
	}
}
