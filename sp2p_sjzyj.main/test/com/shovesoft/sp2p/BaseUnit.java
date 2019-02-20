package com.shovesoft.sp2p;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.UnitTest;

public class BaseUnit extends UnitTest {

	static Date dateBegin;
	static Date dateEnd;

	@BeforeClass
	public static void testBegin() {
		dateBegin = new Date();
		System.out.println("\n\n------------------------淫-荡-的-分-割-线------------------------------->\n测试开始:" + dateBegin.toLocaleString()+"\n");
	}

	@AfterClass
	public static void testEnd() {
		dateEnd = new Date();
		System.out.println("\n测试结束:" + dateEnd.toLocaleString());
		System.out.println("##----------------------\t总耗时:\t"+ (dateEnd.getTime() - dateBegin.getTime()) + "\t毫秒----------------------##\n<------------------------线-割-分-的-荡-淫-------------------------------\n\n");
	}
	
/*	*//**
	 * 使用日志示范
	 *
	 * @param args
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月16日
	 *//*
	@Test
	public void aaaaa() {
		System.out.println("=======================异常日志提示示范");
		method1();
		
		System.out.println("=======================提示信息示范");
		LoggerUtil.info(true, "重复操作！");
		
		try {
			int i = 10/0;
		} catch (Exception e) {
			LoggerUtil.error(true, e, "除法计算异常。");
		}
	}
	
	public static void method1() {
		method2(); 
	}
	public static void method2() {
		method3();
	}
	public static void method3() {
		 method4();
	}
	public static void method4() {
		caculate();
	}
	public static void caculate() {
		
		int chushu = 10;
		
		int beichushu = 0;
		
		try {
			int i = chushu/beichushu;
		} catch (Exception e) {
			LoggerUtil.error(true, e, "除法计算异常。【除数：%s;被除数：%s】", chushu, beichushu);
		}
	}*/
}
