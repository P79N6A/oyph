package controllers.back.setting;

import models.common.bean.SupervisorEventLog;
import models.common.entity.t_event_supervisor;

import com.shove.Convert;
import common.utils.PageBean;

import controllers.common.BackBaseController;

/**
 * 后台-设置-操作日志-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class SupervisorEventMngCtrl extends BackBaseController {

	/**
	 * 后台-设置-操作日志
	 *
	 * @rightID 803001
	 * 
	 * @param column 栏目
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public static void showEventLogPre(int currPage,int pageSize,String supervisorName){
		int column = (Convert.strToInt(params.get("column"), 0));
		
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);//0,按编号，2,按栏目
		if (orderType != 0 && orderType != 2) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		PageBean<SupervisorEventLog> page = supervisorService.pageOfAllEventLogs(currPage, pageSize,orderType,orderValue, null, t_event_supervisor.Item.getEnum(column),supervisorName);
		
		render(page,supervisorName,column);
	}
	
}
