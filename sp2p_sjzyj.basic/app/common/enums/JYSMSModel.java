package common.enums;

public enum JYSMSModel {

	MODEL_REGISTER_SUCC(1,"2192","注册成功","user_name",false),
	MODEL_ACCOUTN_OPENING_SUCC(2,"2195","开户成功","user_name",false),
	MODEL_BIND_MOBILE_SUCC(3,"2198","绑定手机成功","user_name",false),
	MODEL_SEND_CODE(4,"2201","发送验证码","code,time",true),//
	MODEL_ADD_BANKCARD_SUCC(5,"2207","添加银行卡成功","user_name",false),
	MODEL_RESET_PASSWORD_SUCC(6,"2210","重置密码成功","user_name",true),
	MODEL_RECHARGE_SUCCESS(7,"2213","充值成功","user_name,amount,balance",true),//
	MODEL_WITHDRAW(8,"2216","提现成功","user_name,withdraw_amount,fee,actual_amount,balance",true),//
	MODEL_SUBJECT_AUTID_PASS(9,"2234","科目审核通过","user_name,subject_name",true),//
	MODEL_SUBJECT_AUTID_REJECT(10,"2237","科目审核不通过","user_name,subject_name",true),
	MODEL11(11,"2258","账单到期提醒","user_name,bill_no,repayment_time,amount",true),
	MODEL_REPAYMENT_SUCC(12,"2261","还款成功","user_name,bill_no,amount",true),
	MODEL13(13,"2270","平台奖励兑换成功","user_name,amount,balance",true),
	MODEL14(14,"2273","注册成功","user_name,debt_no,title",true),
	MODEL15(15,"2279","项目初审不通过","user_name,debt_no,title",true),
	MODEL16(16,"2282","转让成功(转让人)","user_name,debt_no,title,amount,fee,balance",true),
	MODEL17(17,"2285","转让成功(受让人)","user_name,debt_no,title,amount",true),
	MODEL18(18,"2288","转让失败","user_name,debt_no,title",true),
	MODEL19(19,"2291","获得体验金","user_name,amount",true),
	MODEL20(20,"2294","购买体验标成功","user_name,exper_no,exper_name,amount,period,apr",true),
	MODEL21(21,"2297","体验项目回款","user_name,exper_no,exper_name,amount",true),
	MODEL22(22,"2300","获得资金托管开户红包","user_name,amount",true),
	MODEL23(23,"2303","获得实名认证红包","user_name,amount",true),
	MODEL24(24,"2306","获得绑定邮箱红包","user_name,amount",true),
	MODEL25(25,"2309","获得绑定银行卡红包","user_name,amount",true),
	MODEL26(26,"2312","获得首次充值红包","user_name,amount",true),
	MODEL27(27,"2315","获得首次购买红包","user_name,amount",true),
	MODEL28(28,"2318","CPS推广成功","spreader_name,user_name",true),
	MODEL29(29,"2321","获得CPS推广返佣","user_name,amount",true),
	MODEL30(30,"2324","财富圈邀请成功","spreader_name,user_name",true),
	MODEL31(31,"2327","获得财富圈返佣","spreader_name,amount",true),
	MODEL32(32,"2330","获得财富圈邀请码","user_name",true),
	MODEL_TRANSFER_APPLY_NOTICE(33,"2333","客户申请转让通知","user_name,debt_no",true),//
	MODEL34(34,"2336","使用红包投标失败","user_name,bid_no,bid_name,invest_amount,red_amount,pay_amount",true),
	MODEL35(35,"2339","获得理财红包","user_name,amount",true),
	MODEL36(36,"2345","获得理财加息券","user_name,rate",true),
	MODEL_INVEST_SUCC(37,"2348","购买成功","user_name,bid_no,bid_name,amount,period_num,period_unit,apr,repayment_type",true),//
	MODEL_INVEST_SECTION(38,"2351","投资回款","user_name,bid_no,bid_name,bill_no,amount,principal,interest,fee,balance",true),//
	MODEL_INVEST_FAIL(39,"2354","投资失败","user_name,bid_no,bid_name,amount",true),//
	MODEL_BID_APPLAY_SUCC(40,"2357","借款申请成功","user_name,bid_no,bid_name",true),//
	MODEL41(41,"2360","冻结借款保证金","user_name,bid_no,bid_name,bail",true),
	MODEL_BID_PREAUTID_PASS(42,"2363","项目初审通过","user_name,bid_no,bid_name",true),//
	MODEL_BID_PREAUTID_REJECT(43,"2366","项目初审不通过","user_name,bid_no,bid_name",true),//
	MODEL44(44,"2369","项目复审通过","user_name,bid_no,bid_name",true),
	MODEL45(45,"2372","项目复审不通过","user_name,bid_no,bid_name",true),
	MODEL46(46,"2375","借款满标","user_name,bid_no,bid_name",true),
	MODEL_BID_INTEREST(47,"2378","借款计息","user_name,bid_no,bid_name,amount,period_num,period_unit,repayment_type,loan_fee,balance",true),//
	MODEL48(48,"2381","借款流标","user_name,bid_no,bid_name",true),
	MODEL49(49,"2384","解冻借款保证金","user_name,bid_no,bid_name",true),
	MODEL_DEBT_APPLY_SUCC(50,"3335","转让申请成功发消息给用户","user_name,debt_no,title",true),//
	MODEL_INVEST_INTEREST(51,"2453","投资计息","user_name,bid_no,bid_name,amount,period_num,period_unit,apr,repayment_type",true),//
	MODEL52(52,"3371","债权转让","alienator,alienatorName,date,assignee,bid_no",true),//
	MODEL_BILLS_DUE(53,"3447","账单到期通知(财务/风控)","bid",true),
	MODEL_JD_ECARD(54,"3471","京东E卡通知(中奖用户)","user_name,denomination,jd_number,jd_password",true);
	/** 编号 */
	public int code;
	
	/** 焦云平台模板id */
	public String tplId;
	
	/** 使用场景描述 */
	public String senceDescription;
	
	public String value;
	
	/** 是否发送短息 */
	public boolean flag;
	
	private JYSMSModel(int code,String tplId,String senceDescription,String value,boolean flag) {
		this.code = code;
		this.tplId = tplId;
		this.senceDescription = senceDescription;
		this.value = value;
		this.flag = flag;
	}
	
	public static JYSMSModel getEnum (int code) {
		JYSMSModel[] model = JYSMSModel.values();
		for (JYSMSModel jysmsModel : model) {
			if (jysmsModel.code == code) {
				return jysmsModel;
			}
		}
		return null;
	}
	
	public JYSMSModel getEnum (String tplId) {
		JYSMSModel[] model = JYSMSModel.values();
		for (JYSMSModel jysmsModel : model) {
			if (jysmsModel.tplId.equals(tplId)) {
				return jysmsModel;
			}
		}
		return null;
	}
}
