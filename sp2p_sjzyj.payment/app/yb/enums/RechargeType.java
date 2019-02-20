package yb.enums;

import java.util.Iterator;

/**
 * 充值类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum RechargeType {

	CUSTOMER_WITHHOLD_RECHARGE(1, "R01"), //客户代扣充值
	
	CUSTOMER_CYBER_BANK_RECHARGE(2, "R02"), //客户网银充值
	
	MARKETING_RECHARGE(3, "R03"), //营销充值
	
	COMPENSTATE_RECHARGE(4, "R04"), //代偿充值
	
	FEE_RECHARGE(5, "R05"), //费用充值
	
	ADVANCE_MONEY_RECHARGE(6, "R06"), //垫资充值
	
	OFFLINE_RECHARGE(7, "R07"); //线下充值
	
	public int code;
	public String value;
	
	private RechargeType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static RechargeType getEnum(int code) {
		
		RechargeType[] types = RechargeType.values();
		
		for (RechargeType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
}


















