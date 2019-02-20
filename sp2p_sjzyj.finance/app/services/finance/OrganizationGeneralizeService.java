package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
import daos.finance.OrganizationGeneralizeDao;
import models.finance.entity.t_borrower_repayment;
import models.finance.entity.t_organization_generalize;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import models.finance.entity.t_user_information;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: OrganizationGeneralizeService
 *
 * @description 机构概括Service接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class OrganizationGeneralizeService extends BaseService<t_organization_generalize> {
	private OrganizationGeneralizeDao organizationGeneralizeDao = Factory.getDao(OrganizationGeneralizeDao.class);

	public void createOrganizationGeneralizeXml() {
		/** 查询t_organization_generalize数据 */
		List<t_organization_generalize> t_organization_generalize = organizationGeneralizeDao.listAll();
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
			reportType.setText("P.P01.001");
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
			draftsman.setText("贾静雯");
			Element reviewer = reportBasic.addElement("Reviewer");
			reviewer.setText("裴雪松");
			Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
			leadingOfficial.setText("冯鑫");
			// 报文清单
			Element reports = reportInfo.addElement("Reports");
			for (int a = 0; a < t_organization_generalize.size(); a++) {
				t_organization_generalize models = t_organization_generalize.get(a);
				Element report = reports.addElement("Report");

				Field[] field = models.getClass().getDeclaredFields();
				/** 设置集合长度，实体类时间自杜纳不需要上报所以-1 */
				int l = field.length - 1;
				Element p1[] = new Element[l];
				int flag = 10001;
				String value = null;
				BigDecimal number = null;
				Long use = null;
				Date real_repayment_time = null;
				for (int j = 0; j < l; j++) {
					p1[j] = report.addElement("P0" + flag);
					flag++;
					// 获取属性类型
					String type = field[j].getGenericType().toString();
					// 获取属性的名字
					String name = field[j].getName();

					// 关键。。。可访问私有变量
					field[j].setAccessible(true);
					name = name.substring(0, 1).toUpperCase() + name.substring(1);

					if (type.equals("class java.lang.String")) {
						// 如果type是类类型，则前面包含"class "，后面跟类名
						Method m = models.getClass().getMethod("get" + name);
						value = (String) m.invoke(models);
						if (value != null) {
							p1[j].setText(value.toString());
						}
					}
					if (type.equals("class java.math.BigDecimal")) {
						Method m = models.getClass().getMethod("get" + name);
						number = (BigDecimal) m.invoke(models);
						if (number != null) {
							p1[j].setText(number.toString());
						}
					}
					if (type.equals("class java.lang.Long")) {
						Method m = models.getClass().getMethod("get" + name);
						use = (Long) m.invoke(models);
						if (use != null) {
							p1[j].setText(use.toString());
						}
					}
					if (type.equals("class java.util.Date")) {
						Method m = models.getClass().getMethod("get" + name);
						real_repayment_time = (Date) m.invoke(models);
						if (real_repayment_time != null) {
							SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							p1[j].setText(time.format(real_repayment_time).toString());
						}
					}
				}
			}

			// 5、设置生成xml的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");

			// 6、生成xml文件
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/month/oyph_P.P01机构概括表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("P.P01.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P01.001.xml失败");
		}
	}

}
