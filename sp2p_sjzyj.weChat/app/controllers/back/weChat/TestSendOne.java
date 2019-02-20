package controllers.back.weChat;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Example;

import com.citic.wx.util.WeChatUtils;

import controllers.common.BackBaseController;
import net.sf.json.JSONObject;
import service.SendTemplate;

/**
 * 
 *
 * @ClassName: TestSendOne
 *
 * @description 微信公众号发送模版消息(单人) 与公众号群发消息 （多人）   
 *
 * @author HanMeng
 * @createDate 2018年12月29日-上午11:44:04
 */
public class TestSendOne extends BackBaseController {
	// 发送模版消息接口地址
	// 单人（模板）
	 public static String URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	// 多人（文本或素材）
	//public static String URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";

	/**
	 * 
	 * @Title: test1
	 * @description: 微信公众号发送模版消（单人）
	 * 
	 * @return void
	 *
	 * @author HanMeng
	 * @createDate 2018年12月29日-上午11:43:53
	 */
	public static void test(){
		List<String> list = new ArrayList<String>();
		list.add("ol-VQ5twDYpSW-AtxUvk3ZE3sDpk");
		list.add("ol-VQ5hVvC-hr2zmMeH5IA7V4wbc");
		list.add("ol-VQ5r8AzKTOuWtmnCNkXlzfB54");
		list.add("ol-VQ5v963n6W5luhL_NxG-X2RBI");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.size());			
			 String example = list.get(i);//获取每一个Example对象
		       // String name = example.getName();
		        System.out.print("第"+i+"个=？"+example);
		        SendTemplate.sendTemplate(example);
		}
	}
	

	/**
	 * 
	 * @Title: test
	 * @description: 微信公众号群发（文本类型）
	 * 
	 * @return void
	 *
	 * @author HanMeng
	 * @createDate 2018年12月29日-上午11:44:46
	 */
	public static void testmore() {

		String token = "17_oGzVkTl08DnExLSN150d9GwZHV8zULDhnAKGkN488ybBBmuExXsNBdw3RvE6fCwC5fw5O1rawhEEi-XXxlfiX_twaZFgUUBR71QckaIQqBiO6HEmVeKJZuF2TMwGJKbAHAMPQ";
		String url = URL.replace("ACCESS_TOKEN", token);
		List<String> list = new ArrayList<String>();
		list.add("ol-VQ5twDYpSW-AtxUvk3ZE3sDpk");
		list.add("ol-VQ5hVvC-hr2zmMeH5IA7V4wbc");
		list.add("ol-VQ5r8AzKTOuWtmnCNkXlzfB54");
		list.add("ol-VQ5v963n6W5luhL_NxG-X2RBI");

		JSONObject jsobj1 = new JSONObject();
		JSONObject jsobj2 = new JSONObject();
		JSONObject jsobj3 = new JSONObject();
		JSONObject jsobj4 = new JSONObject();
		JSONObject jsobj5 = new JSONObject();
		JSONObject jsobj6 = new JSONObject();
		//String tt = 
		jsobj2.put("content", "今天天气真好,哈哈！！！");
		jsobj1.put("touser", list);
		jsobj1.put("msgtype", "text");
		jsobj1.put("text", jsobj2);
		System.out.println(jsobj1);
		WeChatUtils.PostSendMsg(jsobj1, url);

	}
}
