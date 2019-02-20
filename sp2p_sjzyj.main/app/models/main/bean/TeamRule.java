package models.main.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JsonConfig;
import common.utils.Factory;
import daos.common.SettingDao;
import models.common.entity.t_setting_platform;

public class TeamRule {
	public int minAmount;
	public int maxAmount;
	public double amount;
    protected static final SettingDao settingDao = Factory.getDao(SettingDao.class);
	/**
	 * 查询cps推广金额
	 * @param investAmount
	 * @return
	 */
	public static double queryAmount(double investAmount ){
		t_setting_platform setting = settingDao.findByColumn("_key=?", "customer_commission");
		List<TeamRule> lists =new ArrayList<TeamRule>();
		 if(setting._value !=""){
			 JSONArray jsonArray  = null;
			 try {
				 jsonArray =  JSONArray.fromObject(setting._value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 lists = (List)JSONArray.toList(jsonArray, new TeamRule(), new JsonConfig());  
		 }
		 double amount = 0;
		 for (int i = 0; i < lists.size(); i++) {
			 TeamRule  cpsRule= lists.get(i);
			 
			 if(cpsRule.maxAmount*10000>=investAmount&&investAmount>=cpsRule.minAmount*10000){
				 amount = investAmount*cpsRule.amount/100;
				 break;
			 }else if( i ==(lists.size()-1)){
				 amount = investAmount*cpsRule.amount/100;
			 }
		}
		return amount;
	}
	
	
    /**
     * 查询规则的金额
     * @param investAmount
     * @param rules
     * @return
     */
	public static double queryAmount(double investAmount,String rules){
		List<TeamRule> lists =new ArrayList<TeamRule>();
		
		 if(rules !=""){
			 JSONArray jsonArray  = null;
			 try {
				 jsonArray =  JSONArray.fromObject(rules);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 lists = (List)JSONArray.toList(jsonArray, new TeamRule(), new JsonConfig());  
		 }
		 double amount = 0;
		 for (int i = 0; i < lists.size(); i++) {
			 TeamRule  cpsRule= lists.get(i);
			 
			 if(cpsRule.maxAmount*10000>=investAmount&&investAmount>=cpsRule.minAmount*10000){
				 amount = investAmount*cpsRule.amount/100;
				 break;
			 }else if( i ==(lists.size()-1)){
				 amount = investAmount*cpsRule.amount/100;
			 }
		}
		return amount;
	}
	
	/*
	*//**
	 * 计算金额
	 * @param invest_id
	 * @param error
	 * @return
	 *//*
	public static  double calculationAmount(long invest_id,double apr,ErrorInfo error){
		t_invests invests = t_invests.findById(invest_id);
		Bid bid = new Bid();
		bid.id = invests.bid_id;
		double decimal = 0.0;
		double day = 0.0;
		switch(bid.periodUnit){
		case Constants.YEAR: day = bid.period*30.0*12.0/360.0; break;
		case Constants.MONTH: day = bid.period*30.0/360; break;
		case Constants.DAY: day= bid.period/360.0; break;
	}
		decimal = invests.amount*day*apr/100.0;
		return decimal;
	}
	
	*//**
	 * 提供规则计算金额
	 * @param investAmount
	 * @param rules
	 * @return
	 *//*
	public static  BigDecimal queryAmount(BigDecimal investAmount,String rules){
		BackstageSet backstageSet = BackstageSet.getCurrentBackstageSet();
		List<CpsRule> lists =new ArrayList<CpsRule>();
		 if(backstageSet.cpsRule !=""){
			 JSONArray jsonArray  = null;
			 try {
				 jsonArray =  JSONArray.fromObject(rules);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 lists = (List)JSONArray.toList(jsonArray, new CpsRule(), new JsonConfig());  
		 }
		 BigDecimal amount = new BigDecimal(0);
		 for (int i = 0; i < lists.size(); i++) {
			 CpsRule  cpsRule= lists.get(i);
			 
			 if(cpsRule.max_amount.doubleValue()>=investAmount.doubleValue()&&investAmount.doubleValue()>=cpsRule.min_amount.doubleValue()){
				 amount = investAmount.multiply(cpsRule.amount).divide(new BigDecimal(100));
				 break;
			 }else if( i ==(lists.size()-1)){
				 amount = investAmount.multiply(cpsRule.amount).divide(new BigDecimal(100));
			 }
		}
		return amount;
	}*/
	
	
}
