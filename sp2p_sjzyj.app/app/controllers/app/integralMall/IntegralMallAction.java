package controllers.app.integralMall;

import java.util.Map;

import common.utils.Factory;
import service.AccountAppService;
import service.IntegralMallService;

/**
 * 积分商城[OPT:512X]
 *
 * @description 
 *
 * @author NiuDongFeng
 * @createDate 2017年2月22日
 */
public class IntegralMallAction {
	
	private static IntegralMallService integralMallService = Factory.getService(IntegralMallService.class);
	
	/***
	 * 
	 * 按商品分类查询商品（opt=5121）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author NiuDongFeng	
	 * @createDate 2017-2-22
	 */
	public static String findGoodsByTypeId(Map<String, String> parameters) {
		
		return integralMallService.findGoodsByTypeId(parameters);
	}
	
	
}
