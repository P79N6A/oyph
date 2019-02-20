package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_disclosure_month;
import models.core.entity.t_bid;

/**
 * 信息披露月dao实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
public class DisclosureMonthDao extends BaseDao<t_disclosure_month> {
	
	protected DisclosureMonthDao() {
		
	}
	
	
	/**
	 * 查询十二个月的月信息数据
	 * @param disclosureId
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年12月21日
	 */
	public List<t_disclosure_month> queryListById(long disclosureId) {
		
		String sql = "select * from t_disclosure_month where disclosure_id <=:disclosureId order by disclosure_id desc limit 12" ;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("disclosureId", disclosureId);
		
		return super.findListBySQL(sql, params);
	}
}
