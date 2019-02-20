package dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.entity.t_wechat_access_token;

public class AccessTokenDao extends BaseDao<t_wechat_access_token> {

	protected AccessTokenDao() {}
	
	/**
	 * 查询微信访问凭证
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年05月18日
	 */
	public t_wechat_access_token queryAccessToken() {
		String sql="SELECT * FROM t_wechat_access_token";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findBySQL(sql, condition);
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
		String sql = "UPDATE t_wechat_access_token SET token =:token,expire_in=:expire_in,time=:time where id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("token", accessToken.token);
		condition.put("expire_in", accessToken.expire_in);
		condition.put("time", new Date());
		condition.put("id", accessToken.id);
		
		return updateBySQL(sql, condition);
	}
}
