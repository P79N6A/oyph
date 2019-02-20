package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.annotations.Nullability;

import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Client;
import common.enums.Education;
import common.enums.Gender;
import common.enums.House;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.Relationship;
import common.enums.WorkExperience;
import common.utils.DateUtil;
import models.common.entity.t_credit_level;
import models.common.entity.t_deal_user.DealType;
import models.common.entity.t_user_info.MemberType;
/***
 * 借款人信息和标信息实体
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-6
 */
@Entity
public class BidUserInfoBean implements Serializable {


	/** 真实姓名 */
	public String realityName;
    @Id
	/** 身份证号码 */
	public String idNumber;
	

	/** 信用等级 */
	public String creditLevelId;


	
	public  int sex;
	/** 性别(枚举)： 1 男 2 女 3 保密 */
	public String getSex() {
		Gender gender = Gender.getEnum(sex);
		return gender == null ? null : gender.name;
	}



	
	public int education;
	/** 教育情况 (枚举)*/
	public String getEducation() {
		Education education = Education.getEnum(this.education);
		return education==null ? null : education.value;
	}

	public int marital;
	/** 婚姻状况  (枚举)*/
	public String getMarital() {
		Marital marital = Marital.getEnum(this.marital);
		return marital==null ? null : marital.value;
	}

	
	public int annualIncome;
	/** 年收入 (枚举) */
	public String getAnnualIncome() {
		AnnualIncome annualIncom = AnnualIncome.getEnum(this.annualIncome);
		return annualIncom == null ? null : annualIncom.value;
	}


	public int netAsset;
	/** 资产估值 (枚举) */
	public String getNetAsset() {
		NetAssets assets = NetAssets.getEnum(this.netAsset);
		return assets == null ? null : assets.value;
	}

	public int workExperience;
	/** 工作经验  (枚举)*/
	public String getWorkExperience() {
		WorkExperience workExperience = WorkExperience.getEnum(this.workExperience);
		return workExperience == null ? null : workExperience.value;
	}

	
	public int house;
	/** 房贷情况 (枚举) */
	public String getHouse() {
		House house = House.getEnum(this.house);
		return house == null ? null : house.value;
	}

	public int car;
	/** 车贷情况  (枚举)*/
	public String getCar() {
		Car car = Car.getEnum(this.car);
		return car == null ? null : car.value;
	}

	
	/** 借款描述  */
	public String description;
   
	
	/** 风控描述  */
	public String preauditSuggest;

	@Transient
	public int age;
	
	/** 年龄 */
	public int getAge(){
		
		if (StringUtils.isBlank(this.idNumber)) {
			
			return 0;
		}
		Date birth = DateUtil.strToDate(this.idNumber.substring(6, 14), "yyyyMMdd");
		int age = DateUtil.getAge(birth);
		return age ;
	}
	
	/** 企业名称  */
	public String enterpriseName;
	
    
    
    
    
}
