package controllers.back.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.security.Encrypt;

import common.annotation.SubmitCheck;
import common.constants.CacheKey;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import daos.proxy.ProxyDao;
import models.proxy.bean.AnnualRate;
import models.proxy.bean.CurrSaleMan;
import models.proxy.bean.ProfitRule;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_salesman_profit_rule;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.cache.Cache;
import play.mvc.Scope.Session;
import services.proxy.RuleService;
import services.proxy.SalesManService;

/**
 * 后台代理商控制器
 * 
 * @author Niu Dongfeng
 */
public class BackProxyCtrl extends BackBaseController {

	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	
	protected static RuleService ruleService = Factory.getService(RuleService.class);
	
	protected static ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	/**
	 * 代理商首页
	 * @author Niu Dongfeng
	 */
	public static void backProxyHomePre(int currPage, int pageSize, int proxyStatus, String proxyName, String proxyMobile) {
		
		if (currPage <= 0) {
			currPage = 1;
		}
		
		if (pageSize <= 0) {
			pageSize = 10;
		}
		
		PageBean<t_proxy_salesman> page = salesManService.pageOfProxy(currPage, pageSize, proxyStatus, proxyName, proxyMobile);
		
		render(page, proxyStatus, proxyName, proxyMobile);
	}
	
	/**
	 * 添加代理商准备
	 * @author Niu Dongfeng
	 */
	@SubmitCheck
	public static void addProxyPre() {
		render();
	}
	
	/**
	 * 添加代理商
	 * @author Niu Dongfeng
	 */
	public static void addProxy(String proxyName, String proxyMobile, String proxyPwd) {
		
		if (StringUtils.isBlank(proxyName) || proxyName.length() < 2 || proxyName.length() > 15) {
			flash.error("真实姓名输入有误");
			addProxyPre();
		}
		
		if (!StrUtil.isMobileNum(proxyMobile)) {
			flash.error("手机号码输入有误!");
			addProxyPre();
		}
		
		if (salesManService.isMobileExists(proxyMobile)) {
			flash.error("手机号已存在");
			addProxyPre();
		}
		
		if (!StrUtil.isValidPassword(proxyPwd, 6, 15)) {
			flash.error("密码输入有误!");
			addProxyPre();
		}
		
		if (salesManService.addProxy(proxyName, proxyMobile, proxyPwd)) {
			flash.success("代理商添加成功");
			backProxyHomePre(1, 10, 1, "", "");
		}
		
		flash.error("代理商添加失败");
		addProxyPre();
	}
	
	/**
	 * 修改代理商准备
	 * @author Niu Dongfeng
	 */
	public static void updateProxyPre(long proxyId, String proxyMobile, String proxyName) {
			
		render(proxyId, proxyMobile, proxyName);
	}
	
