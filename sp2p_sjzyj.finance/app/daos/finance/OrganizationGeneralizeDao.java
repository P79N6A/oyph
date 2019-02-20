package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_organization_generalize;

/**
 * 
 *
 * @ClassName: OrganizationGeneralizeDao
 *
 * @description 机构概括Dao接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class OrganizationGeneralizeDao extends BaseDao<t_organization_generalize> {

	/**
	 * 
	 * @Title: listAll
	 *
	 * @description 用一句话描述这个方法的作用
	 *
	 * @param @return 
	 * 
	 * @return 查询 t_organization_generalize所有信息 
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月15日
	 */
	public List <t_organization_generalize> listAll () {
		String sql = " SELECT tog.* FROM t_organization_generalize tog ";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	
	
	
}
