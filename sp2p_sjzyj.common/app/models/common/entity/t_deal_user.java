package models.common.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;
import common.utils.StrUtil;

/**
 * 用户交易记录表
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月19日
 */

@Entity
public class t_deal_user extends Model {
	
	/** 用户ID */
	public long user_id;

	/** 添加时间 */
	public Date time;

	/** 业务订单号 */
	public String order_no;
	
	/** 交易类型：1-收入；2-支出；3-冻结；4-解冻； */
	private int deal_type;
	
	public DealType getDeal_type() {
		DealType dt = DealType.getEnum(this.deal_type);
		
		return dt;
	}
	
	public void setDeal_type(DealType dealType){
		this.deal_type = dealType.code;
	}
	
	/** 交易类型：1-充值；2-提现；3-放款；4-还款；5-投资 */
	private int operation_type;
	
	public OperationType getOperation_type() {
		OperationType dealType = OperationType.getEnum(this.operation_type);
		return dealType;
	}

	public void setOperation_type(OperationType operationType, Map<String, String> summaryParam) {
		this.deal_type = operationType.dealType.code;
		this.operation_type = operationType.code;
		this.summary = StrUtil.replaceByMap(operationType.value, summaryParam);
	}

	/** 交易金额 */
	public double amount;

	/** 可用余额 */
	public double balance;

	/** 冻结金额 */
	public double freeze;

	/** 备注 */
	public String summary;

	/**
	 * 交易类型枚举
	 *
	 * @description 
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月6日
	 */
	public enum DealType {

		/** 收入 */
		INCOME(1, "收入"),
		
		/** 支出 */
		PAY(2, "支出"),
		
		/** 冻结 */
		FREEZE(3, "冻结"),
		
		/** 解冻 */
		UNFREEZE(4, "解冻");
		
		public int code;
		
		public String value;  
		
		private DealType(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static DealType getEnum(int code){
			DealType[] dts = DealType.values();
			for (DealType dt: dts) {
				if (dt.code == code) {

					return dt;
				}
			}
			
			return null;
		}
	}

	/**
	 * 操作类型枚举
	 * 
	 * @description 
	 *
	 * @author ChenZhipeng
	 * @createDate 2015年12月21日
	 */
	public enum OperationType{
		
		/** 
		 * 操作类型:账户直充[101]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得账户直充充值金额
		 */
		RECHARGE(101, "账户直充",DealType.INCOME, "获得账户直充充值金额"),
		
		/** 
		 * 操作类型:账户网银充值[108]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得账户网银充值金额
		 */
		INTERNETRECHARGE(108, "账户网银充值",DealType.INCOME, "获得账户网银充值金额"),
		
		WITHDRAW_FREE(202, DealType.PAY, "扣除宜宾提现手续费金额"),
		
		/** 
		 * 操作类型:闪电快充[102]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得闪电快充充值金额
		 */
		RECHARGE_QUICK (102,"闪电快充", DealType.INCOME, "获得闪电快充充值金额"),
		
		/** 
		 * 操作类型:放款-借款人收款 [103]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得bidNo借款项目借款金额
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		RELEASE_BID(103, DealType.INCOME, "获得bidNo借款项目借款金额"),
		
		/** 
		 * 操作类型:回款[104]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得billInvestNo账单回款金额
		 * <br>备注参数说明：billInvestNo-理财账单编号
		 */
		RECEIVE(104, DealType.INCOME, "获得bidNo标号billInvestNo账单回款金额"),
		
		/** 
		 * 操作类型:回款[104]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得billInvestNo账单回款金额
		 * <br>备注参数说明：billInvestNo-理财账单编号
		 */
		RECEIVE_ADD_RATE(109, DealType.INCOME, "获得billInvestNo账单加息券金额"),
		
		/** 
		 * 操作类型:逾期罚息-理财人收入[105]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得billInvestNo账单逾期罚息
		 * <br>备注参数说明：billInvestNo-理财账单编号
		 */
		RECEIVE_OVERDUE_FINE(105, DealType.INCOME, "获得bidNo标号billInvestNo账单逾期罚息"),
		
		/** 
		 * 操作类型:奖励兑换[106]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得conversion_type兑换金额
		 * <br>备注参数说明：conversion_type-兑换类型
		 */
		CONVERSION(106, DealType.INCOME, "获得conversion_type兑换金额"),
		
		/** 
		 * 操作类型:债权转让成功-转让人获取竞拍金额[107]
		 * <br>交易类型：收入[1]
		 * <br>备注模板：获得debt_no转让项目出价金额
		 * <br>备注参数说明：获得debt_no转让项目编号
		 */
		RECEIVE_AUCTION_AMOUNT(107, DealType.INCOME, "获得debt_no转让项目出价金额"),
		
		

		/** 
		 * 操作类型:提现[201]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：扣除提现金额
		 */
		WITHDRAW(201, DealType.PAY, "扣除提现金额"),
		
