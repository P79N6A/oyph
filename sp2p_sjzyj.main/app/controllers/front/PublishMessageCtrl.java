package controllers.front;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

import common.enums.InformationMenu;
import common.utils.DateUtil;
import common.utils.Factory;
import controllers.common.FrontBaseController;
import models.common.entity.t_disclosure;
import models.common.entity.t_disclosure_invest;
import models.common.entity.t_disclosure_month;
import models.common.entity.t_information;
import net.sf.json.JSONObject;
import services.common.DisclosureInvestService;
import services.common.DisclosureMonthService;
import services.common.DisclosureService;
import services.common.InformationService;

/**
 * 信息披露
 * 
 * @author liuyang
 * @time 2017-12-21 16:30
 *
 */
public class PublishMessageCtrl extends FrontBaseController{
	
	protected static DisclosureService disclosureService = Factory.getService(DisclosureService.class);
	
	protected static DisclosureMonthService disclosureMonthService = Factory.getService(DisclosureMonthService.class);
	
	protected static DisclosureInvestService disclosureInvestService = Factory.getService(DisclosureInvestService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	static DecimalFormat dfes = new DecimalFormat( "###############0.00 ");

	/**
	 * 信息披露实时数据显示页
	 */
	public static void toPublishMessagePre(long disclosureId) {
		
		if(disclosureId == 0) {
			disclosureId = disclosureService.findByTime().id;
		}
		
		List<t_disclosure> dises = disclosureService.queryListById();
		renderArgs.put("dises", dises);
		
		/* 信息披露总表 */
		t_disclosure dis = disclosureService.findByID(disclosureId);
		renderArgs.put("dis", dis);
		
		/* 当月前十大投资人信息 */
		List<t_disclosure_invest> disInvest = disclosureInvestService.findListByColumn("disclosure_id=?", disclosureId);
		renderArgs.put("disInvest", disInvest);
		
		/* 当月统计信息 */
		t_disclosure_month disMonth = disclosureMonthService.findByColumn("disclosure_id=?", disclosureId);
		renderArgs.put("disMonth", disMonth);
		
		/* 查询十二个月的月信息统计 */
		List<t_disclosure_month> disMonthes = disclosureMonthService.queryListById(disclosureId);
		renderArgs.put("disMonthes", disMonthes);
		
		Date dates = DateUtil.minusDay(dis.time, 1);
		renderArgs.put("dates", dates);
		
		double qita = Double.parseDouble(disMonth.mon_amount)-Double.parseDouble(disMonth.top_ten_amount);
		renderArgs.put("qita", qita);
		
		render();
	}
	
	public static void toPublishMessage(long disclosureId) {
		
		/* 查询十二个月的月信息统计 */
		List<t_disclosure_month> disMonthes = disclosureMonthService.queryListById(disclosureId);
		
		renderJSON(new Gson().toJson(disMonthes));
	}
	
    public static void publishIndexPre() {
		
		render();
	}
    
    public static void archivalInformationPre() {
    	String disclosureIds = params.get("disclosureId");
		long disclosureId = 0;
		
		if(disclosureIds == null) {
			disclosureId = disclosureService.findByTime().id;
		}else {
			disclosureId = Long.parseLong(disclosureIds);
		}
		
		t_disclosure dis = disclosureService.findByID(disclosureId);
		renderArgs.put("dis", dis);
		
		Date dates = DateUtil.minusDay(dis.time, 1);
		renderArgs.put("dates", dates);
		
		render();
	}
    
    public static void recordsInformationPre() {
		
  		render();
  	}
    
    public static void tissueInformationPre() {
		
  		render();
  	}
    
    public static void auditInformationPre() {
 		render();
 	}
    public static void auditingReportPre() {
 		render();
 	}
    public static void policyRegulationsPre() {
 		render();
 	}
    public static void riskEducationPre(Integer limit) {
    	int size = informationService.findListByColumn(" column_key = ? and is_use = ? ", InformationMenu.RISK_EDUCATION.code, true).size();
    	
    	List<t_information> information = informationService.queryRiskEducations(InformationMenu.RISK_EDUCATION, true,limit);
    	
 		render(information,size);
 	}
    /**储蓄管理条例*/
    public static void savingsManagementPre() {
 		render();
 	}
    /** 中华人民共和国商业银行法(2015年修正)*/
    public static void commercialBankLawPre() {
 		render();
 	}
    /**中华人民共和国中国人民银行法(2003修正)*/
    public static void peoplesBankLawPre() {
 		render();
 	}
    /**中华人民共和国人民币管理条例*/
    public static void rmbManagementPre() {
 		render();
 	}
    /**中华人民共和国银行业监督管理法(2006修正)*/
    public static void supervisionManagementPre() {
 		render();
 	}
}
