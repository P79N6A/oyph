package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.search.DateTerm;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.enums.SuperviseReportType;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.TimeUtil;
import daos.finance.MaxTenUserInformationDao;
import jobs.IndexStatisticsJob;
import models.common.entity.t_user_info;
import models.finance.bean.UserInfoBean;
import models.finance.entity.t_max_ten_user_information;
import models.finance.entity.t_user_information;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import play.Play;
import services.base.BaseService;

/***
 * 
 * @ClassName: MaxTenUserInformationService
 *
 * @description (P05 河北省P2P机构最大十家客户信息表)Service层
 *
 * @author yaozijun
 * @createDate 2018年10月5日
 */
public class MaxTenUserInformationService extends BaseService<t_max_ten_user_information> {

	protected static MaxTenUserInformationDao maxTenUserInformationDao = Factory.getDao(MaxTenUserInformationDao.class);
	protected static UserInfoBeanService userInfoBeanService = Factory.getService(UserInfoBeanService.class);

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
	 * @createDate 2018年10月11日
	 */
	public static void truncateTable() {
		Date time = new Date();
		maxTenUserInformationDao.truncateTable(time);
	}

	/**
	 * 
	 * @Title: insertMaxTenUserInformation
	 *
	 * @description 向t_max_ten_user_information表中插入数据
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月14日
	 */
	public static void insertMaxTenUserInformation() {
		Date time = new Date();
		truncateTable();

		List<UserInfoBean> infos = userInfoBeanService.findListByType();

		/** 将IndexStatisticsJob表中已经计算出的贷款余额调过来 */
		String str1 = IndexStatisticsJob.totalBillAmount;
		/** 将获取到的String str1中的逗号取消掉方便算合计 */
		String str2 = str1.replace(",", "");
		BigDecimal str3 = new BigDecimal(str2);
		/** 创建BigDecimal格式对象 计算方法： 贷款余额总和=十个Debt(期末余额)的和 */
		BigDecimal flag = new BigDecimal(0);
		/** 循环遍历t_user_info数据，求十家期末余额之和 */
		for (UserInfoBean t_user_info : infos) {
			flag = flag.add(t_user_info.getDebt());
		}
		BigDecimal flag1 = flag.divide(str3, 2, RoundingMode.HALF_UP);
		/** 循环遍历t_user_info数据，插入到t_max_ten_user_information表中 */
		for (UserInfoBean t_user_info : infos) {
			t_max_ten_user_information t_max_ten_user_information = new t_max_ten_user_information();

			/** 判断客户类型 */
			if (t_user_info.getEnterprise_name() != null) {
				/** 对公 */
				/** 证件类型 */
				t_max_ten_user_information.setPapers_type("OT01");
				/** 证件号码 */
				t_max_ten_user_information.setId_no(t_user_info.getEnterprise_credit());
				/** 客户类型 */
				t_max_ten_user_information.setCust_type("UT01");
				/** 客户名称 */
				t_max_ten_user_information.setCustomer_name(t_user_info.getReality_name());
				/** 期末余额 */
				t_max_ten_user_information.setEnd_balance(t_user_info.getDebt());
			} else {
				/** 对私 */
				/** 证件类型 */
				t_max_ten_user_information.setPapers_type("PT01");
				/** 证件号码 */
				t_max_ten_user_information.setId_no(t_user_info.getId_number());
				/** 客户类型 */
				t_max_ten_user_information.setCust_type("UT02");
				/** 客户名称 */
				t_max_ten_user_information.setCustomer_name(t_user_info.getReality_name());
				/** 期末余额 */
				t_max_ten_user_information.setEnd_balance(t_user_info.getDebt());
			}
			/** 插入贷款总额 计算方法：各个标还未换的本金和利息总和 */
			t_max_ten_user_information.setLoan_receipt_balance(str2);
			/** 合计= 十家期末余额之和/贷款余额 */
			t_max_ten_user_information.setCount(flag1);
			t_max_ten_user_information.setTime(time);

			/** 保存插入数据 */
			t_max_ten_user_information.save();
			
		}
		Logger.info("P05 河北省P2P机构最大十家客户信息表插入成功!!!");
	}

	/**
	 * 
	 * @Title: createMaxTenUserInformationXml
	 *
	 * @description 生成最大十家客户信息表Xml文件
	 *
	 * @param
	 * 
	 * @return void
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月11日
	 */
	public void createMaxTenUserInformationXml() {
		Date time = new Date();
		/** 查询t_user_information数据 */
		List<t_max_ten_user_information> t_max_ten_user_information = maxTenUserInformationDao.listAll(time);

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
			reportType.setText("P.P05.001");
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
			Element report = reports.addElement("Report");

			Element p1[] = new Element[52];
			Integer flag = 50001;
			/** 循环52次，前50次循环是大客户的5个寻药循环的字段 */
			for (int j = 0; j < 52; j++) {
				p1[j] = report.addElement("P0" + flag.toString());
				flag++;
			}
			int j = 0;
			for (t_max_ten_user_information result : t_max_ten_user_information) {
				Field[] field = result.getClass().getDeclaredFields();
				for (int i = 0; i < 5; i++) {
					String type = field[i].getGenericType().toString();
					String name = field[i].getName();
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
					if (type.equals("class java.math.BigDecimal")) {
						Method m = result.getClass().getMethod("get" + name);
						BigDecimal num = (BigDecimal) m.invoke(result);
						if (num != null) {
							p1[j].setText(num.toString());
						}
					}
					j++;
				}
				Method loan_receipt_balance = null;
				/** 传入期末余额合计 */
				loan_receipt_balance = result.getClass().getMethod("getCount");
				BigDecimal val1 = (BigDecimal) loan_receipt_balance.invoke(result);
				p1[50].setText(val1.toString());
				/** 传入贷款余额 */
				loan_receipt_balance = result.getClass().getMethod("getLoan_receipt_balance");
				String val = (String) loan_receipt_balance.invoke(result);
				p1[51].setText(val.toString());
			}

			// 5、设置生成xml的格式da'te
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");
			// 6、生成xml文件
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/month"+ File.separator+ JRBUtils.getXMLFileName(null, SuperviseReportType.LARGEST_TEN_CUSTOMER_INFO_TABLE, "oyph"));
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("P.P05.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P05.001.xml失败");
		}
	}

}
