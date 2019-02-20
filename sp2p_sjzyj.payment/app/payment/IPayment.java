package payment;

import java.util.List;
import java.util.Map;

import common.enums.Client;
import common.utils.ResultInfo;
import models.common.entity.t_conversion_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import yb.enums.EntrustFlag;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;

/**
 * 互金平台和托管系统需要对接的业务接口
 * 
 * @author niu
 * @create 2017.08.23
 */
public interface IPayment {
	

	/**
	 * 个人客户注册
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public ResultInfo personCustomerRegist(int client, String businessSeqNo, long userId, String username, String certNo,
			String startTime, String endTime,String jobType,String job, String national,
			String postcode, String address, Object... objects);
	
	/**
	 * 企业客户注册
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public ResultInfo enterpriseCustomerRegist(int client, String businessSeqNo, long userId, String companyName,
			String uniSocCreCode, String entType, String establish, String companyId, String licenseAddress,
			String bankNo, String bankName,String userName ,String idCard, Object... objects);
	

	/**
	 * 校验客户充值交易密码
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public ResultInfo rechargeCheckPassword(int client, String businessSeqNo, long userId,double rechargeAmt,String bankNo, Object[] objects);
	
	
	/**
	 * 客户充值
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public ResultInfo recharge(int client, String businessSeqNo, long userId, double rechargeAmt,String bankNo, Object... obj);
	
	/**
	 * 校验客户提现交易密码
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */
	public ResultInfo withdrawalCheckPassword(int client, String businessSeqNo, long userId,double rechargeAmt,
			String bankNo,ServiceType serviceType,double withdrawalFee, Object[] objects);
	
