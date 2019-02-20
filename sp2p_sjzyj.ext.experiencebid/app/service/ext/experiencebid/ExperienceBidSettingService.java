package service.ext.experiencebid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.experience.entity.t_experience_bid_setting;
import services.base.BaseService;
import common.utils.Factory;
import common.utils.ResultInfo;
import daos.ext.experiencebid.ExperienceBidSettingDao;

public class ExperienceBidSettingService extends BaseService<t_experience_bid_setting> {
	
	public ExperienceBidSettingDao experienceBidSettingDao = null;
	
	public ExperienceBidSettingService(){
		experienceBidSettingDao =  Factory.getDao(ExperienceBidSettingDao.class);
		basedao = experienceBidSettingDao;
	}
	
	/**
	 * 保存体验金发放规则
	 *
	 * @param opportunityCount
	 * @param everyGrant
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public ResultInfo saveExperienceGold(int opportunityCount, double everyGrant) {
		ResultInfo result = new ResultInfo();
		
		t_experience_bid_setting experienceBidSetting = experienceBidSettingDao.findByKey("opportunityCount");
		t_experience_bid_setting experienceBidSetting2 = experienceBidSettingDao.findByKey("everyGrant");
		if (experienceBidSetting == null) {
			experienceBidSetting = new t_experience_bid_setting();
		}
		experienceBidSetting._key = "opportunityCount";
		experienceBidSetting._value = ""+opportunityCount;
		experienceBidSettingDao.save(experienceBidSetting);
		
		if (experienceBidSetting2 == null) {
			experienceBidSetting2 = new t_experience_bid_setting();
		}
		experienceBidSetting2._key = "everyGrant";
		experienceBidSetting2._value = ""+everyGrant;
		experienceBidSettingDao.save(experienceBidSetting2);
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 保存体验项目发布规则
	 *
	 * @param raiseTime
	 * @param period
	 * @param apr
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public ResultInfo saveExperienceBid(int raiseTime, int period, double apr) {
		ResultInfo result = new ResultInfo();
		
		t_experience_bid_setting experienceBidSetting = experienceBidSettingDao.findByKey("raiseTime");
		t_experience_bid_setting experienceBidSetting2 = experienceBidSettingDao.findByKey("period");
		t_experience_bid_setting experienceBidSetting3 = experienceBidSettingDao.findByKey("apr");
		if (experienceBidSetting == null) {
			experienceBidSetting = new t_experience_bid_setting();
		}
		experienceBidSetting._key = "raiseTime";
		experienceBidSetting._value = ""+raiseTime;
		experienceBidSettingDao.save(experienceBidSetting);
		
		if (experienceBidSetting2 == null) {
			experienceBidSetting2 = new t_experience_bid_setting();
		}
		experienceBidSetting2._key = "period";
		experienceBidSetting2._value = ""+period;
		experienceBidSettingDao.save(experienceBidSetting2);
		
		if (experienceBidSetting3 == null) {
			experienceBidSetting3 = new t_experience_bid_setting();
		}
		experienceBidSetting3._key = "apr";
		experienceBidSetting3._value = ""+apr;
		experienceBidSettingDao.save(experienceBidSetting3);
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 查询体验标设置
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public Map<String, Object> queryExperienceBidInfo(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<Map<String, Object>> list = experienceBidSettingDao.queryExperienceBidInfo();
		if (list==null || list.size()==0) {
			
			return resultMap;
		}
		
		for(Map<String, Object> m : list){
			resultMap.put(String.valueOf(m.get("_key")), m.get("_value"));
		}
		
		return resultMap;
	}
	
	public t_experience_bid_setting findByKey(String key) {
		return experienceBidSettingDao.findByKey(key);
	}
}
