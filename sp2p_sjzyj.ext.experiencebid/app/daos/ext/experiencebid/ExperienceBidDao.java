package daos.ext.experiencebid;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.experience.entity.t_experience_bid;
import common.constants.Constants;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.base.BaseDao;

public class ExperienceBidDao extends BaseDao<t_experience_bid>{
	
	public ExperienceBidDao(){}

	/**
	 * 更新体验标
	 *
	 * @param investAmount
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int updateExperienceBid(double investAmount, long experienceBidId) {
		
		String sql = "UPDATE t_experience_bid SET has_invest_amount=(has_invest_amount+:investAmount), invest_count=(invest_count+1) WHERE id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("investAmount", investAmount);
		params.put("id", experienceBidId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 放款，更新体验标的信息
	 *
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int updateExperienceBidStatus(long experienceBidId, Date repayment_time) {
		
		String sql = "UPDATE t_experience_bid SET status=:status, release_time=:release_time, repayment_time=:repayment_time WHERE id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_experience_bid.Status.REPAYING.code);
		params.put("release_time", new Date());
		params.put("repayment_time", repayment_time);
		params.put("id", experienceBidId);
		
		return super.updateBySQL(sql, params);
	}

	/**
	 * 加入人次为0，直接结束体验标
	 *
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月9日
	 */
	public int interruptExperienceBid(long experienceBidId) {
		
		String sql = "UPDATE t_experience_bid SET status=:status, release_time=:release_time WHERE id=:id AND invest_count=0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_experience_bid.Status.END.code);
		params.put("release_time", new Date());
		params.put("id", experienceBidId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 更新体验标的状态为结束
	 *
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int endExperienceBid(long experienceBidId) {
		
		String sql = "UPDATE t_experience_bid SET status=:end, real_repayment_time=:real_repayment_time WHERE id=:id AND status=:repaying";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("end", t_experience_bid.Status.END.code);
		params.put("real_repayment_time", new Date());
		params.put("id", experienceBidId);
		params.put("repaying", t_experience_bid.Status.REPAYING.code);
		
		return super.updateBySQL(sql, params);
	}
	
	

	/**
	 * 体验标列表分页
	 *
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports 1：导出 default：默认不导出
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public PageBean<t_experience_bid> pageOfexperienceBid(int showType, int currPage, int pageSize, int exports){
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_experience_bid ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_experience_bid ");
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{//借款中
				querySQL.append(" WHERE status=:status");
				countSQL.append(" WHERE status=:status");
				condition.put("status", t_experience_bid.Status.FUNDRAISING.code);
				break;
			}
			case 2:{//还款中
				querySQL.append(" WHERE status=:status");
				countSQL.append(" WHERE status=:status");
				condition.put("status", t_experience_bid.Status.REPAYING.code);
				break;
			}
			case 3:{//已结束
				querySQL.append(" WHERE status=:status");
				countSQL.append(" WHERE status=:status");
				condition.put("status", t_experience_bid.Status.END.code);
				break;
			}
			default:{
				break;
			}
		}
		querySQL.append("  ORDER BY id DESC ");
		
		//是否导出
		if(exports == Constants.EXPORT){
			PageBean<t_experience_bid> page = new PageBean<t_experience_bid>();
			page.page = this.findListBySQL(querySQL.toString(), condition);
			return page;
		}
		
		return pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), condition);
	}
	
	/**
	 * 获取已经到期的体验标（今天或者今天之前到期）
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public List<t_experience_bid> queryExperienceBidForRepayment() {
		
		String sql = "SELECT * FROM t_experience_bid WHERE status=:status AND TO_DAYS(repayment_time)<=TO_DAYS(:nowTime)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_experience_bid.Status.REPAYING.code);
		params.put("nowTime", new Date());
		
		return super.findListBySQL(sql, params);
	}
	
	/**
	 * 查询已投体验标的总额
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public double findTotalGold(int showType) {
		
		StringBuffer sql = new StringBuffer("SELECT SUM(has_invest_amount) FROM t_experience_bid ");
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		switch (showType) {
			case 1:{//借款中
				sql.append(" WHERE status=:status");
				conditionArgs.put("status", t_experience_bid.Status.FUNDRAISING.code);
				break;
			}
			case 2:{//还款中
				sql.append(" WHERE status=:status");
				conditionArgs.put("status", t_experience_bid.Status.REPAYING.code);
				break;
			}
			case 3:{//已结束
				sql.append(" WHERE status=:status");
				conditionArgs.put("status", t_experience_bid.Status.END.code);
				break;
			}
			default:{
				break;
			}
		}
		
		return super.findSingleDoubleBySQL(sql.toString(), 0, conditionArgs);
	}

	/**
	 * 查询一个借款中的体验标（最早发布的那个）
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public t_experience_bid findExperienceBidFront() {
		
		String sql = "SELECT * FROM t_experience_bid WHERE status=:status LIMIT 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_experience_bid.Status.FUNDRAISING.code);
		
		return super.findBySQL(sql, params);
	}

}