		/** 
		 * 操作类型:提现手续费[202]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：扣除提现手续费
		 */
		WITHDRAW_FEE(202, DealType.PAY, "扣除提现手续费"),
		
		/** 
		 * 操作类型:放款-理财才人付款[203]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付bidNo理财项目购买金额
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		RELEASE_INVEST(203, DealType.PAY, "支付bidNo理财项目购买金额"),
		
		/** 2、支出（借款服务费）：支付bidNo借款项目借款服务费（支付J164借款项目借款服务费） */
		/** 
		 * 操作类型:扣除借款服务费[204]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付bidNo借款项目借款服务费
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		LOAN_SERVICE_FEE(204, DealType.PAY, "支付bidNo借款项目借款服务费"),
		
		/** 
		 * 操作类型:还款[205]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付billNo账单还款金额
		 * <br>备注参数说明：billNo-借款账单编号
		 */
		REPAYMENT(205, DealType.PAY, "支付bidNo标号billNo账单还款金额"),
		
		/** 
		 * 操作类型:扣除理财服务费[206]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付billInvestNo账单理财服务费
		 * <br>备注参数说明：billInvestNo-理财账单编号
		 */
		INVEST_SERVICE_FEE(206, DealType.PAY, "支付bidNo标号billInvestNo账单理财服务费"),
		
		/** 
		 * 操作类型:逾期罚息-借款人支出[207]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付billNo账单逾期罚息
		 * <br>备注参数说明：billNo-借款账单编号
		 */
		REPAYMENT_OVERDUE_FINE(207, DealType.PAY, "支付bidNo标号billNo账单逾期罚息"),
		
		/** 
		 * 操作类型:购买债权成功-转让人支付转让服务费[208]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付debt_no转让项目转让费用
		 * <br>备注参数说明：debt_no-转让项目编号
		 */
		REPAYMENT_TRANSFER_FEE(208, DealType.PAY, "支付debt_no转让项目转让费用"),
		
		/** 
		 * 操作类型:竞拍成功-竞拍者支付竞拍金额[209]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付debt_no转让项目出价金额
		 * <br>备注参数说明：debt_no-转让项目编号
		 */
		REPAYMENT_TRANSFER_AMOUNT(209, DealType.PAY, "支付debt_no转让项目出价金额"),
		
		/** 
		 * 操作类型:担保方代偿-[210]
		 * <br>交易类型：支出[2]
		 * <br>备注模板：支付debt_no转让项目出价金额
		 * <br>备注参数说明：debt_no-转让项目编号
		 */
		REPAYMENT_GUARANTEE_AMOUNT(210, DealType.PAY, "支付bidNo项目代偿金额"),
		
		
		
		/** 
		 * 操作类型:投标[301]
		 * <br>交易类型：冻结[3]
		 * <br>备注模板：冻结bidNo理财项目购买金额
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		INVEST_FREEZE(301, DealType.FREEZE, "冻结bidNo理财项目购买金额"),

		/** 
		 * 操作类型:冻结保证金[302]
		 * <br>交易类型：冻结[3]
		 * <br>备注模板：冻结bidNo借款项目借款保证金
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		BAIL_FREEZE(302, DealType.FREEZE, "冻结bidNo借款项目借款保证金"),
		
		/** 4、解冻（购买金额）：退还bidNo理财项目购买金额（退还J164理财项目购买金额） */
		/** 
		 * 操作类型:投标[401]
		 * <br>交易类型：解冻[4]
		 * <br>备注模板：退还bidNo理财项目购买金额
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		INVEST_UNFREEZE(401, DealType.UNFREEZE, "退还bidNo理财项目购买金额"),
		
		/** 4、解冻（放款,标的失败）：退还bidNo借款项目借款保证金（退还Q15借款项目借款保证金） */
		/** 
		 * 操作类型:解冻保证金[402]
		 * <br>交易类型：解冻[4]
		 * <br>备注模板：退还bidNo借款项目借款保证金
		 * <br>备注参数说明：bidNo-借款项目编号
		 */
		BAIL_UNFREEZE(402, DealType.UNFREEZE, "退还bidNo借款项目借款保证金"),
		
		;
		
		/** 充值类型：(包括 直充和闪电快充 ) */
		public static final List<Integer> RECHARGE_TYPE = Arrays.asList(
				RECHARGE.code, RECHARGE_QUICK.code); 
		

		public int code;
		
		/** 操作描述  */
		public String description;
		
		/** 交易类型code  */
		public DealType dealType;
		
		/** 备注模板 */
		public String value;
		
		private OperationType(int code, DealType dealType, String value) {
			this.code = code;
			this.dealType = dealType;
			this.value = value;
		}
		
		private OperationType(int code, String description, DealType dealType, String value) {
			this.code = code;
			this.description = description;
			this.dealType = dealType;
			this.value = value;
		}
		
		public static OperationType getEnum(int code){
			OperationType[] ots = OperationType.values();
			for (OperationType ot: ots) {
				if (ot.code == code) {

					return ot;
				}
			}
			
			return null;
		}
	}
	
}
