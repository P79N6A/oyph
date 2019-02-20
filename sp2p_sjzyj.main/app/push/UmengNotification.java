package push;

import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONObject;


/**友盟+ 消息推送*/
public abstract class UmengNotification {

	protected final JSONObject rootJson = new JSONObject();

	protected String appMasterSecret;

	/**ROOT_KEYS
	 * 
	 * @param appkey 应用唯一标识
	 * @param timestamp 时间戳，10位或者13位均可，时间戳有效期为10分钟
	 * @param type 发送对象类型
	 * @param device_tokens 设备唯一表示
	 * @param alias 开发者自有账号
	 * @param alias_type 开发者自有账号类型
	 * @param file_id 
	 * @param filter 过滤条件
	 * @param production_mode 模式（正式/测试）
	 * @param feedback 响应结果
	 * @param description 描述
	 * @param thirdparty_id 第三方编号
	 * */
	protected static final HashSet<String> ROOT_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"appkey", "timestamp", "type", "device_tokens", "alias", "alias_type", "file_id", 
			"filter", "production_mode", "feedback", "description", "thirdparty_id", "mipush", "mi_activity"}));

	/**发送策略*/
	/**POLICY_KEYS
	 * 
	 * @param start_time 定时发送时间，默认为立即发送。发送时间不能小于当前时间
	 * @param expire_time 消息过期时间,其值不可小于发送时间
	 * @param max_send_num 发送限速，每秒发送的最大条数
	 * */
	protected static final HashSet<String> POLICY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"start_time", "expire_time", "max_send_num"
	}));

	/**
	 * 添加参数
	 * 
	 * @param key 参数名称
	 * @param value 参数值
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public abstract boolean setPredefinedKeyValue(String key, Object value) throws Exception;
	public void setAppMasterSecret(String secret) {
		appMasterSecret = secret;
	}
		
	public String getPostBody(){
		return rootJson.toString();
	}
		
	protected final String getAppMasterSecret(){
		return appMasterSecret;
	}
		
	protected void setProductionMode(Boolean prod) throws Exception {
	   	setPredefinedKeyValue("production_mode", prod.toString());
    }

	/**
	 * 发送模式设置--正式模式
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setProductionMode() throws Exception {
		setProductionMode(true);
	}

	/**
	 * 发送模式设置--测试模式
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setTestMode() throws Exception {
	    setProductionMode(false);
	}

	/**
	 * 添加发送消息描述
	 * 
	 * @param description 消息描述(建议填写)
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setDescription(String description) throws Exception {
	    setPredefinedKeyValue("description", description);
	}

	/**
	 * 添加定时发送时间  格式: "YYYY-MM-DD hh:mm:ss"
	 * 
	 * @param startTime 定时发送时间，若不填写表示立即发送
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setStartTime(String startTime) throws Exception {
	    setPredefinedKeyValue("start_time", startTime);
	}
	
	/**
	 * 添加消息过期时间  格式: "YYYY-MM-DD hh:mm:ss"
	 * 
	 * @param expireTime 消息过期时间
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setExpireTime(String expireTime) throws Exception {
		setPredefinedKeyValue("expire_time", expireTime);
	}
	
	/**
	 * 添加发送限速
	 * 
	 * @param num 发送限速 ,每秒发送的最大条数
	 * @author guoshijie
	 * @createdate 2017.12.7
	 * */
	public void setMaxSendNum(Integer num) throws Exception {
		setPredefinedKeyValue("max_send_num", num);
	}
	
}
