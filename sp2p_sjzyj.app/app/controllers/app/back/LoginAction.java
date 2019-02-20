package controllers.app.back;

import java.util.List;
import java.util.Map;

import play.cache.Cache;
import play.mvc.Scope.Session;
import services.common.SupervisorService;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import controllers.common.BackBaseController;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_supervisor;
import net.sf.json.JSONObject;

/**
 * 后台登录
 * LoginAction
 * @author lihuijun
 * @createDate 2017年5月11日
 */
public class LoginAction extends BackBaseController{
	
	/**
	 * 后台登录 
	 * @param parameters
	 * @return
	 * @createDate 2017年5月11日
	 * @author lihuijun
	 */
	public static String backLogin(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String supervisor_name=parameters.get("supervisor_name");
		String password=parameters.get("password");
		password = com.shove.security.Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		t_supervisor supervisor = supervisorService.findByColumn("name=? and password=?", supervisor_name, password);
		
		if(supervisor!=null){
			//添加管理员事件
			supervisorService.addSupervisorEvent(supervisor.id, t_event_supervisor.Event.LOGIN, null);
			
			List<Long> rights = rightService.queryRightsOfSupervisor(supervisor.id);
			CurrSupervisor currSupervisor = new CurrSupervisor();
			currSupervisor.id = supervisor.id;
			currSupervisor.name = supervisor.name;
			currSupervisor.reality_name = supervisor.reality_name;
			currSupervisor.rights = rights;
			currSupervisor.setLock_status(supervisor.getLock_status());
			//管理员id缓存
			Cache.safeSet(CacheKey.LOGIN_SUPERVISOR_ID+Session.current().getId(), supervisor.id, Constants.CACHE_TIME_MINUS_30);
			//管理员Bean缓存
			Cache.safeSet(CacheKey.LOGIN_SUPERVISOR + currSupervisor.id, currSupervisor, Constants.CACHE_TIME_MINUS_30);
			json.put("showType", parameters.get("showType"));
			json.put("currSupervisor", currSupervisor);
			
			json.put("code", 1);
			json.put("msg", "登录成功!");
		}else{
			json.put("code", -1);
			json.put("msg", "用户名或密码错误!");
		}
		return json.toString();
	}
}
