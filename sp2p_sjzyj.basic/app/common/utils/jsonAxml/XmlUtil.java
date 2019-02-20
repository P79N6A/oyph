package common.utils.jsonAxml;

import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.parsers.*;

/**
 * xml格式化类
 *
 * @description 
 *
 * @author liudong
 * @createDate 2015年12月9日
 */
public class XmlUtil {
	Document doc;

	/**
	 * 读取XML文件
	 *
	 * @author liudong
	 * @createDate 2015年12月9日
	 * @param amount
	 * @return
	 */
	public XmlUtil setDocument(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			xml = xml.trim();
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
			docBuilder = factory.newDocumentBuilder();
			doc = docBuilder.parse(inputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return this;
	}
	
	/**
	 * 获得xml中的节点数
	 *
	 * @author liudong
	 * @createDate 2015年12月9日
	 * @param amount
	 * @return
	 */
	public int getCount(String NodeName) {
		NodeList list = doc.getDocumentElement().getElementsByTagName(NodeName);
		int count = list.getLength();
		
		return count;
	}

	/**
	 * 获得xml中的节点值
	 *
	 * @author liudong
	 * @createDate 2015年12月9日
	 * @param amount
	 * @return
	 */
	public String getNodeValue(String NodeName) {
		NodeList list = doc.getDocumentElement().getElementsByTagName(NodeName);
		
		if (list == null || list.getLength() <= 0) {
			return "";
		}

		return (list.item(0).getFirstChild().getNodeValue() == null ? "" : list.item(0).getFirstChild().getNodeValue());
	}
}
