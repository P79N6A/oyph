package common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSONObject;

import play.Logger;

/**
 * 生成XML工具类
 * @author guoShiJie
 * */
public class JRBXMLUtils {

	/**
	 * 生成XML
	 * @param headerInfoMap 报文头
	 * @param reportBasicInfoMap 报文基本信息
	 * @param reportList 报文
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.10
	 * */
	public static void createXML (JSONObject headerInfoMap,JSONObject reportBasicInfoMap, List<JSONObject> reportList) {
		
		try{
			// 1、创建document对象
			Document document = DocumentHelper.createDocument();
			// 创建跟节点Document
			Element doc = document.addElement("Document");
			// 报文头
			Element headerInfo = doc.addElement("HeaderInfo");
			
			for (Map.Entry<String, Object> header : headerInfoMap.entrySet()) {
	        	
	        	Element element = headerInfo.addElement(header.getKey());
	        	element.setText(header.getValue() == null ? "" : header.getValue().toString());
			}
			
			/** 报文信息 */
	        Element reportInfo = doc.addElement("ReportInfo");
	        /** 报文基本信息 */
	        Element reportBasicInfo = reportInfo.addElement("ReportBasicInfo");
	        /** 报文基本信息中的内容 */
	        for (Map.Entry<String, Object> header : reportBasicInfoMap.entrySet()) {
	        	
	        	Element element = reportBasicInfo.addElement(header.getKey());
	        	element.setText(header.getValue() == null ? "" : header.getValue().toString());
			}
	        
	        /** 报文清单 */
	        Element reports = reportInfo.addElement("Reports");
	        /** 报文清单中的内容 */
	        if (reportList != null && reportList.size() > 0 ) {
	        	for (JSONObject rpt : reportList) {
	        		if (rpt != null && rpt.size() > 0) {
	        			/**报文*/
	        			Element report = reports.addElement("Report");
	        			for (Map.Entry<String, Object> pt : rpt.entrySet()) {
	        	            	
	        	           	Element element = report.addElement(pt.getKey());
	        	           	element.setText(pt.getValue() == null ? "" : pt.getValue().toString());
	        			}
	        		}
	        		
				}
	        }
	        
	        // 5、设置生成xml的格式
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        // 设置编码格式
	        format.setEncoding("UTF-8");
	
	        // 6、生成xml文件
	        File file = new File("d:/P.xml");
	        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
	        // 设置是否转义，默认使用转义字符
	        writer.setEscapeText(false);
	        writer.write(document);
	        writer.close();
	        System.out.println("P.P08.001.xml成功");
        
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("P.P08.001.xml失败");
		}
	}
	
	
	/**
	 * 生成XML
	 * @param headerInfoMap 报文头
	 * @param reportBasicInfoMap 报文基本信息
	 * @param reportList 报文
	 * @param fileURL 生成文件路径
	 * @author guoShiJie
	 * @createDate 2018.10.10
	 * */
	public static File createXML (JSONObject headerInfoMap,JSONObject reportBasicInfoMap, List<JSONObject> reportList,String fileURL) {
		String fileName = null;
		try{
			// 1、创建document对象
			Document document = DocumentHelper.createDocument();
			// 创建跟节点Document
			Element doc = document.addElement("Document");
			// 报文头
			Element headerInfo = doc.addElement("HeaderInfo");
			
			for (Map.Entry<String, Object> header : headerInfoMap.entrySet()) {
	        	
	        	Element element = headerInfo.addElement(header.getKey());
	        	element.setText(header.getValue() == null ? "" : header.getValue().toString());
			}
			
			/** 报文信息 */
	        Element reportInfo = doc.addElement("ReportInfo");
	        /** 报文基本信息 */
	        Element reportBasicInfo = reportInfo.addElement("ReportBasicInfo");
	        /** 报文基本信息中的内容 */
	        for (Map.Entry<String, Object> header : reportBasicInfoMap.entrySet()) {
	        	
	        	Element element = reportBasicInfo.addElement(header.getKey());
	        	element.setText(header.getValue() == null ? "" : header.getValue().toString());
			}
	        
	        /** 报文清单 */
	        Element reports = reportInfo.addElement("Reports");
	        /** 报文清单中的内容 */
	        if (reportList != null && reportList.size() > 0 ) {
	        	for (JSONObject rpt : reportList) {
	        		if (rpt != null && rpt.size() > 0) {
	        			/**报文*/
	        			Element report = reports.addElement("Report");
	        			for (Map.Entry<String, Object> pt : rpt.entrySet()) {
	        	            	
	        	           	Element element = report.addElement(pt.getKey());
	        	           	element.setText(pt.getValue() == null ? "" : pt.getValue().toString());
	        			}
	        		}
	        		
				}
	        }
	        
	        // 5、设置生成xml的格式
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        // 设置编码格式
	        format.setEncoding("UTF-8");
	
	        // 6、生成xml文件
	        File file = new File(fileURL);
	        fileName = file.getName();
	        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
	        // 设置是否转义，默认使用转义字符
	        writer.setEscapeText(false);
	        writer.write(document);
	        writer.close();
	        Logger.info(fileName+"文件生成成功");
	        return file;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info(fileName+"文件生成失败");
		}
		return null;
	}
}
