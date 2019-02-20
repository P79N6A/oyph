package controllers.front.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.annotation.ProxyLoginCheck;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.ProxyInterceptor;
import models.proxy.bean.AnnualRate;
import models.proxy.bean.CurrSaleMan;
import models.proxy.bean.ProfitRule;
import models.proxy.bean.SalesManUserBean;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_salesman_profit_rule;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.With;
import play.mvc.Scope.Session;
import services.proxy.ProfitService;
import services.proxy.RuleService;
import services.proxy.SalesManService;
import services.proxy.SalesManUserService;

/**
 * 前台 业务员控制器
 * 
 * @author Niu Dongfeng
 */
@With(ProxyInterceptor.class)
public class SalesManCtrl extends FrontBaseController {

	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	
	protected static RuleService ruleService = Factory.getService(RuleService.class);
	
	protected static ProfitService profitService = Factory.getService(ProfitService.class);
	
	protected static SalesManUserService salesManUserService = Factory.getService(SalesManUserService.class);

	
	/**
	 * 业务员首页
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManHomePre() {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		//业务员信息
		t_proxy_salesman salesman = salesManService.findByID(currSaleMan.id);
		if (salesman == null) {
			flash.error("代理商业务员角色查询失败");
			
			LoginCtrl.loginPre();
		}
		
		//代理商前三个月收益
		List<t_proxy_profit> list = profitService.listOfSalesManProfit(currSaleMan.id);
		
		String salesmanId = Encrypt.encrypt3DES(String.valueOf(salesman.id), ConfConst.ENCRYPTION_APP_KEY_DES);
		
		String extLink = getBaseURL() + "front/proxy/login/register.html?extCode=" + salesmanId;
		
		render(salesman, list, extLink);
	}
	
	/**
	 * 业务员收益规则
	 * 
	 * @author Niu Dongfeng
	 */
	public static void salesManRulePre(long proxyId) {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		t_proxy_salesman_profit_rule annual = ruleService.findByColumn("_key=? AND proxy_id=?" , "annual_discount_rate",proxyId);
		List<AnnualRate> annualConvert = new ArrayList<AnnualRate>();
		if (annual != null) {
			if (annual._value != null && !"".equals(annual._value)) {
				JSONArray jsonArray1 = JSONArray.fromObject(annual._value);
				annualConvert = (List)JSONArray.toList(jsonArray1, new AnnualRate() , new JsonConfig());
			}
		}
		
		render(annualConvert);
	}
	
	/**
	 * 查看名下会员
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManUserPre( int currPage, int pageSize, String userName, String userMobile) {
		
		flash.put("userName", userName);
		flash.put("userMobile", userMobile);
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		
		long salesManId = currSaleMan.id;
		
		PageBean<SalesManUserBean> page = salesManUserService.pageOfSalesManUsers(salesManId, currPage, pageSize, userName, userMobile,4,1);

		render(page, salesManId);
	}

}
