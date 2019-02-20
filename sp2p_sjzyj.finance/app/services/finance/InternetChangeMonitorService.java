package services.finance;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.FinanceExcelUtils;
import common.utils.ResultInfo;
import daos.finance.InternetChangeMonitorDao;
import jobs.IndexStatisticsJob;
import models.finance.entity.t_internet_change_monitor;
import services.base.BaseService;

/**
 * 重点对象（整改类）整改落实进展监测明细表Service
 * @createDate 2018.10.22
 * */
public class InternetChangeMonitorService extends BaseService<t_internet_change_monitor>{

	protected InternetChangeMonitorDao internetChangeMonitorDao = Factory.getDao(InternetChangeMonitorDao.class);
	
	protected InternetChangeMonitorService () {
		super.basedao = this.internetChangeMonitorDao;
	}
	
	/**
	 * 添加数据 
	 * 
	 * @param category 所属领域
	 * @param agencyName 机构名称
	 * @param name D平台网址（或者APP名称）
	 * @param date 存量不合规业务计划退出时间
	 * @param riskDegree 风险程度
	 * @param riskExpress 风险主要表现
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.24
	 * */
	public Boolean addInternetChangeMonitor (Date createDate ,String category , String agencyName ,String name , String date ,Integer manageStatus , Integer changeStatus , String source , String workPress) {
		
		return internetChangeMonitorDao.addInternetChangeMonitor (createDate,category, agencyName, name, date, manageStatus, changeStatus,source,workPress);
	}
	
	/**
	 * 数据更新
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.24
	 * */
	public ResultInfo updateInternetChangeMonitor (Date date) {
		ResultInfo result = new ResultInfo();
		String dateString = DateUtil.dateToString(date, "yyyy-MM");
		t_internet_change_monitor monitor = internetChangeMonitorDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", dateString );
		
		if (monitor == null) {
			result.code = -1;
			result.msg = "未添加不能修改";
			
			return result;
		}
		monitor = internetChangeMonitorDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", dateString );
		String amount = IndexStatisticsJob.totalBillAmount.replace(",", "");
		
		BigDecimal amount1= new BigDecimal(amount).divide(new BigDecimal("100000000"),3,BigDecimal.ROUND_HALF_UP);
		
		/**F4整改初始机构存量规模（亿元）*/
		monitor.initial_scale = new BigDecimal("0.88");
		/**F5整改初始机构存量不合规业务规模（亿元）*/
		monitor.initial_unqualified_scale = new BigDecimal("0.085");
		/**G1月末机构存量规模（亿元）*/
		monitor.end_month_scale = amount1;
		
		String lateDateString = DateUtil.dateToString(DateUtil.addMonth(date, -1), "yyyy-MM");
		t_internet_change_monitor lastMonitor = internetChangeMonitorDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", lateDateString );
		
		if (lastMonitor != null) {
			/**G2当月存量规模变化（亿元）*/
			monitor.current_month_scale_change = amount1.subtract(lastMonitor.end_month_scale);
			/**G3整改以来存量规模变化（亿元）*/
			monitor.scale_change = lastMonitor.scale_change.add(monitor.current_month_scale_change);
		} else {
			/**G2当月存量规模变化（亿元）*/
			monitor.current_month_scale_change = new BigDecimal("0.0");
			/**G3整改以来存量规模变化（亿元）*/
			monitor.scale_change = new BigDecimal("0.0");
		}
		
		
		/**H1月末机构存量不合规业务规模（亿元）*/
		monitor.end_month_unqualified_scale = new BigDecimal("0.0");
		
		/**H2当月存量不合规业务变化（亿元）*/
		monitor.current_month_unqualified_scale = new BigDecimal("0.0");
		
		/**H3整改以来存量不合规业务规模变化（亿元）*/
		monitor.unqualified_scale_change = new BigDecimal("-0.085");
		
		/**I是否新增不合规业务*/
		monitor.is_new_unqualified_service = "否";
		
		/**J机构经营状态1---正在经营*/
		monitor.manage_status = 1;
		
		/**K整改状态1-正在整改*/
		monitor.change_status = 1;
		
		/**L风险程度------1高；2中；3低；*/
		monitor.risk_degree = "本列由各县（市，区）整治办填写（评价依据为规模大小，不合规业务情况，清整工作开展与企业配合情况等）";
		
		/**N风险主要表现*/
		monitor.risk_express = "本列由各县（市，区）整治办填写（根据L风险程度列情况注明具体原因）";
		
		Boolean flag = internetChangeMonitorDao.save(monitor);
		if (flag) {
			result.code = 1;
			result.msg = "t_internet_change_monitor表 修改数据成功";
			result.obj = monitor;
			return result;
		}
		
		result.code = -2;
		result.msg = "t_internet_change_monitor表 修改数据失败";
		return result;
		
	}
	
