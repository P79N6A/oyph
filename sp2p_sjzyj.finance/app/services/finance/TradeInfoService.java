package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import daos.common.UserDao;
import daos.common.UserFundDao;
import daos.common.UserInfoDao;
import daos.finance.OrgInfoDao;
import daos.finance.TradeInfoDao;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.finance.entity.t_org_info;
import models.finance.entity.t_trade_info;
import models.finance.entity.t_trade_info.TradeType;
import play.Play;
import models.finance.entity.t_return_status.ReportType;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: TradeInfoService

 * @description 交易信息Service接口
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午10:17:52
 */
public class TradeInfoService extends BaseService<t_trade_info> {
	protected static final UserFundDao userFundDao = Factory.getDao(UserFundDao.class);
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	protected static final UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	protected static final OrgInfoDao orgInfoDao = Factory.getDao(OrgInfoDao.class);
	protected TradeInfoDao tradeInfoDao = null;
	
	protected TradeInfoService () {
		this.tradeInfoDao = Factory.getDao(TradeInfoDao.class);
		
		super.basedao = tradeInfoDao;
	}
	
	/**
	 * 清空表数据
	 *
	 * @Title: truncateTable
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:20:19
	 */
	public void truncateTable() {
		tradeInfoDao.truncateTable();
	}
	
	/**
	 * 查询表数据
	 *
	 * @Title: findTradeInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_trade_info>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:46:45
	 */
	public List<t_trade_info> findTradeInfo () {
		
		return tradeInfoDao.findTradeInfo();
	}
	
	/**
	 * 生成xml文档
	 *
	 * @Title: createXmlOfTradeInfo
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月11日-上午10:27:24
	 */
	public void createXmlByTradeInfo() {
		List<t_trade_info> listTradeInfo = tradeInfoDao.findTradeInfo();

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
			reportType.setText("FP.FP05.001");
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
			for (t_trade_info result : listTradeInfo) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("DATA_DATE");
				p1[1] = report.addElement("TRADE_DATE");
				p1[2] = report.addElement("TRADE_ORGANIZATION_ID");
				p1[3] = report.addElement("SERIAL_NUMBER");
				p1[4] = report.addElement("ACCOUNT");
				p1[5] = report.addElement("TRAN_TYPE");
				p1[6] = report.addElement("CUSTOMER_ID");
				p1[7] = report.addElement("CUSTOMER_NAME");
				p1[8] = report.addElement("CURRENCY");
				p1[9] = report.addElement("AMOUNT");
				p1[10] = report.addElement("AMOUNT_CNY");
				p1[11] = report.addElement("PRODUCT_CODE");

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
						BigDecimal bd = (BigDecimal) m.invoke(result);
						DecimalFormat df = new DecimalFormat("#.00");
						if (bd != null) {
							p1[j].setText(df.format(bd));
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
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/day/oyph_FP.FP05交易信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP05.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP05.001.xml失败");
		}
	}
	
	/**
	 * 保存交易信息记录
	 * @author guoShiJjie
	 * @createDate 2018.10.18
	 * */
	public Boolean saveTradeInfo (Date dataDate , String businessNo, TradeType serviceType,Long userId , String currency , BigDecimal amount ,Long bidId) {
		Date tradeDate = new Date();
		List<t_org_info> org = orgInfoDao.findAll();
		
		String userName = null;
		t_user_info userInfo = userInfoDao.findByColumn( " user_id = ? ", userId);
		if (userInfo != null ) {
			if (userInfo.enterprise_name != null) {
				userName = userInfo.enterprise_name;
			}
			userName = userInfo.reality_name;
		}
		t_user_fund userFund = userFundDao.findByColumn(" user_id = ? ", userId);
		
		
		return tradeInfoDao.saveTradeInfo(dataDate, tradeDate, org.get(0).org_num, businessNo, userFund.payment_account, serviceType, userId, userName, currency, amount, bidId);
				
	}
	
}
