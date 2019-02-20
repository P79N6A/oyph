package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.GuaranteeDao;
import models.common.entity.t_guarantee;
import services.base.BaseService;

public class GuaranteeService extends BaseService<t_guarantee> {

	protected GuaranteeDao guartanteeDao = Factory.getDao(GuaranteeDao.class);
	
	protected GuaranteeService() {
		this.basedao = guartanteeDao;
	}
	
	/**
	 * 后台代偿，获取关联担保人
	 *
	 * @param key
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年1月8日
	 */
	public PageBean<t_guarantee> queryGuaranteesByKey(int currPage, int pageSize, String key) {
		
		return guartanteeDao.queryGuaranteesByKey(currPage, pageSize, key);
	}
}
