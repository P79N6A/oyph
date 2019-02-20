package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_user_vip_grade;

public class UserVIPGradeDao extends BaseDao<t_user_vip_grade>{

	protected UserVIPGradeDao () {}
	
	/**
	 * 修改客户的vip等级
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public Integer updateUserVipGrade (Long grade ,Long user_id) {
		
		String sql = " update t_user_info set vip_grade_id = :grade where user_id = :userId ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("grade", grade);
		condition.put("userId", user_id);
		
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 查询所有的VIP等级 降序
	 * @author guoShiJie
	 * @createDate 2018.11.12
	 * */
	public List<t_user_vip_grade> queryAll () {
		String sql = "select * from t_user_vip_grade order by min_amount desc";
		
		return this.findListBySQL(sql, null);
	}
}
