package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.enums.Relationship;
import common.utils.Factory;
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import models.common.entity.t_user_info;
import models.finance.entity.t_cust_info;
import models.finance.entity.t_cust_org;
import models.finance.entity.t_org_info;
import services.common.UserInfoService;
import services.finance.CustOrgService;
import services.finance.OrgInfoService;

/**
 * 
 *
 * @ClassName: CustOrgCtrl
 * 
 * @description 客户关联信息Controller层
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-下午4:07:55
 */
public class CustOrgCtrl extends FrontBaseController {

	protected static final CustOrgService custOrgService = Factory.getService(CustOrgService.class);
	protected static final UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	/**
	 * 插入客户关联表数据
	 *
	 * @Title: saveCustOrg
	 * 
	 * @description
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * 
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午11:39:31
	 */
	public static void saveCustOrg() {
		/*custOrgService.truncateTable(); // 清空表数据
		long nowTime = System.currentTimeMillis() / 1000;
		Integer nowTimeInt = new Long(nowTime).intValue();
		List<t_user_info> listUserInfo = userInfoService.queryUserInfoAll();
		List<t_org_info> infos = orgInfoService.findAll();

		for (t_user_info userInfo : listUserInfo) {
			t_cust_org custOrg = new t_cust_org();
			custOrg.setData_date(nowTimeInt);
			custOrg.setOrg_num(infos.get(0).org_num); // 机构代码
			custOrg.setCust_id(Long.toString(userInfo.getUser_id()));
			// code = 2关联方为配偶
			if (userInfo.getEmergency_contact_type().code == 2) {
				custOrg.setRelation_id(userInfo.getMate_id_number()); // 关联方代码（配偶身份证号）
				custOrg.setRelation_type("207"); // 关联方类型（配偶）
				custOrg.setRelation_id_type((char) userInfo.getMate_papers_type()); // 关联方证件类型（身份证）
				custOrg.setRelation_id_no(userInfo.getMate_id_number()); // 关联方证件号码
				custOrg.setCust_telephone_no(userInfo.getMate_phone()); // 关联方联系电话
			}
			custOrg.save();
		}*/
		custOrgService.saveCustOrg();
	}

	/**
	 * 生成xml文档
	 *
	 * @Title: createXmlOfCustOrg
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月11日-上午10:22:59
	 */
	public static void createXmlByCustOrg() {
		custOrgService.createXmlByCustOrg();
	}

}
