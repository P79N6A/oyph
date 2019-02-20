
package controllers.front;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_information;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import services.common.AdvertisementService;
import services.common.InformationService;
import common.constants.Constants;
import common.enums.InformationMenu;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;

/**
 * 前台-资讯控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class InformationCtrl extends FrontBaseController {
	
	/** 注入资讯管理services  */
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);
	
	
	/**
	 * 展示前几条资讯
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void showInfosPre(String columnkey,int currPage,int pageSize){
		if(pageSize < 1){
			pageSize = 3;
		}
		
		/* 搜索条件 */
		String keywords = params.get("keywords");
		
		List<String> list = new ArrayList<String>();
		if(StringUtils.isNotBlank(columnkey) && (columnkey.equalsIgnoreCase("info_bulletin") || columnkey.equalsIgnoreCase("info_report")  || columnkey.equalsIgnoreCase("info_story"))) {
				list.add(columnkey);
		}else{
			list = InformationMenu.INFORMATION_FRONT;
		}
		PageBean<t_information> page = informationService.pageOfInformationFront(list, currPage, pageSize, keywords);
		render(page, columnkey, keywords);
	}
	
	/**
	 * 资讯消息， 点击查看更多
	 *
	 * @param val 
	 * @param scale
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月30日
	 */
	public static void loadMoreInfosPre(String columnkey,int currPage,int pageSize){
		if(pageSize < 1){
			pageSize = 3;
		}
		
		/* 搜索条件 */
		String keywords = params.get("keywords");
		
		List<String> list = new ArrayList<String>();
		if(StringUtils.isNotBlank(columnkey) && (columnkey.equalsIgnoreCase("info_bulletin") || columnkey.equalsIgnoreCase("info_report")  || columnkey.equalsIgnoreCase("info_story"))) {
				list.add(columnkey);
		}else{
			list = InformationMenu.INFORMATION_FRONT;
		}
		PageBean<t_information> page = informationService.pageOfInformationFront(list, currPage, pageSize, keywords);
		render(page, columnkey, keywords);
	}
	
	/**
	 * 点赞 
	 *
	 * @param id 资讯消息id 
	 * @param scale
	 * @return
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void supportCountPre(long id){
		ResultInfo result = new ResultInfo();
		
		boolean supportFlag = informationService.addInformationSupportCount(id);
		if(!supportFlag){
			result.code=-1;
			result.msg="点赞失败";
			
			renderJSON(result);
		}
		
		String readCount = informationService.findReadCountById(id);

		result.code=1;
		result.obj=readCount;
		
		renderJSON(result);
	}
	
	/**
	 * 查看消息详情
	 *
	 * @param id 资讯消息id 
	 * @param scale
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void informationDetailPre(long id){
		//阅读次数增加
		informationService.addInformationReadCount(id);
		
		//查询资讯详情
		t_information information = informationService.findInformationByIdFront(id);
		if(information == null){
			error404();
		}
		renderArgs.put("information", information);
		
		//查询上一条 下一条
		Map<String, Object> map = informationService.queryInformationUpAndNext(id);
		renderArgs.put("map", map);
		
		//资讯右边轮播广告图片
		List<t_advertisement> advertisements = advertisementService.queryAdvertisementFront(Location.INFOR_TURN_ADS, 5);
		renderArgs.put("banners",advertisements);
		
		//资讯右边最新的五条记录 (官方公告，媒体报道，理财故事 )前5篇
		List<t_information> informations = informationService.queryInformationsFront(InformationMenu.INFORMATION_FRONT, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("informations", informations);

		
		render();
	}
	
}
