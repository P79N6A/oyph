package controllers.front.seal;

import java.io.IOException;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.utils.HttpClientSender;
import common.utils.RSAUtils;
import common.utils.ResultInfo;
import play.Logger;

/**
 * 上上签混合云SDK客户端
 */
public class BestsignOpenApiClient {

	private String developerId;

	private String privateKey;

	private String serverHost;
	
	private static String urlSignParams = "?developerId=%s&rtick=%s&signType=rsa&sign=%s";

	public BestsignOpenApiClient(String developerId, String privateKey,
			String serverHost) {
		this.developerId = developerId;
		this.privateKey = privateKey;
		this.serverHost = serverHost;
	}

	/**
	 * 个人用户注册
	 * 
	 * @param account   用户账号
	 * @param name      姓名
	 * @param mail      用来接收通知邮件的电子邮箱
	 * @param mobile    用来接收通知短信的手机号码
	 * @param identity  证件号码
	 * @param identityType   枚举值：0-身份证，目前仅支持身份证
	 * @param contactMail    电子邮箱
	 * @param contactMobile  手机号码
	 * @param province       省份
	 * @param city			   城市
	 * @param address        地址
	 * @return 异步申请任务单号
	 * @throws IOException
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日15:19:46
	 */
	public String userPersonalReg(String account, String name, String mail,
			String mobile, String identity, String identityType,
			String contactMail, String contactMobile, String province,
			String city, String address) throws Exception {
		String host = this.serverHost;
		String method = "/user/reg/";

		// 组装请求参数，作为requestbody
		JSONObject requestBody = new JSONObject();
		requestBody.put("account", account);
		requestBody.put("name", name);
		requestBody.put("userType", "1");
		requestBody.put("mail", mail);
		requestBody.put("mobile", mobile);

		JSONObject credential = new JSONObject();
		credential.put("identity", identity);
		credential.put("identityType", identityType);
		credential.put("contactMail", contactMail);
		credential.put("contactMobile", contactMobile);
		credential.put("province", province);
		credential.put("city", city);
		credential.put("address", address);
		requestBody.put("credential", credential);
		requestBody.put("applyCert", "1");

		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		String responseBody = HttpClientSender.sendHttpPost(host, method,
				urlParams, requestBody.toJSONString());
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		if (userObj.getIntValue("errno") == 0) {
			JSONObject data = userObj.getJSONObject("data");
			if (data != null) {
				return data.getString("taskId");
			}
			
			return null;
		} else {
			
			Logger.info(userObj.getIntValue("errno") + ":"
					+ userObj.getString("errmsg"));
			return null;
		}
	}
	
