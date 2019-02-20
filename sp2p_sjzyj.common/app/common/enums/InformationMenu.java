package common.enums;

import java.util.Arrays;
import java.util.List;

/**
 *  枚举： 资讯管理 左侧菜单
 *
 * @author liudong
 * @createDate 2016年1月5日
 */

public enum InformationMenu {

	/** 公司介绍  */
	HOME_PROFILE("home_profile","公司介绍"),
	
    PLATFORM_ADVANTAGE("platform_advantage","平台优势"),
	
	/** 加入我们  */
	HOME_JOINUS("home_joinus","加入我们"),
	
	/** 官方公告  */
	INFO_BULLETIN("info_bulletin","官方公告"),
	
	/** 媒体报道 */
	INFO_REPORT("info_report","媒体报道"),
	
	/** 金融故事  */
	INFO_STORY("info_story","金融故事"),
	
	/** 平台注册协议  */
	PLATFORM_REGISTER("platform_register","平台注册协议"),
	
    MALL_GUIDE("mall_guide","商城指引"),
    
    COMMON_PROBLEM("common_problem","常见问题"),
    
    

	
	/** 投资协议模板  */
	INVEST_AGREEMENT_TEMPLATE("investment_agreement_template","投资协议"),
	
	/** 委托投资协议模板*/
	INVEST_ENTRUEST_AGREEMENT("invest_entrust_agreement","委托投资协议"),
	
	/** 借款协议 */
	LOAN_AGREEMENT("loan_agreement","借款协议"),
	
	/**风险教育*/
	RISK_EDUCATION("risk_education","风险教育");
	
	public String code;
	public String value;
	
	
	/** 资讯 （前台） 官方公告，理财故事，媒体报道  */
	public static final List<String> INFORMATION_FRONT = Arrays.asList( 
			INFO_BULLETIN.code,
			INFO_REPORT.code,
			INFO_STORY.code);
	
	/* liuyang begin 2017-1-7------------------------------------------ */
	/** 资讯 （前台） 官方公告  */
	public static final List<String> INFORMATION_BULLETIN = Arrays.asList( 
			INFO_BULLETIN.code);
	
	/** 资讯 （前台） 媒体报道  */
	public static final List<String> INFORMATION_REPORT = Arrays.asList( 
			INFO_REPORT.code);
	
	/** 资讯 （前台） 理财故事  */
	public static final List<String> INFORMATION_STORY = Arrays.asList( 
			INFO_STORY.code);
	/* liuyang end 2017-1-7------------------------------------------ */
	private InformationMenu(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public static InformationMenu getEnum(String code){
		InformationMenu[] menus = InformationMenu.values();
		for (InformationMenu menu : menus) {
			if (menu.code.equals(code)) {

				return menu;
			}
		}
		
		return null;
	}
	
}
