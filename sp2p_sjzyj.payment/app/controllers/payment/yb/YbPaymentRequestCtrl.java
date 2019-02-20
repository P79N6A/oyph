package controllers.payment.yb;

import controllers.payment.PaymentBaseCtrl;

/**
 * 请求汇付托管控制器
 * 
 * @author niu
 * @create 2017.08.24
 */
public class YbPaymentRequestCtrl extends PaymentBaseCtrl  {

	private static YbPaymentRequestCtrl instance = null;
	
	private YbPaymentRequestCtrl(){
		
	}
	
	public static YbPaymentRequestCtrl getInstance(){
		if(instance == null){
			synchronized (YbPaymentRequestCtrl.class) {
				if(instance == null){
					instance = new YbPaymentRequestCtrl();
				}
			}
		}
		
		return instance;
	}
}
