package services.finance;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.util.HSSFColor;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.FinanceExcelUtils;
import common.utils.ResultInfo;
import daos.finance.InternetChangeMonitorDao;
import daos.finance.InternetClearSummaryDao;
import models.finance.entity.t_internet_change_monitor;
import models.finance.entity.t_internet_clear_summary;
import services.base.BaseService;

/**
 * 清理整顿阶段工作进展情况汇总表Service
 * @createDate 2018.10.22
 * */
public class InternetClearSummaryService extends BaseService<t_internet_clear_summary>{

	protected InternetChangeMonitorDao internetChangeMonitorDao = Factory.getDao(InternetChangeMonitorDao.class);
	
	protected InternetChangeMonitorService internetChangeMonitorService = Factory.getService(InternetChangeMonitorService.class);
	
	protected InternetClearSummaryDao internetClearSummaryDao = Factory.getDao(InternetClearSummaryDao.class);
	
	protected InternetClearSummaryService () {
		super.basedao = this.internetClearSummaryDao;
	}
	
	/**
	 * 数据添加
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.24
	 * */
	public Boolean addInternetClearSummary (Date date ,t_internet_change_monitor change) {
		
		t_internet_clear_summary clearSummary = new t_internet_clear_summary ();
		if (change == null) {
			
			return false;
		}
		
		clearSummary.initial_scale = change.initial_scale;
		clearSummary.end_month_scale = change.end_month_scale;
		clearSummary.current_month_scale_change = change.current_month_scale_change;
		clearSummary.scale_change = change.scale_change;
		
		clearSummary.initial_unqualified_scale = change.initial_unqualified_scale;
		clearSummary.end_month_unqualified_scale = change.end_month_unqualified_scale;
		clearSummary.current_month_unqualified_scale = change.current_month_unqualified_scale;
		clearSummary.unqualified_scale_change = change.unqualified_scale_change;
		clearSummary.time = date;
		
		return internetClearSummaryDao.save(clearSummary);
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
	public File createInternetClearSummaryExcel (Date date) {
		
		t_internet_clear_summary summary = internetClearSummaryDao.findByColumn( " DATE_FORMAT( time , '%Y-%m' ) = ? " , DateUtil.dateToString(date, "yyyy-MM") );
		
		if (summary == null) {
			t_internet_change_monitor change = internetChangeMonitorService.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", DateUtil.dateToString(date, "yyyy-MM" ));
			if (change == null) {
				Boolean flag = internetChangeMonitorDao.addInternetChangeMonitor(date , "P2P", "石家庄菲尔德投资咨询有限公司", "讴业普惠", "2017.12.01", 1, 1,"重点对象","提交整改计划");
				if (flag) {
					internetChangeMonitorService.updateInternetChangeMonitor(date);
				}
			}
			change = internetChangeMonitorService.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", DateUtil.dateToString(date, "yyyy-MM" ));
			this.addInternetClearSummary(date,change);
		}
		
		summary = internetClearSummaryDao.findByColumn( " DATE_FORMAT( time , '%Y-%m' ) = ? " , DateUtil.dateToString(date, "yyyy-MM") );
		
		List<String []> list = new ArrayList<String []>();
		String [] s1 = new String [] {"1,1,0,7" , "清理整顿阶段工作进展情况汇总表" , "1,0" , "m" , "15" , "500,1200" , "noBorder",HSSFColor.BLACK.index+""};
		String [] s2 = new String [] {"0,0,0,7" , "附件2" , "0,0" , "l" , "12" ,"400,1200" ,"noBorder",HSSFColor.BLACK.index+""};
		String [] s3 = new String [] {"2,2,0,7" , "一、整改类机构整改落实进展情况" , "2,0" , "l" , "10" , "300,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s4 = new String [] {"3,3,0,1" , "" , "3,0" , "l" , "10" , "500,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s5 = new String [] {"" , "合计" , "3,2" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s6 = new String [] {"" , "P2P网贷" , "3,3" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s7 = new String [] {"" , "股权众筹" , "3,4" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s8 = new String [] {"" , "互联网保险" , "3,5" , "m" , "9", "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s9 = new String [] {"" , "非银行支付" , "3,6" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s10 = new String [] {"" , "互联网资管及跨界" , "3,7" , "m" , "9" , "500,2200" , "border",HSSFColor.BLACK.index+""};
		String [] s11 = new String [] {"4,4,0,1" , "机构数量" , "4,0" , "l" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s12 = new String [] {"5,6,0,0" , "来源" , "5,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s13 = new String [] {"" , "其中:状态分类阶段确定" , "5,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s14 = new String [] {"" , "状态分类阶段结束后新纳入" , "6,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s15 = new String [] {"7,13,0,0" , "工作进展" , "7,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s16 = new String [] {"" , "正在检查家数" , "7,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s17 = new String [] {"" , "完成现场检查家数" , "8,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s18 = new String [] {"" , "出具整改意见家数" , "9,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s19 = new String [] {"" , "提交整改计划家数" , "10,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s20 = new String [] {"" , "整改计划已获批准家数" , "11,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s21 = new String [] {"" , "整改初始机构存量规模（亿元）" , "12,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s22 = new String [] {"" , summary == null ? "" : summary.initial_scale == null ? "" : summary.initial_scale+"" , "12,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s23 = new String [] {"" , summary == null ? "" : summary.initial_scale == null ? "" : summary.initial_scale+"" , "12,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s24 = new String [] {"" , summary == null ? "" : summary.initial_unqualified_scale == null ? "" : summary.initial_unqualified_scale+"" , "13,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s25 = new String [] {"" , summary == null ? "" : summary.initial_unqualified_scale == null ? "" : summary.initial_unqualified_scale+"" , "13,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s26 = new String [] {"14,18,0,0" , "存量规模变化情况" , "14,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s27 = new String [] {"" , "月末机构存量规模（亿元）" , "14,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s28 = new String [] {"" , summary == null ? "" : summary.end_month_scale == null ? "" : summary.end_month_scale+"" , "14,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s29 = new String [] {"" , summary == null ? "" : summary.end_month_scale == null ? "" : summary.end_month_scale+"" , "14,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s30 = new String [] {"" , "当月存量规模变化（亿元）" , "15,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s31 = new String [] {"" , summary == null ? "" : summary.current_month_scale_change == null ? "" : summary.current_month_scale_change+"" , "15,2" , "r" , "9" , "250,2000" ,"border",HSSFColor.BLACK.index+""};
		String [] s32 = new String [] {"" , summary == null ? "" : summary.current_month_scale_change == null ? "" : summary.current_month_scale_change+"" , "15,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s33 = new String [] {"" , "整改以来存量规模变化（亿元）" , "16,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s34 = new String [] {"" , summary == null ? "" : summary.scale_change == null ? "" : summary.scale_change+"" , "16,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s35 = new String [] {"" , summary == null ? "" : summary.scale_change == null ? "" : summary.scale_change+"" , "16,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s36 = new String [] {"" , "当月存量规模增加家数" , "17,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s37 = new String [] {"" , "整改以来存量规模增加家数（亿元）" , "18,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s38 = new String [] {"19,24,0,0" , "不合规业务变化情况" , "19,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s39 = new String [] {"" , "月末机构存量不合规业务规模（亿元）" , "19,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s40 = new String [] {"" , summary == null ? "" : summary.end_month_unqualified_scale == null ? "" : summary.end_month_unqualified_scale+"" , "19,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s41 = new String [] {"" , summary == null ? "" : summary.end_month_unqualified_scale == null ? "" : summary.end_month_unqualified_scale+"" , "19,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s42 = new String [] {"" , "当月存量不合规业务变化（亿元）" , "20,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s43 = new String [] {"" , summary == null ? "" : summary.current_month_unqualified_scale == null ? "" : summary.current_month_unqualified_scale+"" , "20,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s44 = new String [] {"" , summary == null ? "" : summary.current_month_unqualified_scale == null ? "" : summary.current_month_unqualified_scale+"" , "20,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s45 = new String [] {"" , "整改以来存量不合规业务变化（亿元）" , "21,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s46 = new String [] {"" , summary == null ? "" : summary.unqualified_scale_change == null ? "" : summary.unqualified_scale_change+"" , "21,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s47 = new String [] {"" , summary == null ? "" : summary.unqualified_scale_change == null ? "" : summary.unqualified_scale_change+"" , "21,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s48 = new String [] {"" , "当月存量不合规业务增加家数" , "22,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s49 = new String [] {"" , "0" , "22,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s50 = new String [] {"" , "0" , "22,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s51 = new String [] {"" , "整改以来存量不合规业务增加家数" , "23,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s52 = new String [] {"" , "0" , "23,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s53 = new String [] {"" , "0" , "23,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s54 = new String [] {"" , "新增不合规业务家数" , "24,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s55 = new String [] {"" , "0" , "24,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s56 = new String [] {"" , "0" , "24,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s57 = new String [] {"25,26,0,0" , "经营状态" , "25,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s58 = new String [] {"" , "正常运营机构数量" , "25,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s59 = new String [] {"" , "1" , "25,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s60 = new String [] {"" , "1" , "25,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s61 = new String [] {"" , "停业机构数量" , "26,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s62 = new String [] {"" , "0" , "26,2" , "r" , "9" , "250,2000" , "border" ,HSSFColor.BLACK.index+""};
		String [] s63 = new String [] {"" , "0" , "26,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s64 = new String [] {"27,29,0,0" , "整改状态" , "27,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s65 = new String [] {"" , "正在整改机构数量" , "27,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s66 = new String [] {"" , "1" , "27,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s67 = new String [] {"" , "1" , "27,3" , "r" , "9", "250,2000" ,"border" ,HSSFColor.BLACK.index+""};
		String [] s68 = new String [] {"" , "已完成整改机构家数" , "28,1" , "l" , "9" ,"250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s69 = new String [] {"" , "0" , "28,2" , "r" , "9", "250,2000", "border",HSSFColor.BLACK.index+""};
		String [] s70 = new String [] {"" , "0" , "28,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s71 = new String [] {"" , "转入取缔类机构家数" , "29,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s72 = new String [] {"" , "0" , "29,2" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s73 = new String [] {"" , "0" , "29,3" , "r" , "9" , "250,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s74 = new String [] {"30,30,0,7" , "二、取缔类机构处置情况" , "30,0" , "l" , "10" , "300,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s75 = new String [] {"31,31,0,1" , "" , "31,0" , "" , "9" , "500,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s76 = new String [] {"" , "合计" , "31,2" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s77 = new String [] {"" , "P2P网贷" , "31,3" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s78 = new String [] {"" , "股权众筹" , "31,4" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s79 = new String [] {"" , "互联网保险" , "31,5" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s80 = new String [] {"" , "非银行支付" , "31,6" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s81 = new String [] {"" , "互联网资管及跨界" , "31,7" , "m" , "9" , "500,2200" , "border",HSSFColor.BLACK.index+""};
		String [] s82 = new String [] {"32,32,0,1" , "机构数量" , "32,0" , "l" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s83 = new String [] {"33,34,0,0" , "来源" , "33,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s84 = new String [] {"" , "状态分类阶段确定" , "33,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s85 = new String [] {"" , "整改类机构转入" , "34,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s86 = new String [] {"35,39,0,0" , "处置去向" , "35,0" , "m" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s87 = new String [] {"" , "转入处非机制" , "35,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s88 = new String [] {"" , "列入打击非法证券活动机制" , "36,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s89 = new String [] {"" , "公安部门" , "37,1" , "l" , "9" , "250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s90 = new String [] {"" , "工商部门" , "38,1" , "l" , "9" ,"250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s91 = new String [] {"" , "其他" , "39,1" , "l" , "9" ,"250,8900" , "border",HSSFColor.BLACK.index+""};
		String [] s92 = new String [] {"40,40,0,1" , "尚未完成交接的机构数量" , "40,0" , "l" , "9" , "250,1200" ,"border",HSSFColor.BLACK.index+""};
		String [] s93 = new String [] {"41,41,0,7" , "三、非重点对象抽查进展情况" , "41,0" , "l" , "9" ,"300,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s94 = new String [] {"42,42,0,1" , "" , "42,0" , "" , "" , "500,1200" ,"border",HSSFColor.BLACK.index+""};
		String [] s95 = new String [] {"" , "合计" , "42,2" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s96 = new String [] {"" , "P2P网贷" , "42,3" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s97 = new String [] {"" , "股权众筹" , "42,4" , "m" , "9" , "500,2000" ,"border",HSSFColor.BLACK.index+""};
		String [] s98 = new String [] {"" , "互联网保险" , "42,5" , "m" , "9" , "500,2000" , "border",HSSFColor.BLACK.index+""};
		String [] s99 = new String [] {"" , "非银行支付" , "42,6" , "m" , "9" ,"500,2000" ,"border",HSSFColor.BLACK.index+""};
		String [] s100 = new String [] {"" , "互联网资管及跨界" , "42,7" , "m" , "9" , "500,2200" , "border",HSSFColor.BLACK.index+""};
		String [] s101 = new String [] {"43,43,0,1" , "机构数量" , "43,0" , "l" , "9" ,"250,1200" ,"border",HSSFColor.BLACK.index+""};
		String [] s102 = new String [] {"44,44,0,1" , "其中:新发现的机构数量" , "44,0" , "l" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s103 = new String [] {"45,45,0,1" , "已完成的抽查数量" , "45,0" , "l" , "9" , "250,1200" ,"border",HSSFColor.BLACK.index+""};
		String [] s104 = new String [] {"46,46,0,1" , "当月完成抽查数量" , "46,0" , "l" , "9" , "250,1200" ,"border",HSSFColor.BLACK.index+""};
		String [] s105 = new String [] {"47,47,0,1" , "抽查后移入重点对象数量" , "47,0" , "l" , "9" , "250,1200" , "border",HSSFColor.BLACK.index+""};
		String [] s106 = new String [] {"","整改初始机构存量不合规业务规模（亿元）","13,1","l","9" , "250,8900" ,"border",HSSFColor.BLACK.index+""};
		
		String [] s107 = new String [] {"","","1,1","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s108 = new String [] {"","","1,2","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s109 = new String [] {"","","1,3","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s110 = new String [] {"","","1,4","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s111 = new String [] {"","","1,5","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s112 = new String [] {"","","1,6","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s113 = new String [] {"","","1,7","","8","","noBorder",HSSFColor.BLACK.index+""};
		String [] s137 = {"","","1,8","","8","","noBorder",HSSFColor.BLACK.index+""};
		
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
		list.add(s75);
		list.add(s76);
		list.add(s77);
		list.add(s78);
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
		list.add(s137);
		
		String [] s114 = {"","填写说明","2,8","m","10","250,6000","border",HSSFColor.RED.index+""};
		String [] s115 = {"","附件3中B（或C）合计","4,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s116 = {"5,6,8,8","对应附件3中E","5,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s117 = {"7,11,8,8","对应附件3中F1","7,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s118 = {"","对应附件3中F4","12,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s119 = {"","对应附件3中F5","13,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s120 = {"","对应附件3中G1合计数","14,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s121 = {"","对应附件3中G2合计数","15,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s122 = {"","对应附件3中G3合计数","16,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s123 = {"","对应附件3中G2为正的家数","17,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s124 = {"","对应附件3中G3为正的家数","18,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s125 = {"","对应附件3中H1合计数","19,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s126 = {"","对应附件3中H2合计数","20,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s127 = {"","对应附件3中H3合计数","21,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s128 = {"","对应附件3中H2为正的家数","22,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s129 = {"","对应附件3中H3为正的家数","23,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s130 = {"","对应附件3中I为是的家数","24,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s131 = {"25,26,8,8","对应附件3中J","25,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s132 = {"27,29,8,8","对应附件3中K","27,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s133 = {"","附件4中B（或C）合计","32,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s134 = {"33,34,8,8","对应附件4中E","33,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s135 = {"35,39,8,8","对应附件4中F","35,8","l","10","","border",HSSFColor.RED.index+""};
		String [] s136 = {"27,29,8,8","对应附件4中G","40,8","l","10","","border",HSSFColor.RED.index+""};
		
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
		
		return FinanceExcelUtils.createExcel("清理整顿阶段工作进展情况汇总表", 48, 9, list);
	}
}
