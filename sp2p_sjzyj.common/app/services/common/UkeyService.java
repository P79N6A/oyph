package services.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.UkeyDao;
import models.common.entity.t_ukey;
import services.base.BaseService;

/**
 * 加密锁业务类
 * 
 * @author niu
 * @date 2018-03-23
 */
public class UkeyService extends BaseService<t_ukey> {

	protected UkeyDao ukeyDao = Factory.getDao(UkeyDao.class);
	
	public UkeyService() {
		super.basedao = ukeyDao;
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
	public ResultInfo saveUkey(String ukeySn, String ukeyId) {
		
		ResultInfo result = new ResultInfo();
		
		t_ukey ukey = ukeyDao.findByColumn(" t.ukey_sn = ? OR t.ukey_id = ? ", ukeySn, ukeyId);
		
		if (ukey != null) {
			result.code = -1;
			result.msg = "加密锁已存在";
			
			return result;
		}
		
		if (!ukeyDao.saveUkey(ukeySn, ukeyId)) {
			result.code = -1;
			result.msg = "加密锁保存失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "加密锁保存成功";
		
		return result;
	}
	
	/**
	 * 加密锁分页查询
	 * 
	 * @author niu
	 * @date 2018-03-23
	 */
	public PageBean<t_ukey> pageOfUkey(int currPage, int pageSize) {
		
		return ukeyDao.pageOfUkey(currPage, pageSize);
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
		return ukeyDao.updateUkeyStatus(ukeyId, stat);
	}
	
	
	public ResultInfo setUkey(String ukeyId, String ckey, String backAcct) {
		ResultInfo result = new ResultInfo();
		
		t_ukey ukey = ukeyDao.geUkeyByUkeyId(ukeyId);
		if (ukey == null) {
			result.code = -1;
			result.msg  = "查询不到加密锁";
			
			return result;
		}
		
		ukey.ukey_ckey = ckey;
		ukey.ukey_stat = 1;
		ukey.ukey_user = backAcct;
		
		ukey = ukey.save();
		
		if (ukey == null) {
			result.code = -1;
			result.msg  = "设置加密锁失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "设置加密锁成功";
		
		return result;
	}
	
	
	
}



















