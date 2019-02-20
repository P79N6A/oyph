package controllers.back.setting;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softkey.UkeyUtil;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import models.common.entity.t_ukey;
import services.common.UkeyService;


public class UkeySetCtrl extends BackBaseController {
	
	protected static UkeyService ukeyService = Factory.getService(UkeyService.class);
	
	/**
	 * 加密锁列表页面
	 * 
	 * @author niu
	 * @date 2018-03-21
	 */
	public static void toUkeyListPre(int currPage, int pageSize) {
		PageBean<t_ukey> page = ukeyService.pageOfUkey(currPage, pageSize);
		render(page);
	}
	
	/**
	 * 加密锁添加页面
	 * 
	 * @author niu
	 * @date 2018-03-21
	 */
	public static void toAddUkeyPre() {
		render();
	}
	
	/**
	 * 加密锁添加
	 * 
	 * @param ukeySn 加密锁SN号
	 * @param ukeyId 加密锁ID
	 * 
	 * @author niu
	 * @date 2018-03-23
	 */
	public static void addUkey(String ukeySn, String ukeyId) {
		
		flash("ukeySn", ukeySn);
		flash("ukeyId", ukeyId);
		
		if (ukeySn == null || "".equals(ukeySn)) {
			flash.error("请输入加密锁SN编号");
			toAddUkeyPre();
		}
		
		String regex = "SN.[\\d]{4}";
		Pattern pattern = Pattern.compile(regex);  
        Matcher matcher=pattern.matcher(ukeySn);  
		
		if (!matcher.matches()) {
			flash.error("加密锁SN编号输入格式错误");
			toAddUkeyPre();
		}
		
		if (ukeyId == null || "".equals(ukeyId)) {
			flash.error("请获取加密锁ID");
			toAddUkeyPre();
		}
		
		if (ukeyId.length() != 16) {
			flash.error("请获取16位加密锁ID");
			toAddUkeyPre();
		}
		
		ResultInfo result = ukeyService.saveUkey(ukeySn, ukeyId.toUpperCase());
		
		if (result.code < 1) {
			flash.error(result.msg);
			toAddUkeyPre();
		}
		
		flash.success("加密锁添加成功 ！");
		toAddUkeyPre();
	}
	
	/**
	 * ukey设置页面
	 * 
	 * @author niu
	 * @date 2018-03-16
	 */
	public static void toUkeySetPre(String ukeySn) {
		
		Random random = new Random();

		String[] numArr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		String[] strArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"}; 
		
		//生成增强算法密钥
		StringBuffer calKey = new StringBuffer();
		for (int i = 0; i < 32; i++) {
			int ran = random.nextInt(2);
			
			if (ran == 0) {
				calKey.append(numArr[random.nextInt(10)]);
			} 
			if (ran == 1) {
				calKey.append(strArr[random.nextInt(26)]);
			}
		}
		
		String cKey = calKey.toString().toUpperCase();
		
		render(ukeySn, cKey);
	}
	
	/**
	 * 修改加密锁状态
	 * 
	 * @param stat	状态
	 * @return
	 * 
	 * @author niu
	 * @date 2018-03-26
	 */
	public static void updateUkeyStatus(long ukeyId, int stat) {
		
		ResultInfo result = new ResultInfo();
		
		if (ukeyId <= 0 || stat <= 0) {
			result.code = -1;
			result.msg  = "请求参数错误";

			renderJSON(result);
		}
		
		if (!ukeyService.updateUkeyStatus(ukeyId, stat)) {
			result.code = -1;
			result.msg  = "修改加密锁状态失败";

			renderJSON(result);
		}
		
		result.code = 1;
		result.msg  = "修改加密锁状态成功";

		renderJSON(result);
	}
	
	/**
	 * 加密锁设置
	 * 
	 * @param ckey		增强算法密钥
	 * @param backAcct	后台登录账户
	 * @param ukeySn	加密锁SN号
	 * 
	 * @author niu
	 * @date 2018-03-26
	 */
	public static void setUkey(String ckey, String backAcct, String ukeySn, String ukeyId) {

		if (backAcct == "" || backAcct.length() < 2 || backAcct.length() > 10) {
			flash.error("请输入后台登录账户");
			
			toUkeySetPre(ukeySn);
		}
		
		ResultInfo result = ukeyService.setUkey(ukeyId, ckey, backAcct);
		if (result.code < 1) {
			flash.error(result.msg);
			
			toUkeySetPre(ukeySn);
		}
		
		flash.success("设置成功");
		toUkeySetPre(ukeySn);
	}
	
	
}





























