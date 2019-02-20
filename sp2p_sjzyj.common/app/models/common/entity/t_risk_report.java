package models.common.entity;

import java.util.Arrays;
import java.util.Date;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Transient;

import common.enums.Marital;
import daos.common.BranchDao;
import daos.common.RiskReceptionDao;
import daos.common.RiskSuggestDao;
import models.core.entity.t_bid.RewardGrantType;
import models.core.entity.t_bid.Status;
import play.db.jpa.JPA;
import play.db.jpa.Model;
@Entity
public class t_risk_report extends Model{
		
	/**面谈时间*/
	public Date see_time;
	
	/**面谈地点*/
	public String see_address;
	
	/**申请人姓名*/
	public String app_name;
	
	/**申请人性别*/
	public String app_sex;
	
	/**申请人年龄*/
	public Integer app_age;
	
	/**申请人现住址*/
	public String app_address;
	
	/**申请人工作单位*/
	public String app_company;
	
	/**接待人员*/
	public long reception_id;
	
	@Transient
	public String receptionName;
	public String getReceptionName(){
		RiskReceptionDao receptionDao = common.utils.Factory.getDao(RiskReceptionDao.class);
		t_risk_reception rec = null;
		if(this.reception_id != 0) {
			rec = receptionDao.findByID(this.reception_id);
		}
		if(rec != null) {
			return rec.reception_name;
		}else{
			return null;
		}
	}
	
	/**申请人身份证号*/
	public String app_creditNo;
	
	/**申请人所从事的职业*/
	public String app_guild;
	
	/**申请人工作单位地址*/
	public String company_address;
	
	/**申请人工作单位电话*/
	public String company_tel;
	
	/**申请人家庭住址*/
	public String app_home_address;
	
	/**申请人联系电话*/
	public String app_phone;
	
	/**申请人婚姻状况*/
	public int app_marriage;
	
	/**申请人配偶姓名*/
	public String pair_name;
	
	/**申请人配偶联系方式*/
	public String pair_contact_way;
	
	/**申请人月收入*/
	public Double app_monthly_income;
	
	/**申请人配偶月收入*/
	public Double pair_monthly_income;
	
	/**申请人家庭年收入*/
	public Double home_annual_income;
	
	/**申请人借款用途*/
	public String loan_purpose;
	
	/**申请人借款数额*/
	public Double loan_amount;
	
	/**借款品种*/
	public String loan_kind;
	
	/**借款品种为其它的（注明借款品种）*/
	public String loan_clear_kind;
	
	/**申请人劳动力报酬*/
	public Double labor_reward;
	
	/**筹款时间(天)*/
	public Integer loan_time;
	
	/**申请人收入来源有几处*/
	public  int income_kinds;
	
	/**申请人还款来源*/
	public String back_resource;
	
	/**申请人房产条件*/
	public String house_condition;
	
	/**申请人车产条件*/
	public String vehicle_condition;
	
	/**除房车产其他固定资产*/
	public String otherfixed_condition;
	
	/**申请人担保方式*/
	public String guaranty_kind;
	
	/**保证人姓名*/
	public String guarantor_name;
	
	/**担保金额*/
	public Double guarantee_amount;
	
	/**抵押物种类(抵押方式)*/
	public String pledge_kind;
	
	/**抵押物权属*/
	public boolean pledge_ownership;
	
	/**抵押物评估价格*/
	public Double evaluate_price;
	
	/**借款期限*/
	public String loan_time_limit;
	
	/**借款利率*/
	public String annual_rate;
	
	/**是否贷过款*/
	public boolean is_loan_pass;
	
	/**履行还款情况*/
	public String backloan_condition;
	
	/**所属分公司*/
	public Long branch_id;
	
	/**企业名称*/
	@Transient
	public String enterpriseName;
	
	@Transient
	public String branch_company;
	public String getBranch_company(){
		BranchDao branchDao=common.utils.Factory.getDao(BranchDao.class);
		t_company_branch branch=null;
		if(this.branch_id!=null){
			branch=branchDao.findByID(this.branch_id);
		}
		if(branch!=null){
			return branch.branch_name;
		}else{
			return null;
		}
	}
	
	/**状态*/
	public int status; 
	
	/**借款说明*/
	public String loan_state;
	
	/**报告提交时间*/
	public Date time;
	
	/**申请人学历*/
	public String applicate;
	
	/**申请人信用卡总额度*/
	public Double credit_card_limit;
	
	/**申请人银行负债情况*/
	public String bank_debt;
	
	/**申请人民间负债情况*/
	public String folk_debt;
	
	/**申请人贷款记录*/
	public String debt_record;
	
	/**申请人征信情况*/
	public String credit_condition;
	
