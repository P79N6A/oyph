package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bouncycastle.util.Strings;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import daos.common.UserInfoDao;
import daos.finance.UserInformationDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_agre_guarantee;
import models.finance.entity.t_user_information;
import services.common.UserInfoService;
import services.finance.UserInformationService;

/**
 * 
 *
 * @ClassName: UserInformationCtrl

 * @description (P04 河北省P2P机构客户信息表)Controller层
 *
 * @author yaozijun
 * @createDate 2018年10月6日
 */
public class UserInformationCtrl extends BaseController {
	protected static UserInformationService userInformationService = Factory.getService(UserInformationService.class);
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	
	/**
	 * 
	 * @Title: queryData
	 *
	 * @description 向t_user_information表中插入数据
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public static void insertUserInformation () {
		userInformationService.insertUserInformation();
	}
	
	/**
	 * 
	 * @Title: createUserInfomationXml
	 *
	 * @description 调用UserInformationService方法生成xml文件
	 *
	 * @param  
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月12日
	 */
	public static void createUserInfomationXml () {
		userInformationService.createUserInfomationXml();
	}
	
	
	
	
}
