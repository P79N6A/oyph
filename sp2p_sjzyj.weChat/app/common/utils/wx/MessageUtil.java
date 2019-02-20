package common.utils.wx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import models.message.response.Article;
import models.message.response.ImageMessage;
import models.message.response.MusicMessage;
import models.message.response.NewsMessage;
import models.message.response.TextMessage;
import models.message.response.VideoMessage;
import models.message.response.VoiceMessage;
import play.mvc.Scope.Params;


/**
 * 微信消息处理工具
 * @author liuyang
 *
 */
public class MessageUtil {
	
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";//返回消息类型：文本 
	
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";//返回消息类型：音乐 
    
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";//返回消息类型：图文 
    
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";//请求消息类型：文本
   
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";//请求消息类型：图片
    
    public static final String REQ_MESSAGE_TYPE_LINK = "link"; //请求消息类型：链接 
    
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";//请求消息类型：地理位置 
   
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";//请求消息类型：音频
    
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";//请求消息类型：推送   
  
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";//事件类型：subscribe(订阅)  
     
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";//事件类型：unsubscribe(取消订阅)  
    
    public static final String EVENT_TYPE_CLICK = "CLICK";//事件类型：CLICK(自定义菜单点击事件) 
     
	
	/** 
	 * 解析微信发来的请求（XML） 
	 *  
	 * @param request 
	 * @return 
	 * @throws Exception 
	 */  
	@SuppressWarnings("unchecked")  
	public static Map<String, String> parseXml(String request) throws Exception {  
	    // 将解析结果存储在HashMap中  
	    Map<String, String> map = new HashMap<String, String>();
	    
	    // 从request中取得输入流  
	    InputStream inputStream = new ByteArrayInputStream(request.substring(request.indexOf("<xml>"), request.lastIndexOf("</xml>")+6).getBytes());  
	    // 读取输入流  
	    SAXReader reader = new SAXReader();  
	    Document document = reader.read(inputStream);  
	    // 得到xml根元素  
	    Element root = document.getRootElement();  
	    // 得到根元素的所有子节点  
	    List<Element> elementList = root.elements();  
	  
	    // 遍历所有子节点  
	    for (Element e : elementList)  
	        map.put(e.getName(), e.getText());  
	  
	    // 释放资源  
	    inputStream.close();  
	    inputStream = null;  
	  
	    return map;  
	}  
	
	
	/** 
	 * 文本消息对象转换成xml 
	 *  
	 * @param textMessage 文本消息对象 
	 * @return xml 
	 */  
	public static String textMessageToXml(TextMessage textMessage) {  
	    xstream.alias("xml", textMessage.getClass());  
	    return xstream.toXML(textMessage);  
	}
	
	/** 
	 * 图文消息对象转换成xml 
	 *  
	 * @param newsMessage 图文消息对象 
	 * @return xml 
	 */  
	public static String newsMessageToXml(NewsMessage newsMessage) {  
	    xstream.alias("xml", newsMessage.getClass());  
	    xstream.alias("item", new Article().getClass());  
	    return xstream.toXML(newsMessage);  
	} 
	
	/**
     * @Description: 图片消息对象转换成xml
     * @param @param imageMessage 图片消息对象
     * @param @return
     * @author liuyang
     * @date 2018年6月27日
     */
    public static String imageMessageToXml(ImageMessage imageMessage) {
        xstream.alias("xml", imageMessage.getClass());
        return xstream.toXML(imageMessage);
    }
    
    /**
     * @Description: 语音消息对象转换成xml
     * @param @param voiceMessage 语音消息对象
     * @param @return
     * @author liuyang
     * @date 2018年6月27日
     */
    public static String voiceMessageToXml(VoiceMessage voiceMessage) {
        xstream.alias("xml", voiceMessage.getClass());
        return xstream.toXML(voiceMessage);
    }
	
    /**
     * @Description: 视频消息对象转换成xml
     * @param @param videoMessage 视频消息对象
     * @param @return
     * @author liuyang
     * @date 2018年6月27日
     */
    public static String videoMessageToXml(VideoMessage videoMessage) {
        xstream.alias("xml", videoMessage.getClass());
        return xstream.toXML(videoMessage);
    }
 
    /**
     * @Description: 音乐消息对象转换成xml
     * @param @param musicMessage 音乐消息对象
     * @param @return
     * @author liuyang
     * @date 2018年6月27日
     */
    public static String musicMessageToXml(MusicMessage musicMessage) {
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }
	
	/** 
	 * 扩展xstream，使其支持CDATA块 
	 *  
	 * @date 2013-05-19 
	 */  
	private static XStream xstream = new XStream(new XppDriver() {  
	    public HierarchicalStreamWriter createWriter(Writer out) {  
	        return new PrettyPrintWriter(out) {  
	            // 对所有xml节点的转换都增加CDATA标记  
	            boolean cdata = true;  
	   
	            @SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {  
	                super.startNode(name, clazz);  
	            }  
	  
	            protected void writeText(QuickWriter writer, String text) {  
	                if (cdata) {  
	                    writer.write("<![CDATA[");  
	                    writer.write(text);  
	                    writer.write("]]>");  
	                } else {  
	                    writer.write(text);  
	                }  
	            }  
	        };  
	    }  
	});  

}
