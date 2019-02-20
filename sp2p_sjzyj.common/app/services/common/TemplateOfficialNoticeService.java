package services.common;

import common.utils.Factory;
import daos.common.TemplateOfficialNoticeDao;
import models.common.entity.t_template_official_notice;
import services.base.BaseService;

public class TemplateOfficialNoticeService extends BaseService<t_template_official_notice>{

	protected TemplateOfficialNoticeDao templateOfficialNoticeDao = null;
	
	protected TemplateOfficialNoticeService () {
		templateOfficialNoticeDao = Factory.getDao(TemplateOfficialNoticeDao.class);
		
		super.basedao = this.templateOfficialNoticeDao;
	}
	
	public long selectLastTemplateOfficialNotice() {
		return templateOfficialNoticeDao.selectLastTemplateOfficialNotice();
	}
}
