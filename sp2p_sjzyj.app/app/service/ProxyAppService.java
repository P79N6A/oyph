package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BaseController;
import dao.AccountAppDao;
import dao.ProxyAppDao;
import daos.proxy.ProfitDao;
import daos.proxy.RuleDao;
import daos.proxy.SalesManDao;
import daos.proxy.SalesManUserDao;
import models.app.bean.ProxyAppBean;
import models.app.bean.ProxyProfitBean;
import models.app.bean.ProxyRuleBean;
import models.app.bean.SaleManAppBean;
import models.app.bean.SaleManInfo;
import models.app.bean.SaleManListBean;
import models.proxy.bean.AnnualRate;
import models.proxy.bean.ProfitRule;
import models.proxy.bean.SalesManProfitBean;
import models.proxy.bean.SalesManUserBean;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_salesman_profit_rule;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import play.mvc.Controller;
import services.proxy.ProxyService;

/**
 * 代理商或者业务员APP业务类
 * 
 * @author 
 */
public class ProxyAppService extends ProxyService {

	private static SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	private static RuleDao ruleDao = Factory.getDao(RuleDao.class);
	private static SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class);
	private static ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	private static ProxyAppDao proxyAppDao;
	private ProxyAppService() {
		proxyAppDao = Factory.getDao(ProxyAppDao.class);
		super.basedao = proxyAppDao;
	}
	
	/**
	 * 代理商或者业务员登录
	 * 
	 * @param mobile		用户名
	 * @param password		密码
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-02-05
	 */
	public String proxyOrSalemanLogining(String mobile, String password) {
		
		JSONObject json = new JSONObject();
		
		t_proxy_salesman salesman = salesManDao.findByColumn(" sale_mobile = ? ", mobile);
		if (salesman == null) {
			json.put("code", -1);
			json.put("msg" , "用户不存在");
			
			return json.toString();
		}
		if (salesman.sale_status != 1) {
			json.put("code", -1);
			json.put("msg" , "用户已被锁定");
			
			return json.toString();
		}
		
		String encryptPwd = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		if (!salesman.sale_pwd.equals(encryptPwd)) {
			json.put("code", -1);
			json.put("msg" , "密码不正确");
			
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg" , "登录成功");
		
		json.put("saleManId", salesman.id);
		json.put("type", salesman.type);
		json.put("saleManMobile", salesman.sale_mobile);
		json.put("proxyId", salesman.proxy_id);
		json.put("realName", salesman.sale_name);
		
		return json.toString();
	}
	
	
	/**
	 * APP 代理商主页收查询
	 * 
	 * @param saleManId
	 * @return
	 */
	public String proxyHome(long saleManId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		SaleManAppBean saleManAppBean = proxyAppDao.getSaleManById(saleManId);
		
		String salesmanId = Encrypt.encrypt3DES(String.valueOf(saleManId), ConfConst.ENCRYPTION_APP_KEY_DES);
		
		String extLink = BaseController.getBaseURL() + "front/proxy/login/register.html?extCode=" + salesmanId;
		
		
		
		if (saleManAppBean == null) {
			result.put("code", -1);
			result.put("msg" , "业务员信息查询失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		saleManAppBean.extLink = extLink;
		
		long proxyId = saleManAppBean.id;
		if (saleManAppBean.type == 2) {
			proxyId = saleManAppBean.proxyId;
		}
		
		ProxyAppBean proxyAppBean = proxyAppDao.getProxyById(proxyId, saleManAppBean.type);
		if (proxyAppBean == null) {
			result.put("code", -1);
			result.put("msg" , "代理商信息查询失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		List<ProxyProfitBean> list = proxyAppDao.listOfThreeMonthProfit(proxyId, saleManAppBean.type);
		
		result.put("code", 1);
		result.put("msg" , "查询成功");
		
		
		result.put("saleMan", saleManAppBean);
		result.put("proxy", proxyAppBean);
		result.put("profitList", list);
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 代理商规则
	 * 
	 * @param proxyId
	 * @return
	 */
	public String proxyRule(long proxyId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Object object = proxyAppDao.getProxyRuleById(proxyId);
		if (object == null || "".equals(object)) {
			result.put("code", -1);
			result.put("msg" , "代理商规则查询失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		JSONArray jsonArray = JSONArray.fromObject(object);
		List<ProxyRuleBean> proxyRule =  (List)JSONArray.toList(jsonArray, new ProxyRuleBean(), new JsonConfig());
		
		result.put("code", 1);
		result.put("msg" , "代理商规则查询成功");
		result.put("ruleList", proxyRule);
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 业务员规则查询
	 * @return
	 */
	public String saleManRule(long saleManId) {
		Map<String, Object> result = new HashMap<String, Object>();
		t_proxy_salesman salesman = salesManDao.findByID(saleManId);
		
		if (salesman == null) {
			result.put("code", 1);
			result.put("msg" , "业务员查询失败");
			
			JSONObject.fromObject(result).toString();
		}
		
		result.put("code", 1);
		result.put("msg" , "业务员规则查询成功");
		
		t_proxy_salesman_profit_rule saleManRule1 = ruleDao.findByColumn(" _key = ? AND proxy_id = ?", "annual_discount_rate", salesman.proxy_id);
		t_proxy_salesman_profit_rule saleManRule2 = ruleDao.findByColumn(" _key = ? AND proxy_id = ?", "salesman_rule_1", salesman.proxy_id);
		
		if (saleManRule1 != null && saleManRule1._value != null && !"".equals(saleManRule1._value)) {
			JSONArray jsonArray1 = JSONArray.fromObject(saleManRule1._value);
			result.put("convertList", (List)JSONArray.toList(jsonArray1, new ProxyRuleBean() , new JsonConfig()));
		} else {
			result.put("convertList", null);
		}
		
		if (saleManRule2 != null && saleManRule2._value != null && !"".equals(saleManRule2._value)) {
			JSONArray jsonArray2 = JSONArray.fromObject(saleManRule2._value);
			result.put("profitList", (List)JSONArray.toList(jsonArray2, new ProfitRule() , new JsonConfig()));
		} else {
			result.put("profitList", null);
		}

		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 业务员列表查询
	 * @param proxyId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public String listOfSaleMan(long proxyId, int currPage, int pageSize) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("code", 1);
		result.put("msg" , "业务员列表查询成功");
		
		PageBean<SaleManListBean> pageBean = proxyAppDao.pageOfSaleMan(proxyId, currPage, pageSize);
		if (pageBean != null) {
			result.put("saleManList", pageBean.page);
		} else {
			result.put("saleManList", null);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 业务员信息查询
	 * @param saleManId
	 * @return
	 */
	public String getSaleManInfo(long saleManId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		SaleManInfo saleManInfo= proxyAppDao.getSaleManInfo(saleManId);
		
		String salesmanId = Encrypt.encrypt3DES(String.valueOf(saleManId), ConfConst.ENCRYPTION_APP_KEY_DES);
		
		String extLink = BaseController.getBaseURL() + "front/proxy/login/register.html?extCode=" + salesmanId;
		
		saleManInfo.extLink = extLink;
		
		result.put("code", 1);
		result.put("msg" , "业务员信息查询成功");
		result.put("saleMan", saleManInfo);
		
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 业务员名下会员查询
	 * @param saleManId
	 * @return
	 */
	public String listOfSaleManUsers(long saleManId, int currPage, int pageSize, int orderType, int orderValue) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("code", 1);
		result.put("msg" , "业务员名下会员查询成功");
		
		PageBean<SalesManUserBean> pageBean = salesManUserDao.pageOfSalesManUsers(saleManId, currPage, pageSize, "", "", orderType, orderValue);
		
		if (pageBean != null) {
			result.put("userList", pageBean.page);
		} else {
			result.put("userList", null);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 业务员提成
	 * @param proxyId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public String listOfSaleManPrift(long proxyId, int currPage, int pageSize, String profitTime) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("code", 1);
		result.put("msg" , "业务员提成查询成功");
		
		PageBean<SalesManProfitBean> pageBean = profitDao.pageOfSalesManProfit(proxyId, currPage, pageSize, "", "", profitTime);
		if (pageBean != null) {
			result.put("profitList" , pageBean.page);
		} else {
			result.put("profitList" , null);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	
}

































