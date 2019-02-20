package com.shovesoft.sp2p.common;

import org.junit.Test;

import services.common.UserInfoService;

import com.shovesoft.sp2p.BaseUnit;
import common.enums.Client;
import common.utils.Factory;

public class UserInfoServiceUnit extends BaseUnit {

	
	/**
	 * 测试添加u用户信息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	@Test
	public void test_addUserInfo(){
		UserInfoService userInfoService = Factory.getService(UserInfoService.class);
		boolean flag = userInfoService.addUserInfo(2L,Client.PC,"","");
		System.out.println(flag);
	}
}
