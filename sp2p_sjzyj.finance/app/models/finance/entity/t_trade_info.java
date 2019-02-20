package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体：交易信息表
 *
 * @ClassName: t_trade_info

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月5日-下午2:54:03
 */
@Entity
public class t_trade_info extends Model {

	/** 数据日期 */
	public Date data_date;
	
	/** 交易日期 */
	public Date trade_date;
	
	/** 交易机构 */
	public String trade_organization_id;
	
	/** 流水号 */
	public String serial_number;
	
	/** 账号 */
	public String account;
	
	/** 交易类型 */
	//public Character tran_type;
	public Integer tran_type;
	
	/** 客户号 */
	public String customer_id;
	
	/** 客户 名称 */
	public String customer_name;
	
	/** 交易币种 */
	public String currency;
	
	/** 交易金额 */
	public  BigDecimal amount;
	
	/** 交易金额 (折人名币)*/
	public  BigDecimal amount_cny;
	
	/** 产品代码 */
	public  String product_code;

	public Date getData_date() {
		return data_date;
	}

	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}

	public Date getTrade_date() {
		return trade_date;
	}

	public void setTrade_date(Date trade_date) {
		this.trade_date = trade_date;
	}

	public String getTrade_organization_id() {
		return trade_organization_id;
	}

	public void setTrade_organization_id(String trade_organization_id) {
		this.trade_organization_id = trade_organization_id;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getTran_type() {
		return tran_type;
	}

	public void setTran_type(Integer tran_type) {
		this.tran_type = tran_type;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount_cny() {
		return amount_cny;
	}

	public void setAmount_cny(BigDecimal amount_cny) {
		this.amount_cny = amount_cny;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	
	public enum TradeType {

		INVEST(1,"11","募集"),
		PAYMENT(2,"12","兑付"),
		RELEASE(3,"21","放款"),
		RECEIVE(4,"22","还款");
		
		public int code;
		public String value;
		public String description;
		
		private TradeType (int code,String value,String description) {
			this.code = code;
			this.value = value;
			this.description = description;
		}
		
		public TradeType getEnum (int code) {
			TradeType [] type = TradeType.values();
			if (type != null && type.length > 0) {
				for (TradeType tradeType : type) {
					if (tradeType.code == code) {
						return tradeType;
					}
				}
			}
			return null;
		}
		
	}
	
}
