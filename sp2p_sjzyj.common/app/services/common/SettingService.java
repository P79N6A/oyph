package services.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_setting_platform;
import play.Logger;
import play.cache.Cache;
import services.base.BaseService;
import common.constants.Constants;
import common.constants.SettingKey;
import common.interfaces.ICacheable;
import common.utils.Factory;
import daos.common.SettingDao;

/**
 * 平台参数设置service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月15日
 */
public class SettingService extends BaseService<t_setting_platform> implements ICacheable {

	protected SettingDao settingDao = null;

	protected SettingService() {
		settingDao	= Factory.getDao(SettingDao.class);
		super.basedao = this.settingDao;
	}
	
	/**
	 * 查找所有的系统设置(key_value)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月15日
	 */
	public Map<String, String> queryAllSettings() {
		Map<String, t_setting_platform> optionMaps = getCache();
		if ( optionMaps == null ) {
			
			return null;
		}
		
		Map<String, String> map = new HashMap<String, String>();
		for (String key : optionMaps.keySet()) {
			map.put(key, optionMaps.get(key)._value);
		}

		return map;
	}

	/**
	 * 根据键名取得系统参数
	 *
	 * @param key 系统参数的键名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	public t_setting_platform findSettingByKey(String key) {
		Map<String, t_setting_platform> optionMaps = getCache();
		if ( optionMaps == null ) {
			
			return null;
		}
		
		t_setting_platform option = optionMaps.get(key);
		
		return option;
	}

	/**
	 * 根据键名取得系统参数的值
	 *
	 * @param key 系统参数的键名
	 * @return 该系统参数的值(String 类型)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	public String findSettingValueByKey(String key) {
		t_setting_platform option = findSettingByKey(key);
		if (option == null) {

			return null;
		}
		
		return option._value;
	}
	
	/**
	 * 根据平台参数的id的查找平台参数
	 *
	 * @param id 平台参数id
	 * @return 实体
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月3日
	 */
	public t_setting_platform findByID(long id) {
		t_setting_platform setting = null;
		
		Map<String, t_setting_platform> optionMaps = getCache();
		if ( (optionMaps == null) || (optionMaps.keySet() == null) || (optionMaps.keySet().size()==0) ) {
			
			return null;
		}
		
		for (t_setting_platform setting_temp : optionMaps.values()) {
			if (setting_temp.id == id) {
				setting = setting_temp;
				break;
			}
		}
		
		return setting;
	}

	@Override
	public List<t_setting_platform> findAll() {
		Map<String, t_setting_platform> optionMaps = getCache();
		if ( optionMaps == null ) {
			
			return null;
		}
		
		List<t_setting_platform> listOfSettings = new ArrayList<t_setting_platform>();
		listOfSettings.addAll(optionMaps.values());
		
		return listOfSettings ;
	}
	
	/**
	 * 更改"前台是否显示统计数据"系统参数
	 *
	 * @param display ture显示(1),false不显示0
	 * @param 操作的管理员名称
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public boolean updateIsStatisticsShow(boolean display) {
		String _value = "0";
		if (display) {
			_value = "1";
		}
		
		boolean flag = updateSetting(SettingKey.IS_STATISTICS_SHOW,_value);
	
		return	flag;

	}

	/**
	 * 更改"项目发布预告:project_releases_trailer "系统参数
	 *
	 * @param trailer 更新后的内容
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public boolean updateProjectReleasesTrailer(String trailer) {
		
		return updateSetting(SettingKey.PROJECT_RELEASES_TRAILER,trailer);
	}
	
	/**
	 * 更新系统参数中键名为_key的值为_value
	 *
	 * @param _key 系统参数的键名
	 * @param _value 更新后的值
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public boolean updateSetting(String _key, String _value) {
		t_setting_platform setting = settingDao.findByColumn("_key=?", _key);
		if (setting == null) {
			
			return false;
		}
		
		setting._value = _value;
		
		boolean flag = settingDao.save(setting);
		if (!flag) {

			return false;
		}
		
		addAFlushCache();
		return true;
	
	}
	
	/**
	 * 更新系统参数中键名为_key的值为_value
	 *
	 * @param _key 系统参数的键名
	 * @param _value 更新后的值
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月25日
	 */
	public boolean updateOrAddSetting(String _key, String _value) {
		t_setting_platform setting = settingDao.findByColumn("_key=?", _key);
		if (setting == null) {
			
			setting = new t_setting_platform();
			setting.time = new Date();
			setting._key = _key;
		}
		
		setting._value = _value;
		
		boolean flag = settingDao.save(setting);
		if (!flag) {
			
			return false;
		}
		
		addAFlushCache();
		return true;
	}

	/**
	 * 批量修改平台系统参数设定
	 *
	 * @description Map的key为系统参数的_key,value为对应_key更新后的值_value
	 * 
	 * @param maps 待修改的参数名(_value)-值的map
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public boolean updateSettings(Map<String, String> maps) {
		if (maps == null || maps.keySet() == null || maps.keySet().size() == 0 ) {
			return false;
		}

		for (String _key : maps.keySet()) {
			t_setting_platform setting = settingDao.findByColumn("_key=?", _key);
			setting._value = maps.get(_key);
			settingDao.save(setting);
		}
		
		addAFlushCache();
		return true;
	}

	@Override
	public void addAFlushCache() {
		Map<String, t_setting_platform> maps = null;
		
		try {
			List<t_setting_platform> options = settingDao.findAll();
			if(options != null && options.size()>0){
				maps = new HashMap<String, t_setting_platform>();
				for(t_setting_platform option : options){
					maps.put(option._key, option);
				}
			}
		} catch (Exception e) {
			Logger.error(e, "%s.addCache():"+"添加缓存时出错", this.getClass().getName());
		}
		
		Cache.safeSet(this.getClass().getName(),maps,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public Map<String, t_setting_platform> getCache() {
		Map<String, t_setting_platform> maps = (Map<String, t_setting_platform>) Cache.get(this.getClass().getName());
		if ( (maps == null) || (maps.keySet() == null) || (maps.keySet().size() == 0) ) {
			addAFlushCache();
			maps = (Map<String, t_setting_platform>) Cache.get(this.getClass().getName());
		}
		
		return maps;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
}
