package controllers.finance;

import common.utils.Factory;
import controllers.common.BaseController;
import services.finance.OrganizationGeneralizeService;

public class OrganizationGeneralizeController extends BaseController  {
	protected static OrganizationGeneralizeService organizationGeneralizeService =  Factory.getService(OrganizationGeneralizeService.class);
	
	/**
	 * 
	 * @Title: createOrganizationGeneralizeXml
	 *
	 * @description 调用OrganizationGeneralizeService方法生成Xml文件
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月15日
	 */
	public static void createOrganizationGeneralizeXml () {
		organizationGeneralizeService.createOrganizationGeneralizeXml();
	}
}
