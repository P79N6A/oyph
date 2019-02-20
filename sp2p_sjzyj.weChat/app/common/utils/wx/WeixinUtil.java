package common.utils.wx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;



import common.utils.Constant;
import common.utils.JacksonUtil;
import common.utils.ResultInfo;
import common.utils.net.HttpUtil;
import models.AccessToken;
import models.entity.t_wechat_access_token;
import models.entity.t_wechat_template;
import models.entity.t_wechat_template_data;
import models.menu.Menu;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 微信工具类
 * @author 51452
 *
 */
public class WeixinUtil {
	
	private static Logger log =  Logger.getLogger("WeixinUtil");
	
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET"; // 获取access_token的接口地址（GET） 限200（次/天）
	
	public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";// 菜单创建（POST） 限100（次/天）   
	  
	public static String add_kf_url = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN"; //添加客服账号
	
	public static String update_kf_url = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=ACCESS_TOKEN"; //修改客服账号
	
	public static String del_kf_url = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT"; //删除客服账号
	
	public static String get_all_kf_url = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN"; //获取所有客服账户
	
	public static String get_online_kf_url = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=ACCESS_TOKEN"; //获取所有的在线客服账户
	
	public static String inviteworker_url = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=ACCESS_TOKEN";//邀请绑定客服帐号
	
	//https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN
	/** 
	 * 获取t_wechat_access_token 
	 *  
	 * @param appid 凭证 
	 * @param appsecret 密钥 
	 * @return 
	 */  
	public static t_wechat_access_token getAccessToken(String appid, String appsecret) {  
		t_wechat_access_token accessToken = null;  
	  
	    String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);  
	    JSONObject jsonObject = HttpUtil.httpsRequest(requestUrl, "GET", null);  
	    
