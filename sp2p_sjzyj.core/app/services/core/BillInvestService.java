package services.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import play.db.jpa.JPA;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.shove.net.Email;

import models.core.bean.BillInvest;
import models.core.bean.InvestReceive;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_bill_invest.Status;
import models.finance.entity.t_trade_info;
import services.base.BaseService;
import services.finance.TradeInfoService;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.BillInvestDao;

/**
 * 理财账单 业务实现类
 *
 * @author liudong
 * @createDate 2015年12月16日
 */
public class BillInvestService extends BaseService<t_bill_invest> {

	protected TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	
	protected BillInvestDao billInvestDao = Factory.getDao(BillInvestDao.class);
	
	protected BillInvestService() {
		super.basedao = billInvestDao;
	}

	
	
	/**
	 * 生成投资账单
	 *
	 * @param bid_id
	 * @param repayment_type
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月24日
	 */
	public ResultInfo createBillInvest(long bid_id, int repayment_type, ResultInfo res) {
		
		return billInvestDao.saveBillInvest(bid_id, repayment_type, res);
	}

	public boolean copyToBillInvest(long originalInvestId,long newUserId,long newInvestId){
		return billInvestDao.copyToBillInvest(originalInvestId,newUserId,newInvestId);
	}
	
	/**
	 * 理财人收到回款，更新理财账单回款数据（状态和实际回款时间）
	 *
	 * @param billInvestId 理财账单id
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public boolean updateReceiveData(long billInvestId) {
		int row = billInvestDao.updateStatusAndRealReceiveTime(billInvestId, t_bill_invest.Status.RECEIVED.code, new Date());
		if (row < 1) {
			LoggerUtil.error(true, "理财人收到回款，更新理财账单回款数据失败。[billInvestId:%s]", billInvestId);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 理财人收到回款，更新理财账单回款数据（状态和实际回款时间）
	 *
	 * @param billInvestId 理财账单id
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年10月05日
	 */
	public boolean updateReceiveData(long billInvestId,String businessSeqNo) {
		int row = billInvestDao.updateStatusAndRealReceiveTime(billInvestId, t_bill_invest.Status.RECEIVED.code, new Date(),businessSeqNo);
		if (row < 1) {
			LoggerUtil.error(true, "理财人收到回款，更新理财账单回款数据失败。[billInvestId:%s]", billInvestId);
			
			return false;
		}
		
		return this.addTradeInfoByBillId(billInvestId, businessSeqNo);
	}
	
	/**
	 * 债权转让完成，更新原来投资账单(未还的)的状态为'已转让'
	 *
	 * @param invested
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public boolean updateBillToTransfered(long invested){
		
		return billInvestDao.updateBillToTransfered(invested);
	}
	
	/**
	 * 判断某个投资是否有逾期记录
	 *
	 * @param investId 投资id
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月5日
	 */
	public boolean isInvestOverdue(long investId) {
		t_bill_invest bill = billInvestDao.findByColumn(" invest_id=? AND is_overdue=? ", investId,true);
		return bill != null;
	}
	
	/**
	 * 查询投资总额  
	 *
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还  4.已转让
	 * @param loanName 借款人昵称
	 * @param projectName 项目名称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findBillInvestTotalAmt(int showType, String loanName,String projectName) {
		return billInvestDao.findBillInvestTotalAmt(showType,loanName,projectName);
	}
	
	/**
	 * 查询所有的应收利息(也即给用户创造的收益，已还未还都算)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月23日
	 */
	public double findAllInterest() {
		
		return billInvestDao.findAllInterest();
	}
	
