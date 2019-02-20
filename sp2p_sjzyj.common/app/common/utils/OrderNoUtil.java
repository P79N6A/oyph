package common.utils;

import java.util.Date;
import java.util.UUID;

import models.common.entity.t_sequence_bid;
import models.common.entity.t_sequence_order_no;
import yb.enums.ServiceType;


/**
 * 资金托管订单号、编号工具类
 * <br>订单号生成规则：
 * <br>时间戳（yyMMdd六位） + 操作代号（两位） + 特殊位（一位）+ 数据库表自增ID
 * 
 * <br>标的编号生成规则：
 * <br>时间戳（yyMMdd六位） + sequenceId
 *
 * @author huangyunsong
 * @createDate 2016年1月4日
 */
public class OrderNoUtil {
	
	/** 时间戳格式 */
	private static final String yyMMdd = "yyMMdd";
	
	/** 常规订单号  */
	private static final String SPECIAL_NORMAL = "0";
	
	/** 借款账单订单号  */
	public static final String SPECIAL_BILL = "1";
	
	/** 理财账单订单号  */
	public static final String SPECIAL_BILL_INVEST = "2";
	
	/** 奖励兑换订单号 */
	public static final String SPECIAL_CONVERSION = "3";
	
	private static final String PLATFORM_FLAG = "oyph_";
	
	/**
	 * 获取客户端流水号
	 *
	 * @author niu
	 * @create 2017.09.14
	 */
	public static String getClientSn() {
		return PLATFORM_FLAG + UUID.randomUUID().toString();
	}
	
	/**
	 *  获得订单号（常规订单号）。
	 *  <br>规则：当前时间戳（yyMMdd六位） + 操作代号（两位） + 特殊位（固定：SPECIAL_NORMAL）+ sequenceId
	 *
	 * @param operatin
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月5日
	 */
	public static String getNormalOrderNo(ServiceType serviceType) {
		StringBuffer orderNo = new StringBuffer();
		orderNo.append(PLATFORM_FLAG);
		orderNo.append(DateUtil.dateToString(new Date(), yyMMdd));
		orderNo.append(serviceType.code);
		orderNo.append(SPECIAL_NORMAL);
		orderNo.append(getOrderSequence());
		
		return orderNo.toString();
	}
	
	/**
	 * 获得交易订单号(特殊订单号)。
	 * <br>规则：关联对象创建的时间（yyMMdd六位）+ 操作代号（两位） + 特殊位（一位）+ 关联对象id
	 * <br>说明：
	 * <br>对同一关联对象重复做同一操作时，获得的交易订单号是相同的。
	 *
	 * @param referTime 关联对象创建的时间
	 * @param operatin 操作类型枚举
	 * @param referId 关联对象ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月4日
	 */
	public static String getSpecialOrderNo(Date referTime, ServiceType serviceType, String special, long referId) {
		StringBuffer orderNo = new StringBuffer();
		orderNo.append(DateUtil.dateToString(referTime, yyMMdd));
		orderNo.append(serviceType.code);
		orderNo.append(special);
		orderNo.append(referId);
		
		return orderNo.toString();
	}
	
	/**
	 * 获得标的编号。
	 * <br>规则：当前时间戳（yyMMdd六位）+ sequenceId
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public static String getBidNo() {
		StringBuffer orderNo = new StringBuffer();
		orderNo.append(DateUtil.dateToString(new Date(), yyMMdd));
		orderNo.append(getOrderSequence());
		
		return orderNo.toString();
	}
	
	/**
	 * 生成订单号的sequence
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月5日
	 */
	private static long getOrderSequence() {
		t_sequence_order_no sequence = new t_sequence_order_no();
		sequence.save();  //不做异常处理，发生异常直接中断程序

		return sequence.id;
	}

}
