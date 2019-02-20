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
import daos.finance.ShareholderInformationDao;
import models.finance.entity.t_acct_invest;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import models.finance.entity.t_shareholder_information;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: ShareholderInformationService
 *
 * @description 股东信息Service接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class ShareholderInformationService extends BaseService<t_shareholder_information> {

	protected static ShareholderInformationDao shareholderInfomationDao = Factory.getDao(ShareholderInformationDao.class);
	
	protected ShareholderInformationService () {	
		super.basedao = shareholderInfomationDao;
	}
	
	public  void createShareholderInformationXml() {
		try {
			List<t_shareholder_information> shareholderInformation = shareholderInfomationDao.findAll();
		
			
			// 获取时间
			Date d = new Date();
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			reportSendDate.setText(sdf.format(d).toString());
			Element reportSendTime = head.addElement("ReportSendTime");
			SimpleDateFormat sdfq = new SimpleDateFormat("HH:mm:ss");
			reportSendTime.setText(sdfq.format(d).toString());
			Element reportType = head.addElement("ReportType");
			reportType.setText("P.P02.001");
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
			for (t_shareholder_information result : shareholderInformation) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("P020001");
				p1[1] = report.addElement("P020002");
				p1[2] = report.addElement("P020003");
				p1[3] = report.addElement("P020004");
				p1[4] = report.addElement("P020005");
				p1[5] = report.addElement("P020006");
				p1[6] = report.addElement("P020007");
				p1[7] = report.addElement("P020008");
				
				for (int j = 0; j < field.length-1; j++) {
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
					if (type.equals("class java.math.BigDecimal")) {
						Method m = result.getClass().getMethod("get" + name);
						BigDecimal number = (BigDecimal) m.invoke(result);
						if (number != null) {
							p1[j].setText(number.toString());
							
						} else {
							
							p1[j].setText("0.00");
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
							SimpleDateFormat sdfa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							
							p1[j].setText(sdfa.format(time).toString());
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
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/month/oyph_P.P02股东信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("P.P02.001.xml成功");
			// 上报XML
			JRBHttpsUtils.post(file,ReportType.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P02.001.xml失败");
		}
	}
}
