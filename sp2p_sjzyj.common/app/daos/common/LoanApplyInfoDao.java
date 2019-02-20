package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.Page;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_loan_apply_info;
/**
 * 
 *
 * @ClassName: LoanApplyInfoDao
 *
 * @description 前台借款信息Dao
 *
 * @author LiuHangjing
 * @createDate 2018年12月18日-下午5:04:09
 */
public class LoanApplyInfoDao extends BaseDao<t_loan_apply_info>{
	
	protected LoanApplyInfoDao() {}
	/**
	 * 
	 * @Title: findPageOfLoanApply
	 * 
	 * @description 后台-风控-借款申请-分页列表显示
	 * @param currPage
	 * @param pageSize

	 * @return PageBean<t_loan_apply_info>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午5:48:47
	 */
	public PageBean<t_loan_apply_info> findPageOfLoanApply(int showType,String appli_phone,int currPage,int pageSize){
		
		StringBuffer querySql = new StringBuffer("SELECT * FROM t_loan_apply_info WHERE 1=1 ");
		StringBuffer countSql = new StringBuffer("SELECT count(id) FROM t_loan_apply_info WHERE 1=1 ");
		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
		case 0:
			break;
		case 1:
			countSql.append(" AND appli_status = 0");
			querySql.append(" AND appli_status = 0");
			
			break;

		case 2:
			countSql.append(" AND appli_status = 1");
			querySql.append(" AND appli_status = 1");
			break;
		}
		if (StringUtils.isNotBlank(appli_phone)) {
			countSql.append(" AND appli_phone like :appli_phone");
			querySql.append(" AND appli_phone like :appli_phone");
			condition.put("appli_phone", "%"+appli_phone+"%");
		}
		countSql.append(" ORDER BY appli_time DESC");
		querySql.append(" ORDER BY appli_time DESC");
		
		return pageOfBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), condition);
	}
	
	/**
	 * 
	 * @Title: updateAppliStatus
	 * 
	 * @description 修改受理状态
	 * @param @param id
	 * @return Integer    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月19日-下午2:45:36
	 */
	public Integer updateAppliStatus(long id) {
		String sql = "update t_loan_apply_info set appli_status = 1,update_time = now() where id =:id";
		Map<String, Object> condition = new HashMap<>();
		condition.put("id", id);
		return updateBySQL(sql, condition);
	}
	
	
}
