package controllers.back.webOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.LeftBarMenu;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_information;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import play.cache.Cache;
import play.mvc.With;
import push.AndroidNotification;
import push.IOSBroadcast;
import push.PushDemo;
import push.AndroidBroadcast;
import services.common.ColumnService;
import services.common.InformationService;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ColumnKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.InformationMenu;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OSSUploadUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;

import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-运维-资讯管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(SubmitRepeat.class)
public class InformationMngCtrl extends BackBaseController {
	
	protected static String appkey = null;
	
	protected static String appMasterSecret = null;

	/** 注入资讯管理services  */
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	/** 注入栏目管理services  */
	protected static ColumnService columnService = Factory.getService(ColumnService.class);

	
	/**
	 * 左侧菜单部分
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月4日
	 */
	public static List<LeftBarMenu> showLeftBarMenu(){
		List<LeftBarMenu> leftBar  = new ArrayList<LeftBarMenu>();
		
		/** 公司介绍  */
		leftBar.add(new LeftBarMenu(InformationMenu.HOME_PROFILE.code, columnService.findColumnBackNameByKey(ColumnKey.HOME_PROFILE)));
				
		/** 加入我们  */
		leftBar.add(new LeftBarMenu(InformationMenu.HOME_JOINUS.code, columnService.findColumnBackNameByKey(ColumnKey.HOME_JOINUS)));
		
		/** 官方公告  */
		leftBar.add(new LeftBarMenu(InformationMenu.INFO_BULLETIN.code, columnService.findColumnBackNameByKey(ColumnKey.INFO_BULLETIN)));
		
		/** 媒体报道  */
		leftBar.add(new LeftBarMenu(InformationMenu.INFO_REPORT.code, columnService.findColumnBackNameByKey(ColumnKey.INFO_REPORT)));
		
		/**  理财故事*/
		leftBar.add(new LeftBarMenu(InformationMenu.INFO_STORY.code, columnService.findColumnBackNameByKey(ColumnKey.INFO_STORY)));
	
		/** 平台注册协议  */
		leftBar.add(new LeftBarMenu(InformationMenu.PLATFORM_REGISTER.code, InformationMenu.PLATFORM_REGISTER.value));
		
		/** 投资协议模板  */
		leftBar.add(new LeftBarMenu(InformationMenu.INVEST_AGREEMENT_TEMPLATE.code, InformationMenu.INVEST_AGREEMENT_TEMPLATE.value));
		
		/** 委托投资协议*/
		leftBar.add(new LeftBarMenu(InformationMenu.INVEST_ENTRUEST_AGREEMENT.code,InformationMenu.INVEST_ENTRUEST_AGREEMENT.value));
		
		/** 借款协议 */
		leftBar.add(new LeftBarMenu(InformationMenu.LOAN_AGREEMENT.code, InformationMenu.LOAN_AGREEMENT.value));
		
		//商城指引
		leftBar.add(new LeftBarMenu(InformationMenu.MALL_GUIDE.code, InformationMenu.MALL_GUIDE.value));
		
		//常见问题
		leftBar.add(new LeftBarMenu(InformationMenu.COMMON_PROBLEM.code, InformationMenu.COMMON_PROBLEM.value));
		
		//平台优势
		leftBar.add(new LeftBarMenu(InformationMenu.PLATFORM_ADVANTAGE.code, InformationMenu.PLATFORM_ADVANTAGE.value));
		
		//风险教育
		leftBar.add(new LeftBarMenu(InformationMenu.RISK_EDUCATION.code, InformationMenu.RISK_EDUCATION.value));
		
		
		return leftBar;

	}
	
	/**
	 * 分页查询 资讯     
	 * @rightID 202001
	 * 
	 * @param showType 值为空时，查询所有，为具体值时，查询具体栏目的资讯消息
	 * @param inforTitle 资讯标题
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public static void showInformationsPre(String showType,int currPage,int pageSize){
		/* 搜索条件 */
		String inforTitle = params.get("inforTitle");
		
		//获取左侧菜单 
		List<LeftBarMenu> leftBar = showLeftBarMenu();
			
