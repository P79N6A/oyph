package common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息场景
 *
 * @description 1.maskable表示是否可以设置成拒收(例如充值成功，设置成拒收后会在t_notice_setting_user中添加一条数据,以后对该场景的消息会根据该记录进行发送),<br>
 * 				false表示不能拒收，此时程序会根据枚举中的sms,msg,email值判断这几个消息是否发送<br>
 * 				2.体验标和红包的相关消息设置暂时不管，在涉及该模块时要做单独处理<br>
 * 				3.平台邮件和平台的广告是没有模板的，会使用单独的方法进行消息的发送
 * 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
public enum NoticeScene {

	/**
	 * <b>code:</b>				1<br>
	 * <b>scene(title):</b>		注册成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您注册成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您注册成功。<br>
	 */
	REGISTER_SUCC(1,"注册成功",false,false,true,false),
	
	/**
	 * <b>code:</b>				2<br>
	 * <b>scene(title):</b>		开户成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您开户成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您开户成功。<br>
	 */
	ACCOUTN_OPENING_SUCC(2,"开户成功",false,false,true,false),

	/**
	 * <b>code:</b>				3<br>
	 * <b>scene(title):</b>		绑定手机成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您绑定手机成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您绑定手机成功。<br>
	 */
	BIND_MOBILE_SUCC(3,"绑定手机成功",false,false,true,false),
	
	/**
	 * <b>code:</b>				4<br>
	 * <b>scene(title):</b>		绑定邮箱成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您绑定邮箱成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您绑定邮箱成功。<br>
	 */
	BIND_EMAIL_SUCC(4,"绑定邮箱成功",false,false,true,false),
	
	/**
	 * <b>code:</b>				5<br>
	 * <b>scene(title):</b>		添加银行卡成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您添加银行卡成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您添加银行卡成功。<br>
	 */
	ADD_BANKCARD_SUCC(5,"添加银行卡成功",false,false,true,false),
	
	
	/**
	 * <b>code:</b>				6<br>
	 * <b>scene(title):</b>		重置密码成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您重置密码成功。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您重置密码成功。<br>
	 */
	RESET_PASSWORD_SUCC(6,"重置密码成功",false,true,true,true),
	
	/**
	 * <b>code:</b>				7<br>
	 * <b>scene(title):</b>		充值成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount,balance】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您充值成功！充值amount元，平台账户余额为balance元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您充值成功！充值10000元，平台账户余额为10000.00元。<br>
	 */
	RECHARGE_SUCC(7,"充值成功",false,true,true,true),
	
	/**
	 * <b>code:</b>				8<br>
	 * <b>scene(title):</b>		提现成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,withdraw_amount,fee,actual_amount,balance】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您提现成功！提现金额withdraw_amount元，手续费fee元，实际到账actual_amount元，平台账户余额为balance元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您提现成功！提现金额10000.00元，手续费2元，实际到账9998.00元，平台账户余额为0.00元。<br>
	 */
	WITHDRAW_SUCC(8,"提现成功",false,true,true,true),
	
	/**
	 * <b>code:</b>				9<br>
	 * <b>scene(title):</b>		购买成功<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,amount,period_num,period_unit,apr,repayment_type】<br>
	 * <b>description:</b>		亲爱的user_name：您购买bid_no bid_name 成功,购买金额amount元，期限period_num(period_unit),年利率apr%，还款方式repayment_type。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您购买J6 优质企业短期资金周转 成功,购买金额10000元，期限3(月),年利率18%，还款方式先息后本。<br>
	 */
	INVEST_SUCC(9,"购买成功",true,true,true,true),
	
	/**
	 * <b>code:</b>				10<br>
	 * <b>scene(title):</b>		投资计息<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,amount,period_num,period_unit,apr,repayment_type】<br>
	 * <b>description:</b>		亲爱的user_name：您投资bid_no bid_name放款通过开始计息，投资金额amount元，期限period_num(period_unit),年利率apr%，还款方式repayment_type。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您投资J6 优质企业短期资金周转放款通过开始计息，投资金额10000元，期限3(月),年利率18%，还款方式先息后本。<br>
	 */
	INVEST_INTEREST(10,"投资计息",true,true,true,true),
	
