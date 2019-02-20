package com.shovesoft.sp2p.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.SupervisorEventLog;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_supervisor;

import org.junit.Test;

import services.common.SupervisorService;

import com.shovesoft.sp2p.BaseUnit;
import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;

import daos.common.SupervisorDao;
import daos.common.UserFundDao;

public class SupervisorServiceUnit extends BaseUnit {
	
	@Test
	public void test_hys() {
		SupervisorDao supervisorDao = Factory.getDao(SupervisorDao.class);
		
		
		
		System.out.println(supervisorDao.countByColumn("name like ?", "%hys%"));
		System.out.println(supervisorDao.findByColumn("name like ?", "%hys%"));
		System.out.println(supervisorDao.pageOfByColumn(1, 10, "name like ?", "%hys%"));
		System.out.println(supervisorDao.pageOfAll(1, 10));
	}
	
	@Test
	public void test_hys2() {
//		IDataSafetyService dataSafetyService = Factory.getService(DataSafetyService.class);
//		
//		IUserService userService = Factory.getService(UserService.class);
//		
//		ResultInfo result = null;
//		
//		result = dataSafetyService.userFundSignCheck(28L);
//		System.out.println("验签："+ result.msg);
//		
//		int row = userService.userFundAdd(28L, 10.0);
//		System.out.println("用户资金增加：row=" + row);
//		
//		
//		
//		result = dataSafetyService.userFundSignUpdate(28L);
//		System.out.println("更新：" + result.msg);
		
		
		
		
	}
	
	@Test
	public void test_hys3() {
//		IDataSafetyService dataSafetyService = Factory.getService(DataSafetyService.class);
		
		UserFundDao userFundDao = Factory.getDao(UserFundDao.class);
		
//		ResultInfo result = null;
		
//		t_user_fund user = userFundDao.findByColumn("user_id = ?", 28L);
		
//		System.out.println("更新前：" + user.balance);
		
//		user.balance = 100;
//		userFundDao.save(user);
		
//		userFundDao.updateUserFundAdd(28L, 10.0);
		

		
//		t_user_fund user2 = userFundDao.findByColumn("user_id = ?", 28L);
		
//		System.out.println("更新后：" + user2.balance);
		
		
		
		
		
		
	}

	@Test
	public void test_save() {
			
	}
	
/*	@Test
	public void test_update() {
		
		ISupervisorService supervisorService = Factory.getService(SupervisorService.class);
		
		t_supervisor supervisor = supervisorService.findByID(2L);
		
		supervisor.name = "hys";
		
		supervisorService.save(supervisor);

	}*/
	
/*	@Test
	public void test_delete() {

		ISupervisorService supervisorService = Factory.getService(SupervisorService.class);
		
		supervisorService.delete(1L);

	}*/
	
	@Test
	public void test_findByID() {
		try {
			
			SupervisorService supervisorService = Factory.getService(SupervisorService.class);
			
			t_supervisor supervisor = supervisorService.findByID(1L);
			if (supervisor != null) {
				System.out.println(supervisor.getLock_status());
			}
			
			System.out.println(supervisor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_findAll() {
			
		SupervisorService supervisorService = Factory.getService(SupervisorService.class);
		
		List<t_supervisor> supervisor = supervisorService.findAll();
		
		System.out.println(supervisor);
	}
	
	@Test
	public void test_findByColumn() {
		
		SupervisorService supervisorService = Factory.getService(SupervisorService.class);
		
		List<t_supervisor> supervisors = supervisorService.findListByColumn("id = ? AND name like ?", 1L, "%hys%");
		
		System.out.println(supervisors.toString());
	}
	

	
	
	/**
	 * 登录测试(测试通过)
	 * 
	 * @description 测试用例包括:<br>
	 * 					正常登录、<br>
	 * 					用户名错误、<br>
	 * 					密码错误(错误次数不到限制数量)、<br>
	 * 					密码错误(到大上限)、<br>
	 * 					密码错误锁定有效期内登录、<br>
	 * 					密码错误锁定超出有效期内登录、<br>
	 * 					账户被管理员锁定<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	@Test
	public void test_login(){
		try {
			SupervisorService supervisorService = Factory.getService(SupervisorService.class);
			
			String name = "dzm121502";
			String password = "1234567";
//			ResultInfo result = supervisorService.login(name, com.shove.security.Encrypt.MD5(password + PlayConfConst.ENCRYPTION_KEY), "127.0.0.1");
			ResultInfo result = supervisorService.login(name, password, "127.0.0.1");
			
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试判断名字是否存在的方法
	 *
	 *@description 测试用例包括:<br>
	 *				空串(结果:不报错，返回false)<br>
	 *				null(结果:不报错，返回false)<br>
	 *				大小写有问题(结果:不报错，返回true【mysql默认的utf-8字符集不区分大小写。需要更新校对规则】)<br>
	 *				大小没有有问题(结果:不报错，返回true)<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
//	@Test
	public void test_isNameExists() {

		try {
			String name = "Dzm1023423";
			
			SupervisorService supervisorService = Factory.getService(SupervisorService.class);
			boolean flag = supervisorService.isNameExists(name);
			System.out.println(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * 测试管理员手机是否存在，如果存在则返回true，反之返回false
	 *
	 *@description 测试用例包括:<br>
	 *				已经有的号码(true)<br>		
	 *				没有有的号码(false)<br>		
	 *				空串(false)<br>		
	 *				null(false)<br>		
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	@Test
	public void test_isMobileExists() {
		
		try {
			String mobile = "18823858481";
			
			SupervisorService supervisorService = Factory.getService(SupervisorService.class);
			boolean flag = supervisorService.isMobileExists(mobile);
			System.out.println(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 测试添加管理员
	 *
	 *@description 测试用例包括:<br>
	 *				用户名相同<br>
	 *				手机相同<br>
	 *				正常添加<br>
	 *				手动设置错误，并回滚<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
//	@Test
	public void addSupervisor(){
	
		
	}
	
	/**
	 * 测试编辑管理员(主要是id和)
	 *
	 * @description 测试用例包括:<br>
	 * 				手机号更改，且原系统中有,修改密码(-2，提示管理员手机重复)<br>
	 * 				手机号更改，且原系统中没有，修改密码(1,更新成功)<br>
	 * 				手机号不更改，修改密码(1,更新成功)<br>
	 * 				手动让程序报错(-3,系统异常)<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	@Test
	public void test_upadteSupervisorPassAMobile() {

	
		
	}
	
	/**
	 * 测试解锁和锁定管理员
	 *
	 *@description 测试用例包括:<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	@Test
	public void test_lockSupervisor_unlockSupervisor() {

		try {
			SupervisorService supervisorService = Factory.getService(SupervisorService.class);
			Long id = 1L;
			boolean result = supervisorService.lockSupervisor(id);
//			ResultInfo result = supervisorService.unlockSupervisor(id);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 测试添加管理员日志事件
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	@Test
	public void test_addSupervisorEvent(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("supervisor", "黄运松");
		
		SupervisorService supervisorService = Factory.getService(SupervisorService.class);
		
		boolean flag = supervisorService.addSupervisorEvent(2L, Event.LOGIN, map);
		
		if(flag){
			System.out.println("添加管理员事件成功");
		}else {
			System.out.println("添加管理员事件失败");
		}
		
	}
	
	/**
	 * 分页查询所有的管理员日志
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	@Test
	public void test_pageOfAllEventLogs(){
		
		
	}
	
}
