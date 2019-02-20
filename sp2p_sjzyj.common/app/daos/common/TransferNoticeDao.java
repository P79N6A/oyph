package daos.common;

import models.common.entity.t_transfer_notice;

import java.util.List;

import daos.base.BaseDao;

public class TransferNoticeDao extends BaseDao<t_transfer_notice>{
	/**
	 * 
	 * @Title: findAssNotice
	 * 
	 * @description 查询转让通知人电话
	 * @return List<t_transfer_notice>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月30日-下午4:39:05
	 */
	public List<t_transfer_notice> findAssNotice() {
		String sql = "SELECT * FROM t_transfer_notice WHERE notice_temp = 0";
		
		return findListBySQL(sql, null);
	}
	/**
	 * 
	 * @Title: findRepayNotice
	 * 
	 * @description 查询还款通知人电话
	 * @return List<t_transfer_notice>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月30日-下午4:42:41
	 */
	public List<t_transfer_notice> findRepayNotice() {
		String sql = "SELECT * FROM t_transfer_notice WHERE notice_temp = 1";
		
		return findListBySQL(sql, null);
	}

}
