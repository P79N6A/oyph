package controllers.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import models.app.bean.Bills;
import play.Logger;
import play.mvc.Controller;
import services.core.BillService;

/**
 * AntifraudController 贷后管理接口（风控）
 * 
 * @createDate 2018 11 27
 */
public class AntifraudController extends Controller {

	protected static final BillService billService = Factory.getService(BillService.class);

	public static String loanTrack() {
		Long timeStamp = new Date().getTime();

		Map<String, Object> map = new HashMap<String, Object>();

		List<Bills> page = billService.pageOfBill();

		map.put("pageOfBills", page);
		map.put("timeStamp", timeStamp);
		String jsonStr = JSONObject.toJSONString(map);
		// Logger.error("响应:%s", jsonStr);

		return jsonStr;

	}

	/**
	 * 
	 * @Title: loanMobile
	 * @description: 按电话搜索
	 *
	 * @param mobile
	 * @return
	 * @return String
	 *
	 * @author HanMeng
	 * @createDate 2018年12月18日-上午11:35:23
	 */
	public static String loanMobile(String mobile) {

		Long timeStamp = new Date().getTime();

		Map<String, Object> map = new HashMap<String, Object>();

		List<Bills> pagemobile = billService.getBymobile(mobile);
		map.put("pagemobile", pagemobile);
		map.put("timeStamp", timeStamp);
		String jsonStr = JSONObject.toJSONString(map);
		// Logger.error("响应电话:%s", jsonStr);

		return jsonStr;

	}

	/**
	 * 
	 * @Title: loanOverdue
	 * @description: 按预期状态搜索
	 *
	 * @param is_overdue
	 * @return
	 * @return String
	 *
	 * @author HanMeng
	 * @createDate 2018年12月18日-上午11:35:43
	 */
	public static String loanOverdue(Integer is_overdue) {

		Long timeStamp = new Date().getTime();

		Map<String, Object> map = new HashMap<String, Object>();

		List<Bills> pageoverdue = billService.getByoverdue(is_overdue);

		map.put("pageoverdue", pageoverdue);
		map.put("timeStamp", timeStamp);
		String jsonStr = JSONObject.toJSONString(map);
		// Logger.error("响应逾期状态:%s", jsonStr);

		return jsonStr;

	}

}
