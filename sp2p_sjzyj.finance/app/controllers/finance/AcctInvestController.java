package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import controllers.common.BaseController;
import models.common.entity.t_pact;
import models.core.entity.t_product;
import models.finance.entity.t_acct_invest;
import models.finance.entity.t_agre_guarantee;
import models.finance.entity.t_org_info;
import models.finance.entity.t_return_status.ReportType;
import services.common.PactService;
import services.core.BidService;
import services.core.ProductService;
import services.finance.AcctInvestService;
import services.finance.OrgInfoService;

public class AcctInvestController extends BaseController {

	protected static AcctInvestService acctInvestService = Factory.getService(AcctInvestService.class);
	protected static PactService pactService = Factory.getService(PactService.class);
	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	/**
	 * 
	 * @Title: createProduct
	 * @description 生成产品投向表数据 全量 void
	 * @author likai
	 * @createDate 2018年10月7日 下午3:36:03
	 */
	public static void createProduct() {
		// 清除数据库数据
		acctInvestService.truncateTable();
		// 查询出所有的散标投资合同
		List<t_pact> pactAll = pactService.findListByType(0);
		// 查询组织机构信息
		t_org_info t_org_info = orgInfoService.getById(1L);
		// 循环遍历合同
		for (t_pact pact : pactAll) {
			t_acct_invest t_acct_invest = new t_acct_invest();
			// 数据日期时间
			t_acct_invest.setData_date(new Date());
			// 标的ID
			t_acct_invest.setProduct_code(Long.toString(pact.pid));
			// 产品投向 固定贷款
			t_acct_invest.setInvest_type("A02");
			// 组织机构代码
			t_acct_invest.setOrg_num(t_org_info.org_num);
			// 产品是否多层嵌套 固定 否
			t_acct_invest.setWhe_more("N");
			t_acct_invest.save();
		}
		System.out.println("执行完成");
	}

	/**
	 * 
	 * @Title: createXml
	 * @description 生成XML void
	 * @author likai
	 * @createDate 2018年10月11日 上午9:14:24
	 */
	public static void createXml() {
		try {
			t_org_info t_org_info = orgInfoService.getById(1L);
			List<t_acct_invest> t_acct_invests = acctInvestService.listAll();
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
			reportType.setText("FP.FP08.001");
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
			draftsman.setText("李媛媛");
			Element reviewer = reportBasic.addElement("Reviewer");
			reviewer.setText("裴雪松");
			Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
			leadingOfficial.setText("冯鑫");
			// 报文清单
			Element reports = reportInfo.addElement("Reports");
			for (t_acct_invest result : t_acct_invests) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("DATA_DATE");
				p1[1] = report.addElement("PRODUCT_CODE");
				p1[2] = report.addElement("INVEST_TYPE");
				p1[3] = report.addElement("ORG_NUM");
				p1[4] = report.addElement("WHE_MORE");
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
			File file = new File("data/day/FP.FP08.001.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP08.001.xml成功");
			// 上报XML
			JRBHttpsUtils.post(file,ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("FP.FP08.001.xml失败");
		}
	}
}
