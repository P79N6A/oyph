package com.shovesoft.sp2p.core;

import models.common.entity.t_user;

import org.junit.Test;

import play.Logger;

import java.util.List;

import services.common.UserFundService;
import services.common.UserService;

import com.shovesoft.sp2p.BaseUnit;

import common.utils.Factory;
import common.utils.ResultInfo;
import daos.common.UserDao;

/** 
 * 批量更新用户资产签名
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年4月1日 下午2:38:25  
 */
public class UserFundSignUnit extends BaseUnit{
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	/**
	 * 批量更新用户资产签名
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月1日
	 */
	@Test
	public void bacthUpdateUserFundSign(){
		ResultInfo result = new ResultInfo();
		
		Logger.info("-----------批量更新用户资产签名开始-----------");
	    
		/* 查询平台用户 */
		List<t_user> userList = userService.findAll();
		if(userList == null)
		{
			return;
		}
		
		int count = 0;
		for(t_user user : userList)
		{
			long userId = user.id;

			/* 根据用户ID更新签名 */
			result = userFundService.userFundSignUpdate(userId);
			if(result.code > 0)
			{
				count++ ;
			}
		}
		
		Logger.info("-----------批量更新用户资产签名共计：" + count);
	}

}