	/**
	 * 客户提现
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */
	public ResultInfo withdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankNo,double withdrawalFee, Object... obj) ;
	
	
	/**
	 * 企业提现
	 * 
	 * @author LiuPengwei
	 * @create 2017年10月15日
	 */
	public ResultInfo enterpriseWithdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankNo,double withdrawalFee, Object... obj) ;
	
	/**
	 * 放款
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 业务订单号
	 * @param releaseSupervisorId 管理员ID
	 * @param bid 标的信息
	 * @param obj
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId, t_bid bid, Object... obj );
	
	
	
	/**
	 * 客户绑卡
	 * 
	 * @author niu
	 * @create 2017.08.29
	 */
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, String name, String idNumber, String mobile, Object... obj);
	
	/**
	 * 实时查询用户绑定的银行卡
	 * 
	 * @author niu
	 * @create 2017.08.29
	 */
	public ResultInfo queryBindedBankCard (int client, long userId, String serviceOrderNo, int serviceType, Object... obj);
	
	
	
	
	/**
	 * 根据接口类型代号获取对应的接口描述
	 * 
	 * @author niu
	 * @create 2017.08.24
	 */
	public String getInterfaceDes(int code);
	
	/**
	 * 获取接口类型的描述
	 * 
	 * @author niu
	 * @create 2017.08.24
	 */
	public int getInterfaceType(Enum interfaceType);
	
	/**
	 * 设置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public ResultInfo setUserPassword(int client, String businessSeqNo, long userId, Object... objects);
	
	/**
	 * 修改交易密码
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	public ResultInfo amendUserPassword(int client, String businessSeqNo, long userId, Object... objects);
	
	/**
	 * 重置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	public ResultInfo resetUserPassword(int client, String businessSeqNo, long userId, Object... objects);
	
	/**
	 * 校验交易密码
	 * 
	 * @author liu
	 * @create 2017.09.13
	 */
	public ResultInfo checkPassword(int client, String businessSeqNo, long userId, Object... objects);
	
	/**
	 * 托管账户资金信息查询
	 *
	 * @param client 客户端
	 * @param account 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月14日
	 */
	public ResultInfo queryFundInfo(int client, String account, String businessSeqNo);
	
	/**
	 * 代偿账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryCompensatoryBalance(int client, String businessSeqNo);
	
	/**
	 * 担保账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo
	 * @param paymentAccount  
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryGuaranteeBalance(int client, String businessSeqNo, long paymentAccount);
	
	/**
	 * 营销账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryMarketBalance(int client, String businessSeqNo);
	
	/**
	 * 费用账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryCostBalance(int client, String businessSeqNo);
	
	/**
	 * 台账平台充值
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo merchantRecharge(int client, String serviceOrderNo, double rechargeAmt, int flag);
	
	/**
	 * 台账平台取现
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月21日
	 */
	public ResultInfo merchantWithdrawal(int client, String serviceOrderNo, double rechargeAmt, int flag);
	
	/**
	 * 后台代偿线下收款
	 * 
	 * @param clientSn 客户端
	 * @param businessSeqNo
	 * @param bid
	 * @param serviceType
	 * @param entrustFlag
	 * @param accountList
	 * @param contractList
	 * @param objects
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年10月12日
	 */
	public ResultInfo offlineReceive(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects);
	
	/**
	 * 校验交易密码(网银充值)
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public ResultInfo ebankRechargeCheckPassword(int client, String businessSeqNo, long userId,double rechargeAmt, int flags);
	
	/**
	 * 校验交易密码(绑定银行卡)
	 * 
	 * @author liuyang
	 * @create 2018年1月3日
	 */
	public ResultInfo bindCardCheckPassword(int client, String businessSeqNo, long userId);
	
	/**
	 * 校验交易密码(担保人还款)
	 * 
	 * @author liuyang
	 * @create 2018年1月3日
	 */
	public ResultInfo guaranteeCheckPassword(int client, String businessSeqNo, long userId, String userName, double amount, String backUrl, String billSign);
	
	/**
	 * 客户网银充值
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public ResultInfo ebankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt);
	
	/**
	 * 企业网银充值
	 * 
	 * @author liuyang
	 * @create 2017年12月18日
	 */
	public ResultInfo enterpriseEbankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt, int flags);
	
	
	
	public ResultInfo autoCheckPassword(int client, String businessSeqNo, long userId, ServiceType serviceType, double min_apr,
			int max_period, double invest_amt, String valid_time, int valid_day, Object... objects);
	
	
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo, long userId, ServiceType serviceType,  double invest_amt,
			String valid_time,Object... obj);
	
	/**
	 * 客户注册
	 * 
	 * @author niu
	 * @create 2017.08.29
	 */
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj);
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj);
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill,  List<Map<String, Double>> billInvestFeeList, Object... obj) ;
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) ;
	public ResultInfo repaymentAll(int client, String serviceOrderNo, t_bill bill,  List<Map<String, Double>> billInvestFeeList, Object... obj) ;
	public ResultInfo debtTransfer(int clint,String serviceOrderNo,Long debtId,Long userId);
	public ResultInfo login(int client, long userId,  Object... obj);
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj);
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) ;
	public ResultInfo queryMerchantBalance (int client, Object... obj);
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj);
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj);
	
	public ResultInfo advanceReceive(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj);
	
	
	/**
	 * 标的信息同步（O00001）
	 * 
	 * @author niu
	 * @create 2017.09.14
	 */
	public ResultInfo bidInfoAysn(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, ProjectStatus projectStatus, List<Map<String, Object>> returnInfoList, Object... objects);
	
	/**
	 * 资金交易（T00002）
	 * 
	 * @author niu
	 * @create 2017.09.18
	 */
	public ResultInfo fundTrade(String clientSn, long userId, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects);
	
	/**
	 * 交易状态查询（C00001）
	 * 
	 * @author niu
	 * @create 2017.09.25
	 */
	public ResultInfo queryTradeStatus(String clientSn, String businessSeqNo, String oldbusinessSeqNo, String operType, Object... objects);
	
	/**
	 * 数据信息查询（C00002）
	 * 
	 * @author niu
	 * @create 2017.09.25
	 */
	public ResultInfo dataInfoQuery(String clientSn, String customerId, String accountNo, ServiceType serviceType, Object... objects);
	
	/**
	 * 校验交易密码（H5）
	 * 
	 * @author niu
	 * @create 2017.09.18
	 */
	public ResultInfo checkTradePass(int client, String businessSeqNo, long userId, String actionURL, String userName, ServiceType serviceType, double amount, Map<String, String> serviceRequestParams, ServiceType checkPassType, int type, String cardNo, String authtime, String authority, Object... objects);
	
	/**
	 * 客户信息同步（U00001）
	 *
	 * @author niu
	 * @create 2017.10.15
	 */
	public ResultInfo custInfoAysn(Map<String, String> inBody, String clientSn, ServiceType serviceType, Object... objects);
	
	/**
	 * 绑定银行卡(银行卡预留手机号)
	 * 
	 * @author guoShiJie
	 * @create 2018.04.16
	 */
	public ResultInfo bindCardCheckPassword1(int client, String businessSeqNo, long userId,String mobilePhone);
	
	/**
	 * 受让冲正数据拼接
	 * 
	 * @param businessSeqNo 业务流水号
	 * @param clientSn 托管请求唯一标识
	 * @param debitAccountNo 受让方台账账号
	 * @param financierAccountNo 转让方台账账号
	 * @param financierFee 交易金额
	 * @param oldbusinessSeqNo 原业务流水号
	 *
	 * @author liuyang
	 * @createDate 2018年8月9日
	 */
	public ResultInfo reversalTransfer(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, double financierFee, String oldbusinessSeqNo, String bidNo);
}



























