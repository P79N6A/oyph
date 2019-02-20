package common.enums;

/**
 * 河北省P2P机构非现场监管报表 reportType
 * 
 * @author guoShiJjie
 * @createDate 2018.10.9
 * */

public enum SuperviseReportType {

	AGENCY_INCLUDE_TABLE(1,"P.P01.001","P.P01机构概括表报文"),
	PARTNER_INFO_TABLE(2,"P.P02.001","P.P02股东信息表报文"),
	EXECUTIVE_INFO_TABLE(3,"P.P03.001","P.P03高管信息表报文"),
	CUSTOMER_INFO_TABLE(4,"P.P04.001","P.P04客户信息表报文"),
	LARGEST_TEN_CUSTOMER_INFO_TABLE(5,"P.P05.001","P.P05最大十家客户信息表报文"),
	DEBT_PACT_TABLE(6,"P.P06.001","P.P06贷款合同表报文"),
	INVEST_PACT_TABLE(7,"P.P07.001","P.P07投资合同表报文"),
	BORROWER_REPAYMENT_DETAILS_TABLE(8,"P.P08.001","P.P08借款人还款明细表报文"),
	LENDERS_RECEIVE_DETAILS_TABLE(9,"P.P09.001","P.P09出借人收款明细表报文"),
	ASSET_DEBT_TABLE(10,"P.P10.001","P.P10资产负债表报文"),
	AGENCY_PROFIT_TABLE(11,"P.P11.001","P.P11机构利润表报文"),
	AGENCY_ASSET_DEBT_TABLE(12,"P.P12.001","P.P12机构现金流量表报文"),
	INTERNAL_CONTROL_SITUATION_TABLE(13,"P.P13.001","P.P13内控情况表报文");
	
	public int code;
	
	public String value;
	
	public String description;
	
	private SuperviseReportType (int code ,String value,String description ) {
		this.code = code;
		this.value = value;
		this.description = description;
	}
	
	public static SuperviseReportType getEnum (int code) {
		SuperviseReportType []  reportType = SuperviseReportType.values();
		if (reportType != null && reportType.length > 0) {
			for(int i = 0 ; i < reportType.length ; i++) {
				if (code == reportType[i].code) {
					return reportType[i];
				}
			}
		}
		
		return null;
	}
}