	/**勘察师意见*/
	public String tech_opinion;
	
	/** 用户id */
	public long user_id;
	
	/** 居住年限 */
	public String resident_year;
	
	/** 担保机构用户Id*/
	public long guarantee_user;
	
	/** 服务费费率 */
	public double service_charge;
	
	public Marital getApp_marriage() {
		Marital marital = Marital.getEnum(this.app_marriage);
		return marital;
	}

	public void setApp_marriage(Marital app_marriage) {
		
		this.app_marriage = app_marriage.code;
	}

	/**风控意见（首条）*/
	@Transient
	public String risk_opinion;
	public String getRisk_opinion(){
		String sql="select * from t_risk_suggest s where s.type=1 and s.risk_id=? order by s.time asc limit 1";
		Query qry=JPA.em().createNativeQuery(sql, t_risk_suggest.class);
		qry.setParameter(1, this.id);
		List<t_risk_suggest> suggests=qry.getResultList();
		if(suggests!=null && suggests.size()>0){
			return suggests.get(0).suggest;
		}else{
			return null;
		}
	}
	/**经理意见（末条）*/
	@Transient
	public String manager_opinion;
	/** 报单编号 */
	public String entry_number;
	
	public String getManager_opinion(){
		String sql="select * from t_risk_suggest s where s.type=2 and s.risk_id=? order by s.time desc limit 1";
		Query qry=JPA.em().createNativeQuery(sql, t_risk_suggest.class);
		qry.setParameter(1, this.id);
		List<t_risk_suggest> suggests=qry.getResultList();
		if(suggests!=null && suggests.size()>0){
			return suggests.get(0).suggest;
		}else{
			return null;
		}
	}
	
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	public Income_kinds getIncome_kinds() {
		Income_kinds income_kinds = Income_kinds.getEnum(this.income_kinds);
		
		return income_kinds;
	}
	
	public Pledge_ownership getPledge_ownership() {
		Pledge_ownership pledge_ownership = Pledge_ownership.getEnum(this.pledge_ownership);
		
		return pledge_ownership;
	}
	
	public Is_loan_pass getIs_loan_pass() {
		Is_loan_pass is_loan_pass = Is_loan_pass.getEnum(this.is_loan_pass);
		
		return is_loan_pass;
	}
	
	public enum Income_kinds {
		
		one(1,"1处"),
		two(2,"2处"),
		three(3,"3处及以上");
		
		
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private Income_kinds(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Income_kinds getEnum(int code){
			Income_kinds[] income_kinds = Income_kinds.values();
			for(Income_kinds income_kind:income_kinds){
				if(income_kind.code == code){
					
					return income_kind;
				}
			}
			
			return null;
		}
		
	}
	
  public enum Status {
		
	  	save(-1,"待完善"),
		wait(0,"待审核"),
		yet(1,"审核通过"),
		not(2,"审核未通过"),
		edit(3,"打回重申"),
		publish(4,"已发布"),
	  	invalid(5,"已作废");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private Status(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Status getEnum(int code){
			Status[] status = Status.values();
			for(Status s:status){
				if(s.code == code){
					
					return s;
				}
			}
			
			return null;
		}
		
	}
  
  	public enum Pledge_ownership {
		
		NOT(false,"非本人"),
		YES(true,"本人");
	
		/** 状态 */
		public boolean code;
		
		/** 描述 */
		public String value;
		
		private Pledge_ownership(boolean code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Pledge_ownership getEnum(boolean code){
			Pledge_ownership[] pledge_ownership = Pledge_ownership.values();
			for(Pledge_ownership ownership:pledge_ownership){
				if(ownership.code == code){	
					return ownership;
				}
			}
			return null;
		}
		
	}
  	
  	public enum Is_loan_pass {
		
		NOT(false,"否"),
		YES(true,"是");
	
		/** 状态 */
		public boolean code;
		
		/** 描述 */
		public String value;
		
		private Is_loan_pass(boolean code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Is_loan_pass getEnum(boolean code){
			Is_loan_pass[] is_loan_pass = Is_loan_pass.values();
			for(Is_loan_pass is_loan:is_loan_pass){
				if(is_loan.code == code){	
					return is_loan;
				}
			}
			return null;
		}
		
	}
  	
  	/** 申请人借款类型*/
  	public Integer bor_type;
  	
  	/** 借款地点(发生地区划代码)*/
  	public Integer site;
  	
  	/** 还款方式(（1-先息后本，2-等本等息，3-一次性还款）)*/
	public Integer repayment_type;
	
	/** 投向行业*/
	public Integer throw_industry;
	
	/**业务类型*/
	public Integer business_type;
	
}
