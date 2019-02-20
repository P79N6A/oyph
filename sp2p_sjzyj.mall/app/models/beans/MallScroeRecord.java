package models.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.common.entity.t_user;
import models.common.entity.t_user_vip_grade;
import models.core.entity.t_invest;
import models.entity.t_mall_goods;
import models.entity.t_mall_scroe_record;
import models.entity.t_mall_scroe_rule;
import models.entity.t_mall_virtual_goods_records;

import org.apache.commons.lang.StringUtils;

import common.constants.MallConstants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.NumberUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import services.common.UserVIPGradeService;

/**
 * 积分商城 积分记录业务逻辑
 * 
 * @author yuy
 * @time 2015-10-13 17:17
 *
 */
public class MallScroeRecord {

	protected static final UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	/**
	 * 查询积分记录列表
	 * 
	 * @param name
	 * @param status
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public static PageBean<t_mall_scroe_record> queryScroeRecordByPage(long user_id, String user_name, int type, String orderTypeStr,
			String orderStatus, int currPage, int pageSize, int isExport, ResultInfo error,String startTimeStr,String endTimdStr) {

		int orderType = 0;

		PageBean<t_mall_scroe_record> page = new PageBean<t_mall_scroe_record>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}

		if (NumberUtil.isNumericInt(orderTypeStr)) {
			orderType = Integer.parseInt(orderTypeStr);
		}

		if (orderType < 0 || orderType > 10) {
			orderType = 0;
		}
		
	

		StringBuffer sql_count = new StringBuffer("select count(*) from t_mall_scroe_record where 1 = 1");
		StringBuffer sql_list = new StringBuffer("select new t_mall_scroe_record(id, user_id, user_name, time, type, relation_id, scroe, status, "
				+ "quantity, description, remark) from t_mall_scroe_record t where 1=1");
		List<Object> params = new ArrayList<Object>();
		Map<String, Object> conditionMap = new HashMap<String, Object>();

		conditionMap.put("user_name", user_name);
		conditionMap.put("type", type);
		conditionMap.put("orderType", orderType);
		page.conditions = conditionMap;
		page.currPage = currPage;
		page.pageSize = pageSize;
		if (user_id != 0) {
			sql_count.append(" and user_id = ? ");
			sql_list.append(" and user_id = ? ");
			params.add(user_id);
		}
		if (!StringUtils.isBlank(user_name)) {
			sql_count.append(" and user_name like ? ");
			sql_list.append(" and user_name like ? ");
			params.add("%" + user_name + "%");
		}
		if (type != 0) {
			if(type==1){
				sql_count.append(" and type in(1,2,3,4) ");
				sql_list.append(" and type in(1,2,3,4) ");
			}else{
				sql_count.append(" and type = 5 ");
				sql_list.append(" and type = 5 ");
			}
		}
		
		if(StringUtils.isNotBlank(startTimeStr) && StringUtils.isNotBlank(endTimdStr)){
			Date start = DateUtil.strDateToStartDate(startTimeStr);
			Date end =  DateUtil.strDateToEndDate(endTimdStr);
			sql_count.append(" and  time >= ? and time <= ? ");
			sql_list.append(" and  time >= ? and time <= ? ");
			params.add(start);
			params.add(end);
		}

		
		sql_list.append(MallConstants.MALL_RECORD_ORDER[orderType]);
		/* 升降序 */
		if (StringUtils.isNotBlank(orderStatus) && orderType > 0) {
			if (Integer.parseInt(orderStatus) == 1)
				sql_list.append(" ASC");
			else
				sql_list.append(" DESC");
			conditionMap.put("orderStatus", orderStatus);
		}

		int count = 0;
		EntityManager em = JPA.em();
		Query query = em.createNativeQuery(sql_count.toString());

		for (int n = 1; n <= params.size(); n++) {
			query.setParameter(n, params.get(n - 1));
		}

