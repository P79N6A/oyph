package controllers.back.weChat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import common.utils.Constant;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.wx.WeixinUtil;
import controllers.common.BackBaseController;
import models.common.entity.t_supervisor;
import models.entity.t_wechat_access_token;
import models.entity.t_wechat_customer;
import net.sf.json.JSONObject;
import service.AccessTokenService;
import service.CustomerMessageService;

/**
 * 后台-微信-客服管理-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年07月04日
 */
public class CustomerMessageCtrl extends BackBaseController {
	
	protected static CustomerMessageService customerMessageService = Factory.getService(CustomerMessageService.class);

	protected static AccessTokenService accessTokenService = Factory.getService(AccessTokenService.class);
	/**
	 * 进入客服列表界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年07月04日
	 */
	public static void showCustomerListPre(int currPage,int pageSize) {
		
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		JSONObject json = WeixinUtil.getkflist(accessTokens);
		List<Map<String,Object>> list = (List<Map<String,Object>>)json.get("kf_list");
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size() ; i++) {
				Map<String,Object> resMap = list.get(i);
				String kfAccount = (String)resMap.get("kf_account");
				Integer kfId = (Integer)resMap.get("kf_id");
				String kfNick = (String)resMap.get("kf_nick");
				String kfWx = (String)resMap.get("kf_wx");
				
				t_wechat_customer customer = customerMessageService.findByColumn(" kf_account = ? ", kfAccount);
				if (customer == null){
					t_wechat_customer wxCus = new t_wechat_customer();
					wxCus.kf_account = kfAccount;
					wxCus.kf_id = kfId;
					wxCus.kf_nick = kfNick;
					wxCus.kf_wx = kfWx;
					customerMessageService.saveCustomer(wxCus);
				} else {
					if(!(customer.kf_id == kfId && customer.kf_nick.equals(kfNick) && customer.kf_wx.equals(kfWx))) {
						customer.kf_id = kfId;
						customer.setKf_nick(kfNick);
						customer.kf_wx = kfWx;
						
						customerMessageService.saveCustomer(customer);
					}
				}
			}
		}
		
		String name = params.get("name");
		
		PageBean<t_wechat_customer> page = customerMessageService.pageOfCustomerList(currPage, pageSize, name);
		 
		render(page);
	}
	
	/**
	 * 进入添加客服列表界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年07月04日
	 */
	public static void toAddCustomerPre() {
		List<t_supervisor> supervisor = supervisorService.findListByColumn(" id  not in ( select supervisor_id from t_wechat_customer )", null);
		
		render(supervisor);
	}
	
	/**
	 * 进入修改客服列表界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年07月04日
	 */
	public static void toEditCustomerPre(long customerId) {
		
		t_wechat_customer customer = customerMessageService.findByID(customerId);
		List<t_supervisor> supervisor = supervisorService.findListByColumn(" id not in ( select supervisor_id from t_wechat_customer )", null);
		
		if(customer == null) {
			flash.error("系统错误，请重试");
			showCustomerListPre(1,10);
		}
		
		render(customer,supervisor);
	}
	/**
	 * 进入邀请绑定客服界面
	 *
	 * @return
	 *
	 * @author gengjincang
	 * @createDate 2018年8月20日
	 */
	public static void toInviteCustomerPre(Long customerId,String kf_account,String invite_wx) {
		render(customerId,kf_account,invite_wx);
	}
	
	/**
	 * 保存客服信息
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年07月04日
	 */
	public static void addCustomer() {
		
		String nickNameStr = params.get("nickname");
		String kfAccountStr = params.get("kf_account");
		String realNameId = params.get("realNameId");
		Long supervisorId = Long.parseLong(realNameId);
		
		if (nickNameStr == null) {
			flash.error("请输入客服昵称");
			toAddCustomerPre();
		}
		if (kfAccountStr == null) {
			flash.error("请输入客服账号");
			toAddCustomerPre();
		}
		if (realNameId == null) {
			flash.error("请输入客服真实姓名");
			toAddCustomerPre();
		}
		
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("kf_account", kfAccountStr);
		jsonObject.put("nickname", nickNameStr);
		ResultInfo res = WeixinUtil.addKf(jsonObject.toString(), accessTokens);
		if (res.code == 0) {
			t_wechat_customer customer = new t_wechat_customer();
			customer.supervisor_id = supervisorId;
			customer.kf_account = kfAccountStr;
			customer.kf_nick = nickNameStr;
			customer.time = new Date();
			customerMessageService.saveCustomer(customer);
			
			flash.error("添加客服成功");
			showCustomerListPre(1,10);
		}
		flash.error(res.msg+",添加客服失败");
		toAddCustomerPre();
	}
	
	/**
	 * 修改客服信息
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年07月04日
	 */
	public static void editCustomer() {
		String nickNameStr = params.get("nickname");
		String kfAccount = params.get("kf_account");
		String realNameId = params.get("realNameId");
		String customerId = params.get("customerId");
		Long cusId = Long.parseLong(customerId);
		if (nickNameStr == null) {
			flash.error("请输入客服昵称");
			toEditCustomerPre(cusId);
		}
		if (kfAccount == null) {
			flash.error("请输入客服账号");
			toEditCustomerPre(cusId);
		}
		if (customerId == null) {
			flash.error("该客服信息不能被修改");
			toEditCustomerPre(cusId);
		}
		if (realNameId == null) {
			flash.error("请输入客服姓名");
			toEditCustomerPre(cusId);
		}
		Long supervisorId = Long.parseLong(realNameId);
		
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("kf_account", kfAccount);
		jsonObject.put("nickname", nickNameStr);
		
		ResultInfo res = WeixinUtil.updateKf(jsonObject.toString(), accessTokens);
		if (res.code == 0) {
			
			t_wechat_customer customer = customerMessageService.findByID(cusId);
			customer.supervisor_id = supervisorId;
			customer.kf_account = kfAccount;
			customer.kf_nick = nickNameStr;
			customer.save();
			
			flash.error("修改客服信息成功");
			showCustomerListPre(1, 10);
		}
		flash.error(res.msg+",修改客服信息失败");
		toEditCustomerPre(cusId);
	}
	
	/**
	 * 邀请绑定客服账号
	 * 
	 * @author guoShiJie
	 * @createDate 2018.8.9
	 * */
	public static void inviteCustomer(){
		String kfAccountStr = params.get("kf_account");
		String inviteWx = params.get("invite_wx");
		String customerId = params.get("customerId");
		if (kfAccountStr == null) {
			flash.error("请输入客服账号");
			showCustomerListPre(1,10);
		}
		if (inviteWx == null) {
			flash.error("请输入客服微信号");
			showCustomerListPre(1,10);
		}
		if (customerId == null) {
			flash.error("该客服账号不存在");
			showCustomerListPre(1,10);
		}
		Long cusId = Long.parseLong(customerId);
		t_wechat_customer customer = t_wechat_customer.findById(cusId);
		if (customer == null) {
			flash.error("该客服账号不存在");
			showCustomerListPre(1,10);
		}
		
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("kf_account", kfAccountStr);
		jsonObject.put("invite_wx", inviteWx);
		ResultInfo res = WeixinUtil.inviteworker(jsonObject.toString(), accessTokens);
		if (res.code == 0) {
			flash.error("邀请绑定客服账号成功");
			showCustomerListPre(1,10);
		}
		flash.error(res.msg+",邀请绑定客服账号失败");
		showCustomerListPre(1,10);
		
	}
	
	public static void judgeKfAccount (String kf_account) {
		
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		JSONObject json = WeixinUtil.getkflist(accessTokens);
		List<Map<String, Object>> list = (List<Map<String, Object>>)json.get("kf_list");
		for(int i = 0; i< list.size() ; i++){
			Map<String,Object> res = list.get(i);
			if (kf_account.equals(res.get("kf_account"))) {
				renderJSON(false);
			}
		}
		renderJSON(true);
	}
	
	public static void delKfAccount (String kf_account) {
		ResultInfo result = new ResultInfo();
		t_wechat_access_token accessToken = accessTokenService.queryAccessToken();
		String accessTokens = accessToken.getToken();
		ResultInfo res = WeixinUtil.delKf(kf_account, accessTokens);
		if (res.code == 0) {
			t_wechat_customer customer = customerMessageService.findByColumn(" kf_account = ? ", kf_account);
			if (customer != null) {
				customer.delete();
				result.code = 1;
				result.msg = kf_account+"客服删除成功";
				renderJSON(result);
			}
		}
		result.code = -1;
		result.msg = res.msg+",删除失败";
		renderJSON(result);
	}
}
