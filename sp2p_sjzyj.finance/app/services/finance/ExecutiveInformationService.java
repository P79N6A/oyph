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
import daos.finance.ExecutiveInformationDao;
import models.finance.entity.t_executive_information;
import play.Play;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: ExecutiveInformationService
 *
 * @description 高管信息Service接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class ExecutiveInformationService extends BaseService<t_executive_information> {

	protected ExecutiveInformationDao executiveInformationDao = null;
	
	protected ExecutiveInformationService () {
		this.executiveInformationDao = Factory.getDao(ExecutiveInformationDao.class);
		
		super.basedao = executiveInformationDao;
	}
	
	public List<JSONObject> getExceutiveReportList(){
		List<JSONObject> executives = new ArrayList<JSONObject>();
		List<t_executive_information> executiveList = executiveInformationDao.findAll();
		if(executiveList!=null && executiveList.size()>0){
			for(t_executive_information executive:executiveList){
				JSONObject intervalMap = new JSONObject(true);
				intervalMap.put("P030001",executive.certificate_type);
				intervalMap.put("P030002",executive.certificate_num);
				intervalMap.put("P030003",executive.name);
				intervalMap.put("P030004",executive.position);
				intervalMap.put("P030005",DateUtil.dateToString(executive.entry_time,"yyyy-MM-dd"));
				intervalMap.put("P030006",executive.education);
				intervalMap.put("P030007",executive.experience);
				executives.add(intervalMap);
			}
		}
		return executives;
		
	}
	public Map<String,JSONObject> getExecutiveMap(){
		Map<String,JSONObject> resultMap = new HashMap<String,JSONObject>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);
		String lastDay = format.format(cale.getTime());
		
		 Date date = new Date();
		 /**获取机构内控表报文头*/
		 JSONObject headerInfo = JRBUtils.getHeaderInfoMap("911301050799558020", "000000", "石家庄菲尔德投资咨询有限公司", "", DateUtil.dateToString(date, "yyyy-MM-dd") ,DateUtil.dateToString(date, "HH:mm:ss"), SuperviseReportType.EXECUTIVE_INFO_TABLE.value, "A1234B1234C1234D1234oplk8976tr1d");
		 /**获取机构内控表报文基本信息*/
		 JSONObject reportBasicInfo = JRBUtils.getReportBasicInfoMap("911301050799558020", lastDay, "贾静雯", "裴雪松", "冯鑫");
		 
		 resultMap.put("headerInfo",headerInfo);
		 resultMap.put("reportBasicInfo", reportBasicInfo);
		 
		 return resultMap;
	}
	
	public File createexEcutiveInformationXML(String fileUrl){
		 Map<String,JSONObject> executive = getExecutiveMap();
		 
		 List<JSONObject> executives = getExceutiveReportList();
		 System.out.println("路径："+fileUrl);
		 System.out.println("完整路径："+Play.applicationPath.toString().replace("\\", "/")+ File.separator+ fileUrl);
		 return JRBXMLUtils.createXML(executive.get("headerInfo"), executive.get("reportBasicInfo"), executives,  fileUrl);
		 
		 
	 }
	
}
