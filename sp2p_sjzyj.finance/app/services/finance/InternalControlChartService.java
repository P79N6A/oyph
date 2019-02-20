package services.finance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import common.enums.SuperviseReportType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JRBUtils;
import common.utils.JRBXMLUtils;
import daos.finance.InternalControlChartDao;
import models.finance.entity.t_internal_control_chart;
import play.Play;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: InternalControlChartService
 *
 * @description P13机构内控情况Service接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class InternalControlChartService extends BaseService<t_internal_control_chart> {

	protected InternalControlChartDao internalControlChartDao = null;
	
	protected InternalControlChartService () {
		this.internalControlChartDao = Factory.getDao(InternalControlChartDao.class);
		
		super.basedao = internalControlChartDao;
	}
	
	public List<JSONObject> getInternalReportList(){
		List<JSONObject> intervalList = new ArrayList<JSONObject>();
		List<t_internal_control_chart> reportInternal = internalControlChartDao.reportInternal();
		if(reportInternal != null && reportInternal.size()>0){
			for(t_internal_control_chart internal : reportInternal){
				JSONObject intervalMap = new JSONObject(true);
				intervalMap.put("P130001",internal.third_party_digital_authentication_system);
				intervalMap.put("P130002", internal.contract_of_loan);
				intervalMap.put("P130003", internal.fortress_product);
				intervalMap.put("P130004", internal.firewall);
				intervalMap.put("P130005", internal.emergency_response_plan);
				intervalMap.put("P130006", internal.approval_process_management);
				intervalMap.put("P130007", internal.security_risk_control);
				intervalMap.put("P130008", internal.risk_model);
				intervalMap.put("P130009", internal.risk_identification_software_system);
				intervalMap.put("P130010", internal.post_loan_risk_monitoring);
				intervalMap.put("P130011", internal.customer_information_collection_and_management);
				intervalMap.put("P130012", internal.risk_prompt_interface);
				intervalMap.put("P130013", internal.investment_risk_assessment);
				intervalMap.put("P130014", internal.regulatory_penalties);
				intervalMap.put("P130015", internal.records_of_illegal_operations);
				intervalMap.put("P130016", internal.third_party_digital_authentication_system_explain);
				intervalMap.put("P130017", internal.contract_of_loan_explain);
				intervalMap.put("P130018", internal.fortress_product_explain);
				intervalMap.put("P130019", internal.firewall_explain);
				intervalMap.put("P130020", internal.emergency_response_plan_explain);
				intervalMap.put("P130021", internal.approval_process_management_explain);
				intervalMap.put("P130022", internal.security_risk_control_explain);
				intervalMap.put("P130023", internal.risk_model_explain);
				intervalMap.put("P130024", internal.risk_identification_software_system_explain);
				intervalMap.put("P130025", internal.post_loan_risk_monitoring_explain);
				intervalMap.put("P130026", internal.customer_information_collection_and_management_explain);
				intervalMap.put("P130027", internal.risk_prompt_interface_explain);
				intervalMap.put("P130028", internal.investment_risk_assessment_explain);
				intervalMap.put("P130029", internal.regulatory_penalties_explain);
				intervalMap.put("P130030", internal.records_of_illegal_operations_explain);
				
				intervalList.add(intervalMap);
			}
		}
		return intervalList;
	}
	/**
	 * 
	 * @Title: getInternalMap
	 * 
	 * @description 获取内控机构表报文头和报文基本信息
	 * @return Map<String,JSONObject>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月15日-下午5:03:45
	 */
	 public Map<String,JSONObject> getInternalMap(){
		 Map<String,JSONObject> resultMap = new HashMap<String,JSONObject>();

		 Date date = new Date();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar cale = Calendar.getInstance();
		 cale.set(Calendar.DAY_OF_MONTH, 0);
		 String lastDay = format.format(cale.getTime());
		 /**获取机构内控表报文头*/
		 JSONObject headerInfo = JRBUtils.getHeaderInfoMap("911301050799558020", "000000", "石家庄菲尔德投资咨询有限公司", "", DateUtil.dateToString(date, "yyyy-MM-dd") ,DateUtil.dateToString(date, "HH:mm:ss"), SuperviseReportType.INTERNAL_CONTROL_SITUATION_TABLE.value, "A1234B1234C1234D1234oplk8976tr1d");
		 /**获取机构内控表报文基本信息*/
		 JSONObject reportBasicInfo = JRBUtils.getReportBasicInfoMap("911301050799558020",lastDay, "贾静雯", "裴雪松", "冯鑫");
		 
		 resultMap.put("headerInfo",headerInfo);
		 resultMap.put("reportBasicInfo", reportBasicInfo);
		 
		 return resultMap;
		 
	 }
	 /**
	  * 
	  * @Title: createInternalXML
	  * 
	  * @description 获取机构内控表信息，生产xml文件
	  * @param  fileUrl
	  * @return File    
	  *
	  * @author LiuHangjing
	  * @createDate 2018年10月15日-下午5:31:45
	  */
	 public File createInternalXML(String fileUrl){
		 Map<String,JSONObject> internal = getInternalMap();
		 
		 List<JSONObject> reportList = getInternalReportList();
		 
		 return JRBXMLUtils.createXML(internal.get("headerInfo"), internal.get("reportBasicInfo"), reportList, fileUrl);
		 
		 
	 }
	 
}
