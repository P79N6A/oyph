package common.utils;

import java.util.Date;

/**
 * 系统对象编号工具类
 * <br>编号生成规则： 前缀 + [六位时间戳yyMMdd（固定值）] + 关联对象ID
 * 
 * @author huangyunsong
 * @createDate 2016年1月4日
 */
public class NoUtil {
	
	/** 借款标的前缀:J */
	private static final String BID_PREFIX = "J";
	
	/** 借款账单编号前缀:Z */
	private static final String BILL_PREFIX = "Z";
	
	/** 理财账单编号前缀:H */
	private static final String BILL_INVEST_PREFIX = "H";
	
	/** 合同编号 */
	private static final String PACT_PREFIX = "HT";
	
	/** 时间戳格式 */
	private static final String yyMMdd = "yyMMdd";
	
	/** 债权转让项目的前缀:Q */
	private static final String DEBT_TRANSFER_PREFIX = "Q";
	
	/**
	 * 获取标的编号
	 *
	 * @param bidId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static String getBidNo(long bidId) {
		StringBuffer no = new StringBuffer(BID_PREFIX);
		no.append(bidId);
		
		return no.toString();
	}
	
	/**
	 * 获取借款账单编号
	 *
	 * @param billId
	 * @param time 账单创建的时间
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static String getBillNo(long billId, Date time) {
		StringBuffer no = new StringBuffer(BILL_PREFIX);
		no.append(DateUtil.dateToString(time, yyMMdd));
		no.append(billId);
		
		return no.toString();
	}
	/**
	 * 获取理财账单号
	 *
	 * @param billInvestId
	 * @param time 账单创建的时间
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static String getBillInvestNo(long billInvestId, Date time) {
		StringBuffer no = new StringBuffer(BILL_INVEST_PREFIX);
		no.append(DateUtil.dateToString(time, yyMMdd));
		no.append(billInvestId);
		
		return no.toString();
	}
	/**
	 * 获取合同编号
	 *
	 * @param pactId
	 * @param time 合同创建的时间
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static String getPactNo(long pactId, Date time) {
		StringBuffer no = new StringBuffer(PACT_PREFIX);
		no.append(DateUtil.dateToString(time, yyMMdd));
		no.append(pactId);
		
		return no.toString();
	}
	
	/**
	 * 
	 * 获取债权转让项目的编号
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public static String getDebtTransferNo(long debtTransferId) {
		StringBuffer no = new StringBuffer(DEBT_TRANSFER_PREFIX);
		no.append(debtTransferId);
		
		return no.toString();
	}

}
