package common.enums;


/**
 * 枚举:年收入
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum AnnualIncome {

	/** 5W以下:1 */
	LESS_THAN_50000(1,"5W以下"),
	
	/** 5W~10W:2 */
	BETWEEN_50000_100000(2,"5W~10W"),
	
	/** 10W~50W:3 */
	BETWEEN_100000_500000(3,"10W~50W"),
	
	/** 50W以上:4 */
	MORE_THAN_500000(4,"50W以上");
	
	public int code;
	public String value;  
	
	private AnnualIncome(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static AnnualIncome getEnum(int code){
		AnnualIncome[] incomes = AnnualIncome.values();
		for (AnnualIncome income : incomes) {
			if (income.code == code) {

				return income;
			}
		}
		
		return null;
	}

}
