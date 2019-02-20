package controllers.back;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.SupervisorEventLog;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;
import models.common.entity.t_conversion_user.ConversionStatus;
import models.common.entity.t_event_supervisor.Event;
import payment.impl.PaymentProxy;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import services.common.ConversionService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.main.StatisticIndexEchartDataService;
import yb.enums.ServiceType;

import com.shove.Convert;

import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.Client;
import common.utils.DateUtil;
import common.utils.ECharts;
import common.utils.Factory;
import common.utils.HttpUtils;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import controllers.common.BackBaseController;
import controllers.front.seal.ElectronicSealCtrl;

/**
 * 后台-首页
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class BackHomeCtrl extends BackBaseController {
		
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static StatisticIndexEchartDataService echartDataService = Factory.getService(StatisticIndexEchartDataService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	/**
	 * 后台-首页<br>
	 * 
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月18日
	 */
	public static void backHomePre() {
		int is_statistics_show = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW), 0);//前台是否显示统计数据
		renderArgs.put("is_statistics_show", is_statistics_show);
		
		String project_releases_trailer = settingService.findSettingValueByKey(SettingKey.PROJECT_RELEASES_TRAILER);//平台短讯
		renderArgs.put("project_releases_trailer", project_releases_trailer);
		
		PageBean<SupervisorEventLog> pageBean = supervisorService.pageOfAllEventLogs(1, 7, 0, 0, null, null, null);
		renderArgs.put("eventLogs", pageBean.page);
		
		Map countBidInfo = bidService.backCountBidInfo();
		Map countBillInfo = billService.backCountBillInfo();
		countBidInfo.putAll(countBillInfo);
		
		int applyingConv = conversionService.countConversions();//待兑换奖励记录
		
		int applyingDebt = debtService.backCountDebtInfo();//待审转让项目
		
		render(countBidInfo,applyingConv,applyingDebt);
	}

	/**
	 * 后台-首页-ECharts数据加载
	 * 
	 * @param type
	 * @param position
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public static void showEchartsDataPre(int type,String position){
		//根据type获取不同Echarts数据
		ECharts chartBean = new ECharts();
		if ("left".equals(position)) {
			chartBean = echartDataService.findMembersCount(type);
		}else if("right".equals(position)){
			chartBean = echartDataService.findMoneyNumber(type);
		}
		renderJSON(chartBean);
	}
	
	/**
	 * 后台-首页-ECharts数据加载
	 * 
	 * @param type
	 * @param position
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public static void showEchartsDatasPre(int type,String position){
		//根据type获取不同Echarts数据
		ECharts chartBean = new ECharts();
		if ("left".equals(position)) {
			chartBean = echartDataService.findMembersCount(type);
		}else if("right".equals(position)){
			chartBean = echartDataService.findDatas(type);
		}
		renderJSON(chartBean);
	}
	
	/**
	 * 首页-首页-前台数据显示<br>
	 * 
	 * @rightID 10100101
	 *
	 * @param flag true前台显示统计数据，false不显示统计数据
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void updateIsStatisticsShow(boolean flag) {
		ResultInfo result = new ResultInfo();
		
		if (!settingService.updateIsStatisticsShow(flag)) {
			result.msg = "数据没有更新";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.HOME_STATISTICS_SHOW, null);
		
		renderJSON(result);
	}
	
	/**
	 * 后台-首页-项目发布预告
	 * 
	 * @rightID 101002
	 *
	 * @param trailer 新的项目预告
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void updateProjectReleasesTrailer(String trailer) {
		ResultInfo result = new ResultInfo();
	
		if (StringUtils.isBlank(trailer) || trailer.length() > 20) {
			result.code = -2;
			result.msg = "你输入的数据有误，请检查后重新输入!";

			renderJSON(result);
		}
		
		boolean flag = settingService.updateProjectReleasesTrailer(trailer);
		
		if (!flag) {
			result.msg = "没有更新成功，请稍后重试!";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "更新成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.HOME_EDIT_TRAILER, null);
	
		renderJSON(result);
	}
	
	/**开户界面跳转
	 * 
	 * @createDate 2017年9月9日
	 */
	public static void createAccountsPre() {		
		
		render();
	}
	
	/**
	 * 宜宾银行企业开户
	 * @param userName 法人姓名
	 * @param companyName 企业名称
	 * @param uniSocCreCode 统一信用代码
	 * @param idCard  身份证号
	 * @param entType	主体类型
	 * @param establishDate	成立时间
	 * @param companyId	对公户账号
	 * @param licenseAddress	营业外执照住所
	 * @param bankNo 对公户开户行行号
	 * @param bankName	对公户开户行名称
	 * @param industry_involved  所属行业
	 * @param enterprise_scale  公司规模
	 * @param exp_date  营业执照到期日期
	 * @param sts_flg  营业执照是否有效
	 * 
	 * @throws ParseException 
	 * 
	 */
	 
	public static void ybFirmRegist(String phone, String userName, String companyName, String uniSocCreCode, String entType, String establishDate, String companyId,
			String licenseAddress, String bankNo, String bankName, String idCard, String industry_involved, String enterprise_scale, String exp_date, String sts_flg) throws ParseException {		

		if (!userService.isMobileExists(phone)) {
			flash.error("手机号码不存在");
			createAccountsPre();
		}		
		
		t_user user = userService.findByColumn("mobile=?", phone);
		t_user_fund userFund = userFundService.queryUserFundByUserId(user.id);		
		
		
		if (userFund == null) {
			
			flash.error("获取用户信息失败");
			createAccountsPre();
		}
		
		ResultInfo result = new ResultInfo();
		/** 企信查三要素验证 */
		/*ResultInfo result = enterpriseThree(userName, companyName, uniSocCreCode);
		
		if (result.code < 0){
			
			flash.error(result.msg);
			createAccountsPre();
		}*/
		
		if (StringUtils.isNotBlank(userFund.payment_account)) {
			flash.error("你已开通资金托管");
			createAccountsPre();
		}

		if (industry_involved.equals("-1")) {
			flash.error("请选择所属行业");
			createAccountsPre();
		}
		
		if (enterprise_scale.equals("-1")) {
			flash.error("请选择公司规模");
			createAccountsPre();
		}
		
		//格式化时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date2=sdf.parse(establishDate);
		String end1=sdf.format(date2);
		
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTERPRISE_CUSTOMER_REGIST);
		
		result = PaymentProxy.getInstance().enterpriseCustomerRegist(Client.PC.code, businessSeqNo, user.id, 
				companyName, uniSocCreCode, entType, end1, companyId, licenseAddress, bankNo, bankName,userName,idCard);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", user.id);
		
		if (userInfo == null) {
			flash.error("用户不存在，请重试");
			createAccountsPre();
		}
		
		userInfo.industry_involved = industry_involved;
		userInfo.enterprise_scale = enterprise_scale;
		userInfo.sts_flg = sts_flg.charAt(0);
		userInfo.exp_date = DateUtil.strToDate(exp_date,  Constants.DATE_FORMATESS);
		
		userInfo.save();
		
		t_user_info userInfos = userInfoService.findByColumn("user_id = ?", user.id);
		
		//上上签企业注册
		String taskId = ElectronicSealCtrl.EnterpriseUserReg(userInfos);
		if(taskId == null){
			
			flash.error("企业级电子签证注册失败");
			createAccountsPre();
		}
		
		createAccountsPre();
		
	}
	
	
	/**
	 * 企信查三要素（API）
	 * @param userName 法人姓名
	 * @param companyName 企业名称
	 * @param uniSocCreCode 统一信用代码
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月23日16:51:47
	 */
	public static ResultInfo enterpriseThree(String userName, String companyName, String uniSocCreCode) {
	    ResultInfo result =new ResultInfo();
		
		String host = "https://business3k.market.alicloudapi.com";
	    String path = "/v2/business3key_compare/";
	    String method = "POST";
	    String appcode = "75dcd7626a2b4a7985d13941041d2208";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    //根据API的要求，定义相对应的Content-Type
	    headers.put("Content-Type", "application/json; charset=UTF-8");
	    Map<String, String> querys = new HashMap<String, String>();
	    Map<String, String> body = new HashMap<String, String>();
	    
	    body.put("legal_person_name", userName);
	    body.put("company_name", companyName);
	    body.put("register_num", uniSocCreCode);
	    
	    String bodys = JSONObject.fromObject(body).toString();

	    try {
	    	/**
	    	 * 企业三要素校验：0000一致
	    	*/
	    	HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
	    	//获取response的body
	    	String respJsonString = EntityUtils.toString(response.getEntity());
	    	JSONObject respMsg =  JSONObject.fromObject(respJsonString);
	    	String status = (String) respMsg.get("status");
	    	if (status.equals("00000")){
	    		
	    		result.code = 1;
	    		result.msg  = (String) respMsg.get("remark");
	    	}else {
	    		
	    		result.code = -1;
	    		result.msg = (String) respMsg.get("remark");
	    	}
	    	
	    	return result ;
	    	
	    } catch (Exception e) {
	    	
	    	result.code = -1;
	    	result.msg = "企业信息校验失败" ;
	    	return result ;
	    }
	   
	}

}
