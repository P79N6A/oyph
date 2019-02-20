package service;

import java.util.HashMap;
import java.util.Map;

import common.enums.InformationMenu;
import common.utils.Factory;
import common.utils.PageBean;
import dao.DebtTransferAppDao;
import dao.InformationAppDao;
import models.app.bean.InformationBean;
import net.sf.json.JSONObject;
import services.common.InformationService;

/**
 * 资讯模块Service
 * 
 * @author guoshijie
 * @createdate 2017.12.15
 * */
public class InformationAppService extends InformationService{
	
	protected InformationAppDao informationAppDao = null;
	
	protected InformationAppService() {
		informationAppDao = Factory.getDao(InformationAppDao.class);
	}
	
	/**
	 * 分页查询资讯列表
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param type 资讯类型(公告 / 媒体 / 故事)
	 * @author guoshijie
	 * @createdate 2017.12.15
	 * */
	public String pageOfInformations(String type , int currPage , int pageSize) {
		
		InformationMenu informationMenu = null;
		
		if ("info_bulletin".equals(type)) {
			
			informationMenu = InformationMenu.getEnum(type);
		} else if ("info_report".equals(type)) {
			
			informationMenu = InformationMenu.getEnum(type);
		} else if ("info_story".equals(type)){
			
			informationMenu = InformationMenu.getEnum(type);
		}
		
		PageBean<InformationBean> page = informationAppDao.queryInformation(informationMenu, currPage, pageSize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("informations", page.page);
		
		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 查询资讯详情
	 * 
	 * @param */
	public String findDetailById(long id) {
		InformationBean information = informationAppDao.findDetailById(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("information", information);
		
		return JSONObject.fromObject(map).toString();
	}
}
