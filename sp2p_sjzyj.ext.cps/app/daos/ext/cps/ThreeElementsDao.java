package daos.ext.cps;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.cps.entity.t_three_elements_setting;

public class ThreeElementsDao extends BaseDao<t_three_elements_setting> {

	/**
	 * 通过标识符查询三元素
	 * @param key
	 * @author guoShiJie
	 * */
	public t_three_elements_setting findElementsByKey (String key) {
		return this.findByColumn(" _key = ? ", key );
	}
	
	/**
	 * 删除指定三元素
	 * @author guoShiJie
	 * @createDate 2018.6.27
	 * */
	public int deleteElements (t_three_elements_setting setting) {
		String sql = "delete from t_three_elements_setting where id = :settingId ";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("settingId", setting.id);
		return this.deleteBySQL(sql, condition);
	}
	
}
