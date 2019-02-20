package models.entity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import payment.impl.PaymentProxy;
import play.db.jpa.Model;
import yb.enums.ServiceType;
import yb.enums.TradeType;

@Entity
public class t_payment_request extends Model {
	/** 创建时间 */
	public Date time;
	
	/** 关联用户Id */
	public long user_id;
	
	/**
	 * 托管业务订单号
	 * <br>说明：
	 * <br>①每次托管操作生成全新的业务订单号，全局唯一；
	 * <br>②托管操作需要多次请求第三方接口时，该业务订单号标识多次请求属于同一个业务操作
	 */
	public String service_order_no;
	
	/** 托管业务类型 */
	public int service_type;
	
	/**
	 * 交易订单号
	 * <br>说明：
	 * <br>①交易订单号是请求第三方接口时，需要提供给第三方的订单号，通常是全局唯一的。
	 * <br>②特殊情况：有重复交易可能性时，同一业务，请求第三方的订单号必须固定。
	 */
	public String order_no;
	
	/** 第三方托管接口交易类型 */
	public int pay_type;
	
	/** 订单状态:-1-失败;0-处理中;1-成功; */
	private int status;
	
	/** 日志补单地址，异步回调地址 */
	public String ayns_url;

	/** 接口请求提交参数(包含备注参数) */
	public String req_params;
	
	/** 托管请求唯一标识 */
	public String mark;
	
	
	public String req_map;
	
	
	public String getPay_type() {
		
		return PaymentProxy.getInstance().getInterfaceDes(this.pay_type);
	}
	
	public void setPay_type(Enum e) {
		
		this.pay_type = PaymentProxy.getInstance().getInterfaceType(e);
	}
	
	public ServiceType getService_type() {
		
		return ServiceType.getEnum(this.service_type);
	}
	
	public void setService_type(ServiceType serviceType) {
		
		this.service_type = serviceType.code;
	}
	
	public Status getStatus() {
		
		return Status.getEnum(this.status);
	}

	public void setStatus(Status status) {
		
		this.status = status.code;
	}
	
	/**
	 * 状态枚举
	 *
	 *
	 * @author niu
	 * @createDate 2017.08.24
	 */
	public enum Status {
		
		/** -2-掉单 */
		ERROR(-2, "掉单"),
		
		/** -1-失败 */
		FAILED(-1, "失败"),

		/** 0-处理中 */
		HANDING(0, "处理中"),
		
		/** 1-成功 */
		SUCCESS(1, "成功");

		public int code;
		public String value;

		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Status getEnum(int code) {
			Status[] status = Status.values();
			for (Status statu : status) {
				if (statu.code == code) {

					return statu;
				}
			}

			return null;
		}
	}
}

