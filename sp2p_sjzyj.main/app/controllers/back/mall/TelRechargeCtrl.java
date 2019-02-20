package controllers.back.mall;

import common.utils.CostRecharge;
import controllers.common.BackBaseController;
import net.sf.json.JSONObject;

/**
 * 积分商城：话费充值功能
 * 
 * @author liuyang
 * @created 2017-11-27
 */
public class TelRechargeCtrl extends BackBaseController {
	
	/**
	 * 
	 * @author liuyang
	 * @created 2017-11-27
	 * 
	 */
	public static void telRechargePre() {
		
		String balances = null;
		JSONObject obj = null;
		
		try {
			balances = CostRecharge.balanceMoney();
			obj = JSONObject.fromObject(balances); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		render(obj);
	}
}