	/**
	 * <b>code:</b>				11<br>
	 * <b>scene(title):</b>		投资回款<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bill_no,amount,principal,interest,fee,balance】<br>
	 * <b>description:</b>		亲爱的user_name：您的bill_no理财账单成功回款，回款金额amount元，本金principal元，利息interest元，扣除服务费fee元，到账金额balance元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的L45理财账单成功回款，回款金额150.00元，本金0.00元，利息150.00元，扣除服务费15.00元，到账金额135.00元。<br>
	 */
	INVEST_SECTION(11,"投资回款",true,true,true,true),
	
	/**
	 * <b>code:</b>				12<br>
	 * <b>scene(title):</b>		投资流标<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：您投资bid_no bid_name筹款失败，退还投资金额amount元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您投资J6 优质企业短期资金周转筹款失败，退还投资金额10000元。<br>
	 */
	INVEST_FAIL(12,"投资失败",false,true,true,true),
	
	/**
	 * <b>code:</b>				121<br>
	 * <b>scene(title):</b>		(使用红包)投资流标<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,invest_amount,red_amount,pay_amount】<br>
	 * <b>description:</b>		亲爱的user_name：您投资bid_no bid_name筹款失败，投标金额invest_amount元，退还红包金额red_amount元和花费金额pay_amount元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您投资J6 优质企业短期资金周转筹款失败，投标金额100元，退还红包金额100元和花费金额0元。<br>
	 */
	INVEST_FAIL_REDPACKET(121,"投资失败",false,true,true,true),
	
	/**
	 * <b>code:</b>				14<br>
	 * <b>scene(title):</b>		借款申请成功<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您申请了bid_no bid_name，请配合完善审核科目，为方便平台对您的真实身份进行验证，请务必保持通话畅通。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您申请了J6 优质企业短期资金周转，请配合完善审核科目，为方便平台对您的真实身份进行验证，请务必保持通话畅通。<br>
	 */
	BID_APPLAY_SUCC(14,"借款申请成功",true,true,true,true),
	
	/**
	 * <b>code:</b>				15<br>
	 * <b>scene(title):</b>		冻结借款保证金<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,bail】<br>
	 * <b>description:</b>		亲爱的user_name：冻结bid_no bid_name借款保证金bail元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：冻结J6 优质企业短期资金周转借款保证金10000元。<br>
	 */
	BAIL_FREEZE(15,"冻结借款保证金",false,true,true,true),
	
	/**
	 * <b>code:</b>				16<br>
	 * <b>scene(title):</b>		科目审核通过<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,subject_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您！您提交的subject_name审核通过。 <br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您！您提交的身份证正面照审核通过。 <br>
	 */
	SUBJECT_AUTID_PASS(16,"科目审核通过",true,true,true,true),
	
	/**
	 * <b>code:</b>				17<br>
	 * <b>scene(title):</b>		科目审核不通过<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,subject_name】<br>
	 * <b>description:</b>		亲爱的user_name：您提交的subject_name审核未通过，您可以继续提交。  <br>
	 * <b>demo:</b>				亲爱的mh137****5696：您提交的身份证正面照审核未通过，您可以继续提交。  <br>
	 */
	SUBJECT_AUTID_REJECT(17,"科目审核不通过",false,true,true,true),
	
	/**
	 * <b>code:</b>				18<br>
	 * <b>scene(title):</b>		项目初审通过<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您！您在平台申请了bid_no bid_name审核通过，已发布到平台进行筹款。  <br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您！您在平台申请了J6 优质企业短期资金周转审核通过，已发布到平台进行筹款。<br>
	 */
	BID_PREAUTID_PASS(18,"项目初审通过",true,true,true,true),
	
	/**
	 * <b>code:</b>				19<br>
	 * <b>scene(title):</b>		项目初审不通过<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您的bid_no bid_name审核未通过。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的J6 优质企业短期资金周转审核未通过。<br>
	 */
	BID_PREAUTID_REJECT(19,"项目初审不通过",false,true,true,true),
	
