package common.constants;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import common.utils.LoggerUtil;
import play.Logger;
import play.Play;

/**
 * 阿里云常量
 * 
 * @author liu
 * @create 2018.02.27
 */
public class AliyunConsts {

	/** 阿里云常见配置属性 */
	private static Properties alyConf = null;
	
	/** 阿里云配置文件 */
	private static final String path = Play.getFile("conf" + File.separator + "config.properties").getAbsolutePath();
	
	/**
	 * 加载配置文件，并初始化相关信息
	 */
	static {
		Logger.info("阿里云配置文件路径：%s", path);
		
		if (alyConf == null) {
			loadProperties();
		}
		
	}
	
	/**
	 * 加载阿里云配置文件
	 */
	private static void loadProperties() {
		
		Logger.info("读取config.properties配置文件");
		alyConf = new Properties();
		
		try {
			alyConf.load(new FileInputStream(new File(path)));
		} catch (Exception e) {
			LoggerUtil.error(false, e, "读取config.properties配置文件异常");
		}
	}
	
	/**
	 * 访问域名
	 */
	public static final String ENDPOINT = alyConf.getProperty("endpoint");
	
	/**
	 * 储存空间名称
	 */
	public static final String BUCKETNAME = alyConf.getProperty("bucketName");
	
	/**
	 * 上传文件的保存路径
	 */
	public static final String PICLOCATION = alyConf.getProperty("picLocation");
	
	/**
	 * 访问密钥的ID
	 */
	public static final String ACCESSKEYID = alyConf.getProperty("accessKeyId");
	
	/**
	 * 访问密钥的key值
	 */
	public static final String ACCESSKEYSECRET = alyConf.getProperty("accessKeySecret");
	
}
