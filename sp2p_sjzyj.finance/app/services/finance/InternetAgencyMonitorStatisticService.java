package services.finance;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.FinanceExcelUtils;
import common.utils.MapUtils;
import common.utils.ResultInfo;
import daos.finance.InternetAgencyMonitorStatisticDao;
import jobs.IndexStatisticsJob;
import models.finance.entity.t_internet_agency_monitor_statistic;
import services.base.BaseService;

/**
 * 重点机构流动行缺口监测统计表Service
 * @author guoShiJie
 * @createDate 2018.10.22
 * */
public class InternetAgencyMonitorStatisticService extends BaseService<t_internet_agency_monitor_statistic>{

	protected InternetAgencyMonitorStatisticDao internetAgencyMonitorStatisticDao = Factory.getDao(InternetAgencyMonitorStatisticDao.class);
	
	protected InternetAgencyMonitorStatisticService () {
		super.basedao = this.internetAgencyMonitorStatisticDao;
	}
	
	/**
	 * 添加数据 
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Boolean addInternetAgencyMonitorStatistic (Date date) {
		
		return internetAgencyMonitorStatisticDao.addInternetAgencyMonitorStatistic(date);
	}
	
	/**
	 * 数据查询 未来多个月 存量 资产段（应收回金额，亿元）预计
	 * 
	 * @param month 几个月
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Double queryRecoverAmount (Integer month,Date date) {
		Double result = 0.0;
		for(int i = 1; i <= month ; i++) {
			Date dateTime = DateUtil.addMonth(date , i);
			result += internetAgencyMonitorStatisticDao.queryRecoverAmount(dateTime);
		}
		
		return result;
	}
	
	/**
	 * 更新数据 t_internet_agency_monitor_statistic
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public ResultInfo updateInternetAgencyMonitorStatistic (Date datetime) {
		String date = DateUtil.dateToString(datetime, "yyyy-MM");
		
		ResultInfo result = new ResultInfo ();
		
		t_internet_agency_monitor_statistic statistic = internetAgencyMonitorStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", date );
		if (statistic == null) {
			result.code = -2;
			result.msg = "没有该数据，不能修改";
			return result;
			
		}
		String amount = IndexStatisticsJob.totalBillAmount.replace(",", "");
		statistic.recover_amount = new BigDecimal(amount).divide( new BigDecimal("100000000") ,4,BigDecimal.ROUND_HALF_UP);
		statistic.invest_amount = new BigDecimal(amount).divide( new BigDecimal("100000000") ,4,BigDecimal.ROUND_HALF_UP);
		statistic.one_month_estimate_gap = new BigDecimal("0.0");
		statistic.two_month_estimate_gap = new BigDecimal("0.0");
		statistic.n_estimate_gap = new BigDecimal("0.0");
		statistic.n_estimate_invest_amount = new BigDecimal(amount).divide( new BigDecimal("100000000") ,4,BigDecimal.ROUND_HALF_UP);
		statistic.n_estimate_recover_amount = new BigDecimal(amount).divide( new BigDecimal("100000000") ,4,BigDecimal.ROUND_HALF_UP);
		
		statistic.one_month_estimate_invest_amount = new BigDecimal(this.queryRecoverAmount(1,datetime)).setScale(4,BigDecimal.ROUND_HALF_UP);
		statistic.one_month_estimate_recover_amount = new BigDecimal(this.queryRecoverAmount(1,datetime)).setScale(4,BigDecimal.ROUND_HALF_UP);
		statistic.two_month_estimate_invest_amount = new BigDecimal(this.queryRecoverAmount(2,datetime)).setScale(4,BigDecimal.ROUND_HALF_UP);
		statistic.two_month_estimate_recover_amount = new BigDecimal(this.queryRecoverAmount(2,datetime)).setScale(4,BigDecimal.ROUND_HALF_UP);
		
		Boolean flag = internetAgencyMonitorStatisticDao.save(statistic);
		if (!flag) {
			result.code = -1;
			result.msg = "更新数据失败!!!";
			return result;
		}
		
		result.code = 1;
		result.msg = "更新数据成功!!!";
		
		return result;
	}
	
	/**
	 * 生成Excel文件
	 * 
	 * @param date 日期
	 * @param String [] 对string数组的值的解释
	 * @exception
	 * 0---合并单元格 ;格式: "起始行,结束行,起始列,结束列"
	 * 1---单元格内容值;
	 * 2---单元格位置;格式: "行,列"
	 * 3---对齐; l-左对齐;m-居中对齐;r-右对齐;
	 * 4---字号;
	 * 5---行高和列宽; 格式: "行高,列宽"
	 * 6---有无边框; 有边框-border;无边框-noBorder;
	 * 7---文字颜色; short类型值
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.25
	 * */
	public File createInternetAgencyMonitorStatisticExcel (Date date) {
		
		t_internet_agency_monitor_statistic monitor = internetAgencyMonitorStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", DateUtil.dateToString(date, "yyyy-MM"));
		if (monitor == null) {
			this.addInternetAgencyMonitorStatistic(date);
		}
		
		this.updateInternetAgencyMonitorStatistic(date);
		
		List<String []> list = new ArrayList<String[]>();
		String [] s1 = new String [] {"0,0,0,16","附件5","0,0","l","14","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s2 = new String [] {"1,1,0,16","重点机构流动性缺口监测统计表","1,0","m","15","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s3 = new String [] {"2,3,0,0","A编号","2,0","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s4 = new String [] {"2,3,1,1","B所属领域","2,1","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s5 = new String [] {"2,3,2,2","C机构名称","2,2","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s6 = new String [] {"2,3,3,3","D来源","2,3","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s7 = new String [] {"2,3,4,4","E风险预判","2,4","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s8 = new String [] {"2,3,5,5","F拟采取措施","2,5","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s9 = new String [] {"2,2,6,7","G当前存量情况","2,6","m","11","600,2200","border",HSSFColor.BLACK.index+""};
		String [] s10 = new String [] {"","G1资产段（应收回金额，亿元）","3,6","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s11 = new String [] {"","G2资金端（应兑付金额，亿元）","3,7","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s12 = new String [] {"2,2,8,10","H未来1个月当月资金流量","2,8","m","11","600,2200","border",HSSFColor.BLACK.index+""};
		String [] s13 = new String [] {"","H1预计收回金额（亿元）","3,8","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s14 = new String [] {"","H2预计兑付金额（亿元）","3,9","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s15 = new String [] {"","H3预计缺口（亿元）","3,10","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s16 = new String [] {"2,2,11,13","未来2个月当月资金流量","2,11","m","11","600,2200","border",HSSFColor.BLACK.index+""};
		String [] s17 = new String [] {"","预计收回金额（亿元）","3,11","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s18 = new String [] {"","预计兑付金额（亿元）","3,12","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s19 = new String [] {"","预计缺口（亿元）","3,13","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s20 = new String [] {"2,2,14,16","未来第N个月当月资金流量","2,14","m","11","600,2200","border",HSSFColor.BLACK.index+""};
		String [] s21 = new String [] {"","预计收回金额（亿元）","3,14","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s22 = new String [] {"","预计兑付金额（亿元）","3,15","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s23 = new String [] {"","预计缺口（亿元）","3,16","m","10","1500,2200","border",HSSFColor.BLACK.index+""};
		String [] s24 = new String [] {"","","","m","11","600,1500","border",HSSFColor.BLACK.index+""};
		if (monitor != null) {
			String [] s25 = new String [] {"","","4,0","","","600,1500","border",HSSFColor.BLACK.index+""};
			String [] s26 = new String [] {"",monitor.category == null ? "" : monitor.category,"4,1","m","11","600,1500","border",HSSFColor.BLACK.index+""};
			String [] s27 = new String [] {"",monitor.agency_name == null ? "" : monitor.agency_name,"4,2","m","11","600,1500","border",HSSFColor.BLACK.index+""};
			String [] s28 = new String [] {"",monitor.source == null ? "" : monitor.source,"4,3","m","11","600,1500","border",HSSFColor.BLACK.index+""};
			String [] s29 = new String [] {"",monitor.risk_prejudgement == null ? "" : monitor.risk_prejudgement,"4,4","m","11","600,1500","border",HSSFColor.BLACK.index+""};
			String [] s30 = new String [] {"",monitor.take_steps == null ? "" : monitor.take_steps,"4,5","m","11","600,1500","border",HSSFColor.BLACK.index+""};
			
			String [] s31 = new String [] {"",monitor.recover_amount == null ? "" : monitor.recover_amount+"","4,6","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s32 = new String [] {"",monitor.invest_amount == null ? "" : monitor.invest_amount+"","4,7","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s33 = new String [] {"",monitor.one_month_estimate_recover_amount == null ? "" : monitor.one_month_estimate_recover_amount+"","4,8","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s34 = new String [] {"",monitor.one_month_estimate_invest_amount == null ? "" : monitor.one_month_estimate_invest_amount+"","4,9","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s35 = new String [] {"",monitor.one_month_estimate_gap == null ? "" : monitor.one_month_estimate_gap+"","4,10","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s36 = new String [] {"",monitor.two_month_estimate_recover_amount == null ? "" : monitor.two_month_estimate_recover_amount+"","4,11","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s37 = new String [] {"",monitor.two_month_estimate_invest_amount == null ? "" : monitor.two_month_estimate_invest_amount+"","4,12","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s38 = new String [] {"",monitor.two_month_estimate_gap == null ? "" : monitor.two_month_estimate_gap+"","4,13","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s39 = new String [] {"",monitor.n_estimate_recover_amount == null ? "" : monitor.n_estimate_recover_amount+"","4,14","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s40 = new String [] {"",monitor.n_estimate_invest_amount == null ? "" : monitor.n_estimate_invest_amount+"","4,15","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			String [] s41 = new String [] {"",monitor.n_estimate_gap == null ? "" : monitor.n_estimate_gap+"","4,16","m","11","600,2200","border",HSSFColor.BLACK.index+""};
			list.add(s25);
			list.add(s26);
			list.add(s27);
			list.add(s28);
			list.add(s29);
			list.add(s30);
			list.add(s31);
			list.add(s32);
			list.add(s33);
			list.add(s34);
			list.add(s35);
			list.add(s36);
			list.add(s37);
			list.add(s38);
			list.add(s39);
			list.add(s40);
			list.add(s41);
		}
		String [] s42 = new String [] {"","合计","13,0","m","12","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s43 = new String [] {"14,14,0,16","","14,0","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s44 = new String [] {"15,15,0,16","备注：1.【A编号】：建议对辖内整改类机构进行分领域编号，整改期间保持“一企一号”，以便于对应。如P2P网贷机构变为A001，股权众筹机构B001等。","15,0","l","10","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s45 = new String [] {"16,16,0,16","2.【D来源】栏填写“重点对象”，“非重点对象” ","16,0","l","10","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s46 = new String [] {"17,17,0,16","3.【E风险预判】是指近期风险程度，以及预计风险较高的时间段。","17,0","l","10","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s47 = new String [] {"18,18,0,16","4.【未来第N个月当月资金流量】需根据机构整改计划中提供的存量不合规业务退出时间确定，原则上在退出前的计划安排均需提供。此外需特别注意的是，该项中提供的均为当月资金流量情况。","18,0","l","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		list.add(s1);
		list.add(s2);
		list.add(s3);
		list.add(s4);
		list.add(s5);
		list.add(s6);
		list.add(s7);
		list.add(s8);
		list.add(s9);
		list.add(s10);
		list.add(s11);
		list.add(s12);
		list.add(s13);
		list.add(s14);
		list.add(s15);
		list.add(s16);
		list.add(s17);
		list.add(s18);
		list.add(s19);
		list.add(s20);
		list.add(s21);
		list.add(s22);
		list.add(s23);
		list.add(s24);
		list.add(s42);
		list.add(s43);
		list.add(s44);
		list.add(s45);
		list.add(s46);
		list.add(s47);
		
		String [] s48 = new String [] {"","","1,1","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s49 = new String [] {"","","1,2","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s50 = new String [] {"","","1,3","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s51 = new String [] {"","","1,4","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s52 = new String [] {"","","1,5","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s53 = new String [] {"","","1,6","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s54 = new String [] {"","","1,7","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s55 = new String [] {"","","1,8","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s56 = new String [] {"","","1,9","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s57 = new String [] {"","","1,10","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s58 = new String [] {"","","1,11","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s59 = new String [] {"","","1,12","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s60 = new String [] {"","","1,13","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s61 = new String [] {"","","1,14","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s62 = new String [] {"","","1,15","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s63 = new String [] {"","","1,16","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s64 = new String [] {"","","14,1","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s65 = new String [] {"","","14,2","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s66 = new String [] {"","","14,3","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s67 = new String [] {"","","14,4","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s68 = new String [] {"","","14,5","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s69 = new String [] {"","","14,6","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s70 = new String [] {"","","14,7","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s71 = new String [] {"","","14,8","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s72 = new String [] {"","","14,9","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s73 = new String [] {"","","14,10","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s74 = new String [] {"","","14,11","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s75 = new String [] {"","","14,12","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s76 = new String [] {"","","14,13","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s77 = new String [] {"","","14,14","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s78 = new String [] {"","","14,15","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s79 = new String [] {"","","14,16","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s80 = new String [] {"","","15,1","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s81 = new String [] {"","","15,2","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s82 = new String [] {"","","15,3","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s83 = new String [] {"","","15,4","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s84 = new String [] {"","","15,5","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s85 = new String [] {"","","15,6","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s86 = new String [] {"","","15,7","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s87 = new String [] {"","","15,8","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s88 = new String [] {"","","15,9","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s89 = new String [] {"","","15,10","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s90 = new String [] {"","","15,11","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s91 = new String [] {"","","15,12","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s92 = new String [] {"","","15,13","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s93 = new String [] {"","","15,14","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s94 = new String [] {"","","15,15","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s95 = new String [] {"","","15,16","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s96 = new String [] {"","","16,1","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s97 = new String [] {"","","16,2","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s98 = new String [] {"","","16,3","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s99 = new String [] {"","","16,4","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s100 = new String [] {"","","16,5","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s101 = new String [] {"","","16,6","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s102 = new String [] {"","","16,7","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s103 = new String [] {"","","16,8","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s104 = new String [] {"","","16,9","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s105 = new String [] {"","","16,10","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s106 = new String [] {"","","16,11","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s107 = new String [] {"","","16,12","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s108 = new String [] {"","","16,13","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s109 = new String [] {"","","16,14","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s110 = new String [] {"","","16,15","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s111 = new String [] {"","","16,16","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s112 = new String [] {"","","17,1","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s113 = new String [] {"","","17,2","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s114 = new String [] {"","","17,3","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s115 = new String [] {"","","17,4","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s116 = new String [] {"","","17,5","l","12","370,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s117 = new String [] {"","","17,6","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s118 = new String [] {"","","17,7","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s119 = new String [] {"","","17,8","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s120 = new String [] {"","","17,9","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s121 = new String [] {"","","17,10","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s122 = new String [] {"","","17,11","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s123 = new String [] {"","","17,12","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s124 = new String [] {"","","17,13","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s125 = new String [] {"","","17,14","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s126 = new String [] {"","","17,15","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s127 = new String [] {"","","17,16","l","12","370,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s128 = new String [] {"","","18,1","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s129 = new String [] {"","","18,2","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s130 = new String [] {"","","18,3","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s131 = new String [] {"","","18,4","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s132 = new String [] {"","","18,5","l","12","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s133 = new String [] {"","","18,6","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s134 = new String [] {"","","18,7","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s135 = new String [] {"","","18,8","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s136 = new String [] {"","","18,9","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s137 = new String [] {"","","18,10","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s138 = new String [] {"","","18,11","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s139 = new String [] {"","","18,12","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s140 = new String [] {"","","18,13","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s141 = new String [] {"","","18,14","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s142 = new String [] {"","","18,15","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		String [] s143 = new String [] {"","","18,16","l","12","600,2200","noBorder",HSSFColor.BLACK.index+""};
		
		list.add(s48);
		list.add(s49);
		list.add(s50);
		list.add(s51);
		list.add(s52);
		list.add(s53);
		list.add(s54);
		list.add(s55);
		list.add(s56);
		list.add(s57);
		list.add(s58);
		list.add(s59);
		list.add(s60);
		list.add(s61);
		list.add(s62);
		list.add(s63);
		list.add(s64);
		list.add(s65);
		list.add(s66);
		list.add(s67);
		list.add(s68);
		list.add(s69);
		list.add(s70);
		list.add(s71);
		list.add(s72);
		list.add(s73);
		list.add(s74);
		list.add(s75);
		list.add(s76);
		list.add(s77);
		list.add(s78);
		list.add(s79);
		list.add(s79);
		list.add(s80);
		list.add(s81);
		list.add(s82);
		list.add(s83);
		list.add(s84);
		list.add(s85);
		list.add(s86);
		list.add(s87);
		list.add(s88);
		list.add(s89);
		list.add(s90);
		list.add(s91);
		list.add(s92);
		list.add(s93);
		list.add(s94);
		list.add(s95);
		list.add(s96);
		list.add(s97);
		list.add(s98);
		list.add(s99);
		list.add(s100);
		list.add(s101);
		list.add(s102);
		list.add(s103);
		list.add(s104);
		list.add(s105);
		list.add(s106);
		list.add(s107);
		list.add(s108);
		list.add(s109);
		list.add(s110);
		list.add(s111);
		list.add(s112);
		list.add(s113);
		list.add(s114);
		list.add(s115);
		list.add(s116);
		list.add(s117);
		list.add(s118);
		list.add(s119);
		list.add(s120);
		list.add(s121);
		list.add(s122);
		list.add(s123);
		list.add(s124);
		list.add(s125);
		list.add(s126);
		list.add(s127);
		list.add(s128);
		list.add(s129);
		list.add(s130);
		list.add(s131);
		list.add(s132);
		list.add(s133);
		list.add(s134);
		list.add(s135);
		list.add(s136);
		list.add(s137);
		list.add(s138);
		list.add(s139);
		list.add(s140);
		list.add(s141);
		list.add(s142);
		list.add(s143);
	
		return FinanceExcelUtils.createExcel("重点机构流动性缺口监测统计表", 19, 17, list );
	}
	
}
