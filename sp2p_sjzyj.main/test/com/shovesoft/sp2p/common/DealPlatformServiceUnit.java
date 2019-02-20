package com.shovesoft.sp2p.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import models.common.entity.t_deal_platform.DealRemark;

import org.junit.Test;

import services.common.DealPlatformService;

import com.shovesoft.sp2p.BaseUnit;
import common.utils.Factory;

public class DealPlatformServiceUnit extends BaseUnit {

	/**
	 * 测试添加平台交易记录
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	@Test
	public void  test_addPlatformDeal() {
		
		DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bill_no", "Z32");
		Random random = new Random();
		for(int i=0;i<100;i++){
			boolean flag = dealPlatformService.addPlatformDeal(2L, 30+random.nextInt(100), DealRemark.ADVANCE_PRIN_INTER,param);
			if(flag){
				System.out.println("交易记录添加成功");
			} else {
				System.out.println("交易记录添加失败");
			}
		}
	}
	
}