	/**
	 * <b>code:</b>				20<br>
	 * <b>scene(title):</b>		项目复审通过<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您的bid_no bid_name放款通过。  <br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的J6 优质企业短期资金周转放款通过。<br>
	 */
	BID_AUTID_PASS(20,"项目复审通过",true,true,true,true),
	
	
	/**
	 * <b>code:</b>				21<br>
	 * <b>scene(title):</b>		项目复审不通过<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您的bid_no bid_name复审未通过。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的J6 优质企业短期资金周转复审未通过。<br>
	 */
	BID_AUTID_REJECT(21,"项目复审不通过",false,true,true,true),
	
	/**
	 * <b>code:</b>				22<br>
	 * <b>scene(title):</b>		借款满标<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您的bid_no bid_name已满标，请等待平台放款审核。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的J6 优质企业短期资金周转已满标，请等待平台放款审核。<br>
	 */
	BID_FULL(22,"借款满标",true,true,true,true),
	
	/**
	 * <b>code:</b>				23<br>
	 * <b>scene(title):</b>		借款计息<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,amount,period_num,period_unit,repayment_type,loan_fee,balance】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您！您的bid_no bid_name成功放款，借款金额amount元，期限period_num(period_unit)，还款方式repayment_type，扣除服务费loan_fee元，到账金额balance元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您！您的J6 优质企业短期资金周转成功放款，借款金额100000元，期限3(月)，还款方式先息后本，扣除服务费300.00元，到账金额99700.00元。<br>
	 */
	BID_INTEREST(23,"借款计息",true,true,true,true),
	
	/**
	 * <b>code:</b>				24<br>
	 * <b>scene(title):</b>		账单到期提醒<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bill_no,repayment_time,amount】<br>
	 * <b>description:</b>		亲爱的user_name：您的bill_no借款账单到期还款日repayment_time,账单金额为amount元，请按时还款。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的Z003借款账单到期还款日2015-11-30,账单金额为75.00元，请按时还款。<br>
	 */
	BILL_EXPIRES(24,"账单到期提醒",false,true,true,true),
	
	/**
	 * <b>code:</b>				25<br>
	 * <b>scene(title):</b>		还款成功<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bill_no,amount】<br>
	 * <b>description:</b>		亲爱的user_name：您的bill_no借款账单还款成功，还款金额amount元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的Z003借款账单还款成功，还款金额75.00元。<br>
	 */
	REPAYMENT_SUCC(25,"还款成功",true,true,true,true),
	
	/**
	 * <b>code:</b>				26<br>
	 * <b>scene(title):</b>		借款流标<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name】<br>
	 * <b>description:</b>		亲爱的user_name：您的bid_no bid_name筹款失败。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的J6 优质企业短期资金周转筹款失败。<br>
	 */
	BID_FLOW(26,"借款流标",false,true,true,true),
	
	/**
	 * <b>code:</b>				27<br>
	 * <b>scene(title):</b>		解冻借款保证金<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,bid_no,bid_name,bail】<br>
	 * <b>description:</b>		亲爱的user_name：解冻bid_no bid_name借款保证金bail元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：解冻J6 优质企业短期资金周转借款保证金10000元。<br>
	 */
	BAIL_THAW(27,"解冻借款保证金",false,true,true,true),

	
	/**
	 * <b>code:</b>				28<br>
	 * <b>scene(title):</b>		平台奖励兑换成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount,balance】<br>
	 * <b>description:</b>		亲爱的user_name：amount元平台奖励兑换成功，可用余额增加amount元，当前可用余额为balance元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：15元平台奖励兑换成功，可用余额增加15.00元，当前可用余额为15.00元。<br>
	 */
	EXCHANGE_SUCC(28,"平台奖励兑换成功",false,true,true,true),
	

	
	/**
	 * <b>code:</b>				101<br>
	 * <b>scene(title):</b>		通知消息<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			通知消息没有模板，要自己传入内容<br>
	 * <b>description:</b>		通知消息没有模板，要自己传入内容<br>
	 * <b>demo:</b>				通知消息没有模板，要自己传入内容<br>
	 */
	PLATEFORM_EMAIL(29,"通知消息",true,false,false,false),
	
