package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_ukey;

/**
 * 加密锁数据库操作类
 * 
 * @author niu
 * @date 2018-03-23 
 */
public class UkeyDao extends BaseDao<t_ukey> {

	public UkeyDao() {
		
	}
	
	/**
	 * 加密锁添加
	 * 
	 * @param ukeySn 加密锁SN号
	 * @param ukeyId 加密锁ID
	 * 
	 * @return 保存成功返回true,保存失败返回 false.
	 * 
	 * @author niu
	 * @date 2018-03-23
	 */
	public boolean saveUkey(String ukeySn, String ukeyId) {
		
		t_ukey ukey = new t_ukey();
		ukey.ukey_sn = ukeySn;
		ukey.ukey_id = ukeyId;
		
		return ukey.save() != null;
	}
	
	/**
	 * 加密锁分页查询
	 * 
	 * @author niu
	 * @date 2018-03-23
	 */
	public PageBean<t_ukey> pageOfUkey(int currPage, int pageSize) {
		
		String querySQL = "SELECT * FROM t_ukey t ORDER BY t.ukey_sn ASC";
		String countSQL = "SELECT COUNT(t.id) FROM t_ukey t ORDER BY t.ukey_sn ASC";
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, t_ukey.class, null);
	}
	
	/**
	 * 修改加密锁状态
	 * 
	 * @param stat	状态
	 * @return
	 * 
	 * @author niu
	 * @date 2018-03-26
	 */
	public boolean updateUkeyStatus(long ukeyId, int stat) {
		String sql = "UPDATE t_ukey SET ukey_stat = :stat WHERE id = :ukeyId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("stat", stat);
		condition.put("ukeyId", ukeyId);
		
		return updateBySQL(sql, condition) == 1;
	}
	
	/**
	 * 通过加密锁SN号查找加密锁
	 * 
	 * @param ukeySn
	 * @return
	 * 
	 * @author niu
	 * @date 2018-03-26
	 */
	public t_ukey getUkey(String ukeySn) {
		
		String sql = "SELECT * FROM t_ukey t WHERE t.ukey_sn = :ukeySn";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("ukeySn", ukeySn);
		
		return findBySQL(sql, condition);
	}
	
	/**
	 * 
	 * @param ukeyId
	 * @return
	 * 
	 * @author niu
	 * @date 2018-03-26
	 */
	public t_ukey geUkeyByUkeyId(String ukeyId) {
		
		String sql = "SELECT * FROM t_ukey t WHERE t.ukey_id = :ukeyId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("ukeyId", ukeyId);
		
		return findBySQL(sql, condition);
	}

	
}























