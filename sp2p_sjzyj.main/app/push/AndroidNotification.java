package push;

import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONObject;


public class AndroidNotification extends UmengNotification {
	
	/**消息内容*/
	protected static final HashSet<String> PAYLOAD_KEYS = new HashSet<String>(Arrays.asList(new String[]{
				"display_type"}));
		
	/**消息体(详情参照友盟+api文档)*/
	protected static final HashSet<String> BODY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"ticker", "title", "text", "builder_id", "icon", "largeIcon", "img", "play_vibrate", "play_lights", "play_sound",
			"sound", "after_open", "url", "activity", "custom"}));
	
	/**消息类型*/
	public enum DisplayType{
		
		/**通知:消息送达到用户设备后，由友盟SDK接管处理并在通知栏上显示通知内容。*/
		NOTIFICATION{public String getValue(){return "notification";}},
		
		/**消息:消息送达到用户设备后，消息内容透传给应用自身进行解析处理。*/
		MESSAGE{public String getValue(){return "message";}};
		
		public abstract String getValue();
		
	}
	
	/**后续行为*/
	public enum AfterOpenAction{
		
		/**打开应用*/
        go_app,
        
        /**跳转到URL*/
        go_url,
        
        /**打开特定的activity*/
        go_activity,
        
        /**用户自定义内容*/
        go_custom
        
	}
	
	/**
	 * 添加参数
	 * 
	 * @param key 参数名称
	 * @param value 参数值
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	@Override
	public boolean setPredefinedKeyValue(String key, Object value) throws Exception {
		
		if (ROOT_KEYS.contains(key)) {
			
			rootJson.put(key, value);
			
		} else if (PAYLOAD_KEYS.contains(key)) {

			JSONObject payloadJson = null;
			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}
			payloadJson.put(key, value);
			
		} else if (BODY_KEYS.contains(key)) {

			JSONObject bodyJson = null;
			JSONObject payloadJson = null;

			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}

			if (payloadJson.has("body")) {
				bodyJson = payloadJson.getJSONObject("body");
			} else {
				bodyJson = new JSONObject();
				payloadJson.put("body", bodyJson);
			}
			bodyJson.put(key, value);
		} else if (POLICY_KEYS.contains(key)) {

			JSONObject policyJson = null;
			if (rootJson.has("policy")) {
				policyJson = rootJson.getJSONObject("policy");
			} else {
				policyJson = new JSONObject();
				rootJson.put("policy", policyJson);
			}
			policyJson.put(key, value);
		} else {
			if (key == "payload" || key == "body" || key == "policy" || key == "extra") {
				throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
			} else {
				throw new Exception("Unknown key: " + key);
			}
		}
		return false; 
		
	}

	/**
	 * 添加自定义参数
	 * 
	 * @param key 参数名称
	 * @param value 参数值
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * * */
		public boolean setExtraField(String key, String value) throws Exception {
			
			JSONObject payloadJson = null;
			JSONObject extraJson = null;
			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}
			
			if (payloadJson.has("extra")) {
				extraJson = payloadJson.getJSONObject("extra");
			} else {
				extraJson = new JSONObject();
				payloadJson.put("extra", extraJson);
			}
			extraJson.put(key, value);
			return true;
			
		}
		
		/**
		 * 添加消息类型
		 * @param d 消息类型
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setDisplayType(DisplayType d) throws Exception {
			
			setPredefinedKeyValue("display_type", d.getValue());
			
		}
		
		/**
		 * 添加通知提示文字
		 * @param ticker 通知提示文字
		 * @author guoshijie
		 * */
		public void setTicker(String ticker) throws Exception {
			
			setPredefinedKeyValue("ticker", ticker);
			
		}
		
		/**
		 * 添加通知标题
		 * @param title 通知标题
		 * @author guoshijie
		 * */
		public void setTitle(String title) throws Exception {
			
			setPredefinedKeyValue("title", title);
			
		}
		
		/**
		 * 添加文字描述
		 * @param text 文字描述
		 * @author guoshijie
		 * */
		public void setText(String text) throws Exception {
			
			setPredefinedKeyValue("text", text);
			
		}
		
		/**
		 * 设置用于标识该通知采用的样式
		 * @param builder_id 用于标识该通知采用的样式 注:使用该参数时, 必须在SDK里面实现自定义通知栏样式
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setBuilderId(Integer builder_id) throws Exception {
			
			setPredefinedKeyValue("builder_id", builder_id);
			
		}
		
		/**
		 * 设置状态栏图标ID(R.drawable.[smallIcon])
		 * @param icon 状态栏图标ID 注:如果没有, 默认使用应用图标
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setIcon(String icon) throws Exception {
			
			setPredefinedKeyValue("icon", icon);
			
		}
		
		/**
		 * 设置通知栏拉开后左侧图标ID
		 * @param largeIcon 通知栏拉开后左侧图标ID
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setLargeIcon(String largeIcon) throws Exception {
			
			setPredefinedKeyValue("largeIcon", largeIcon);
			
		}
		
		/**
		 * 添加通知栏大图标的URL链接
		 * @param img 通知栏大图标的URL链接 注:该字段的优先级大于largeIcon。该字段要求以http或者https开头
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setImg(String img) throws Exception {
			
			setPredefinedKeyValue("img", img);
			
		}
		
		/**
		 * 设置收到通知是否震动
		 * @param play_vibrate 收到通知震动(默认为"true")
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setPlayVibrate(Boolean play_vibrate) throws Exception {
			
			setPredefinedKeyValue("play_vibrate", play_vibrate.toString());
			
		}
		
		/**
		 * 设置收到通知是否闪灯
		 * @param play_lights 收到通知闪灯(默认为"true")
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setPlayLights(Boolean play_lights) throws Exception {
			
			setPredefinedKeyValue("play_lights", play_lights.toString());
			
		}
		
		/**
		 * 设置收到通知是发出声音
		 * @param play_sound 收到通知发出声音(默认为"true")
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setPlaySound(Boolean play_sound) throws Exception {
			
			setPredefinedKeyValue("play_sound", play_sound.toString());
			
		}
		
		/**
		 * 设置通知声音(R.raw.[sound])
		 * @param sound 通知声音文件名(如果该字段为空，采用SDK默认的声音)
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setSound(String sound) throws Exception {
			
			setPredefinedKeyValue("sound", sound);
			
		}
		
		/**
		 * 收到通知后播放指定的声音文件
		 * @param sound 声音文件
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setPlaySound(String sound) throws Exception {
			
			setPlaySound(true);
			setSound(sound);
			
		}
		
		/**
		 * 点击"通知"的后续行为(默认为打开app)
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void goAppAfterOpen() throws Exception {
			
			setAfterOpenAction(AfterOpenAction.go_app);
			
		}
		/**
		 * 点击"通知"的后续行为--跳转链接(默认为打开app)
		 * @param url 指定链接
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void goUrlAfterOpen(String url) throws Exception {
			
			setAfterOpenAction(AfterOpenAction.go_url);
			setUrl(url);
			
		}
		
		/**
		 * 点击"通知"的后续行为--跳转到指定activity(默认为打开app)
		 * @param activity 
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void goActivityAfterOpen(String activity) throws Exception {
			
			setAfterOpenAction(AfterOpenAction.go_activity);
			setActivity(activity);
			
		}
		
		/**
		 * 点击"通知"的后续行为--用户自定义内容(默认为打开app)
		 * @param custom 用户自定义内容 
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void goCustomAfterOpen(String custom) throws Exception {
			setAfterOpenAction(AfterOpenAction.go_custom);
			setCustomField(custom);
		}
		
		public void goCustomAfterOpen(JSONObject custom) throws Exception {
			
			setAfterOpenAction(AfterOpenAction.go_custom);
			setCustomField(custom);
			
		}
		
		/**start
		 * 点击"通知"的后续行为，默认为打开app。原始接口
		 * @author guoshijie
		 * @createdate 2017.12.7
		 * */
		public void setAfterOpenAction(AfterOpenAction action) throws Exception {
			
			setPredefinedKeyValue("after_open", action.toString());
			
		}
		public void setUrl(String url) throws Exception {
			
			setPredefinedKeyValue("url", url);
			
		}
		public void setActivity(String activity) throws Exception {
			
			setPredefinedKeyValue("activity", activity);
			
		}
		
		public void setCustomField(String custom) throws Exception {
			
			setPredefinedKeyValue("custom", custom);
			
		}
		public void setCustomField(JSONObject custom) throws Exception {
			
			setPredefinedKeyValue("custom", custom);
			
		}
		/**end*/
		
}
