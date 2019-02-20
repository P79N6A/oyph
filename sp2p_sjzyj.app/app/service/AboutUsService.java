package service;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import models.common.entity.t_information;
import net.sf.json.JSONObject;
import common.constants.SettingKey;
import common.enums.InformationMenu;
import common.utils.Factory;

import dao.AboutUsAppDao;
import dao.AccountAppDao;
import services.common.InformationService;
import services.common.SettingService;

public class AboutUsService extends InformationService {

	private static SettingService settingService = Factory.getService(SettingService.class);
	
	private AboutUsAppDao aboutUsDao;
	private AboutUsService() {
		aboutUsDao = Factory.getDao(AboutUsAppDao.class);
		super.basedao = aboutUsDao;
	}
	
	/***
	 * 
	 * 关于我们（opt=411）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public String findAboutUs(){
		JSONObject json = new JSONObject();
		Map<String, Object> dataMap = aboutUsDao.findAboutUs();
		
		json.put("imgUrl", dataMap.get("imgUrl"));
		json.put("html", dataMap.get("content"));
		json.put("code", 1);
		json.put("msg", "查询成功");
		return json.toString();
	}
	
	/***
	 * 
	 * 联系我们（opt=421）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public String findContactUs() {
		JSONObject json = new JSONObject();
	
		Map<String, String> settings = settingService.queryAllSettings();
	
		if(settings == null){
			json.put("code", -1);
			json.put("msg", "查询失败");
			
			return json.toString();
		}
		
		json.put("companyName",settings.get(SettingKey.COMPANY_NAME));
		json.put("companyTel", settings.get(SettingKey.COMPANY_TEL));
		json.put("companyEmail", settings.get(SettingKey.COMPANY_EMAIL));
		json.put("companyAddress", settings.get(SettingKey.COMPANY_ADDRESS));
		json.put("code", 1);
		json.put("msg", "查询成功");
		
		return json.toString();		
	}
	
	/***
	 * 
	 * 平台注册协议（OPT=112）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public String findRegisterProtocol() {
		JSONObject json = new JSONObject();

		t_information registerDeal = super.findRegisterDeal();
		if (registerDeal == null) {
			json.put("code", -1);
			json.put("msg", "注册协议加载失败");

			return json.toString();
		}

		json.put("code", 1);
		json.put("html", registerDeal.content);
		json.put("htmlTitle", registerDeal.title);
		json.put("msg", "注册协议加载成功");

		return json.toString();
	}
	
}
