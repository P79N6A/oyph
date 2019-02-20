package controllers.back.activity.shake;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.enums.JYSMSModel;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.common.entity.t_jdecard;
import services.common.JdecardService;

public class NewTaskCtrl extends BackBaseController{
	
	private static JdecardService jdecardService = Factory.getService(JdecardService.class);
	
	/**
	 * 
	 * @Title: newTaskListPre
	 * 
	 * @description 分页显示E卡中奖记录
	 * @param  currPage
	 * @param  pageSize
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午2:19:18
	 */
	public static void newTaskListPre(int currPage,int pageSize) {
		String mobile = params.get("appli_phone");
		PageBean<t_jdecard> pageBean = jdecardService.pageOfNewTask(mobile,currPage,pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 
	 * @Title: sendSmsNewTask
	 * 
	 * @description 给用户发送短信
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午2:56:16
	 */
	public static void sendSmsNewTask(Long id){
		t_jdecard jdecard = jdecardService.findByID(id);
		if (jdecard==null) {
			flash.error("发送失败");
		}
		String denomination = String.valueOf(jdecard.denomination);
		Map<String,String> map = new HashMap<String,String>();
		map.put("user_name", jdecard.user_name);
		map.put("denomination", denomination);
		map.put("jd_number", jdecard.jd_number);
		map.put("jd_password", jdecard.jd_password);
		
		String mobile = jdecard.mobile;
		if (JYSMSUtil.sendMessage(mobile, JYSMSModel.MODEL_JD_ECARD.tplId, map)) {
			jdecard.grant_status = 1;
			jdecard.sms_time = new Date();
			jdecard.save();
		}
	}
	/**
	 * 
	 * @Title: toEditJdECardPre
	 * 
	 * @description 进入编辑页面
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午3:57:34
	 */
	public static void toEditJdECardPre(Long id){
		t_jdecard jdecard = jdecardService.findByID(id);
		
		render(jdecard);
	}

	/**
	 * 
	 * @Title: editJdECard
	 * 
	 * @description 修改E卡账号，密码
	 * @param id
	 * @param jd_number
	 * @param jd_password
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午4:03:45
	 */
	public static void editJdECard(Long id,String jd_number,String jd_password){
		t_jdecard jdecard = jdecardService.findByID(id);
		if(jdecard==null){
			flash.error("编辑失败");
		}
		jdecard.jd_number = jd_number;
		jdecard.jd_password = jd_password;
		
		jdecard.save();
		flash.error("编辑成功");
		newTaskListPre(1,10);
		
	}
	
}
