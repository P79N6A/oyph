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
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.ProxyInterceptor;
import models.proxy.bean.AnnualRate;
import models.proxy.bean.CurrSaleMan;
import models.proxy.bean.SalesManProfitBean;
import models.proxy.bean.SalesManUserBean;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import play.cache.Cache;
import play.mvc.Scope.Session;
import play.mvc.With;
import services.proxy.ProfitService;
import services.proxy.ProxyService;
import services.proxy.SalesManService;
import services.proxy.SalesManUserService;

/**
 * 前台 代理商控制器
 * 
 * @author Niu Dongfeng
 */
@With(ProxyInterceptor.class)
public class ProxyCtrl extends FrontBaseController {

	protected static ProxyService proxyService = Factory.getService(ProxyService.class);
	
	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	
	protected static ProfitService profitService = Factory.getService(ProfitService.class);
	
	protected static SalesManUserService salesManUserService = Factory.getService(SalesManUserService.class);
	
	
	
	/**
	 * 代理商首页
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void proxyHomePre() {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		//业务员信息
		t_proxy_salesman salesman = salesManService.findByID(currSaleMan.id);
		if (salesman == null) {
			flash.error("代理商业务员角色查询失败");
			
			LoginCtrl.loginPre();
		}
		//代理商信息
		t_proxy proxy = proxyService.findByID(salesman.proxy_id);
		if (proxy == null) {
			flash.error("代理商信息查询失败");
			
			LoginCtrl.loginPre();
		}
		
		//代理商前三个月收益
		List<t_proxy_profit> list = profitService.listOfProxyProfit(salesman.proxy_id);
		String enProxyId = Encrypt.encrypt3DES(String.valueOf(salesman.id), ConfConst.ENCRYPTION_APP_KEY_DES);
		String extLink = getBaseURL() + "front/proxy/login/register.html?extCode=" + enProxyId;
		
		render(proxy, salesman, list, extLink);
	}
	
	/**
	 * 代理商收益规则
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void proxyRulePre() {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		List<AnnualRate> proxyRule = new ArrayList<AnnualRate>();
		Object object = Cache.get(CacheKey.FRONT_ + "proxy" + Session.current().getId());
		
		if (object == null) {
			LoginCtrl.loginPre();
		}
		long proxyId = Convert.strToLong(object.toString(), 0);
		t_proxy_salesman salesman = salesManService.findByID(proxyId);
		
		t_proxy proxy = proxyService.findByID(salesman.proxy_id);
		if (proxy != null && proxy.profit_rule != null && !"".equals(proxy.profit_rule)) {
			JSONArray jsonArray = JSONArray.fromObject(proxy.profit_rule);
			proxyRule = (List)JSONArray.toList(jsonArray, new AnnualRate(), new JsonConfig());
		}
		
		render(proxyRule);
	}
	
	/**
	 * 业务员管理
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManManagePre(int currPage, int pageSize, String saleName, String saleMobile) {
		
		flash.put("saleName", saleName);
		flash.put("saleMobile", saleMobile);
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		if(saleName != null && (!saleName.equals(""))) {
			currPage = 1;
		}
		
		if(saleMobile != null && (!saleMobile.equals(""))) {
			currPage = 1;
		}
		
		//业务员查询
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		
		PageBean<t_proxy_salesman> page = salesManService.pageOfSalesManByProxyId(currSaleMan.proxyId, currPage, pageSize, saleName, saleMobile);
		
		render(page);
	}
	
	/**
	 * 业务员名下会员
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManUsersPre(long salesManId, int currPage, int pageSize, String userName, String userMobile) {
		
		flash.put("userName", userName);
		flash.put("userMobile", userMobile);
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		if(userName != null && (!userName.equals(""))) {
			currPage = 1;
		}
		
		if(userMobile != null && (!userMobile.equals(""))) {
			currPage = 1;
		}
		
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		
		PageBean<SalesManUserBean> page = salesManUserService.pageOfSalesManUsers(salesManId, currPage, pageSize, userName, userMobile,4,1);
	
		render(page, salesManId);
	}
	
	/**
	 * 业务员添加准备
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void addSalesManPre() {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		render();
	}
	
	/**
	 * 业务员添加
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void addSalesMan(String salesManName, String salesManMobile, String salesManPwd) {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		if (StringUtils.isBlank(salesManName) || salesManName.length() < 2 || salesManName.length() > 15) {
			flash.error("真实姓名输入有误");
			
			addSalesManPre();
		}
		
		if (!StrUtil.isMobileNum(salesManMobile)) {
			flash.error("手机号码输入有误!");
			
			addSalesManPre();
		}
		
		if (salesManService.isMobileExists(salesManMobile)) {
			flash.error("手机号已存在");
			
			addSalesManPre();
		}
		
		if (!StrUtil.isValidPassword(salesManPwd, 6, 15)) {
			flash.error("密码输入有误!");
			
			addSalesManPre();
		}
		
		ResultInfo result = salesManService.addSalesMan(currSaleMan.proxyId, salesManName, salesManMobile, salesManPwd);
		if (result.code < 0) {
			flash.error(result.msg);
			
			addSalesManPre();
		}
		
		
		
		flash.success(result.msg);
		salesManManagePre(1, 10, "", "");
	}
	
	/**
	 * 业务员编辑准备
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManEidtPre(long saleManId) {

		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		//查询业务员信息
		t_proxy_salesman salesman = salesManService.findByID(saleManId);
		
		render(salesman);
	}
	
	/**
	 * 业务员编辑
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManEidt(long proxyId, String salesManName, String salesManMobile, String salesManPwd) {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		//业务员编辑
		t_proxy_salesman salesman = salesManService.findByID(proxyId);
		if (salesman == null) {
			flash.error("查询不到业务员");
			
			salesManEidtPre(proxyId);
		}
		
		if (StringUtils.isBlank(salesManName) || salesManName.length() < 2 || salesManName.length() > 15) {
			flash.error("真实姓名输入有误");
			
			salesManEidtPre(proxyId);
		}
		
		if (!StrUtil.isMobileNum(salesManMobile)) {
			flash.error("手机号码输入有误!");
			
			salesManEidtPre(proxyId);
		}
		
		if (salesManService.isMobileExists2(salesManMobile, salesman.id)) {
			flash.error("手机号已存在");
			
			salesManEidtPre(proxyId);
		}
		
		if (!StringUtils.isBlank(salesManPwd) && StrUtil.isValidPassword(salesManPwd, 6, 15)) {
			salesman.sale_pwd = Encrypt.MD5(salesManPwd + ConfConst.ENCRYPTION_KEY_MD5);//密码加密	
		}
	
		salesman.sale_name = salesManName;
		salesman.sale_mobile = salesManMobile;

		t_proxy_salesman salesman2 = salesman.save();
		if (salesman2 == null) {
			flash.error("业务员编辑失败!");
			
			salesManEidtPre(proxyId);
		}
		
		flash.success("业务员修改成功");
		salesManEidtPre(proxyId);
	}
	
	
	/**
	 * 业务员提成
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void salesManProfitPre(int currPage, int pageSize, String userName, String userMobile, String profitTime) {
		
		//回显数据
		flash.put("userName", userName);
		flash.put("userMobile", userMobile);
		flash.put("profitTime", "".equals(profitTime) ? null : profitTime);
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null || currSaleMan.type != 2) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);

		//查询业务员提成
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}
		
		PageBean<SalesManProfitBean> page = profitService.pageOfSalesManProfit(currSaleMan.proxyId, currPage, pageSize, userName, userMobile, profitTime);
		
		render(page);
	}
	
	
	
}
