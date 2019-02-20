package yb.enums;


public enum YbPayType {

	USERREGISTER(1, "用户开户", true),
	
	USERBINDCARD(2, "用户绑卡", true),
	
	NETSAVE(3, "充值", true),
	
	CASH(31, "取现", true),
	
	ADDBIDINFO(4, "标的信息录入", true),
	
	INITIATIVETENDER(5, "主动投标", true),
	
	AUTOTENDER(6, "自动投标", true),
	
	AUTOTENDERPLAN(7, "自动投标计划", true),
	
	TENDERCANCLE(8, "投标撤销", true),
	
	LOANS(9, "放款", true),
	
	USRFREEZEBG(10, "资金冻结", true),
	
	USRUNFREEZE(11, "资金解冻", true),
	
//	REPAYMENT(12, "还款", true),
	
	TRANSFER(13, "商户转账", true),
	
	USRACCTPAY(14, "用户支付", true),
	
	BATCHREPAYMENT(15, "批量还款", true),
	
	QUERYCARDINFO(16, "银行卡查询", false),
	
	QUERYACCTS(17, "商户余额查询", false),
	
	QUERYBALANCEBG(18, "用户余额查询", false),
	
	CREDITASSIGN(19,"债权转让",true),
	;
	
	/**
	 * 接口类型代号
	 */
	public int code;
	
	/**
	 * 描述
	 */
	public String value;

	/**
	 * 是否添加请求记录
	 */
	public boolean isAddRequestRecord;
	
	/**
	 * 是否添加回调记录（注意:当isAddRequestRecord为true，isAddCallBackRecord配置才生效，即不添加请求记录，则不添加回调记录）
	 */
	public boolean isAddCallBackRecord = true;
	
	private YbPayType(int code, String value, boolean isAddRequestRecord) {
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
	}
	
	private YbPayType(int code, String value, boolean isAddRequestRecord, boolean isAddCallBackRecord) {
		this.code = code;
		this.value = value;
		this.isAddRequestRecord = isAddRequestRecord;
		this.isAddCallBackRecord = isAddCallBackRecord;
	}
	
	public static YbPayType getEnum(int code) {
		YbPayType[] status = YbPayType.values();
		for (YbPayType statu : status) {
			if (statu.code == code) {
				
				return statu;
			}
		}
		
		return null;
	}
	
}