	    // 如果请求成功  
	    if (null != jsonObject) {  
	        try {  
	            accessToken = new t_wechat_access_token();  
	            accessToken.token = jsonObject.getString("access_token");  
	            accessToken.expire_in = jsonObject.getInt("expires_in");  
	            accessToken.time = new Date();
	        } catch (JSONException e) {  
	            accessToken = null;  
	            // 获取token失败  
	            log.error("获取token失败 ");
	            log.error("errcode:"+jsonObject.getInt("errcode"));
	            log.error("errmsg:"+jsonObject.getString("errmsg"));
	        }  
	    }  
	    return accessToken;  
	} 
	
	
	/** 
	 * 创建菜单 
	 *  
	 * @param menu 菜单实例 
	 * @param accessToken 有效的access_token 
	 * @return 0表示成功，其他值表示失败 
	 */  
	public static int createMenu(Menu menu, String accessToken) {
		
	    int result = 0;  
	  
	    // 拼装创建菜单的url  
	    String url = menu_create_url.replace("ACCESS_TOKEN", accessToken);  
	    // 将菜单对象转换成json字符串  
	    String jsonMenu = JSONObject.fromObject(menu).toString();  
	    // 调用接口创建菜单  
	    JSONObject jsonObject = HttpUtil.httpsRequest(url, "POST", jsonMenu);  
	  
	    if (null != jsonObject) {  
	        if (0 != jsonObject.getInt("errcode")) {  
	            result = jsonObject.getInt("errcode");  
	            log.error("创建菜单失败");
	            log.error("errcode:"+jsonObject.getInt("errcode"));
	            log.error("errmsg:"+jsonObject.getString("errmsg"));
	        }  
	    }
	  
	    return result;  
	}
	
	/**
	 * 添加客服账号
	 * @return
	 * @param requestJson 提交数据
	 * @param accessToken 有效的accessToken
	 * @author guoShiJie
	 * */
	public static ResultInfo addKf(String requestJson,String accessToken) {
		ResultInfo result = new ResultInfo();
		result.code = -1;
		String url = add_kf_url.replace("ACCESS_TOKEN", accessToken);
		log.error("------------------添加客服账号,开始------------------");
		JSONObject jsonObject = HttpUtil.httpsRequest(url, "POST", requestJson);
		if (null != jsonObject) {
			result.code = jsonObject.getInt("errcode");
			result.msg = jsonObject.getString("errmsg");
			log.error("errcode:"+jsonObject.getInt("errcode"));
			log.error("errmsg:"+jsonObject.getString("errmsg"));
		}
		log.error("------------------添加客服账号,结束------------------");
		return result;
	}

	/**
	 * 设置客服信息
	 * @param requestJson 提交数据
	 * @param accessToken 有效的accessToken
	 * @author guoShiJie
	 * */
	public static ResultInfo updateKf(String requestJson,String accessToken) {
		ResultInfo result = new ResultInfo();
		result.code = -1;
		String url = update_kf_url.replace("ACCESS_TOKEN", accessToken);
		log.error("-----------------设置客服信息,开始-----------------------");
		JSONObject jsonObject = HttpUtil.httpsRequest(url, "POST", requestJson);
		if (null != jsonObject) {
			result.code = jsonObject.getInt("errcode");
			result.msg = jsonObject.getString("errmsg");
			log.error("errcode:"+jsonObject.getInt("errcode"));
			log.error("errmsg:"+jsonObject.getString("errmsg"));
		}
		log.error("-----------------设置客服信息,结束-----------------------");
		return result;
	}
	
	/**
	 * 邀请绑定客服帐号
	 * @param requestJson 提交数据
	 * @param accessToken 有效的accessToken
	 * @author guoShiJie
	 * */
	public static ResultInfo inviteworker(String requestJson,String accessToken) {
		ResultInfo result = new ResultInfo();
		result.code = -1;
		String url = inviteworker_url.replace("ACCESS_TOKEN", accessToken);
		log.error("-------------------------邀请绑定客服账号,开始---------------------");
		JSONObject jsonObject = HttpUtil.httpsRequest(url, "POST", requestJson);
		if (null != jsonObject) {
			result.code = jsonObject.getInt("errcode");
			result.msg = jsonObject.getString("errmsg");
			log.error("errcode:"+jsonObject.getInt("errcode"));
			log.error("errmsg:"+jsonObject.getString("errmsg"));
		}
		log.error("-------------------------邀请绑定客服账号,结束---------------------");
		return result;
	}
	
	/**
	 * 删除客服帐号
	 * @param kf_account 完整客服账号
	 * @param accessToken 有效的accessToken
	 * */
	public static ResultInfo delKf(String kf_account,String accessToken) {
		ResultInfo result = new ResultInfo();
		result.code = -1;
		String url = del_kf_url.replace("ACCESS_TOKEN", accessToken).replace("KFACCOUNT", kf_account);
		log.error("------------------------------删除客服账号,开始-----------------------------");
		JSONObject jsonObject = HttpUtil.httpsRequest(url,"GET", null);
		if (null != jsonObject) {
			result.code = jsonObject.getInt("errcode");
			result.msg = jsonObject.getString("errmsg");
			log.error("errcode:"+jsonObject.getInt("errcode"));
			log.error("errmsg:"+jsonObject.getString("errmsg"));
		} 
		log.error("------------------------------删除客服账号,结束-----------------------------");
		return result;
	}
	
	/**
	 * 获取所有客服账户
	 * */
	public static JSONObject getkflist(String accessToken) {
		String url = get_all_kf_url.replace("ACCESS_TOKEN", accessToken);
		log.error("---------------------------获取所有客服账户,开始---------------------------");
		JSONObject jsonObject = HttpUtil.httpsRequest(url, "GET", null);
		if (null != jsonObject) {
			log.error(jsonObject.toString());
			log.error("---------------------------获取所有客服账户,结束---------------------------");
			return jsonObject;
		}
		log.error("---------------------------获取所有客服账户,结束---------------------------");
		return null;
	}
	
	
	
	
	/**
	 * 用户注册成功的模板消息
	 *//*
	public void registTemplate(UserExtend user, String openid, String appId, String secret){
	    // 获取基础支持的access_token
//	    String resultUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ appId +"&secret="+ secret;
//	    String json = HttpUtil.get(resultUrl);
//	    JSONObject jsonObject = JacksonUtil.toEntity(json, JSONObject.class);
//	    String access_token = jsonObject.get("access_token").toString();
		
		t_wechat_access_token access_token = WeixinUtil.getAccessToken(Constant.appid, Constant.appsecret);
	    // 发送模板消息
	    String resultUrl2 = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
	    System.out.println("access_token:"+access_token);
	    // 封装基础数据
	    t_wechat_template wechatTemplate = new t_wechat_template();
	    wechatTemplate.setTemplate_id("LTXAoB-2oLRPNs26hobQBINwTwy9QXh3G4TtpAruNM4");
	    wechatTemplate.setTouser(openid);
	    wechatTemplate.setUrl("http://tw.xxx.com/member/member.html?id="+user.getId());
	    Map<String,t_wechat_template_data> mapdata = new HashMap<>();
	    // 封装模板数据
	    t_wechat_template_data first = new t_wechat_template_data();
	    first.setValue("您好，您已注册成为XXX平台用户。");
	    first.setColor("#173177");
	    mapdata.put("first", first);
	 
	    t_wechat_template_data keyword1 = new t_wechat_template_data();
	    keyword1.setValue(user.getMobile());
	    first.setColor("#173177");
	    mapdata.put("keyword1", keyword1);
	 
	    t_wechat_template_data keyword2 = new t_wechat_template_data();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String format = formatter.format(new Date());
	    keyword2.setValue(format);
	    
	    first.setColor("#173177");
	    mapdata.put("keyword2", keyword2);
	 
	    t_wechat_template_data keyword3 = new t_wechat_template_data();
	    keyword3.setValue("欢迎您的加入，请及时购买会员并完善资料>>");
	    keyword3.setColor("#173177");
	    mapdata.put("remark", keyword3);
	 
	    wechatTemplate.setData(mapdata);
//	    String toString = JacksonUtil.toJson(wechatTemplate).toString();
	    String json2 = HttpUtil.post(resultUrl2,toString);
	    JSONObject jsonObject2 = JacksonUtil.toEntity(json2, JSONObject.class);
	    int result = 0;
	    if (null != jsonObject2) {
	        if (0 != jsonObject2.getInt("errcode")) {
	            result = jsonObject2.getInt("errcode");
	            System.out.println("错误 errcode:{} errmsg:{}"+ jsonObject2.getInt("errcode")+jsonObject2.get("errmsg").toString());
	        }
	    }
	    log.info("模板消息发送结果："+result);
	}
*/
	
}
