package models.common.entity;


import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体类: 官方公告模板
 * @author guoShiJie
 * @createDate 2018年5月8日
 * */
@Entity
public class t_template_official_notice extends Model{
	
	/**创建时间*/
	public Date time;
	
	/**栏目key值*/
	public String column_key;
	
	/**栏目名称*/
	public String column_name;
	
	/**标题*/
	public String title;
	
	/**内容*/
	public String content;
	 
	/**关键词*/
	public String key_words;
	 
	/**上传图片路径*/
	public String image_url;
	 
	/**图片分辨率*/
	public String image_resolution;
	 
	/**图片大小*/
	public String image_size;
	 
	/**图片格式*/
	public String image_format;
	 
	/**发布时间*/
	public Date release_time;
	 
	/**排序时间*/
	public Date sort_time;
	 
	/**来源*/
	public String source_from;
	
	/**发标时间(yyyy年MM月dd日)*/
	public String date;
	
	/**发标时间key值*/
	public String saleBidTimeKey;
	
	/**发标时间(HH:mm)*/
	public String saleBidTimeValue;
	
	/**项目名称key值*/
	public String projectNameKey;
	
	/**项目期限Valuez值*/
	public String projectNameValue;
	
	/**项目期限key值*/
	public String projectTimeLimitKey;
	
	/**项目期限Value值*/
	public String projectTimeLimitValue;
	
	/**发标金额Key值*/
	public String saleBidAmountKey;
	
	/**发标金额Value值*/
	public String saleBidAmountValue;
	
	/**年化利率key值*/
	public String annualConvertKey;
	
	/**年化利率value值*/
	public String annualConvertValue;
	
	/**月还利息key值*/
	public String monthInterestKey;
	
	/**月还利息value值*/
	public String monthInterestValue;
	
	/**借款类型key值*/
	public String borrowingTypeKey;
	
	/**借款类型value值*/
	public String borrowingTypeValue;
	
	/**还款方式key值*/
	public String repaymentMethodKey;
	
	/**还款方式value值*/
	public String repaymentMethodValue;
	
	/**上午；下午*/
	public String amOrPm;
	
	/**借款说明*/
	public String loanDescription;
	
	/**段落1*/
	public String fixedContent1;
	
	/**段落2*/
	public String fixedContent2;
	
	/**段落3*/
	public String fixedContent3;
	
	/**段落4*/
	public String fixedContent4;
	
	/**段落5*/
	public String fixedContent5;
	
	/**段落6*/
	public String fixedContent6;
	
	/**当前时间*/
	public String currentDate;
}
