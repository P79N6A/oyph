package controllers.back.risk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.groovy.control.StaticImportVisitor;

import com.thoughtworks.xstream.io.naming.NameCoderWrapper;

import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.common.entity.t_credit;
import services.common.CreditService;
/**
 * 
 *
 * @ClassName: CreditCtrl
 *
 * @description 运维分级管理
 *
 * @author LiuHangjing
 * @createDate 2018年12月18日-下午2:29:28
 */
public class CreditCtrl extends BackBaseController{
	
	protected static CreditService creditService = Factory.getService(CreditService.class);
	/**
	 * 
	 * @Title: creditManagePre
	 * 
	 * @description 进入分级管理页面(分页列表显示)
	 * @param  currPage
	 * @param  pageSize
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:42:22
	 */
	public static void showCreditManagePre(int currPage,int pageSize){
		
		PageBean<t_credit> page = creditService.findPageOfCredit(currPage, pageSize);
		
		render(page);
	}
	/**
	 * 
	 * @Title: toAddCreditManagePre
	 * 
	 * @description 
	 * @param 进入添加分级管理页面
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:44:09
	 */
	public static void toAddCreditManagePre(){
		render();
	}
	/**
	 * 
	 * @Title: addCreditManage
	 * 
	 * @description 添加一个分级管理 
	 * @param credit
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年12月18日-下午2:46:40
	 */
	public static void addCreditManage(t_credit credit) throws ParseException{
		
		/** 获取类型*/
		String types = params.get("type");
		int type = Integer.parseInt(types);
		if (type == 1) {
			credit.type = "保守型";
		}else if (type == 2) {
			credit.type = "稳健型";
		}else if (type == 3) {
			credit.type = "平衡型";
		}else if (type == 4) {
			credit.type = "成长型";
		}else if (type == 5) {
			credit.type = "进取型";
		}
		/** 出解标的种类*/
		String invest_type = params.get("invest_type");
		int invest_types = Integer.parseInt(invest_type);
		if (invest_types == 1) {
			credit.invest_type = "全部";
		}
		Date date = new Date();
		credit.time = date;
		credit.save();
		
		flash.success("添加成功");
		showCreditManagePre(1,10);
	}
	/**
	 * 
	 * @Title: toEditCreditManagePre
	 * 
	 * @description 进入编辑分级管理页面 
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:48:19
	 */
	public static void toEditCreditManagePre(long id){
		t_credit credit = creditService.findByID(id);
		
		if (credit==null) {
			error404();
		}
		render(credit);
	}
	/**
	 * 
	 * @Title: editCreditManage
	 * 
	 * @description 修改一条分级管理 
	 * @param credit
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午2:51:08
	 */
	public static void editCreditManage(t_credit credit){
		
	
		Date date = new Date();
		credit.time = date;
		credit.save();
		flash.success("编辑成功");
		showCreditManagePre(1,10);
	}
}
