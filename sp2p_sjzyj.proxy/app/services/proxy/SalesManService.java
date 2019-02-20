package services.proxy;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.security.Encrypt;

import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.proxy.ProxyDao;
import daos.proxy.SalesManDao;
import models.common.bean.CurrUser;
import models.proxy.bean.CurrSaleMan;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;
import play.cache.Cache;
import play.mvc.Scope.Session;
import services.base.BaseService;

/**
 * 业务员业务
 * @author Niu Dongfeng
 */
public class SalesManService extends BaseService<t_proxy_salesman> {

	protected SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	protected static ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	protected SalesManService() {
		super.basedao = salesManDao;
	}
	
	/**
	 * 代理商查询
	 * @author Niu Dongfeng
	 */
	public PageBean<t_proxy_salesman> pageOfProxy(int currPage, int pageSize, int proxyStatus, String proxyName, String proxyMobile) {
			
		return proxyDao.pageOfProxy(currPage, pageSize, proxyStatus, proxyName, proxyMobile);	
	}
	
	/**
	 * 添加代理商
	 * @author Niu Dongfeng
	 */
	public boolean addProxy(String proxyName, String proxyMobile, String proxyPwd) {
		
		//添加代理商
		t_proxy proxy = proxyDao.addProxy();

		if (proxy == null) {
			return false;
		}
		
		//添加业务员
		proxyPwd = Encrypt.MD5(proxyPwd + ConfConst.ENCRYPTION_KEY_MD5);//密码加密
		t_proxy_salesman salesMan = salesManDao.addSalesMan(proxyName, proxyMobile, proxyPwd, 1, 2, proxy.id);
		
		if (salesMan == null) {
			
			LoggerUtil.info(true, "添加代理商业务员角色失败！");
			return false;
		}		
		
		return true;
	}
	
