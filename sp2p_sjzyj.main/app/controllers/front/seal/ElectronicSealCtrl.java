package controllers.front.seal;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.org.apache.regexp.internal.recompile;

import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.TimeUtil;
import controllers.common.FrontBaseController;
import controllers.front.seal.BestsignOpenApiClient;
import models.common.bean.CurrUser;
import models.common.entity.t_pact;
import models.common.entity.t_ssq_user;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_debt_transfer;
import models.core.entity.t_invest;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.libs.F;
import services.common.NoticeService;
import services.common.PactService;
import services.common.UserInfoService;
import services.common.ssqUserService;
import services.core.BidService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import yb.YbUtils;

/**
 * 上上签数据对接
 * 
 * @author LiuPengwei
 * @createDate 2018年3月6日 10:48:32
 */
public class ElectronicSealCtrl {

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static BidService bidService = Factory.getService(BidService.class);
	protected static InvestService investservice = Factory.getService(InvestService.class);
	protected static BillService billService = Factory.getService(BillService.class);
	protected static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	protected static PactService pactService = Factory.getService(PactService.class);
	protected static DebtService debtService = Factory.getService(DebtService.class);

	// 上上签开发者ID
	public static final String DEVELOPERID = Play.configuration.getProperty("ssq.developerId");

	// 上上签http路径
	public static final String SERVERHOST = Play.configuration.getProperty("ssq.serverHost");

	// 上上签开发者私钥
	public static final String PRIVATEKEY = Play.configuration.getProperty("ssq.privateKey");

	// 上上签开发者账号
	public static final String ACCOUNT = Play.configuration.getProperty("ssq.account");

	// 已封装的HTTP接口调用的的client，您可以使用它来调用常规api和特殊api，您可以在此BestsignOpenApiClient中添加新的接口方法方便调用。
	private static BestsignOpenApiClient openApiClient = new BestsignOpenApiClient(DEVELOPERID, PRIVATEKEY, SERVERHOST);

