package services.common;


import java.util.List;

import common.utils.Factory;
import daos.common.DisclosureDao;
import models.common.entity.t_disclosure;
import models.common.entity.t_disclosure_month;
import services.base.BaseService;

/**
 * 信息披露service实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
public class DisclosureService extends BaseService<t_disclosure> {

	private DisclosureDao disclosureDao = Factory.getDao(DisclosureDao.class);
	
	protected DisclosureService() {
		super.basedao = this.disclosureDao;
	}
	
	/**
	 * 查询最近插入的一条数据
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public t_disclosure findByTime() {
		
		return disclosureDao.findByTime();
	}
	
	/**
	 * 查询所有的披露信息
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月29日
	 *
	 */
	public List<t_disclosure> queryListById() {
		
		return disclosureDao.queryListById();
	}
	
}
