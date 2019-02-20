package common.utils;

import java.util.Map;

import common.utils.wx.MessageUtil;

/**
 * ClassName: EventDispatcher
 * @Description: 事件消息业务分发器
 * @author liuyang
 * @date 2018年6月27日
 */
public class EventDispatcher {

	public static String processEvent(Map<String, String> requestMap) {
		// 事件类型  
        String eventType = requestMap.get("Event");  
        // 订阅  
        if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {  
            return "谢谢您的关注！";  
        }  
        // 取消订阅  
        if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {  
            // 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息  
        }  
        // 自定义菜单点击事件  
        if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {  
        	//事件KEY值，与创建自定义菜单时指定的KEY值对应  
        	String eventKey = requestMap.get("EventKey");
        	return "点击菜单："+eventKey; 
        } 
		return null;
	}
}
