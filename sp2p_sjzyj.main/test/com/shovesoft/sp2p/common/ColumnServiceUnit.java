package com.shovesoft.sp2p.common;

import java.util.Map;

import org.junit.Test;

import services.common.ColumnService;

import com.shovesoft.sp2p.BaseUnit;
import common.utils.Factory;
import common.utils.PageBean;

import daos.common.UserDao;

public class ColumnServiceUnit extends BaseUnit {

	@Test
	public void test(){
		ColumnService columnService = Factory.getService(ColumnService.class);
		System.out.println(columnService);
	}
	
	@Test
	public void test2(){
		
		UserDao userDao = Factory.getDao(UserDao.class);
		
		String querySQL = "SELECT t.id as id,t.name as name,tuf.balance as balance FROM t_user t, t_user_fund tuf WHERE t.id = tuf.user_id";
		String countSQL = "SELECT COUNT(t.id) FROM t_user t, t_user_fund tuf WHERE t.id = tuf.user_id";
		PageBean<Map<String, Object>> pageBean = userDao.pageOfMapBySQL(1, 10, countSQL, querySQL, null);
		System.out.println(pageBean);
	}
	
}
