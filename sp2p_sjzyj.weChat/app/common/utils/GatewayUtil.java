package common.utils;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import common.utils.wx.MessageUtil;
import models.message.response.TextMessage;
import play.mvc.Scope.Params;
import service.ImageMessageService;
import service.TextMessageService;

public class GatewayUtil {
	
	protected static TextMessageService textMessageService = Factory.getService(TextMessageService.class);
	
	protected static ImageMessageService imageMessageService = Factory.getService(ImageMessageService.class);
	
	/** 
     * 处理微信发来的请求 
     *  
     * @param request 
     * @return 
     */  
    public static String processRequest(String request) {  
        String respMessage = null;  
       // return "000000000";
        try {  
            // 默认返回的文本消息内容  
            String respContent = "请求处理异常，请稍候尝试！";  
  
            // xml请求解析  
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            
            // 消息类型  
            String msgType = requestMap.get("MsgType");
            
            if(MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){
            	System.out.println("111111111111111");
            	respContent = EventDispatcher.processEvent(requestMap); //进入事件处理
            	System.out.println("respContent:::::::"+respContent);
            }else{
            	System.out.println("2222222222    ");
            	respContent = MsgDispatcher.processMessage(requestMap); //进入消息处理
            	System.out.println("respContent:::::::"+respContent);
            }
            
            respMessage = respContent;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return respMessage;  
    }
}
