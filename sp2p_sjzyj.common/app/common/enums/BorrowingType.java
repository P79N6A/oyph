package common.enums;

public enum BorrowingType {
	
	BENIFIT_CAR_LOANS(1,"benifit_car_loans","惠车贷"),
	
	BENIFIT_HOUSE_LOANS(2,"benifit_house_loans","惠房贷");
	
	public int code;
	public String description;
	public String value;
	
	private BorrowingType(int code,String description,String value) {
		this.code = code;
		this.description = description;
		this.value = value;
	}
	
	public static BorrowingType getEnum(int code) {
		BorrowingType[] cas = BorrowingType.values();
		for (BorrowingType c : cas) {
			if (c.code == code) {
				return c;
			}
		}
		return null;
	}
}
