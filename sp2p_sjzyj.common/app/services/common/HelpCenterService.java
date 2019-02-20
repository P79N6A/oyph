package services.common;

import java.util.Date;

import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;
import services.base.BaseService;

import common.enums.IsUse;
import common.utils.Factory;
import common.utils.PageBean;

import daos.common.HelpCenterDao;


public class HelpCenterService extends BaseService<t_help_center> {

	protected static HelpCenterDao helpCenterDao = Factory.getDao(HelpCenterDao.class);;
	
	protected HelpCenterService() {
		super.basedao = this.helpCenterDao;
	}
	
	/**
	 * 添加 帮助中心的内容
	 *
	 * @param help 帮助中心
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public boolean addHelpCenter(t_help_center help) {
		help.time = new Date();
		help.setIs_use(IsUse.USE);

		return helpCenterDao.save(help);
	}
	
	/**
	 * 分页查询  查询帮助中心内容(会过滤掉未上架的部分)
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param column 栏目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月30日
	 */
	public  PageBean<t_help_center> pageOfHelpCenterFront(int currPage,int pageSize, Column column) {
		
		return helpCenterDao.pageOfHelpCenterFront(currPage, pageSize, column);
		
	}
	
	/**
	 * 分页查询  帮助中心列表查询 
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param column 栏目  为null时查询所有
	 * @param helpTitle 问题标题
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public PageBean<t_help_center> pageOfHelpCenterBack(int currPage,int pageSize, Column column,String helpTitle) {
		
		return helpCenterDao.pageOfHelpCenterBack(currPage, pageSize, column, helpTitle);
	}
	

	/**
	 * 更新 帮助中心内容
	 *
	 * @param id 帮助中心id
	 * @param columnNo 栏目 
	 * @param title 标题
	 * @param content 内容答案
	 * @param orderTime 排序时间
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public boolean upadteHelpCenter(long id, t_help_center.Column columnNo, String title,String content, Date orderTime) {
		int row = helpCenterDao.upadteHelpCenter(id, columnNo, title, content, orderTime);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}

	/**
	 * 上下架
	 * 
	 * @param id 帮助中心id
	 * @param isUse true上架   false下架 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public boolean upadteHelpCenterIsUse(long id,boolean isUse) {
		int row = helpCenterDao.upadteHelpCenterIsUse(id, isUse);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}
}
