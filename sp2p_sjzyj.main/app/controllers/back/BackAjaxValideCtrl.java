package controllers.back;

import play.mvc.Controller;
import services.common.SupervisorService;

import common.utils.Factory;

public class BackAjaxValideCtrl extends Controller {

	private static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	/**
	 * 验证手机号码是否存在
	 *
	 * @param supervisorName
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月6日
	 */
	public static void checkSupervisorNameExits(String supervisorName) {
		boolean flag = supervisorService.isNameExists(supervisorName);
		if (flag) {
			
			renderJSON(false);
		} else {
			
			renderJSON(true);
		}
	}
	
	/**
	 * 判断管理员手机是否存在
	 *
	 * @param mobile
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月6日
	 */
	public static void checkSupervisorMobileExits(String mobile) {

		boolean flag = supervisorService.isMobileExists(mobile);
		if (flag) {
			renderJSON(false);
		} else {

			renderJSON(true);
		}
	}
	
	/**
	 * 修改用户手机号时(如果相等，让它修改)
	 *
	 * @param mobile
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月26日
	 */
	public static void checkSupervisorMobileExits2(String mobile,String hidemobile) {
		if (mobile.equals(hidemobile)) {

			renderJSON(true);
		}
		
		boolean flag = supervisorService.isMobileExists(mobile);
		if (flag) {
			
			renderJSON(false);
		} else {

			renderJSON(true);
		}
	}
}
