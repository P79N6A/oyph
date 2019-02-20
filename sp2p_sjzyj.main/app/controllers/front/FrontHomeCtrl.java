package controllers.front;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.poi.util.StringUtil;

import models.common.bean.CurrUser;
import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_company_style;
import models.common.entity.t_consultant;
import models.common.entity.t_data_statistics;
import models.common.entity.t_event;
import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;
import models.common.entity.t_information;
import models.common.entity.t_partner;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.core.bean.DebtTransfer;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_product;
import models.ext.experience.entity.t_experience_bid;
import payment.impl.PaymentProxy;
import play.Play;
import play.templates.GroovyTemplate;
import play.templates.types.SafeHTMLFormatter;
import service.ext.experiencebid.ExperienceBidService;
import services.common.AdvertisementService;
import services.common.CompanyStyleService;
import services.common.ConsultantService;
import services.common.DataStatisticService;
import services.common.DevelopEventService;
import services.common.HelpCenterService;
import services.common.InformationService;
import services.common.PartnerService;
import services.common.UserInfoService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import services.core.ProductService;
import services.main.StatisticIndexEchartDataService;
import yb.enums.EntrustFlag;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;

import com.shove.Convert;
import com.sun.org.apache.bcel.internal.generic.NEW;

import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.InformationMenu;
import common.enums.IsUse;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import daos.core.BidDao;

/**
 * 前台-首页控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */

public class FrontHomeCtrl extends FrontBaseController {

	/** 注入资讯管理services */
	protected static InformationService informationService = Factory.getService(InformationService.class);

	/** 注入理财顾问services */
	protected static ConsultantService consultantService = Factory.getService(ConsultantService.class);