	/**
	 * <b>code:</b>				<br>
	 * <b>scene(title):</b>		广告资讯<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			广告资讯没有模板，要自己传入内容<br>
	 * <b>description:</b>		广告资讯没有模板，要自己传入内容<br>
	 * <b>demo:</b>				广告资讯没有模板，要自己传入内容<br>
	 */
	PLATEFORM_AD(30,"广告资讯",true,false,false,false),
	
	/**
	 * <b>code:</b>				201<br>
	 * <b>scene(title):</b>		安全验证码<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【sms_code】<br>
	 * <b>description:</b>		sms_code(动态验证码），请在30分钟内填写。<br>
	 * <b>demo:</b>				165964(动态验证码），请在30分钟内填写。<br>
	 */
	SECRITY_CODE(31,"安全验证码",false,true,false,false),
	
	/**
	 * <b>code:</b>				202<br>
	 * <b>scene(title):</b>		绑定邮箱链接<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,bind_email_button,bind_email_Url】<br>
	 * <b>description:</b>		亲爱的user_name:您正在进行邮箱更改操作，请点击bind_email_button按钮进行更改确认。 如果点击无效，请复制下方网页地址到浏览器地址栏中打开：bind_email_Url<br>
	 * <b>demo:</b>				亲爱的mhmh137****5696:您正在进行邮箱更改操作，请点击确认按钮进行更改确认。 如果点击无效，请复制下方网页地址到浏览器地址栏中打开：https://www.honhe.com:443/email/validate?vi=27404&vc=c8d90bc3-be13-4501-938b-0e80a4ef76fa<br>
	 */
	BIND_EMAIL(32,"绑定邮箱链接",false,false,false,true),
	
	/**
	 * <b>code:</b>				33<br>
	 * <b>scene(title):</b>		转让申请成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title】<br>
	 * <b>description:</b>		尊敬的user_name：您申请了debt_no title。<br>
	 * <b>demo:</b>				尊敬的mh137****5696：您申请了Q6 优质企业短期资金周转。<br>
	 */
	DEBT_APPLY_SUCC(33,"转让申请成功",false,true,true,true),
	
	/**
	 * <b>code:</b>				34<br>
	 * <b>scene(title):</b>		转让审核通过<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title】<br>
	 * <b>description:</b>		亲爱的user_name：您的debt_no title审核通过，已发布到平台进行转让。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的Q6 优质企业短期资金周转审核通过，已发布到平台进行转让。<br>
	 */
	DEBT_AUDIT_PASS(34,"转让审核通过",false,true,true,true),
	
	/**
	 * <b>code:</b>				35<br>
	 * <b>scene(title):</b>		转让审核不通过<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title】<br>
	 * <b>description:</b>		亲爱的user_name：您的debt_no title审核未通过。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的Q6 优质企业短期资金周转审核未通过。<br>
	 */
	DEBT_AUDIT_REJECT(35,"转让审核不通过",false,true,true,true),
	
	/**
	 * <b>code:</b>				36<br>
	 * <b>scene(title):</b>		转让成功(转让人)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title,amount,fee,balance】<br>
	 * <b>description:</b>		亲爱的user_name：您债权的debt_no title  转让成功，获得金额 amount 元，扣除转让服务费fee元，到账金额 balance 元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您债权的Q6 优质企业短期资金周转  转让成功，获得金额 10000 元，扣除转让服务费100.00元，到账金额 9900.00 元。<br>
	 */
	AUCTION_SUCC_TRANSFER(36,"转让成功(转让人)",false,true,true,true),
	
	/**
	 * <b>code:</b>				37<br>
	 * <b>scene(title):</b>		转让成功(受让人)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title,amount】<br>
	 * <b>description:</b>		亲爱的user_name：您购买的debt_no title 成功，扣除转让金额 amount 元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您购买的Q6 优质企业短期资金周转 成功，扣除转让金额 10000 元。<br>
	 */
	AUCTION_SUCC_AUCTION(37,"转让成功(受让人)",false,true,true,true),
	
	/**
	 * <b>code:</b>				38<br>
	 * <b>scene(title):</b>		转让失败<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,debt_no,title】<br>
	 * <b>description:</b>		亲爱的user_name：您的debt_no title 失败。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您的债权Q6 优质企业短期资金周转失败。<br>
	 */
	DEBT_FAIL(38,"转让失败",false,true,true,true),
	
	
	/**
	 * <b>code:</b>				1001<br>
	 * <b>scene(title):</b>		获得体验金(有体验金这个模块时起作用)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元体验金。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的10000元体验金。<br>
	 */
	GET_EXPER(51,"获得体验金",false,true,true,true),
	
