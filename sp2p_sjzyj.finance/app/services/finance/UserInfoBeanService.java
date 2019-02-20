package services.finance;

import java.util.List;

import common.utils.Factory;
import daos.finance.UserInfoBeanDao;
import models.finance.bean.UserInfoBean;
import models.finance.entity.t_a_loan_receipt;
import services.base.BaseService;

/**
 * 
 * @ClassName: UserInfoBeanService
 *
 * @description UserInfoBean的Service层，为方便(P05 河北省P2P机构最大十家客户信息表)查询建立
 *
 * @author yaozijun
 *
 * @createDate 2018年10月17日
 */
public class UserInfoBeanService extends BaseService<UserInfoBean> {
	protected static UserInfoBeanDao userInfoBeanDao= Factory.getDao(UserInfoBeanDao.class);
	
	/**
	 * 
	 * @Title: findListByType
	 *
	 * @description 调用UserInfoBeanDao方法查询十家各自期末余额
	 *
	 * @param @return 
	 * 
	 * @return List<UserInfoBean>    
	 *
	 * @author yaozijun
	 * 
	 * @createDate 2018年10月8日
	 */
	public List<UserInfoBean> findListByType() {
		
		return userInfoBeanDao.findListByType();
	}
	

}