		List<?> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录数时：:" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
			return page;
		}

		count = list == null ? 0 : Integer.parseInt(list.get(0).toString());
		if (count < 1) {
			return page;
		}

		List<t_mall_scroe_record> mallScroeRecordList = new ArrayList<t_mall_scroe_record>();
		try {
			query = em.createQuery(sql_list.toString(), t_mall_scroe_record.class);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			if (isExport != 1) {
				query.setFirstResult((currPage - 1) * pageSize);
				query.setMaxResults(pageSize);
			}
			mallScroeRecordList = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录列表时：" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
		page.totalCount = count;
		page.page = mallScroeRecordList;
		return page;
	}
	
	/**
	 * 查询积分记录列表
	 * 
	 * @param name
	 * @param status
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public static PageBean<t_mall_scroe_record> queryScroeByPage(long user_id, String user_name, int type, String orderTypeStr,
			String orderStatus, int currPage, int pageSize, int isExport, ResultInfo error) {
		
		int orderType = 0;
		
		PageBean<t_mall_scroe_record> page = new PageBean<t_mall_scroe_record>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		
		if (NumberUtil.isNumericInt(orderTypeStr)) {
			orderType = Integer.parseInt(orderTypeStr);
		}
		
		if (orderType < 0 || orderType > 10) {
			orderType = 0;
		}
		
		StringBuffer sql_count = new StringBuffer("select count(*) from t_mall_scroe_record where 1 = 1");
		StringBuffer sql_list = new StringBuffer("select new t_mall_scroe_record(id, user_id, user_name, time, type, relation_id, scroe, status, "
				+ "quantity, description, remark,good_status,logistics_number,send_time) from t_mall_scroe_record t where 1=1");
		List<Object> params = new ArrayList<Object>();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		conditionMap.put("user_name", user_name);
		conditionMap.put("type", type);
		conditionMap.put("orderType", orderType);
		page.conditions = conditionMap;
		page.currPage = currPage;
		page.pageSize = pageSize;
		if (user_id != 0) {
			sql_count.append(" and user_id = ? ");
			sql_list.append(" and user_id = ? ");
			params.add(user_id);
		}
		if (!StringUtils.isBlank(user_name)) {
			sql_count.append(" and user_name like ? ");
			sql_list.append(" and user_name like ? ");
			params.add("%" + user_name + "%");
		}
		/*if (type != 0) {
			sql_count.append(" and type = ? ");
			sql_list.append(" and type = ? ");
			params.add(type);
		}*/
		
		
		sql_count.append(" and scroe <=0 and relation_id in (select id from t_mall_goods where virtual_goods_id <= 0) ");
		sql_list.append(" and scroe <=0 and relation_id in (select id from t_mall_goods where virtual_goods_id <= 0) ");
		
		sql_list.append(MallConstants.MALL_RECORD_ORDER[orderType]);
		/* 升降序 */
		if (StringUtils.isNotBlank(orderStatus) && orderType > 0) {
			if (Integer.parseInt(orderStatus) == 1)
				sql_list.append(" ASC");
			else
				sql_list.append(" DESC");
			conditionMap.put("orderStatus", orderStatus);
		}
		
		int count = 0;
		EntityManager em = JPA.em();
		Query query = em.createNativeQuery(sql_count.toString());
		
		for (int n = 1; n <= params.size(); n++) {
			query.setParameter(n, params.get(n - 1));
		}
		
		List<?> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录数时：:" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
			return page;
		}
		
		count = list == null ? 0 : Integer.parseInt(list.get(0).toString());
		if (count < 1) {
			return page;
		}
		
		List<t_mall_scroe_record> mallScroeRecordList = new ArrayList<t_mall_scroe_record>();
		try {
			query = em.createQuery(sql_list.toString(), t_mall_scroe_record.class);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			if (isExport != 1) {
				query.setFirstResult((currPage - 1) * pageSize);
				query.setMaxResults(pageSize);
			}
			mallScroeRecordList = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录列表时：" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
		page.totalCount = count;
		page.page = mallScroeRecordList;
		return page;
	}

	/**
	 * 兑换：保存消费积分记录
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param goods
	 * @param postDetail
	 * @param error
	 */
	public static void saveScroeExchangeRecord(int exchangeNum, t_user user, t_mall_goods goods, String postDetail, ResultInfo error) {
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", user.id);
		
		t_mall_scroe_record record = new t_mall_scroe_record();
		record.user_id = user.id;
		record.user_name = user.name;
		record.time = new Date();
		record.type = MallConstants.EXCHANGE;
		record.relation_id = goods.id;
		record.scroe = (int)(-goods.exchange_scroe * exchangeNum*10000*(vip == null ? 1:vip.use_discount_integral==null?1:vip.use_discount_integral));
		record.status = MallConstants.STATUS_SUCCESS;
		record.quantity = Double.parseDouble(String.valueOf(exchangeNum));
		record.description = MallConstants.STR_EXCHANGE + goods.name;
		record.remark = postDetail;
		error.code = MallScroeRecord.insertScroeRecordDetail(record);
	}

	/**
	 * 注册：保存赠送积分记录
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param goods
	 * @param postDetail
	 * @param error
	 */
	public static void saveScroeRegistereRecord(t_user user, int scroe, ResultInfo error) {
		if (user == null || scroe == 0)
			return;

		t_mall_scroe_record record = new t_mall_scroe_record();
		record.user_id = user.id;
		record.user_name = user.name;
		record.time = new Date();
		record.type = MallConstants.REGISTER;
		record.scroe = scroe;
		record.status = MallConstants.STATUS_SUCCESS;
		record.description = MallConstants.STR_REGISTER;
		error.code = MallScroeRecord.insertScroeRecordDetail(record);
	}

	/**
	 * 每日签到：保存赠送积分记录
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param goods
	 * @param postDetail
	 * @param error
	 */
	public static void saveScroeSignRecord(t_user user, int scroe, ResultInfo error) {
		if (user == null || scroe == 0)
			return;

		t_mall_scroe_record record = new t_mall_scroe_record();
		record.user_id = user.id;
		record.user_name = user.name;
		record.time = new Date();
		record.type = MallConstants.SIGN;
		record.scroe = scroe;
		record.status = MallConstants.STATUS_SUCCESS;
		record.description = MallConstants.STR_SIGN;
		error.code = MallScroeRecord.insertScroeRecordDetail(record);
	}

	/**
	 * 投资：保存赠送积分记录(初始化)
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param invest
	 * @param productName
	 * @param error
	 */
	public static void saveScroeInvestRecord(t_user user, t_invest invest, t_mall_scroe_rule rule, String productName, ResultInfo error) {
		if (user == null || invest == null || productName == null)
			return;

		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", user.id);
		
		t_mall_scroe_record record = new t_mall_scroe_record();
		record.user_id = user.id;
		record.user_name = user.name;
		record.time = new Date();
		record.type = MallConstants.INVEST;
		record.relation_id = invest.id;
		
		int scroe=(int) (invest.amount/rule.amount*(vip==null?1:vip.give_integral==null?1:vip.give_integral));
		
		record.scroe = scroe*rule.scroe;
		record.status = MallConstants.STATUS_SUCCESS;
		record.quantity = invest.amount;
		record.description = MallConstants.STR_INVEST + productName;
		error.code = MallScroeRecord.insertScroeRecordDetail(record);
	}

	/**
	 * 放款回调：批量修改赠送积分记录状态为'成功'
	 * 
	 * @param invests
	 * @param error
	 */
	public static void updateScroeInvestRecordStatusByBatch(List<t_invest> invests, ResultInfo error) {
		if (invests == null || invests.size() == 0)
			return;

		for (t_invest invest : invests) {
			t_mall_scroe_record record = queryRecordDetailByInvestId(invest.id);
			updateScroeInvestRecordStatus(record, MallConstants.STATUS_SUCCESS);
		}
	}

	/**
	 * 放款回调：修改赠送积分记录状态为'成功'
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param goods
	 * @param postDetail
	 * @param error
	 */
	public static void updateScroeInvestRecordStatus(t_mall_scroe_record record, int status) {
		if (record == null)
			return;

		try {
			record.status = status;
			record.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			Logger.error("修改赠送积分记录状态为'成功'时：" + e.getMessage());
		}
	}

	/**
	 * 根据id查询积分记录信息
	 * 
	 * @param id
	 * @return
	 */
	public static t_mall_scroe_record queryRecordDetailById(long id) {
		t_mall_scroe_record record = null;
		try {
			record = t_mall_scroe_record.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录信息时：" + e.getMessage());
		}
		return record;
	}

	/**
	 * 根据投资id查询积分记录信息
	 * 
	 * @param id
	 * @return
	 */
	public static t_mall_scroe_record queryRecordDetailByInvestId(long invest_id) {
		t_mall_scroe_record record = null;
		try {
			record = t_mall_scroe_record.find(" relation_id = ? and type = ?", invest_id, MallConstants.INVEST).first();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录信息时：" + e.getMessage());
		}
		return record;
	}

	/**
	 * 保存积分记录
	 * 
	 * @param goods
	 * @return
	 */
	public static int insertScroeRecordDetail(t_mall_scroe_record record) {
		if (record == null)
			return MallConstants.COM_ERROR_CODE;
		try {
			record.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			Logger.error("保存积分记录时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}

	/**
	 * 查询用户可用积分
	 * 
	 * @param userId
	 *            用户
	 * @return
	 */
	public static int queryScoreRecordByUser(long userId, ResultInfo error) {
		String sql = "SELECT SUM(scroe) FROM t_mall_scroe_record WHERE user_id = ? and status = ?";
		Object sum = null;
		try {
			sum = JPA.em().createNativeQuery(sql).setParameter(1, userId).setParameter(2, MallConstants.STATUS_SUCCESS).getSingleResult();
		} catch (Exception e) {
			Logger.error("查询用户积分时，%s", e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}

		return sum == null ? 0 : ((BigDecimal) sum).intValue();
	}

	/**
	 * 根据日期查询积分记录条数
	 * 
	 * @param userId
	 *            用户
	 * @return
	 */
	public static int queryScoreRecordByDate(long userId, String dateStr, int type, ResultInfo error) {
		String sql = "SELECT count(*) FROM t_mall_scroe_record t Where t.time like ? and t.type = ? and t.user_id = ?";
		Object count = null;
		try {
			count = JPA.em().createNativeQuery(sql).setParameter(1, dateStr + "%").setParameter(2, type).setParameter(3, userId).getSingleResult();
		} catch (Exception e) {
			Logger.error("根据日期查询积分记录条数时，%s", e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}

		return count == null ? 0 : ((BigInteger) count).intValue();
	}

	/**
	 * 当前兑换动态
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> currentScroeNews() {
		ResultInfo error = new ResultInfo();

		Object scroeNewsObj = Cache.get("scroeNews");
		if (scroeNewsObj == null) {
			List<Map<String, Object>> newsList = MallGoods.queryHasExchangedGoodsNow(10, error);
			Cache.set("scroeNews", newsList);
			return newsList;
		}

		return (List<Map<String, Object>>) scroeNewsObj;
	}

	/**
	 * 当前积分
	 * 
	 * @return
	 */
	public static int currentMyScroe(long user_id) {
		ResultInfo error = new ResultInfo();

		int scroe = MallScroeRecord.queryScoreRecordByUser(user_id, error);
		return ((Integer) scroe).intValue();
	}

	public static int updateAddess(String rid, String address3) {
		ResultInfo error = new ResultInfo();
		String sql = " update t_mall_scroe_record set remark=? where id=? ";
		try {
			int row = JPA.em().createQuery(sql).setParameter(1, address3).setParameter(2, Long.parseLong(rid)).executeUpdate();
			return row;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			Logger.info(e.getMessage());
			error.code = -1;
			error.msg = "数据库异常";
			return error.code;
		}

	}
	
	/**
	 * 修改
	 * @param id
	 * @param content
	 * @param dec
	 * @return
	 */
	public static int updateShipment(long id,String content,String dec){
		ResultInfo error = new ResultInfo();
		String sql = "update t_mall_scroe_record set logistics_number=?,send_time=?,good_status=1 where id=?";
				
		try {
			int row = JPA.em().createQuery(sql).setParameter(1, dec).setParameter(2, new Date()).setParameter(3, id).executeUpdate();
			return row;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			Logger.info(e.getMessage());
			error.code = -1;
			error.msg = "数据库异常";
			return error.code;
		}
		
	}
	
	/**
	 * 摇一摇积分
	 * @param userId
	 * @param userName
	 * @param amount
	 * @return
	 */
	public static Boolean sharkScroeRecord(long userId, String userName, int amount) {
		ResultInfo errors = new ResultInfo();
		
		t_mall_scroe_record records = new t_mall_scroe_record();
		records.user_id = userId;
		records.user_name = userName;
		records.time = new Date();
		records.type = MallConstants.RAFFLE;
		records.scroe = amount;
		records.status = MallConstants.STATUS_SUCCESS;
		records.description = MallConstants.STR_SHARK;
		errors.code = MallScroeRecord.insertScroeRecordDetail(records);
		
		if(errors.code == 1) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 保存虚拟物品使用情况表
	 * @author guoShiJie
	 * */
	public static void saveVirtualGoodsRecords (Long user_id,Long mall_goods_id,Long goods_id,ResultInfo error) {
		if (user_id == null || mall_goods_id == null || goods_id == null || error == null) {
			return ;
		}
		t_mall_virtual_goods_records virtualGoods = new t_mall_virtual_goods_records();
		virtualGoods.user_id = user_id;
		virtualGoods.mall_goods_record_id = mall_goods_id;
		virtualGoods.goods_id = goods_id;
		error.code = MallScroeRecord.insertVirtualGoodsRecordDetail(virtualGoods);
		return ;
	}
	
	/**
	 * 保存虚拟物品表
	 * @author guoShiJie
	 * */
	public static int insertVirtualGoodsRecordDetail(t_mall_virtual_goods_records virtualGoods) {
		if (virtualGoods == null)
			return MallConstants.COM_ERROR_CODE;
		try {
			virtualGoods.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			Logger.error("保存虚拟商品积分记录时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}
	/**
	 * 查询虚拟物品红包和加息券的详细信息
	 * @author guoShiJie
	 * */
	public static PageBean<t_mall_virtual_goods_records> queryVirtualGoodsScoreRecordPage (int currPage, int pageSize, ResultInfo error) {
		PageBean<t_mall_virtual_goods_records> page = new PageBean<t_mall_virtual_goods_records>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		StringBuffer sql_count  = new StringBuffer ("select count(*) from t_mall_virtual_goods_records where 1=1 ");
		StringBuffer sql_list = new StringBuffer ("select * from t_mall_virtual_goods_records t where 1=1 ");
		List<Object> params = new ArrayList<Object>();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		page.conditions = conditionMap;
		page.currPage = currPage;
		page.pageSize = pageSize;
		
		sql_count.append("order by id desc");
		sql_list.append("order by id desc");
		
		int count = 0;
		EntityManager em = JPA.em();
		Query query = em.createNativeQuery(sql_count.toString());

		for (int n = 1; n <= params.size(); n++) {
			query.setParameter(n, params.get(n - 1));
		}

		List<?> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询积分记录数时：:" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
			return page;
		}

		count = list == null ? 0 : Integer.parseInt(list.get(0).toString());
		if (count < 1) {
			return page;
		}

		List<t_mall_virtual_goods_records> mallScroeRecordList = new ArrayList<t_mall_virtual_goods_records>();
		try {
			
			query = em.createNativeQuery(sql_list.toString(), t_mall_virtual_goods_records.class);
			query.setFirstResult((currPage - 1) * pageSize);
			query.setMaxResults(pageSize);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			
			mallScroeRecordList = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询虚拟商品积分记录列表时：" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
		page.totalCount = count;
		page.page = mallScroeRecordList;
		return page;
	}
	/**
	 * 查询积分商城记录表最后添加的数据(t_mall_score_record)
	 * @author guoShiJie
	 * @createDate 2018.06.06
	 * */
	public static t_mall_scroe_record queryLastMallScoreRecord () {
		String sql = "select * from t_mall_scroe_record order by id desc limit 1 ";
		Query query = JPA.em().createNativeQuery(sql, t_mall_scroe_record.class);
		List<t_mall_scroe_record> recordList = query.getResultList();
		if (recordList.size() > 0) {
			return recordList.get(0);
		}
		return null;
	}
	
	/**
	 * 生日礼物赠送
	 * @author guoShiJie
	 * @createDate 2018.11.8
	 * */
	public static ResultInfo saveRecord (t_mall_goods goods, Double num , Long userId, int score, String description, String postDetail, ResultInfo error) {
		t_user user = t_user.findById(userId);
		t_mall_scroe_record record = new t_mall_scroe_record();
		record.user_id = userId;
		record.user_name = user.name;
		record.time = new Date();
		record.type = MallConstants.RAFFLE;
		record.relation_id = goods.id;
		record.scroe = score;
		record.status = MallConstants.STATUS_SUCCESS;
		record.quantity = num;
		record.description = description;
		record.remark = postDetail;
		error.code = MallScroeRecord.insertScroeRecordDetail(record);
		return error;
	} 
}
