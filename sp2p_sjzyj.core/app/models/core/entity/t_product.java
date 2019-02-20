package models.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import yb.enums.ReturnType;

/**
 * 借款产品
 *
 * @author yaoyi
 * @createDate 2015年12月15日
 */
@Entity
public class t_product extends Model{
	
	public String img_logo_url;
	public String img_logo_resolution;
	public String img_logo_size;
	public String img_logo_format;
	
	/** 添加时间 */
	public Date time = new Date();
	
	/** 产品类型
	 * 1-普通
	 * 2-信用
	 * 3-净值 */
	private int type;
	
	/** 产品名称 */
	public String name;
	
	/** 产品图片 */
	public String image_url;
	
	/** 图片分辨率 */
	public String image_resolution;
	
	/** 文件大小 */
	public String image_size;
	
	/** 文件格式 */
	public String image_format;
	
	/** app产品图片 */
	public String image_app_url;
	
	/** app图片分辨率 */
	public String image_app_resolution;
	
	/** app文件大小 */
	public String image_app_size;
	
	/** app文件格式 */
	public String image_app_format;
	
	/** 排序时间 */
	public Date order_time;
	
	/** 借款额度下限 */
	public double min_amount;
	
	/** 借款额度上限 */
	public double max_amount;
	
	/** 年利率下限 */
	public double min_apr;
	
	/** 年利率上限 */
	public double max_apr;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	private int period_unit;
	
	/** 借款期限下限 */
	public int min_period;
	
	@Transient
	public int period_units;
	public int getPeriod_units() {
		return this.period_unit;
	}
	
	/** 借款期限上限 */
	public int max_period;
	
	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 
	 * 多个还款方式用逗号隔开*/
	public String repayment_type;
	
	/** 购买方式
	 * 1-按金额
	 * 2-按份数 */
	public int buy_type;
	
	/** 按金额购买：起投金额*/
	public double min_invest_amount;
	
	/** 拆分份数 */
	public int invest_copies;
	
	/** 保证金百分比 */
	public double bail_scale;
	
	/** 借款服务费、理财服务费、逾期罚息规则（以JSON串的形式存储） */
	public String service_fee_rule;
	
	/** 审核科目ID列表
	 * 多个科目间以逗号隔开
	 * 如：1,2,3 */
	public String audit_subjects;
	
	/** 启用标识
	 * 0-下架
	 * 1-上架 */
	public boolean is_use;
	
	public void setType(ProductType producttype){
		this.type = producttype.code;
	}
	
	public ProductType getType(){
		return ProductType.getEnum(this.type);
	}
	
	public void setPeriod_unit(PeriodUnit productPeriodUnit){
		this.period_unit = productPeriodUnit.code;
	}
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum((this.period_unit));
	}
	
	public BuyType getBuy_type() {
		BuyType buyType = BuyType.getEnum(this.buy_type);
		
		return buyType;
	}

	public void setBuy_type(BuyType buyType) {
		this.buy_type = buyType.code;
	}
	
	/**
	 * 获取当前对象的还款方式集合
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public List<ReturnType> getProductRepaymentTypeList(){
		List<ReturnType> productRepaymentType = new ArrayList<ReturnType>();
		String[] rep = this.repayment_type.split(",");
		for(String r:rep){
			productRepaymentType.add(ReturnType.getEnum(Integer.parseInt(r.trim())));
		}
		
		return productRepaymentType;
	}
	
	public t_product() {}
	
	public t_product(int type, String name, String image_url,
			String image_resolution, String image_size, String image_format,
			String image_app_url,String image_app_resolution, String image_app_size, String image_app_format,
			Date order_time, double min_amount, double max_amount,
			double min_apr, double max_apr, int period_unit, int min_period,
			int max_period, String repayment_type, int buy_type,
			double min_invest_amount, int invest_copies, double bail_scale,
			String service_fee_rule, String audit_subjects, boolean is_use,String img_logo_url,String img_logo_size,String img_logo_format,String img_logo_resolution) {
		super();
		this.type = type;
		this.name = name;
		this.image_url = image_url;
		this.image_resolution = image_resolution;
		this.image_size = image_size;
		this.image_format = image_format;
		this.image_app_url = image_app_url;
		this.image_app_resolution = image_app_resolution;
		this.image_size = image_app_size;
		this.image_app_format = image_app_format;
		this.order_time = order_time;
		this.min_amount = min_amount;
		this.max_amount = max_amount;
		this.min_apr = min_apr;
		this.max_apr = max_apr;
		this.period_unit = period_unit;
		this.min_period = min_period;
		this.max_period = max_period;
		this.repayment_type = repayment_type;
		this.buy_type = buy_type;
		this.min_invest_amount = min_invest_amount;
		this.invest_copies = invest_copies;
		this.bail_scale = bail_scale;
		this.service_fee_rule = service_fee_rule;
		this.audit_subjects = audit_subjects;
		this.is_use = is_use;
		this.img_logo_url=img_logo_url;
		this.img_logo_size=img_logo_size;
		this.img_logo_format=img_logo_format;
		this.img_logo_resolution=img_logo_resolution;
	}
	
	/**
	 * 购买方式枚举
	 *
	 * @description 
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public enum BuyType {
		/** 按金额购买 */
		PURCHASE_BY_AMOUNT(1,"按金额购买"),
		
		/** 按份数购买 */
		PURCHASE_BY_COPY(2,"按份数购买");

		public int code;
		public String value;
		
		private BuyType(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static BuyType getEnum(int code){
			BuyType[] typies = BuyType.values();
			for (BuyType type : typies) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 产品借款期限单位
	 *
	 * @description 
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public enum PeriodUnit {
		
		/** 月 */
		MONTH(2, "月"),
		
		/** 天 */
		DAY(1, "天");
		
		/** 类型 */
		public int code;
		
		/** 单位名 */
		public String value;
		
		private PeriodUnit(int code, String name){
			this.code = code;
			this.value = name;
		}
		
		public static PeriodUnit getEnum(int code){
			PeriodUnit[] incomes = PeriodUnit.values();
			for (PeriodUnit income : incomes) {
				if (income.code == code) {
					
					return income;
				}
			}
			
			return null;
		}
		
		public String getShowValue(){
			return MONTH.equals(this) ? "个"+value : value;
		}
	}
	
	/**
	 * 还款方式
	 *
	 * @description 
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public enum RepaymentType {
		
		/** 先息后本 */
		AFTER_THE_REST(1, "先息后本"),
		
		/** 等本等息 */
		AND_OTHER_INFORMATION(2, "等额本息"),
		
		/** 一次性还款 */
		LUMP_SUM_REPAYMENT(3, "一次性还款");
		
		/** 类型 */
		public int code;
		
		/** 名称 */
		public String value;
		
		private RepaymentType(int code, String name){
			this.code = code;
			this.value = name;
		}
		
		public static RepaymentType getEnum(int code){
			RepaymentType[] incomes = RepaymentType.values();
			for (RepaymentType income : incomes) {
				if (income.code == code) {
					
					return income;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 产品类型
	 *
	 * @description 
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public enum ProductType {
		
		/** 普通 */
		ORDINARY(1, "普通"),
		
		
		/** 净值 */
		WORTH(3, "净值");
		
		/** 类型 */
		public int code;
		
		/** 名称 */
		public String value;
		
		private ProductType(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static ProductType getEnum(int code){
			ProductType[] incomes = ProductType.values();
			for (ProductType income : incomes) {
				if (income.code == code) {

					return income;
				}
			}
			
			return null;
		}
	}
}


