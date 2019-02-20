package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.DateUtil;
import common.utils.Factory;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import jobs.IndexStatisticsJob;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.finance.bean.UserInfoBean;
import models.finance.entity.t_max_ten_user_information;
import models.finance.entity.t_user_information;
import services.common.UserInfoService;
import services.core.BidService;
import services.core.BillService;
import services.finance.MaxTenUserInformationService;
import services.finance.UserInfoBeanService;

/**
 * @ClassName: MaxTenUserInformationCtrl
 *
 * @description (P05 河北省P2P机构最大十家客户信息表)Controller层
 *
 * @author yaozijun
 * @createDate 2018年10月6日
 */
public class MaxTenUserInformationCtrl extends BaseController {
	protected static MaxTenUserInformationService maxTenUserInformationService = Factory.getService(MaxTenUserInformationService.class);
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static BidService bigService = Factory.getService(BidService.class);
	protected static UserInfoBeanService userInfoBeanService = Factory.getService(UserInfoBeanService.class);
	protected static BillService billService = Factory.getService(BillService.class);
	
	/**
	 *@Title: queryData
	 *
	 * @description 向t_max_ten_user_information中插入数据
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public static void insertMaxTenUserInformation () {
		/** 获取当前时间 为防止第一次上报错误，第二次上报是数据冲突，当第二次上报时先删除第一次上报的数据，然后重新上传。 */
		maxTenUserInformationService.truncateTable();
		maxTenUserInformationService.insertMaxTenUserInformation();
		
	}
	
	/**
	 * @Title: createMaxTenUserInformationXml
	 *
	 * @description 调用MaxTenUserInformationService方法生成xml文件
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public static void createMaxTenUserInformationXml () {
		maxTenUserInformationService.createMaxTenUserInformationXml();
	}
}
