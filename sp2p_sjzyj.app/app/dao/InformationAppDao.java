package dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.enums.InformationMenu;
import common.enums.IsUse;
import common.utils.PageBean;
import daos.common.InformationDao;
import models.app.bean.DebtTransferDetailBean;
import models.app.bean.InformationBean;

/**
 * 资讯模块AppDao
 * 
 * @description
 * 
 * @author guoshijie
 * @createdate 2017.12.15
 * */
public class InformationAppDao extends InformationDao{
	
	protected InformationAppDao() {
	}
	
	/**
	 * 根据资讯类型分页查询资讯
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param informationMenu 资讯管理 左侧菜单(为空查询所有栏目)
	 * @param title 资讯标题
	 * 
	 * @author guoshijie	
	 * @createdate 2017.12.15
	 * */
	public PageBean<InformationBean> queryInformation(InformationMenu informationMenu , int currPage , int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_information WHERE 1=1 ");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(1) FROM t_information WHERE 1=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if(informationMenu != null){
			sql.append(" AND column_key =:columnKey");
			sql.append(" AND is_use =:isUse");
			sql.append(" AND show_time < :nowTime");
			sqlCount.append(" AND column_key =:columnKey");
			sqlCount.append(" AND is_use =:isUse");
			sqlCount.append(" AND show_time < :nowTime");
			condition.put("columnKey", informationMenu.code);
			condition.put("isUse", IsUse.USE.code);
			condition.put("nowTime", new Date());
		}
		
		sql.append(" ORDER BY order_time DESC");
		return pageOfBeanBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(),InformationBean.class, condition);
	}
	
	/**
	 * 根据资讯id查询资讯详情
	 * 
	 * @param id 资讯id
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.15
	 * */
	public InformationBean findDetailById(long id) {
		String sql="SELECT * FROM t_information WHERE is_use=:isUse AND show_time < :nowTime AND id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("isUse", IsUse.USE.code);
		condition.put("nowTime", new Date());
		condition.put("id", id);
		
		return findBeanBySQL(sql, InformationBean.class, condition);
	}
	
}
