package services.common;

import java.util.Date;
import java.util.List;

import models.common.entity.t_partner;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.PartnerDao;

public class PartnerService extends BaseService<t_partner> {

	protected static PartnerDao partnerDao = Factory.getDao(PartnerDao.class);

	protected PartnerService() {
		super.basedao = this.partnerDao;
	}
	
	/**
	 * 添加 合作伙伴 
	 *
	 * @param partner 合作伙伴 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean addPartner(t_partner partner) {
		partner.time = new Date();
		
		return partnerDao.save(partner);		
	}
	
	/**
	 * 合作伙伴 编辑 
	 *
	 * @param name  名称
	 * @param orderTime 排序时间
	 * @param imageUrl 图片路径
	 * @param imageResolution 图片分辨率
	 * @param imageSize 文件大小 
	 * @param imageFormat 文件格式
	 * @param url  合作伙伴链接
	 * @param target  链接打开方式  true-_self, false-_blank
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean upadtePartner(long id, String name, Date orderTime,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String url, int target) {
		int row = partnerDao.updatePartner(id, name, orderTime, imageUrl,
				imageResolution, imageSize, imageFormat, url, target);
		
		if(row<=0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 合作伙伴 删除
	 *
	 * @param id 合作伙伴id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public boolean delPartner(long id) {
		int row = partnerDao.delete(id);
		if(row<=0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 获取合作伙伴前几条 不分页
	 *
	 * @param limit 查询条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public List<t_partner> queryPartnersFront(int limit) {
		
		return partnerDao.queryPartnersFront(limit);
	}
	
	/**
	 * 合作伙伴查询  列表
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页的条数
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public PageBean<t_partner> pageOfPartnerBack(int currPage, int pageSize) {
		
		return partnerDao.pageOfPartnerBack(currPage, pageSize);
	}

}
