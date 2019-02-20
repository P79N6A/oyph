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
import daos.finance.CustOrgDao;
import daos.finance.OrgInfoDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_cust_org;
import models.finance.entity.t_org_info;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import services.base.BaseService;
import services.common.UserInfoService;

/**
 * 
 *
 * @ClassName: CustOrgService

 * @description 客户关联方信息Service接口
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午10:08:09
 */
public class CustOrgService extends BaseService<t_cust_org>{

	protected static final UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	protected static OrgInfoDao orgInfoDao = Factory.getDao(OrgInfoDao.class);
	protected CustOrgDao custOrgDao = null;

	protected CustOrgService () {
		this.custOrgDao = Factory.getDao(CustOrgDao.class);
		
		super.basedao = custOrgDao;
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
	 * @createDate 2018年10月7日-上午11:11:39
	 */
	public void truncateTable() {
		custOrgDao.truncateTable();
	}
	
	/**
	 * 查询表数据
	 *
	 * @Title: findCustOrg
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_cust_org>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月7日-上午11:19:56
	 */
	public List<t_cust_org> findCustOrg () {
		
		return custOrgDao.findCustOrg();
	}
	
	/**
	 * 插入数据
	 *
	 * @Title: saveCustOrg
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月11日-下午12:44:51
	 */
	public void saveCustOrg() {
		custOrgDao.truncateTable(); // 清空表数据
		List<t_org_info> infos = orgInfoDao.findAll();
		
		t_cust_org custOrg = new t_cust_org();
		custOrg.setData_date(new Date());
		custOrg.setOrg_num(infos.get(0).org_num); // 机构代码
		custOrg.setRelation_id_type("PT10"); // 关联方证件类型（身份证）
				
		custOrg.save();
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
	 * @createDate 2018年10月11日-上午10:21:45
	 */
	public void createXmlByCustOrg() {
		List<t_cust_org> ListCustOrg = custOrgDao.findCustOrg();
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
			reportType.setText("FP.FP04.001");
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
			for (t_cust_org result : ListCustOrg) {
				Field[] field = result.getClass().getDeclaredFields();
				Element p1[] = new Element[field.length];
				Element report = reports.addElement("Report");
				p1[0] = report.addElement("DATA_DATE");
				p1[1] = report.addElement("ORG_NUM");
				p1[2] = report.addElement("CUST_ID");
				p1[3] = report.addElement("RELATION_ID");
				p1[4] = report.addElement("RELATION_TYPE");
				p1[5] = report.addElement("RELATION_ID_TYPE");
				p1[6] = report.addElement("RELATION_ID_NO");
				p1[7] = report.addElement("CUST_TELEPHONE_NO");
				p1[8] = report.addElement("CUST_ADDE_DESC");
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
							p1[j].setText("0");
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
							p1[j].setText(sdf.format(number).toString());
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
			File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/day/oyph_FP.FP04客户关联方信息表报文.xml");
			XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
			// 设置是否转义，默认使用转义字符
			writer.setEscapeText(false);
			writer.write(document);
			writer.close();
			System.out.println("FP.FP04.001.xml成功");
			JRBHttpsUtils.post(file, ReportType.DAY);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FP.FP04.001.xml失败");
		}
	}
	
}
