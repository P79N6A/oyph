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

import common.utils.Factory;
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import models.common.entity.t_user_info;
import models.finance.entity.t_agre_guarantee;
import models.finance.entity.t_cust_info;
import models.finance.entity.t_org_info;
import services.common.UserInfoService;
import services.finance.CustInfoService;
import services.finance.OrgInfoService;

/**
 * 
 *
 * @ClassName: CustInfoCtrl
 * 
 * @description 客户信息Controller层
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-下午4:04:59
 */
public class CustInfoCtrl extends FrontBaseController {

	protected static final CustInfoService custInfoService = Factory.getService(CustInfoService.class);
	protected static final UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static final OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	/**
	 * 保存客户基本信息
	 *
	 * @Title: saveCustInfo
	 * 
	 * @description
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * 
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午9:22:21
	 */
	public static void saveCustInfo() {
		/*custInfoService.truncateTable(); // 清空表数据
		long nowTime = System.currentTimeMillis() / 1000;
		Integer nowTimeInt = new Long(nowTime).intValue();
		List<t_org_info> infos = orgInfoService.findAll();
		// 判断数据是否插入成功
		if (custInfoService.saveCustInfo()) {
			List<t_cust_info> ListCustInfo = custInfoService.findCustInfo();
			for (t_cust_info custInfo : ListCustInfo) {
				custInfo.setData_date(nowTimeInt);
				custInfo.setOrg_num(infos.get(0).org_num); // 机构代码

				custInfo.save();
			}
		}*/
		custInfoService.saveCustInfo();
	}

	/**
	 * 生成xml文档
	 *
	 * @Title: createXml
	 * 
	 * @description
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * 
	 * @author lujinpeng
	 * @createDate 2018年10月9日-下午6:00:36
	 */
	public static void createXmlByCustInfo() {
		
		custInfoService.createXmlByCustInfo();
	}

}
