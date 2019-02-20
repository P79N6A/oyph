package controllers.back.webOperation;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_column;
import models.common.entity.t_event_supervisor;

import org.apache.commons.lang.StringUtils;

import services.common.ColumnService;

import common.utils.Factory;
import common.utils.ResultInfo;

import controllers.common.BackBaseController;

/**
 * 后台-运维-栏目设置控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class ColumnMngCtrl extends BackBaseController {

	protected static ColumnService columnService = Factory.getService(ColumnService.class);
	
	/**
	 * 后台-运维-栏目设置-显示前台栏目
	 *
	 * @rightID 201001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showColumnsPre(){
		
		Map<String, t_column> columnMap = columnService.queryAllColumns();
	
		render(columnMap);
	}
	
	/**
	 * 后台-运维-栏目设置-显示前台栏目-更新栏目名称
	 * 
	 * @rightID 201002
	 *
	 * @param _key 栏目的key
	 * @param columnName 更新后的名称
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月30日
	 */
	public static void updateColumnName(String _key,String columnName){
		ResultInfo result = new ResultInfo();
		if(StringUtils.isBlank(_key) || StringUtils.isBlank(columnName)){
			result.code = -1;
			result.msg = "栏目名称不能为空!";
			
			renderJSON(result);
		}
		
		boolean isUpdated = columnService.updateColumnName(_key,columnName);
		if (!isUpdated) {
			result.code = -1;
			result.msg = "没有更新成功";

			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "栏目名称已经更新!";
		
		//管理员事件
		long supervisorId = getCurrentSupervisorId();
		t_column column = columnService.findColumnByKey(_key);
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("item_id", column.id.toString());
		supervisorEventParam.put("item_name", column.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.ITEM, supervisorEventParam);
		
		renderJSON(result);
	}
	
}
