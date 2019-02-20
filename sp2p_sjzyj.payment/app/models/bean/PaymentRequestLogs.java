package models.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.entity.t_payment_request;
import payment.impl.PaymentProxy;
import yb.enums.ServiceType;

@Entity
public class PaymentRequestLogs {
	
	@Id
	public long id;
	
	/** 请求记录唯一标识 */
	public String mark;
	
	/** 创建时间 */
	public Date time;
	
	/** 关联用户名称 */
	public String user_name;
	
	/**
	 * 托管业务订单号
	 * <br>说明：
	 * <br>①每次托管操作生成全新的业务订单号，全局唯一；
	 * <br>②托管操作需要多次请求第三方接口时，该业务订单号标识多次请求属于同一个业务操作
	 */
	public String service_order_no;
	
	/** 托管业务类型 */
	private int service_type;
	
	/**
	 * 交易订单号
	 * <br>说明：
	 * <br>①交易订单号是请求第三方接口时，需要提供给第三方的订单号，通常是全局唯一的。
	 * <br>②特殊情况：有重复交易可能性时，同一业务，请求第三方的订单号必须固定。
	 */
	public String order_no;
	
	/** 第三方托管接口交易类型 */
	private int pay_type;
	
	/** 订单状态:-1-失败;0-处理中;1-成功; */
	private int status;
	
	public String getPay_type() {
		
		return PaymentProxy.getInstance().getInterfaceDes(this.pay_type);
	}

	public ServiceType getService_type() {
		
		return ServiceType.getEnum(this.service_type);
	}

	public t_payment_request.Status getStatus() {
		
		return t_payment_request.Status.getEnum(this.status);
	}
	
}

