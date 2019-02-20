package controllers.common.interceptor;

import com.shove.Convert;

import common.annotation.ProxyLoginCheck;
import common.constants.CacheKey;
import common.utils.ResultInfo;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Session;

public class ProxyInterceptor extends Controller {

	/**
	 * 登陆拦截器
	 */
	@Before(priority=11)
	static void checkProxyLogin(){
		ProxyLoginCheck checkAction = getActionAnnotation(ProxyLoginCheck.class);
		if (checkAction == null) {
			
			return;
		}
		
		//Cache.set(CacheKey.FRONT_ + "proxy" + sessionId, salesman.id, Constants.CACHE_TIME_MINUS_30);
		
		Object object = Cache.get(CacheKey.FRONT_ + "proxy" + Session.current().getId());
		
		//System.out.println(object);
		
		if (object == null) {
			redirect("front.proxy.LoginCtrl.loginPre");
		}
		
		/*String proxyId = object.toString();
		long proxySalesManId = Convert.strToLong(proxyId, 0);
		
		if (proxySalesManId == 0) {
			redirect("front.proxy.SalesManCtrl.loginPre");
		}*/
		/*CurrUser user = FrontBaseController.getCurrUser();
		if (user != null) {
			
			return;
		}*/

		/*boolean isAjax = request.isAjax();
		if(isAjax){
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_LOGIN;
			result.msg = "没有登录，或者登录状态已经失效!请重新登录!";
			
			renderJSON(result);
			
		} else{
			
			redirect("front.proxy.SalesManCtrl.loginPre");
		}*/
	}
}
