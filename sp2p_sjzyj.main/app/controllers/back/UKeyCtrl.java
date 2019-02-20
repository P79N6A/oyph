package controllers.back;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import services.common.SupervisorService;

import common.utils.Factory;

import controllers.common.BaseController;

/**
 * 安全云盾控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年3月14日
 */
public class UKeyCtrl extends BaseController{

	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	/**
	 * 云盾登录
	 * @param userName
	 * @param password
	 * @param sign
	 * @throws UnsupportedEncodingException
	 */
	public static void ukeyCheck(String userName, String password, String sign, String time) {

		String supervisorSign = supervisorService.checkUkey(userName, password, sign, time);
		ByteArrayInputStream is = null;
		try {
			is = new ByteArrayInputStream(supervisorSign.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		renderBinary(is);
	}

}
