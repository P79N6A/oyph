package services.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.enums.SuperviseReportType;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.PageBean;
import common.utils.TimeUtil;
import daos.finance.CashFlowDao;
import models.finance.entity.t_cash_flow;
import models.finance.entity.t_return_status.ReportType;
import play.Play;
import services.base.BaseService;
/**
 * 
 *
 * @ClassName: CashFlowService
 *
 * @description 财务-财务信息-现金流量表Service
 *
 * @author LiuHangjing
 * @createDate 2018年10月6日-下午6:12:45
 */
public class CashFlowService extends BaseService<t_cash_flow>{

	protected static CashFlowDao cashFlowDao = Factory.getDao(CashFlowDao.class);
	
	public CashFlowService() {
		super.basedao = cashFlowDao;
	}
	/**
	 * 
	 * @Title: pageOfCashFlow
	 * 
	 * @description 现金流量分页显示
	 * @param currPage 当前页
	 * @param  pageSize 每页条数
	 * @return PageBean<t_cash_flow>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月7日-上午9:45:54
	 */
	public PageBean<t_cash_flow> pageOfCashFlow(int currPage, int pageSize){
		
		return cashFlowDao.pageOfCashFlow(currPage, pageSize);
	}
	/**
	 * 
	 * @Title: onIssue
	 * 
	 * @description 查询上期信息返回页面显示
	 * @return t_cash_flow    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月11日-上午11:43:33
	 */
	public t_cash_flow onIssue(String createTime){
		
		return cashFlowDao.onIssue(createTime); 
	}
	

	/**
     * 生成xml方法
     */
    public void createCashFlowXml(){
    	t_cash_flow model = cashFlowDao.reportCashFlow();
    	t_cash_flow model1 = cashFlowDao.reportCashFlowLast();
    	if(model!=null){
    	
        try {
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            //创建跟节点Document
            Element doc = document.addElement("Document");
            //2.报文头
            Element head = doc.addElement("HeaderInfo");
            
            Element sender = head.addElement("SenderID");
            //3.设置内容
            sender.setText("911301050799558020");
            Element receiver = head.addElement("ReceiverID");
            receiver.setText("000000");
            Element reportSender = head.addElement("ReportSender");
            reportSender.setText("石家庄菲尔德投资咨询有限公司");
            Element reportReceiver = head.addElement("ReportReceiver");
            reportReceiver.setText("");
            Element reportSendDate = head.addElement("ReportSendDate");
            //获取时间
			Date d=new Date();
			reportSendDate.setText(TimeUtil.dateToStrDate(d));
            Element reportSendTime = head.addElement("ReportSendTime");
			reportSendTime.setText(TimeUtil.dateToStr_HHmmss(d));
            Element reportType = head.addElement("ReportType");
            reportType.setText("P.P12.001");
            Element mesgID = head.addElement("MesgID");
            mesgID.setText("A1234B1234C1234D1234oplk8976tr1d");
            //报文信息
            Element reportInfo = doc.addElement("ReportInfo");
            //报文基本信息
            Element reportBasic = reportInfo.addElement("ReportBasicInfo");
            Element fill = reportBasic.addElement("FillingAgency");
            fill.setText("911301050799558020");
            Element reportDate = reportBasic.addElement("ReportDate");
            reportDate.setText(TimeUtil.dateToStrDate(model.create_time));
            Element draftsman = reportBasic.addElement("Draftsman");
            draftsman.setText("兰自旋");
            Element reviewer = reportBasic.addElement("Reviewer");
            reviewer.setText("裴雪松");
            Element leadingOfficial = reportBasic.addElement("LeadingOfficial");
            leadingOfficial.setText("冯鑫");
            //报文清单
            Element reports = reportInfo.addElement("Reports");
            Element report = reports.addElement("Report");
            //获取属性
            Field[] field = model.getClass().getDeclaredFields();
            //本期金额
            //属性的长度
            int l = field.length;
            
            Element p1[] = new Element[l];
            int flag = 120001;
            for(int j = 0;j < l;j++){
            	String name = field[j].getName();
           
            	name = name.substring(0, 1).toUpperCase() + name.substring(1);
            	//获取属性的类型
            	String type = field[j].getGenericType().toString();
            	if(type.equals("class java.math.BigDecimal")){
            		BigDecimal val = null;
            		//通过get方法
            		Method m = model.getClass().getMethod("get"+name);
            		val = (BigDecimal) m.invoke(model);
            		
            		p1[j] = report.addElement("P"+flag);
            		if(val!=null){
            			p1[j].setText(val.toString());
            			
            		}else{
            			p1[j].setText("0.00");
            		
            		}
            		flag++;
            		
            	}
            }
            //上期金额
            Element p2[] = new Element[l];
            int flag2 = 120036;
            for(int j = 0;j < l;j++){
            	String name = field[j].getName();
            	
            	name = name.substring(0, 1).toUpperCase() + name.substring(1);
            	//获取属性的类型
            	String type = field[j].getGenericType().toString();
            	if(type.equals("class java.math.BigDecimal")){
            		BigDecimal val = null;
            		//通过get方法
            		Method m = model1.getClass().getMethod("get"+name);
            		val = (BigDecimal) m.invoke(model1);
            		
            		p2[j] = report.addElement("P"+flag2);
            		if(val!=null){
            			p2[j].setText(val.toString());
            			
            		}else{
            			p2[j].setText("0.00");
            		
            		}
            		flag2++;
            		
            	}
            }
            
            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            // 6、生成xml文件            
            File file = new File(Play.applicationPath.toString().replace("\\", "/")+"/data/season"+ File.separator+ JRBUtils.getXMLFileName(null, SuperviseReportType.AGENCY_ASSET_DEBT_TABLE, "oyph"));
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            // 设置是否转义，默认使用转义字符
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
            
            JRBHttpsUtils.post(file,ReportType.SEASON);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    	
    	}
    }
    
}
