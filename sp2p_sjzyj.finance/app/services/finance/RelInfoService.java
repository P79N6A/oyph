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
import daos.finance.RelInfoDao;
import models.finance.entity.t_org_info;
import models.finance.entity.t_rel_info;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: RelInfoService
 *
 * @description 机构关联方信息Service接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class RelInfoService extends BaseService<t_rel_info> {

	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);
	protected static RelInfoDao relInfoDao = null;
	
	protected RelInfoService () {
		this.relInfoDao = Factory.getDao(RelInfoDao.class);
		
		super.basedao = relInfoDao;
	}

	public t_rel_info getById(Long id) {
		
		return relInfoDao.getById(id);
	}
	
	

	public static void createXmlbByRelInfo() {
		try {
			t_org_info t_org_info = orgInfoService.getById(1L);
			List<t_rel_info> t_rel_info = relInfoDao.findAll();
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
			reportType.setText("FP.FP02.001");
			Element mesgID = head.addElement("MesgID");
			mesgID.setText("A1234B1234C1234D1234oplk897new18");
			// 报文信息
			Element reportInfo = doc.addElement("ReportInfo");
			// 报文基本信息
			Element reportBasic = reportInfo.addElement("ReportBasicInfo");
			Element fill = reportBasic.addElement("FillingAgency");
			fill.setText(t_org_info.org_num);
			Element reportDate = reportBasic.addElement("ReportDate");
			Calendar cale = Calendar.getInstance();
			cale.add(Calendar.DATE, -1);
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
			for (t_rel_info rel_info : t_rel_info) {
				Field[] field = rel_info.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("DATA_DATE");
				p1[1] = report.addElement("ORG_NUM");
				p1[2] = report.addElement("RELATION_ID");
				p1[3] = report.addElement("RELATION_TYPE");
				p1[4] = report.addElement("RELATION_NAME");
				p1[5] = report.addElement("RELATION_ID_TYPE");
				p1[6] = report.addElement("RELATION_ID_NO");
				p1[7] = report.addElement("SHR_RATIO");
				p1[8] = report.addElement("CUST_TELEPHONE_NO");
				p1[9] = report.addElement("CUST_ADDE_DESC");
				for (int j = 0; j < 10; j++) {
					String type = field[j].getGenericType().toString();
					String name = field[j].getName();
					field[j].setAccessible(true);
					name = name.substring(0, 1).toUpperCase() + name.substring(1);
					if (type.equals("class java.lang.String")) {
						// 如果type是类类型，则前面包含"class"，后面跟类名
						Method m = rel_info.getClass().getMethod("get" + name);
						String value = (String) m.invoke(rel_info);
						if (value != null) {
							p1[j].setText(value.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("class java.lang.Integer")) {
						Method m = rel_info.getClass().getMethod("get" + name);
						Integer number = (Integer) m.invoke(rel_info);
						if (number != null) {
							p1[j].setText(number.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("class java.math.BigDecimal")) {
						Method m = rel_info.getClass().getMethod("get" + name);
						BigDecimal num = (BigDecimal) m.invoke(rel_info);

						if (num != null) {
							p1[j].setText(num.toString());
						}
					}
					if (type.equals("class java.lang.Long")) {
						Method m = rel_info.getClass().getMethod("get" + name);
						Long use = (Long) m.invoke(rel_info);
						if (use != null) {
							p1[j].setText(use.toString());
						} else {
							p1[j].setText("");
						}
					}
					if (type.equals("class java.util.Date")) {
						Method m = rel_info.getClass().getMethod("get" + name);
						Date time = (Date) m.invoke(rel_info);
						if (time != null) {
							p1[j].setText(sdf.format(time).toString());
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
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/day/oyph_FP.FP02机构关联方信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP02.001.xml成功");
			// 上报XML
			JRBHttpsUtils.post(file, ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP02.001.xml失败");
		}
	}
}
