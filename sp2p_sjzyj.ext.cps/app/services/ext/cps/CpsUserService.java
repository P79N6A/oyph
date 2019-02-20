package services.ext.cps;

import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import daos.ext.cps.CpsUserDao;
import models.ext.cps.bean.CpsSpreadRecord;
import models.ext.cps.entity.t_cps_user;
import services.base.BaseService;

public class CpsUserService extends BaseService<t_cps_user>{

	protected CpsUserDao cpsUserDao = null;
	protected CpsUserService () {
		cpsUserDao = Factory.getDao(CpsUserDao.class);
	}
	
	/**
	 * 查询cps推广记录
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public PageBean<CpsSpreadRecord> queryCpsRecord(int currPage , int pageSize,long spreader_id) {
		return cpsUserDao.queryCpsRecord(currPage, pageSize, spreader_id);
	}
	
	/**
	 * 通过userId查询t_cps_user
	 * @param userId 注册id
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public t_cps_user findByUserId(long userId) {
		return cpsUserDao.findByUserId(userId);
	}
}
