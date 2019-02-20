package services.ext.redpacket;

import java.util.List;

import common.utils.Factory;
import common.utils.Page;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.ext.redpacket.AddRateActDao;
import models.ext.redpacket.entity.t_add_rate_act;
import models.ext.redpacket.entity.t_add_rate_ticket;
import services.base.BaseService;

/**
 * 业务类：加息券活动
 * 
 * @author niu
 * @create 2017.10.27
 */
public class AddRateActService extends BaseService<t_add_rate_act> {
	
	protected AddRateActDao addRateActDao = null;
	
	protected AddRateActService() {
		this.addRateActDao = Factory.getDao(AddRateActDao.class);
		super.basedao = addRateActDao;
	}
	
	/**
	 * 加息券活动-添加
	 * 
	 * @author niu
	 * @create 2017.10.27
	 */
	public ResultInfo insertAct(t_add_rate_act act) {
		
		ResultInfo result = new ResultInfo();
		
		if (act == null) {
			result.code = -1;
			result.msg  = "添加对象不能为空";
			
			return result;
		}
		
		boolean is = addRateActDao.save(act);
		
		if (!is) {
			result.code = -1;
			result.msg  = "加息券活动添加失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "加息券活动添加成功";
		
		return result;
	}
	
	/**
	 * 加息券活动-查询所有
	 * 
	 * @author niu
	 * @create 2017.10.27
	 */
	public PageBean<t_add_rate_act> listOfAct(int currPage, int pageSize) {
		
		return addRateActDao.listOfAct(currPage, pageSize);
	}
	
	/**
	 * 降序查询所有 加息券活动(未开始的)
	 * 
	 * @author niu
	 * @create 2017.10.31
	 */
	public List<t_add_rate_act> listOfActStart() {
		
		return addRateActDao.listOfActStart();
	}
	
	/**
	 * 查询进行中的加息券活动（）
	 * 
	 * @author niu
	 * @create 2017.11.02
	 */
	public t_add_rate_act getAct() {
		
		return addRateActDao.getAct();
	}
	
	
	
	
	
	
	
	

}




























