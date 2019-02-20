package common.utils;

import com.shove.Convert;

import common.constants.SettingKey;
import play.Logger;
import play.db.jpa.JPA;
import services.common.SettingService;

/**
 * 日志打印工具
 *
 * @description 本系统所有日志输出和业务回滚只能使用本工具类。
 * <br>禁止直接使用play自带的日志输出方法。
 * <br>禁止直接使用JPA.setRollbackOnly()方法。
 * <br>“位置”指的是调用日志方法的位置。而非异常抛出的位置，具体异常详见堆栈信息。
 *
 * @author huangyunsong
 * @createDate 2015年12月16日
 */
public class LoggerUtil {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * error错误级别日志
	 *
	 * @param e 异常对象
	 * @param desc 异常描述
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月16日
	 */
	public static void error(boolean isRollback, Exception e, String desc, Object... args) {
		
		Throwable throwException = new Throwable();
		StringBuffer info = new StringBuffer();
		
		info.append("\n描述：").append(desc);
		
		info.append("。\n位置：（类名：")
			.append(throwException.getStackTrace()[1].getClassName())
			.append("；方法名：")
			.append(throwException.getStackTrace()[1].getMethodName())
			.append("；行号：")  
			.append(throwException.getStackTrace()[1].getLineNumber())
			.append("）");
		
		info.append("\n是否执行回滚操作：").append(isRollback);
		
		Logger.error(e, info.toString(), args);
		
		if (isRollback) {
			JPA.setRollbackOnly();
		}
		
	}
	
	/**
	 * info错误级别日志
	 *
	 * @param e 异常对象
	 * @param desc 异常描述
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月16日
	 */
	public static void info(boolean isRollback, Exception e, String desc, Object... args) {
		
		Throwable throwException = new Throwable();
		StringBuffer info = new StringBuffer();
		
		info.append("\n描述：").append(desc);
		
		info.append("。\n位置：（类名：")
			.append(throwException.getStackTrace()[1].getClassName())
			.append("；方法名：")
			.append(throwException.getStackTrace()[1].getMethodName())
			.append("；行号：")
			.append(throwException.getStackTrace()[1].getLineNumber())
			.append("）");
		
		info.append("\n是否执行回滚操作：").append(isRollback);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(e, info.toString(), args);
		}
		
		if (isRollback) {
			JPA.setRollbackOnly();
		}
		
	}
	
	/**
	 * 提示信息日志
	 *
	 * @param desc 提示信息
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月16日
	 */
	public static void info(boolean isRollback, String desc, Object... args) {
		
		Throwable e = new Throwable();
		
		StringBuffer info = new StringBuffer();
		
		info.append("\n描述：").append(desc);
		
		info.append("。\n位置：（类名：")
			.append(e.getStackTrace()[1].getClassName())
			.append("；方法名：")
			.append(e.getStackTrace()[1].getMethodName())
			.append("；行号：")
			.append(e.getStackTrace()[1].getLineNumber())
			.append("）");
		
		info.append("\n是否执行回滚操作：").append(isRollback);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString(), args);
		}
		
		if (isRollback) {
			JPA.setRollbackOnly();
		}
	}
	
	/**
	 * 提示信息日志
	 *
	 * @param desc 提示信息
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月16日
	 */
	public static void error(boolean isRollback, String desc, Object... args) {
		
		Throwable e = new Throwable();
		
		StringBuffer info = new StringBuffer();
		
		info.append("\n描述：").append(desc);
		
		info.append("。\n位置：（类名：")
		.append(e.getStackTrace()[1].getClassName())
		.append("；方法名：")
		.append(e.getStackTrace()[1].getMethodName())
		.append("；行号：")
		.append(e.getStackTrace()[1].getLineNumber())
		.append("）");
		
		info.append("\n是否执行回滚操作：").append(isRollback);
		
		Logger.error(info.toString(), args);
		
		if (isRollback) {
			JPA.setRollbackOnly();
		}
	}
}
