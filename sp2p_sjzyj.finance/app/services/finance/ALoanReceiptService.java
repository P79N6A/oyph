package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
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
import common.utils.JRBHttpsUtils;
import common.utils.TimeUtil;
import daos.finance.ALoanReceiptDao;
import models.common.entity.t_pact;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.finance.entity.t_a_loan_receipt;
import models.finance.entity.t_org_info;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import services.base.BaseService;
import services.common.PactService;
import services.common.UserFundService;
import services.core.BidService;
import services.core.BillService;

public class ALoanReceiptService extends BaseService<t_a_loan_receipt> {

	protected static ALoanReceiptDao aLoanReceiptDao = Factory.getDao(ALoanReceiptDao.class);
	protected static ALoanReceiptService aLoanReceiptService = Factory.getService(ALoanReceiptService.class);
	protected static PactService pactService = Factory.getService(PactService.class);
	protected static BidService bigService = Factory.getService(BidService.class);
	protected static BillService billService = Factory.getService(BillService.class);
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	public void truncateTable() {
		aLoanReceiptDao.truncateTable();

	}

	public List<t_a_loan_receipt> listAll() {
		return aLoanReceiptDao.listAll();
	}

	/**
	 * 
	 * @Title: createIOU
	 * @description 生成借据信息表数据 全量 void
	 * @author likai
	 * @createDate 2018年10月6日 下午5:19:39
	 */
	public static void createIOUByALoanReceipt() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 清除数据库数据
			aLoanReceiptService.truncateTable();
			// 查询出所有的散标投资合同
			List<t_pact> pactAll = pactService.findListByType(0);
			// 查询组织机构信息
			t_org_info t_org_info = orgInfoService.getById(1L);
			// 循环遍历合同
			for (t_pact t_pact : pactAll) {
				t_a_loan_receipt t_a_loan_receipt = new t_a_loan_receipt();
				// 根据合同编号查询标的信息
				t_bid bid = bigService.findByID(t_pact.pid);
				// 把查询出的值封装到对象
				// 数据日期时间
				t_a_loan_receipt.setData_date(new Date());
				// 合同编号 合同表的主键
				t_a_loan_receipt.setContract_no(Long.toString(t_pact.id));
				// 借据号对应标号
				t_a_loan_receipt.setLoan_receipt_no(Long.toString(t_pact.pid));
				// 客户号 对应User_id
				t_a_loan_receipt.setCustomer_id(Long.toString(bid.user_id));
				// 原始起息日 对应放款时间
				t_a_loan_receipt.setOrigin_value_date(sdf.parse(sdf.format(bid.release_time)));
				// 原始到期时间 判断是时间单位1是日 2是月
				if (bid.period_units == 1) {
					t_a_loan_receipt.setOrigin_mature_date(DateUtil.addDay(bid.release_time, bid.period));
				} else {
					t_a_loan_receipt.setOrigin_mature_date(DateUtil.addMonth(bid.release_time, bid.period));
				}
				// 放款金额 对应 标的借款金额
				t_a_loan_receipt.setLoan_receipt_amount(BigDecimal.valueOf(bid.amount));
				// 判读 标是否还清
				if (bid.getStatus().code == 4) {
					t_a_loan_receipt.setLoan_receipt_balance(BigDecimal.valueOf(bid.amount));
				} else if (bid.getStatus().code == 5) {
					t_a_loan_receipt.setLoan_receipt_balance(BigDecimal.valueOf(0.00));
				}
				// 币种 固定人民币
				t_a_loan_receipt.setCurrency("CNY");
//				// 查询出标的所有借款账单 分了几期就有几个借款账单
				List<t_bill> billResult = billService.getBillByBid(bid.id);
				String flag = null;
//				// 循环遍历借款账单 判读标是否逾期 逾期则跳出循环 A 正常 B逾期 C结清
				for (t_bill t_bill : billResult) {
					if (t_bill.getIs_overdue().code) {
						flag = "B";
						break;
					} else if (bid.getStatus().code == 5) {
						flag = "C";
					} else {
						flag = "A";
					}
				}
				t_a_loan_receipt.setAcct_typ(flag);
				// 贷款利率 对应 标的年利率
				t_a_loan_receipt.setAcct_int(BigDecimal.valueOf(bid.apr / 100));
				// 根据 user_id 查询出userfund表的第三方托管账户
				t_a_loan_receipt.setAccount(userFundService.findByColumn("user_id=?", bid.user_id).payment_account);
				// 组织机构代码
				t_a_loan_receipt.setOrg_num(t_org_info.org_num);
				t_a_loan_receipt.save();
			}
			System.out.println("执行完成");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * 
	 * @Title: createXml
	 * @description 生成XML void
	 * @author likai
	 * @createDate 2018年10月11日 上午9:15:13
	 */
	public static void createXmlByALoanReceipt() {
		try {
			t_org_info t_org_info = orgInfoService.getById(1L);
			// 获取时间
			Date d = new Date();
			List<t_a_loan_receipt> t_a_loan_receipts = aLoanReceiptService.listAll();
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
			reportType.setText("FP.FP06.001");
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
			draftsman.setText("李媛媛");
			Element reviewer = reportBasic.addElement("Reviewer");
			reviewer.setText("裴雪松");
			Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
			leadingOfficial.setText("冯鑫");
			// 报文清单
			Element reports = reportInfo.addElement("Reports");
			for (t_a_loan_receipt result : t_a_loan_receipts) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("P060001");
				p1[1] = report.addElement("CONTRACT_NO");
				p1[2] = report.addElement("ORG_NUM");
				p1[3] = report.addElement("LOAN_RECEIPT_NO");
				p1[4] = report.addElement("CUSTOMER_ID");
				p1[5] = report.addElement("ORIGIN_VALUE_DATE");
				p1[6] = report.addElement("ORIGIN_MATURE_DATE");
				p1[7] = report.addElement("LOAN_RECEIPT_AMOUNT");
				p1[8] = report.addElement("LOAN_RECEIPT_BALANCE");
				p1[9] = report.addElement("CURRENCY");
				p1[10] = report.addElement("ACCT_TYP");
				p1[11] = report.addElement("ACCT_INT");
				p1[12] = report.addElement("ACCOUNT");
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
					if (type.equals("class java.math.BigDecimal")) {
						Method m = result.getClass().getMethod("get" + name);
						BigDecimal num = (BigDecimal) m.invoke(result);

						if (num != null) {
							p1[j].setText(num.toString());
						}
					}
				}
			}

			// 5、设置生成xml的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");

			// 6、生成xml文件
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/day/oyph_FP.FP06借据信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP06.001.xml成功");
			// 上报XML
			JRBHttpsUtils.post(file,ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP06.001.xml失败");
		}
	}
}
