package push;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import models.common.entity.t_information;

public class PushDemo {
	
	/**构造方法:一*/
	public PushDemo() {}
	
	/**ios 消息推送(针对全部用户)
	 * 
	 * @param appkey app唯一标识符
	 * @param appMasterSecret 服务器秘钥，用于服务器端调用API请求时对发送内容做签名验证(友盟+生成)
	 * @param title 标题
	 * @param text 内容
	 * @param type 自定义参数类型(由开发者自己定义)
	 * @param content 自定义参数值(由开发者自己定义)
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.13
	 * */
	public static int iosBroadcastPush(String appkey, String appMasterSecret , String title , String text ,String type , String content) {

		try {
			IOSBroadcast broadcast = new IOSBroadcast(appkey,appMasterSecret);
			broadcast.setBody(text);
			broadcast.setTitle(title);
			broadcast.setBadge(1);
			broadcast.setSound("default");
			//broadcast.setProductionMode();
			broadcast.setTestMode();
			broadcast.setCustomizedField("type", type);
			broadcast.setCustomizedField("content", content);
			
			PushClient client = new PushClient();
			boolean flag = client.send(broadcast);
			if (flag) {
				
				return 1;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return 0;
	}

	/**android 消息推送(针对全部用户)
	 * 
	 * @param appkey app唯一标识符
	 * @param appMasterSecret 服务器秘钥，用于服务器端调用API请求时对发送内容做签名验证(友盟+生成)
	 * @param title 标题
	 * @param text 内容
	 * @param type 自定义参数类型(由开发者自己定义)
	 * @param content 自定义参数值(由开发者自己定义)
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.13
	 * */
	public static int androidBroadcastPush(String appkey, String appMasterSecret , String title , String text , String type , String content) {
		
		try {
			PushClient client = new PushClient();
			AndroidBroadcast broadcast = new AndroidBroadcast(appkey,appMasterSecret);
			broadcast.setTicker("Ticker");
			broadcast.setTitle(title);
			broadcast.setText(text);
			broadcast.goCustomAfterOpen("");
			broadcast.setPlayLights(true);
			broadcast.setPlayVibrate(true);
			broadcast.setPlaySound(true);
			broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
			//broadcast.setProductionMode();
			broadcast.setTestMode();
			broadcast.setExtraField("type", type);
			broadcast.setExtraField("content", content);
			broadcast.setPredefinedKeyValue("mipush" , true);
			broadcast.setPredefinedKeyValue("mi_activity" , "com.yq.oyph.ui.WelcomeActivity");
			broadcast.setDescription(title);
			boolean flag = client.send(broadcast);
			if (flag) {
				
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * 通知栏文本内容处理
	 * 
	 * @param html 需要处理的内容
	 * @author guoshijie
	 * @createdate 2017.12.13
	 * */
	public static String getTextFromHTML(String html) {
		String text = null;
		Document doc = null;
		
		if (html != null){
			doc = Jsoup.parse(html);
		}
		if(doc != null) {
			text = doc.text();
		}
		if(text != null) {
			
			StringBuilder builder = new StringBuilder(text);
			int index = 0;
			while (builder.length() > index) {
				char tmp = builder.charAt(index);
				if (Character.isSpaceChar(tmp) || Character.isWhitespace(tmp)) {
					builder.setCharAt(index, ' ');
				}
				index++;
			}
			text = builder.toString().replaceAll(" +", " ").trim();
			if (text.length() >= 30) {
				text = text.substring(0, 30) + "  详情>>";
			} else {
				text = text.substring(0, text.length()-1)+ "  详情>>";
			}
			
		}
		
		return text;

	}


}
