package models.main.bean;

public class DiscountRate {
	public int timeLimit;
	public double discountRate;
	public double sum;
	public double getSum() {
		return 100.0*discountRate;
	}
	public double getSum1(){
		return 1*discountRate*timeLimit;
	}

}