	/**
	 * <b>code:</b>				1002<br>
	 * <b>scene(title):</b>		购买体验标成功(有体验金这个模块时起作用)<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,exper_no,exper_name,amount,period,apr】<br>
	 * <b>description:</b>		亲爱的user_name：您购买 exper_no exper_name 成功,购买金额amount元，期限period天,年利率apr%，还款方式一次性还款。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您购买 T1 1月体验项目第一期 成功,购买金额10000元，期限7天,年利率18%，还款方式一次性还款。<br>
	 */
	BUY_EXPER_BID_SUCC(52,"购买体验标成功",true,true,true,true),
	
	/**
	 * <b>code:</b>				1004<br>
	 * <b>scene(title):</b>		体验项目回款(有体验金这个模块时起作用)<br>
	 * <b>maskable:</b>			true<br>
	 * <b>param:</b>			【user_name,exper_no,exper_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：您投资的 exper_no exper_name成功回款，获得amount元体验收益。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您投资的 T1 1月体验项目第一期成功回款，获得15元体验收益。<br>
	 */
	EXPER_SECTION(54,"体验项目回款",true,true,true,true),
	
	/**
	 * <b>code:</b>				2001<br>
	 * <b>scene(title):</b>		获得资金托管开户红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元资金托管开户红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元资金托管开户红包。<br>
	 */
	REDPACKET_IPS(61,"获得资金托管开户红包",false,true,true,true),
	
	/**
	 * <b>code:</b>				2002<br>
	 * <b>scene(title):</b>		获得实名认证红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元实名认证红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元实名认证红包。<br>
	 */
	REDPACKET_REALNAME_VERIFIED(62,"获得实名认证红包",false,true,true,true),
	
	/**
	 * <b>code:</b>				2003<br>
	 * <b>scene(title):</b>		获得绑定邮箱红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元绑定邮箱红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元绑定邮箱红包。<br>
	 */
	REDPACKET_BIND_EMAIL(63,"获得绑定邮箱红包",false,true,true,true),
	
	/**
	 * <b>code:</b>				2003<br>
	 * <b>scene(title):</b>		获得绑定银行卡红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元绑定银行卡红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元绑定银行卡红包。<br>
	 */
	REDPACKET_BIND_CARD(66,"获得绑定银行卡红包",false,true,true,true),
	
	/**
	 * <b>code:</b>				2004<br>
	 * <b>scene(title):</b>		获得首次充值红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元首次充值红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元首次充值红包。<br>
	 */
	REDPACKET_FIRST_RECHARGE(64,"获得首次充值红包",false,true,true,true),
	
	
	/**
	 * <b>code:</b>				2004<br>
	 * <b>scene(title):</b>		获得累计充值福利加息券(有加息券模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,coupon】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的充值福利coupon加息券。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的充值福利1%加息券。<br>
	 */
	COUPON_RECHARGE(73,"获得累计充值福利加息券",false,true,true,true),
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		获得首次购买红包(有红包模块时)<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元首次购买红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元首次购买红包。<br>
	 */
	REDPACKET_FIRST_INVEST(65,"获得首次购买红包",false,true,true,true),
	
	
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		CPS推广成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【spreader_name(推广人，接收消息者),user_name(被推广人，当前注册成功的人)】<br>
	 * <b>description:</b>		亲爱的spreader_name：您成功推广了user_name。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您成功推广了bv13578946578<br>
	 */
	CPS_SPREAD_SUCC(67,"CPS推广成功",false,true,true,true),
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		获得CPS推广返佣<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得CPS推广返佣amount元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得CPS推广返佣5元。<br>
	 */
	CPS_REBATE(68,"获得CPS推广返佣",false,true,true,true),
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		财富圈邀请成功<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【spreader_name(推广人，接收消息者),user_name(被推广人，当前注册成功的人)】<br>
	 * <b>description:</b>		亲爱的spreader_name：您成功邀请了user_name。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：您成功邀请了bv13578946578。<br>
	 */
	WEALTHCIRCLE_SPREAD_SUCC(69,"财富圈邀请成功",false,true,true,true),
	
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		获得财富圈返佣<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【spreader_name,amount】<br>
	 * <b>description:</b>		亲爱的spreader_name：恭喜您获得财富圈返佣amount元。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得财富圈返佣bv13578946578元。<br>
	 */
	WEALTHCIRCLE_REBATE(70,"获得财富圈返佣",false,true,true,true),
	