	/** 注入合作伙伴services */
	protected static PartnerService partnerService = Factory.getService(PartnerService.class);

	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);

	/** 散标投资 */
	protected static BidService bidService = Factory.getService(BidService.class);

	/** 借款账单services */
	protected static BillService billService = Factory.getService(BillService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	/** 帮助中心 */
	protected static HelpCenterService helpCenterService = Factory.getService(HelpCenterService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	/** 月排行榜 */
	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);

	protected static DevelopEventService deveService = Factory.getService(DevelopEventService.class);

	protected static CompanyStyleService companyStyleService = Factory.getService(CompanyStyleService.class);

	protected static DataStatisticService dataStatisticService = Factory.getService(DataStatisticService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static StatisticIndexEchartDataService siedService = Factory
			.getService(StatisticIndexEchartDataService.class);

	/**
	 * 前台-首页
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月18日
	 */
	public static void frontHomePre() {
		
		// 前台是否显示统计数据
		int is_statistics_show = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_STATISTICS_SHOW),
				0);
		renderArgs.put("is_statistics_show", is_statistics_show);

		// 项目发布预告
		String project_releases_trailer = settingService.findSettingValueByKey(SettingKey.PROJECT_RELEASES_TRAILER);
		renderArgs.put("project_releases_trailer", project_releases_trailer);

		// 首页banner
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.HOME_TURN_ADS, 10);
		renderArgs.put("banners", banners);

		// 平台优势
		t_advertisement fourprotect = advertisementService.findFourprotectPic();
		renderArgs.put("fourprotect", fourprotect);

		// experienceBid 体验标 start
		if (ModuleConst.EXT_EXPERIENCEBID) {
			ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
			t_experience_bid experienceBid = experienceBidService.findExperienceBidFront();
			if (experienceBid == null) {
				renderArgs.put("experience", false);
			} else {
				renderArgs.put("experience", true);
				renderArgs.put("experienceBid", experienceBid);
			}
		}
		// end

		// 散标投资
		PageBean<t_bid> pageOfBis = bidService.pageOfBidInvest(1, 6);
		if (pageOfBis.page != null) {
			renderArgs.put("bids", pageOfBis.page);
		}
		renderArgs.put("sysNowTime", new Date());
		List<t_product> products = productService.findAll();
		if (products != null) {
			renderArgs.put("products", products);
		}

		// 债权
		PageBean<DebtTransfer> pageDebt = debtService.pageOfDebtTransfer(1, 2);
		if (pageDebt != null && pageDebt.page != null) {
			renderArgs.put("debts", pageDebt.page);
		}

		// 月排行榜
		List<Map<String, Object>> mouthInvest = investService.queryMouthInvestList();
		if (mouthInvest != null && mouthInvest.size() > 0) {
			renderArgs.put("mouthInvest", mouthInvest);
		}

		/*
		 * //媒体报道 List<t_information> inforeports =
		 * informationService.queryInformationFront(InformationMenu.INFO_REPORT,
		 * Constants.MEDIA_REPORT_NUM); renderArgs.put("inforeports",
		 * inforeports);
		 * 
		 * //理财故事 List<t_information> infostorys =
		 * informationService.queryInformationFront(InformationMenu.
		 * INFO_BULLETIN, Constants.MEDIA_REPORT_NUM);
		 * renderArgs.put("infostorys", infostorys);
		 * 
		 * //统计资讯(官方公告，媒体报道，理财故事 )前5篇 List<t_information> informations =
		 * informationService.queryInformationsFront(InformationMenu.
		 * INFORMATION_FRONT, Constants.INFORMATION_ADS_NUM);
		 * renderArgs.put("informations", informations);
		 */

		/* liuyang begin 2017-1-7----------------------------------- */
		// 媒体报道 前3篇
		List<t_information> inforeports = informationService.queryInformationsFront(InformationMenu.INFORMATION_REPORT,
				3);

		t_information inforeFirst=inforeports.get(0);
		renderArgs.put("inforeFirst", inforeFirst);
		inforeports.remove(0);
		renderArgs.put("inforeports", inforeports);

		/*
		 * //理财故事 前6篇 List<t_information> infostorys =
		 * informationService.queryInformationsFront(InformationMenu.
		 * INFORMATION_STORY, Constants.MEDIA_REPORT_NUM);
		 * renderArgs.put("infostorys", infostorys);
		 * 
		 * //理财故事 第一个 t_information infostoryFirst = infostorys.get(0);
		 * renderArgs.put("infostoryFirst", infostoryFirst);
		 */
		
		// 官方公告 第2篇到第5篇
		List<t_information> informations = informationService
				.queryInformationsFront(InformationMenu.INFORMATION_BULLETIN, 5);
		t_information informationFirst = informations.get(0);
		
		renderArgs.put("informationFirst", informationFirst);
		
		informations.remove(0);
		renderArgs.put("informations", informations);

		// 官方公告 第一个
		

		/* liuyang end 2017-1-7------------------------------------- */

		// 理财顾问
		List<t_consultant> consultants = consultantService.queryConsultantsFront();
		renderArgs.put("consultants", consultants);

		// 合作伙伴
		List<t_partner> partners = partnerService.queryPartnersFront(Constants.PARTNER_NUM);
		renderArgs.put("partners", partners);

		Date date = new Date();
		int zhi = date.getHours();
		String ips = getIp();
		// 前台访问量统计
		List<t_data_statistics> datas = dataStatisticService.findAll();
		t_data_statistics data = new t_data_statistics();
		if (datas.size() > 0) {
			boolean flag = false;
			for (int i = 0; i < datas.size(); i++) {
				if (datas.get(i).login_ip.equals(ips)) {
					flag = true;
				}
			}
			if (flag == false) {
				data.login_ip = ips;
				data.time = new Date();
				data.save();
				if (zhi >= 0 && zhi < 2) {
					siedService.updateDatas(2);
				} else if (zhi >= 2 && zhi < 4) {
					siedService.updateDatas(4);
				} else if (zhi >= 4 && zhi < 6) {
					siedService.updateDatas(6);
				} else if (zhi >= 6 && zhi < 8) {
					siedService.updateDatas(8);
				} else if (zhi >= 8 && zhi < 10) {
					siedService.updateDatas(10);
				} else if (zhi >= 10 && zhi < 12) {
					siedService.updateDatas(12);
				} else if (zhi >= 12 && zhi < 14) {
					siedService.updateDatas(14);
				} else if (zhi >= 14 && zhi < 16) {
					siedService.updateDatas(16);
				} else if (zhi >= 16 && zhi < 18) {
					siedService.updateDatas(18);
				} else if (zhi >= 18 && zhi < 20) {
					siedService.updateDatas(20);
				} else if (zhi >= 20 && zhi < 22) {
					siedService.updateDatas(22);
				} else {
					siedService.updateDatas(24);
				}
			}
		} else {
			data.login_ip = ips;
			data.time = new Date();
			data.save();
			if (zhi >= 0 && zhi < 2) {
				siedService.updateDatas(2);
			} else if (zhi >= 2 && zhi < 4) {
				siedService.updateDatas(4);
			} else if (zhi >= 4 && zhi < 6) {
				siedService.updateDatas(6);
			} else if (zhi >= 6 && zhi < 8) {
				siedService.updateDatas(8);
			} else if (zhi >= 8 && zhi < 10) {
				siedService.updateDatas(10);
			} else if (zhi >= 10 && zhi < 12) {
				siedService.updateDatas(12);
			} else if (zhi >= 12 && zhi < 14) {
				siedService.updateDatas(14);
			} else if (zhi >= 14 && zhi < 16) {
				siedService.updateDatas(16);
			} else if (zhi >= 16 && zhi < 18) {
				siedService.updateDatas(18);
			} else if (zhi >= 18 && zhi < 20) {
				siedService.updateDatas(20);
			} else if (zhi >= 20 && zhi < 22) {
				siedService.updateDatas(22);
			} else {
				siedService.updateDatas(24);
			}
		}

		render();
	}

	/**
	 * 首页-公司介绍
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void aboutUsPre() {

		t_information profile = informationService.findLastProfile();

		if (profile == null) {

			error404();
		}

		render(profile);
	}

	/**
	 * 首页-平台优势
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void safePre() {
		t_information profile = informationService.findLastAdvantage();

		if (profile == null) {

			error404();
		}

		render(profile);
	}

	/**
	 * 首页-加入我们
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void joinusPre() {
		t_information joinus = informationService.findLastJoinus();

		if (joinus == null) {

			error404();
		}

		render(joinus);
	}

	/**
	 * 首页-联系我们
	 *	
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void contactusPre() {
		render();
	}

	public static void contactus2Pre() {
		render();
	}

	/**
	 * 首页-联系我们-百度地图
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void baidumapPre() {
		render();
	}

	/**
	 * 首页-帮助中心
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void helpPre(int columnNo, String currPage) {
		int current = Convert.strToInt(currPage, -1);
		if (current < 0) {

			error404();
		}
		Column column = Column.getEnum(columnNo);
		if (column == null) {

			error404();
		}

		PageBean<t_help_center> page = helpCenterService.pageOfHelpCenterFront(current, 8, column);
		render(page, column);

	}

	/**
	 * 首页-帮助中心
	 * 
	 * @author guoshijie
	 * @createDate 2017年11月22日
	 */
	public static void help2Pre(int columnNo, String currPage) {
		int current = Convert.strToInt(currPage, -1);
		if (current < 0) {

			error404();
		}

		
		Column column = Column.getEnum(columnNo);
		if (column == null) {

			error404();
		}
		
		PageBean<t_help_center> page = helpCenterService.pageOfHelpCenterFront(current, 8, column);
		render(page, column);
	}

	/**
	 * 首页-发展历程
	 *
	 *
	 * @author lihuijun
	 * @createDate 2017年1月10日
	 */
	/*
	 * public static void developPre(Integer year) { List<Integer>
	 * years=deveService.getAllYear(); int ct=years.size(); if(ct>8){
	 * List<Integer> yearss=new ArrayList<Integer>(); for(int i=0;i<8;i++){
	 * yearss.add(years.get(i)); } years=yearss; } render(years); }
	 */
	/**
	 * 首页-发展历程_修改
	 *
	 * @param no
	 *            模块编号
	 * @author guoshijie
	 * @createDate 2017年1月10日
	 */
	public static void developPre(Integer no) {

		/* 企业文化左侧导航栏 员工风采，企业风貌 */
		List<t_company_style> companyStyles = companyStyleService.findAll();
		t_company_style firstCompany = companyStyles.get(0);
		
		render(no, companyStyles, firstCompany);

	}

	/**
	 * 发展历程展示
	 * 
	 * @author guoshijie
	 * @createDate 7017.11.21
	 */
	public static void developmenthistoryPre() {

		List<Map<String, Object>> develop = new ArrayList<>();
		List<Integer> years = deveService.getAllYear();
		for (Integer i : years) {
			Map<String, Object> devel = new HashMap();
			PageBean<t_event> bean = new PageBean<t_event>();
			bean = deveService.getDevelopEvent(i);
			devel.put("year", i);
			devel.put("bean", bean);
			develop.add(devel);
		}

		render(develop);
	}

	/**
	 * 发展历程图片_ajax
	 * 
	 * @param id
	 *            发展历程id
	 * @author guoshijie
	 * @createDate 2017年11月13日
	 */
	public static void devel(Integer id) {

		t_event event = deveService.findByID(id);
		ResultInfo result = new ResultInfo();
		result.msg = event.image_url;

		renderJSON(result);
	}

	/**
	 * 企业风貌
	 * 
	 * @param id
	 *            企业风貌id
	 * @author guoshijie
	 * @createDate 2017.11.15
	 */
	public static void companyStylePre(Integer id) {

		t_company_style firstCompany = companyStyleService.findByID(id);

		render(firstCompany);
	}

	/**
	 * 员工风采
	 * 
	 * @param id
	 *            员工风采id
	 * @author guoshijie
	 * @createDate 2017.11.15
	 */
	public static void employeeStylePre(Integer id) {

		t_company_style firstCompany = companyStyleService.findByID(id);

		render(firstCompany);
	}

	/**
	 * 员工风采_企业风貌_ajax
	 * 
	 * @param id
	 *            企业风貌/员工风采id
	 * @author guoshijie
	 * @createDate 2017.11.15
	 */
	
	  public static void style(Integer id) {
	  
	  t_company_style style = companyStyleService.findByID(id);
	  
	  renderJSON(style);
	  
	 }
	 
	public static void change(Integer year) {
		if (year == null) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		PageBean<t_event> page = deveService.getDevelopEvent(year);
		render(page);
	}

	/**
	 * 资讯板块_ajax 统计具体一个栏目的前若干篇 （针对单一栏目进行统计）
	 * 
	 * @param info
	 *            资讯某一栏目
	 * @param limit
	 *            查询条数
	 * 
	 * @author guoshijie
	 * @createDate 2017.11.17
	 */
	public static void informationPre(String info, Integer limit) {
		InformationMenu message = null;
		
		if ("info_bulletin".equals(info)) {
			
			message = InformationMenu.getEnum("info_bulletin");

		} else if ("info_report".equals(info)) {
			
			message = InformationMenu.getEnum("info_report");

		} else if ("info_story".equals(info)) {
			
			message = InformationMenu.getEnum("info_story");

		}
		
		int size = informationService.findListByColumn("column_key=? AND is_use=? AND show_time < ?",message.code , IsUse.USE.code , new Date()).size();
		
		List<t_information> information = informationService.queryInformationFront(message, limit);

		render(information, message,size);

	}

	// 新手指引
	public static void newhandPre() {
		render();
	}
	/**
	 * lihuijun---end
	 */

	/**
	 * 禁止收录
	 */
	public static void robots() {
		boolean is_robot = Convert.strToBoolean(Play.configuration.getProperty("is.robots"), true);
		String path = Play.configuration.getProperty("trust.funds.path") + "/robots.txt";
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (is_robot) {
			renderBinary(is);
		} else {
			renderText("百度收录已开启");
		}
	}

	public static void baiduVerify() {
		boolean is_robot = Convert.strToBoolean(Play.configuration.getProperty("is.robots"), true);
		String path = Play.configuration.getProperty("trust.funds.path") + "/baidu_verify_646X364qq3.html";
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!is_robot) {
			renderBinary(is);
		} else {
			renderText("百度收录已开启");
		}
	}

	public static void sogousiteverification() {
		boolean is_robot = Convert.strToBoolean(Play.configuration.getProperty("is.robots"), true);
		String path = Play.configuration.getProperty("trust.funds.path") + "/sogousiteverification.txt";
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!is_robot) {
			renderBinary(is);
		} else {
			renderText("搜狗已开启");
		}
	}

	// lvweihuan
	public static void honor2Pre() {
		render();
	}

	public static void honorPre() {
		render();
	}

	// lvweihuan
	/**
	 * 去企业风貌 niu
	 */
	public static void enterStylePre() {
		List<t_company_style> companyStyles = companyStyleService.findAll();

		if (companyStyles.size() >= 1) {
			t_company_style comStyle = companyStyles.get(0);
			renderArgs.put("comStyle", comStyle);
		}

		render(companyStyles);
	}

	public static void employeePre() {

		List<t_company_style> companyStyles = companyStyleService.findAll();

		if (companyStyles.size() >= 1) {
			t_company_style comStyle = companyStyles.get(0);
			renderArgs.put("comStyle", comStyle);
		}

		render(companyStyles);
	}

	public static void changeCityPre(long ids) {
		ResultInfo error = new ResultInfo();

		t_company_style comStyle = companyStyleService.findByID(ids);

		error.code = 1;
		error.msg = comStyle.employee_pic;

		renderJSON(error);
	}

	public static void changeCitysPre(long ids) {
		ResultInfo error = new ResultInfo();

		t_company_style comStyle = companyStyleService.findByID(ids);

		error.code = 1;
		error.msg = comStyle.enter_pic;

		renderJSON(error);
	}

	/* zhixin */
	public static void zhixinPre() {
		render();
	}

	public static void ouvrirUnComptePre() {
		render();
	}

	public static void risqueEvaluationPre() {
		render();
	}

	
	public static void sainPre() {
		render();
	}

	public static void lecturePre() {
		render();
	}
	public static void riskHintPre(Integer no) {
		render(no);
	}
	public static void policyRegulationsPre() {
		render();
	}
	public static void constitutionPre() {
		render();
	}
	public static void financePre() {
		render();
	}
	public static void debitPre() {
		render();
	}
	public static void lendingGuidelinesPre() {
		render();
	}
	public static void interimProceduresPre() {
		render();
	}
	public static void recordsPre() {
		render();
	}
	/*共享分页*/
	public static void sharedInformationPre(Integer no) {
		render(no);
	}
	/**
	 * 风险评估计算
	 * 
	 * @author LiuPengwei
	 */
	public static void risqueEvaluation(int Sex1,int Sex2, int Sex3,int Sex4,
			int Sex5,int Sex6,int Sex7,int Sex8,int Sex9,int Sex10,int Sex11,
			int Sex12,int Sex13, int Sex14, int Sex15, int Sex16) {
		
		
		//总得分
		int total = Sex1+Sex2+Sex3+Sex4+Sex5+Sex6+Sex7+Sex8+Sex9+Sex10+Sex11+Sex12
				+Sex13+Sex14+Sex15+Sex16;
		String res = "";
		int grade = 0;
		if (total <= 27 && total >= 1) {
			 grade = 1;
			res= "评测结果:保守型 低于27分 属于可以承担低风险而作风谨慎类型的投资者.您适合投资于保本为主的投资工具,但您因此会牺牲资本升值的机会" ;
		}else if ( 28<=total && total<=39) {
			res = "评测结果:稳健型 28—39分 属于可以承担低至中等风险类型的投资者.您适合投资于能够权衡保本而亦有若干升值能力的投资工具" ;
			grade = 2;
		}else if (40 <= total && total <= 51) {
			res = "评测结果:成长型 40—51分 属于可以承担中等至高风险类型的投资者.您适合投资于能够为您提供升值能力,而投资价值有波动的投资工具";
			grade = 3;
		}else if (total >= 52){
			res = "评测结果:进取型 大于52分 属于可以承受高风险类型的投资者.适合投资于能够为您提供升值能力而投资价值波动大的投资工具.最坏的情况下,可能失去全部投资本金并需对您投资所导致的任何亏损承担责任" ;
			grade = 4;
		}else {
			res ="您还未参与评测，请点击确定开始" ;
			grade = 0;
		}
		Map map =new TreeMap<String, String>();
		
		CurrUser currUser = getCurrUser();
		
		if(currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = currUser.id;

		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		
		if (userInfo.credit_id > 0){
			map.put("res", "用户已测评成功！");
			map.put("grade", 0);
			renderJSON(map);
		}
		
		boolean flag = userInfoService.userCredit(userId, grade);				
		
		if (!flag) {
			map.put("res", "填写出错，请重新测评！");
			map.put("grade", 0);
			renderJSON(map);
		}
			
		map.put("res", res);
		map.put("grade", grade);
		renderJSON(map);
	}
	
	public static void medicalExaminationPre() {
		
		render();
	}
	
	public static void sitemapPre() {
		render();
	}
	
}