	/**
	 * 企业用户注册
	 * 
	 * @param account		用户账号
	 * @param name      	企业名称
	 * @param mail     		用来接收通知邮件的电子邮箱
	 * @param mobile   		用来接收通知短信的手机号码
	 * @param credit       	企业统一信用代码
	 * @param realityName		法人姓名		
	 * @param idNumber			法人身份证
	 * @param legalPersonIdentityType		法人证件类型
	 * @param province       省份
	 * @param city			   城市
	 * @param address        地址
	 * @return 异步申请任务单号
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日15:22:57
	 */
	public String userEnterpriseReg(String account, String name, String mail, String mobile, String credit,
			String realityName, String idNumber, String legalPersonIdentityType, String province,
			String city, String address) throws Exception {
		String host = this.serverHost;
		String method = "/user/reg/";

		// 组装请求参数，作为requestbody
		JSONObject requestBody = new JSONObject();
		requestBody.put("account", account);
		requestBody.put("name", name);
		requestBody.put("userType", "2");
		requestBody.put("mail", mail);
		requestBody.put("mobile", mobile);

		JSONObject credential = new JSONObject();
		credential.put("regCode", credit);
		credential.put("orgCode", credit);
		credential.put("taxCode", credit);
		credential.put("legalPerson", realityName);
		credential.put("legalPersonIdentity", idNumber);
		credential.put("legalPersonIdentityType", legalPersonIdentityType);
		credential.put("legalPersonMobile", mobile);
		credential.put("contactMobile", mobile);
		credential.put("province", province);
		credential.put("city", city);
		credential.put("address", address);
		requestBody.put("credential", credential);
		requestBody.put("applyCert", "1");
		
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		String responseBody = HttpClientSender.sendHttpPost(host, method,
				urlParams, requestBody.toJSONString());
		
		
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		if (userObj.getIntValue("errno") == 0) {
			JSONObject data = userObj.getJSONObject("data");
			if (data != null) {
				return data.getString("taskId");
			}
			
			return null;
		} else {
			
			Logger.info(userObj.getIntValue("errno") + ":"
					+ userObj.getString("errmsg"));
			
			return null;
		}
	}
	
	
	public String userCredentialStatus(String account, String task) {
		String host = this.serverHost;
		String method = "/user/async/applyCert/status/";

		// 组装请求参数，作为requestbody
		JSONObject requestBody = new JSONObject();
		requestBody.put("account", account);
		requestBody.put("taskId", task);
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		String responseBody = null;
		try {
			responseBody = HttpClientSender.sendHttpPost(host, method,
					urlParams, requestBody.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		if (userObj.getIntValue("errno") == 0) {
			JSONObject data = userObj.getJSONObject("data");
			if (data != null) {
				return data.getString("status");
			}
			
			return null;
		} else {
		
			
			return null;
		}
	}
	
	
	/**
     * 上传并创建合同
     * 
     * @author LiuPengwei
     * @createDate 2018年3月9日 10:00:59
     */
	public String createContract(String account, String fmd5, String ftype,
			String fname, String fpages, String fdata, String title, String expireTime,
			String description) {
		
		String host = this.serverHost;
		String method = "/storage/contract/upload/";
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("account", account);
		requestBody.put("fmd5", fmd5);
		requestBody.put("ftype", ftype);
		requestBody.put("fname", fname);
		requestBody.put("fpages", fpages);
		requestBody.put("fdata", fdata);
		requestBody.put("title", title);
		requestBody.put("expireTime", expireTime);
		requestBody.put("description", description);
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		
		String responseBody = null;
		try {
			responseBody = HttpClientSender.sendHttpPost(host, method,
						urlParams, requestBody.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		if (userObj.getIntValue("errno") == 0) {
			JSONObject data = userObj.getJSONObject("data");
			if (data != null) {
				return data.getString("contractId");
			}
			return null;
			
		} else {
			return null;
		}
	}
	
	/**
	 * 合同签署
	 * 
	 * @param contractId			合同编号
	 * @param signer				签署人
	 * @param x   					x坐标
	 * @param y						y坐标
	 * @param pageNum				页码
	 * @param signatureImageName	签名图片名称
	 * @param signatureImageData	签名图片
	 * @return
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月15日 16:27:12
	 */
	public int signContract(String contractId, String signer, String x, String y, String pageNum,
			String signatureImageName, String signatureImageData) {
		String host = this.serverHost;
		String method = "/storage/contract/sign/cert/";
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("contractId" , contractId);
		requestBody.put("signer", signer);
		JSONArray signaturePositions = new JSONArray();
		TreeMap<Object, Object>  a = new TreeMap<>();
		a.put("x", x);
		a.put("y", y);
		a.put("pageNum",pageNum);
		
		signaturePositions.add(a) ; 
		
		requestBody.put("signaturePositions" , signaturePositions);
		requestBody.put("signatureImageName" , signatureImageName);
		requestBody.put("signatureImageData" , signatureImageData);
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		
		
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		String responseBody = null;
		try {
			responseBody = HttpClientSender.sendHttpPost(host, method,
					urlParams, requestBody.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		
		return userObj.getIntValue("errno") ;
	} 
	
	/**
	 * 锁定并结束合同
	 * 
	 * @param contractId
	 * @return
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月15日 17:52:19
	 */
	
	public int endSignContract(String contractId){
		String host = this.serverHost;
		String method = "/storage/contract/lock/";
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("contractId" , contractId);
		
	
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		
		String responseBody = null;
		try {
			responseBody = HttpClientSender.sendHttpPost(host, method,
						urlParams, requestBody.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		// 返回errno为0，表示成功，其他表示失败
		
		return userObj.getIntValue("errno");
					
	}
	
	public String previewContract (String contractId, String account){
		String host = this.serverHost;
		String method = "/contract/getPreviewURL/";
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("contractId" , contractId);
		requestBody.put("account", account);
		requestBody.put("expireTime" , "0");
		requestBody.put("dpi", 160+"");
		requestBody.put("expireTime", "");
	
		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, null,
				requestBody.toJSONString());
		// 签名参数追加为url参数
		String urlParams = String.format(urlSignParams, this.developerId,
				rtick, paramsSign);
		// 发送请求
		
		String responseBody = null;
		try {
			responseBody = HttpClientSender.sendHttpPost(host, method,
						urlParams, requestBody.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 返回结果解析
		JSONObject userObj = JSON.parseObject(responseBody);
		Logger.info(account+"+"+userObj.toString());
		// 返回errno为0，表示成功，其他表示失败
		if (userObj.getIntValue("errno") == 0) {
			JSONObject data = userObj.getJSONObject("data");
			if (data != null) {
				return data.getString("url");
			}
			return null;
			
		} else {
			return null ;
		}
	}
	
	
	/**
	 * GET方法示例
	 * 下载合同PDF文件
	 * 
	 * @param contractId
	 *            合同编号
	 * @return
	 * @throws Exception
	 */
	public byte[] contractDownload(String contractId) throws Exception {
		String host = this.serverHost;
		String method = "/storage/contract/download/";

		// 组装url参数
		String urlParams = "contractId=" + contractId;

		// 生成一个时间戳参数
		String rtick = RSAUtils.getRtick();
		// 计算参数签名
		String paramsSign = RSAUtils.calcRsaSign(this.developerId,
				this.privateKey, host, method, rtick, urlParams, null);
		// 签名参数追加为url参数
		urlParams = String.format(urlSignParams, this.developerId, rtick,
				paramsSign) + "&" + urlParams;
		// 发送请求
		byte[] responseBody = HttpClientSender.sendHttpGet(host, method,
				urlParams);
		// 返回结果解析
		return responseBody;
	} 
}