	/**
	 * 查询某个标记已还的本金
	 *
	 * @param investId 标记id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public double findHasReceivePrincipal(long investId){
		
		return billInvestDao.findHasReceivePrincipal(investId);
	}
	
	/**
	 * 查询某个投资的待还账单
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public int countPayingBill(Long investId){
		
		return billInvestDao.countByColumn(" invest_id=? AND status=? ", investId,models.core.entity.t_bill_invest.Status.NO_RECEIVE.code);
	}
	
	/**
	 * 查询某个投资的账单
	 *
	 * @param investId 
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
	public List<InvestReceive> queryMyBillInvest(long investId) {
		
		return billInvestDao.queryMyBillInvestFront(investId);
	}
	
	/**
	 * 分页查询 我的理财账单 
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数 
	 * @param userId 用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<t_bill_invest> pageOfMyBillInvest(int currPage,int pageSize, long userId) {
		
		return billInvestDao.pageOfMyBillInvest(currPage, pageSize, userId);
	}

	/**
	 * 分页查询 ,理财账单列表
	 *
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还   4.已转让
	 * @param currPage
	 * @param pageSize
	 * @param orderType 参与排序的栏目 0,按编号;3,账单金额;5,逾期时长;6,到期时间;7,回款时间
	 * @param orderValue 排序的类型:0,降序;1,升序
	 * @param exports 1:导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param projectName 项目
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public PageBean<BillInvest> pageOfBillInvestBack(int showType, int currPage, int pageSize,int orderType,int orderValue, int exports, String loanName, String projectName) {

		return billInvestDao.pageOfBillInvestBack(showType,currPage, pageSize,orderType,orderValue, exports, loanName, projectName);
	}
	
	/**
	 * 分页查询 回款计划
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月18日
	 *
	 */
	public PageBean<InvestReceive> pageOfInvestReceive(int currPage, int pageSize, long userId) {

		return billInvestDao.pageOfInvestReceive(currPage, pageSize, userId);
	}
	
