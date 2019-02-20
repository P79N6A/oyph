package yb.enums;

/**
 * 标的状态
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum ProjectStatus {

	PUBLIC(1, "01"), //发布
	
	INVESTING(2, "02"),//投资中
	
	LOAN(3, "03"), //放款
	
	FAIL(4, "04"), //流标
	
	CANCEL(5, "05"), //撤标
	
	REPAYMENT(6, "06"), //还款中
	
	FINISH(7, "07");//结束
	
	public int code; 
	public String value;
	
	private ProjectStatus(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static ProjectStatus getEnum(int code) {
		
		ProjectStatus[] statuss = ProjectStatus.values();
		for (ProjectStatus status : statuss) {
			
			if (status.code == code) {
				return status;
			}
		}
		
		return null;
	}
	
	
	
	
	
	
	
}















