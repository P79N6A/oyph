package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import controllers.common.BaseController;
import models.finance.entity.t_org_info;
import models.finance.entity.t_return_status.ReportType;
import services.finance.OrgInfoService;

public class OrgInfoController extends BaseController {

	private static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	/**
	 * 
	 * @Title: createXmlbByOrgInfo
	 * @description 生成机构基本表报文 上报报文 void
	 * @author likai
	 * @createDate 2018年10月15日 下午5:03:53
	 */
	public static void createXmlbByOrgInfo() {
		try {
			t_org_info t_org_info = orgInfoService.getById(1L);
			// 获取时间
			Date d = new Date();
			// 1、创建document对象
			Document document = DocumentHelper.createDocument();
			// 创建跟节点Document
			Element doc = document.addElement("Document");
			// 报文头
			Element head = doc.addElement("HeaderInfo");
			Element sender = head.addElement("SenderID");
			sender.setText(t_org_info.org_num);
			Element receiver = head.addElement("ReceiverID");
			receiver.setText("");
			Element reportSender = head.addElement("ReportSender");
			reportSender.setText("石家庄菲尔德投资咨询有限公司");
			Element reportReceiver = head.addElement("ReportReceiver");
			reportReceiver.setText("");
			Element reportSendDate = head.addElement("ReportSendDate");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			reportSendDate.setText(sdf.format(d).toString());
			Element reportSendTime = head.addElement("ReportSendTime");
			SimpleDateFormat sdfq = new SimpleDateFormat("HH:mm:ss");
			reportSendTime.setText(sdfq.format(d).toString());
			Element reportType = head.addElement("ReportType");
			reportType.setText("FP.FP01.001");
			Element mesgID = head.addElement("MesgID");
			mesgID.setText("A1234B1234C1234D1234oplk897new18");
			// 报文信息
			Element reportInfo = doc.addElement("ReportInfo");
			// 报文基本信息
			Element reportBasic = reportInfo.addElement("ReportBasicInfo");
			Element fill = reportBasic.addElement("FillingAgency");
			fill.setText(t_org_info.org_num);
			Element reportDate = reportBasic.addElement("ReportDate");
			reportDate.setText(sdf.format(d).toString());
			Element draftsman = reportBasic.addElement("Draftsman");
			draftsman.setText("贾静雯");
			Element reviewer = reportBasic.addElement("Reviewer");
			reviewer.setText("裴雪松");
			Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
			leadingOfficial.setText("冯鑫");
			// 报文清单
			Element reports = reportInfo.addElement("Reports");
			Field[] field = t_org_info.getClass().getDeclaredFields();
			Element p1[] = new Element[field.length];
			Element report = reports.addElement("Report");
			p1[0] = report.addElement("ORG_NAME");
			p1[1] = report.addElement("ORG_NUM");
			p1[2] = report.addElement("DATA_DATE");
			p1[3] = report.addElement("REG_ADDR");
			p1[4] = report.addElement("RUN_ADDR");
			p1[5] = report.addElement("REGION_CD");
			p1[6] = report.addElement("THIRD_BANK");
			p1[7] = report.addElement("EXP_DATE");
			p1[8] = report.addElement("STS_FLG");
			for (int j = 0; j < 9; j++) {
				String type = field[j].getGenericType().toString();
				String name = field[j].getName();
				field[j].setAccessible(true);
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				if (type.equals("class java.lang.String")) {
					// 如果type是类类型，则前面包含"class"，后面跟类名
					Method m = t_org_info.getClass().getMethod("get" + name);
					String value = (String) m.invoke(t_org_info);
					if (value != null) {
						p1[j].setText(value.toString());
					} else {
						p1[j].setText("");
					}
				}
				if (type.equals("class java.lang.Integer")) {
					Method m = t_org_info.getClass().getMethod("get" + name);
					Integer number = (Integer) m.invoke(t_org_info);
					if (number != null) {
						p1[j].setText(number.toString());
					} else {
						p1[j].setText("");
					}
				}
				if (type.equals("class java.lang.Long")) {
					Method m = t_org_info.getClass().getMethod("get" + name);
					Long use = (Long) m.invoke(t_org_info);
					if (use != null) {
						p1[j].setText(use.toString());
					} else {
						p1[j].setText("");
					}
				}
				if (type.equals("class java.util.Date")) {
					Method m = t_org_info.getClass().getMethod("get" + name);
					Date time = (Date) m.invoke(t_org_info);
					if (time != null) {
						p1[j].setText(sdf.format(time).toString());
					} else {
						p1[j].setText("");
					}
				}
			}

			// 5、设置生成xml的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");

			// 6、生成xml文件
			File file = new File("data/day/FP.FP01.001.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP01.001.xml成功");
			// 上报XML
			JRBHttpsUtils.post(file,ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP01.001.xml失败");
		}
	}

}
