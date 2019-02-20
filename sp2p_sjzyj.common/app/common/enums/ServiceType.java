package common.enums;

/**
 * sp2p托管业务类型枚举
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月7日
 */
public enum ServiceType {

	/** 开户 */
	REGIST(10, "开户"),
	
	/** 用户绑卡 */
	BIND_CARD(11, "用户绑卡"),
	
	/** 自动投标签约 */
	AUTO_INVEST_SIGN(12, "自动投标签约"),
	
	/** 自动还款签约 */
	AUTO_REPAYMENT_SIGN(13, "自动还款签约"),

	/** 充值 */
	RECHARGE(30, "充值"),
	
	/** 提现 */
	WITHDRAW(31, "提现"),
	
	/** 标的发布 */
	BID_CREATE(32, "标的发布"),

	/** 手动投标  */
	INVEST(33, "手动投标"),
	
	/** 自动投标 */
	AUTO_INVEST(34, "自动投标"),
	
	/** 标的审核通过（放款） */
	BID_AUDIT_SUCC(35, "放款"),
	
	/** 初审不通过 */
	BID_PRE_AUDIT_FAIL(36, "初审不通过"),
	
	/** 复审不通过 */
	BID_AUDIT_FAIL(37, "复审不通过"),

	/** 流标 */
	BID_FLOW(38, "流标"),
	
	/** 还款 */
	REPAYMENT(39, "还款"),
	
	/** 垫付 */
	ADVANCE(40, "垫付"),
	
	/** 线下收款 */
	OFFLINE_RECEIVE(41, "线下收款"),
	
	/** 提前还款*/
	ADVANCE_RECEIVE(42,"提前还款"),
	
	/** 垫付后还款 */
	REPAYMENT_AFER_ADVANCE(43, "垫付后还款"),
	
	/** 奖励兑换 */
	CONVERSION(44, "奖励兑换"),
	
	/** 商户充值 */
	MERCHANT_RECHARGE(45, "商户充值"),
	
	/** 商户提现 */
	MERCHANT_WITHDRAWAL(46, "商户提现"),
	
	/** 债权转让 */
	DEBT_TRANSFER(47, "债权转让"),
	
	/** 登录接口 */
	LOGIN(48, "登录"),
	
	/** 设置交易密码 */
	SETUSERPASSWORD(49, "设置交易密码"),
	
	/** 修改交易密码 */
	AMENDUSERPASSWORD(50, "修改交易密码"),
	
	/** 重置交易密码 */
	RESETUSERPASSWORD(51, "重置交易密码"),
	
	/** 校验交易密码 */
	CHECKPASSWORD(52, "校验交易密码"),
	
	/** 查询绑卡信息 */
	QUERYBANK(53, "查询绑卡信息"),
	
	;
	
	
	/**
	 * 操作类型代号
	 */
	public int code;
	
	/**
	 * 描述
	 */
	public String value;
	
	private ServiceType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static ServiceType getEnum(int code) {
		ServiceType[] pts = ServiceType.values();
		for (ServiceType pt : pts) {
			if (pt.code == code) {
				
				return pt;
			}
		}
		
		return null;
	}
}