		PageBean<t_information> page = informationService.pageOfInformationBack(InformationMenu.getEnum(showType), currPage, pageSize,inforTitle);
		render(page,leftBar,showType,inforTitle);
	}
	
	/**
	 * 进入编辑页面
	 * @rightID 202003
	 *
	 * @param id 资讯id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void toEditInformationPre(long id){
		t_information information = informationService.findByID(id);
		
		if(information == null){
			error404();
		}
		
		List<LeftBarMenu> barMenu = showLeftBarMenu();
		
		render(information,barMenu);
	}
	
	/**
	 * 资讯编辑 确认 
	 * @rightID 202003
	 *
	 * @param sign 资讯加密id 
	 * @param columnKey 栏目值
	 * @param title 标题
	 * @param sourceFrom 来源 
	 * @param content 内容
	 * @param orderTime 排序时间 
	 * @param readCount 查看次数
	 * @param supportCount 点赞次数
	 * @param imageUrl 图片路径 
	 * @param imageResolution 图片分辨率
	 * @param imageSize 文件大小 
	 * @param imageFormat 文件格式 
	 * @param showTime  发布时间 
	 * @param isUse 0-下架  1-上架
	 * @return 
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public static void editInformation(String sign ,t_information information, String orderTime, String showTime){
		ResultInfo result = new ResultInfo();
		//解密sign
		result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long informationId = Long.parseLong((String) result.obj);
		
		//参数校验
		result = isCheckInformation(information, orderTime, showTime);
		if(result.code < 1){
			flash.error(result.msg);
			
			toEditInformationPre(informationId);
		}
		
		boolean editFlag = informationService.updateInformation(informationId, InformationMenu.getEnum(information.column_key),
				information.title, information.source_from, information.keywords, information.content,
				DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN),
				information.image_url, information.image_resolution, information.image_size, information.image_format,
				DateUtil.strToDate(showTime, Constants.DATE_PLUGIN));
		if(!editFlag){
			/** 编辑失败 ,回到编辑页面 */
			toEditInformationPre(informationId);
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("information_id", informationId+"");  
		supervisorEventParam.put("information_name", information.title);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_EDIT, supervisorEventParam);
		
		flash.success("编辑内容成功！");
		showInformationsPre(null, 1, 10);
	}
	
	/**
	 * 资讯 上下架
	 * @rightID 202004
	 *
	 * @param sign 资讯加密id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void isUseInformation(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		long informationId = Long.parseLong((String) result.obj);
		t_information information = informationService.findByID(informationId);
		if(information==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}
		boolean isUseFlag = informationService.updateInformationisUse(informationId, !information.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
	    }else{
	    	//添加管理员事件
	    	long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("information_id", information.id.toString());  
			supervisorEventParam.put("information_name", information.title);
			
			//下架事件
			if(information.getIs_use().code){
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
			//上架事件
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);				
			}
	    }
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=information.getIs_use().code;
			
		renderJSON(result);
	}
	
	/**
	 * 资讯 删除
	 * @rightID 202005
	 *
	 * @param sign 资讯加密id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delInformation(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long informationId = Long.parseLong((String) result.obj);
		t_information information = informationService.findByID(informationId);
		String image_url = information.image_url;
		String image_format = information.image_format;
		//删除图片默认失败
		boolean delUrl = false;
		//删除信息默认失败
		boolean delFlag = false;
		if ("".equals(image_url) && "".equals(image_format)) {
			delFlag = informationService.delInformation(informationId);
			if (!delFlag) {
				result.code=-1;
				result.msg="删除失败";
				
				renderJSON(result);
			}	
		}else{
			delUrl = OSSUploadUtil.deleteFile(image_url, image_format);
			delFlag = informationService.delInformation(informationId);
			
			if(!delFlag || !delUrl){
				result.code=-1;
				result.msg="删除失败";
				
				renderJSON(result);
			}
		}
		//管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("information_id", information.id.toString());  
		supervisorEventParam.put("information_name", information.title);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_REMOVE, supervisorEventParam);
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	
	/**
	 * 进入 添加资讯消息页面
	 *  @rightID 202002
	 *
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年1月5日
	 */
	@SubmitCheck
	public static void toAddInformationPre(){
		List<LeftBarMenu> barMenu = showLeftBarMenu();
		
		//回显数据用到的处理方式
		t_information information = (t_information) Cache.get(CacheKey.INFORMATION_+session.getId());
		if (information != null) {
			renderArgs.put("information", information);
			Cache.delete(CacheKey.INFORMATION_+session.getId());
		}
		
		render(barMenu);
	}
	
	/**
	 * 添加  资讯消息
	 * @rightID 202002
	 * 
	 * @param information 资讯
	 * @param orderTime 排序时间
	 * @param showTime 显示时间
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	@SubmitOnly
	public static void addInformation(t_information information, String orderTime, String showTime){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		//参数校验
		result = isCheckInformation(information, orderTime, showTime);
		if(result.code < 1){
			flash.error(result.msg);
			
			addCacheFlash(information, orderTime, showTime);
			toAddInformationPre();
		}
		
		information.order_time = DateUtil.strToDate(orderTime,  Constants.DATE_PLUGIN);
		information.show_time =  DateUtil.strToDate(showTime,  Constants.DATE_PLUGIN);
		
		boolean addFlag = informationService.addInformation(information);
		if(!addFlag){
			flash.error("添加失败");
			
			addCacheFlash(information, orderTime, showTime);
			toAddInformationPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("information_id", information.id.toString());  
		supervisorEventParam.put("information_name", information.title);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ADD, supervisorEventParam);

		flash.success("添加内容成功！");
		showInformationsPre(null, 1, 10);
	}
	
	/**
	 * 校验参数是否合法
	 * 
	 * @param information 内容
	 * @param orderTime 排序时间
	 * @param showTime 显示时间
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年3月21日
	 */
	public static ResultInfo isCheckInformation(t_information information, String orderTime, String showTime){
		ResultInfo result = new ResultInfo();
		
		if(InformationMenu.getEnum(information.column_key) == null){
			result.code=-1;
			result.msg="栏目没有设置";
			
			return result;
		}
		if(StringUtils.isBlank(information.title)){
			result.code=-1;
			result.msg="标题不能为空";
			
			return result;
		}
		if(StringUtils.isBlank(information.source_from)){
			result.code=-1;
			result.msg="资讯来源不能为空";
			
			return result;
		}
		
		if(StringUtils.isBlank(information.content)){
			result.code=-1;
			result.msg="资讯内容不能为空";
			
			return result;
		}
		if(StringUtils.isBlank(orderTime)){
			result.code=-1;
			result.msg="排序时间不能为空";
			
			return result;
		}
		
		if(StringUtils.isBlank(showTime)){
			result.code=-1;
			result.msg="发布时间不能为空";
			
			return result;
		}
		
		result.code=1;
		result.msg="参数合法";
		
		return result;
	}
	
	/**
	 * 将内容添加到 flash中
	 * 
	 * @param information 内容
	 * @param orderTime 排序时间
	 * @param showTime 显示时间
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年3月21日
	 */
	private static void addCacheFlash(t_information information, String orderTime, String showTime){
		Cache.set(CacheKey.INFORMATION_+session.getId(), information, Constants.CACHE_TIME_SECOND_60);
		flash.put("orderTime", orderTime);
		flash.put("showTime", showTime);
	}
	
	/**
	 * 上传内容管理图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadInformationImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
	
	/**
	 * 资讯模块--消息推送
	 * @rightID 202006
	 * @param sign 资讯加密id
	 * @return
	 * @author guoshijie
	 * @createdate 2017.12.11
	 * */
	public static void InformationPush (String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		ResultInfo res = new ResultInfo();
		long informationId = Long.parseLong((String) result.obj);
		t_information information = informationService.findByID(informationId);
		int androidResult = PushDemo.androidBroadcastPush("58b3c1368f4a9d01ae001623", "93g9etyokqjwhlgonwkfxmevrjk2wxb3", information.title, PushDemo.getTextFromHTML(information.content) , "url", "https://ouyepuhui.com/information/informationdetail.html?id="+information.id);
		int iosResult = PushDemo.iosBroadcastPush("58c752381061d264c4001ad2", "gehsqjgokosr48rvxkrkzjycpq9axofs", information.title, PushDemo.getTextFromHTML(information.content) , "url", "https://ouyepuhui.com/information/informationdetail.html?id="+information.id);
		if (androidResult == 1 && iosResult == 1) {
			
			res.msg = "发送成功！";
		} else if (androidResult != 1 && iosResult != 1) {
			
			res.msg = "发送失败！";
		} else {
			
			if(androidResult != 1) {
				
				res.msg = "安卓发送失败！";
			} else {
				if(iosResult != 1) {
					
					res.msg = "IOS发送失败！";
				}
			}
		}

		renderJSON(res);
	}
}
