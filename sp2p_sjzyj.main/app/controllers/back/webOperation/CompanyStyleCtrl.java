package controllers.back.webOperation;

import java.io.File;
import java.util.Map;

import models.common.entity.t_company_style;

import org.apache.commons.lang.StringUtils;

import common.annotation.SubmitOnly;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.file.FileUtil;
import play.mvc.With;
import services.common.CompanyStyleService;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-公司风采管理
 * @author liuyang
 *
 */
@With(SubmitRepeat.class)
public class CompanyStyleCtrl extends BackBaseController {
	
	protected static CompanyStyleService companyStyleService = Factory.getService(CompanyStyleService.class);

	/**
	 * 分公司列表查询
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	
	 * @author liuyang
	 * @createDate 2017年4月21日
	 */
	public static void showCompanyPre(int currPage,int pageSize) {
		
		PageBean<t_company_style> page = companyStyleService.pageOfCompanyStyleBack(currPage, pageSize);
		
		render(page);
	}
	
	/**
	 * 进入添加分公司页
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年4月21日
	 */
	public static void toAddCompanyStylePre() {
		
		render();
	}
	
	/**
	 * 保存分公司
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年4月21日
	 */
	public static void addCompanyStyle() {
		t_company_style companyStyle = new t_company_style();
		
		/** 企业风貌图 */
        String filename = params.get("image_url");
        if(StringUtils.isBlank(filename)){
        	flash.error("企业风貌图片不能为空");
        	toAddCompanyStylePre();
        }else {
        	companyStyle.enter_pic = filename;
        	flash.put("image_url", filename);
        }
        
        /** 员工风采图 */
        String filenames = params.get("image_urls");
        if(StringUtils.isBlank(filenames)){
        	flash.error("员工风采图片不能为空");
        	toAddCompanyStylePre();
        }else {
        	companyStyle.employee_pic = filenames;
        	flash.put("image_urls", filenames);
        }
        
        String cityName = params.get("cityName");
        companyStyle.city_name = cityName;
        
        String orderTime = params.get("order_time");
        companyStyle.create_time = DateUtil.strToDate(orderTime);
        
        companyStyle.save();
        flash.success("添加成功");
		showCompanyPre(1,10);
	}
	
	/**
	 *  
	 * @Description: 后台-维护-公司上下架
	 * @param houseId
	 * @param flag
	 * void
	 * @author liuyang
	 * 2017年4月5日
	 */
	public static void changeStatus(long sign,String flag) {

		 t_company_style companyStyle=companyStyleService.findByID(sign);
		 ResultInfo result = new ResultInfo();
		 if(flag.equals("上架")){
			 companyStyle.status = 1;
		 }else if(flag.equals("下架")){
			 companyStyle.status = 0;
		 }
		 companyStyle.save();
		 result.code=1;
		 result.msg="上下架操作成功";
		 result.obj=companyStyle.status;
		 renderJSON(result); 
	}
	
	/**
	 * 进入公司图片编辑 页
	 *
	 * @param id 
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年4月22日
	 */
	public static void toEditCompanyStylePre(long id) {
		t_company_style companyStyle=companyStyleService.findByID(id);
		if(companyStyle==null){
			error404();
		}
		render(companyStyle);
	}
	
	/**
	 * 保存公司图片编辑 页
	 *
	 * @param ids 
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年4月22日
	 */
	public static void editCompanyStyle(long ids) {
		t_company_style companyStyle=companyStyleService.findByID(ids);
		if(companyStyle == null) {
			flash.error("编辑失败，请重新操作");
			showCompanyPre(1,10);
		}
		/** 企业风貌图 */
        String filename = params.get("image_url");
        if(StringUtils.isBlank(filename)){
        	flash.error("企业风貌图片不能为空");
        	toEditCompanyStylePre(ids);
        }else {
        	companyStyle.enter_pic = filename;
        }
        
        /** 员工风采图 */
        String filenames = params.get("image_urls");
        if(StringUtils.isBlank(filenames)){
        	flash.error("员工风采图片不能为空");
        	toEditCompanyStylePre(ids);
        }else {
        	companyStyle.employee_pic = filenames;
        }
        
        String cityName = params.get("cityName");
        companyStyle.city_name = cityName;
        
        String orderTime = params.get("order_time");
        companyStyle.create_time = DateUtil.strToDate(orderTime);
        
        companyStyle.save();
        flash.success("编辑成功");
		showCompanyPre(1,10);
	}
	
}
