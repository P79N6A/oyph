package yb;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.groovy.util.ListHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import models.entity.t_payment_request;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.Logger;
import services.common.SettingService;
import yb.enums.ServiceType;
import yb.enums.TradeType;

/**
 * 宜宾工具类
 * 
 * @author niu
 * @create 2017.08.24
 */
public class YbUtils {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public static final int PRIVATE = 0;
	public static final int PUBLIC = 1;
	
	/**
	 * 得到公钥或者私钥
	 * 
	 * @author niu
	 * @create 2017.08.28
	 */
	private static Key getKeyFromFile(String filename, int type) throws FileNotFoundException {
	      FileInputStream fis = null;
	      fis = new FileInputStream(filename);
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      int b;
	      try {
	          while ((b = fis.read()) != -1) {
	              baos.write(b);
	          }
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      byte[] keydata = baos.toByteArray();

	      Key key = null;
	      try {
	          KeyFactory kf = KeyFactory.getInstance("RSA");
	          switch (type) {
	              case PRIVATE:
	                  PKCS8EncodedKeySpec encodedPrivateKey = new PKCS8EncodedKeySpec(keydata);
	                  key = kf.generatePrivate(encodedPrivateKey);
	                  return key;
	              case PUBLIC:
	                  X509EncodedKeySpec encodedPublicKey = new X509EncodedKeySpec(keydata);
	                  key = kf.generatePublic(encodedPublicKey);
	                  return key;
	          }
	      } catch (NoSuchAlgorithmException e) {
	          e.printStackTrace();
	      } catch (InvalidKeySpecException e) {
	          e.printStackTrace();
	      }

	      return key;
	  }
	
	/**
	 * 加签
	 * value 需加签字段，
	 * privateKeyName 秘钥文件名
	 * 返回：加签之后的串
	 */
	public static String addSign(String vale){
		
		String keypath = YbConsts.MER_PRI_KEY_PATH;
		//签名示例
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("签名开始...");
		}
        
        //从文件中读取私钥
        String signatureInBase64 = "";
		try {
			 PrivateKey privateKeyLoadedFromFile = (PrivateKey)getKeyFromFile(keypath,PRIVATE);
	        //初始化签名算法
	        Signature sign = Signature.getInstance("SHA256withRSA");
			//建议SHA256withRSA
	        //sign.initSign(privateKey);//指定签名所用私钥
			sign.initSign(privateKeyLoadedFromFile);
			//指定使用从文件中读取的私钥
	        byte[] data = vale.getBytes();//待签名明文数据
	        
	        if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	        	Logger.info("待签名的明文串: "+new String(data));
			}
	        
	        //更新用于签名的数据
			sign.update(data);
			
	        //签名
	        byte[] signature = sign.sign();
	        //将签名signature转为BASE64编码，用于HTTP传输
	        Base64 base64 = new Base64();
	        signatureInBase64 = base64.encodeToString(signature);
	        
	        if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	        	Logger.info("Base64格式编码的签名串: " + signatureInBase64);
	 	        Logger.info("签名结束...");
			}
	       
		}catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return signatureInBase64.replaceAll("\r|\n", "");
	}
	
	/**
	 * 验签
	 * value 待验签明文数据，
	 * signature 收到的签名（报文中的签名）
	 * publickeyName 公钥文件名
	 * 
	 */
	public static boolean varifySign(String value,String signature){
		String keypath = YbConsts.MER_PUB_KEY_PATH;
		boolean flag = false;
		//验签示例
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("验签开始...");
		}
        
        //从文件中读取公钥
		try {
			PublicKey publicKeyLoadedFromFile = (PublicKey)getKeyFromFile(keypath, PUBLIC);
        //将签名signature转为BASE64编码，用于HTTP传输
        Base64 base64 = new Base64();
        //从Base64还原得到签名
        byte[] signatureFromBase64 = base64.decodeBase64(signature);
        //初始化验签算法
        Signature verifySign = Signature.getInstance("SHA256withRSA");
//        verifySign.initVerify(publicKey);
        //指定验签所用公钥
        verifySign.initVerify(publicKeyLoadedFromFile);
        byte[] data = value.getBytes();//待签名明文数据
        //data为待验签的数据(明文)
        verifySign.update(data);
        
        if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
        	Logger.info("待验签的明文串："+new String(data)+" 签名（BASE64格式）："+signature);
		}
        
        //验签
        flag = verifySign.verify(signatureFromBase64);
        
        if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
        	Logger.info("验签是否通过:"+flag);//true为验签成功
            Logger.info("验签结束!...");
		}
        
		} catch (FileNotFoundException e) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签异常!..."+e);
			}
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签异常!..."+e);
			}
			
			e.printStackTrace();
		}catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签异常!..."+e);
			}
			
			e.printStackTrace();
		}catch (SignatureException e) {
			// TODO Auto-generated catch block
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签异常!..."+e);
			}
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 生成html表单
	 *
	 * @param maps
	 * @param action
	 * @return
	 *
	 * @author niu
	 * @createDate 2017.08.24
	 */
	public static String createFormHtml(Map<String,String> maps,String action){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html>")
			  .append("<html>")
			  .append("<head>")
			  .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
			  .append("<title>Servlet AccountServlet</title>")
			  .append("</head>")
			  .append("<body>")
			  .append("<form action="+action+" id=\"frm1\" method=\"get\">");		
		for(Entry<String, String> entry : maps.entrySet()){
			buffer.append("<input type=\"hidden\" name="+entry.getKey()+" value="+entry.getValue()+" />");
		}
		
		buffer.append("</form>")
			  .append("<script language=\"javascript\">")
			  .append("document.getElementById(\"frm1\").submit();")
			  .append("</script>")
			  .append("</body>")
			  .append("</html>");
		return buffer.toString();
	}
	
	/**
	 * 生成html表单
	 *
	 * @param maps
	 * @param action
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017.10.25
	 */
	public static String createFormHtmlPost(Map<String,String> maps,String action){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html>")
			  .append("<html>")
			  .append("<head>")
			  .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
			  .append("<title>Servlet AccountServlet</title>")
			  .append("</head>")
			  .append("<body>")
			  .append("<form action="+action+" id=\"frm1\" method=\"post\">");		
		for(Entry<String, String> entry : maps.entrySet()){
			buffer.append("<input type=\"hidden\" name="+entry.getKey()+" value="+entry.getValue()+" />");
		}
		
		buffer.append("</form>")
			  .append("<script language=\"javascript\">")
			  .append("document.getElementById(\"frm1\").submit();")
			  .append("</script>")
			  .append("</body>")
			  .append("</html>");
		return buffer.toString();
	}
	
	/**
	 * 获取指定格式的日期
	 */
	public static String getFormatDate(String format) {
		
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 获取指定格式的时间
	 */
	public static String getFormatTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
	
	/**
	 * 格式化金额,保留2位小数
	 */
	public static String formatAmount(double money){
		return String.format("%.2f", money);
		
	}
	
	/**
	 * json To map
	 * @param json
	 * @return
	 */
	public static Map<String,String> jsonToMap(String json){
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		return jsonToMap(jsonObj);
	}
	
	/**
	 * json To map
	 * @param json
	 * @return
	 */
	private static Map<String,String> jsonToMap(JsonObject json){
		Set<Entry<String, JsonElement>> set =json.entrySet();
		Map<String,String> maps = new HashMap<String, String>();
		for(Entry<String, JsonElement> entry : set){
			if ("\"\"".equals(entry.getValue().toString())) {
				maps.put(entry.getKey(), "");
			} else {
				maps.put(entry.getKey(), (entry.getValue() instanceof JsonNull | entry.getValue() == null)? "":((entry.getValue() instanceof JsonArray)?entry.getValue().getAsJsonArray().toString():entry.getValue().getAsString()));
			}
		}
		
		Iterator it = maps.keySet().iterator();
		while (it.hasNext()) {
			it.next().toString();
		}
		
		return maps;
	}
	
	
	/**
	 * 获得响应体
	 * 
	 * @author niu
	 * @create 2017.09.01
	 */
	public static TreeMap<String, String> getRespHeader(String respJsonString, ServiceType serviceType, String listName) {
		
		if (respJsonString == null || respJsonString.isEmpty()) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("获取响应体失败！");
			}
			
			return null;
		}
		
		JSONObject respMsg =  JSONObject.fromObject(respJsonString);
		
		if (respMsg == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应体转化JSON对象失败");
			}
			
			return null;
		}
		
		Object respHeader = respMsg.get("respHeader");
		
		
		if (respHeader == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应头获取失败");
			}
			
			return null;
		}
		
		Map<String, String> respHeaderMap = jsonToMap(respHeader.toString());
		
		
		if (respHeaderMap == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应头转Map失败");
			}
			
			return null;
		}
		
		TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				//指定排序器按照key升序排列
				return o1.compareTo(o2);
			}
		});
		
		if (serviceType.code == 14 || serviceType.code == 15 || serviceType.code ==40 ||  serviceType.code ==29 ||
				serviceType.code ==41 || serviceType.code ==42 || serviceType.code ==43 || serviceType.code ==44
				|| serviceType.code ==45 || serviceType.code == 32 ||  serviceType.code ==23 || serviceType.code == 24 
				|| serviceType.code == 25 || serviceType.code == 8 ||  serviceType.code == 9 ||serviceType.code == 27 ||
				serviceType.code == 28 || serviceType.code == 22) {
			
		} else {
			
			Object outBody = respMsg.get("outBody");
			
			/*if (outBody == null) {
				Logger.info("响应体对象获取失败");
				return null;
			}*/
			
			if (outBody != null) {
				Map<String, String> outBodyMap = jsonToMap(outBody.toString());
				
				if (outBodyMap == null) {
					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info("响应体Map获取失败");
					}
					
					return null;
				}
					
				//查询响应体加密参数
				String[] respEncryptParams = YbConsts.getRespEncryptKeys(serviceType.key);
				String[] respListEncryParams = YbConsts.getRespListEncryKeys(serviceType.key);
				String[] respListParams = YbConsts.getRespListKeys(serviceType.key);
				String[] respParams = YbConsts.getRespKeys(serviceType.key);
				
				for (String next : respParams) {
					
					//处理list参数
					if (next.equals(listName)) {
						String respListStr = outBodyMap.get(next);
						if (respListStr == null || checkNull(respListStr)) {
							/*for (String resp : respListParams) {
								map.put(resp + "0", "");
							}*/
						} else {
							List<Map<String, String>> respList = getList(respListStr);
							for (Map<String, String> map2 : respList) {
								String oderNo = map2.get("oderNo");

								for (String respParam : respListParams) {
									map.put(respParam + oderNo, map2.get(respParam));
								}
								for (String listEncryParam : respListEncryParams) {
									String value = map2.get(listEncryParam);
									if (value == null || value.isEmpty()) {
										map.put(listEncryParam + oderNo, "");
									} else {
										map.put(listEncryParam + oderNo, EncryptUtil.decrypt(value));
									}
								}
							}	
						}	
					}
					
					//放入响应List外的参数
					if (!next.equals(listName)) {
						String value = outBodyMap.get(next);
						
						if (value == null || value.isEmpty()) {
							map.put(next, "");
						} else {
							
							if (respEncryptParams != null && respEncryptParams.length > 0) {
								boolean flag = false;
								for (String ep : respEncryptParams) {
									
									if (next.equals(ep)) {
										map.put(next, EncryptUtil.decrypt(value));
										flag = true;
									}
								}
								if (!flag) {
									map.put(next, value); 
								}
							} else { 
								map.put(next, value);
							}
						}
					}
				}
				
				String addSignData = "";//加签数据
				
				Iterator ite = map.keySet().iterator();
				while (ite.hasNext()) {
					String next = ite.next().toString();

					addSignData = addSignData + map.get(next) + "|";
				}

				// 去除最后一位的|线分隔符
				if (addSignData.length() == 1 || addSignData.length() == 0) {
					addSignData = "";
				} else {		
					addSignData = addSignData.substring(0, addSignData.length() - 1);
				}
				
				//拼接加签时间戳
				addSignData = respHeaderMap.get("signTime") + "|" + addSignData;
				
				boolean varifyRes = varifySign(addSignData, respHeaderMap.get("signature"));
				if (!varifyRes ) {
					return null;
				}
			}
		}
			
		Iterator itHead = respHeaderMap.keySet().iterator();
		while (itHead.hasNext()) {
			String next = itHead.next().toString();
			
			map.put(next, respHeaderMap.get(next));
		}
		
		return map;
	}
	
	
	
	public static List<Map<String, String>> getList(String jsonStr) {
		
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		
		if (jsonStr == null || checkNull(jsonStr)) {
			return null;
		}
		
		JSONArray jsonArr = JSONArray.fromObject(jsonStr);
		
		if (jsonArr == null || jsonArr.isEmpty()) {
			return null;
		}
		
		for (Object o : jsonArr) {
			Map<String, String> map = jsonToMap(JSONObject.fromObject(o).toString());
			listMap.add(map);
		}
		
		return listMap;
	}
	
	
	
	
	
	

	/**
	 * 
	 */
	public static boolean checkNull(String str) {
		
		if ("[]".equals(str)) {
			return true;
		}
		if ("{}".equals(str)) {
			return true;
		}
		return false;
		
	}
	
	/**
	 * 获取银行卡
	 *
	 * @param key
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月11日
	 */
	public static String getBankName(String code) {
		JSONObject banks = JSONObject.fromObject(YbConsts.BANK_LIST);
		
		return banks.containsKey(code)?banks.getString(code):code;
	}
	
	/**
	 * 获得异步响应体
	 * 
	 * @author niu
	 * @create 2017.09.15
	 */
	public static TreeMap<String, String> getAysnResponse(String bodyStr) {
		
		JSONObject body = JSONObject.fromObject(bodyStr);
		if (body == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应字符串转JSON失败");
			}
			
			return null;
		}
		
		Object inBody = body.get("inBody");
		Object reqHeader = body.get("reqHeader");
		
		if (inBody == null || reqHeader == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("报文体或者报文头获取失败");
			}
			
			return null;
		}
		
		Map<String, String> inBodyMap = YbUtils.jsonToMap(inBody.toString());
		Map<String, String> reqHeaderMap = YbUtils.jsonToMap(reqHeader.toString());
		
		if (inBodyMap == null || reqHeaderMap == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("报文体或者报文头获取失败");
			}
			
			return null;
		}
		
		TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				//指定排序器按照key升序排列
				return o1.compareTo(o2);
			}
		});
		
		String accNo = inBodyMap.get("accNo");
		if (accNo == null || accNo.trim().isEmpty()) {
			map.put("accNo", "");
		} else {
			map.put("accNo", EncryptUtil.decrypt(accNo));
		}
		
		map.put("businessSeqNo", getValue(inBodyMap.get("businessSeqNo")));
		map.put("dealStatus", getValue(inBodyMap.get("dealStatus")));
		map.put("note", getValue(inBodyMap.get("note")));
		map.put("oldTxnType", getValue(inBodyMap.get("oldTxnType")));
		map.put("oldbusinessSeqNo", getValue(inBodyMap.get("oldbusinessSeqNo")));
		map.put("respCode", getValue(inBodyMap.get("respCode")));
		map.put("respMsg", getValue(inBodyMap.get("respMsg")));
		
		String value = inBodyMap.get("secBankaccNo");
		if (value == null || value.trim().length() == 0) {
			map.put("secBankaccNo", "");
		} else {
			map.put("secBankaccNo", EncryptUtil.decrypt(value));
		}
		//map.put("secBankaccNo", getValue());
		
		String addSignData = "";//加签数据
		
		Iterator ite = map.keySet().iterator();
		while (ite.hasNext()) {
			String next = ite.next().toString();

			addSignData = addSignData + map.get(next) + "|";
		}

		// 去除最后一位的|线分隔符
		if (addSignData.length() == 1) {
			addSignData = "";
		} else {		
			addSignData = addSignData.substring(0, addSignData.length() - 1);
		}
		
		//拼接加签时间戳
		addSignData = reqHeaderMap.get("signTime") + "|" + addSignData;
		
		boolean varifyRes = varifySign(addSignData, reqHeaderMap.get("signature"));
		if (!varifyRes ) {
			return null;
		}
		
		Iterator itHead = reqHeaderMap.keySet().iterator();
		while (itHead.hasNext()) {
			String next = itHead.next().toString();
			
			map.put(next, reqHeaderMap.get(next));
		}
		
		
		return map;
	}
	
	public static String getValue(String value) {
		
		if (value == null || value.trim().isEmpty()) {
			return "";
		}
		
		return value;
	}
	
	
