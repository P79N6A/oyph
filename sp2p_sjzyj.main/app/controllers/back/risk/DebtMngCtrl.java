package controllers.back.risk;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_pact;
import models.common.entity.t_event_supervisor.Event;
import models.core.bean.BackDebtTransfer;
import models.core.entity.t_bid;
import models.core.entity.t_debt_transfer;
import models.core.entity.t_invest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.StaticVerifier;

import services.common.PactService;
import services.core.BidService;
import services.core.DebtService;
import services.core.InvestService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;
import controllers.front.seal.ElectronicSealCtrl;

/**
 * 后台-风控-债权转让管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
public class DebtMngCtrl extends BackBaseController {
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static InvestService investservice = Factory.getService(InvestService.class);
	
	protected static BidService bidservice = Factory.getService(BidService.class);
	
	protected static PactService pactService = Factory.getService(PactService.class);
	/**
	 * 后台-风控-转让项目
	 * 
	 * @rightID 402001
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param showType 0-所有   1-待审核  2-转让中 3-成功   4-失败(审核不通过,过期,失效)
	 * @param transferName 转让人姓名
	 * @param orderType 排序栏目  0：编号 3：债权总额   4：转让期数  5：转让价格
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param exports 1：导出   default：不导出
	 * @param numNo 编号
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月28日
	 *
	 */
	public static void showDebtsPre(int showType, int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String transferName = params.get("transferName");
		String numNo = params.get("numNo");
		String projectName = params.get("projectName");
		
		// 排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0：编号 2：债权总额   3：转让期数  4：转让价格
		if (orderType != 0 && orderType != 2 && orderType != 3 && orderType != 4 ) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);

		// 排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);// 0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		if (showType < 0 || showType > 4) {
			showType = 0;
		}
		
		/* 查询债权列表 */
		PageBean<BackDebtTransfer> page = debtService.pageOfDebtTransferBack(showType, currPage, pageSize, transferName, orderType, orderValue, exports, numNo, projectName);
		
		/* 导出excel*/
		if (exports == Constants.EXPORT) {
			List<BackDebtTransfer> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject debtTransfer = (JSONObject)obj;
				
				DecimalFormat df = new DecimalFormat("0");
				if (StringUtils.isNotBlank(debtTransfer .getString("transfer_period"))) {
					debtTransfer.put("transfer_period", df.format(debtTransfer .getDouble("transfer_period")));
				}
				
				if (StringUtils.isNotBlank(debtTransfer.getString("status"))) {
					debtTransfer.put("status", t_debt_transfer.Status.valueOf(debtTransfer.getString("status")).value);
				}
			}
			
			String fileName="转让项目列表";
			switch (showType) {
				case 1:
					fileName = "待审核转让项目列表";
					break;
				case 2:
					fileName = "转让中转让项目列表";
					break;
				case 3:
					fileName = "已成功转让项目列表";
					break;
				case 4:
					fileName = "失败转让项目列表";
					break;
			}
			
			File file = ExcelUtils.export(
						fileName,
						arrList,
						new String[] {
								"编号", "项目", "债权总额", "转让期数","转让价格", "转让人", "受让人", "成交时间", "状态"
						},
						new String[] {
								"debt_transfer_no","title", "debt_amount","transfer_period", "transfer_price", "transfer_name", "transaction_name", "transaction_time", "status"
						}
					);
			   
			renderBinary(file, fileName + ".xls");
		}
		
		/* 查询总额 */
		double debtTotal = debtService.findTotalDebtAmount(showType, transferName, numNo, projectName);
		
		render(page,showType,debtTotal,transferName,numNo,projectName);
	}
	
	/**
	 *  后台-风控-转让项目-审核通过
	 * 
	 * @rightID 402002
	 * 
	 * @param sign 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public static void auditPass(String sign){
		 
		ResultInfo result = new ResultInfo();
		
		result = Security.decodeSign(sign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showDebtsPre(0,1,10);
		}
		long debtTransferId = Long.parseLong((String) result.obj);
		
		//管理员id
		long supervisorId = getCurrentSupervisorId();
		
		//审核通过操作
		result = debtService.auditDebtTransfer(debtTransferId, supervisorId,t_debt_transfer.Status.AUCTING);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.error(true, result.msg);

			showDebtsPre(0, 1, 10);
		}
		
		t_debt_transfer debtTransfer = (t_debt_transfer) result.obj;
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("transfer_id", debtTransfer.getDebt_transfer_no());
		supervisorEventParam.put("transfer_name", debtTransfer.title);
		supervisorService.addSupervisorEvent(supervisorId, Event.TRANSFER_ADUIT_PASS, supervisorEventParam);
		
		flash.success("审核通过成功!");
		
		showDebtsPre(0,1,10);
	}
	
	/**
	 * 后台-风控-转让项目-审核不通过
	 * 
	 * @rightID 402002
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public static void auditNoPass(String sign){
        ResultInfo result = new ResultInfo();
        result = Security.decodeSign(sign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showDebtsPre(0,1,10);
		}
		long debtTransferId = Long.parseLong((String) result.obj);
		
		//管理员id
		long supervisorId = getCurrentSupervisorId();
		
		//审核不通过操作
		result = debtService.auditDebtTransfer(debtTransferId, supervisorId,t_debt_transfer.Status.AUDIT_NOT_THROUGH);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.error(true, result.msg);

			showDebtsPre(0, 1, 10);
		}
		
		t_debt_transfer debtTransfer = (t_debt_transfer) result.obj;
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("transfer_id", debtTransfer.getDebt_transfer_no());
		supervisorEventParam.put("transfer_name", debtTransfer.title);
		supervisorService.addSupervisorEvent(supervisorId, Event.TRANSFER_ADUIT_REJECT, supervisorEventParam);
		
		flash.success("审核不通过成功!");
		
		showDebtsPre(0,1,10);
	}
	/**
	 *
	 * @Title: ssqDebtContract
	 * 
	 * @description 风控-转让项目-生成电子签章合同
	 * @param @param debtSign
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午3:41:56
	 */
	public static void ssqDebtContract(String debtSign){
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}
		long debtId = Long.parseLong(result.obj.toString());
		/** 查询债券信息*/
		t_debt_transfer debt = debtService.findByID(debtId);
		if (debt == null) {
			flash.error("债券标不存在");

			showDebtsPre(0,1,10);
		}
		long investId = debt.invest_id;
		t_invest invests = investservice.findByID(investId);
		if (invests == null) {
			flash.error("获得用户对标所投的金额有误!");

			showDebtsPre(0,1,10);
		}
		/** 生成合同 PDF文件 */
		t_pact pact = pactService.findByDebtId(debtId);
		result = pactService.exportPact(pact.id, true);
		if (result.code < 1) {
			flash.error("生成合同失败!");
			showDebtsPre(0,1,10);
		}
		File file = (File) result.obj;
		/** 上上签创建合同 和签署合同 */
		result = ElectronicSealCtrl.createDebtContract(debtId, file, pact);
		if (result.code < 1) {
			flash.error(result.msg);
			showDebtsPre(0,1,10);
		}
		flash.success("生成成功！");
		showDebtsPre(0,1,10);
	}
}
