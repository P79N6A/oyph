package services.common;

import common.utils.Factory;

import daos.common.DataStatisticsDao;
import models.common.entity.t_data_statistics;
import services.base.BaseService;

/**
 * 用户交易记录service接口实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class DataStatisticService extends BaseService<t_data_statistics> {

	protected DataStatisticsDao dataStatisticsDao = Factory.getDao(DataStatisticsDao.class);
	
	protected DataStatisticService() {
		super.basedao = this.dataStatisticsDao;
	}
	
	
}