/******************************************************************************************************************************************************************/
	
	
	public static HashMap<String, JSONObject> getRequestText(String clientSn, Map<String, String> inBody, String serviceType, String tradeType, List<Map<String, Object>> list1, String listName1, List<Map<String, Object>> list2, String listName2) {
		
		//获取请求报文头
		Map<String, String> reqHeader = getReqHeader(clientSn, inBody, serviceType, tradeType, list1, list2);
		
		//请求报文体敏感信息加密
		inBody = getEncryptMap(inBody, YbConsts.getEncryptKeys(serviceType));
		
		//请求报文体放入非必须参数
		inBody = getMap(inBody, YbConsts.getNotNecessaryParams(serviceType));

		//请求报文体放入List参数
		inBody = getMap(inBody, list1, listName1, YbConsts.getListEncryptKeys(serviceType));
		inBody = getMap(inBody, list2, listName2, YbConsts.getList2EncryptKeys(serviceType));

		//请求报文
		HashMap<String, JSONObject> requestText = new HashMap<String, JSONObject>();
		requestText.put("inBody", JSONObject.fromObject(inBody));
		requestText.put("reqHeader", JSONObject.fromObject(reqHeader));
		
		return requestText;
	}
	
	/**
	 * 获取请求报文头
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static Map<String, String> getReqHeader(String clientSn, Map<String, String> inBody, String serviceType, String tradeType, List<Map<String, Object>> list1, List<Map<String, Object>> list2) {
		
		String signTime = System.currentTimeMillis() + "";//当前时间戳
		
		LinkedHashMap<String, String> reqHeader = new LinkedHashMap<String, String>();
		
		reqHeader.put("version", YbConsts.VERSION);//版本号
		reqHeader.put("merchantCode", "oyph");//接入系统号：oyph
		reqHeader.put("txnType", tradeType);//交易类型
		reqHeader.put("clientSn", clientSn);//客户端请求流水号：OYPH+UUID
		reqHeader.put("clientDate", getFormatDate("yyyyMMdd"));//客户端日期
		reqHeader.put("clientTime", getFormatTime("HHmmss"));//客户端时间戳
		reqHeader.put("fileName", "");//文件名
		reqHeader.put("signTime", signTime);//加签时间戳
		reqHeader.put("signature", getSignature(signTime, inBody, serviceType, list1, list2));
		
		return reqHeader;
	}
	
	/**
	 * 公共应答头
	 * 
	 * @author niu
	 * @create 2017.08.28
	 */
	public static HashMap<String, JSONObject> getResaHeader(TreeMap<String, String> resMap, LinkedHashMap<String, String> inBody, String serviceType, String tradeType, List<Map<String, Object>> list) {

		String signTime = System.currentTimeMillis() + "";//当前时间戳
		
		LinkedHashMap<String, String> reqHeader = new LinkedHashMap<String, String>();
		
		String businessSeqNo = YbConsts.MER_CODE + "_" + OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_RECHARGE);
		
		reqHeader.put("version", YbConsts.VERSION);//版本号
		reqHeader.put("merchantCode", "oyph");//接入系统号：oyph
		reqHeader.put("txnType", tradeType);//交易类型
		reqHeader.put("clientSn",businessSeqNo );//客户端请求流水号：OYPH+UUID
		reqHeader.put("clientDate", getFormatDate("yyyyMMdd"));//客户端日期
		reqHeader.put("clientTime", getFormatTime("HHmmss"));//客户端时间戳
		reqHeader.put("serviceSn", resMap.get("businessSeqNo"));//P2P资金存管系统流水号
		reqHeader.put("serviceDate", resMap.get("clientDate"));//P2P资金存管系统日期(yyyymmdd)
		reqHeader.put("serviceTime",resMap.get("clientTime"));//P2P资金存管系统处理时间戳(hhmmss)
		reqHeader.put("respCode", resMap.get("respCode"));//返回交易成功或错误信息
		reqHeader.put("respMsg",  resMap.get("respMsg"));//响应信息
		reqHeader.put("signTime", signTime);	
		reqHeader.put("signature", getSignature(signTime, null, serviceType, list,null));//签名
		
		//请求报文
		HashMap<String, JSONObject> request = new HashMap<String, JSONObject>();
		//request.put("outBody", JSONObject.fromObject(null));
		request.put("respHeader", JSONObject.fromObject(reqHeader));
		
		return request;
	}
	
	
	/**
	 * 获取签名
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static String getSignature(String signTime, Map<String, String> inBody, String serviceType, List<Map<String, Object>> list1, List<Map<String, Object>> list2) {
		
		// 1.声明TreeMap
		TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				//指定排序器按照key升序排列
				return o1.compareTo(o2);
			}
		});
		
		// 2.必须参数放入TreeMap
		map = getTreeMap(map, inBody);
		
		// 3.List参数放入TreeMap
		map = getTreeMap(map, list1);
		map = getTreeMap(map, list2);
		
		// 4.非必须参数放入TreeMap
		map = getTreeMap(map, YbConsts.getNotNecessaryParams(serviceType));
		
		// 5.得到待加签字符串
		String toSignText = getToSignText(signTime, map);

		return YbUtils.addSign(toSignText);
	}
	
	/**
	 * List中的敏感信息加密
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static String getListMap(List<Map<String, Object>> listMap, String[] encryptKeys) {
		
		if (listMap == null || listMap.size() == 0) {
			return "[]";
		}
		
		if (encryptKeys == null || encryptKeys.length == 0) {
			return JSONArray.fromObject(listMap).toString();
		}		
		
		for (Map<String, Object> map : listMap) {
			
			for (String encryptKey : encryptKeys) {
				String value = map.get(encryptKey).toString().trim();
				
				if (value != null && value.length() > 0) {
					map.put(encryptKey, EncryptUtil.encrypt(value));
				}
			}		
		}
		
		return JSONArray.fromObject(listMap).toString();
	}
	
	
	/**
	 * Map中的敏感信息加密
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static Map<String, String> getEncryptMap(Map<String, String> map, String[] encryptKeys) {
		
		if (encryptKeys == null || encryptKeys.length == 0) {
			return map;
		}
		
		for (String encryptKey : encryptKeys) {	
			
			String value = map.get(encryptKey).trim();
			if (value == null || value.length() == 0) {
				map.put(encryptKey, "");
			} else {
				map.put(encryptKey, EncryptUtil.encrypt(map.get(encryptKey)));
			}	
		}
		
		return map;
	}
	
	/**
	 * 把ListMap放入TreeMap
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static TreeMap<String, String> getTreeMap(TreeMap<String, String> map, List<Map<String, Object>> listMap) {
		
		if (listMap == null || listMap.size() == 0) {
			return map;
		}
		
		for (Map<String, Object> map2 : listMap) {
			
			String oderNo = map2.get("oderNo").toString();
			Iterator<Map.Entry<String, Object>> it = map2.entrySet().iterator();
			
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				
				if (!entry.getKey().equals("oderNo")) {				
					map.put(entry.getKey() + oderNo, entry.getValue().toString());
				}
			}	
		}
		
		return map;
	}
	
	public static Map<String, String> getMap(Map<String, String> map, List<Map<String, Object>> listMap, String listName, String[] listEncryptKeys) {
		
		if (listName == null) {
			return map;
		}
		
		if (listMap == null || listMap.size() == 0) {
			map.put(listName, "[]");
			return map;
		}
		
		map.put(listName, getListMap(listMap, listEncryptKeys));
		
		return map;
	}
	
	/**
	 * 把String[]放入TreeMap
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static TreeMap<String, String> getTreeMap(TreeMap<String, String> map, String[] strArr) {
		
		if (strArr == null || strArr.length == 0) {
			return map;
		}
		
		for (String str : strArr) {
			map.put(str, "");
		}
		
		return map;
	}
	
	public static Map<String, String> getMap(Map<String, String> map, String[] strArr) {
		
		if (strArr == null || strArr.length == 0) {
			return map;
		}
		
		for (String str : strArr) {
			map.put(str, "");
		}
		
		return map;
	}
	
	/**
	 * 把Map放入TreeMap
	 * 
	 * @author niu
	 * @create 2017.09.19
	 */
	public static TreeMap<String, String> getTreeMap(TreeMap<String, String> map, Map<String, String> map2) {
		
		if (map2 == null || map2.isEmpty()) {
			return map;
		}
		
		Iterator<Map.Entry<String, String>> it = map2.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			map.put(entry.getKey(), entry.getValue());
		}
		
		return map;
	}
	
	/**
	 * 得到待加签明文
	 * 
	 * @author niu
	 * @create 2017.09.18
	 */
	public static String getToSignText(String signTime, TreeMap map) {
		
		StringBuffer stBu = new StringBuffer();
		
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			stBu.append(map.get(it.next())).append("|");
		}
		
		if (stBu.length() == 1 || stBu.length() == 0) {
			
			return signTime + "|" + "";
		} 	
		
		return signTime + "|" + stBu.substring(0, stBu.length() - 1);
	}
	
	/**
	 * 验签
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public static ResultInfo checkSign(String respMsgString, ServiceType serviceType, String listName) {
		
		ResultInfo result = new ResultInfo();
		
		//获取响应信息
		
		//空指针
		if (respMsgString == null || respMsgString.isEmpty()) {
			result.code = -1;
			result.msg  = "响应信息字符串不能为空";
			
			return result;
		}
		
		//获取响应体
		JSONObject respMsg = JSONObject.fromObject(respMsgString);
		if (respMsg == null) {
			result.code = -1;
			result.msg  = "响应信息字符串转换响应信息JSON对象失败";
			
			return result;
		}
		return result;
	}
	
	/**
	 * 获取响应体Json对象
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public static ResultInfo getRespBody(String respBodyString) {
		
		ResultInfo result = new ResultInfo();
		
		//空指针
		if (respBodyString == null || respBodyString.isEmpty()) {
			result.code = -1;
			result.msg  = "响应体字符串不能为空";
			
			return result;
		}
		
		//获取响应体
		JSONObject respBody = JSONObject.fromObject(respBodyString);
		if (respBody == null) {
			result.code = -1;
			result.msg  = "响应体字符串转换响应体失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "";
		result.obj = respBody;
		
		return result;
	}
	
	/**
	 * 获取合同列表
	 * 
	 * @author niu
	 * @create 2018-01-03
	 */
	public static List<Map<String, Object>> getContractList() {
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		
		conMap.put("oderNo", "0");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		return contractList;
	}
	
	/**
	 * 获取资金账务处理列表
	 * 
	 * @author niu
	 * @create 2018-01-03
	 */
	public static Map<String, Object> getAccountMap(String oderNo, String oldbusinessSeqNo, String oldOderNo, String debitAccountNo, String cebitAccountNo, String amount, String summaryCode) {
		
		Map<String, Object> accMap = new HashMap<String, Object>();
		
		accMap.put("oderNo", oderNo);
		accMap.put("oldbusinessSeqNo", oldbusinessSeqNo);
		accMap.put("oldOderNo", oldOderNo);
		accMap.put("debitAccountNo", debitAccountNo);
		accMap.put("cebitAccountNo", cebitAccountNo);
		accMap.put("currency", "CNY");
		accMap.put("amount", amount);
		accMap.put("summaryCode", summaryCode);
		
		return accMap;
	}
	
}

