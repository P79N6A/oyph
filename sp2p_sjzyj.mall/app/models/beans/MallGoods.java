package models.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.common.entity.t_user;
import models.entity.t_mall_goods;
import models.entity.t_mall_goods_type;
import models.entity.t_mall_scroe_record;
import models.entity.v_mall_goods_views;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_ticket_virtual_goods;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_coupon;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_virtual_goods;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import com.shove.Convert;
import com.shove.gateway.weixin.gongzhong.vo.user.UserInfo;

import common.constants.MallConstants;
import common.constants.ext.RedpacketKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.NumberUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.UserDao;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import services.common.UserService;

/**
 * 积分商城 商品业务逻辑
 * 
 * @author yuy
 * @time 2015-10-13 17:17
 * 
 */
public class MallGoods {

	/**
	 * 查询商品列表
	 * 
	 * @param name
	 * @param status
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public static PageBean<t_mall_goods> queryMallGoodsByPage(String name, int status, String orderTypeStr, String orderStatus, int currPage,
			int pageSize, ResultInfo resultInfo,Integer typeId) {

		int orderType = 0;

		PageBean<t_mall_goods> page = new PageBean<t_mall_goods>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 6;
		}

		if (NumberUtil.isNumericInt(orderTypeStr)) {
			orderType = Integer.parseInt(orderTypeStr);
		}

		if (orderType < 0 || orderType > 10) {
			orderType = 0;
		}

		StringBuffer sql_count = new StringBuffer("select count(*) from t_mall_goods where 1 = 1 and visible = 1 ");
		StringBuffer sql_list = new StringBuffer("select new t_mall_goods(id,name,time,pic_path,introduction,total,"
				+ "max_exchange_count,surplus,exchange_scroe,status,type_id,pic_format) from t_mall_goods where 1 = 1 and visible = 1 ");
		List<Object> params = new ArrayList<Object>();
		Map<String, Object> conditionMap = new HashMap<String, Object>();

		conditionMap.put("name", name);
		conditionMap.put("status", status);
		conditionMap.put("orderType", orderType);
		conditionMap.put("typeId", typeId);
		page.conditions = conditionMap;
		page.currPage = currPage;
		page.pageSize = pageSize;
		if (!StringUtils.isBlank(name)) {
			sql_count.append(" and name like ? ");
			sql_list.append(" and name like ? ");
			params.add("%" + name.trim() + "%");
		}
		if (status != 0) {
			sql_count.append(" and status = ? ");
			sql_list.append(" and status = ? ");
			params.add(status);
		}
		if (typeId != -1) {
			sql_count.append(" and type_id = ? ");
			sql_list.append(" and type_id = ? ");
			params.add(typeId);
		}

		sql_list.append(MallConstants.MALL_GOODS_ORDER[orderType]);
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
			Logger.error("查询商品记录数时：:" + e.getMessage());
			resultInfo.code = MallConstants.DML_ERROR_CODE;
			return page;
		}

		count = list == null ? 0 : Integer.parseInt(list.get(0).toString());
		if (count < 1) {
			return page;
		}

		List<t_mall_goods> mallGoods = new ArrayList<t_mall_goods>();
		try {
			query = em.createQuery(sql_list.toString(), t_mall_goods.class);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			query.setFirstResult((currPage - 1) * pageSize);
			query.setMaxResults(pageSize);
			mallGoods = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询商品列表时：" + e.getMessage());
			resultInfo.code = MallConstants.DML_ERROR_CODE;
		}
		page.totalCount = count;
		page.page = mallGoods;
		return page;
	}	
	
	/**
	 * author:lihuijun
	 * date:2017-2-5
	 */
	public static PageBean<t_mall_goods_type> queryMallGoodsTypeByPage(int currPage,int pageSize) {

	

		PageBean<t_mall_goods_type> page = new PageBean<t_mall_goods_type>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}

		

		StringBuffer sql_count = new StringBuffer("select count(*) from t_mall_goods_type ");
		StringBuffer sql_list = new StringBuffer("select new t_mall_goods_type(id,goods_type,pic_path,pic_format,type) from t_mall_goods_type ");
		List<Object> params = new ArrayList<Object>();
	
		page.currPage = currPage;
		page.pageSize = pageSize;
		

	
		/* 升降序 */
		

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
			Logger.error("查询商品记录数时：:" + e.getMessage());
			
