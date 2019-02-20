package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import models.finance.entity.t_cust_org;
import models.finance.entity.t_org_info;
import models.finance.entity.t_trade_info;
import services.finance.OrgInfoService;
import services.finance.TradeInfoService;

/**
 * 
 *
 * @ClassName: TradeInfoCtrl
 * 
 * @description 交易信息Controller层
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-下午4:10:25
 */
public class TradeInfoCtrl extends FrontBaseController {

	protected static final TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);

	/**
	 * 生成xml文档
	 *
	 * @Title: createXmlOfTradeInfo
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月11日-上午10:26:27
	 */
	public static void createXmlByTradeInfo() {
		tradeInfoService.createXmlByTradeInfo();
	}

}
