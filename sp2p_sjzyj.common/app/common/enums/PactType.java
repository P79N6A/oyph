package common.enums;

/**
 * 枚举:合同类型
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
public enum PactType {

	/** 散标投资合同模板 */
	BID(0,"散标投资合同"),
	
	/** 债权转让合同模板 */
	DEBT(1,"债权转让合同");
	
	public int code;
	public String value;
	
	private PactType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static PactType getEnum(int code){
		PactType[] clients = PactType.values();
		for (PactType cli : clients) {
			if (cli.code == code) {

				return cli;
			}
		}
		
		return null;
	}
	
}
