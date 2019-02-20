package services.activity.shake;

import common.utils.Factory;
import daos.activity.shake.DisplayStatusDao;
import daos.activity.shake.ShakeRecordDao;
import models.activity.shake.entity.t_display_status;
import models.activity.shake.entity.t_shake_record;
import services.base.BaseService;

public class DisplayStatusService extends BaseService<t_display_status>{
	
	protected DisplayStatusDao displayStatusDao;
	
	protected DisplayStatusService() {
		displayStatusDao = Factory.getDao(DisplayStatusDao.class);
		this.basedao = displayStatusDao;
	}
	
	/**
	 * 
	 * @Title: findDisplayStatus
	 * 
	 * @description 页面是否显示状态值
	 * @return t_shake_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月7日-上午11:50:42
	 */
	public t_display_status findDisplayStatus() {
		
		return displayStatusDao.findDisplayStatus();
	}
	/**
	 * 
	 * @Title: updateDisplayStatus
	 * 
	 * @description 修改显示状态
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月7日-下午5:19:09
	 */
	public int updateDisplayStatus() {
		
		return displayStatusDao.updateDisplayStatus();
	}
	
}
