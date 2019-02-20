package daos.common;

import java.util.List;

import daos.base.BaseDao;
import models.common.entity.t_template_official_notice;

/**
 * 官方公告模板Dao的具体实现
 * 
 * @author guoShiJie
 * @createDate 2018年5月8日*/
public class TemplateOfficialNoticeDao extends BaseDao<t_template_official_notice>{

	protected TemplateOfficialNoticeDao () {
		
	}
	
	public long selectLastTemplateOfficialNotice(){
		String sql = "select id from t_template_official_notice order by id desc limit 1 ";
		
		return this.findBySQL(sql, null).id;
	}
}
