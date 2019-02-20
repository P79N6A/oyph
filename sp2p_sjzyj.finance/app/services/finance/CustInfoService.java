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
import daos.common.UserInfoDao;
import daos.finance.CustInfoDao;
import daos.finance.OrgInfoDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_cust_info;
import models.finance.entity.t_org_info;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import services.base.BaseService;

/**
 * 
 *
 * @ClassName: CustInfoService

 * @description 客户基本信息Service接口
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午10:01:58
 */
public class CustInfoService extends BaseService<t_cust_info> {
	
	protected static final OrgInfoDao orgInfoDao = Factory.getDao(OrgInfoDao.class);
	protected static final UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);

	protected CustInfoDao custInfoDao = null;
	
	protected CustInfoService () {
		this.custInfoDao = Factory.getDao(CustInfoDao.class);
		
		super.basedao = custInfoDao;
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
	 * @createDate 2018年10月6日-下午5:37:33
	 */
	public void truncateTable() {
		custInfoDao.truncateTable();
	}
	
	/**
	 * 插入表数据
	 *
	 * @Title: saveCustInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return boolean    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:19:13
	 */
	public void saveCustInfo () {
		custInfoDao.truncateTable(); // 清空表数据
		List<t_org_info> orgInfo = t_org_info.findAll();
		// 判断数据是否插入成功
		if (custInfoDao.saveCustInfo()) {
			List<t_cust_info> ListCustInfo = custInfoDao.findCustInfo();
			
			for (t_cust_info custInfo : ListCustInfo) {
				custInfo.setData_date(new Date());
				custInfo.setOrg_num(orgInfo.get(0).org_num); // 机构代码
				//如果证件类型为身份证（代码为0），则转换为报文要求格式
				if (custInfo.id_type.equals("0")) {
					custInfo.setId_type("PT01");
				}
				//如果营业执照为空：则该字段数据为N（无效）
				if (custInfo.getSts_flg() == null) {
					custInfo.setSts_flg('Y');
				}
				
				custInfo.save();
			}
		}
	}
	
	/**
	 * 查询表数据
	 *
	 * @Title: findCustInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_cust_info>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:19:48
	 */
	public List<t_cust_info> findCustInfo () {
		
		return custInfoDao.findCustInfo();
	}
	
	/**
	 * 生成xml文档
	 *
	 * @Title: createXml
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月11日-上午10:16:13
	 */
	public void createXmlByCustInfo() {
		List<t_cust_info> ListCustInfo = custInfoDao.findCustInfo();

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
			reportType.setText("FP.FP03.001");
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
			for (t_cust_info result : ListCustInfo) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("DATA_DATE");
				p1[1] = report.addElement("ORG_NUM");
				p1[2] = report.addElement("CUST_ID");
				p1[3] = report.addElement("CUST_NAM");
				p1[4] = report.addElement("ID_TYPE");
				p1[5] = report.addElement("ID_NO");
				p1[6] = report.addElement("CUST_ADDE_DESC");
				p1[7] = report.addElement("EXP_DATE");
				p1[8] = report.addElement("STS_FLG");

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
					if (type.equals("class java.lang.Character")) {
						// 如果type是类类型，则前面包含"class "，后面跟类名
						Method m = result.getClass().getMethod("get" + name);
						Character value = (Character) m.invoke(result);
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
							//p1[j].setText(time.toString());
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

			// 6、生成xml文件 Play.configuration.getProperty("attachments.path", "attachments");
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/day/oyph_FP.FP03客户基本信息表报文.xml");
			System.out.println("file:"+file.getPath());
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP03.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP03.001.xml失败");
		}
	}
	
	
}
