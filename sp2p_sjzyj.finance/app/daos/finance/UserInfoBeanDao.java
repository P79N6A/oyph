package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.bean.UserInfoBean;
import models.finance.entity.t_acct_invest;

/**
 * 
 * @ClassName: UserInfoBeanDao
 *
 * @description UserInfoBean的Dao层，为方便(P05 河北省P2P机构最大十家客户信息表)查询建立
 *
 * @author yaozijun
 *
 * @createDate 2018年10月12日
 */
public class UserInfoBeanDao extends BaseDao<UserInfoBean> {
	/**
	 * 
	 * @Title: findListByType
	 *
	 * @description 查询出十家各自期末余额
	 *
	 * @param @return 
	 * 
	 * @return List<UserInfoBean>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public List<UserInfoBean> findListByType() {
		String sql = "SELECT tui.*, tb.*, IFNULL(SUM(tb.repayment_corpus ) ,0) AS debt FROM t_bill tb INNER JOIN t_bid t ON t.id = tb.user_id INNER JOIN t_user_info tui on tui.user_id=tb.user_id WHERE tb.status IN (0, 1) GROUP BY tb.user_id  ORDER BY IFNULL(SUM(tb.repayment_corpus),0) desc LIMIT 10";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findListBeanBySQL(sql, UserInfoBean.class, condition);
	}

}
