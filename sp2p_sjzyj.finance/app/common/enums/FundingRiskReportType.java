package common.enums;

public enum FundingRiskReportType {

	AGENCY_BASIC_TABLE(1,"FP.FP01.001","FP.FP01机构基本表报文"),
	AGENCY_RELATION_INFO_TABLE(2,"FP.FP02.001","FP.FP02机构关联方信息表报文"),
	CUSTOMER_BASIC_INFO_TABLE(3,"FP.FP03.001","FP.FP03客户基本信息表报文"),
	CUSTOMER_RELATION_INFO_TABLE(4,"FP.FP04.001","FP.FP04客户关联方信息表报文"),
	DEAL_INFO_TABLE(5,"FP.FP05.001","FP.FP05交易信息表报文"),
	LOAN_INFO_TABLE(6,"FP.FP06.001","FP.FP06借据信息表报文"),
	PRODUCT_INFO_TABLE(7,"FP.FP07.001","FP.FP07产品信息表报文"),
	PRODUCT_THROW_TABLE(8,"FP.FP08.001","FP.FP08产品投向表报文");
	
	public int code;
	
	public String value;
	
	public String description;
	
	private FundingRiskReportType (int code ,String value, String description) {
		this.code = code;
		this.value = value;
		this.description = description;
	}
	
	public static FundingRiskReportType getEnum (int code) {
		FundingRiskReportType [] fundingRiskReportType = FundingRiskReportType.values();
		if (fundingRiskReportType != null && fundingRiskReportType.length > 0) {
			for (FundingRiskReportType reportType : fundingRiskReportType) {
				if (reportType.code == code) {
					return reportType;
				}
			}
		}
		return null;
	}
}