	/**
	 * 个人用户注册 [注册，设置实名信息，申请数字证书，生成默认签名]
	 * 
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日 10:48:24
	 */
	public static String personalUserReg(t_user_info userInfo) {

		// 用户注册
		String account = userInfo.mobile; // 账号
		String name = userInfo.reality_name; // 用户名称
		String mail = ""; // 用户邮箱
		String mobile = userInfo.mobile; // 用户手机号码

		// 设置个人实名信息
		String identity = userInfo.id_number; // 证件号码
		String identityType = "0"; // 证件类型 0-身份证
		String contactMail = ""; // 联系邮箱
		String contactMobile = mobile; // 联系电话
		String province = ""; // 所在省份
		String city = ""; // 所在城市
		String address = ""; // 联系地址

		// 注册返回异步申请证书任务id
		String taskId = null;

		try {
			taskId = openApiClient.userPersonalReg(account, name, mail, mobile, identity, identityType, contactMail,
					contactMobile, province, city, address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (taskId != null) {
			// 保存任务编号
			boolean fag = ssquserService.userSealTaskId(userInfo.user_id, userInfo.mobile, taskId, 0);
			if (fag) {

				Logger.info("证书任务编号:" + taskId);
				Logger.info("上上签用户注册成功");
				return taskId;
			} else {

				Logger.info("上上签用户注册失败");
				return null;
			}
		} else {

			Logger.info("上上签用户注册失败");
			return null;
		}
	}

	/**
	 * 企业用户注册 [注册，设置实名信息，申请数字证书，生成默认签名]
	 * 
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日14:41:49
	 */
	public static String EnterpriseUserReg(t_user_info userInfo) {

		// 用户注册
		String account = userInfo.mobile; // 账号
		String name = userInfo.enterprise_name; // 企业名称
		String mail = ""; // 用户邮箱
		String mobile = userInfo.mobile; // 用户手机号码

		// 设置企业证件信息
		String credit = userInfo.enterprise_credit; // 统一社会信用代码
		String realityName = userInfo.reality_name; // 法人姓名
		String idNumber = userInfo.id_number; // 法人身份证号
		String legalPersonIdentityType = "0"; // 证件类型 0-身份证
		String province = ""; // 所在省份
		String city = ""; // 所在城市
		String address = ""; // 联系地址

		// 注册返回异步申请证书任务id
		String taskId = null;

		try {

			taskId = openApiClient.userEnterpriseReg(account, name, mail, mobile, credit, realityName, idNumber,
					legalPersonIdentityType, province, city, address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (taskId != null) {
			// 保存任务编号
			boolean fag = ssquserService.userSealTaskId(userInfo.user_id, userInfo.mobile, taskId, 0);
			if (fag) {

				Logger.info("证书任务编号:" + taskId);
				Logger.info("上上签用户注册成功");
				return taskId;
			} else {

				Logger.info("上上签用户注册失败");
				return null;
			}
		} else {

			Logger.info("上上签用户注册失败");
			return null;
		}
	}

	/**
	 * 用户证书状态查询
	 * 
	 * @param account
	 *            上上签账户
	 * @param taskId
	 *            用户证书编号
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月29日15:51:27
	 */
	public static ResultInfo userCredentialStatus(String account, String taskId) {
		ResultInfo result = new ResultInfo();

		String credentialStatus = openApiClient.userCredentialStatus(account, taskId);
		if (!credentialStatus.equals("5")) {
			result.code = -1;
			result.msg = account + "电子签章证书检测失败！";

			return result;
		}

		result.code = 1;
		result.msg = account + "电子签章证书检测成功！";
		return result;

	}

	/**
	 * 上传并创建合同
	 * 
	 * @param bidId
	 *            标id
	 * @param contractFile
	 *            PDF字节流
	 * @param invests
	 *            投资人数
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月9日 10:00:59
	 */
	public static ResultInfo createContract(long bidId, File contractFile, t_pact pact) {
		ResultInfo result = new ResultInfo();
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			result.code = -1;
			result.msg = "标的编号不存在！";

		}
		String account = ACCOUNT;
		byte[] bdata = null;
		try {
			FileInputStream file = new FileInputStream(contractFile);
			bdata = IOUtils.toByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fmd5 = DigestUtils.md5Hex(bdata);
		String ftype = "pdf";
		String fname = "投资协议.pdf";
		String fpages = 9 + "";
		byte[] bdatas = null;
		try {
			FileInputStream files = new FileInputStream(contractFile);
			bdatas = IOUtils.toByteArray(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fdata = Base64.encodeBase64String(bdatas);
		String title = "投资协议";
		Date now = new Date();
		Date add = DateUtil.add(now, Calendar.DATE, 2); // 投资协议签署有效两天
		String expireTime = add.getTime() / 1000 + "";
		String description = "";
		String contractId = openApiClient.createContract(account, fmd5, ftype, fname, fpages, fdata, title, expireTime,description);
		if (contractId != null) {
			// boolean falg = bidService.addBidContract(bidId,contractId);
			boolean falg = pactService.addcontractId(pact, contractId);
			if (falg) {
				Logger.info("标的编号:" + bidId+" "+pact.user_id+ "合同创建成功");
				// a:pdf文件一页分成60个方块，从第8页第41个方块开始签署
				int a = 41;
				t_ssq_user ssqUser = ssquserService.findByUserId(pact.user_id);
				signContract(contractId, a, ssqUser.account);
				// 借款人签署
				result = loanSignContract(contractId, bid.user_id);
				if (result.code < 0) {
					return result;
				}
				// 担保人签署
				if (bid.ssq_guarantee_user != null) {
					result = suretySignContract(contractId, bid.ssq_guarantee_user);
					if (result.code < 0) {
						return result;
					}
				}
				// 平台签署
				result = platformSignContract(contractId);
				if (result.code < 0) {
					return result;
				}

			}

		} else {

			result.code = -1;
			result.msg = "标的编号:" + bidId + "合同创建失败";
			return result;
		}

		// 结束合同
		result = endSignContract(contractId, bidId);

		if (result.code < 0) {
			return result;
		}

		result.code = 1;
		result.msg = "合同生成完成！";

		return result;

	}

	/**
	 * 投资人借款合同签署
	 * 
	 * @param contractId
	 *            合同编号
	 * @param count
	 *            人数
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月9日 14:12:46
	 */
	public static void signContract(String contractId, int count, String account) {

		String signer = account;

		String x1 = "0.1";
		String y1 = "0.06";
		double y = 0.06;
		double x = 0.1;

		// price 计算过渡值
		int price = count % 60;

		if (price == 0) {
			price = 60;
		}

		int i = price % 4 - 1;
		x = x + 0.2 * i;
		if (price % 4 == 0) {
			x = 0.7;
		}
		x1 = YbUtils.formatAmount(x);

		int j = price / 4;

		y = y + 0.06 * j;
		if (price % 4 == 0) {
			y = 0.06 * j;
		}
		y1 = YbUtils.formatAmount(y);

		int num = count % 60 == 0 ? count / 60 : count / 60 + 1;

		int page = 6 + num;

		String pageNum = page + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			Logger.info("标的第" + count + "个用户签署合同失败");
		}
	}

	/**
	 * 借款人借款合同签署
	 * 
	 * @param contractId
	 *            合同编号
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月26日17:31:55
	 */
	public static ResultInfo loanSignContract(String contractId, long userId) {
		ResultInfo result = new ResultInfo();

		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);

		String signer = userInfo.mobile;

		String x1 = "";
		String y1 = "";
		// 个人借款人和企业借款人坐标区分
		if (userInfo.enterprise_name == null || StringUtils.isBlank(userInfo.enterprise_name)) {
			x1 = 0.77 + "";
			y1 = 0.76 + "";
		} else {

			x1 = 0.77 + "";
			y1 = 0.72 + "";
		}

		String pageNum = 8 + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			result.code = -1;
			result.msg = "合同编号:" + contractId + "借款用户签署合同失败";
			return result;
		}
		result.code = 1;
		result.msg = "合同编号:" + contractId + "借款用户签署成功";
		return result;
	}

	/**
	 * 担保人借款合同签署
	 * 
	 * @param contractId
	 *            合同编号
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月27日10:49:42
	 */
	public static ResultInfo suretySignContract(String contractId, String userMobile) {
		ResultInfo result = new ResultInfo();

		String signer = userMobile;

		String x1 = 0.5 + "";
		String y1 = 0.72 + "";
		String pageNum = 8 + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			result.code = -1;
			result.msg = "合同编号:" + contractId + "担保人签署合同失败";
			return result;
		}
		result.code = 1;
		result.msg = "合同编号:" + contractId + "担保人签署成功";
		return result;
	}

	/**
	 * 平台借款合同签署
	 * 
	 * @param contractId
	 *            合同编号
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月27日10:53:51
	 */
	public static ResultInfo platformSignContract(String contractId) {
		ResultInfo result = new ResultInfo();

		String signer = ACCOUNT;

		String x1 = 0.27 + "";
		String y1 = 0.72 + "";
		String pageNum = 8 + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			result.code = -1;
			result.msg = "合同编号:" + contractId + "平台签署合同失败";
			return result;
		}

		result.code = 1;
		result.msg = "合同编号:" + contractId + "平台签署成功";
		return result;

	}

	/**
	 * 锁定并结束合同
	 * 
	 * @param contractId
	 *            合同编号
	 * @return result
	 * 
	 * @throws Exception
	 * @author LiuPengwei
	 * @createDate 2018年3月15日17:49:47
	 * 
	 */
	public static ResultInfo endSignContract(String contractId, long bidId) {
		ResultInfo result = new ResultInfo();

		int endSignContract1 = openApiClient.endSignContract(contractId);

		if (endSignContract1 != 0) {
			result.code = -1;
			result.msg = "标的编号:" + bidId + "合同结束失败";
			return result;
		}
		result.code = 1;
		result.msg = "标的编号:" + bidId + "合同结束成功";

		return result;

	}

	/**
	 * 预览合同
	 * 
	 * @throws Exception
	 * 
	 * @author LiuPengwei
	 * @throws ParseException 
	 * @createDate 2018年3月9日 14:12:46
	 */
	public static String previewContract(long bidId, long userId,String time,Long uid) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//正式环境需要把时间改为2018-12-14 18:00:00
		Date date = dateFormat.parse("2018-12-14 18:00:00");
		
		Date release_time = TimeUtil.strToDate(time);
		String contractId = null;// 合同编号
		if (release_time.before(date)) {
			t_bid bid = bidService.findByID(bidId);
			contractId = bid.contract_id; 
			
			userId = uid;
		} else {	
		
			t_pact pact = pactService.findByCondition(bidId,userId);
			contractId = pact.contract_id;
		}
		
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		String account = userInfo.mobile; // 用户上上签账号
		String previewContract = openApiClient.previewContract(contractId, account);
		return previewContract;
	}

