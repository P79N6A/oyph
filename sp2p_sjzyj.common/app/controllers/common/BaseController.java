package controllers.common;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import services.common.SettingService;

import com.shove.web.security.InjectionInterceptor;
import common.constants.ConfConst;
import common.utils.Factory;

/**
 * 系统基本控制器(拦截器优先级参数：1~5)
 *
 * @description 功能描述：
 * <br>注入系统设置服务
 * <br>防跨站脚本攻击拦截器
 * <br>用户登陆标志更新拦截器
 * <br>错误页面处理
 * <br>获取当前请求根路径的工具方法
 * <br>获取当前请求IP 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class BaseController extends Controller {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 脚本注入拦截器
	 * 
	 *
	 * @throws Exception
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月3日
	 */
	@Before(unless={
			"back.webOperation.InformationMngCtrl.editInformation",//编辑内容
			"back.webOperation.InformationMngCtrl.addInformation",////添加内容
			"back.webOperation.HelpMngCtrl.editHelp",//编辑帮助
			"back.webOperation.HelpMngCtrl.addHelp",//添加帮助
			"back.webOperation.NoticeMngCtrl.editTemplateNotice",//编辑通知模板
			"back.UserMngCtrl.sendEmail",//发送邮件给用户
			"back.UserMngCtrl.massEmail",//批量发送邮件
			"back.UserMngCtrl.sendBatchEmail",//群发邮件
			"back.risk.PactMngCtrl.editPactTemp",//更新合同模板
			"back.setting.PlateformSettingCtrl.editSEO",//更新SEO(百度统计代码)
			"back.mall.GoodsCtrl.saveGoods",//积分商城、新增商品-商品介绍上传图片
			"back.webOperation.VoteMngCtrl.addVote",
			"back.webOperation.VoteMngCtrl.editVote",
			"back.webOperation.UpdateContractCtrl.editContract",
			"back.risk.SuperAntiCtrl.receiveInfo"
		},priority=1)
	protected static void injectionInterceptor() throws Exception {
		try {
			new InjectionInterceptor().run();
		} catch (Exception e) {
			response.status = 401;
			renderText(e.getMessage());
		}
	}
	
	/**
	 * 更新系统参数
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月29日
	 */
	@Before(priority=2)
	protected static void setting() {
		
		/** 更新系统参数 */
		Map<String, String> settings = settingService.queryAllSettings();
		if(settings == null){
			error();
		}
		renderArgs.put("settings", settings);
		
		Request request = Request.current.get();
		Logger.debug("正在执行ation请求命令："+request.action);
	}
	
	
	
	/**
	 * 跳转到公共的404页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	protected static void error404(){

		renderTemplate("common/errors/http-404.html");	
	}
	
	/**
	 * 跳转到公共的500页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	protected static void error500(){

		renderTemplate("common/errors/http-500.html");	
	}
	
	/**
	 * 跳转到公共的error页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	protected static void error(){

		renderTemplate("common/errors/http-error.html");	
	}

	/**
	 * 获取当前请求根路径
	 * 
	 * @return
	 */
	public static String getBaseURL() {
		Request req = Request.current();
		if (req != null) {
			return req.getBase() + ConfConst.HTTP_PATH + "/";
		}
		return ConfConst.APPLICATION_BASEURL;
	}
	
	/**
	 * 获取当前请求IP 
	 * 
	 * @return
	 */
	public static String getIp() {
		Request request = Request.current();
		
		if(null == request) {
			return "127.0.0.1";
		}
		
		return StringUtils.isBlank(request.remoteAddress.toString()) ? "127.0.0.1" : request.remoteAddress.toString();
	}
	
	/**
	 * 获取cookie值
	 *
	 * @param key
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年5月12日
	 */
	public static String getCookie(String key) {
		Http.Cookie cookie = Http.Request.current().cookies.get(key);
		
		if (cookie != null && Play.started && cookie.value != null && !cookie.value.trim().equals("")) {
			
			return cookie.value;
		}
		
		return "";
	}
}
