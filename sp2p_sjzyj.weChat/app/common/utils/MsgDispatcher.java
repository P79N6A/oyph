package common.utils;

import java.util.Date;
import java.util.Map;

import common.utils.wx.MessageUtil;
import models.message.response.TextMessage;
import service.ImageMessageService;
import service.TextMessageService;

/**
 * ClassName: MsgDispatcher
 * @Description: 消息业务处理分发器
 * @author liuyang
 * @date 2018年6月27日
 */
public class MsgDispatcher {
	
	protected static TextMessageService textMessageService = Factory.getService(TextMessageService.class);
	
	protected static ImageMessageService imageMessageService = Factory.getService(ImageMessageService.class);

	public static String processMessage(Map<String, String> requestMap) {
		// 消息类型  
        String msgType = requestMap.get("MsgType");
        System.out.println("msgType:::"+msgType);
        // 文本消息  
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
        	
        	String openid=requestMap.get("FromUserName"); //用户openid
        	String mpid=requestMap.get("ToUserName");   //公众号原始ID
        	         
        	//普通文本消息
        	TextMessage txtmsg=new TextMessage();
        	System.out.println("你好");
        	txtmsg.setToUserName(openid);
        	System.out.println("openid:"+openid);
        	txtmsg.setFromUserName(mpid);
        	txtmsg.setCreateTime(new Date().getTime());
        	txtmsg.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        	txtmsg.setFuncFlag(0);
        	
        	String resp = null;
        	String keywords = requestMap.get("Content");
        	
        	resp = textMessageService.queryContent(keywords);
        	
        	if(resp == null) {
        		resp = imageMessageService.queryContent(keywords);
        	}
        	
        	txtmsg.setContent(resp);
            return MessageUtil.textMessageToXml(txtmsg);
        	
        	
        }  
        // 图片消息  
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {  
            return "您发送的是图片消息！";  
        }  
        // 地理位置消息  
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {  
        	return "您发送的是地理位置消息！";  
        }  
        // 链接消息  
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {  
        	return "您发送的是链接消息！";  
        }  
        // 音频消息  
        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {  
        	return "您发送的是音频消息！";  
        }
        
        return null;
	}
}
