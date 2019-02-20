package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.app.bean.BillInvestInfo;
import models.app.bean.BillInvestListBean;
import models.app.bean.DealRecordsBean;
import models.app.bean.MyInvestRecordBean;
import models.app.bean.MyLoanRecordBean;
import models.app.bean.RechargeBean;
import models.app.bean.ReturnMoneyPlanBean;
import models.app.bean.UserBankCard;
import models.app.bean.WithdrawBean;
import models.common.entity.t_information;
import models.common.entity.t_user;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import common.enums.InformationMenu;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 账户到数据库的DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年4月5日
 */
public class AccountAppDao extends BaseDao<t_user> {
	
	/**
	 * 提现记录
	 *
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月5日
	 */
	public PageBean<WithdrawBean> pageOfWithdrawRecord(long userId, int currPage, int pageSize) {
		
		String querySQL = "SELECT id, status, time, amount, order_no AS orderNo, summary FROM t_withdrawal_user WHERE user_id=:userId ORDER BY id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_withdrawal_user WHERE user_id=:userId";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, WithdrawBean.class, conditionArgs);
	}
	
	/**
	 * 充值记录
	 *
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月5日
	 */
	public PageBean<RechargeBean> pageOfRechargeRecord(long userId, int currPage, int pageSize) {
		
		String querySQL = "SELECT id, status, time, amount, order_no AS orderNo, summary FROM t_recharge_user  WHERE user_id=:userId ORDER BY id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_recharge_user WHERE user_id=:userId";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, RechargeBean.class, conditionArgs);
	}
	
	/**
	 * 消息记录
	 *
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月5日
	 */
	public PageBean<Map<String, Object>> pageOfUserMessage(long userId, int currPage, int pageSize) {
		String querySQL = "SELECT m.title AS title, UNIX_TIMESTAMP(m.time)*1000 AS time, m.content AS content ,mu.is_read AS isRead FROM t_message m INNER JOIN t_message_user mu ON mu.message_id = m.id WHERE mu.user_id=:userId ORDER BY m.id DESC";
		String countSQL = "SELECT count(m.id) FROM t_message m INNER JOIN t_message_user mu ON mu.message_id = m.id WHERE mu.user_id=:userId";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		
		return super.pageOfMapBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}
	
	/***
	 * 
	 * 回款计划接口（OPT=242）
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public PageBean<ReturnMoneyPlanBean> pageOfInvestReceive(long userId, int currPage, int pageSize) {

		String sqlCount = "SELECT COUNT(1) FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.user_id =:user_id AND tbi.status =:status";
		String querySQL = " SELECT tbi.id AS billInvestId,tbi.id AS billInvestNo,tbi.id AS bid_no, tbi.time AS time, tbi.receive_time AS receiveTime,  tbi.user_id AS userId, tb.service_fee_rule AS serviceFeeRule, tbi.period AS period,(SELECT COUNT(id) FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod,  tbi.receive_corpus AS receiveCorpus, tbi.receive_interest AS receiveInterest, tbi.status AS status  FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.user_id =:user_id AND tbi.status =:status ORDER BY tbi.receive_time  ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		
		return super.pageOfBeanBySQL(currPage, pageSize, sqlCount, querySQL,ReturnMoneyPlanBean.class, condition);
	}
	
	/***
	 * 
	 * 我的投资接口（OPT=231）
	 * @param userId 
	 * @param investId 
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public PageBean<MyInvestRecordBean> pageOfMyInvestRecord(int currPage, int pageSize, long userId){
		
		String sql="SELECT tb.id AS id, ti.id AS investOriId, tb.title AS title,ti.amount AS amount, tb.apr AS apr,tb.period AS period, tb.period_unit AS periodUnit , tb.repayment_type AS repaymentType, tb.status AS status,  tb.release_time  AS time,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+") AND tbi.user_id =ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay FROM	t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId AND ti.debt_id = 0 AND ti.transfer_status = 0 ORDER BY ti.id DESC ";
		
		String sqlCount="SELECT COUNT(ti.id) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId AND ti.debt_id = 0 AND ti.transfer_status = 0";
	
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, MyInvestRecordBean.class, condition);
	}
	
	/**
	 * 
	 * @Title: pageOfMyInvestRecordNew
	 *
	 * @description 资产管理——>我的出借——已完成(OPT=231)根据标的状态(t_bid.status=5)
	 *
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @param userId
	 * @param @return 
	 * 
	 * @return PageBean<MyInvestRecordBean>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月28日
	 */
     public PageBean<MyInvestRecordBean> pageOfMyInvestRecordNewEnd(int currPage, int pageSize, long userId){
		
    	String sql="SELECT tb.id AS id,ti.time AS time,ti.user_id AS user_id,ti.bid_id AS bidId,ti.id AS investOriId,tb.title AS title,ti.amount AS amount,tb.apr AS apr,tb.period AS period,tb.period_unit AS periodUnit,tb.repayment_type AS repaymentType,tb. STATUS AS STATUS,tb.release_time AS release_time,tb.contract_id AS contract_id,(SELECT	COUNT(1) FROM t_bill_invest tbi	WHERE tbi.invest_id = ti.id AND tbi. STATUS IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+")AND tbi.user_id = ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM	t_bill_invest tbi	WHERE	tbi.invest_id = ti.id	AND tbi.user_id = ti.user_id) AS totalPay,(SELECT	t_pact.contract_id	FROM	t_pact	WHERE	pid = ti.bid_id	AND t_pact.user_id = ti.user_id) AS contract FROM	t_bid tb,	t_invest ti WHERE	tb.id = ti.bid_id AND ti.user_id =:userId AND tb. STATUS =5 AND ti.debt_id = 0 AND ti.transfer_status = 0 ORDER BY  ti.id DESC";
 		
    	String sqlCount="SELECT COUNT(1) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND tb. STATUS =5 AND ti.user_id =:userId and transfer_status=0";
		
    	Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, MyInvestRecordBean.class, condition);
	}
	
	/**
	 * 
	 * @Title: pageOfMyUnfinishedInvestRecord
	 *
	 * @description 资产管理——>我的出借——未完成(OPT=231)根据标的状态(t_bid.status<>5)
	 *
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @param userId
	 * @param @return 
	 * 
	 * @return PageBean<MyInvestRecordBean>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月28日
	 */
    public PageBean<MyInvestRecordBean> pageOfMyInvestRecordUnfinished(int currPage, int pageSize, long userId){
		
    	String sql="SELECT tb.id AS id,ti.time AS time,ti.user_id AS user_id,ti.bid_id AS bidId,ti.id AS investOriId,tb.title AS title,ti.amount AS amount,tb.apr AS apr,tb.period AS period,tb.period_unit AS periodUnit,tb.repayment_type AS repaymentType,tb. STATUS AS STATUS,tb.release_time AS release_time,tb.contract_id AS contract_id,(SELECT	COUNT(1) FROM t_bill_invest tbi	WHERE tbi.invest_id = ti.id AND tbi. STATUS IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+")AND tbi.user_id = ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM	t_bill_invest tbi	WHERE	tbi.invest_id = ti.id	AND tbi.user_id = ti.user_id) AS totalPay,(SELECT	t_pact.contract_id	FROM	t_pact	WHERE	pid = ti.bid_id	AND t_pact.user_id = ti.user_id) AS contract FROM	t_bid tb,	t_invest ti WHERE	tb.id = ti.bid_id AND ti.user_id =:userId AND tb. STATUS <>5 AND ti.debt_id = 0 AND ti.transfer_status = 0 ORDER BY  ti.id DESC";
 		
    	String sqlCount="SELECT COUNT(1) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND tb. STATUS<>5 AND ti.user_id =:userId and transfer_status=0";
        
    	Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, MyInvestRecordBean.class, condition);
	}
	
	
	
	/***
	 * 
	 * 理财账单列表接口（OPT=232）
	 * @param userId  用户id
	 * @param investId 投资id
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public List<BillInvestListBean> listOfInvestBillRecord(long userId,long investId){
		String sql = " SELECT tbi.id AS billInvestId,tbi.id AS billInvestNo,tb.id AS bidId , tbi.time AS time, tbi.receive_corpus AS receiveCorpus, tbi.receive_interest AS receiveInterest, tbi.receive_time AS receiveTime FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.user_id =:userId AND tbi.invest_id =:investId  ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("investId", investId);
		
		return findListBeanBySQL(sql, BillInvestListBean.class, condition);
	}
		
	/***
	 * 
	 * 理财账单详情接口（OPT=237）
	 * @param billInvestId 理财账单id
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public BillInvestInfo findInvestBillRecord(long billInvestId) {
		String sql = "SELECT tbi.id AS billInvestNo,tbi.id AS bid_no, tbi.time AS time, tbi.user_id AS userId, tbi.period AS period, tbi.receive_corpus AS receiveCorpus, tbi.receive_interest AS receiveInterest, tbi.receive_time AS receiveTime, tbi.real_receive_time AS realReceiveTime, tb.service_fee_rule AS serviceFeeRule, tbi.status AS status FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND tbi.id =:billInvestId  ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("billInvestId", billInvestId);
	
		return findBeanBySQL(sql, BillInvestInfo.class, condition);
	}
	
	/**
	 * app我的借款
	 *
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月5日
	 */
	public PageBean<MyLoanRecordBean> pageOfMyLoan (long userId, int currPage, int pageSize){
		
		String querySQL = "SELECT bid.id AS id, bid.title, bid.amount, bid.apr, bid.period, bid.period_unit AS periodUnit, bid.repayment_type AS repaymentType, bid.release_time AS releaseTime, bid.status,(SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+","+t_bill.Status.ADVANCE_lINE_RECEIVE.code+","+t_bill.Status.ADVANCE_lINE_RECEIVE_YET.code+")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill  FROM t_bid bid WHERE bid.user_id=:userId ORDER BY bid.id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_bid WHERE user_id=:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, MyLoanRecordBean.class, condition);
	}
	
	/***
	 * 交易记录接口（OPT=241）
	 *
	 * @param userId 用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public PageBean<DealRecordsBean> pageOfUserDealRecords (long userId, int currPage, int pageSize) {
		
		String querySQL = "SELECT id, time AS time,deal_type AS dealType ,amount AS amount, balance, freeze, order_no   AS orderNo ,summary AS summary  FROM t_deal_user WHERE user_id=:userId ORDER BY id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_deal_user WHERE user_id=:userId  ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, DealRecordsBean.class, condition);
	}
	
	/***
	 * 
	 *	客户端获取会员信息接口（OPT=214）
	 * @param userId 用户id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public 	Map<String, Object>  findUserInfo(long userId) throws Exception{
		String querySQL = " select photo, reality_name AS realName, id_number AS idNumber, mobile FROM t_user_info  WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return 	findMapBySQL(querySQL, condition);
	}
		
	/***
	 * 
	 *	客户端保存会员信息接口（OPT=254）
	 * @param userId 用户id
	 * @param education 学历
	 * @param mairtal 婚姻
	 * @param workExperience 工作 
	 * @param annualIncome 年收入
	 * @param netAsset  资产
	 * @param car 车
	 * @param house 房
	 * @param emergencyContactType 紧急联系人关系
	 * @param emergencyContactName  紧急联系人姓名
	 * @param emergencyContactMobile 紧急联系人电话
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public 	int updateUserInfo(long userId, int education, int marital, int workExperience,int annualIncome,int netAsset,int car,int house,int emergencyContactType,String emergencyContactName,String emergencyContactMobile) throws Exception{
	
		String updateSQL = " UPDATE t_user_info SET add_base_info_schedule=3, education=:education, marital=:marital, work_experience =:workExperience, annual_income =:annualIncome, net_asset =:netAsset, car =:car, house =:house, emergency_contact_type =:emergencyContactType, emergency_contact_name =:emergencyContactName, emergency_contact_mobile =:emergencyContactMobile WHERE user_id =:userId";
	
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("education", education);
		condition.put("marital", marital);
		condition.put("workExperience",workExperience );
		condition.put("annualIncome", annualIncome);
		condition.put("netAsset",netAsset );
		condition.put("car", car);
		condition.put("house",house );
		condition.put("emergencyContactType",emergencyContactType );
		condition.put("emergencyContactName",emergencyContactName );
		condition.put("emergencyContactMobile", emergencyContactMobile);
		
		return 	updateBySQL(updateSQL, condition);
	}
		
	/***
	 * 
	 * 绑卡列表(opt=122)
	 * @param userId 用户id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public List<UserBankCard> listOfUserBankCard(long userId) throws Exception{
		String sql = "SELECT id AS id,bank_name AS bankName,account AS account ,status AS status FROM t_bank_card_user WHERE user_id=:userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findListBeanBySQL(sql, UserBankCard.class, condition);
	} 
		
	/***
	 * 
	 *  查询用户是否有未读信息
	 * @param userId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */	
	public boolean hasUserUnreadMsg(long userId){
		String sql = " SELECT count(1) FROM t_message_user WHERE  user_id=:userId AND is_read=:isRead";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("isRead", false);

		int count = countBySQL(sql,condition);

		return count > 0 ;
	}
	
	/***
	 * 查询注册协议标题
	 *
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public String findRegisterDealTitle(){
		String sql = " SELECT title  FROM t_information WHERE  column_key=:columnKey AND is_use=:isUse ORDER BY order_time DESC";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("columnKey", InformationMenu.PLATFORM_REGISTER.code);
		condition.put("isUse", true);
		
		Map<String, Object> titleMap = findMapBySQL(sql, condition);
		
		return titleMap == null ? null : titleMap.get("title").toString();	
	}

	/**
	 * 更新为已读
	 *
	 * @param userId 用户id
	 * @param currPage 当前页
	 * @param pageSize 页大小
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月18日
	 */
	public int editUserMsgStatus(long userId, int currPage, int pageSize) {
		int start = (currPage - 1) * pageSize;
		
		String sql = "UPDATE t_message_user t1, (SELECT mu.id FROM t_message m INNER JOIN t_message_user mu ON mu.message_id = m.id  WHERE mu.user_id=:userId ORDER BY m.id DESC LIMIT :start,:pageSize) t2 SET t1.is_read=TRUE WHERE t1.id=t2.id AND t1.is_read=FALSE";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start", start);
		condition.put("pageSize", pageSize);
		condition.put("userId", userId);
		
		return super.updateBySQL(sql, condition);
	}
	
}
