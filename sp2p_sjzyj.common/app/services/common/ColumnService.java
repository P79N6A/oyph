package services.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_column;
import play.cache.Cache;
import services.base.BaseService;

import common.interfaces.ICacheable;
import common.utils.Factory;

import daos.common.ColumnDao;

/**
 * 前台栏目设置的service的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月29日
 */
public class ColumnService extends BaseService<t_column> implements ICacheable {

	protected ColumnDao columnDao = null;
	
	protected ColumnService() {
		this.columnDao = Factory.getDao(ColumnDao.class);
		
		super.basedao = columnDao;
		
	}
	
	/**
	 * 根据键名取得前台栏目的实体
	 *
	 * @param key 前台栏目的key
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月30日
	 */
	public t_column findColumnByKey(String key) {
		Map<String, t_column> columnMap = getCache();
		if (columnMap == null || columnMap.size() == 0 ||columnMap.keySet() == null || columnMap.keySet().size() == 0) {
			
			return null;
		}
		
		return columnMap.get(key);
	}

	/**
	 * 根据键名取得前台栏目的名称(返回的是name， 不是back_name)
	 *
	 * @param key 前台栏目的key
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月30日
	 */
	public String findColumnNameByKey(String key) {
		t_column column = findColumnByKey(key);
		if (column == null) {
			
			return null;
		}
		
		return column.name;
	}
	

	/**
	 * 根据键名取得前台栏目的名称(返回的是name， 不是back_name)
	 *
	 * @param key 前台栏目的key
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月29日
	 */
	public String findColumnBackNameByKey(String key) {
		t_column column = findColumnByKey(key);
		if (column == null) {
			
			return null;
		}
		
		return column.back_name;
	}
	
	/**
	 * 查询所有的前台栏目，以map的形式返回
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月29日
	 */
	public Map<String, String> queryAllColumsMap() {
		Map<String, t_column> map = queryAllColumns();
		if (map == null || map.keySet() == null || map.keySet().size() == 0) {
			
			return null;
		}
		
		Map<String, String> columnMap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			columnMap.put(key, map.get(key).name);
		}

		return columnMap;
	}

	/**
	 * 查询出所有的栏目以键值对的形式返回
	 *
	 * @return key:栏目标识(具体见ColumnKey类)，value:栏目信息
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月8日
	 */
	public Map<String, t_column> queryAllColumns() {
		return getCache();
	}
	
	/**
	 * 更新键名为 column_key的栏目名称为columnName
	 *
	 * @param column_key 前台栏目的key
	 * @param columnName 更新后前台栏目的名称
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月30日
	 */
	public boolean updateColumnName(String column_key, String columnName) {
		t_column column = columnDao.findByColumn("column_key = ?", column_key);
		if (column == null) {

			return false;
		}
		
		column.name = columnName;
		boolean isUpdated = columnDao.save(column);
		if (!isUpdated) {
			return false;
		}
		
		addAFlushCache();
		
		return true;
	}

	@Override
	public void addAFlushCache() {
		Map<String, t_column> columnMap = null;
		
		List<t_column> columns = columnDao.findAll();
		if (columns != null && columns.size() > 0) {
			columnMap = new HashMap<String, t_column>();
			for (t_column column : columns) {
				columnMap.put(column.column_key, column);
			}
		}
		
		Cache.safeSet(this.getClass().getName(),columnMap, null);
	}

	@Override
	public Map<String, t_column> getCache() {
		Map<String, t_column> columnMap = (Map<String, t_column>) Cache.get(this.getClass().getName());
		if (columnMap == null || columnMap.keySet() == null || columnMap.keySet().size() == 0) {
			addAFlushCache();
			
			columnMap = (Map<String, t_column>) Cache.get(this.getClass().getName());
		}

		return columnMap;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
}
