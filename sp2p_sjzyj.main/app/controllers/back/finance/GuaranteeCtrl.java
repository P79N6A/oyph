package controllers.back.finance;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import models.common.entity.t_guarantee;
import models.common.entity.t_user_info;
import payment.impl.PaymentProxy;
import services.common.GuaranteeService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import yb.enums.ServiceType;

/**
 * 后台-财务-担保人管理-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年1月5日
 */
public class GuaranteeCtrl extends BackBaseController {
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static GuaranteeService guaranteeService = Factory.getService(GuaranteeService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	/**
	 * 后台-财务-担保人管理-首页
	 * @rightID 512001
	 * @createDate 2018年1月5日
	 *
	 */
	public static void showGuaranteePre() {
		
		List<t_guarantee> guarantees = guaranteeService.findAll();
		
		render(guarantees);
	}
	
	/**
	 * 后台-财务-担保人管理-担保人列表页
	 * @param guaranteeId
	 * @createDate 2018年1月5日
	 */
	public static void showGuaranteeRechargePre(long guaranteeId) {
		
		t_guarantee gua = guaranteeService.findByID(guaranteeId);
		
		if(gua == null) {
			showGuaranteePre();
		}
		
		render(gua);
	}
	
	/**
	 * 后台-财务-担保人管理-担保人账户网银充值
	 * @param rechargeAmt
	 * @param rrecharges
	 * @param userId
	 * @createDate 2018年1月5日
	 */
	public static void guaranteeRecharge(double rechargeAmt, int rrecharges, long userId) {
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);
		
		//保存充值记录
		ResultInfo result = userFundService.recharge(userId, businessSeqNo, rechargeAmt, "账户网银充值", Client.PC);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			showGuaranteePre();
		}

		//先校验交易密码才网银充值
		if (ConfConst.IS_TRUST) {
			 result = PaymentProxy.getInstance().ebankRechargeCheckPassword(Client.PC.code, businessSeqNo, userId, rechargeAmt, rrecharges);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				showGuaranteePre();
			}
			return ;
		}
		
		flash.error("充值成功");
		showGuaranteePre();
	}
	
	/**
	 * 后台-财务-担保人管理-添加担保人页面 
	 * @createDate 2018年1月7日
	 */
	public static void toAddBondsmanPre() {
		
		render();
	}
	
	public static void addGuarantee(String mobile) {
		
		/* 回显数据 */
		flash.put("mobile", mobile);
		
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			toAddBondsmanPre();
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号不符合规范");
			
			toAddBondsmanPre();
		}
		
		/* 判断手机号码是否存在 */
		if (!(userService.isMobileExists(mobile))) {
			flash.error("手机号码不存在");

			toAddBondsmanPre();
		}
		
		t_user_info userInfo = userInfoService.findByColumn("mobile=?", mobile);
		
		if(userInfo == null) {
			flash.error("用户信息不存在");
			toAddBondsmanPre();
		}
		
		/* 判断是否为企业账户 */
		if(userInfo.enterprise_name == null || userInfo.enterprise_name.equals("")) {
			flash.error("请添加企业账户");
			toAddBondsmanPre();
		}

		t_guarantee gua = new t_guarantee();
		gua.user_id = userInfo.user_id;
		gua.name = userInfo.enterprise_name;
		gua.save();
		
		flash.error("添加成功");
		
		showGuaranteePre();
	}
}