	/**
	 * 修改代理商手机号
	 * @author Niu Dongfeng
	 */
	public static void updateMobile(long proxyId, String proxyMobile, String proxyName) {
		
		if (proxyId <= 0) {
			flash.error("代理商Id错误");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		if (!StrUtil.isMobileNum(proxyMobile)) {
			flash.error("手机号码输入有误!");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		if (salesManService.isMobileExists(proxyMobile)) {
			flash.error("手机号已存在");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		if (salesManService.updateMobile(proxyId, proxyMobile)) {
			flash.success("手机号修改成功");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		flash.error("手机号修改失败");
		updateProxyPre(proxyId, proxyMobile, proxyName);
	}
	
	/**
	 * 修改代理商密码
	 * @author Niu Dongfeng
	 */
	public static void updatePass(long proxyId, String proxyPwd, String proxyMobile, String proxyName) {
		
		if (proxyId <= 0) {
			flash.error("代理商Id错误");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		if (!StrUtil.isValidPassword(proxyPwd, 6, 15)) {
			flash.error("密码输入有误!");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		if (salesManService.updatePassWord(proxyId, proxyPwd)) {
			flash.success("密码修改成功");
			updateProxyPre(proxyId, proxyMobile, proxyName);
		}
		
		flash.error("密码修改失败!");
		updateProxyPre(proxyId, proxyMobile, proxyName);
	}
	
	/**
	 * 锁定代理商
	 * @author Niu Dongfeng
	 */
	public static void lockOrUnlockProxy(long proxyId) {
		
		ResultInfo result = new ResultInfo();
		
		if (proxyId <= 0) {
			result.code = -1;
			result.msg = "代理商Id错误";
			
			renderJSON(result);
		}
		
		result = salesManService.updateStatus(proxyId);
		renderJSON(result);
	}
	
	
	/**
	 * 代理商模拟登录
	 * @author Niu Dongfeng
	 */
	public static void imitateLogin() {
		
		backProxyHomePre(1, 10, 1, "", "");
	}
	
	/**
	 * 代理商收益规则 添加修改准备
	 * @author Niu Dongfeng
	 */
	public static void proxyProfitRulePre(long proxyId) {
		t_proxy proxy = proxyDao.findByID(proxyId);
		List<AnnualRate> profitRule = new ArrayList<AnnualRate>();
		if (proxy.profit_rule != null && !"".equals(proxy.profit_rule)) {
			JSONArray jsonArray = JSONArray.fromObject(proxy.profit_rule);
			profitRule = (List)JSONArray.toList(jsonArray, new AnnualRate(),new JsonConfig());
		}
		render(profitRule,proxyId);
	}
	
	/**
	 * 代理商/业务员收益规则 修改
	 * @author Niu Dongfeng
	 */
	public static void addOrUpdateProfitRule(long proxyId,Integer number) {
		Map<String, String> infos = new HashMap<String, String>();
		if (1 == number) {
			if (params.get("profit_rule") != "" && params.get("profit_rule") != null) {
				proxyDao.updateProxyProfitRule(proxyId, params.get("profit_rule"));
			}
			proxyProfitRulePre(proxyId);
		} else if (2 == number) {
			
			if (params.get("annual_discount_rate") !="" && params.get("annual_discount_rate") != null) {
				infos.put("annual_discount_rate", params.get("annual_discount_rate"));
			}
			if (params.get("salesman_rule_1") != "" && params.get("salesman_rule_1") != null) {
				infos.put("salesman_rule_1", params.get("salesman_rule_1"));
			}
			ruleService.updateRules(infos,proxyId);
			salesManRulePre(proxyId);
		}
	}
	
	/**
	 * 业务员提成规则
	 * @author Niu Dongfeng
	 */
	public static void salesManRulePre(long proxyId) {
		List<AnnualRate> convert = new ArrayList<AnnualRate>();
		List<ProfitRule> salesmanRule = new ArrayList<ProfitRule>();
		
		t_proxy_salesman_profit_rule annualConvert = ruleService.findByColumn("_key=? AND proxy_id=?", "annual_discount_rate",proxyId);
		t_proxy_salesman_profit_rule salesman = ruleService.findByColumn("_key=?  AND proxy_id=?", "salesman_rule_1",proxyId);
		
		if (annualConvert != null) {
			if (annualConvert._value != null && !"".equals(annualConvert._value)) {
				JSONArray jsonArray1 = JSONArray.fromObject(annualConvert._value);
				convert = (List)jsonArray1.toList(jsonArray1,new AnnualRate(),new JsonConfig());
			}
		}
		
		if (salesman != null) {
			if (salesman._value != null && !"".equals(salesman._value)) {
				JSONArray jsonArray2 = JSONArray.fromObject(salesman._value);
				salesmanRule = (List)jsonArray2.toList(jsonArray2, new ProfitRule(),new JsonConfig());
			}
		}
		
		render(convert,salesmanRule,proxyId);
	}
	
	/**
	 * 模拟登录
	 * 
	 * @param proxyId 代理商Id
	 * 
	 * @author niu
	 * @create 
	 */
	public static void proxySimulatedLoginPre(long proxyId) {
		
		t_proxy_salesman salesman = salesManService.findByID(proxyId);
		if (salesman == null) {
			flash.error("查询不到用户");
			
			backProxyHomePre(1, 10, 1, "", "");
		}
		
		if (Session.current() != null) {
			String sessionId = Session.current().getId();
			//设置用户凭证 
			Cache.set(CacheKey.FRONT_ + "proxy" + sessionId, salesman.id, Constants.CACHE_TIME_MINUS_30);
		}
		
		CurrSaleMan saleMan = new CurrSaleMan();
		saleMan.id = salesman.id;
		saleMan.name = salesman.sale_name;
		saleMan.proxyId = salesman.proxy_id;
		saleMan.mobile = salesman.sale_mobile;
		saleMan.type = salesman.type;
		
		// 刷新用户缓存信息 
		Cache.set("proxy_" + salesman.id, saleMan, Constants.CACHE_TIME_MINUS_30);
		if (saleMan.type == 2) {
			redirect("front.proxy.ProxyCtrl.proxyHomePre");
		}
		if (saleMan.type == 1) {
			redirect("front.proxy.SalesManCtrl.salesManHomePre");
		}
	}
	
}

















