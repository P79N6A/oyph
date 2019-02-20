package controllers.back.mall;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import models.beans.MallScroeRecord;
import models.common.entity.t_user;
import models.entity.t_mall_scroe_record;
import models.entity.t_mall_virtual_goods_records;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;


import services.common.NoticeService;
import services.common.UserInfoService;
import services.common.UserService;
import yb.YbConsts;
import yb.YbUtils;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import controllers.payment.yb.YbPaymentRequestCtrl;

/**
 * 积分商城：积分记录
 * 
 * @author yuy
 * @created 2015-10-14
 */
public class ScroeRecordCtrl extends BackBaseController {

	private static final UserService userService = Factory.getService(UserService.class);
	
	private static final NoticeService noticeService = Factory.getService(NoticeService.class);
	
	/**
	 * 积分记录列表
	 */
	public static void scroeRecordListPre() {
		ResultInfo error = new ResultInfo();
		String orderType = params.get("orderType");
		String orderStatus = params.get("orderStatus");
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		String user_name = params.get("user_name");
		String typeStr = params.get("type");
		String isExportStr = params.get("isExport");
		int currPage = 0;
		int pageSize = 0;
		int type = 0;
		int isExport = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(typeStr)) {
			type = Integer.parseInt(typeStr);
		}
		if (StringUtils.isNotBlank(isExportStr)) {
			isExport = Integer.parseInt(isExportStr);
		}
		PageBean<t_mall_scroe_record> page = MallScroeRecord.queryScroeRecordByPage(0, user_name, type, orderType, orderStatus, currPage, pageSize,
				isExport, error,"","");
		if (error.code < 0) {
			flash.error("抱歉，系统出错，请联系管理员");
		}

		/* 导出excel */
		if (isExport == 1) {
			new ScroeRecordCtrl().exportExcel(page.page, "兑换记录", new String[] { "用户名", "时间", "积分", "兑换数量/投资金额", "事件", "状态(1:成功,2:消费/赠送中)", "备注" },
					new String[] { "user_name", "time", "scroe", "quantity", "description", "status", "remark" });
		}

		render(page);
	}

	/**
	 * 导出Excel表格
	 * 
	 * @param list
	 *            数据集合
	 * @param name
	 *            导出表格名称
	 * @param arr1
	 *            必要参数1
	 * @param arr2
	 *            必要参数2
	 * @param key
	 *            需要转换的key标示
	 */
	public void exportExcel(List<?> list, String name, String[] arr1, String[] arr2) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));
		jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor("##,##0.00"));
		JSONArray arrList = JSONArray.fromObject(list, jsonConfig);

		File file = ExcelUtils.export(name, arrList, arr1, arr2);

		renderBinary(file, name + ".xls");
	}
	
	
	/**
	 * 发货记录（实物）
	 */
	public  static void goodsDeliveryPre(){
		ResultInfo error = new ResultInfo();
		String orderType = params.get("orderType");
		String orderStatus = params.get("orderStatus");
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		String user_name = params.get("user_name");
		String typeStr = params.get("type");
		String isExportStr = params.get("isExport");
		int currPage = 0;
		int pageSize = 0;
		int type = 0;
		int isExport = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(typeStr)) {
			type = Integer.parseInt(typeStr);
		}
		if (StringUtils.isNotBlank(isExportStr)) {
			isExport = Integer.parseInt(isExportStr);
		}
		PageBean<t_mall_scroe_record> page = MallScroeRecord.queryScroeByPage(0, user_name, type, orderType, orderStatus, currPage, pageSize,
				isExport, error);
		if (error.code < 0) {
			flash.error("抱歉，系统出错，请联系管理员");
		}
		
		/* 导出excel */
		if (isExport == 1) {
			new ScroeRecordCtrl().exportExcel(page.page, "兑换记录", new String[] { "用户名", "时间", "积分", "兑换数量/投资金额", "事件", "状态(1:成功,2:消费/赠送中)", "备注" },
					new String[] { "user_name", "time", "scroe", "quantity", "description", "status", "remark" });
		}

		render(page);
	}
	
	/**
	 * 标记发货
	 * @param userId
	 */
	public static void markShipmentPre(long id,long userId){
		
		t_user user = userService.findByID(userId);
		String mobile = user.mobile;
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		
		render(mobile,userId, scenes,id);
	}
	
	/**
	 * 标记发货
	 * @param userId
	 * @param mark
	 * @param content
	 * @param dec
	 */
	public static void markShipment(long id,long userId,String mark,String content,String dec,int sms,int mail,int email){
		int rows = MallScroeRecord.updateShipment(id, content, dec);
		
		if(rows >0){
			flash.error("修改成功");
		}else{
			flash.error("数据库异常");
		}
		
		if(sms>0){
			boolean flag = noticeService.sendSMS(userId, getCurrentSupervisorId(), content);
			if (!flag) {
				flash.error("短信消息发送失败");
				markShipmentPre(id,userId);
			} 
		}
		
		if(mail>0){
			boolean flag = noticeService.sendMsg(userId, getCurrentSupervisorId(), "物流信息", content);
			if (!flag) {
				flash.error("消息发送失败");
				markShipmentPre(id,userId);
			} 
		}
		if(email>0){
			 noticeService.sendEmail(userId, getCurrentSupervisorId(), "物流信息", content);
		}
		goodsDeliveryPre();
	}
	
	/**
	 * 后台积分商城物流信息查询
	 * @param dec
	 */
	public static void jsonpCallback(String nu) {
		
		String datas = HttpUtil.getsMethod(nu);
		
		renderJSON(datas);
	}
	
	public static void goodsVirtualDeliveryPre() {
		ResultInfo error = new ResultInfo();
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		
		int currPage = 0;
		int pageSize = 0;
		
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		
		PageBean<t_mall_virtual_goods_records> page = MallScroeRecord.queryVirtualGoodsScoreRecordPage(currPage, pageSize, error);
		render(page);
	}
}
