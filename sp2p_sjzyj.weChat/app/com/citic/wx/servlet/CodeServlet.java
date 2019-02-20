package com.citic.wx.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.citic.wx.util.WeChatUtils;
import net.sf.json.JSONObject;

public class CodeServlet extends HttpServlet {

	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String code = req.getParameter("code");
		System.out.println(code);
		String appId = "wxeb1fdb3ff2b823cd";
		String secret = "c5c793fec5f60b440657f638da6c475d";
		String openid = WeChatUtils.getOpenId(code,appId,secret);
		System.out.println("openid"+openid);
		
//		String appId = "wxab191b9f";
//		String secret = "9d190e481c2f7fe3bb";
//		AccessToken token = WeChatUtils.getAccessToken(appId, secret);
//		System.out.println(token.toString());
		
		// 发送模版消息
		String token = "17_x7EsjlSStLqb5gUIRuGbmNAH4jiMe51wdh3aVbpXMpcDIjkN6FhiCT9C82mXmB3jWyX6dKs-GZKTcllNd-lEi-P4jiKJ_7dCUZNrTAwfOVto7iUZDC0nDYbP-ZmsYwWiHMACDfqLaarNDgCREKWdAIAIOA";
		String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
		String url = URL.replace("ACCESS_TOKEN", token);

		JSONObject jsobj1 = new JSONObject();
		JSONObject jsobj2 = new JSONObject();
		JSONObject jsobj3 = new JSONObject();
		JSONObject jsobj4 = new JSONObject();
		JSONObject jsobj5 = new JSONObject();
		JSONObject jsobj6 = new JSONObject();

		//模版一
//		jsobj1.put("touser", openid);
//		jsobj1.put("template_id", "P0IFiIKhEc2ia6r1pjkzn6EL-j-vdFLFEB7-U4Dg-JQ");
//
//		jsobj3.put("value", "账单支付成功");
//		jsobj3.put("color", "#173177");
//		jsobj2.put("first", jsobj3);
//
//		jsobj4.put("value", "666");
//		jsobj4.put("color", "#173177");
//		jsobj2.put("keyword1", jsobj4);
//
//		jsobj5.put("value", "2564659879813214796416");
//		jsobj5.put("color", "#173177");
//		jsobj2.put("keyword2", jsobj5);
//
//		jsobj6.put("value", "你好，顾客已支付成功。");
//		jsobj6.put("color", "#173177");
//		jsobj2.put("remark", jsobj6);

		//模版二
		jsobj1.put("data", jsobj2);
		System.out.println(jsobj1);
		
		jsobj1.put("touser", openid);
		jsobj1.put("template_id", "iygn5z_lOfRsc0jUx-KdNcJk");

		jsobj3.put("value", "礼品领取成功");
		jsobj3.put("color", "#173177");
		jsobj2.put("first", jsobj3);

		jsobj4.put("value", "人民币一个亿");
		jsobj4.put("color", "#173177");
		jsobj2.put("keyword1", jsobj4);

		jsobj5.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		jsobj5.put("color", "#173177");
		jsobj2.put("keyword2", jsobj5);

		jsobj6.put("value", "你好，你已欠小明一个亿，请速度还！");
		jsobj6.put("color", "#173177");
		jsobj2.put("remark", jsobj6);

		jsobj1.put("data", jsobj2);
		System.out.println(jsobj1);

		WeChatUtils.PostSendMsg(jsobj1, url);
	}
	

}