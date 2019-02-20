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

import common.enums.SuperviseReportType;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.ResultInfo;
import daos.finance.LenderCollectionDao;
import models.finance.entity.t_borrower_repayment;
import models.finance.entity.t_lender_collection;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import play.Play;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: LenderCollectionService
 *
 * @description 出借人收款明细dao实现
 *
 * @author HanMeng
 * @createDate 2018年10月6日-下午2:00:06
 */
public class LenderCollectionService extends BaseService<t_lender_collection> {
	protected LenderCollectionDao lendercollectionDao = Factory.getDao(LenderCollectionDao.class);

	protected LenderCollectionService() {
		super.basedao = lendercollectionDao;
	}

	/**
	 * 
	 *
	 * @Title: createLenderCollection
	 * 
	 * @description: 向出借人收款明细表中插入数据
	 *
	 * @param id
	 * @param service_order_no
	 *            流水号
	 * @param real_repayment_time
	 *            还款日期
	 * @param p_id
	 *            贷款合同号
	 * @param user_id
	 *            借款人客户号
	 * @param repayment_corpus
	 *            还款本金
	 * @param repayment_interest
	 *            还款利息
	 * @return
	 * 
	 * @return ResultInfo
	 *
	 * @author HanMeng
	 * @createDate 2018年10月6日-下午1:58:39
	 */
	public ResultInfo savaLenderCollection() {
		ResultInfo result = new ResultInfo();

		int flag = lendercollectionDao.savaLenderCollection();
		if (flag < 0) {
			result.code = -1;
			result.msg = "向出借人收款明细表中插入数据失败";
			return result;
		}
		result.code = 1;
		result.msg = "向出借人收款明细表中插入数据成功";
		Logger.info("P09向出借人收款明细表中插入数据成功!!!");
		return result;
	}

	/**
	 * 查询数据（当月）
	 *
	 * @Title: getLenderCollection
	 * 
	 * @description:
	 *
	 * @return
	 * 
	 * @return List<t_lender_collection>
	 *
	 * @author HanMeng
	 * @createDate 2018年10月10日-下午1:33:07
	 */
	public List<t_lender_collection> getLenderCollection() {

		return lendercollectionDao.getLenderCollection();
	}

	/**
	 * 生成xml方法
	 */
	public void createLenderCollectionXml() {
		List<t_lender_collection> model = lendercollectionDao.getLenderCollection();
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
			// 获取时间
			Date e = new Date();
			// 转换格式
			SimpleDateFormat sdfq = new SimpleDateFormat("HH:mm:ss");
			reportSendTime.setText(sdfq.format(e).toString());
			Element reportType = head.addElement("ReportType");
			reportType.setText("P.P09.001");
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
			for (int a = 0; a < model.size(); a++) {
				t_lender_collection models = model.get(a);
				Element report = reports.addElement("Report");

				Field[] field = models.getClass().getDeclaredFields();
				int l = field.length;
				Element p1[] = new Element[l];
				int flag = 90001;
				String value = null;
				BigDecimal number = null;
				Long use = null;
				Date real_receive_time = null;
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
						real_receive_time = (Date) m.invoke(models);

						if (real_receive_time != null) {
							SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");

							p1[j].setText(time.format(real_receive_time).toString());

						}
					}

				}
			}

			// 5、设置生成xml的格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 设置编码格式
			format.setEncoding("UTF-8");

			// 6、生成xml文件
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/month"+ File.separator+ JRBUtils.getXMLFileName(null, SuperviseReportType.LENDERS_RECEIVE_DETAILS_TABLE, "oyph"));
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("P.P09.001.xml成功");
			JRBHttpsUtils.post(file,ReportType.MONTH);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P09.001.xml失败");
		}

	}

}