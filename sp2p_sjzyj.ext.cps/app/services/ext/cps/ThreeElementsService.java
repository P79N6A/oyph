package services.ext.cps;

import common.utils.Factory;
import daos.ext.cps.ThreeElementsDao;
import models.ext.cps.entity.t_three_elements_setting;
import services.base.BaseService;

public class ThreeElementsService extends BaseService<t_three_elements_setting> {

	protected ThreeElementsDao threeElementsDao  = null;
	
	public ThreeElementsService () {
		threeElementsDao = Factory.getDao(ThreeElementsDao.class);
	}
	
	/**
	 * 通过标识符查询三元素
	 * @param key
	 * @author guoShiJie
	 * */
	public t_three_elements_setting findElementsByKey (String key) {
		return threeElementsDao.findElementsByKey(key);
	}
	
	/**
	 * 更新三元素
	 * @param setting t_three_elements_setting表
	 * @author guoShiJie
	 * @createDate 2018.6.27
	 * */
	public int updateElements (t_three_elements_setting setting) {
		if (setting.describe1.isEmpty() && setting.keyword.isEmpty() && setting.title.isEmpty() && setting.id!= null ) {
			
			return threeElementsDao.deleteElements(setting);
		}
		t_three_elements_setting ele = threeElementsDao.findByID(setting.id);
		if(ele == null) {
			return threeElementsDao.save(setting)  ? 1 : 0;
		}
		ele.title = setting.title;
		ele.keyword = setting.keyword;
		ele.describe1 = setting.describe1;
		ele._key = setting._key;
		return threeElementsDao.save(ele)  ? 1 : 0;
	}
}
