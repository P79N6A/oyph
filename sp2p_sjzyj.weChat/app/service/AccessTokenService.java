package service;

import java.util.List;

import common.utils.Factory;
import dao.AccessTokenDao;
import models.entity.t_wechat_access_token;
import services.base.BaseService;

/**
 * 微信凭证service实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月18日
 */
public class AccessTokenService extends BaseService<t_wechat_access_token> {
	
	private AccessTokenDao accessTokenDao;
	
	protected AccessTokenService() {
		
		accessTokenDao = Factory.getDao(AccessTokenDao.class);
		super.basedao = accessTokenDao;
	}

	/**
	 * 查询微信访问凭证
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年05月18日
	 */
	public t_wechat_access_token queryAccessToken() {
		
		return accessTokenDao.queryAccessToken();
		
	}
	
	/**
	 * 修改更新微信访问凭证
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月18日
	 *
	 */
	public int updateAccessToken(t_wechat_access_token accessToken) {
		
		return accessTokenDao.updateAccessToken(accessToken);
		
	}
}
