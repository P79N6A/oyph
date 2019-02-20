package services.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import play.Play;
import play.db.jpa.Blob;

import com.google.zxing.BarcodeFormat;
import com.shove.code.Qrcode;

import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_user;
import services.base.BaseService;
import common.enums.InformationMenu;
import common.enums.IsUse;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.StrUtil;
import daos.common.InformationDao;


public class InformationService extends BaseService<t_information> {

	protected static InformationDao informationDao = Factory.getDao(InformationDao.class);
	
	protected static ColumnService columnService = Factory.getService(ColumnService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected InformationService(){
		super.basedao = informationDao;
	}
	
	/**
	 * 添加资讯 
	 *
	 * @param information  资讯
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addInformation(t_information information) {
		information.time = new Date();
		information.setIs_use(IsUse.USE);
		
		return informationDao.save(information);
	}
	
	/**
	 * 资讯消息  阅读次数次数增加
	 *
	 * @param id 资讯id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addInformationReadCount(long id) {
		int row = informationDao.addInformationReadCount(id);
		if(row<=0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 资讯消息  点赞次数增加
	 *
	 * @param id 资讯id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addInformationSupportCount(long id) {
		int row = informationDao.addInformationSupportCount(id);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 资讯管理  资讯删除
	 *
	 * @param id 资讯消息id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean delInformation(long id) {
		int row = informationDao.delete(id);
		if(row<=0){
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 编辑资讯 
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
	public boolean updateInformation(long id, InformationMenu informationMenu, String title,String sourceFrom, String keywords, String content, 
			Date orderTime, String imageUrl, String imageResolution,String imageSize, String imageFormat, Date showTime) {
		int row = informationDao.updateInformation(id, informationMenu, title,
				sourceFrom, keywords, content, orderTime, imageUrl,
				imageResolution, imageSize, imageFormat, showTime);
		
		if(row<=0){
		
			return false;
		}
				
		return true;
	}

	/**
	 * 资讯管理  上下架
	 *
	 * @param id 资讯 id
	 * @param isUse 上下架
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean updateInformationisUse(long id, boolean isUse) {
		int row = informationDao.isUseInformation(id, isUse);
		if(row<=0){

			return false;
		}
		
		return true;
	}
	
	/**
	 * 查询资讯详情 已发布  已上架
	 *
	 * @param id 资讯id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public t_information findInformationByIdFront(long id) {
		
		return informationDao.findInformationByIdFront(id);
	}
	
	/**
	 * 查找最新的理财协议
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_information findInvestDeal() {
		t_information platform_register = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.INVEST_AGREEMENT_TEMPLATE.code,true);
		
		return platform_register;
	}
	
	/**
	 * 查找最新的委托理财协议
	 *
	 * @return
	 *
	 * @author lihuijun
	 * @createDate 2017年10月11日
	 */
	public t_information findInvestEntrustDeal(long userId) {
		t_information platform_register = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.INVEST_ENTRUEST_AGREEMENT.code,true);
		t_user user =userService.findByID(userId);
		if(platform_register!=null&&user!=null){
			Map<String, String> contentParam = new HashMap<String, String>();
			contentParam.put("procotol_no", "ouyepuhui"+userId);
			contentParam.put("mandator", user.name);
			platform_register.content = StrUtil.replaceByMap(platform_register.content, contentParam);
		}
		
		return platform_register;
	}

	
	/**
	 * 查找最新的一篇加入我们文章
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	public t_information findLastJoinus() {
		t_information joinus = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.HOME_JOINUS.code,true);
		
		return joinus;
	}
	
	/**
	 * 查找最新的一篇公司介绍
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	public t_information findLastProfile() {
		t_information profile = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.HOME_PROFILE.code,true);
		
		return profile;
	}
	
	/**
	 * 平台优势
	 * @return
	 */
	public t_information findLastAdvantage() {
		t_information profile = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.PLATFORM_ADVANTAGE.code,true);
		
