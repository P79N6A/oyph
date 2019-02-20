package models.common.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;

import common.utils.StrUtil;

/**
 * 实体:平台交易记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月22日
 */
@Entity
public class t_deal_platform extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 交易记录类型 */
	private int operation;
	
	/** 用户id */
	public long user_id;
	
	/** 交易金额 */
	public double amount;
	
	/** 类型:1-收入，2-支出 */
	private int type;
	
	/** 备注 */
	private String remark;

	@Override
	public String toString() {
		return "t_deal_platform [time=" + time + ", operation=" + operation
				+ ", user_id=" + user_id + ", amount=" + amount + ", type="
				+ type + ", remark=" + remark + ", id=" + id + "]";
	}

	public DealType getType() {
		DealType dealType = DealType.getEnum(this.type);
		return dealType;
		
	}
	
	public DealRemark getOperation() {
		DealRemark operat = DealRemark.getEnum(this.operation);
		return operat;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(DealRemark revenueOperation,Map<String,Object> param) {
		Map<String,String> param_format = StrUtil.mapToMap(param);

		this.type = revenueOperation.dealType;
		this.operation = revenueOperation.dealOption;
		
		String descrip = StrUtil.replaceByMap(revenueOperation.value, param_format);
		
		this.remark = descrip;
	}

	/**
	 * 枚举： 收支类型 1.收入 2.支出
	 *
	 * @author liudong
	 * @createDate 2016年1月15日
	 */
	public enum DealType {
		
		/** 1-收入 */
		INCOME(1,"收入","+"),
		
		/** 2-支出 */
		EXPENSES(2,"支出","-");
		
		public int code;
		public String value;
		public String symbol;
		private DealType(int code, String value,String symbol) {
			this.code = code;
			this.value = value;
			this.symbol = symbol;
		}
		
		public static DealType getEnum(int code){
			DealType[] types = DealType.values();
			for (DealType type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 枚举： 交易类型   1：平台奖励 2：闪电充值 3：充值手续费  4：提现手续费  5：本息垫付 6：逾期罚息  7：线下收款 8:理财服务费  9:借款服务费   10:转让服务费 
	 *
	 * @author liudong
	 * @createDate 2016年1月15日
	 */
	public enum DealOption {
		
		/** 1：平台奖励 */
		PLATFORM_REWARD(1,"平台奖励"),
		
		/**:2：闪电充值 */
	//	QUICK_RECHAREG(2,"闪电充值"),
		
		/**:3：充值手续费 */
		RECHAREG_FREE(3,"充值手续费"),
		
		/** 4：提现手续费 */
		WITHDRAW_FREE(4,"提现手续费"),
		
		/**:5：本息垫付 */
		ADVANCE_PRIN_INTER(5,"本息垫付"),
		
		/** 6：逾期罚息 */
		OVERFUE_INTEREST(6,"逾期罚息"),

		/**:7：线下收款  */
		OFFLINE_SEATION(7,"线下收款"),
		
		/** 8:理财服务费 */
		INVEST_FEE(8,"理财服务费"),
		
		/** 9:借款服务费 */
		BID_FEE(9,"借款服务费"),
		
		/** 10:转让服务费 */
		TRANSFER_FEE(10,"转让服务费"),
		
		/**11:加息券利息 */
		ADD_RATE_FEE(11,"加息券利息"),
		;
		
		public int code;//对应option
		public String value;
		
		private DealOption(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static DealOption getEnum(int code){
			DealOption[] types = DealOption.values();
			for (DealOption type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 枚举:平台收支类型备注
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public enum DealRemark{
		
		/**
		 * <b>code:</b>				1<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		1:平台奖励<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		支付CPS推广佣金<br>
		 * <b>demo:</b>				支付CPS推广佣金<br>
		 */
		CONVERSION(1,DealType.EXPENSES.code,DealOption.PLATFORM_REWARD.code,"支付奖励兑换金额"),
		
		/**
		 * <b>code:</b>				1<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		2-闪电充值<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		支出闪电充值金额<br>
		 * <b>demo:</b>				支出闪电充值金额<br>
		 */
	//	QUICK_RECHARGE(1,DealType.EXPENSES.code,DealOption.QUICK_RECHAREG.code,"支出闪电充值金额"),
		
		/**
		 * <b>code:</b>				1<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		3-充值手续费<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		垫付充值手续费<br>
		 * <b>demo:</b>				垫付充值手续费<br>
		 */
	//	ADVANCE_RECHARGE_FREE(1,DealType.EXPENSES.code,DealOption.RECHAREG_FREE.code,"垫付充值手续费"),
		
		/**
		 * <b>code:</b>				1<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		4-提现手续费<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		垫付提现手续费<br>
		 * <b>demo:</b>				垫付提现手续费<br>
		 */
		ADVANCE_WITHDRAW_FREE(1,DealType.EXPENSES.code,DealOption.WITHDRAW_FREE.code,"垫付提现手续费"),
		
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		5-本息垫付<br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		垫付bill_no账单还款金额<br>
		 * <b>demo:</b>				垫付Z64账单还款金额<br>
		 */
		ADVANCE_PRIN_INTER(2,DealType.EXPENSES.code,DealOption.ADVANCE_PRIN_INTER.code,"垫付bill_no账单还款金额"),

		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		6-逾期罚息<br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		垫付bill_no账单逾期罚息<br>
		 * <b>demo:</b>				垫付Z64账单逾期罚息<br>
		 */
		OVERDUE_INTEREST(2,DealType.EXPENSES.code,DealOption.OVERFUE_INTEREST.code,"垫付bill_no账单逾期罚息"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>		7-线下收款<br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		代还bill_no账单还款金额<br>
		 * <b>demo:</b>				代还Z64账单还款金额<br>
		 */
		OFFLINE_SECTION(2,DealType.EXPENSES.code,DealOption.OFFLINE_SEATION.code,"代还bill_no账单还款金额"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				2-支出<br>
		 * <b>收支类型:</b>			7-线下收款<br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		代还bill_no账单加息券金额<br>
		 * <b>demo:</b>				代还Z64账单还款金额<br>
		 */
		PLATFORM_DEAL_ADD_RATE(2,DealType.EXPENSES.code,DealOption.ADD_RATE_FEE.code,"代还bill_invest_no账单加息券金额"),
		
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		2-闪电充值<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		收入闪电充值金额<br>
		 * <b>demo:</b>				收入闪电充值金额<br>
		 */
	//	QUICK_RECHARGE_INCOME(2,DealType.INCOME.code,DealOption.QUICK_RECHAREG.code,"收入闪电充值金额"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		4-提现手续费<br>
		 * <b>param:</b>			【】<br>
		 * <b>description:</b>		收入提现手续费<br>
		 * <b>demo:</b>				收入提现手续费<br>
		 */
		WITHDRAW_FREE_INCOME(2,DealType.INCOME.code,DealOption.WITHDRAW_FREE.code,"收入提现手续费"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		8-理财服务费 <br>
		 * <b>param:</b>			【bill_invest_no】<br>
		 * <b>description:</b>		获得bill_invest_no账单理财服务费<br>
		 * <b>demo:</b>				获得H65账单理财服务费<br>
		 */
		INVEST_FEE(2,DealType.INCOME.code,DealOption.INVEST_FEE.code,"获得bidNo标号bill_invest_no账单理财服务费"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		9-借款手续费<br>
		 * <b>param:</b>			【bid_no】<br>
		 * <b>description:</b>		获得bid_no借款项目借款服务费<br>
		 * <b>demo:</b>				获得J164借款项目借款服务费<br>
		 */
		BID_FEE(2,DealType.INCOME.code,DealOption.BID_FEE.code,"获得bid_no借款项目借款服务费"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		10-转让服务费 <br>
		 * <b>param:</b>			【transfer_no】<br>
		 * <b>description:</b>		获得transfer_no转让项目转让费用<br>
		 * <b>demo:</b>				获得Q15转让项目转让费用<br>
		 */
		TRANSFER_FEE(2,DealType.INCOME.code,DealOption.TRANSFER_FEE.code,"获得transfer_no转让项目转让费用"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		5-本息垫付 <br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		获得bill_no账单还款金额<br>
		 * <b>demo:</b>				获得Z64账单还款金额<br>
		 */
		ADVANCE_PRIN_INTER_INCOME(2,DealType.INCOME.code,DealOption.ADVANCE_PRIN_INTER.code,"获得bill_no账单还款金额"),
		
		/**
		 * <b>code:</b>				2<br>
		 * <b>收支:</b>				1-收入<br>
		 * <b>收支类型:</b>		6-逾期罚息 <br>
		 * <b>param:</b>			【bill_no】<br>
		 * <b>description:</b>		获得bill_no账单逾期罚息<br>
		 * <b>demo:</b>				获得Z64账单逾期罚息<br>
		 */
		OVERFUE_INTEREST_INCOME(2,DealType.INCOME.code,DealOption.OVERFUE_INTEREST.code,"获得bill_no账单逾期罚息"),
		
		;
		public int code;//对应option
		public int dealType;//对应type
		public int dealOption;//对应type
		public String value;//对应remark
		
		private DealRemark(int code, int dealType, int dealOption, String value) {
			this.code = code;
			this.dealType = dealType;
			this.dealOption = dealOption;
			this.value = value;
		}
		
		public static DealRemark getEnum(int code){
			DealRemark[] types = DealRemark.values();
			for (DealRemark type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
		
	}
	
}
