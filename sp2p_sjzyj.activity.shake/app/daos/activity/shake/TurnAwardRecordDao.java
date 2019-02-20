package daos.activity.shake;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.ResultSetInternalMethods;

import common.utils.PageBean;
import common.utils.TimeUtil;
import daos.base.BaseDao;
import jj.play.ns.com.jhlabs.image.HSBAdjustFilter;
import models.activity.shake.entity.t_turn_award_record;

public class TurnAwardRecordDao extends BaseDao<t_turn_award_record> {
	/**
	 * 
	 * @Title: pageOfByTurn
	 *
	 * @description 分页显示所有中奖人信息
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页返回记录数
	 * @param countSQL 每页返回记录数               
	 * @param querySQL 查询的SQL语句
	 * 
	 * @param @return
	 * 
	 * @return PageBean<t_turn_award_record>
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月29日
	 */
	public PageBean<t_turn_award_record> pageOfByTurn(int currPage, int pageSize) {
		String querySQL = "SELECT * FROM t_turn_award_record ORDER BY time DESC ";
		String countSQL = "SELECT count(*) FROM t_turn_award_record ORDER BY time DESC";

		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, t_turn_award_record.class, null);
	}
	
	

	/**
	 * 
	 * @Title: findById
	 *
	 * @description 根据用户ID查询中奖人信息
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页返回记录数 5 (在这写死)
	 * @param countSQL 每页返回记录数               
	 * @param querySQL 查询的SQL语句
	 * @param userid 登陆人id
	 * 
	 * @param @return List<t_turn_award_record>
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月25日
	 */
	public PageBean<t_turn_award_record> findByUserId(int currPage, int pageSize, Long userid) {
		String querySQL = "SELECT * FROM t_turn_award_record WHERE user_id = :userid AND award_id<18 ORDER BY time DESC";
		String countSQL = "SELECT count(*) FROM t_turn_award_record WHERE user_id = :userid ORDER BY time DESC";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userid", userid);

		return pageOfBeanBySQL(currPage, 5, countSQL, querySQL, t_turn_award_record.class,condition);

	}

	/**
	 * 
	 * @Title: updateEndStatus
	 * 
	 * @description 修改到期状态
	 * @return int
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月31日-下午5:57:20
	 */
	public int updateEndStatus() {
		/** 当前时间超过到期时间时，状态为到期 */
		String upSql = "update t_turn_award_record set end_status = 1 where end_time < now()";
		
		return updateBySQL(upSql, null);
	}

	/**
	 * 
	 * @Title: insert
	 * 
	 * @description 插入中奖信息并返回当前id
	 * @param t_turn_award_record
	 * @return t_turn_award_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午2:54:23
	 */
	public t_turn_award_record insert(t_turn_award_record t_turn_award_record) {
		return this.saveT(t_turn_award_record);
	}
	/**
	 * 
	 * @Title: getInfoHistory
	 * 
	 * @description 获取当前用户地址和电话信息，并回显
	 * @param @param user_id
	 * @return t_turn_award_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午4:02:41
	 */
	public t_turn_award_record getInfoHistory(Long user_id) {
		String eqSql = "SELECT * FROM t_turn_award_record WHERE user_id = :user_id";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("user_id", user_id);
		
		return findBySQL(eqSql, condition);
	}
	/**
	 * 
	 * @Title: updateReciveStatus
	 * 
	 * @description 修改领取状态
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午4:05:02
	 */
	public int updateReciveStatus(Long id) {
		String upSql = "update t_turn_award_record set status=1 where id = :id";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("id", id);
		return updateBySQL(upSql, condition);
	}

	/**
	 * 
	 * @Title: findByUserId
	 *
	 * @description 查询登陆人获得0.5元话费数量
	 *
	 * @param @param user_id
	 * @param @return 
	 * 
	 * @return t_turn_award_record    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月17日
	 */
	public t_turn_award_record findByUserId(Long user_id) {
		String upSql = "SELECT * FROM t_turn_award_record WHERE user_id =:user_id AND award_id=18";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("user_id", user_id);
		
		return findBySQL(upSql, condition);
	}
	
	/**
	 * 
	 * @Title: updateOnetelCount
	 *
	 * @description 当0.5元话费20次兑换10元话费时插入一条10元话费中奖纪录
	 *
	 * @param @param user_id
	 * @param @return 
	 * 
	 * @return int    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月23日
	 */
	public int updateOnetelCount(Long user_id) {
		String upSql = "update t_turn_award_record set onetel_count=onetel_count-20 where user_id = :user_id AND award_id=18";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("user_id", user_id);
		
		return updateBySQL(upSql, condition);
	}
	
	
}
