package daos.common;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_credit_level;
import daos.base.BaseDao;

/**
 * 信用等级dao实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
public class CreditLevelDao extends BaseDao<t_credit_level> {

	protected CreditLevelDao() {}
	
	/**
	 * 修改信用等级
	 * 
	 * @param id 信用等级id
	 * @param name 信用等级名称
	 * @param imageUrl 图片URL
	 * @param imageResolution 图片分辨率
	 * @param imageSize 图片大小
	 * @param imageFormat 图片格式
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 *
	 */
	public int updateCreditLevel(long id, String name, String imageUrl,
			String imageResolution, String imageSize, String imageFormat) {
		String sql = "UPDATE t_credit_level SET name=:name, image_url=:imageUrl,image_resolution =:imageResolution, image_size =:imageSize,image_format =:imageFormat  WHERE id=:id ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("name", name);
		condition.put("imageUrl", imageUrl);
		condition.put("imageResolution", imageResolution);
		condition.put("imageSize", imageSize);
		condition.put("imageFormat", imageFormat);
		
		return updateBySQL(sql, condition);
	}
	
}
