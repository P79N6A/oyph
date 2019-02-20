package controllers.app.onlineVisitor;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.h2.security.AES;

import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.OnlinePubKeyUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;
import play.Logger;
import service.AccountAppService;

public class OnlineVisitorAction extends BackBaseController{
	protected static AccountAppService accountAppService = Factory.getService(AccountAppService.class);

	/**
	 * 访客端聊天窗数据埋点
	 * @createDate 2018.7.12
	 * */
	public static String visitorChatDataPoint (Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();
		
		//还原出RSA公钥对象
		PublicKey publicKey = null;
		try {
			publicKey = OnlinePubKeyUtil.getPubKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String signId = parameters.get("userId");
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong((String)result.obj);
		t_user_info info = t_user_info.findById(userId);
		String userInfo = accountAppService.findUserInfomation(userId);
		JSONObject uInfo = JSONObject.fromObject(userInfo);
		JSONObject cinfo = new JSONObject();
		JSONObject extInfo = new JSONObject();
		
		//extInfo.put("uId", userId+"");
		extInfo.put("userName", info.reality_name);//真实姓名
		extInfo.put("customerName", uInfo.get("name"));//昵称
		extInfo.put("totalIncome", uInfo.get("totalIncome"));//总收益
		extInfo.put("totalAssets",uInfo.get("totalAssets"));//总资产
		extInfo.put("balance",uInfo.get("balance"));//可用余额
		extInfo.put("grade", uInfo.get("grade"));//风险评估等级
		cinfo.put("extInfo", extInfo);
		cinfo.put("userId", userId+"");
		cinfo.put("timestamp", new Date().getTime());
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(cinfo.toString());
		}
		
		Map<String,String> map = CustomerInfoCryptoUtil.encryptByPublicKey(cinfo.toString(), publicKey);
		String mainUrl = "https://cschat-ccs.aliyun.com/h5portal.htm?tntInstId=_0rhgRUW&scene=SCE00002186";
		String url = "&cinfo="+map.get("text")+"&key="+map.get("key");
		json.put("code", 1);
		json.put("msg", "获取在线访客名片埋点成功");
		json.put("url", mainUrl + url);
		return json.toString();
	}
	
	
}
