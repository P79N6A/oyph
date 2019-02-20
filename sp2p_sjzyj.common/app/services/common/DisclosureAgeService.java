package services.common;

import common.utils.Factory;
import daos.common.DisclosureAgeDao;
import models.common.entity.t_disclosure_age;
import services.base.BaseService;

/**
 * 信息披露年龄统计service实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
public class DisclosureAgeService extends BaseService<t_disclosure_age> {

	private DisclosureAgeDao disclosureAgeDao = Factory.getDao(DisclosureAgeDao.class);
	
	protected DisclosureAgeService() {
		super.basedao = this.disclosureAgeDao;
	}
}
