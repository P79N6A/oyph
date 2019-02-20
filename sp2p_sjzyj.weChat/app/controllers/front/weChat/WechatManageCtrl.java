package controllers.front.weChat;

import java.io.PrintWriter;

import common.utils.GatewayUtil;
import common.utils.wx.SignUtil;
import controllers.common.FrontBaseController;
import jobs.AccessTokenJob;
import play.mvc.Scope.Params;

public class WechatManageCtrl extends FrontBaseController {

	/**
	 * 验证信息是否来自微信
	 *
	 *
	 * @author liuyang
	 * @createDate 2018年05月30日
	 */
	public static void checkFromWechat() {
		
		String signature = params.get("signature");// 微信加密签名  
		String timestamp = params.get("timestamp");// 时间戳  
		String nonce = params.get("nonce");// 随机数  
		String echostr = params.get("echostr");// 随机字符串   
		
		SignUtil.checkSignature(signature, timestamp, nonce);
		
		renderJSON(echostr);
//		System.out.println("1211111");
//		renderJSON("刘航精能吃");

	}
	
	/**
	 * 处理微信服务器发来的消息
	 *
	 *
	 * @author liuyang
	 * @createDate 2018年05月31日
	 */
	public static void disposeWechat() {
		System.out.println("wolaol");
		String request = params.allSimple().toString();
		System.out.println(request);
		// 调用核心业务类接收消息、处理消息  
        String respMessage = GatewayUtil.processRequest(request);
        System.out.println(respMessage+"respMessage");
        renderJSON(respMessage);
		

	}
}
