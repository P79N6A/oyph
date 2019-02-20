package models.app.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.enums.IsOverdue;
import common.utils.Factory;
import models.common.entity.t_user;
import services.core.BillService;
/**
 * 
 *
 * @ClassName: Bills
 *
 * @description 风控项目接口
 *
 * @author HanMeng
 * @createDate 2018年12月18日-下午2:31:56
 */
@Entity
public class Bills implements Serializable{

	protected static final BillService billService = Factory.getService(BillService.class);
	
	/**账单ID*/
	@Id
	public Long id;
	
	/**标的ID*/
	public Long bidId;
	
	/**用户ID*/
	public Long userId;
	
	/**报单编号*/
	public String entry_number;
	
	/**金额 格式:###,###,## */
	public String amount;
	
	/**还款时间*/
	public Date repayment_time;
	
	/**账单期数*/
	public Integer period;
	
	public Integer is_overdue;
	
	/**用户姓名*/
	public String realName;
	
	/**用户手机号*/
	public String mobile;
	
	/**账单总期数*/
	
	public int total_period;
	
	public int getTotal_period() {
		return total_period;
	}
	
	

	
	
}
