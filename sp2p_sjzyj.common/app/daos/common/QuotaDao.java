package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.enums.IsOverdue;
import daos.base.BaseDao;
import models.common.entity.t_quota;
import models.core.entity.t_bill.Status;

/**
 * 投资限额Dao
 * 
 * @author liuyang
 * @createDate 2018.01.23
 */
public class QuotaDao extends BaseDao<t_quota> {

	protected QuotaDao() {}
	
	public List<t_quota> queryQuotaesByType(int showType) {
		
		StringBuffer sql = new StringBuffer("SELECT * from t_quota ");		
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		//筛选类型
		switch (showType) {
			case 0://1:所有
				break;
			
			case 1://2:通过
				sql.append(" where type=:type ");
		    	condition.put("type", 1);
				break;
				
			case 2:////3:待审核
				sql.append(" where type=:type");
		    	condition.put("type", 0);
				break;
		}
		
		return findListBySQL(sql.toString(), condition);
	}
}
