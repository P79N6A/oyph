package push;

/**广播(broadcast): 向安装该App的所有设备发送消息。*/
public class AndroidBroadcast extends AndroidNotification {
	/**
	 * 向全部安卓客户端发送消息
	 * 
	 * @param appkey 应用唯一标识(友盟+提供)
	 * @param appMasterSecret 服务器秘钥，用于服务器端调用API请求时对发送内容做签名验证(友盟+提供)
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.8
	 * */
	public AndroidBroadcast(String appkey,String appMasterSecret) throws Exception {
		setAppMasterSecret(appMasterSecret);
		setPredefinedKeyValue("appkey", appkey);
		this.setPredefinedKeyValue("type", "broadcast");	
	}

}
