package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.TimeUtil;
import daos.common.UserInfoDao;
import daos.finance.UserInformationDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_user_information;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import play.Play;
import services.base.BaseService;

/**
 * 
 * @ClassName: UserInformationService
 *
 * @description (P04 河北省P2P机构客户信息表)Service层
 *
 * @author yaozijun
 * 
 * @createDate 2018年10月5日
 */
public class UserInformationService extends BaseService<t_user_information> {
	private UserInformationDao userInformationDao = Factory.getDao(UserInformationDao.class);
	private UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);

	/**
	 * 
	 * @Title: truncateTable
	 *
	 * @description 清空表中数据
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * @author yaozijun
	 * 
	 * @createDate 2018年10月6日
	 */
	public void truncateTable() {
		userInformationDao.truncateTable();
	}

	/**
	 * 
	 * @Title: createUserInfomationXml
	 *
	 * @description 向t_user_information表中插入数据
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月14日
	 */
	public void insertUserInformation() {
		/** 清除表数据 */
		userInformationDao.truncateTable();
		List<t_user_info> userInfo = userInfoDao.queryUserInfoAll();
		/** 循环遍历t_user_info数据，插入到t_user_information表中 */
		for (t_user_info t_user_info : userInfo) {
			t_user_information t_user_information = new t_user_information();
			

			/** 判断客户类型 */
			if (t_user_info.enterprise_name != null ) {
				/** 对公 */
				/** 客户编码 */
				t_user_information.setCust_id(Long.toString(t_user_info.user_id));
				/** 客户类型 */
				t_user_information.setCust_type("UT01");
				/** 客户名称 */
				t_user_information.setCustomer_name(t_user_info.enterprise_name);
				/** 证件类型 */
				t_user_information.setPapers_type("OT01");
				/** 证件号码 */
				t_user_information.setId_no(t_user_info.enterprise_credit);
				/** 是否本机构员工 */
				t_user_information.setWhether_organization_member("N");
				/** 所属行业 */
				t_user_information.setIndustry_involved(t_user_info.industry_involved);
				/** 企业规模 */				
				t_user_information.setEnterprise_scale(t_user_info.enterprise_scale);
			} else {
				/** 对私 */
				/** 客户编码 */
				t_user_information.setCust_id(Long.toString(t_user_info.user_id));
				/** 客户类型 */
				t_user_information.setCust_type("UT02");
				/** 客户名称 */
				t_user_information.setCustomer_name(t_user_info.reality_name);
				/** 证件类型 */
				t_user_information.setPapers_type("PT01");
				/** 证件号码 */
				t_user_information.setId_no(t_user_info.id_number);
				/** 是否本机构员工 */
				t_user_information.setWhether_organization_member("N");
				/** 所属行业 */
				t_user_information.setIndustry_involved("");
				/** 企业规模 */				
				t_user_information.setEnterprise_scale("");
			}

			/** 保存插入数据 */
			t_user_information.save();
			
		}
		Logger.info("P04 河北省P2P机构客户信息表插入成功!!!");
	}

	/**
	 * 
	 * @Title: createUserInfomationXml
	 *
	 * @description 生成客户信息表Xml文件
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * @author yaozijun
	 * @createDate 2018年10月11日
	 */
	public void createUserInfomationXml() {
		/** 查询t_user_information数据 */
		List<t_user_information> t_user_information = userInformationDao.listAll();

		try {
			// 1、创建document对象
			Document document = DocumentHelper.createDocument();
			// 创建跟节点Document
			Element doc = document.addElement("Document");
			// 报文头
			Element head = doc.addElement("HeaderInfo");

			Element sender = head.addElement("SenderID");
			sender.setText("911301050799558020");
			Element receiver = head.addElement("ReceiverID");
			receiver.setText("000000");
			Element reportSender = head.addElement("ReportSender");
			reportSender.setText("石家庄菲尔德投资咨询有限公司");
			Element reportReceiver = head.addElement("ReportReceiver");
			reportReceiver.setText("");
			Element reportSendDate = head.addElement("ReportSendDate");
			// 获取时间
			Date d = new Date();
			// 转换格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			reportSendDate.setText(sdf.format(d).toString());
			Element reportSendTime = head.addElement("ReportSendTime");
			// 转换格式
			SimpleDateFormat sdfq = new SimpleDateFormat("HH:mm:ss");
			reportSendTime.setText(sdfq.format(d).toString());

			Element reportType = head.addElement("ReportType");
			reportType.setText("P.P04.001");
			Element mesgID = head.addElement("MesgID");
			mesgID.setText("A1234B1234C1234D1234oplk8976tr1d");
			// 报文信息
			Element reportInfo = doc.addElement("ReportInfo");
			// 报文基本信息
			Element reportBasic = reportInfo.addElement("ReportBasicInfo");
			Element fill = reportBasic.addElement("FillingAgency");
			fill.setText("911301050799558020");
			Element reportDate = reportBasic.addElement("ReportDate");
			Calendar cale = Calendar.getInstance();
			cale.set(Calendar.DAY_OF_MONTH, 0);
			String lastDay = sdf.format(cale.getTime());
			reportDate.setText(lastDay);
			Element draftsman = reportBasic.addElement("Draftsman");
			draftsman.setText("李媛媛");
			Element reviewer = reportBasic.addElement("Reviewer");
			reviewer.setText("裴雪松");
			Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
			leadingOfficial.setText("冯鑫");
			// 报文清单
			Element reports = reportInfo.addElement("Reports");
			Element p1[] = new Element[8];
			for (t_user_information result : t_user_information) {
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("P040001");
				p1[1] = report.addElement("P040002");
				p1[2] = report.addElement("P040003");
				p1[3] = report.addElement("P040004");
				p1[4] = report.addElement("P040005");
				p1[5] = report.addElement("P040006");
				p1[7] = report.addElement("P040007");
				p1[6] = report.addElement("P040008");
				Field[] field = result.getClass().getDeclaredFields();
				for (int j = 0; j < field.length; j++) {
					String type = field[j].getGenericType().toString();
					String name = field[j].getName();
					field[j].setAccessible(true);
					name = name.substring(0, 1).toUpperCase() + name.substring(1);
					if (type.equals("class java.lang.String")) {
						// 如果type是类类型，则前面包含"class "，后面跟类名
						Method m = result.getClass().getMethod("get" + name);
						String value = (String) m.invoke(result);
						if (value != null) {
							p1[j].setText(value.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("int")) {
						Method m = result.getClass().getMethod("get" + name);
						int number1 = (int) m.invoke(result);
						if (!"".equals(number1)) {
							p1[j].setText(String.valueOf(number1));
						} else {
							p1[j].setText("1111");
						}
					}
					if (type.equals("class java.lang.Integer")) {
						Method m = result.getClass().getMethod("get" + name);
						Integer number = (Integer) m.invoke(result);
						if (number != null) {
							p1[j].setText(number.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("class java.lang.Long")) {
						Method m = result.getClass().getMethod("get" + name);
						Long use = (Long) m.invoke(result);
						if (use != null) {
							p1[j].setText(use.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("class java.util.Date")) {
						Method m = result.getClass().getMethod("get" + name);
						Date time = (Date) m.invoke(result);
						if (time != null) {
							p1[j].setText(time.toString());
						} else {
							p1[j].setText("");
						}
					}
				}
			}

			// 5、设置生成xml的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");
			// 6、生成xml文件
			// File file = new File("data/month/P.P04.001.xml");
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/month/oyph_P.P04客户信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("P.P04.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P04.001.xml失败");
		}
	}

}