	/**
	 * 分页查询理财账单
	 *
	 * @param currPage
	 * @param pageSize
	 * @param investId 
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public PageBean<InvestReceive> pageOfRepaymentBill(int currPage,
			int pageSize, long investId) {
		/*
		 SELECT
				tbi.id AS id,
				tbi.time AS time,
				tbi.user_id as user_id,
				tbi.receive_corpus AS receive_corpus,
				tbi.receive_interest AS receive_interest,
				(tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,
				tbi.invest_id AS invest_id,
				tbi.period AS period,
				tbi.receive_time AS receive_time,
				tbi.real_receive_time AS real_receive_time,
				tbi.status AS status,
				tb.service_fee_rule AS service_fee_rule,
				(SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod
			FROM
				t_bill_invest tbi,
				t_bid tb
			WHERE
				tbi.bid_id = tb.id
			AND invest_id =: investId
		 */
		String querySQL = "SELECT tbi.id AS id, tbi.time AS time,tbi.user_id as user_id, tbi.receive_corpus AS receive_corpus, tbi.receive_interest AS receive_interest, (tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.invest_id AS invest_id, tbi.period AS period, tbi.receive_time AS receive_time, tbi.real_receive_time AS real_receive_time, tbi.status AS status,tb.service_fee_rule AS service_fee_rule, (SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi, t_bid tb WHERE invest_id =:investId AND tbi.bid_id = tb.id ";
		String countSQL = "SELECT COUNT(tbi.id) FROM t_bill_invest tbi, t_bid tb WHERE invest_id =:investId AND tbi.bid_id = tb.id  ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		
		return billInvestDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, InvestReceive.class, condition);
	}
	
	/**
	 * 不分页查询 ，用户该笔投资的理财账单
	 *
	 * @param investId 投资Id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月20日
	 */
	public List<InvestReceive> queryMyBillInvestFront(long investId) {
		return billInvestDao.queryMyBillInvestFront(investId);
	}

	/**
	 * 查询待还的理财账单。根据借款标和借款账单期数
	 *
	 * @param bidId  标的id
	 * @param period  借款账单期数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public  List<t_bill_invest> queryNOReceiveInvestBills(long bidId, int period) {
		
		List<t_bill_invest> billInvestList=billInvestDao.findListByColumn("bid_id=? AND period=? AND status=?", bidId, period, t_bill_invest.Status.NO_RECEIVE.code);
		return billInvestList;
	}
	
	/**
	 * 查询用户待收金额
	 *
	 * @param userId
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public double getUserReceive(long userId) {
		
		return billInvestDao.findUserReceive(userId, Status.NO_RECEIVE.code);
	}
	
	/**
	 * 查询待还投资账单
	 * 
	 * @param bidId
	 * @return
	 * @createDate 2017年5月16日
	 * @author lihuijun
	 */
	public List<t_bill_invest> getBillInvestListByBidIdAndStatus(long bidId){
		
		return billInvestDao.findListByColumn("bid_id = ? and status = 0", bidId);
	}
	
	/**
	 * 查询提前还款待还投资账单
	 * 
	 * @param bidId
	 * @param bidId
	 * @return
	 * @createDate 2017年5月16日
	 * @author lihuijun
	 */
	public List<t_bill_invest> getBillInvestListByBidIdAndStatuss(long bidId, int zhi){
		
		return billInvestDao.findListByColumn("bid_id = ? and status = 0 and period>=?", bidId,zhi);
	}
	
	/**
	 * 查询逾期+当月的待还账单
	 * @param bidId
	 * @return
	 * @createDate 2017年5月18日
	 * @author lihuijun
	 */
	public List<t_bill_invest> getInvestOverduelist(long bidId){
		String sql="select * from t_bill_invest where bid_id = ? and status = 0 and (receive_time < date_sub(now(),interval -1 month)) ";
		return JPA.em().createNativeQuery(sql, t_bill_invest.class).setParameter(1, bidId).getResultList();
	}
	/**
	 * 查询一个月以后的待还账单
	 * @param bidId
	 * @return
	 * @createDate 2017年5月19日
	 * @author lihuijun
	 */
	public List<t_bill_invest> getAdvanceInvestList(long bidId){
		String sql="select * from t_bill_invest where bid_id = ? and status = 0 and (receive_time > date_sub(now(),interval -1 month)) ";
		return JPA.em().createNativeQuery(sql, t_bill_invest.class).setParameter(1, bidId).getResultList();
	}
	
	/**
	 * 查询某标某期投资账单
	 * 
	 * @param bidId
	 * @param period
	 * @return
	 * @createDate 2017年5月16日
	 * @author lihuijun
	 */
	public List<t_bill_invest> billInvestListByBidIdAndPeriod(long bidId,int period){
		
		return billInvestDao.findListByColumn("bid_id = ? and period = ?", bidId, period);
	}
	
	
	public void updateTheBillInvest(long billInvestId,double surplusCorpus,double surplusInterest,double surplusFine){
		billInvestDao.updateTheBillInvest(billInvestId,surplusCorpus,surplusInterest,surplusFine);
	}

	/**
	 * 更新数据
	 * @param billInvestId
	 * @createDate 2017年5月19日
	 * @author lihuijun
	 */
	public void updateThisBillInvest(long billInvestId){
		billInvestDao.updateThisBillInvest(billInvestId);
	}
	
	/**
	 * 查询待还出借人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findLenderCount() {
		
		return billInvestDao.findLenderCount();
	}
	
	/**
	 * 查询总出借人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findTotalLenderCount() {
		
		return billInvestDao.findTotalLenderCount();
	}
	
	/**
	 * 查询某个人待还总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTotalAmounts(long userId) {
		
		return billInvestDao.findTotalAmounts(userId);
	}
	
	/**
	 * 通过账单id查询数据并保存到交易信息表中
	 * @author guoShiJie
	 * @createDate 2018.10.18
	 * */
	public Boolean addTradeInfoByBillId (long billInvestId,String businessSeqNo ) {
		t_bill_invest billInvest = billInvestDao.findByID(billInvestId);
		if (billInvest == null ) {
			return false;
		}
		return tradeInfoService.saveTradeInfo(DateUtil.addDay(new Date(), 0), businessSeqNo, t_trade_info.TradeType.PAYMENT, billInvest.user_id, "CNY", new BigDecimal(billInvest.corpus_interest), billInvest.bid_id);
	}
}
