package services.common;

import common.utils.Factory;
import daos.common.BranchDao;
import models.common.entity.t_company_branch;
import services.base.BaseService;

public class BranchService extends BaseService<t_company_branch>{

	protected static BranchDao branchDao = Factory.getDao(BranchDao.class);
	protected BranchService(){
		super.basedao = branchDao;
	}
	
}