	/**
	 * 下载合同
	 * 
	 * @throws Exception
	 */
	public static void downloadContract(String contractId) throws Exception {

		byte[] pdf = openApiClient.contractDownload(contractId);
		byte2File(pdf, "D:\\test", contractId + ".pdf"); // 文件下载到本地目录
	}

	/**
	 * 辅助方法，本地文件转为byte[]
	 * 
	 * @param filePath
	 * @return
	 */
	private static byte[] inputStream2ByteArray(String filePath) {
		byte[] data = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new FileInputStream(filePath);
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 4];
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}
			data = out.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				// ignore
			}
		}
		return data;
	}

	/**
	 * 辅助方法，byte数组保存为本地文件
	 * 
	 * @param buf
	 * @param filePath
	 * @param fileName
	 */
	private static void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: createDebtContract
	 * 
	 * @description 生成我的转让/我的受让协议
	 * @param debtId 债权id
	 * @param contractFile 文件
	 * @param  pact 合同
	 * @return ResultInfo    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午1:45:17
	 */
	public static ResultInfo createDebtContract(long debtId, File contractFile, t_pact pact) {
		ResultInfo result = new ResultInfo();
		t_debt_transfer debt = debtService.findByID(debtId);
		
		if (debt == null) {
			result.code = -1;
			result.msg = "债权编号不存在！";
			return result;
		}
		//转让人
		long transferUser = debt.user_id;
		//查询转让人上上签用户是否正常
		t_ssq_user ssqUser = ssquserService.findByUserId(transferUser);
		
		if (ssqUser == null) {
			result.code = -1;
			result.msg = "转让用户电子签章用户效验失败！";
			return result;
		}
		String account = ACCOUNT;
		byte[] bdata = null;
		try {
			FileInputStream file = new FileInputStream(contractFile);
			bdata = IOUtils.toByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fmd5 = DigestUtils.md5Hex(bdata);
		String ftype = "pdf";
		String fname = "转让协议.pdf";
		String fpages = 4 + "";
		byte[] bdatas = null;
		try {
			FileInputStream files = new FileInputStream(contractFile);
			bdatas = IOUtils.toByteArray(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fdata = Base64.encodeBase64String(bdatas);
		String title = "转让协议";
		Date now = new Date();
		Date add = DateUtil.add(now, Calendar.DATE, 2); // 转让协议签署有效两天
		String expireTime = add.getTime() / 1000 + "";
		String description = "";
		String contractId = openApiClient.createContract(account, fmd5, ftype, fname, fpages, fdata, title, expireTime,description);
		if (contractId != null) {
			// boolean falg = bidService.addBidContract(bidId,contractId);
			boolean falg = pactService.addcontractId(pact, contractId);
			if (falg) {
				Logger.info("债权编号:" + debtId + " 协议创建成功");
				// a:pdf文件一页分成60个方块，从第8页第1个方块开始签署
				int a = 1;
				//受让人
				long assUser = debt.transaction_user_id; 
				// 转让人签署
				signDebtContract(contractId, a, ssqUser.account);
				//受让人签署
				result = assSignContract(contractId,assUser);
				
				if (result.code < 0) {
					return result;
				}
				// 平台签署
				result = platformDebtSignContract(contractId);
				if (result.code < 0) {
					return result;
				}

			}

		} else {

			result.code = -1;
			result.msg = "债券编号:" + debtId + "合同创建失败";
			return result;
		}

		// 结束合同
		result = endSignDebtContract(contractId, debtId);
		if (result.code < 0) {
			return result;
		}

		result.code = 1;
		result.msg = "合同生成完成！";

		return result;
	}

	/**
	 * 
	 * @Title: signDebtContract
	 * 
	 * @description 我的转让/我的受让----转让人签署
	 * @param  contractId 合同id
	 * @param  count 人数 1人
	 * @param  account  账户
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午1:47:33
	 */
	public static void signDebtContract(String contractId, int count, String account) {
		
		String signer = account;

		String x1 = "0.1";
		String y1 = "0.45";
		double y = 0.45;
		double x = 0.1;

		// price 计算过渡值
		int price = count % 60;

		if (price == 0) {
			price = 60;
		}

		int i = price % 4 - 1;
		x = x + 0.2 * i;
		if (price % 4 == 0) {
			x = 0.7;
		}
		x1 = YbUtils.formatAmount(x);

		int j = price / 4;

		y = y + 0.06 * j;
		if (price % 4 == 0) {
			y = 0.06 * j;
		}
		y1 = YbUtils.formatAmount(y);

		int num = count % 60 == 0 ? count / 60 : count / 60 + 1;

		int page = 2 + num;
		
		String pageNum = page + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			Logger.info("债权转让用户签署合同失败");
		}
	}
	
	/**
	 * 
	 * @Title: assSignContract
	 * 
	 * @description 我的转让/我的受让---受让用户签署
	 * @param  contractId 
	 * @param  user_id  受让用户
	 * @return ResultInfo    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午1:44:02
	 */
	private static ResultInfo assSignContract(String contractId, long user_id) {
	
		ResultInfo result = new ResultInfo();

		t_user_info userInfo = userInfoService.findUserInfoByUserId(user_id);

		String signer = userInfo.mobile;
		String x1 = "";
		String y1 = "";
		// 个人借款人和企业借款人坐标区分
		if (userInfo.enterprise_name == null || StringUtils.isBlank(userInfo.enterprise_name)) {
			x1 = 0.1 + "";
			y1 = 0.59 + "";
		} else {

			x1 = 0.1 + "";
			y1 = 0.59 + "";
		}

		String pageNum = 3 + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			result.code = -1;
			result.msg = "合同编号:" + contractId + "受让用户签署合同失败";
			return result;
		}
		result.code = 1;
		result.msg = "合同编号:" + contractId + "受让用户签署成功";
		return result;
	}

	/***
	 * 
	 * @Title: platformDebtSignContract
	 * 
	 * @description 我的转让/受让平台签署
	 * @param @param contractId 合同号
	 * @return ResultInfo    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午1:43:21
	 */
	public static ResultInfo platformDebtSignContract(String contractId) {
		ResultInfo result = new ResultInfo();

		String signer = ACCOUNT;

		String x1 = 0.27 + "";
		String y1 = 0.65 + "";
		String pageNum = 3 + "";
		String signatureImageName = "";
		String signatureImageData = "";

		int intsignContract1 = openApiClient.signContract(contractId, signer, x1, y1, pageNum, signatureImageName,
				signatureImageData);

		if (intsignContract1 != 0) {

			result.code = -1;
			result.msg = "合同编号:" + contractId + "平台签署协议失败";
			return result;
		}

		result.code = 1;
		result.msg = "合同编号:" + contractId + "平台签署成功";
		return result;

	}
	
	/**
	 * 
	 * @Title: endSignDebtContract
	 * 
	 * @description 锁定并结束合同(我的转让和我的受让)
	 * @param  contractId 合同id
	 * @param debtId 债权id
	 * @return ResultInfo    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月24日-下午2:07:47
	 */
	public static ResultInfo endSignDebtContract(String contractId, long debtId) {
		
		ResultInfo result = new ResultInfo();

		int endSignContract1 = openApiClient.endSignContract(contractId);

		if (endSignContract1 != 0) {
			
			result.code = -1;
			result.msg = "债权编号:" + debtId + "合同结束失败";
			return result;
		}
		
		result.code = 1;
		result.msg = "债权编号:" + debtId + "合同结束成功";

		return result;

	}
	/***
	 * 
	 * @Title: previewDebtContract
	 * 
	 * @description 预览我的转让/我的受让协议
	 * @param debtId 债权id
	 * @param  user_id 查看的用户
	 * @throws ParseException
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午1:42:29
	 */
	public static String previewDebtContract(long debtId,long user_id) throws ParseException {
		
		String contractId = null;// 合同编号	
		t_pact pact = pactService.findByDebtId(debtId);
		contractId = pact.contract_id;

		t_user_info userInfo = userInfoService.findUserInfoByUserId(user_id);
		String account = userInfo.mobile; // 用户上上签账号
		String previewContract = openApiClient.previewContract(contractId, account);
		
		return previewContract;
	}
}