		return profile;
	}
	
	/**
	 * 查找最新的用户借款协议
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_information findLoanDeal() {
		t_information platform_register = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.LOAN_AGREEMENT.code,true);
		
		return platform_register;
	}
	
	/**
	 * 查询借款协议的标题
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月20日
	 */
	public String findLoanPactTitle(){
				
		return informationDao.findLoanPactTitle();
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
		
		return informationDao.findReadCountById(id);
	}
	
	/**
	 * 查找最新的平台注册协议
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_information findRegisterDeal() {
		t_information platform_register = informationDao.findByColumn(" column_key=? and is_use=? order by order_time desc", InformationMenu.PLATFORM_REGISTER.code,true);
		
		return platform_register;
	}
	
	/**
	 * 统计具体一个栏目的前若干篇   （针对单一栏目进行统计）
	 *
	 * @param informationMenu 资讯管理栏目
	 * @param limit 查询条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public List<t_information> queryInformationFront(InformationMenu informationMenu, int limit) {
		
		return informationDao.queryInformationFront(informationMenu, limit);
	}
	
	/**
	 * 统计资讯中 多个栏目中的前若干篇  （针对多个栏目混合统计）
	 *
	 * @param list 所要查询的几个栏目list
	 * @param  limit 查询条数
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public List<t_information> queryInformationsFront(List<String> list,int limit) {
		
		return informationDao.queryInformationsFront(list, limit);
	}
	
	/**
	 * 分页查询 ，资讯管理 列表查询   informationMenu 为空时，查询所有栏目 ； 不为空时，查询具体某个栏目
	 * 
	 * @param informationMenu 资讯管理左侧菜单
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param inforTitle 资讯标题
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_information> pageOfInformationBack(InformationMenu informationMenu,int currPage, int pageSize, String inforTitle) {
		PageBean<t_information> page =  informationDao.pageOfInformationBack(informationMenu, currPage, pageSize, inforTitle);
		List<t_information> list = page.page;
		List<t_information> newList = new ArrayList<t_information>();
		if(page.page !=null ){
			for(int i=0;i<list.size();i++){
				t_information information = list.get(i);
				
				/** 投资协议模板 */
				if(information.column_key.equalsIgnoreCase(InformationMenu.INVEST_AGREEMENT_TEMPLATE.code)){
					information.column_name = InformationMenu.INVEST_AGREEMENT_TEMPLATE.value;
				}
				/** 委托投资协议*/
				else if(information.column_key.equalsIgnoreCase(InformationMenu.INVEST_ENTRUEST_AGREEMENT.code)){
					information.column_name = InformationMenu.INVEST_ENTRUEST_AGREEMENT.value;
				}
				/** 平台注册协议  */
				else if(information.column_key.equalsIgnoreCase(InformationMenu.PLATFORM_REGISTER.code)){
					information.column_name = InformationMenu.PLATFORM_REGISTER.value;
				}
				/** 借款协议 */
				else if(information.column_key.equalsIgnoreCase(InformationMenu.LOAN_AGREEMENT.code)){
					information.column_name = InformationMenu.LOAN_AGREEMENT.value;
				}
				else{
					information.column_name = columnService.findColumnBackNameByKey(information.column_key);
				}
				newList.add(information);
			}
		}	
		if(!newList.isEmpty()){
			page.page = newList;
		}
		
		return page;
	}
	
	/**
	 * 分页查询 资讯管理  列表查询  针对若干个栏目统计(查询时，有限制条件：上架，已发布(预发布筛选))
	 *  
	 * @param list 多个栏目key票拼接的list 
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public PageBean<t_information> pageOfInformationFront(List<String> list,int currPage,int pageSize, String keywords){
		
		return informationDao.pageOfInformationFront(list, currPage, pageSize, keywords);
	}
	
	/**
	 * 查询本条资讯的 上一条标题和下一条标题(注意所在项目要相同)
	 *
	 * @param id 资讯消息id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月13日
	 */
	public Map<String, Object> queryInformationUpAndNext(long id) {
		Map<String, Object> mapUp = informationDao.queryInformationUp(id);
		Map<String, Object> mapNext = informationDao.queryInformationNext(id);
		
		Map<String, Object> map = new HashMap<String, Object>();	
		if(mapUp != null){
			map.put("titleUp", mapUp.get("title"));
			map.put("idUp", mapUp.get("id"));
		}	
		if(mapNext != null){
			map.put("titleNext", mapNext.get("title"));
			map.put("idNext", mapNext.get("id"));
		}
		
		return map;
	}

	/**
	 * 风险教育
	 * */
	public List<t_information> queryRiskEducations(InformationMenu info , Boolean is_use,Integer limit) {
		return informationDao.queryRiskEducations(info, is_use, limit);
//		List<t_information> information = this.findListByColumn(" column_key = ? and is_use = ? ", info.code , is_use);
		
	}
}
