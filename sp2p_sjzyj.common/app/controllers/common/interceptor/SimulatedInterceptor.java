package controllers.common.interceptor;

import models.common.bean.CurrUser;

import org.apache.commons.lang.StringUtils;

import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.constants.CacheKey;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Session;

/**
 * 模拟登录功能拦截器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年5月30日
 */
public class SimulatedInterceptor extends Controller {

	/**
	 * 模拟登录拦截
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月30日
	 */
	@Before(priority=11)
	static void checkSimulated(){
		SimulatedCheck checkSimulated = getActionAnnotation(SimulatedCheck.class);
		
		/* 不拦截 */
		if(checkSimulated == null){ 
			
			return;
		}
		
		String sessionId = Session.current().getId();
		Object isSimulated = Cache.get(CacheKey.SIMULATED_ + sessionId);	
		if (isSimulated == null) {

			return;
		}
		if (!(Boolean) isSimulated) {
			return;
		}

		
		boolean isAjax = request.isAjax();
		
		if(isAjax){//上传操作不是ajax。但是要求返回一个json
			
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.SIMULATED_LOGIN;
			result.msg = "模拟登录不能执行该操作";
			
			renderJSON(result);
		} else{
			redirect("front.account.MyAccountCtrl.simulatedLoginPre");
		}
	}
	
}
