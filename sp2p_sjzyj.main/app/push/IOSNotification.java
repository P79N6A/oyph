package push;

import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONObject;

public class IOSNotification extends UmengNotification {

		/**aps*/
		protected static final HashSet<String> APS_KEYS = new HashSet<String>(Arrays.asList(new String[]{
				"badge", "sound", "content-available"
		}));
		/**alert*/
		protected static final HashSet<String> ALERT_KEYS = new HashSet<String>(Arrays.asList(new String[]{
				"body", "subtitle", "title"
		}));
		/**
		 * 添加参数
		 * 
		 * @param key 参数名称
		 * @param value 参数值
		 * 
		 * @author guoshijie
		 * @createdate 2017.12.8
		 * */
		@Override
		public boolean setPredefinedKeyValue(String key, Object value) throws Exception {
			if (ROOT_KEYS.contains(key)) {
				
				rootJson.put(key, value);
			} else if (APS_KEYS.contains(key)) {
				
				JSONObject apsJson = null;
				JSONObject payloadJson = null;
				if (rootJson.has("payload")) {
					payloadJson = rootJson.getJSONObject("payload");
				} else {
					payloadJson = new JSONObject();
					rootJson.put("payload", payloadJson);
				}
				if (payloadJson.has("aps")) {
					apsJson = payloadJson.getJSONObject("aps");
				} else {
					apsJson = new JSONObject();
					payloadJson.put("aps", apsJson);
				}
				apsJson.put(key, value);
			} else if (POLICY_KEYS.contains(key)) {
				
				JSONObject policyJson = null;
				if (rootJson.has("policy")) {
					policyJson = rootJson.getJSONObject("policy");
				} else {
					policyJson = new JSONObject();
					rootJson.put("policy", policyJson);
				}
				policyJson.put(key, value);
			} else if (ALERT_KEYS.contains(key)) {
				
				JSONObject alertJson = null;
				JSONObject apsJson = null;
				JSONObject payloadJson = null;
				if (rootJson.has("payload")) {
					payloadJson = rootJson.getJSONObject("payload");
				} else {
					payloadJson = new JSONObject();
					rootJson.put("payload", payloadJson);
				}
				if (payloadJson.has("aps")) {
					apsJson = payloadJson.getJSONObject("aps");
				} else {
					apsJson = new JSONObject();
					payloadJson.put("aps", apsJson);
				}
				if (apsJson.has("alert")) {
					alertJson = apsJson.getJSONObject("alert");
				} else {
					alertJson = new JSONObject();
					apsJson.put("alert", alertJson);
				}
				alertJson.put(key, value);
			} else {
				if (key == "payload" || key == "aps" || key == "policy") {
					throw new Exception("You don't need to set value for " + key + " , just set values for the sub keys in it.");
				} else {
					throw new Exception("Unknownd key: " + key);
				}
			}
			
			return true;
		}
		
		/**
		 * 添加参数
		 * 
		 * @param key 参数名称
		 * @param value 参数值
		 * 
		 * @author guoshijie
		 * @createdate 2017.12.8
		 * */
		public boolean setCustomizedField(String key, String value) throws Exception {
		
			JSONObject payloadJson = null;
			if (rootJson.has("payload")) {
				payloadJson = rootJson.getJSONObject("payload");
			} else {
				payloadJson = new JSONObject();
				rootJson.put("payload", payloadJson);
			}
			payloadJson.put(key, value);
			return true;
		}

		public void setAlert(String token) throws Exception {
	    	setPredefinedKeyValue("alert", token);
	    }
		
		public void setBadge(Integer badge) throws Exception {
	    	setPredefinedKeyValue("badge", badge);
	    }
		
		public void setSound(String sound) throws Exception {
	    	setPredefinedKeyValue("sound", sound);
	    }
		
		public void setContentAvailable(Integer contentAvailable) throws Exception {
	    	setPredefinedKeyValue("content-available", contentAvailable);
	    }
		
		public void setBody(String body) throws Exception {
			setPredefinedKeyValue("body",body);
		}
		
		public void setSubtitle(String subtitle) throws Exception {
			setPredefinedKeyValue("subtitle",subtitle);
		}
		
		public void setTitle(String title) throws Exception {
			setPredefinedKeyValue("title",title);
		}
}
