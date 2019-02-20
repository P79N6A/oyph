package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.common.entity.t_information;
import common.enums.InformationMenu;
import common.enums.IsUse;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 资讯管理 实现Dao
 *
 * @author liudong
 * @createDate 2015年12月28日
 */

public class InformationDao extends BaseDao<t_information> {

	protected InformationDao() {}
	
	/**
	 * 资讯阅读  次数增加
	 *
	 * @param id 资讯id 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int addInformationReadCount(long id) {
		String sql="UPDATE t_information SET read_count = (read_count + 1) WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}

	/**
	 * 资讯阅读  点赞次数增加
	 *
	 * @param id 资讯id 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int addInformationSupportCount(long id) {
		String sql="UPDATE t_information SET support_count = (support_count + 1) WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 查询具体资讯内容  (过滤未上架，未到发布时间的)
	 *
	 * @param id 资讯内容id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月8日
	 */
	public t_information findInformationByIdFront(long id){
		String sql="SELECT * FROM t_information WHERE is_use=:isUse AND show_time < :nowTime AND id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", IsUse.USE.code);
		condition.put("nowTime", new Date());
		condition.put("id", id);
		
		return super.findBySQL(sql, condition);
	}

	/**
	 * 查询借款协议的标题
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月20日
	 */
	public String findLoanPactTitle() {
		
		String sql = "SELECT title FROM t_information WHERE column_key=:columnKey AND is_use=:isUse ORDER BY order_time DESC ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("columnKey", InformationMenu.LOAN_AGREEMENT.code);
		params.put("isUse", true);
		
		return super.findSingleStringBySQL(sql, "", params);
	}
	
	/**
	 * 根据消息id 查询点赞次数
	 *
	 * @param id 消息资讯id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public String findReadCountById(long id) {
		String sql = "SELECT support_count AS support_count FROM t_information WHERE id=:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return findSingleStringBySQL(sql, "0",condition);
	}
	
	/**
	 * 资讯管理 上下架
	 *
	 * @param id  资讯id 
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int isUseInformation(long id, boolean isUse) {
		String sql = "UPDATE t_information SET is_use=:isUse WHERE id=:id";		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", isUse);
		condition.put("id", id);
	
		return this.updateBySQL(sql, condition);
	}
  	
	/**
	 * 资讯管理 列表查询
	 * 
	 * @param InformationMenu  资讯管理 左侧菜单(为空查询所有栏目)
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param inforTitle 资讯标题
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_information> pageOfInformationBack(InformationMenu informationMenu, int currPage, int pageSize, String inforTitle) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_information WHERE 1=1 ");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(1) FROM t_information WHERE 1=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if(informationMenu != null){
			sql.append(" AND column_key =:columnKey");
			sqlCount.append(" AND column_key =:columnKey");
			condition.put("columnKey", informationMenu.code);
		}
		
		/* 按资讯标题搜索  */
		if (StringUtils.isNotBlank(inforTitle)) {
			sql.append(" AND title like :title");
			sqlCount.append(" AND title like :title");
			condition.put("title", "%"+inforTitle+"%");
		}
		
		sql.append(" ORDER BY order_time DESC");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}

	/**
	 * 分页查询 ， 资讯管理 列表查询  针对若干个栏目统计(多个栏目统计)
	 * (查询时，有限制条件：上架，已发布(预发布筛选出来))
	 * 
	 * @param list  多个栏目key拼接的list
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public PageBean<t_information> pageOfInformationFront(List<String> list,int currPage, int pageSize, String keywords) {
		Map<String, Object> condition = new HashMap<String, Object>();
		StringBuffer querySQL = new StringBuffer("select * from t_information where column_key in(:list) and show_time <= :nowTime and is_use = true");
		StringBuffer countSQL = new StringBuffer("select count(1) from t_information where column_key in(:list) and show_time <= :nowTime and is_use = true");
		if (StringUtils.isNotBlank(keywords)) {
			/* 按标题或关键字搜索 */
			querySQL.append(" and (title like :keywords or keywords like :keywords)");
			countSQL.append(" and (title like :keywords or keywords like :keywords)");
			condition.put("keywords", "%"+keywords+"%");
		}
		querySQL.append(" order by order_time desc");
		condition.put("list", list);
		condition.put("nowTime", new Date());
		
		return pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), condition);
	}

	/**
	 * 统计具体一个栏目的前若干篇   （针对单一栏目进行统计）
	 *
	 * @param informationMenu 某个栏目的key
	 * @param limit 查询条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public List<t_information> queryInformationFront(InformationMenu informationMenu, int limit) {
		String sql = "SELECT * FROM t_information WHERE show_time <= :nowTime AND is_use=true AND column_key=:columnKey ORDER BY order_time DESC LIMIT :limit";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("nowTime", new Date());
		condition.put("columnKey", informationMenu.code);
		condition.put("limit", limit);
		
		return this.findListBySQL(sql, condition);
	}

	/**
	 * 不分页 统计资讯栏目中  多个栏目中的前若干篇  （针对多个栏目混合统计）
	 *
	 * @param list 多个栏目key拼接的list 
	 * @param limit 查询条数
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public List<t_information> queryInformationsFront(List<String> list,
			int limit) {
		String sql = "SELECT * FROM t_information WHERE show_time <= :nowTime AND is_use=true AND column_key in (:list) ORDER BY order_time DESC LIMIT :limit";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("nowTime", new Date());
		condition.put("list", list);
		condition.put("limit", limit);
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 查询本条资讯的下一条(注意所在栏目目要相同)
	 *
	 * @param val 
	 * @param scale
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public Map<String, Object> queryInformationNext(long id) {
		/**
		    SELECT
				title AS title,
				id AS id
			FROM
				t_information
			WHERE
				column_key = (SELECT column_key FROM t_information WHERE id =: id )
			AND show_time <= :nowTime
			AND is_use = TRUE
			AND order_time > (SELECT order_time FROM t_information WHERE id =: id )
			ORDER BY
				order_time ASC
			LIMIT 1 
		 */
		String sql = "SELECT title AS title,id AS id FROM t_information WHERE column_key = (SELECT column_key FROM t_information WHERE id =:id ) AND show_time <= :nowTime AND is_use = TRUE AND order_time > (SELECT order_time FROM t_information WHERE id =:id ) ORDER BY order_time ASC LIMIT 1 ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("nowTime", new Date());
		
		return findMapBySQL(sql, condition);
	}
	
	/**
	 * 查询本条资讯的 上一条(注意所在栏目要相同)
	 *
	 * @param val 
	 * @param scale
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public Map<String, Object> queryInformationUp(long id) {
		/**
		  	SELECT
				title AS title,
				id AS id
			FROM
				t_information
			WHERE
				column_key = (SELECT column_key FROM t_information WHERE id =: id )
			AND show_time <= :nowTime
			AND is_use = TRUE
			AND order_time < (SELECT order_time FROM t_information WHERE id =: id )
			ORDER BY
				order_time DESC
			LIMIT 1 

		 */
		String sql = "SELECT title AS title,id AS id FROM t_information WHERE column_key = (SELECT column_key FROM t_information WHERE id=:id) AND show_time <= :nowTime AND is_use=true AND order_time < (SELECT order_time FROM t_information WHERE id =:id ) ORDER BY order_time DESC LIMIT 1";		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("nowTime", new Date());
		
		return findMapBySQL(sql, condition);
	}
	
	/**
	 * 资讯管理 资讯编辑更新
	 *
	 * @param informationMenu 资讯管理左侧菜单
	 * @param title 标题
	 * @param sourceFrom 来源 
	 * @param content 内容
	 * @param orderTime 排序时间 
	 * @param readCount 查看次数
	 * @param supportCount 点赞次数
	 * @param imageUrl 图片路径 
	 * @param imageResolution 图片分辨率
	 * @param imageSize 文件大小 
	 * @param imageFormat 文件格式 
	 * @param showTime  发布时间 
	 * @param isUse 0-下架  1-上架
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public int updateInformation(long id,InformationMenu informationMenu, String title,
			String sourceFrom,  String keywords, String content, Date orderTime, String imageUrl, String imageResolution,
			String imageSize, String imageFormat, Date showTime) {
		String sql = "UPDATE t_information SET time=:createTime,column_key=:columnKey,title=:title,source_from=:sourceFrom,keywords=:keywords,content=:content,order_time=:orderTime,"
				+ "image_url=:imageUrl,image_resolution=:imageResolution, image_size=:imageSize,image_format=:imageFormat,show_time=:showTime WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("createTime", new Date());
		condition.put("columnKey", informationMenu.code);
		condition.put("title", title);
		condition.put("sourceFrom", sourceFrom);
		condition.put("keywords", keywords);
		condition.put("content", content);
		condition.put("orderTime", orderTime);
		condition.put("imageUrl", imageUrl);
		condition.put("imageResolution", imageResolution);
		condition.put("imageSize", imageSize);
		condition.put("imageFormat", imageFormat);
		condition.put("showTime", showTime);
		condition.put("id", id);
		
		return this.updateBySQL(sql, condition);
	}
	
	public List<t_information> queryRiskEducations(InformationMenu info , Boolean is_use,Integer limit) {
//		List<t_information> information = this.findListByColumn(" column_key = ? and is_use = ? ", info.code , is_use);
		String sql = "select * from t_information where column_key =:columnKey and is_use =:is_use limit :limit";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("is_use", is_use);
		condition.put("columnKey", info.code);
		condition.put("limit", limit);
		return this.findListBySQL(sql, condition);
	}
	
}
