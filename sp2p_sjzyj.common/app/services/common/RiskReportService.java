package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.RiskReportDao;
import models.common.entity.t_risk_report;
import services.base.BaseService;
/**
 * 
 * RiskReportService
 * @author lihuijun
 * @createDate 2017年4月27日
 */
public class RiskReportService extends BaseService<t_risk_report>{
	
	protected static RiskReportDao riskReportDao = Factory.getDao(RiskReportDao.class);
	
	protected RiskReportService(){
		super.basedao = riskReportDao;
	}
	
	/**
	 * 
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @createDate 2017年4月24日
	 * @author lihuijun
	 */
	public PageBean<t_risk_report> pageOfRiskReport(int showType, int currPage, int pageSize,int orderValue,String year,String month,String day,int exports){
		return riskReportDao.pageOfRiskReport(showType, currPage, pageSize,orderValue,year,month,day,exports);
	}
	
	/**
	 * 得到刚刚保存的那个风控报告
	 * @return
	 * @createDate 2017年4月26日
	 * @author lihuijun
	 */
	public t_risk_report getLastReport(){
		return riskReportDao.getLastReport();
	}
	/**
	 * 
	 * @Title: save
	 * 
	 * @description  保存数据后返回是否保存成功
	 * @param  r
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月27日-下午5:05:33
	 */
	public boolean save(t_risk_report r) {
		
		return riskReportDao.save(r);
	}
	
	/**
	 * 
	 * @Title: getReportId
	 * @description: 根据报单编号获取风控报告id
	 *
	 * @param entry_number
	 * @return    
	 * @return t_risk_report   
	 *
	 * @author HanMeng
	 * @createDate 2018年12月4日-下午3:49:56
	 */
	public t_risk_report getReportId(String entry_number){
		return riskReportDao.getReportId(entry_number);
		
	}
	/**
	 * 
	 * @Title: insert
	 * 
	 * @description 保存一条风控报告,返回当前插入的数据
	 * @param risk_report
	 * @return t_risk_report    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-上午10:30:16
	 */
	public t_risk_report insert(t_risk_report risk_report) {
		
		return riskReportDao.insert(risk_report);
	}
	
	/**
	 * 
	 * @Title: findByBidId
	 *
	 * @description 借贷人其他借贷情况在风控报告中添加一列，前台写活
	 *
	 * @param @param bidId
	 * @param @return 
	 * 
	 * @return t_risk_report    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月11日
	 */
	public t_risk_report findByBidId (Long bidId){
		
		return riskReportDao.findByBidId(bidId);
	}
}
