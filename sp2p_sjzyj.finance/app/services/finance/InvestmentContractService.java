package services.finance;

import java.io.File;
import java.math.BigDecimal;
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
import common.utils.TimeUtil;
import daos.base.BaseDao;
import daos.finance.InvestmentContractDao;
import models.finance.entity.t_investment_contract;
import play.Logger;
import play.Play;
import services.base.BaseService;

/**
 * 金融办-投资合同表 Service
 * @author guoShiJie
 * 
 * @createDate 2018.10.5
 * */
public class InvestmentContractService extends BaseService<t_investment_contract> {

	protected InvestmentContractDao investmentContractDao= Factory.getDao(InvestmentContractDao.class);
	
	protected InvestmentContractService () {
		
		super.basedao = this.investmentContractDao;
	}
	
	/**
	 * 粘贴从其他表中查询得到投资合同表的数据 
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public int updateInvestmentDatas () {
		return investmentContractDao.updateInvestmentDatas();
	}
	
	/**
	 * P07获取投资合同表报文
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public List<JSONObject> getInvestReportList () {
		List<JSONObject> investmentList = new ArrayList<JSONObject>();
		List<t_investment_contract> investment = investmentContractDao.findAll();
		if (investment != null && investment.size() > 0) {
			for (t_investment_contract iment : investment) {
				JSONObject investmentMap = new JSONObject (true);
				investmentMap.put("P070001", iment.p_id);
				investmentMap.put("P070002", iment.user_id);
				investmentMap.put("P070003", iment.amount);
				investmentMap.put("P070004", iment.time == null ? "" : DateUtil.dateToString(iment.time, "yyyy-MM-dd"));
				investmentMap.put("P070005", iment.apr);
				investmentMap.put("P070006", iment.unpaid_principal);
				investmentMap.put("P070007", iment.unpaid_interest);
				investmentMap.put("P070008", iment.manage_cost);
				
				investmentList.add(investmentMap);
			}
		}
		return investmentList;
	}
	
	/**
	 * 更改数据
	 * @author guoShiJie
	 * @createDate 2018.10.10
	 * */
	public void updateDatas () {
		Date date = new Date();
		List<t_investment_contract> invest = investmentContractDao.getInvestmentDatas();
		
		BigDecimal money = new BigDecimal("0.00");
		if (invest != null && invest.size() > 0) {
			for (t_investment_contract inv : invest) {
				/**判断是否逾期*/
				if (inv.real_receive_time == null) {
					/**逾期*/
					if (DateUtil.isDateBefore(inv.receive_time, date)) {
						/**被拖欠利息*/
						inv.unpaid_interest = inv.receive_interest;
						/**被拖欠本金*/
						inv.unpaid_principal = inv.receive_corpus;
					}else {
						/**未逾期*/
						/**被拖欠利息*/
						inv.unpaid_interest = money;
						/**被拖欠本金*/
						inv.unpaid_principal = money;
					}
				} else {
					/**逾期*/
					if (DateUtil.isDateBefore(inv.receive_time, inv.real_receive_time)) {
						/**被拖欠利息*/
						inv.unpaid_interest = inv.receive_interest;
						/**被拖欠本金*/
						inv.unpaid_principal = inv.receive_corpus;
					} else {
						/**未逾期*/
						/**被拖欠利息*/
						inv.unpaid_interest = money;
						/**被拖欠本金*/
						inv.unpaid_principal = money;
					}
				}
				inv.save();
			}
		}
		Logger.info("P07投资合同表更新数据完成!!!");
		
	}
	
	/**
	 * 获取投资合同表报文头 和投资合同表报文基本信息
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public Map< String ,JSONObject> getInvestMap() {
		Map< String ,JSONObject> result = new HashMap<String,JSONObject> ();
		
		Date date = new Date();
		
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar cale = Calendar.getInstance();
		 cale.set(Calendar.DAY_OF_MONTH, 0);
		 String lastDay = format.format(cale.getTime());
		/**获取贷款合同表报文头*/
		JSONObject headerInfo = JRBUtils.getHeaderInfoMap("911301050799558020", "000000", "石家庄菲尔德投资咨询有限公司", "", DateUtil.dateToString(date, "yyyy-MM-dd") ,DateUtil.dateToString(date, "HH:mm:ss"), SuperviseReportType.INVEST_PACT_TABLE.value, "A1234B1234C1234D1234oplk8976tr1d");
		
		/**获取贷款合同表报文基本信息*/
		JSONObject reportBasicInfo = JRBUtils.getReportBasicInfoMap("911301050799558020", lastDay, "李媛媛", "裴雪松", "冯鑫");

		result.put("headerInfo", headerInfo);
		result.put("reportBasicInfo", reportBasicInfo);
		return result;
	}
	
	/**
	 * 获取投资合同信息，生成XML文件
	 * 
	 * @param 生成XML文件的路径
	 * @author guoShiJie
	 * @createDate 2018.10.11
	 * */
	public File createInvestXML (String fileURL) {
		
		
		Map<String,JSONObject> invest = getInvestMap();
		List<JSONObject> report = getInvestReportList();
		return JRBXMLUtils.createXML(invest.get("headerInfo"), invest.get("reportBasicInfo"), report,  fileURL);
	}
}
