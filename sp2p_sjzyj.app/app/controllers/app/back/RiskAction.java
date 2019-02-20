package controllers.app.back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import net.sf.json.JSONObject;
import models.common.entity.t_company_branch;
import models.common.entity.t_risk_reception;
import models.common.entity.t_risk_report;
import services.back.RiskService;
import services.common.BranchService;
import services.common.RiskReceptionService;
import common.utils.BrokerUtils;
import common.utils.Factory;
import controllers.common.BackBaseController;

public class RiskAction extends BackBaseController{
	
	protected static BranchService branchService =  Factory.getService(BranchService.class);
	
	protected static RiskReceptionService riskReceptionService =  Factory.getService(RiskReceptionService.class);
	
	protected static RiskService riskService = Factory.getService(RiskService.class);
	
	/**
	 * 到生成风控报告界面
	 * @return
	 * @throws Exception
	 * @createDate 2017年5月8日
	 * @author lihuijun
	 */
	public static String toCreateRiskReport() throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "查询成功");
		//查询所有分公司
		List<t_company_branch> branchs = branchService.findAll();
		result.put("branchs", branchs);
		
		//查询所有风控人员
		List<t_risk_reception> receptions = riskReceptionService.findAll();
		result.put("receptions", receptions);

		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 生成风控报告/修改风控报告
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @createDate 2017年5月8日
	 * @author lihuijun
	 */
	public static String CreateRiskReport(Map<String, String> parameters) throws Exception{
		String msg="输入内容非法";
		t_risk_report  riskReport=riskService.saveRiskReport(parameters);
		String flag=parameters.get("flag");
		 msg="提交成功";
		if(StringUtils.isNotBlank(flag)){
			riskReport.status=-1;
			riskReport.save();
			msg="保存成功";
		}
		riskService.saveRiskAuxi(parameters, riskReport);	
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", msg);
		return JSONObject.fromObject(result).toString();
	}
		
	
	/**
	 * 按状态条件查询
	 * @return
	 * @throws Exception
	 * @createDate 2017年5月8日
	 * @author lihuijun
	 */
	public static String queryRiskReportList(Map<String, String> parameters) throws Exception{
		return riskService.getRiskReportListStr(parameters,"查询成功");
	}
	/**
	 * 查看风控报告详情
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @createDate 2017年5月10日
	 * @author lihuijun
	 */
	public static String queryRiskReportDetail(Map<String, String> parameters) throws Exception{
		long  riskReportId = Convert.strToLong(parameters.get("riskReportId"), 0);
		return riskReportId == 0 ? BrokerUtils.paramErrorHandle() : riskService.getRiskReportDetailStr(riskReportId);
	}
	
	/**
	 * 审核风控报告
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @createDate 2017年5月10日
	 * @author lihuijun
	 */
	public static String AuditRiskReport(Map<String, String> parameters) throws Exception{
		long superId = Convert.strToLong(parameters.get("superId"), 0);
		return superId==0 ? BrokerUtils.paramErrorHandle() : riskService.auditReport(parameters,superId);
	}
	
}
