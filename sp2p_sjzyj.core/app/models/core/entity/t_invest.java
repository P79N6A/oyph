package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import common.enums.Client;
import common.utils.number.NumberFormat;

/**
 * 
 *投资表
 *
 * @author liudong
 * @createDate 2015年12月15日
 */
@Entity
public class t_invest extends Model {

	/** 用户Id(投资人) */
	public Long user_id;
	
	/** 投资时间 */
	public Date time;
	
	/** 借款标ID */
	public long bid_id;
	
	/** 投资金额  */
	public double amount;

	/** 纠正本金 */
	public double correct_amount;
	
	/** 纠偏利息 */
	public double correct_interest;
	
	/** 分摊到每一笔投资的借款服务费 */
	public double loan_service_fee_divide;
	
	/** 投资入口*/
	private int client;
	
	/** 债权转让状态:0 正常(转让入的也是0) -1 已转让出 1 转让中 */
	private int transfer_status;
	
	/** 债权ID（申请完成之后的债权id）*/
	public long debt_id;//
	
	/** 业务订单号,投标操作唯一标识 */
	public String service_order_no;
	
	/** 交易订单号。托管方产生 */
	public String trust_order_no;
	
	/** 投标方式: 1pc 2自动 3android 4ios 5wechat*/
	private int invest_type;
	
	/**
	 * 红包投资金额
	 */
	public double red_packet_amount ;
	/**
	 * 红包ID
	 */
	public long red_packet_id ;
	
	public long rate_ticket_id;
	
	/** 中文标示 */
	@Transient
	public double cmount;
	
	public String getCmount() {
		String cmounts = NumberFormat.financeCN(amount);
		
		return cmounts;
	}
	
	public long getRate_ticket_id() {
		return rate_ticket_id;
	}

	public void setRate_ticket_id(long rate_ticket_id) {
		this.rate_ticket_id = rate_ticket_id;
	}

	public InvestType getInvest_type() {
		InvestType invest_type = InvestType.getEnum(this.invest_type);
		
		return invest_type;
	}

	public void setInvest_type(InvestType invest_type) {
		this.invest_type = invest_type.code;
	}

	public Client getClient() {
		Client client = Client.getEnum(this.client);
		
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}
	
	public TransferStatus getTransfer_status() {
		return TransferStatus.getEnum(this.transfer_status);
	}

	public void setTransfer_status(TransferStatus transfer_status) {
		this.transfer_status = transfer_status.code;
	}



	/**
	 * 枚举类型:转让状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月8日
	 */
	public enum TransferStatus {
		
		/** 正常:0 */
		NORMAL(0,"正常"),
		
		/** 已转让:-1 */
		TRANSFERED(-1,"已转让"),
		
		/** 转让中:1 */
		TRANSFERING(1,"转让中");
		
		public int code;
		public String value;
		private TransferStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		/**
		 * 根据code取得客户端类型,没有找到返回null
		 *
		 * @param code
		 * @return
		 *
		 * @author DaiZhengmiao
		 * @createDate 2015年12月8日
		 */
		public static TransferStatus getEnum(int code){
			TransferStatus[] clients = TransferStatus.values();
			for (TransferStatus cli : clients) {
				if (cli.code == code) {

					return cli;
				}
			}
			
			return null;
		}
	}
	
	/** 
	 * 枚举类型:投标方式
	 * 
	 * @description 
	 * 
	 * @author ZhouYuanZeng 
	 * @createDate 2016年3月29日 下午2:32:18  
	 */
	public enum InvestType {
		/** PC:1 */
		PC(1,"PC","Web端","/public/front/images/pc.png"),
		
		/** 自动:2 */
		AUTO(2,"自动","自动","/public/common/investtype-auto.png"),
		
		/** 安卓:3 */
		ANDROID(3,"Android","安卓客户端","/public/front/images/Android-pic.png"),
		
		/** 安卓:4 */
		IOS(4,"IOS","IOS客户端","/public/front/images/ios-pic.png"),
		
		/** 微信:5 */
		WECHAT(5,"WECHAT","微信端","/public/common/investtype-wechat.png");
		
		public int code;
		public String value;
		public String description;
		public String path;
		
		private InvestType(int code, String value, String description, String path) {
			this.code = code;
			this.value = value;
			this.description = description;
			this.path = path;
		}
		
		/**
		 * 根据code取得投标方式,没有找到返回null
		 *
		 * @param code
		 * @return
		 *
		 * @author ZhouYuanZeng
		 * @createDate 2016年3月29日
		 */
		public static InvestType getEnum(int code){
			InvestType[] investTypes = InvestType.values();
			for (InvestType type : investTypes) {
				if (type.code == code) {

					return type;
				}
			}
			
			return PC;
		}
	}
}
