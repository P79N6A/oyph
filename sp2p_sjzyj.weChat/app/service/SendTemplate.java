package service;

import java.util.Date;

import com.citic.wx.util.WeChatUtils;

import common.utils.DateUtil;
import net.sf.json.JSONObject;

public class SendTemplate {
	 public static String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	public static void sendTemplate(String example) {
		//String example = null;
		System.out.println("我来了");
		String token = "17_qXn4CJ8c8kiby3jCqIiGNj01dJKF4XZVw4gVK0hTb8UeRw1RbfhoVclo9kZQ4S7MplX1i1RGHQvWxWLqsKYRtpq8XKcfpf1P1FTDaKYjN7XTT57w70k0FAbRu3ultCQC62VTOSo7wS7DN8SxHAIbAJACVD";
		String url = URL.replace("ACCESS_TOKEN", token);
		JSONObject jsobj1 = new JSONObject();
		JSONObject jsobj2 = new JSONObject();
		JSONObject jsobj3 = new JSONObject();
		JSONObject jsobj4 = new JSONObject();
		JSONObject jsobj5 = new JSONObject();
		JSONObject jsobj6 = new JSONObject();

		
		// touser是openid
		jsobj1.put("touser", example);
		// template_id是模板id
		jsobj1.put("template_id", "yG58CIo0U0xzc70GhKnjjT2VuUBJ0iA1NdqmQaY45M4");

		jsobj3.put("value", "韩猛向您致敬");
		jsobj3.put("color", "#173177");
		jsobj2.put("first", jsobj3);

		jsobj4.put("value", "J888");
		jsobj4.put("color", "#173177");
		jsobj2.put("keyword1", jsobj4);
		Date date = new Date();
		String dateStr =DateUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss");
		
		jsobj5.put("value", dateStr);
		jsobj5.put("color", "#173177");
		jsobj2.put("keyword2", jsobj5);

		jsobj6.put("value", "你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。你好，讴业普惠将于今日13:00 发布新标 ，金额100万，期限一辈子，年利率12%，如有意愿请前往普惠平台查看。");
		jsobj6.put("color", "#CD0000");
		jsobj2.put("remark", jsobj6);

		jsobj1.put("data", jsobj2);
		System.out.println(jsobj1);

		WeChatUtils.PostSendMsg(jsobj1, url);

	}
}
