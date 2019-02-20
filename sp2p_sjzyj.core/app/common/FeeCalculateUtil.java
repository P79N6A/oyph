package common;

import java.math.BigDecimal;

import models.core.entity.t_product;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import services.common.SettingService;
import yb.enums.ReturnType;

import com.shove.Convert;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.number.Arith;

/**
 * 费用计算
 * 
 * @author yaoyi
 * @createDate 2015年12月18日
 */
public class FeeCalculateUtil {
	
	private static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 计算保证金
	 *
	 * @param amount
	 * @param bail_scale
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月19日
	 */
	public static double loanBailFee(double amount, double bail_scale){
		double value = 0.00;
		
		if(amount < 0.01 || bail_scale < 0){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("计算保证金时，发现参数不合法");
			}
			
			value = -1;
		}
		
		BigDecimal calculate_value = new BigDecimal(Double.toString(amount))
				.multiply(new BigDecimal(Double.toString(bail_scale)).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP));
		value = calculate_value.doubleValue();
		
		return value;
	}

	/**
	 * 借款管理费计算<br>
	 * 超过本金的50%将按50%计算
	 *
	 * @param amount
	 * @param period_unit
	 * @param period
	 * @param service_fee_rule
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月19日
	 */
	public static double loanServiceFee(double amount, int period_unit, int period, String service_fee_rule){
		double value = 0.00;
		
		if(amount < 0.01 || StringUtils.isBlank(service_fee_rule) || period <= 0){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("计算保证金时，发现参数不合法");
			}
			
			value = -1;
		}
		
		String loan_amount_rate = "";
		int sub_period = 0;
		String sub_loan_amount_rate = "";
		BigDecimal borrow_manage_maxrate = new BigDecimal(Double.toString(Constants.LOAN_SERVICE_FEE_MAXRATE)).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);
		
		if(period_unit == t_product.PeriodUnit.MONTH.code){//月标
			try{
				
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getString(Constants.LOAN_AMOUNT_RATE);
				sub_period = jsobj.getInt(Constants.SUB_PERIOD);
				sub_loan_amount_rate = jsobj.getString(Constants.SUB_LOAN_AMOUNT_RATE);
				
			}catch(JSONException e){
				Logger.error(e, "拆解借款产品的json对象时，出现异常！json:%s", service_fee_rule);
				
				value = -1;
			}
			/** 公式：按借款本金 * ? % + 本金*（期数 - ?个月）* ? % */
			BigDecimal amount_bigdecimal = new BigDecimal(Double.toString(amount));//本金
			BigDecimal rate_left = new BigDecimal(loan_amount_rate).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);//左边的百分比
			BigDecimal rate_right = new BigDecimal(sub_loan_amount_rate).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);//右边的百分比
			
			BigDecimal calculate_value = amount_bigdecimal.multiply(rate_left);
			if(period > sub_period){
				calculate_value = calculate_value.add(amount_bigdecimal.multiply(new BigDecimal(period-sub_period)).multiply(rate_right));
			}
			
			/** 借款金额最大50% */
			BigDecimal max_value = amount_bigdecimal.multiply(borrow_manage_maxrate);
			
			value = calculate_value.compareTo(max_value)==1 ? max_value.doubleValue() : calculate_value.doubleValue();
			
		}else if(period_unit == t_product.PeriodUnit.DAY.code){//天标
			try{
				
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getString(Constants.LOAN_AMOUNT_RATE);
				
			}catch(JSONException e){
				Logger.error(e, "拆解借款产品的json对象时，出现异常！json:%s", service_fee_rule);
				
				value = -1;
			}
			/** 公式：按借款本金 * ?% /360 * 借款天数 */
			
			BigDecimal amount_bigdecimal = new BigDecimal(Double.toString(amount));//本金
			BigDecimal max_value = amount_bigdecimal.multiply(borrow_manage_maxrate);//最大借款服务费
			
			BigDecimal rate = new BigDecimal(loan_amount_rate).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);//费率
			BigDecimal calculate_value = amount_bigdecimal.multiply(rate)
					.divide(new BigDecimal(Constants.DAY_INTEREST), 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(period));
			
			/** 百分比:如果超过50%则取50% */
			
			value = Arith.round(calculate_value.compareTo(max_value)==1 ? max_value.doubleValue() : calculate_value.doubleValue(), 2);
			
		}else{
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("计算保证金时，发现参数不合法");
			}
			
			value = -1;
		}
		
		return value;
	}
	
	/**
	 * 计算理财管理费(实际收取的，包含折扣)
	 *
	 * @param receiveInterest 本期应收利息
	 * @param serviceFeeRule 借款项目服务费规则
	 * @param userId 用户id
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
 	public static double getInvestManagerFee(double receiveInterest, String serviceFeeRule,Long userId){
 		JSONObject rule = JSONObject.fromObject(serviceFeeRule);
 		
 		double serviceFeeRate = rule.getDouble(Constants.INVEST_AMOUNT_RATE);
 		
 		double serviceFee = Arith.div(Arith.mul(receiveInterest, serviceFeeRate), 100.0, 2);  
 		
 		services.common.UserFundService userFundService = Factory.getService(services.common.UserFundService.class);
 		
 		double discount = userFundService.findUserDiscount(userId);
 		
 		serviceFee = Arith.div(Arith.mul(serviceFee, discount), 10, 2);
 		
		return serviceFee;
 	}
 	
 	/**
 	 * 计算理财管理费(原始的理财服务费，不包含折扣)
 	 * 
 	 * 
 	 * @param receiveInterest 本期应收利息
	 * @param serviceFeeRule 借款项目服务费规则
 	 * @return
 	 *
 	 * @author liudong
 	 * @createDate 2016年4月13日
 	 *
 	 */
 	public static double getOriginalInvestManagerFee(double receiveInterest, String serviceFeeRule){
 		JSONObject rule = JSONObject.fromObject(serviceFeeRule);
 		
 		double serviceFeeRate = rule.getDouble(Constants.INVEST_AMOUNT_RATE);
 		
 		double serviceFee = Arith.divUp(Arith.mul(receiveInterest, serviceFeeRate), 100.0, 2);  
 		
		return serviceFee;
 	}
 	
 	/**
 	 * 计算提现手续费
 	 *
 	 * @param withdrawalAmt
 	 * @return
 	 *
 	 * @author huangyunsong
 	 * @createDate 2016年1月11日
 	 */
 	public static double getWithdrawalFee(double withdrawalAmt) {
 		double point = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_POINT), 100.00);//100元
 		double rate = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_RATE), 1.00);//5   万分之5
 		double min = Convert.strToDouble(settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_MIN), 2.00);//最低两块钱
 		
 		//最高提现手续费费
 		//double max = Arith.div(withdrawalAmt*Constants.WITHDRAW_MAXRATE, 100, 2);
 		
 		double withdrawalFee = 0;
 		
 		if (withdrawalAmt > point) {
 			withdrawalFee = Arith.round(min+(withdrawalAmt-point)*rate/10000, 2);
 		} else {
 			withdrawalFee = Arith.round(min, 2);
 		}

 		//return withdrawalFee>max?max:withdrawalFee;
 		return withdrawalFee;
 	}
 	
 	/**
 	 * 计算标的的总利息，用于页面回显
 	 *
 	 * @param amount
 	 * @param apr
 	 * @param period
 	 * @param period_unit
 	 * @return
 	 *
 	 * @author yaoyi
 	 * @createDate 2016年1月12日
 	 */
 	public static double getLoanSumInterest(double amount, double apr, int period, PeriodUnit period_unit, ReturnType repayment_type){
 		
 		if(PeriodUnit.MONTH.equals(period_unit)){//月标
 			double interest = 0;
 			//月标，等额本息的计算
 			if(models.core.entity.t_product.RepaymentType.AND_OTHER_INFORMATION.equals(repayment_type)){
				double monthApr = Arith.div(Arith.div(apr, 100, 10), 12, 10);
				double monthSum = Arith.div(Arith.mul(Arith.mul(amount, monthApr), Math.pow((1 + monthApr), period)), (Math.pow((1 + monthApr), period) - 1), 10); 
				interest = (monthSum * period - amount);
			}else{
				interest = Arith.round(Arith.mul(Arith.div(Arith.mul(amount, Arith.div(apr, 100, 10)), 12, 10), period), 2);
			}
 			return Arith.round(interest, 2);
 		}else{
 			return Arith.round(Arith.mul(Arith.div(Arith.mul(amount, Arith.div(apr, 100, 10)), Constants.DAY_INTEREST, 10), period), 2);
 		}
 	}
 	
 	/**
 	 * 计算标的总共要收取的理财服务费，只用于后台页面的显示
 	 *
 	 * @param interest 利息
 	 * @return
 	 *
 	 * @author yaoyi
 	 * @createDate 2016年2月25日
 	 */
 	public static double getSumBidInvestServiceFee(double interest, String service_fee_rule){
 		
		JSONObject  fee_rules = JSONObject.fromObject(service_fee_rule);
		double invest_amount_rate = fee_rules.getDouble(Constants.INVEST_AMOUNT_RATE);
 
 		return Arith.round(Arith.mul(interest, Arith.div(invest_amount_rate, 100, 10)), 2);
 	}
}
