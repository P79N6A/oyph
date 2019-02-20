package dao;


import java.util.HashMap;
import java.util.Map;

import common.enums.InformationMenu;


import models.common.entity.t_information;
import daos.base.BaseDao;

public class AboutUsAppDao extends BaseDao<t_information> {

	/***
	 * 
	 * 关于我们（opt=301）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public Map<String, Object> findAboutUs() {
		String querySQL = " SELECT image_url AS  imgUrl , content AS content FROM t_information WHERE column_key=:columnKey AND is_use=true ORDER BY order_time DESC ";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("columnKey", InformationMenu.HOME_PROFILE.code);
		
		return findMapBySQL(querySQL, conditionArgs);
	}
	

	
}