			return page;
		}

		count = list == null ? 0 : Integer.parseInt(list.get(0).toString());
		if (count < 1) {
			return page;
		}

		List<t_mall_goods_type> mallGoods = new ArrayList<t_mall_goods_type>();
		try {
			query = em.createQuery(sql_list.toString(), t_mall_goods_type.class);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			query.setFirstResult((currPage - 1) * pageSize);
			query.setMaxResults(pageSize);
			mallGoods = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询商品类型列表时：" + e.getMessage());
			
		}
		page.totalCount = count;
		page.page = mallGoods;
		return page;
	}
	
	/**
	 * 查询商品信息
	 * 
	 * @param id
	 * @return
	 */
	public static t_mall_goods queryGoodsDetailById(long id) {
		t_mall_goods goods = null;
		try {
			goods = t_mall_goods.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询商品信息时：" + e.getMessage());
		}
		return goods;
	}
	/**
	 * 查询商品类型信息
	 * @param id
	 * author:lihuijun
	 * date:2017-2-6
	 */
	public static t_mall_goods_type queryGoodsTypeDetailById(long id){
		t_mall_goods_type goodsType = null;
		try {
			goodsType=t_mall_goods_type.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询商品类型信息时：" + e.getMessage());
		}
		return goodsType;
	}
	
	/**
	 * 查询所有商品类型信息
	 *  author:lihuijun
	 *  date:2017-2-6
	 */
	public static List<t_mall_goods_type> queryAllGoodsType(){
		String sql="select * from t_mall_goods_type where type = 0";
		Query query=JPA.em().createNativeQuery(sql, t_mall_goods_type.class);
		return query.getResultList();
	}
	/**
	 * 保存商品信息
	 * 
	 * @param goods
	 * @return
	 */
	public static int saveGoodsDetail(t_mall_goods goods) {
		if (goods == null)
			return MallConstants.COM_ERROR_CODE;
		// update
		if (goods.id != null) {
			goods = clone(goods);
		}
		try {
			goods.save();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存商品信息时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}
	/**
	 * author：lihuijun
	 * @param goodsType
	 * @return
	 */
	
	public static int saveGoodsType(t_mall_goods_type goodsType){
		if(goodsType==null)
			return MallConstants.COM_ERROR_CODE;
		if(goodsType.id !=null){
			goodsType=clone(goodsType);
		}
		try {
			goodsType.save();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存商品信息时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.DML_ERROR_CODE; 
	}

	/**
	 * 克隆
	 * 
	 * @param goods
	 * @return
	 */
	private static t_mall_goods clone(t_mall_goods goods) {
		if (goods == null)
			return null;
		if (goods.id == null)
			return goods;

		t_mall_goods goods_ = queryGoodsDetailById(goods.id);
		goods_.type_id=goods.type_id;
		goods_.name = goods.name;
		goods_.introduction = goods.introduction;
		goods_.pic_path = goods.pic_path;
		goods_.total = goods.total;
		goods_.max_exchange_count = goods.max_exchange_count;
		goods_.surplus = goods.surplus;
		goods_.exchange_scroe = goods.exchange_scroe;
		goods_.pic_format = goods.pic_format;
		goods_.virtual_goods_id = goods.virtual_goods_id;
		
		return goods_;
	}
	/**
	 * 克隆 
	 * author: lihuijun
	 * date:2017-2-5
	 */
	private static t_mall_goods_type clone(t_mall_goods_type type){
		if (type == null)
			return null;
		if (type.id == null)
			return type;

		t_mall_goods_type goodsType = queryGoodsTypeDetailById(type.id);
		goodsType.goods_type = type.goods_type;
		goodsType.pic_path=type.pic_path;
		goodsType.pic_format = type.pic_format;
		
		return goodsType;
	}
	
	
	/**
	 * 删除商品信息
	 * 
	 * @param id
	 * @return
	 */
	public static int deleteGoodsDetail(long id) {
		t_mall_goods goods = queryGoodsDetailById(id);
		if (goods == null)
			return MallConstants.COM_ERROR_CODE;
		try {
			goods.visible = MallConstants.INVISIBLE;// 隐藏
			goods.save();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("删除商品信息时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}

	/**
	 * 删除商品类型信息
	 * author：lihuijun
	 * date:2017-2-6 
	 */
	public static int deleteGoodsTypeDetail(long id){
		t_mall_goods_type goodsType = queryGoodsTypeDetailById(id);
		if (goodsType == null)
			return MallConstants.COM_ERROR_CODE;
		try {
			goodsType.delete();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("删除商品信息时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}
	/**
	 * 暂停商品兑换
	 * 
	 * @param id
	 * @return
	 */
	public static int stopGoodsExchange(long id, int status) {
		t_mall_goods goods = queryGoodsDetailById(id);
		if (goods == null)
			return MallConstants.COM_ERROR_CODE;
		try {
			goods.status = status;// 暂停/开启
			goods.save();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("暂停/开启商品兑换时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}

	
	public static PageBean<t_mall_scroe_record> queryHasExchangedGoodsByList(String keywords,long user_id,int currPage, int pageSize, ResultInfo resultInfo) {
		PageBean<t_mall_scroe_record> page = new PageBean<t_mall_scroe_record>();
		if (currPage == 0) {
			currPage = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		
		String sql = "select * from t_mall_scroe_record where type =5 and user_id =:user_id order by time desc ";

		String sqlCount = " select count(1) from t_mall_scroe_record where  type=5 and user_id =:user_id order by time desc ";
		
		if(!StringUtils.isBlank(keywords)){
			sql = "select * from t_mall_scroe_record where type =5 and user_id =:user_id AND relation_id IN ( SELECT id FROM t_mall_goods where name LIKE '"+"%"+keywords+"%"+"' )  order by time desc ";
			sqlCount =" select count(1) from t_mall_scroe_record where  type=5 and user_id =:user_id  AND relation_id IN ( SELECT id FROM t_mall_goods where name LIKE '"+"%"+keywords+"%"+"' ) order by time desc ";
		}
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);
		try {
			UserDao userDao = Factory.getDao(UserDao.class);
			page = userDao.pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, t_mall_scroe_record.class,map);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询查询推广会员列表详情时：" + e.getMessage());

			return page;
		}
		return page;
	}


/*	*//**
	 * 用户已兑换商品信息
	 * 
	 * @return
	 *//*
	public static List<Map<String, Object>> queryHasExchangedGoodsByList(long user_id, ResultInfo resultInfo) {
		String sql = "select r.id rId,g.id,g.name,g.pic_path,r.scroe,r.quantity,r.time,r.remark from t_mall_scroe_record r,t_mall_goods g "
				+ "where r.relation_id = g.id and r.type = ? and r.user_id = ? order by r.time desc";

		List<Object> params = new ArrayList<Object>();
		params.add(MallConstants.EXCHANGE);
		params.add(user_id);

		Query query = JPAUtil.createNativeQuery(sql, params);
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setFirstResult(1);
		query.setMaxResults(10);
		
		try {
			return query.getResultList();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return null;
		}
	}*/
	
	/**
	 * 兑换商品排行
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> queryHasExchangedGoodsOrder(Integer num, ResultInfo resultInfo) {
		StringBuilder sql = new StringBuilder("select g.id,g.name,g.max_exchange_count-g.surplus as count from t_mall_goods g ");
		sql.append("order by g.max_exchange_count-g.surplus desc,g.surplus desc ");
		if (num != null && num > 0) {
			sql.append("limit ?,?");
			return JPAUtil.getList(resultInfo, sql.toString(), 0, num);
		}
		return JPAUtil.getList(resultInfo, sql.toString());
	}

	/**
	 * 兑换商品排行
	 * 
	 * @return
	 */
	public static PageBean<v_mall_goods_views> queryHasExchangedGoodsOrder(Integer currPage, Integer pageSize, ResultInfo error) {
		StringBuilder sql = new StringBuilder(
				"select g.id,g.name,g.max_exchange_count-g.surplus as count,g.total as total, pic_path,surplus,exchange_scroe,g.pic_format from t_mall_goods g where g.visible = 1 and g.status = 1 ");
		sql.append("order by surplus desc");
		PageBean<v_mall_goods_views> pageBean = new PageBean<v_mall_goods_views>();
		pageBean.currPage = currPage;
		pageBean.pageSize = pageSize;
		List<v_mall_goods_views> mgv = new ArrayList<v_mall_goods_views>();

		try {
			EntityManager em = JPA.em();
			Query query = em.createNativeQuery(sql.toString(), v_mall_goods_views.class);
			query.setFirstResult((pageBean.currPage - 1) * pageBean.pageSize);
			query.setMaxResults(pageBean.pageSize);
			mgv = query.getResultList();

			StringBuilder sql_count = new StringBuilder();
			sql_count.append("select count(*) from ( ").append(sql).append(" )c");
			Query queryCount = em.createNativeQuery(sql_count.toString());
			pageBean.totalCount = Convert.strToInt(queryCount.getResultList().get(0) + "", 0);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询查询推广会员列表详情时：" + e.getMessage());

			return pageBean;
		}

		pageBean.page = mgv;
		return pageBean;
	}

	/**
	 * 兑换动态
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> queryHasExchangedGoodsNow(Integer num, ResultInfo error) {
		StringBuilder sql = new StringBuilder("select t.user_name,g.name from t_mall_scroe_record t,t_mall_goods g ");
		sql.append("where t.relation_id = g.id and t.type = ? order by t.time desc ");
		if (num != null && num > 0) {
			sql.append("limit ?,?");
			return JPAUtil.getList(error, sql.toString(), MallConstants.EXCHANGE, 0, num);
		}

		return JPAUtil.getList(error, sql.toString(), MallConstants.EXCHANGE);
	}

	/**
	 * 立即兑换，兑换商品逻辑处理（锁机制）
	 * 
	 * @param exchangeNum
	 * @param user
	 * @param goods
	 * @param postDetail
	 * @param error
	 */
	public static synchronized void exchangeHandle(int exchangeNum, t_user user, t_mall_goods goods, String postDetail, ResultInfo error) {
		// 更新剩余数量
		MallGoods.updateSurplus(exchangeNum, user.id, goods.id, error);
		// 更新失败
		if (error.code < 0) {
			return;
		}
		// 保存积分兑换记录
		MallScroeRecord.saveScroeExchangeRecord(exchangeNum, user, goods, postDetail, error);
	}

	/**
	 * 修改剩余量(数据库锁机制保证数据安全，又可做商品剩余数量、积分的校验)
	 * 
	 * @param exchangeNum
	 * @param userScroe
	 * @param user_id
	 * @param error
	 */
	public static void updateSurplus(int exchangeNum, long user_id, long goods_id, ResultInfo error) {
		String sql = "update t_mall_goods g set g.surplus = g.surplus - ? where g.surplus >= ? "
				+ "and g.exchange_scroe * ? <= (SELECT SUM(scroe) FROM t_mall_scroe_record WHERE user_id = ?) and g.id = ?";
		JPAUtil.executeUpdate(error, sql, exchangeNum, exchangeNum, exchangeNum, user_id, goods_id);
	}

	/**
	 * 当前商品兑换动态
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> currentExchangeNews() {
		ResultInfo error = new ResultInfo();

		Object exchangeNewsObj = Cache.get("exchangeNews");
		if (exchangeNewsObj == null) {
			List<Map<String, Object>> goodsList = MallGoods.queryHasExchangedGoodsOrder(10, error);
			Cache.set("exchangeNews", goodsList);
			return goodsList;
		}

		return (List<Map<String, Object>>) exchangeNewsObj;
	}

/*	*//**
	 * 当前常见问题
	 * 
	 * @return
	 *//*
	public static PageBean<t_content_news> currentQuestionNews() {
		ErrorInfo error = new ErrorInfo();

		Object obj = Cache.get("cpage");
		if (obj == null) {
			PageBean<t_content_news> cpage = News.queryNewsByTypeId(String.valueOf(MallConstants.COMMEN_QUESTION), "0", "10", null, error);
			Cache.set("cpage", cpage);
			return cpage;
		}

		return (PageBean<t_content_news>) obj;
	}*/

	public static List<t_mall_goods> queryGoods() {
		List<t_mall_goods> mgs = null;
		try {
			mgs = t_mall_goods.find(" status=? and visible=? and surplus>0 ", 1, 1).fetch();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mgs;
	}
	
	public static List<t_mall_goods> queryGoodss() {
		List<t_mall_goods> mgs = null;
		try {
			mgs = t_mall_goods.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mgs;
	}
	
	/* liuyang 2017-2-5 -------------begin---------------- */
	public static List<t_mall_goods_type> queryGoodsType() {
		List<t_mall_goods_type> mgss = null;
		try {
			mgss = t_mall_goods_type.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mgss;
	}
	public static List<Map<String, Object>> queryGoodsTypes(Integer num, ResultInfo error) {
		StringBuilder sql = new StringBuilder("select t.id,t.goods_type,t.pic_path,t.pic_format from t_mall_goods_type t limit 8");
		return JPAUtil.getList(error, sql.toString());
	}
	/* liuyang 2017-2-5 -------------end---------------- */

	public static void updateSurplusOfCJ(int exchangeNum, long user_id, long goods_id, ResultInfo error) {
		String sql = "update t_mall_goods g set g.surplus = g.surplus - ? where g.surplus >= ?  and g.id = ?";
		JPAUtil.executeUpdate(error, sql, exchangeNum, exchangeNum, goods_id);
	}

	/**
	 * 用户已兑换商品信息
	 * 
	 * @return
	 */
	public static PageBean<t_mall_scroe_record> queryHasExchangedGoodsForPage(long user_id, int currPage, int pageSize, ResultInfo error) {


		PageBean<t_mall_scroe_record> page = new PageBean<t_mall_scroe_record>();

		page.pageSize = pageSize;

		page.currPage = currPage;

		String sql = "select new t_mall_scroe_record(r.time,r.scroe,r.quantity,g.id,g.name,g.pic_path) from t_mall_scroe_record r,t_mall_goods g "
				+ "where r.relation_id = g.id and r.type = ? and r.user_id = ? order by r.time desc";
		String countSql = "select count(*) from t_mall_scroe_record r,t_mall_goods g where r.relation_id = g.id and r.type = ? and r.user_id = ?";

		List<Object> params = new ArrayList<Object>();

		params.add(MallConstants.EXCHANGE);

		params.add(user_id);

		int count = 0;

		EntityManager em = JPA.em();

		Query query = em.createNativeQuery(countSql);

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
			query = em.createQuery(sql.toString(), t_mall_scroe_record.class);
			for (int n = 1; n <= params.size(); n++) {
				query.setParameter(n, params.get(n - 1));
			}
			query.setFirstResult((currPage - 1) * pageSize);
			query.setMaxResults(pageSize);
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
	
	/**查询虚拟商品类型信息
	 * @author guoShiJie
	 * @createDate 2018.5.24
	 * */
	public static List<t_mall_goods_type> queryAllVirtualGoodsType () {
		StringBuffer sql = new StringBuffer("select * from t_mall_goods_type where type = 1 ");
		
		Query query=JPA.em().createNativeQuery(sql.toString(), t_mall_goods_type.class);
		return query.getResultList();
	}
	
	/**
	 * 保存虚拟商品红包的详细信息
	 * @param redPacket 红包
	 * @author guoShiJie
	 * @createDate 2018.5.24
	 * */
	public static int saveRedPacketDetail(t_red_packet_virtual_goods redPacket) {
		if (redPacket == null) {
			return MallConstants.COM_ERROR_CODE;
		}
		if (redPacket.id != null) {
			redPacket = clone(redPacket);
		}
		try {
			redPacket.save();
			return MallConstants.STATUS_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存红包信息时:%s", e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		
	}
	/**
	 * 保存虚拟商品加息券的详细信息
	 * @param coupon 加息券
	 * @author guoShiJie
	 * @createDate 2018.5.24
	 * */
	public static int saveCouponDetail(t_add_rate_ticket_virtual_goods addRateTicket){
		if (addRateTicket == null) return MallConstants.COM_ERROR_CODE;
		if (addRateTicket.id != null) {
			addRateTicket = clone(addRateTicket);
		}
		try {
			addRateTicket.save();
			return MallConstants.STATUS_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存加息券时: %s", e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
	}
	/**
	 * 克隆红包信息
	 * @param redPacket 红包
	 * @author guoShiJie
	 * @createDate 2018.5.24
	 * */
	public static t_red_packet_virtual_goods clone(t_red_packet_virtual_goods redPacket) {
		if (redPacket == null) return null;
		if (redPacket.id == null) return redPacket;
		t_red_packet_virtual_goods redPacket_ = queryRedPacketDetailById(redPacket);
		redPacket_._key = redPacket._key;
		redPacket_.red_packet_name = redPacket.red_packet_name;
		redPacket_.limit_day = redPacket.limit_day;
		redPacket_.time = redPacket.time;
		redPacket_.use_rule = redPacket.use_rule;
		redPacket_.amount = redPacket.amount;
		redPacket_.bid_period = redPacket.bid_period;
 		return redPacket_;
	}
	
	/**
	 * 克隆加息券信息
	 * @param coupon 加息券
	 * @author guoShiJie
	 * @createDate 2018.5.24
	 * */
	public static t_add_rate_ticket_virtual_goods clone (t_add_rate_ticket_virtual_goods addRateTicket) {
		if (addRateTicket == null) return null;
		if (addRateTicket.id == null) return addRateTicket; 
		t_add_rate_ticket_virtual_goods addRateTicket_ = queryCouponDetailById(addRateTicket);
		addRateTicket_.apr = addRateTicket.apr;
		addRateTicket_.day = addRateTicket.day;
		addRateTicket_.amount = addRateTicket.amount;
		addRateTicket_.time = addRateTicket.time;
		
		return addRateTicket_;
	}
	/**
	 * 通过id查询红包的详细信息
	 * @param packet 红包
	 * @author guoShiJie
	 * @createDate 2018.5.24*/
	public static t_red_packet_virtual_goods queryRedPacketDetailById(t_red_packet_virtual_goods packet) {
		t_red_packet_virtual_goods redPacket = null;
		try {
			redPacket = t_red_packet_virtual_goods.findById(packet.id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询红包信息时: %s", e.getMessage());
		}
		return redPacket;
	}
	/**
	 * 通过id查询加息券的详细信息
	 * @param coupon 加息券
	 * @author guoShiJie
	 * @createDate 2018.5.24*/
	public static t_add_rate_ticket_virtual_goods queryCouponDetailById (t_add_rate_ticket_virtual_goods addRate) {
		t_add_rate_ticket_virtual_goods addRateTicket = null;
		try {
			addRateTicket = t_add_rate_ticket_virtual_goods.findById(addRate.id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询加息券时: %s", e.getMessage());
		}
		return addRateTicket;
	}
	
	/**
	 * 通过id查询商品类型
	 * @param
	 * @author guoShiJie
	 * @createDate 2018.05.28
	 * */
	public static String queryVirtualGoodsTypeById (long id) {
		
		t_mall_goods_type goodsType = t_mall_goods_type.findById(id);
		return goodsType.goods_type;
	}
	
	/**
	 * 查询最后一个添加的虚拟物品ID
	 * @param
	 * @author guoShiJie
	 * @createDate 2018.05.28
	 * */
	public static long queryLastVirtualGoodsId (String type) {
		long virtualId = 0;
		Query query = null;
		String sql = null;
		if ("红包".equals(type)) {
			sql = "select * from t_red_packet_virtual_goods order by id desc limit 1";
			query = JPA.em().createNativeQuery(sql, t_red_packet_virtual_goods.class);
			t_red_packet_virtual_goods goods = (t_red_packet_virtual_goods) query.getResultList().get(0);
			virtualId = goods.id;
		} else if ("加息券".equals(type)) {
			sql = "select * from t_add_rate_ticket_virtual_goods order by id desc limit 1";
			query = JPA.em().createNativeQuery(sql, t_add_rate_ticket_virtual_goods.class);
			t_add_rate_ticket_virtual_goods goods = (t_add_rate_ticket_virtual_goods)query.getResultList().get(0);
			virtualId = goods.id;
		}
		return virtualId;
	}
	
	/**
	 * 用户获取红包
	 * @author guoShiJie
	 * @createDate 2018.05.30
	 * */
	public static void exchangeRedPacket (t_red_packet_user redPacketUser,int exchangeNum,t_user user,t_mall_goods goods,ResultInfo error) {
		
		if (redPacketUser == null || user == null || goods == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return ;
		}
		try {
			t_red_packet_account account = t_red_packet_account.find("select new t_red_packet_account(id,time,user_id,balance) from t_red_packet_account where user_id = ?", user.id).first();
			if (account != null) {
				MallGoods.saveRedPacketUser(redPacketUser, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			}else {
				t_red_packet_account redAccount = new t_red_packet_account(new Date(),user.id,0.00);
				MallGoods.saveRedPacketAccount(redAccount, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
				MallGoods.saveRedPacketUser(redPacketUser, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			}
			t_red_packet_user packetUser = MallGoods.queryLastRedPacketUser();
			if (packetUser == null) {
				error.code = MallConstants.COM_ERROR_CODE;
				JPA.setRollbackOnly();
				return;
			}
			MallGoods.virtualExchangeHandle(packetUser,exchangeNum, user, goods, null, error);
			if (error.code < 0) {
				JPA.setRollbackOnly();
				return;
			}
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			error.code = MallConstants.DML_ERROR_CODE;
			e.printStackTrace();
			LoggerUtil.info(true,"用户获取积分商城红包时: %s", e.getMessage());
			
		}
		return;
	}
	
	/**
	 * 用户获取加息券
	 * @author guoShiJie
	 * @createDate 2018.05.30
	 * */
	public static void exchangeAddRateTicket (t_add_rate_ticket addRate,int exchangeNum,t_user user,t_mall_goods goods,ResultInfo error) {
		if (addRate == null || user == null || goods == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return ;
		}
		Date today = new Date();
		try {
			MallGoods.saveAddRateTicket(addRate, error);
			if (error.code < 0) {
				JPA.setRollbackOnly();
				return;
			}
			t_add_rate_ticket rate = MallGoods.queryLastAddRateTicket();
			if (rate == null) {
				error.code = MallConstants.COM_ERROR_CODE;
				JPA.setRollbackOnly();
				return;
			}
			t_add_rate_user rateUser = new t_add_rate_user();
			rateUser.stime = today;
			rateUser.etime = DateUtil.addDay(today, rate.day);
			rateUser.ticket_id = rate.id;
			rateUser.user_id = user.id;
			rateUser.status = 1;
			rateUser.channel = "兑换发放";
			
			MallGoods.saveAddRateUser(rateUser, error);
			if (error.code < 0) {
				JPA.setRollbackOnly();
				return;
			}
			t_add_rate_user addRateUser = MallGoods.queryLastAddRateUser();
			if (error.code < 0) {
				JPA.setRollbackOnly();
				return;
			}
			MallGoods.virtualExchangeHandle(addRateUser,exchangeNum, user, goods, null, error);
			if (error.code < 0) {
				JPA.setRollbackOnly();
				return;
			}
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			error.code = MallConstants.DML_ERROR_CODE;
			e.printStackTrace();
			LoggerUtil.info(true,"用户获取积分商城加息券时: %s", e.getMessage());
		}
		return ;
	}
	
	/**
	 * 数量更新(虚拟物品加息券 红包)
	 * @author guosShiJie
	 * @createdate 2018.05.31
	 * */
	public static void updateVirtualSurplus (int exchangeNum, long user_id, long goods_id, ResultInfo error) {
		t_mall_goods virtualGoods = t_mall_goods.findById(goods_id);
		if (virtualGoods == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		
		String sql = "update t_mall_goods g set g.surplus = g.surplus - ? where g.surplus >= ? "
				+ "and g.exchange_scroe * ? <= (SELECT SUM(scroe) FROM t_mall_scroe_record WHERE user_id = ?) and g.id = ?";
		JPAUtil.executeUpdate(error, sql, exchangeNum, exchangeNum, exchangeNum, user_id, goods_id);
		return;
	}
	
	/**
	 * 积分商城兑换(红包)
	 * @author guoShiJie
	 * */
	public static synchronized void virtualExchangeHandle (t_red_packet_user packetUser,int exchangeNum, t_user user, t_mall_goods goods, String postDetail, ResultInfo error) {
		if (user == null || goods == null || error == null || packetUser == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}

		try {
			if (goods.total >= 0) {
				//数量更新(有数量限制)
				MallGoods.updateVirtualSurplus(exchangeNum, user.id, goods.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return ;
				}
				//保存记录
				MallScroeRecord.saveScroeExchangeRecord(exchangeNum, user, goods, postDetail, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
				t_mall_scroe_record record = MallScroeRecord.queryLastMallScoreRecord();
				if (record == null) {
					error.code = MallConstants.COM_ERROR_CODE;
					JPA.setRollbackOnly();
					return;
				}
				MallScroeRecord.saveVirtualGoodsRecords(user.id, record.id, packetUser.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			} else if (goods.total == -1) {
				//保存记录(无数量限制)
				MallScroeRecord.saveScroeExchangeRecord(exchangeNum, user, goods, postDetail, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return ;
				}
				
				t_mall_scroe_record record = MallScroeRecord.queryLastMallScoreRecord();
				if (record == null) {
					error.code = MallConstants.COM_ERROR_CODE;
					JPA.setRollbackOnly();
					return;
				}
				MallScroeRecord.saveVirtualGoodsRecords(user.id, record.id, packetUser.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			}
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			e.getStackTrace();
			LoggerUtil.info(true,"数量更新时: %s", e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
		return ;
	}
	
	/**
	 * 积分商城兑换(加息券)
	 * @author guoShiJie
	 * */
	public static synchronized void virtualExchangeHandle (t_add_rate_user rateUser,int exchangeNum, t_user user, t_mall_goods goods, String postDetail, ResultInfo error) {
		if (user == null || goods == null || error == null || rateUser == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		
		try {
			if (goods.total >= 0) {
				//数量更新(有数量限制)
				MallGoods.updateVirtualSurplus(exchangeNum, user.id, goods.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return ;
				}
				//保存记录
				MallScroeRecord.saveScroeExchangeRecord(exchangeNum, user, goods, postDetail, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
				t_mall_scroe_record record = MallScroeRecord.queryLastMallScoreRecord();
				if (record == null) {
					error.code = MallConstants.COM_ERROR_CODE;
					JPA.setRollbackOnly();
					return;
				}
				MallScroeRecord.saveVirtualGoodsRecords(user.id, record.id, rateUser.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			} else if (goods.total == -1) {
				//保存记录(无数量限制)
				MallScroeRecord.saveScroeExchangeRecord(exchangeNum, user, goods, postDetail, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
				
				t_mall_scroe_record record = MallScroeRecord.queryLastMallScoreRecord();
				if (record == null) {
					error.code = MallConstants.COM_ERROR_CODE;
					JPA.setRollbackOnly();
					return;
				}
				MallScroeRecord.saveVirtualGoodsRecords(user.id, record.id, rateUser.id, error);
				if (error.code < 0) {
					JPA.setRollbackOnly();
					return;
				}
			}
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			LoggerUtil.info(true,"数量更新时: %s", e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
	}
	
	/**
	 * 获取最后一个添加的t_add_rate_ticket
	 * @author guoShiJie
	 * @createDate 2018.06.01
	 * */
	public static t_add_rate_ticket queryLastAddRateTicket() {
		String sql = "select * from t_add_rate_ticket order by id desc limit 1";
		Query query = JPA.em().createNativeQuery(sql, t_add_rate_ticket.class);
		t_add_rate_ticket addRate = (t_add_rate_ticket)query.getResultList().get(0);
		return addRate;
	}
	
	/**
	 * 保存红包用户表
	 * @author guoShiJie
	 * */
	public static void saveRedPacketUser (t_red_packet_user redPacketUser,ResultInfo error) {
		if (redPacketUser == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		try {
			redPacketUser.save();
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			Logger.error("保存红包用户表时：" + e.getMessage());
			error.code = MallConstants.DML_ERROR_CODE;
		}
		return;
	}
	
	/**
	 * 保存用户红包账单表
	 * @author guoShiJie
	 * */
	public static void saveRedPacketAccount (t_red_packet_account redPacketAccount,ResultInfo error) {
		if (redPacketAccount == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		try {
			redPacketAccount.save();
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			error.code = MallConstants.DML_ERROR_CODE;
		}
		return;
	}
	
	/**保存加息券表
	 * @author guoShiJie
	 * */
	public static void saveAddRateTicket (t_add_rate_ticket addRateTicket,ResultInfo error) {
		if (addRateTicket == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		
		try {
			addRateTicket.save();
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			error.code = MallConstants.DML_ERROR_CODE;
		}
		return;
	}
	
	/**
	 * 保存用户加息券表
	 * @author guoShiJie
	 * */
	public static void saveAddRateUser (t_add_rate_user addRateUser,ResultInfo error) {
		if (addRateUser == null || error == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			return;
		}
		
		try {
			addRateUser.save();
			error.code = MallConstants.SUCCESS_CODE;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			e.printStackTrace();
			error.code = MallConstants.DML_ERROR_CODE;
		}
		return;
	}
	
	/**
	 * 查询最后一个用户红包表
	 * @author guoShiJie
	 * @createDate 2018.06.06
	 * */
	public static t_red_packet_user queryLastRedPacketUser () {
		String sql = "select * from t_red_packet_user order by id desc limit 1 ";
		Query query = JPA.em().createNativeQuery(sql, t_red_packet_user.class);
		List<t_red_packet_user> packetUser = query.getResultList();
		if (packetUser != null) {
			return packetUser.get(0);
		}
		return null;
	}
	/**
	 * 查询最后一个用户加息券表
	 * @author guoShiJie
	 * @createDate 2018.06.06
	 * */
	public static t_add_rate_user queryLastAddRateUser () {
		String sql = "select * from t_add_rate_user order by id desc limit 1";
		Query query = JPA.em().createNativeQuery(sql, t_add_rate_user.class);
		List<t_add_rate_user> rateUser = query.getResultList();
		if (rateUser != null) {
			return rateUser.get(0);
		}
		return null;
	}

	/**
	 * 查询积分商城的商品
	 * @author guoShiJie
	 * @createDate 2018.11.9
	 * */
	public static t_mall_goods queryGoodsById (Long goodsId) {
		if (goodsId == null) {
			return null;
		}
		t_mall_goods goods = t_mall_goods.findById(goodsId);
		
		return goods;
	}
}
