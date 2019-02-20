package jobs;
import payment.impl.PaymentProxy;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import common.constants.ConfConst;

@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
    	  	
    	/** 托管网关初始化 */
	    if(ConfConst.IS_TRUST){
	    	PaymentProxy.getInstance().init();
	    }
    }

}