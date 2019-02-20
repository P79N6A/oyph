package daos.activity.shake;

import daos.base.BaseDao;
import models.activity.shake.entity.t_display_status;
import models.activity.shake.entity.t_shake_record;

public class DisplayStatusDao extends BaseDao<t_display_status>{
	/**
	 * 
	 * @Title: findDisplayStatus
	 * 
	 * @description 查询返回是否显示状态值
	 * @return t_display_status    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月7日-上午11:51:18
	 */
	public t_display_status findDisplayStatus() {
		String sql = "SELECT * FROM t_display_status";
		
		return findBySQL(sql, null);
	}
	/**
	 * 
	 * @Title: updateDisplayStatus
	 * 
	 * @description 修改显示状态
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月7日-下午5:18:52
	 */
	public int updateDisplayStatus() {
		String sql = "UPDATE t_display_status SET display_status = CASE WHEN display_status = 0 THEN 1 WHEN display_status = 1 THEN 0 END,create_time = now()";
		
		return updateBySQL(sql, null);
	}
}
