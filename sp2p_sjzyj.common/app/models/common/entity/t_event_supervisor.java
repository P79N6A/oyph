package models.common.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;
import common.annotation.SimulatedCheck;
import common.utils.StrUtil;

/**
 * 实体:管理员的操作时间
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月17日
 */
@Entity
public class t_event_supervisor extends Model {

	/** 操作时间 */
	public Date time = new Date(); 

	/** 管理员ID */
	public long supervisor_id;
	
	/** ip */
	public String ip ="";
	
	/** 操作类型 */
	private int item;
	
	/** 描述 */
	private String description;
	
	public Item getItem() {
		
		return Item.getEnum(this.item);
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 设置description的内容(会同时设置操作类型)
	 *
	 * @param event
	 * @param param
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public void setDescription(Event event, Map<String,String> param) {
		
		this.item = event.code;
		String descrip = StrUtil.replaceByMap(event.value, param);
		
		this.description = descrip;
	}

	/**
	 * 枚举:管理员事件栏目
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public enum Item {
		
		//---------------------首页-------------
		/** 101:首页*/
		HOME(101,"首页"),

		
		
		//---------------------运维-------------
		/** 201:栏目设置 */
		COLUMN(201,"栏目设置"),
		
		/** 202:内容管理 */
		INFORMATION(202,"内容管理"),
		
		/** 203:广告图片 */
		ADVERTISEMENT(203,"广告图片"),
		
		/** 204:理财顾问 */
		CONSULTANT(204,"理财顾问"),
		
		/** 205:合作伙伴 */
		PARTNER(205,"合作伙伴"),
		
		/** 206:帮助中心 */
		HELP(206,"帮助中心"),
		
		/** 207:通知模板 */
		NOTICE(207,"通知模板"),
		
		BANKQUOTA(208,"银行充值限额"),
		
		/** 209:借款合同管理 */
		CONTRACT(209,"借款合同管理"),
		
		
		//---------------------会员-------------
		/** 301:平台会员 */
		USER(301,"平台会员"),
		
		
		//---------------------风控-------------
		/** 401:理财项目 */
		LOAN(401,"理财项目"),
		
		/** 402:转让项目 */
		DEBT(402,"转让项目"),
		
		/** 403:借款产品 */
		PRODUCT(403,"借款产品"),
		
		/** 404:审核科目 */
		AUDITSUBJECT(404,"审核科目"),
		
		/** 405:信用等级 */
		CREDITLEVEL(405,"信用等级"),
		
		/** 406:合同模板 */
		PACT(406,"合同模板"),
		
		
		//---------------------财务-------------
		/** 501:财务放款 */
		RELEASE(501,"财务放款"),
		
		/** 502:借款账单 */
		BILL(502,"借款账单"),
		
		/** 503:理财账单 */
		INVESTBILL(503,"理财账单"),
		
		/** 504:会员提现 */
		USERWITHDRAW(504,"会员提现"),
		
		/** 505:会员充值 */
		USERRECHARGE(505,"会员充值"),
		
		/** 506:奖励兑换 */
		CONVERSION(506,"奖励兑换"),
		
		/** 507:平台收支 */
		PLATEFORMDEAL(507,"平台收支"),
		
		/** 508:理财设置 */
		FINANCESETTING(508,"理财设置"),
		
		/** 509:商户号管理 */
		MERCHANT(509,"商户号管理"),
		
		/** 510:托管日志 */
		PAYMENTLOG(510,"托管日志"),
		
		/** 511:资金校对 */
		FUNDCHECK(511,"资金校对"),
		
		
		
		//---------------------客服-------------
		
		
		
		
		
		//---------------------推广-------------
		/** 701:红包规则 */
		REDPACKET(701,"红包规则"),
		
		/** 702:体验金规则 */
		EXPERIENCE(702,"体验标规则"),
		
		/** 703:体验金项目 */
		EXPERIENCEBID(703,"体验项目"),
		
		/** 704:CPS规则 */
		CPSSETTING(704,"CPS规则"),
		
		/** 705:CPS推广会员 */
		CPSRECORD(705,"CPS推广会员"),
		
		/** 706:财富圈规则 */
		WEALTHCIRCLESETTING(706,"财富圈规则"),
		
		/** 707:财富圈邀请记录 */
		WEALTHCIRCLERECORD(707,"财富圈邀请记录"),
		
		/** 708:落地页设置 */
		LANDPAGE(708,"落地页设置"),
		//---------------------设置-------------
		/** 801:平台设置 */
		PLATEFORMSETTING(801,"平台设置"),
		
		/** 802:接口 */
		INTERFACESETTING(802,"接口设置"),
		
		/** 803:管理员日志 */
		SUPERVISOREVENT(803,"管理员日志"),
		
		/** 804:权限管理 */
		RIGHT(804,"权限管理"),
		
		/** 805:风格设置 */
		STYLESETTING(805,"风格设置"),
	
		
		//---------------------APP-------------
		APPVERSIONSETTING(902, "app版本管理"),
		
		//------------微信----------------------
		/** 1001:微信欢迎语设置 */
		WECHATDIALOGUE(1001, "微信欢迎语设置"),
		
		/** 1002:微信菜单 */
		WECHATMENU(1002, "微信菜单"),
		
		;
		
		public int code;
		public String value;
		private Item(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static Item getEnum(int code){
			Item[] types = Item.values();
			for (Item type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
	
	
	/**
	 * 枚举:管理员操作事件
	 *
	 * @description 均为操作管理员的真实姓名
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public enum Event {
		
		/** 101:登录系统 */
		LOGIN(Item.HOME.code,"登录系统"),

		/** 101:退出系统 ( 退出系统  ) */
		LOGOUT(Item.HOME.code," 退出系统 "),
		
		/** 101:首页 (勾选了前台显示数据):  编辑前台显示统计数据 ( 编辑前台显示统计数据  ) */
		HOME_STATISTICS_SHOW(Item.HOME.code," 编辑前台显示统计数据 "),
		
		/** 101:首页 (编辑平台简讯):  编辑平台简讯 ( 编辑平台简讯  ) */
		HOME_EDIT_TRAILER(Item.HOME.code," 编辑平台简讯 "),
		
		
//---------------------运维-----------------------------------------------------------------------
		/** 201:栏目设置(编辑栏目名称):  编辑栏目名称item_id item_name( 编辑栏目名称1首页) */
		ITEM(Item.COLUMN.code," 编辑栏目名称item_id item_name"),

		
		/** 202:内容管理(添加内容):  添加内容 information_id information_name( 添加内容207 90后辣妈的理财经) */
		INFO_ADD(Item.INFORMATION.code," 添加内容 information_id information_name"),
		
		/** 202:内容管理(编辑内容):  编辑内容 information_id information_name( 编辑内容207 90后辣妈的理财经) */
		INFO_EDIT(Item.INFORMATION.code," 编辑内容 information_id information_name"),
		
		/** 202:内容管理(下架内容):  下架内容 information_id information_name( 下架内容207 90后辣妈的理财经) */
		INFO_DISABLED(Item.INFORMATION.code," 下架内容 information_id information_name"),
		
		/** 202:内容管理(上架内容):  上架内容 information_id information_name( 上架内容207 90后辣妈的理财经) */
		INFO_ENABLE(Item.INFORMATION.code," 上架内容 information_id information_name"),
		
		/** 202:删除管理(删除内容):  删除内容 information_id information_name( 删除内容207 90后辣妈的理财经) */
		INFO_REMOVE(Item.INFORMATION.code," 删除内容 information_id information_name"),
		
		
		/** 203:广告图片(添加图片):  添加图片 image_id image_name( 添加图片 21 UI升级) */
		IMG_ADD(Item.ADVERTISEMENT.code," 添加图片 image_id image_name"),
		
		/** 203:广告图片(编辑图片):  编辑图片 image_id image_name( 编辑图片 21 UI升级) */
		IMG_EDIT(Item.ADVERTISEMENT.code," 编辑图片 image_id image_name"),
		
		/** 203:广告图片(下架图片):  下架图片 image_id image_name( 下架图片 21 UI升级) */
		IMG_DISABLED(Item.ADVERTISEMENT.code," 下架图片 image_id image_name"),
		
		/** 203:广告图片(上架图片):  上架图片 image_id image_name( 上架图片 21 UI升级) */
		IMG_ENABLE(Item.ADVERTISEMENT.code," 上架图片 image_id image_name"),
		
		/** 203:广告图片(删除图片):  删除图片 image_id image( 删除图片 21 UI升级) */
		IMG_REMOVE(Item.ADVERTISEMENT.code," 删除图片 image_id image"),
		
		
		/** 204:理财顾问(添加理财顾问):  添加理财顾问 consultant_id consultant_name( 添加理财顾问 3 璐璐 ) */
		CONSULTANT_ADD(Item.CONSULTANT.code," 添加理财顾问 consultant_id consultant_name"),
		
		/** 204:理财顾问(编辑理财顾问):  编辑理财顾问 consultant_id consultant_name( 编辑理财顾问 3 璐璐 ) */
		CONSULTANT_EDIT(Item.CONSULTANT.code," 编辑理财顾问 consultant_id consultant_name"),
		
		/** 204:理财顾问(删除理财顾问):  删除理财顾问 consultant_id consultant_name( 删除理财顾问 3 璐璐 ) */
		CONSULTANT_REMOVE(Item.CONSULTANT.code," 删除理财顾问 consultant_id consultant_name"),
		
		
		/** 205:合作伙伴 (添加合作伙伴 ):  添加合作伙伴 partner_id partner_name( 添加合作伙伴 6 汇付天下 ) */
		PARTNER_ADD(Item.PARTNER.code," 添加合作伙伴 partner_id partner_name"),
		
		/** 205:合作伙伴 (编辑合作伙伴 ):  编辑合作伙伴 partner_id partner_name( 编辑合作伙伴 6 汇付天下 ) */
		PARTNER_EDIT(Item.PARTNER.code," 编辑合作伙伴 partner_id partner_name"),
		
		/** 205:合作伙伴 (删除合作伙伴 ):  删除合作伙伴 partner_id partner_name( 删除合作伙伴 6 汇付天下 ) */
		PARTNER_REMOVE(Item.PARTNER.code," 删除合作伙伴 partner_id partner_name"),
		

		/** 206:添加帮助 (添加帮助 ):  添加帮助 help_id help_name( 添加帮助 2信用等级的规则是什么 ) */
		HELP_ADD(Item.HELP.code," 添加帮助 help_id help_name"),
		
		/** 206:帮助中心 (编辑帮助 ):  编辑帮助 help_id help_name( 编辑帮助 2信用等级的规则是什么 ) */
		HELP_EDIT(Item.HELP.code," 编辑帮助 help_id help_name"),
		
		/** 206:帮助中心 (下架帮助 ):  下架帮助 help_id help_name( 下架帮助 2信用等级的规则是什么 ) */
		HELP_DISABLED(Item.HELP.code," 下架帮助 help_id help_name"),
		
		/** 206:帮助中心 (上架帮助 ):  上架帮助 help_id help_name( 上架帮助 2信用等级的规则是什么 ) */
		HELP_ENABLE(Item.HELP.code," 上架帮助 help_id help_name"),
		
		
		/** 207:通知模板 (编辑短信模板 ):  编辑短信模板 template_id template_name( 编辑短信模板 21 短信验证码 ) */
		SMS_EDIT(Item.NOTICE.code," 编辑短信模板 template_id template_name"),
		
		/** 207:通知模板 (编辑邮件模板):  编辑邮件模板template_id template_name( 编辑邮件模板 21 找回密码 码 ) */
		EMAIL_EDIT(Item.NOTICE.code," 编辑邮件模板 template_id template_name"),
		
		/** 207:通知模板 (编辑消息模板):  编辑消息模板template_id template_name( 编辑编辑消息模板 21 借款满标  码 ) */
		MSG_EDIT(Item.NOTICE.code," 编辑消息模板 template_id template_name"),
		
		VOTE_DISUSE(Item.NOTICE.code,"下架投票活动vote_title"),
		
		VOTE_USE(Item.NOTICE.code,"上架投票活动vote_title"),
		
		/** 208:银行限额管理(银行充值限额设置): 修改限额 single_quota day_quota */
		BANKQUOTAS(Item.BANKQUOTA.code,"银行充值限额修改"),
		
		/** 209:借款合同管理(借款合同设置) */
		CONTRACTS(Item.CONTRACT.code,"借款合同修改"),
		
		
//---------------------会员-----------------------------------------------------------------------
		/** 301:会员 (发短信给会员):  发短信给会员 user_id user_name( 发短信给会员54 xf137****5696 ) */
		MEMBER_SEND_SMS(Item.USER.code," 发短信给会员 user_id user_name"),
		
		/** 301:会员 (发邮件给会员):  发邮件给会员 user_id user_name( 发邮件给会员54 xf137****5696 ) */
		MEMBER_SEND_EMAIL(Item.USER.code," 发邮件给会员 user_id user_name"),
		
		/** 301:会员 (发消息给会员):  发消息给会员 user_id user_name( 发消息给会员54 xf137****5696 ) */
		MEMBER_SEND_MSG(Item.USER.code," 发消息给会员 user_id user_name"),
		
		/** 301:会员 (锁定会员):  锁定会员 user_id user_name( 锁定会员54 xf137****5696 ) */
		MEMBER_LOCK(Item.USER.code," 锁定会员 user_id user_name"),
		
		/** 301:会员 (解锁会员):  解锁会员 user_id user_name( 解锁会员54 xf137****5696 ) */
		MEMBER_UNLOCK(Item.USER.code," 解锁会员 user_id user_name"),
		
		/** 301:会员 (编辑会员):  编辑会员 user_id user_name( 编辑会员54 xf137****5696 ) */
		MEMBER_EDIT(Item.USER.code," 编辑会员 user_id user_name"),
		
		/** 301:会员 (群发邮件):  群发邮件 */
		MASS_SEND_EMAIL(Item.USER.code," 群发邮件"),
		
		/** 301:会员 (群发短信):  群发短信 */
		MASS_SEND_SMS(Item.USER.code," 群发短信"),
		
		/** 301:会员 (群发系统消息):  群发系统消息 */
		MASS_SEND_MSG(Item.USER.code," 群发系统消息"),
		
		/** 301:会员 (批量发送短信):  批量发送短信 */
		BATCH_SEND_SMS(Item.USER.code," 批量发送短信"),
		
		/** 301:会员 (批量发送系统消息):  批量发送系统消息 */
		BATCH_SEND_MSG(Item.USER.code," 批量发送系统消息"),
		
		/** 301:会员 (批量发送邮件):  批量发送邮件 */
		BATCH_SEND_EMAIL(Item.USER.code," 批量发送邮件"),
		
		/** 301:会员 (模拟登录): 模拟登陆会员 user_id user_name( 模拟登陆会员 88（编号） Fe18001399881（name） )*/
		SIMULATED_LOGIN(Item.USER.code," 模拟登陆会员 user_id user_name"),
		

//---------------------风控-----------------------------------------------------------------------
		/** 401:理财项目(发布理财项目):  发布理财项目 bid_no bid_name(发布理财项目J1 工资宝第一期) */
		INVEST_ADD(Item.LOAN.code,"发布理财项目 bid_no bid_name"),
		
		/** 401:理财项目(审核通过借款标审核科目):  通过 bid_no bid_name subject filename(通过J3工资宝第三期 身份证 1正面照) */
		INVEST_SUBJECT_AUDIT_PASS(Item.LOAN.code,"通过 bid_no bid_name subject filename"),
		
		/** 401:理财项目(审核不通过借款标审核科目):  通过 bid_no bid_name subject filename(不通过J3工资宝第三期 身份证 2反面照) */
		INVEST_SUBJECT_AUDIT_REJECT(Item.LOAN.code,"不通过 bid_no bid_name subject filename"),
		
		/** 401:理财项目(上传审核借款标审核科目资料):  上传 bid_no bid_name subject filename(上传J3工资宝第三期  身份证 1正面照) */
		INVEST_SUBJECT_FILE_UPLOAD(Item.LOAN.code,"上传 bid_no bid_name subject filename"),
		
		/** 401:理财项目(删除审核借款标审核科目资料):  删除 bid_no bid_name subject filename(删除J3工资宝第三期  身份证 1正面照) */
		INVEST_SUBJECT_FILE_REMOVE(Item.LOAN.code,"删除 bid_no bid_name subject filename"),
		
		/** 401:理财项目(借款标初审通过):  初审通过 bid_no bid_name( 初审通过 J3 工资宝第三期) */
		INVEST_PREADUIT_PASS(Item.LOAN.code,"初审通过 bid_no bid_name"),
		
		/** 401:理财项目(借款标初审不通过):  初审不通过 bid_no bid_name( 初审不通过 J2 工资宝第二期) */
		INVEST_PREADUIT_REJECT(Item.LOAN.code,"初审不通过 bid_no bid_name"),
		
		/** 401:理财项目(借款标复审通过):  复审通过 bid_no bid_name( 复审通过 J3 工资宝第三期) */
		INVEST_ADUIT_PASS(Item.LOAN.code,"复审通过 bid_no bid_name"),
		
		/** 401:理财项目(借款标复审不通过):  复审不通过 bid_no bid_name( 复审不通过 J2 工资宝第二期) */
		INVEST_ADUIT_REJECT(Item.LOAN.code,"复审不通过 bid_no bid_name"),
		
		/** 401:理财项目(编辑借款标项目名称):  编辑 bid_no bid_name 项目名称( 编辑 J2 工资宝第二期 项目名) */
		INVEST_EDIT_PROJECTNAME(Item.LOAN.code,"编辑 bid_no bid_name 项目名称"),
		
		/** 401:理财项目(编辑借款标借款描述):  编辑 bid_no bid_name 借款描述( 编辑 J2 工资宝第二期 借款描述) */
		INVEST_EDIT_DESCRIPTION(Item.LOAN.code,"编辑 bid_no 借款描述"),
		
		
		/** 402:债权转让(审核通过债权转让):  通过 transfer_id transfer_name( 通过 Q1 低价转让先到先得) */
		TRANSFER_ADUIT_PASS(Item.DEBT.code,"通过 transfer_id transfer_name"),
		
		/** 402:债权转让(审核不通过债权转让):  通过 transfer_id transfer_name( 不通过 Q1 低价转让先到先得) */
		TRANSFER_ADUIT_REJECT(Item.DEBT.code,"不通过 transfer_id transfer_name"),
		
		
		/** 403:借款产品(添加借款产品):  添加借款产品 product_id product_name(添加借款产品1名车贷) */
		PRODUCT_ADD(Item.PRODUCT.code,"添加借款产品 product_id product_name"),
		
		/** 403:借款产品(下架借款产品):  下架借款产品 product_id product_name(下架借款产品1名车贷) */
		PRODUCT_DISABLED(Item.PRODUCT.code,"下架借款产品 product_id product_name"),
		
		/** 403:借款产品(上架借款产品):  上架借款产品 product_id product_name(上架借款产品1名车贷) */
		PRODUCT_ENABLE(Item.PRODUCT.code,"上架借款产品 product_id product_name"),
		
		/** 403:借款产品(编辑借款产品):  编辑借款产品 product_id product(编辑借款产品1名车贷) */
		PRODUCT_EDIT(Item.PRODUCT.code,"编辑借款产品 product_id product_name"),
		
		
		/** 404:审核科目(添加审核科目):  添加审核科目 subject_id subject_name(添加审核科目1身份证) */
		SUBJECT_ADD(Item.AUDITSUBJECT.code,"编辑审核科目 subject_id subject_name"),
		
		/** 404:审核科目(编辑审核科目):  添加审核科目 subject_id subject_name(添编辑核科目1身份证) */
		SUBJECT_EDIT(Item.AUDITSUBJECT.code,"编辑审核科目 subject_id subject_name"),
		
		
		/** 405:信用等级(编辑信用等级):   编辑信用等级 level_id level_name( 编辑信用等级1青铜) */
		CREDITLEVEL_EDIT(Item.CREDITLEVEL.code," 编辑信用等级 level_id level_name"),
		
		
		/** 406:合同模板(编辑合同模板):   编辑信用等级 pact_id pact_name( 编辑合同模板1理财合同) */
		PACT_EDIT(Item.PACT.code," 编辑合同模板 pact_id pact_name"),
		
		
		
//---------------------财务-----------------------------------------------------------------------
		/** 501:财务放款(借款标放款):   放款  bid_no bid_name( 放款 J3工资宝第三期) */
		RELEASE(Item.RELEASE.code," 放款 bid_no bid_name"),
		
		
		/** 502:借款账单(借款账单本息垫付):   本息垫付 bill_no 借款账单( 本息垫付 Z1 借款账单) */
		BILL_PRINCIPAL(Item.BILL.code," 本息垫付 bill_no 借款账单"),
		
		/** 502:借款账单(借款账单线下收款):   线下收款 bill_no 借款账单( 线下收款 Z1 借款账单) */
		BILL_OFFLINE(Item.BILL.code," 线下收款 bill_no 借款账单"),
		
		
		/** 506:奖励兑换(奖励兑换):   兑换conversion_id( 兑换15) */
		CONVERSION(Item.CONVERSION.code," 兑换conversion_id"),
		
		
		/** 508:理财设置:  编辑提现设置( 编辑提现设置  ) */
		FINANCESETTING_WITHDRAW(Item.FINANCESETTING.code," 编辑提现设置 "),
		
		/** 508:理财设置:  编辑转让设置( 编辑转让设置  ) */
		FINANCESETTING_TRANSER(Item.FINANCESETTING.code," 编辑转让设置"),
		
		/** 508:理财设置:  编辑充值设置 ( 编辑充值设置   ) */
		FINANCESETTING_RECHARGE(Item.FINANCESETTING.code," 编辑充值设置 "),
		
		/** 508:理财设置:  编辑短信催收设置( 编辑短信催收设置  ) */
		FINANCESETTING_EXPIRES(Item.FINANCESETTING.code," 编辑短信催收设置 "),
		
		
		/** 509:商户号管理(代偿账户充值):   代偿账户( 代偿账户充值) */
		COMPENSATORY_RECHARGE(Item.MERCHANT.code," 代偿账户充值"),
		
		/** 509:商户号管理(代偿账户提现):   提现手续费账户( 代偿账户提现) */
		COMPENSATORY_WITHDRAW(Item.MERCHANT.code," 代偿账户提现"),
		
		/** 509:商户号管理(营销账户充值):   营销账户( 营销账户充值) */
		MARKET_RECHARGE(Item.MERCHANT.code," 营销账户充值"),
		
		/** 509:商户号管理(营销账户提现):   提现手续费账户( 营销账户提现) */
		MARKET_WITHDRAW(Item.MERCHANT.code," 营销账户提现"),
		
		/** 509:商户号管理(费用账户充值):   费用账户( 费用账户充值) */
		COST_RECHARGE(Item.MERCHANT.code," 费用账户充值"),
		
		/** 509:商户号管理(费用账户提现):   提现手续费账户( 费用账户提现) */
		COST_WITHDRAW(Item.MERCHANT.code," 费用账户提现"),
		
		
		
//---------------------财务-----------------------------------------------------------------------
		/** 701:红包规则 (设置红包规则  ):  设置红包规则 ( 设置红包规则  ) */
		SPREAD_EDIT_REDPACKEDT(Item.REDPACKET.code," 设置红包规则 "),
		
		
		/** 702:体验金规则 (设置体验金规则  ):  设置体验金规则 ( 设置体验金发放规则  ) */
		SPREAD_EDIT_EXPERIENCEGOLD(Item.EXPERIENCE.code," 设置体验金发放规则 "),
		
		/** 702:体验金规则 (设置体验金规则  ):  设置体验金规则 ( 设置体验项目发布规则  ) */
		SPREAD_EDIT_EXPERIENCEBID(Item.EXPERIENCE.code," 设置体验项目发布规则 "),
		
		
		/** 703:体验金项目 (发布体验项目  ):  发布体验项目 bid_no bid_name( 发布体验项目T1 体验标第一期) */
		SPREAD_EXPERIENCEBID_CREAT(Item.EXPERIENCEBID.code," 发布体验项目 bid_no bid_name"),
		
		/** 703:体验金项目 (体验项目放款  ):  放款体验项目 ( 放款T1 体验标第一期) */
		SPREAD_EXPERIENCEBID_RELEASE(Item.EXPERIENCEBID.code," 放款体验项目 bid_no bid_name"),
		
		
		/** 704:CPS规则 (设置CPS推广规则):  设置CPS推广规则( 设置CPS推广规则 ) */
		SPREAD_EDIT_CPS(Item.CPSSETTING.code," 设置CPS推广规则"),
		
		
		/** 706:财富圈规则 (设置财富圈规则 ):  设置财富圈规则 ( 设置财富圈规则  ) */
		SPREAD_EDIT_WEALTHCIRCLE(Item.WEALTHCIRCLESETTING.code," 设置财富圈规则 "),
		
		/** 708:设置落地页三元素*/
		EDIT_LANDPAGE(Item.LANDPAGE.code,"设置落地页三元素"),
		
//---------------------财务-----------------------------------------------------------------------
		/** 801:平台设置(设置基本信息): 设置基本信息 */
		SETTING_BASIC(Item.PLATEFORMSETTING.code,"设置基本信息"),
		
		/** 801:平台设置(设置SEO规则):  设置SEO规则 */
		SETTING_SEO(Item.PLATEFORMSETTING.code,"设置SEO规则"),
		
		/** 801:平台设置(设置安全参数):  设置安全参数 */
		SETTING_SECURITY(Item.PLATEFORMSETTING.code,"设置安全参数"),
		
		/** 801:平台设置(设置正版授权):  设置正版授权 */
		SETTING_REGISTERCODE(Item.PLATEFORMSETTING.code,"设置正版授权"),
		
		/** 801:平台设置(设置自动投标):  设置自动投标 */
		SETTING_AUTOINVEST(Item.PLATEFORMSETTING.code,"设置自动投标"),
		
		
		/** 802:接口设置(邮件通道):  设置邮件通道(12月2日 11:00  设置邮件通道) */
		PROVIDER_EMAIL(Item.INTERFACESETTING.code,"设置邮件通道"),
		
		/** 802:接口设置(短信通道):  设置短信通道(12月2日 11:00  设置短信通道) */
		PROVIDER_SMS(Item.INTERFACESETTING.code,"设置短信通道"),
		
		
		/** 804:权限管理(添加管理员):  添加管理员manager_id manager_name(添加管理员2 黄运松) */
		RIGHT_ADD_SUPERVISOR(Item.RIGHT.code,"添加管理员manager_id manager_name"),
		
		/** 804:权限管理(编辑管理员):  编辑管理员manager_id manager_name(编辑管理员2 黄运松) */
		RIGHT_EDIT_SUPERVISOR(Item.RIGHT.code,"编辑管理员manager_id manager_name"),
		
		/** 804:权限管理(制盾管理员):  制盾管理员manager_id manager_name(制盾管理员2 黄运松) */
		RIGHT_UKEY_SUPERVISOR(Item.RIGHT.code,"制盾管理员manager_id manager_name"),
		
		/** 804:权限管理(分配管理员):  分配管理员manager_id manager_name(分配管理员2 黄运松) */
		RIGHT_ASSIGN_SUPERVISOR(Item.RIGHT.code,"分配管理员manager_id manager_name"),
		
		/** 804:权限管理(锁定管理员):  锁定管理员manager_id manager_name(锁定管理员2 黄运松) */
		RIGHT_LOCK_SUPERVISOR(Item.RIGHT.code,"锁定管理员manager_id manager_name"),
		
		/** 804:权限管理(解锁管理员):  解锁管理员manager_id manager_name(解锁管理员2 黄运松) */
		RIGHT_UNLOCK_SUPERVISOR(Item.RIGHT.code,"解锁管理员manager_id manager_name"),
		
		
		/** 805:风格设置(更换皮肤):  更换皮肤skin_id skin_name(12月2日 11:05  更换皮肤2 活跃橙黄) */
		STYLE_CHANGE_SKIN(Item.STYLESETTING.code,"更换皮肤skin_id skin_name"),
		
		/** 805:风格设置(自定义风格):  设置自定义风格(12月2日 11:05  设置自定义风格) */
		STYLE_CUSTOMIZE(Item.STYLESETTING.code,"设置自定义风格"),
		
		/** 902:IOS版本设置 */
		APP_IOSVERSION_SETTING(Item.APPVERSIONSETTING.code, "设置IOS版本管理"),
		
		/** 902:android版本设置 */
		APP_ANDROIDVERSION_SETTING(Item.APPVERSIONSETTING.code, "设置android版本管理"),
		
		/** 902:android(代理商)版本设置 */
		APP_ANDROIDVERSION_SETTING_DL(Item.APPVERSIONSETTING.code, "设置android(代理商)版本管理"),
		
		/** 1001:微信关注欢迎语编辑 */
		WECHAT_WELOCOME_EDIT(Item.WECHATDIALOGUE.code, "微信欢迎语设置"),
		
		/** 1002:创建微信菜单 */
		WECHAT_MENU_CREATE(Item.WECHATMENU.code, "创建微信菜单"),
		
		/** 1002:编辑微信菜单 :  编辑微信菜单 menu_id menu_name */
		WECHAT_MENU_EDIT(Item.WECHATMENU.code, "编辑微信菜单 menu_id menu_name "),
		
		;
		
		public int code;
		public String value;
		
		private Event(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static Event getEnum(int code){
			Event[] types = Event.values();
			for (Event type: types) {
				if (type.code == code) {

					return type;
				}
			}
			
			return null;
		}
	}
	
}
