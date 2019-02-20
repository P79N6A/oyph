package models.proxy.bean;

/**
 * 年化
 * 
 * @author GuoShijie
 * @cteateDate 2018.01.23
 * */
public class AnnualRate {
	
	/** 月份 */
	public int timeLimit;
	
	/** 折算率 */
	public double discountRate;

	public double sum;
	public double getSum() {
		return 1 * discountRate;
	}
	public double getSum1() {
		return discountRate * 100.0;
	} 
}
