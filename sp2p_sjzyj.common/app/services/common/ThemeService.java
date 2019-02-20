package services.common;

import java.util.List;

import models.common.entity.t_theme;
import services.base.BaseService;

import common.utils.Factory;

import daos.common.ThemeDao;

/**
 * 主题service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月18日
 */
public class ThemeService extends BaseService<t_theme> {

	protected ThemeDao themeDao = null;
	
	protected ThemeService() {
		this.themeDao = Factory.getDao(ThemeDao.class);
		
		super.basedao = this.themeDao;
	}
	
	/**
	 * 根据主题文件夹名称查找该主题
	 *
	 * @param folder
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月18日
	 */
	public t_theme findThemeByFolder(String folder){
		t_theme theme = themeDao.findByColumn(" folder=? ", folder);
		
		return theme;
	}
	
	/**
	 * 查找最新的几个主题
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月18日
	 */
	public List<t_theme> queryLastTheme(){

		return themeDao.findAll();
	}
	
}
