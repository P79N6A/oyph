package controllers.back.webOperation;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import models.common.entity.t_event;
import common.utils.Factory;
import common.utils.OSSUploadUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import play.db.jpa.JPA;
import play.mvc.With;
import services.common.DevelopEventService;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import daos.common.DevelopEventDao;

/**
 * 后台-运维-发展历程
 * @author Administrator
 *
 */
@With(SubmitRepeat.class)
public class DevelopmentMngCtrl extends BackBaseController{
	
	/** 注入理财顾问services  */
	protected static DevelopEventService deveService = Factory.getService(DevelopEventService.class);
	protected static DevelopEventDao deDao=Factory.getDao(DevelopEventDao.class);
	
	/**
	 * 分页显示所有大事件列表
	 * @param currPage
	 * @param pageSize
	 */
	public static void showEventPre(int currPage, int pageSize){
		PageBean<t_event> page =deveService.pageOfDevelopEvent(currPage,pageSize);
		
		render(page);
	}
	
	/**
	 * 去添加页面
	 * 
	 */
	public static void toAddEventPre(){
		Date nowTime=new Date();
		int currYear=nowTime.getYear()-100;
		render(currYear);
	}
	
	/**
	 * 添加的表单提交执行
	 */
	public static void addEvent(t_event deve){
		String url = params.get("image_url");
		String imageFormat = params.get("imageFormat");
		if(!StringUtils.isBlank(url)){
			deve.image_url = url;
			deve.image_format = imageFormat;
		}
		deve.create_time=new Date();
		deve.save();
		flash.success("添加成功");
		showEventPre(1,10);
	}
	/**
	 * 根据历程id删除历程
	 * @param id
	 */
	public static void delEvent(long id){
		ResultInfo result=new ResultInfo();
		
		t_event deve=deDao.findByID(id);
		
		if(deve==null){
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}
		
		boolean deleteUrl = OSSUploadUtil.deleteFile(deve.image_url, deve.image_format);
		
		if(!deleteUrl) {
			result.code=-1;
			result.msg="删除失败";
			renderJSON(result);
		}
		
		deve.delete();
		result.code=1;
		result.msg="删除成功";
		renderJSON(result);
	}
	/**
	 * 去修改历程
	 */
	public static void toEditEventPre(long id){
		t_event deve=deDao.findByID(id);
		Date nowTime=new Date();
		int currYear=nowTime.getYear()+1900;
		render(deve,currYear);
	}
	/**
	 * 执行修改
	 */
	public static void editEvent(t_event deve, String image_url, String imageFormat){
		if(!StringUtils.isBlank(image_url)){
			deve.image_url = image_url;
			deve.image_format = imageFormat;
		}
		deve.create_time=new Date();
		deveService.editEvent(deve);
		flash.success("修改成功");
		showEventPre(1, 10);
	}
}
