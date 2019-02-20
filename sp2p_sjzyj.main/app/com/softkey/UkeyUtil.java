package com.softkey;

import java.util.Random;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.common.UkeyDao;
import models.common.entity.t_ukey;
import play.Logger;
import services.common.UkeyService;

public class UkeyUtil {

	static UkeyDao ukeyDao = Factory.getDao(UkeyDao.class);
	
	/**
	 * 
	 * @return
	 */
	public static ResultInfo checkKey(String randomNum, String outString, String ckeyId) {
		ResultInfo result = new ResultInfo();

		t_ukey ukey = ukeyDao.geUkeyByUkeyId(ckeyId);
		if (ukey == null || ukey.ukey_ckey == null || ukey.ukey_ckey.equals("")) {
			result.code = -1;
			result.msg = "加密狗密钥未设置";

			return result;
		}
		
		if (ukey.ukey_stat == 2) {
			result.code = -1;
			result.msg = "加密狗已挂失";

			return result;
		}
		
		
		// 本地加密，判断
		SoftKey mysoftkey = new SoftKey();
		String outString2 = mysoftkey.StrEnc(randomNum, ukey.ukey_ckey);

		if (outString == null || outString.equals("") || outString2 == null || outString2.equals("")
				|| !outString.equals(outString2)) {
			result.code = -1;
			result.msg = "加密狗验证失败";

			return result;
		}

		result.code = 1;
		result.msg = "加密狗验证成功";
		return result;
	}

	/**
	 * 写密码设置
	 * 
	 * @author niu
	 * @date 2018-03-16
	 */
	public static ResultInfo writePassSet() {
		ResultInfo result = new ResultInfo();

		jsyunew3 j9 = new jsyunew3();

		String DevicePath;
		// 这个用于判断系统中是否存在着加密锁。不需要是指定的加密锁,
		DevicePath = j9.FindPort(0);
		if (j9.get_LastError() != 0) {
			result.code = -1;
			result.msg = "未找到加密锁,请插入加密锁。";

			return result;
		}

		// 设置写加密锁储存器空间的写密码
		int ret = j9.SetWritePassword("FFFFFFFF", "FFFFFFFF", ConfConst.WRITE_H_KEY, ConfConst.WRITE_L_KEY, DevicePath);
		if (ret != 0) {
			result.code = -1;
			result.msg = "写密码设置失败";

			return result;
		}

		result.code = 1;
		result.msg = "写密码设置成功";

		return result;
	}

	/**
	 * 读密码设置
	 * 
	 * @author niu
	 * @date 2018-03-16
	 */
	public static ResultInfo readPassSet() {
		ResultInfo result = new ResultInfo();

		jsyunew3 j9 = new jsyunew3();

		String DevicePath;
		// 这个用于判断系统中是否存在着加密锁。不需要是指定的加密锁,
		DevicePath = j9.FindPort(0);
		if (j9.get_LastError() != 0) {
			result.code = -1;
			result.msg = "未找到加密锁,请插入加密锁。";

			return result;
		}

		// 设置读加密锁储存器空间的读密码
		int ret = j9.SetReadPassword(ConfConst.WRITE_H_KEY, ConfConst.WRITE_L_KEY, ConfConst.READ_H_KEY, ConfConst.READ_L_KEY, DevicePath);
		if (ret != 0) {
			result.code = -1;
			result.msg = "读密码设置失败";

			return result;
		}

		result.code = 1;
		result.msg = "读密码设置成功";

		return result;
	}

	/**
	 * 增强算法密钥设置
	 * 
	 * @author niu
	 * @date 2018-03-16
	 */
	public static ResultInfo secretSet() {
		ResultInfo result = new ResultInfo();

		jsyunew3 j9 = new jsyunew3();

		String DevicePath;
		// 这个用于判断系统中是否存在着加密锁。不需要是指定的加密锁,
		DevicePath = j9.FindPort(0);
		if (j9.get_LastError() != 0) {
			result.code = -1;
			result.msg = "未找到加密锁,请插入加密锁。";

			return result;
		}

		// 设置增强算法一密钥
		// 注意：密钥为不超过32个的0-F字符，例如：1234567890ABCDEF1234567890ABCDEF,不足32个字符的，系统会自动在后面补0
		int ret;
		ret = j9.SetCal_2(ConfConst.CKEY, DevicePath);
		if (ret != 0) {
			result.code = -1;
			result.msg = "增强算法密钥设置失败";

			return result;
		}

		result.code = 1;
		result.msg = "增强算法密钥设置成功";

		return result;
	}

	/**
	 * 后台用户名设置
	 * 
	 * @param userName
	 * @return
	 * 
	 * @author niu
	 * @date 2018-03-16
	 */
	public static ResultInfo userNameSet(String userName) {
		ResultInfo result = new ResultInfo();

		jsyunew3 j9 = new jsyunew3();

		String DevicePath;
		// 这个用于判断系统中是否存在着加密锁。不需要是指定的加密锁,
		DevicePath = j9.FindPort(0);
		if (j9.get_LastError() != 0) {
			result.code = -1;
			result.msg = "未找到加密锁,请插入加密锁。";

			return result;
		}

		// 注意，如果是普通单片机芯片，储存器的写次数是有限制的，写次数为1000次，读不限制，如果是智能芯片，写的次数为10万次
		// 这个例子与上面的不同之处是，可以写入非固定长度的字符串，它是先将字符串的长度写入到首地址，然后再写入相应的字符串
		// 写入字符串带长度，,使用默认的读密码
		int ret;
		int nlen;
		String InString;
		byte[] buf = new byte[1];
		InString = userName;

		// 写入字符串到地址1
		nlen = j9.NewWriteString(InString, (short) 1, ConfConst.WRITE_H_KEY, ConfConst.WRITE_L_KEY, DevicePath);
		if (j9.get_LastError() != 0) {
			result.code = -1;
			result.msg = "写入字符串(带长度)错误。";

			return result;
		}
		// 写入字符串的长度到地址0
		buf[0] = (byte) nlen;
		j9.SetBuf(0, buf[0]);
		ret = j9.YWriteEx((short) 0, (short) 1, ConfConst.WRITE_H_KEY, ConfConst.WRITE_L_KEY, DevicePath);
		if (ret != 0) {
			result.code = -1;
			result.msg = "写入字符串长度错误。错误码：";

			return result;
		}

		result.code = 1;
		result.msg = "写入用户名成功";

		return result;
	}

}
