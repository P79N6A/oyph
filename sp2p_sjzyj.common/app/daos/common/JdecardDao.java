package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_jdecard;

public class JdecardDao extends BaseDao<t_jdecard> {
	
	protected  JdecardDao(){
		
	}
	/**
	 * 
	 * @Title: pageOfNewTask
	 * 
	 * @description 分页显示E卡中奖记录
	 * @param currPage
	 * @param pageSize
	 * @return PageBean<t_jdecard>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午2:20:18
	 */
	public PageBean<t_jdecard> pageOfNewTask(String mobile,int currPage,int pageSize) {
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_jdecard WHERE 1=1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(id) FROM t_jdecard WHERE 1=1");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(mobile)) {
			querySQL.append(" AND mobile like :mobile");
			countSQL.append(" AND mobile like :mobile");
			condition.put("mobile", "%"+mobile+"%");
		}
		querySQL.append(" ORDER BY time DESC");
		countSQL.append(" ORDER BY time DESC");
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_jdecard.class,condition);
	}
	
	/**
	 * 
	 * @Title: findjdEBymobile
	 *
	 * @description 根据user_id和denomination查询t_jdecard表中是否存在记录
	 *
	 * @param @param id
	 * @param @param denomination
	 * @param @return 
	 * 
	 * @return t_jdecard    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年2月18日
	 */
	public t_jdecard findjdEByuserId (Long user_id,int denomination){
        String sql="SELECT * FROM t_jdecard WHERE user_id=:user_id AND denomination=:denomination AND win_type=0 ";
		
        Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		condition.put("denomination", denomination);
		
		return super.findBySQL(sql, condition);
	}
	
	/**
	 * 
	 * @Title: pageOfNewTaskAll
	 *
	 * @description 分页显示所有个人中奖信息
	 *
	 * @param @param mobile
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @return 
	 * 
	 * @return PageBean<t_jdecard>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年2月18日
	 */
     public PageBean<t_jdecard> pageOfNewTaskAll(Long user_id,int currPage,int pageSize) {
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_jdecard WHERE user_id =:user_id ORDER BY time DESC");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(id) FROM t_jdecard WHERE user_id =:user_id ORDER BY time DESC");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_jdecard.class,condition);
	}
	
}