	/**
	 * 添加业务员
	 * @author Niu Dongfeng
	 */
	public ResultInfo addSalesMan(long proxyId, String salesManName, String salesManMobile, String salesManPwd) {
		
		ResultInfo result = new ResultInfo();
		
		salesManPwd = Encrypt.MD5(salesManPwd + ConfConst.ENCRYPTION_KEY_MD5);//密码加密
		t_proxy_salesman salesMan = salesManDao.addSalesMan(salesManName, salesManMobile, salesManPwd, 1, 1, proxyId);
		
		if (salesMan == null) {
			result.code = -1;
			result.msg  = "添加业务员失败";
			
			return result;
		}		
		
		t_proxy proxy = proxyDao.findByID(salesMan.proxy_id);
		if (proxy == null) {
			result.code = -1;
			result.msg  = "查询代理商信息失败";
			
			return result;
		}
		
		proxy.sale_count = proxy.sale_count + 1;
		proxy = proxy.save();
		if (proxy == null) {
			result.code = -1;
			result.msg  = "业务员数量添加失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "添加业务员成功";
		
		return result;
	}
	
	/**
	 * 修改手机号
	 * @author Niu Dongfeng
	 */
	public boolean updateMobile(long salesManId, String mobile) {
		
		return salesManDao.updateMobile(salesManId, mobile);
	}
	
	/**
	 * 修改密码
	 * 
	 * @param salesManId	业务员Id
	 * @param password		业务员新密码
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public boolean updatePassWord(long salesManId, String password) {
		
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		return salesManDao.updatePassWord(salesManId, password);
	}
	
	/**
	 * 忘记密码
	 * 
	 * @param mobile
	 * @param password
	 * @return
	 */
	public boolean forgetPwd(String mobile, String password) {
		
		t_proxy_salesman salesman = salesManDao.findByColumn(" sale_mobile = ? ", mobile);
		if (salesman == null) {
			return false;
		}
		
		salesman.sale_pwd = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		t_proxy_salesman salesman2 = salesman.save();
		if (salesman2 != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 判断业务员手机是否存在
	 * @author Niu Dongfeng
	 */
	public boolean isMobileExists(String mobile) {
		
		List<t_proxy_salesman> salesManList = findListByColumn(" sale_mobile = ? ", mobile);
		if (salesManList != null && salesManList.size() > 0) {

			return true;
		}
		
		return false;
	}
	
	/**
	 * 判断业务员手机是否存在
	 * @author Niu Dongfeng
	 */
	public boolean isMobileExists2(String mobile, long id) {
		
		List<t_proxy_salesman> salesManList = findListByColumn(" sale_mobile = ? AND id <> ? ", mobile, id);
		if (salesManList != null && salesManList.size() > 0) {

			return true;
		}
		
		return false;
	}
	
	/**
	 * 修改业务员状态
	 * 
	 * @param salesManId	业务员Id
	 * @param saleStatus	业务员状态：1正常2锁定
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public ResultInfo updateStatus(long salesManId) {
		
		ResultInfo result = new ResultInfo();
		
		t_proxy_salesman salesman = salesManDao.findByID(salesManId);	
		if (salesman == null) {
			result.code = -1;
			result.msg = "查找不到代理商";
			
			return result;
		}
		
		if (salesman.sale_status == 1 && salesManDao.updateStatus(salesManId, 2)) {
			result.code = 1;
			result.msg = "锁定成功";
			result.obj = true;
			
			return result;
		}
		
		if (salesman.sale_status == 2 && salesManDao.updateStatus(salesManId, 1)) {
			result.code = 1;
			result.msg = "解锁成功";
			result.obj = false;
			
			return result;
		}
		
		result.code = 1;
		result.msg = "操作失败";
		
		return result;
	}
	
	/**
	 * 登录
	 * 
	 * @param mobile	手机号
	 * @param password	密码
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public ResultInfo logining(String mobile, String password) {
		
		ResultInfo result = new ResultInfo();
		
		t_proxy_salesman salesman = salesManDao.findByColumn(" sale_mobile = ? ", mobile);
		if (salesman == null) {
			result.code = -1;
			result.msg  = "查询不到用户";
			
			return result;
		}
		if (salesman.sale_status == 2) {
			result.code = -1;
			result.msg  = "该账户被锁定，请联系管理员";
			
			return result;
		}
		if (!salesman.sale_pwd.equals(password)) {
			result.code = -1;
			result.msg  = "密码不正确";
			
			return result;
		}
		
		if (Session.current() != null) {
			String sessionId = Session.current().getId();
			/* 设置用户凭证 */
			Cache.set(CacheKey.FRONT_ + "proxy" + sessionId, salesman.id, Constants.CACHE_TIME_MINUS_30);
		}
		
		CurrSaleMan saleMan = new CurrSaleMan();
		saleMan.id = salesman.id;
		saleMan.name = salesman.sale_name;
		saleMan.proxyId = salesman.proxy_id;
		saleMan.mobile = salesman.sale_mobile;
		saleMan.type = salesman.type;
		
		/* 刷新用户缓存信息 */
		Cache.set("proxy_" + salesman.id, saleMan, Constants.CACHE_TIME_MINUS_30);
		
		
		result.code = 1;
		result.msg = "登录成功";
		result.obj = salesman;
		
		return result;
	}
	
	/**
	 * 分页查询代理商下面的业务员
	 * 
	 * @param proxyId
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-22
	 */
	public PageBean<t_proxy_salesman> pageOfSalesManByProxyId(long proxyId, int currPage, int pageSize, String saleName, String saleMobile) {
		
		return salesManDao.pageOfSalesManByProxyId(proxyId, currPage, pageSize, saleName, saleMobile);
	}
	
	/**
	 * 
	 * @param mobile
	 * @return
	 */
	public t_proxy_salesman getSaleManByMobile(String mobile) {
		
		return findByColumn(" sale_mobile = ? ", mobile);
	}
	
	
	
	
	
	
	
	
}