	/**
	 * <b>code:</b>				2005<br>
	 * <b>scene(title):</b>		获得财富圈邀请码<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得了财富圈邀请码。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得了财富圈邀请码。<br>
	 */
	WEALTHCIRCLE_GET_CODE(71,"获得财富圈邀请码",false,true,true,true),
	/**
	 * <b>code:</b>				72<br>
	 * <b>scene(title):</b>		客户申请转让通知<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			<br>
	 * <b>description:</b>		<br>
	 * <b>demo:</b>				<br>
	 */
	TRANSFER_APPLY_NOTICE(72,"客户申请转让通知",false,true,true,true),
	
	
	/**
	 * <b>code:</b>				74<br>
	 * <b>scene(title):</b>		获得理财红包<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,amount】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的amount元理财红包。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的5元理财红包。<br>
	 */
	INVEST_RED_PACKET(74,"获得理财红包",false,true,true,true),
	
	/**
	 * <b>code:</b>				75<br>
	 * <b>scene(title):</b>		获得理财加息券<br>
	 * <b>maskable:</b>			false<br>
	 * <b>param:</b>			【user_name,rate】<br>
	 * <b>description:</b>		亲爱的user_name：恭喜您获得平台赠送的rate%理财加息券。<br>
	 * <b>demo:</b>				亲爱的mh137****5696：恭喜您获得平台赠送的0.1%理财加息券。<br>
	 */
	INVEST_ADD_RATE(75,"获得理财加息券",false,true,true,true)
	
	;
	
	/** 编号 */
	public int code;
	
	/** 场景 */
	public String value;
	
	/** 是否可配置拒收 */
	public boolean maskable;
	
	/** 不可配置，是否有sms */
	public boolean sms;
	
	/** 不可配置，是否有站内信 */
	public boolean msg;
	
	/** 不可配置，是否有email */
	public boolean email;
	

	private NoticeScene(int code, String value, boolean maskable, boolean sms, boolean msg, boolean email) {
		this.code = code;
		this.value = value;
		this.maskable = maskable;
		this.sms = sms;
		this.msg = msg;
		this.email = email;
	}

	public static NoticeScene getEnum(int scene){
		NoticeScene[] maritals = NoticeScene.values();
		for (NoticeScene marital : maritals) {
			if (marital.code == scene) {

				return marital;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取所有用户可配置的应用场景
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static List<NoticeScene> getMaskableScenes() {
		NoticeScene[] allScenes = values();
		
		List<NoticeScene> scenes = new ArrayList<NoticeScene>();
		
		for (NoticeScene scene : allScenes) {
			if (scene.maskable) {
				scenes.add(scene);
			}
		}
		return scenes;
	}
	
	/**
	 * 获取所有带有模板的应用场景(sms,msg,email不都为false的)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static List<NoticeScene> getScenesWithTemplate() {
		NoticeScene[] allScenes = values();
		
		List<NoticeScene> scenes = new ArrayList<NoticeScene>();
		
		for (NoticeScene scene : allScenes) {
			if (scene.sms || scene.msg || scene.email) {
				scenes.add(scene);
			}
		}
		return scenes;
	}
	
	/**
	 * 获取所有不带任何模板的应用场景(sms,msg,email都为false的)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static List<NoticeScene> getScenesNullTemplate() {
		NoticeScene[] allScenes = values();
		
		List<NoticeScene> scenes = new ArrayList<NoticeScene>();
		
		for (NoticeScene scene : allScenes) {
			if ( (!scene.sms) && (!scene.msg) && (!scene.email)) {
				scenes.add(scene);
			}
		}
		return scenes;
	}
}