	/**
	 * 生成excel文件
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
	 * @createDate 2018.10.26
	 * */
	public File createInternetChangeMonitorExcel (Date date) {
		t_internet_change_monitor change = internetChangeMonitorDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", DateUtil.dateToString( date , "yyyy-MM" ));
		if (change == null) {
			this.addInternetChangeMonitor(date,"P2P", "石家庄菲尔德投资咨询有限公司", "讴业普惠", "2017.12.01", 1, 1,"重点对象", "提交整改计划");
		}
		this.updateInternetChangeMonitor(date);
		
		List<String [] > list = new ArrayList<String []>();
		
		String [] s1 = new String [] {"1,1,0,20","重点对象（整改类）整改落实进展监测明细表","1,0","m","18","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s2 = new String [] {"0,0,0,20","附件3","0,0","l","15","500,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s3 = new String [] {"2,3,0,0","A编号","2,0","m","8","300,1200","border",HSSFColor.BLACK.index+""};
		String [] s4 = new String [] {"2,3,1,1","B所属领域","2,1","m","8","300,1000","border",HSSFColor.BLACK.index+""};
		String [] s5 = new String [] {"2,3,2,2","C机构名称","2,2","m","8","300,2000","border",HSSFColor.BLACK.index+""};
		String [] s6 = new String [] {"2,3,3,3","D平台网址（或APP名称）","2,3","m","8","300,1500","border",HSSFColor.BLACK.index+""};
		String [] s7 = new String [] {"2,3,4,4","E来源","2,4","m","8","300,1200","border",HSSFColor.BLACK.index+""};
		String [] s8 = new String [] {"2,2,5,9","F整治工作推进情况","2,5","m","8","300,1500","border",HSSFColor.BLACK.index+""};
		String [] s9 = new String [] {"","F1工作进展","3,5","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s10 = new String [] {"","F2存量不合规业务计划退出时间","3,6","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s11 = new String [] {"","F3负责监督整改的部门","3,7","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s12 = new String [] {"","F4整改初始机构存量规模（亿元）","3,8","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s13 = new String [] {"","F5整改初始机构存量不合规业务规模（亿元）","3,9","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s14 = new String [] {"2,2,10,12","G存量规模变化情况","2,10","m","8","300,1500","border",HSSFColor.BLACK.index+""};
		String [] s15 = new String [] {"","G1月末机构存量规模（亿元）","3,10","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s16 = new String [] {"","G2当月存量规模变化（亿元）","3,11","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s17 = new String [] {"","G3整改以来存量规模变化（亿元）","3,12","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s18 = new String [] {"2,2,13,15","H存量不合规业务变化情况","2,13","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s19 = new String [] {"","H1月末机构存量不合规业务规模（亿元）","3,13","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s20 = new String [] {"","H2当月存量不合规业务变化（亿元）","3,14","m","8","2100,1500","border",HSSFColor.BLACK.index+""};
		String [] s21 = new String [] {"","H3整改以来存量不合规业务规模变化（亿元）","3,15","m","8","2100,1900","border",HSSFColor.BLACK.index+""};
		String [] s22 = new String [] {"2,3,16,16","I是否新增不合规业务","2,16","m","8","300,1000","border",HSSFColor.BLACK.index+""};
		String [] s23 = new String [] {"2,3,17,17","J机构经营状态","2,17","m","8","300,1000","border",HSSFColor.BLACK.index+""};
		String [] s24 = new String [] {"2,3,18,18","K整改状态","2,18","m","8","300,1000","border",HSSFColor.BLACK.index+""};
		String [] s25 = new String [] {"2,3,19,19","L风险程度（高/中/低）","2,19","m","8","300,4000","border",HSSFColor.BLACK.index+""};
		String [] s26 = new String [] {"2,3,20,20","N风险表现","2,20","m","8","300,4000","border",HSSFColor.BLACK.index+""};
		
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
		list.add(s25);
		list.add(s26);
		
		if (change != null) {
			String [] s27 = new String [] {"",change.number == null ? "" : change.number,"4,0","m","8","2000,1200,","border",HSSFColor.BLACK.index+""};
			String [] s28 = new String [] {"",change.category == null ? "" : change.category,"4,1","m","8","2000,1000","border",HSSFColor.BLACK.index+""};
			String [] s29 = new String [] {"",change.name == null ? "" : change.name,"4,3","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s30 = new String [] {"",change.source == null ? "" : change.source,"4,4","m","8","2000,1200","border",HSSFColor.BLACK.index+""};
			String [] s31 = new String [] {"",change.work_progress == null ? "" : change.work_progress,"4,5","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s32 = new String [] {"",change.out_time == null ? "" : change.out_time,"4,6","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s33 = new String [] {"",change.super_department == null ? "" : change.super_department,"4,7","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s34 = new String [] {"",change.initial_scale == null ? "" : change.initial_scale+"","4,8","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s35 = new String [] {"",change.initial_unqualified_scale == null ? "" : change.initial_unqualified_scale+"","4,9","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s36 = new String [] {"",change.end_month_scale == null ? "" : change.end_month_scale+"","4,10","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s37 = new String [] {"",change.current_month_scale_change == null ? "" : change.current_month_scale_change+"","4,11","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s38 = new String [] {"",change.scale_change == null ? "" : change.scale_change+"","4,12","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s39 = new String [] {"",change.end_month_unqualified_scale == null ? "" : change.end_month_unqualified_scale+"","4,13","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s40 = new String [] {"",change.current_month_unqualified_scale == null ? "" : change.current_month_unqualified_scale+"","4,14","m","8","2000,1500","border",HSSFColor.BLACK.index+""};
			String [] s41 = new String [] {"",change.unqualified_scale_change == null ? "" : change.unqualified_scale_change+"","4,15","m","8","2000,1900","border",HSSFColor.BLACK.index+""};
			String [] s42 = new String [] {"",change.is_new_unqualified_service == null ? "" : change.is_new_unqualified_service,"4,16","m","8","2000,1000","border",HSSFColor.BLACK.index+""};
			String [] s43 = new String [] {"",(change.manage_status == 1 ? "正在经营" : "")+"","4,17","m","8","2000,1000","border",HSSFColor.BLACK.index+""};
			String [] s44 = new String [] {"",(change.change_status == 1 ? "正在整改" : "")+"","4,18","m","8","2000,1000","border",HSSFColor.BLACK.index+""};
			String [] s45 = new String [] {"",change.risk_degree == null ? "" : change.risk_degree,"4,19","m","7","2000,3500","border",HSSFColor.BLACK.index+""};
			String [] s66 = new String [] {"",change.agency_name == null ? "" : change.agency_name,"4,2","m","8","2000,2000","border",HSSFColor.BLACK.index+""};
			String [] s67 = new String [] {"",change.risk_express == null ? "" : change.risk_express,"4,20","m","7","2000,3500","border",HSSFColor.BLACK.index+""};
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
			list.add(s42);
			list.add(s43);
			list.add(s44);
			list.add(s45);
			list.add(s66);
			list.add(s67);
		}
		String [] s46 = new String [] {"","","1,1","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s47 = new String [] {"","","1,2","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s48 = new String [] {"","","1,3","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s49 = new String [] {"","","1,4","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s50 = new String [] {"","","1,5","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s51 = new String [] {"","","1,6","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s52 = new String [] {"","","1,7","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s53 = new String [] {"","","1,8","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s54 = new String [] {"","","1,9","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s55 = new String [] {"","","1,10","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s56 = new String [] {"","","1,11","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s57 = new String [] {"","","1,12","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s58 = new String [] {"","","1,13","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s59 = new String [] {"","","1,14","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s60 = new String [] {"","","1,15","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s61 = new String [] {"","","1,16","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s62 = new String [] {"","","1,17","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s63 = new String [] {"","","1,18","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s64 = new String [] {"","","1,19","","10","","noBorder",HSSFColor.BLACK.index+""};
		String [] s65 = new String [] {"","","1,20","","10","","noBorder",HSSFColor.BLACK.index+""};
		
		list.add(s46);
		list.add(s47);
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
		
		return FinanceExcelUtils.createExcel("重点对象（整改类）整改落实进展监测明细表", 11, 21, list);
		
	}
}
