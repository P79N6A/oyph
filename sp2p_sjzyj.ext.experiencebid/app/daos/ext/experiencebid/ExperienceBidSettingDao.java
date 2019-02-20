package daos.ext.experiencebid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.experience.entity.t_experience_bid_setting;
import daos.base.BaseDao;

public class ExperienceBidSettingDao extends BaseDao<t_experience_bid_setting>{
	
	public ExperienceBidSettingDao(){}
	
	
	/**
	 * 通过_key字段查找
	 *
	 * @param _key
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public t_experience_bid_setting findByKey(String _key){
		
		String sql = "SELECT id, _key, _value, description FROM t_experience_bid_setting WHERE _key=:_key";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("_key", _key);
		
		return super.findBySQL(sql, params);
	}
	
	/**
	 * 查询体验标设置
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public List<Map<String, Object>> queryExperienceBidInfo(){
		
		String sql = "SELECT _key, _value FROM t_experience_bid_setting ";
		Map<String, Object> params = new HashMap<String, Object>();
		
		return super.findListMapBySQL(sql, params);
	}

}
