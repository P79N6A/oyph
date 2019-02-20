package com.shovesoft.sp2p.common;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_setting_platform;

import org.junit.Test;

import services.common.SettingService;

import com.shovesoft.sp2p.BaseUnit;
import common.constants.SettingKey;
import common.utils.Factory;

public class SettingServiceUnit extends BaseUnit {

	/**
	 * 测试findSettingByKey方法
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	@Test
	public void test_findSettingByKey() {

		try {
			
			SettingService settingPlatformService = Factory.getService(SettingService.class);
			
			/*
			t_setting_platform setting_platform = settingPlatformService.findByID(null);
			System.out.println(setting_platform);
			t_setting_platform setting_platform = settingPlatformService.findByID(-2L);
			System.out.println(setting_platform);
			*/
			
			/*
			List<t_setting_platform> listOfSetting = settingPlatformService.findAll();
			System.out.println(listOfSetting);
			*/
			
//			t_setting_platform setting_platform = settingPlatformService.findSettingByKey("register_code");
//			t_setting_platform setting_platform = settingPlatformService.findSettingByKey("");
			t_setting_platform setting_platform = settingPlatformService.findSettingByKey(null);
			System.out.println(setting_platform);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 测试更改"前台是否显示统计数据"系统参数
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	@Test
	public void test_updateIsStatisticsShow(){
		
		SettingService settingPlatformService = Factory.getService(SettingService.class);
		boolean flag = settingPlatformService.updateIsStatisticsShow(false);
		System.out.println(flag);
		
	}
	
	
	/**
	 * 测试更改"项目发布预告:project_releases_trailer "系统参数
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	@Test
	public void test_updateProjectReleasesTrailer(){
		
		SettingService settingPlatformService = Factory.getService(SettingService.class);
		boolean flag = settingPlatformService.updateProjectReleasesTrailer("每天9点，15点准时发布预告");
		System.out.println(flag);
		
	}
	
	/**
	 * 测试修改系统设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	@Test
	public void test_updateSetting(){
		
		SettingService settingPlatformService = Factory.getService(SettingService.class);
		boolean flag = settingPlatformService.updateSetting(SettingKey.IS_STATISTICS_SHOW,"1");
		System.out.println(flag);
		
	}
	
	/**
	 * 测试批量修改系统设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	@Test
	public void test_updateSettings(){
		
		SettingService settingPlatformService = Factory.getService(SettingService.class);
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.IS_STATISTICS_SHOW, "每天9点，15点准时发布预告");
		settings.put(SettingKey.PROJECT_RELEASES_TRAILER, "1");
		
		settingPlatformService.updateSettings(settings);
		
		String setting_platform = settingPlatformService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW);
		
		System.out.println(setting_platform);
		
	}
}
