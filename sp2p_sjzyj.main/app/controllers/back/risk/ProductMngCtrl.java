package controllers.back.risk;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;
import models.core.entity.t_audit_subject;
import models.core.entity.t_product;
import models.core.entity.t_product.BuyType;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.ProductType;
import models.core.entity.t_product.RepaymentType;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import services.core.AuditSubjectService;
import services.core.ProductService;

import com.shove.Convert;

import common.constants.CacheKey;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;

/**
 * 后台-风控-借款产品管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
public class ProductMngCtrl extends BackBaseController {
	
	protected static AuditSubjectService auditsubjectservice = Factory.getService(AuditSubjectService.class);
	protected static ProductService productservice = Factory.getService(ProductService.class);
	/**
	 * 后台-风控-借款产品-借款产品列表
	 *
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public static void showProductPre(int showType, int currPage, int pageSize) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		Boolean sta = null;//全部
		if (1 == showType) {
			sta = true;//上架
		} else if (2 == showType) {
			sta = false;//下架
		}
		
		PageBean<t_product>pageBean = productservice.pageOfProduct(pageSize, currPage, sta);
		
		render(pageBean, showType);
	}
	
	/**
	 * 后台-风控-借款产品-借款产品列表-添加借款产品
	 *
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public static void toAddProductPre() {
		
		List<ProductType> productType = Arrays.asList(ProductType.values());
		
		List<PeriodUnit> productPeriodUnit = Arrays.asList(PeriodUnit.values());
		
		List<RepaymentType> productRepaymentType = Arrays.asList(RepaymentType.values());
		
		List<BuyType> productBuyType = Arrays.asList(BuyType.values());
		
		List<t_audit_subject> productAuditSubject = auditsubjectservice.findAll();
		
		//回显数据用到的处理方式
		t_product product = (t_product) Cache.get(CacheKey.PRODUCT_+session.getId());
		if (product != null) {
			renderArgs.put("product", product);
			Cache.delete(CacheKey.PRODUCT_+session.getId());
		}
		
		render(productType, productPeriodUnit, productRepaymentType, productBuyType, productAuditSubject);
	}
	
	/**
	 * 后台-风控-借款产品-借款产品列表-编辑借款产品
	 *
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public static void toEditProductPre(long productId) {
		
		if(productId < 0){
			flash.error("产品ID参数错误");
			
			showProductPre(0, 1, 10);
		}
		
		t_product product = productservice.findByID(productId);
		if(product == null){
			flash.error("没有找到该借款产品");
			
			showProductPre(0, 1, 10);
		}
		List<PeriodUnit> productPeriodUnit = Arrays.asList(PeriodUnit.values());
		
		List<RepaymentType> productRepaymentType = Arrays.asList(RepaymentType.values());
		
		List<BuyType> productBuyType = Arrays.asList(BuyType.values());
		
		List<t_audit_subject> productAuditSubject = auditsubjectservice.findAll();
		
		render(productPeriodUnit, productRepaymentType, productBuyType, productAuditSubject, product);
	}
	
	/**
	 * 编辑借款产品
	 *
	 * @param product 借款产品对象
	 * @param productId 借款产品id 
	 * @param period_unit 借款期限单位
	 * @param buy_type 购买方式
	 * @param repayment_type 还款方式
	 * @param service_fee_rule 服务费设置
	 * @param audit_subjects 审核科目
	 * @param order_time 排序时间
	 *
	 * @author yaoyi
	 * @createDate 2016年1月6日
	 */
	public static void editProduct(t_product product, long productId, int period_unit, int buy_type, String repayment_type, 
			String service_fee_rule, String audit_subjects, String order_time){
		checkAuthenticity();
		
		t_product update_tp = productservice.findByID(productId);
		if(update_tp == null){
			flash.error("没有找到该借款产品");
			
			showProductPre(0, 1, 10);
		}
		
		ResultInfo result = checkEditProductParams(product, period_unit, buy_type, repayment_type, service_fee_rule, audit_subjects, order_time);
		if(result.code < 1){
			flash.error(result.msg);
			
			toEditProductPre(productId);
		}
		update_tp.img_logo_format=product.img_logo_format;
		update_tp.img_logo_resolution=product.img_logo_resolution;
		update_tp.img_logo_size=product.img_logo_size;
		update_tp.img_logo_url=product.img_logo_url;
		update_tp.image_url = product.image_url;
		update_tp.image_resolution = product.image_resolution;
		update_tp.image_size = product.image_size;
		update_tp.image_format = product.image_format;
		update_tp.image_app_url = product.image_app_url;
		update_tp.image_app_resolution = product.image_app_resolution;
		update_tp.image_app_size = product.image_app_size;
		update_tp.image_app_format = product.image_app_format;
		update_tp.min_amount=product.min_amount;
		update_tp.max_amount=product.max_amount;
		update_tp.min_apr=product.min_apr;
		update_tp.max_apr=product.max_apr;
		update_tp.min_period=product.min_period;
		update_tp.max_period=product.max_period;
		update_tp.min_invest_amount=product.min_invest_amount;
		update_tp.invest_copies=product.invest_copies;
		update_tp.bail_scale=product.bail_scale;
		//补充product对象
		update_tp.setPeriod_unit(PeriodUnit.getEnum(period_unit));
		update_tp.setBuy_type(BuyType.getEnum(buy_type));
		update_tp.repayment_type = repayment_type.replace(" " , "");//去掉其中的空格
		
		JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
		JSONObject jso = new JSONObject();
		if (t_product.PeriodUnit.DAY.equals(product.getBuy_type())) {//天标
			jso.put(Constants.LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.LOAN_AMOUNT_RATE));
			jso.put(Constants.SUB_PERIOD, 0);
			jso.put(Constants.SUB_LOAN_AMOUNT_RATE, 0.0);
			jso.put(Constants.INVEST_AMOUNT_RATE, jsobj.getDouble(Constants.INVEST_AMOUNT_RATE));
			jso.put(Constants.OVERDUE_AMOUNT_RATE, jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE));
		} else {//月标
			jso.put(Constants.LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.LOAN_AMOUNT_RATE));
			jso.put(Constants.SUB_PERIOD, jsobj.getInt(Constants.SUB_PERIOD));
			jso.put(Constants.SUB_LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.SUB_LOAN_AMOUNT_RATE));
			jso.put(Constants.INVEST_AMOUNT_RATE, jsobj.getDouble(Constants.INVEST_AMOUNT_RATE));
			jso.put(Constants.OVERDUE_AMOUNT_RATE, jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE));
		}
		update_tp.service_fee_rule = jso.toString();
		update_tp.audit_subjects = audit_subjects.replace(" ", "");
		
		try{
			update_tp.order_time = DateUtil.strToDate(order_time,  Constants.DATE_PLUGIN);
		}catch(RuntimeException re){
			update_tp.order_time = new Date();
		}
		
		boolean save = productservice.createProduct(update_tp);
		if(!save){
			flash.error("添加失败");
			LoggerUtil.error(true, "添加借款产品失败");
			
			toEditProductPre(productId);
		}
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("product_id", String.valueOf(update_tp.id));
		param.put("product_name", update_tp.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.PRODUCT_EDIT, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");

			toEditProductPre(productId);
		}
		
		flash.success("编辑借款产品成功!");
		
		showProductPre(0, 1, 10);
	}
	
	/**
	 * 编辑借款产品时，检查参数
	 *
	 * @param product 产品对象
	 * @param period_unit 借款期限单位
	 * @param buy_type 购买方式
	 * @param repayment_type 还款方式
	 * @param service_fee_rule 收费方式
	 * @param audit_subjects 审核科目
	 * @param order_time 排序时间
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月24日
	 */
	private static ResultInfo checkEditProductParams(t_product product,	int period_unit, int buy_type, String repayment_type, 
			String service_fee_rule, String audit_subjects, String order_time) {
		ResultInfo result = new ResultInfo();
		
		if (product == null) {
			result.code = -1;
			result.msg = "借款产品参数错误!";
			
			return result;
		}
		
		if (StringUtils.isBlank(product.image_url) || product.image_url.length()>128) {
			result.code = -1;
			result.msg = "借款产品的图片不能为空!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_resolution) && product.image_resolution.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片分辨率参数错误!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_size) && product.image_size.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片大小参数错误!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_format) && product.image_format.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片类型参数错误!";
			
			return result;
		}
		
		if (StringUtils.isBlank(order_time)) {
			result.code = -1;
			result.msg = "借款产品的排序时间不能为空!";
			
			return result;
		}
		
		if(product.min_amount < 1000 || product.min_amount > product.max_amount || product.max_amount > 100000000
				|| product.min_amount%1000 != 0 || product.max_amount%1000 != 0){
			result.code = -1;
			result.msg = "借款产品的额度范围填写错误!";
			
			return result;
		}
		
		if(product.min_apr < 0.1 || product.min_apr > product.max_apr || product.max_apr > 100.0){
			result.code = -1;
			result.msg = "借款产品的年利率范围填写错误!";
			
			return result;
		}	
		
		if (PeriodUnit.DAY.equals(PeriodUnit.getEnum(period_unit))) {//天标：1-30天；只能一次性还款；借款服务费json公式只有一个有效值
			if (product.min_period<1 || product.min_period>30) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[天],期限范围在[1~30]之间!";
				
				return result;
			}
			if (StringUtils.isBlank(repayment_type) 
					|| !RepaymentType.LUMP_SUM_REPAYMENT.equals(RepaymentType.getEnum(Convert.strToInt(repayment_type.trim(), -99)))) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[天],还款方式只能是["+RepaymentType.LUMP_SUM_REPAYMENT.value+"]!";
				
				return result;
			}
			double loan_amount_rate = 0.00,
				   invest_amount_rate = 0.00,
				   overdue_amount_rate = 0.00;
			try{
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getDouble(Constants.LOAN_AMOUNT_RATE);
			}catch(Exception e){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			if ((loan_amount_rate<0 || loan_amount_rate>100)
					|| (invest_amount_rate < 0.00 || invest_amount_rate > 100.0)
					|| (overdue_amount_rate < 0.00 || overdue_amount_rate > 100.0)) {
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			
		} else if (PeriodUnit.MONTH.equals(PeriodUnit.getEnum(period_unit))) {//月标：1-36个月
			if (product.min_period<1 || product.min_period>36) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[月],期限范围在[1~36]之间!";
				
				return result;
			}
			if (StringUtils.isBlank(repayment_type)) {
				result.code = -1;
				result.msg = "借款产品的还款方式填写错误!";
				
				return result;
			}
			String[] retypes = repayment_type.split(",");
			List<t_product.RepaymentType> retypeList = Arrays.asList(t_product.RepaymentType.values());
			for (String retype:retypes) {
				if (!retypeList.contains(RepaymentType.getEnum(Convert.strToInt(retype.trim(), -99)))) {
					result.code = -1;
					result.msg = "借款产品的还款方式填写错误!";
					
					return result;
				}
			}
			double loan_amount_rate = 0.00,
				   sub_period = 0.00,
				   sub_loan_amount_rate = 0.00,
				   invest_amount_rate = 0.00,
				   overdue_amount_rate = 0.00;
			try{
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getDouble(Constants.LOAN_AMOUNT_RATE);
				sub_period = jsobj.getInt(Constants.SUB_PERIOD);
				sub_loan_amount_rate = jsobj.getDouble(Constants.SUB_LOAN_AMOUNT_RATE); 
				invest_amount_rate = jsobj.getDouble(Constants.INVEST_AMOUNT_RATE);
				overdue_amount_rate = jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE);
			}catch(Exception e){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			if((loan_amount_rate < 0.0 || loan_amount_rate>100.0)
					|| (sub_period < 0)
					|| (sub_loan_amount_rate < 0.0 || sub_loan_amount_rate > 100.0)
					|| (invest_amount_rate < 0.00 || invest_amount_rate > 100.0)
					|| (overdue_amount_rate < 0.00 || overdue_amount_rate > 100.0)){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
		} else {
			result.code = -1;
			result.msg = "借款产品期限单位填写错误!";
			
			return result;
		}
		
		if (t_product.BuyType.PURCHASE_BY_AMOUNT.equals(BuyType.getEnum(buy_type))) {//按金额购买
			if (product.min_invest_amount<1 || product.min_invest_amount>1000) {
				result.code = -1;
				result.msg = "当前选择的购买方式["+BuyType.PURCHASE_BY_AMOUNT.value+"],起投金额范围在[1~1000]之间";
				
				return result;
			}
		} else if (t_product.BuyType.PURCHASE_BY_COPY.equals(BuyType.getEnum(buy_type))) {//按份数购买
			if (product.invest_copies!=10 && product.invest_copies!=20 && product.invest_copies!=50 && product.invest_copies!=100) {
				result.code = -1;
				result.msg = "当前选择的购买方式["+BuyType.PURCHASE_BY_COPY.value+"],拆分份数只能是[10、20、50、100]之间";
				
				return result;
			}
		} else {
			result.code = -1;
			result.msg = "借款产品购买方式填写错误!";
			
			return result;
		}
		
		if(product.bail_scale < 0.0 || product.bail_scale > 100.0 || product.bail_scale%1 != 0){
			result.code = -1;
			result.msg = "保证金占比填写错误,范围在[1~100]之间,只能为正整数!";
			
			return result;
		}
		
		String[] subjects = audit_subjects.split(",");
		for(String subject:subjects){
			if(Convert.strToInt(subject.trim(), -99) == -99){
				result.code = -1;
				result.msg = "请为借款产品添加正确的审核科目!";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 添加借款产品
	 *
	 * @author yaoyi
	 * @createDate 2015年12月15日
	 */
	public static void createProduct(t_product product, int type, int period_unit, int buy_type, 
			String repayment_type, String service_fee_rule, String audit_subjects, String order_time){
		checkAuthenticity();
		
		ResultInfo result = checkCreateProductParams(product, type, period_unit, buy_type, repayment_type, service_fee_rule, audit_subjects, order_time);
		if(result.code < 1){
			flash.error(result.msg);
			
			addCacheFlash(product, type, period_unit, buy_type, repayment_type, service_fee_rule, audit_subjects, order_time);
			toAddProductPre();
		}
		
		//补充product对象
		product.setType(ProductType.getEnum(type));
		product.setPeriod_unit(PeriodUnit.getEnum(period_unit));
		product.setBuy_type(BuyType.getEnum(buy_type));
		product.repayment_type = repayment_type.replace(" " , "");//去掉其中的空格
		
		JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
		JSONObject jso = new JSONObject();
		if (t_product.PeriodUnit.DAY.equals(product.getBuy_type())) {//天标
			jso.put(Constants.LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.LOAN_AMOUNT_RATE));
			jso.put(Constants.SUB_PERIOD, 0);
			jso.put(Constants.SUB_LOAN_AMOUNT_RATE, 0.0);
			jso.put(Constants.INVEST_AMOUNT_RATE, jsobj.getDouble(Constants.INVEST_AMOUNT_RATE));
			jso.put(Constants.OVERDUE_AMOUNT_RATE, jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE));
		} else {//月标
			jso.put(Constants.LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.LOAN_AMOUNT_RATE));
			jso.put(Constants.SUB_PERIOD, jsobj.getInt(Constants.SUB_PERIOD));
			jso.put(Constants.SUB_LOAN_AMOUNT_RATE, jsobj.getDouble(Constants.SUB_LOAN_AMOUNT_RATE));
			jso.put(Constants.INVEST_AMOUNT_RATE, jsobj.getDouble(Constants.INVEST_AMOUNT_RATE));
			jso.put(Constants.OVERDUE_AMOUNT_RATE, jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE));
		}
		product.service_fee_rule = jso.toString();
		product.audit_subjects = audit_subjects.replace(" ", "");
		try{
			product.order_time = DateUtil.strToDate(order_time,  Constants.DATE_PLUGIN);
		}catch(RuntimeException re){
			product.order_time = new Date();
		}
		product.is_use = true;
		
		boolean save = productservice.createProduct(product);
		if(!save){
			flash.error("添加借款产品失败");
			LoggerUtil.error(true, "添加借款产品失败");
			
			addCacheFlash(product, type, period_unit, buy_type, repayment_type, service_fee_rule, audit_subjects, order_time);
			toAddProductPre();
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("product_id", String.valueOf(product.id));
		param.put("product_name", product.name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.PRODUCT_ADD, param);
		
		if(!flag){
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");

			addCacheFlash(product, type, period_unit, buy_type, repayment_type, service_fee_rule, audit_subjects, order_time);
			toAddProductPre();
		}
		
		flash.success("创建借款产品成功!");
		
		showProductPre(0, 1, 10);
	}
	
	/**
	 * 验证失败时，将信息放到cache和flash中，回显页面
	 *
	 * @author yaoyi
	 * @createDate 2016年2月24日
	 */
	private static void addCacheFlash(t_product product, int type, int period_unit, int buy_type, 
			String repayment_type, String service_fee_rule, String audit_subjects, String order_time) {
		
		Cache.set(CacheKey.PRODUCT_ + session.getId(), product, Constants.CACHE_TIME_SECOND_60);
		flash.put("type", type);
		flash.put("period_unit", period_unit);
		flash.put("buy_type", buy_type);
		flash.put("repayment_type", repayment_type);
		try{
			JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
			flash.put("loan_amount_rate", jsobj.getDouble(Constants.LOAN_AMOUNT_RATE));
			flash.put("sub_period", jsobj.getInt(Constants.SUB_PERIOD));
			flash.put("sub_loan_amount_rate", jsobj.getDouble(Constants.SUB_LOAN_AMOUNT_RATE));
			flash.put("invest_amount_rate", jsobj.getDouble(Constants.INVEST_AMOUNT_RATE));
			flash.put("overdue_amount_rate", jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE));
		}catch(Exception e){
			LoggerUtil.info(false,e, "");
		}
		flash.put("audit_subjects", audit_subjects);
		flash.put("order_time", order_time);
		
	}

	/**
	 * 创建借款产品时，参数检查
	 *
	 * @param product 产品对象
	 * @param type 产品类型
	 * @param period_unit 借款期限单位
	 * @param buy_type 购买方式
	 * @param repayment_type 还款方式
	 * @param service_fee_rule 服务费的json
	 * @param audit_subjects 审核科目id列
	 * @param order_time 排序时间
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月24日
	 */
	private static ResultInfo checkCreateProductParams(t_product product, int type, int period_unit, int buy_type, 
			String repayment_type, String service_fee_rule, String audit_subjects, String order_time){
		ResultInfo result = new ResultInfo();
		
		if (product == null) {
			result.code = -1;
			result.msg = "借款产品参数错误!";
			
			return result;
		}
		
		if (!Arrays.asList(t_product.ProductType.values()).contains(t_product.ProductType.getEnum(type))) {
			result.code = -1;
			result.msg = "请为借款产品选择产品类型!";
			
			return result;
		}
		
		if (StringUtils.isBlank(product.name) || product.name.length()<2 || product.name.length()>5) {
			result.code = -1;
			result.msg = "借款产品名称长度为[2~5]个字符!";
			
			return result;
		}
		
		if(productservice.checkHasProduct(product.name)){
			result.code = -1;
			result.msg = "该借款产品["+product.name+"]已经存在";
			
			return result;
		}
		
		if (StringUtils.isBlank(product.image_url) || product.image_url.length()>128) {
			result.code = -1;
			result.msg = "借款产品的图片不能为空!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_resolution) && product.image_resolution.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片分辨率参数错误!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_size) && product.image_size.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片分辨率参数错误!";
			
			return result;
		}
		if (StringUtils.isNotBlank(product.image_format) && product.image_format.length()>64) {//不做非空的控制，只保证长度不超过数据库长度
			result.code = -1;
			result.msg = "借款产品的图片分辨率参数错误!";
			
			return result;
		}
		
		
		if (StringUtils.isBlank(order_time)) {
			result.code = -1;
			result.msg = "借款产品的排序时间不能为空!";
			
			return result;
		}
		
		if(product.min_amount < 1000 || product.min_amount > product.max_amount || product.max_amount > 100000000
				|| product.min_amount%1000 != 0 || product.max_amount%1000 != 0){
			result.code = -1;
			result.msg = "借款产品的额度范围填写错误!";
			
			return result;
		}
		
		if(product.min_apr < 0.1 || product.min_apr > product.max_apr || product.max_apr > 100.0){
			result.code = -1;
			result.msg = "借款产品的年利率范围填写错误!";
			
			return result;
		}	
		
		if (PeriodUnit.DAY.equals(PeriodUnit.getEnum(period_unit))) {//天标：1-30天；只能一次性还款；借款服务费json公式只有一个有效值
			if (product.min_period<1 || product.min_period>30) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[天],期限范围在[1~30]之间!";
				
				return result;
			}
			if (StringUtils.isBlank(repayment_type) 
					|| !RepaymentType.LUMP_SUM_REPAYMENT.equals(RepaymentType.getEnum(Convert.strToInt(repayment_type.trim(), -99)))) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[天],还款方式只能是["+RepaymentType.LUMP_SUM_REPAYMENT.value+"]!";
				
				return result;
			}
			double loan_amount_rate = 0.00,
				   invest_amount_rate = 0.00,
				   overdue_amount_rate = 0.00;
			try{
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getDouble(Constants.LOAN_AMOUNT_RATE);
			}catch(Exception e){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			if ((loan_amount_rate<0 || loan_amount_rate>100)
					|| (invest_amount_rate < 0.00 || invest_amount_rate > 100.0)
					|| (overdue_amount_rate < 0.00 || overdue_amount_rate > 100.0)) {
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			
		} else if (PeriodUnit.MONTH.equals(PeriodUnit.getEnum(period_unit))) {//月标：1-36个月
			if (product.min_period<1 || product.min_period>36) {
				result.code = -1;
				result.msg = "当前选择的期限单位为[月],期限范围在[1~36]之间!";
				
				return result;
			}
			if (StringUtils.isBlank(repayment_type)) {
				result.code = -1;
				result.msg = "借款产品的还款方式填写错误!";
				
				return result;
			}
			String[] retypes = repayment_type.split(",");
			List<t_product.RepaymentType> retypeList = Arrays.asList(t_product.RepaymentType.values());
			for (String retype:retypes) {
				if (!retypeList.contains(RepaymentType.getEnum(Convert.strToInt(retype.trim(), -99)))) {
					result.code = -1;
					result.msg = "借款产品的还款方式填写错误!";
					
					return result;
				}
			}
			double loan_amount_rate = 0.00,
				   sub_period = 0.00,
				   sub_loan_amount_rate = 0.00,
				   invest_amount_rate = 0.00,
				   overdue_amount_rate = 0.00;
			try{
				JSONObject jsobj = JSONObject.fromObject(service_fee_rule);
				loan_amount_rate = jsobj.getDouble(Constants.LOAN_AMOUNT_RATE);
				sub_period = jsobj.getInt(Constants.SUB_PERIOD);
				sub_loan_amount_rate = jsobj.getDouble(Constants.SUB_LOAN_AMOUNT_RATE); 
				invest_amount_rate = jsobj.getDouble(Constants.INVEST_AMOUNT_RATE);
				overdue_amount_rate = jsobj.getDouble(Constants.OVERDUE_AMOUNT_RATE);
			}catch(Exception e){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
			if((loan_amount_rate < 0.0 || loan_amount_rate>100.0)
					|| (sub_period < 0)
					|| (sub_loan_amount_rate < 0.0 || sub_loan_amount_rate > 100.0)
					|| (invest_amount_rate < 0.00 || invest_amount_rate > 100.0)
					|| (overdue_amount_rate < 0.00 || overdue_amount_rate > 100.0)){
				result.code = -1;
				result.msg = "借款产品的收费方式填写错误!";
				
				return result;
			}
		} else {
			result.code = -1;
			result.msg = "借款产品期限单位填写错误!";
			
			return result;
		}
		
		if (t_product.BuyType.PURCHASE_BY_AMOUNT.equals(BuyType.getEnum(buy_type))) {//按金额购买
			if (product.min_invest_amount<1 || product.min_invest_amount>1000) {
				result.code = -1;
				result.msg = "当前选择的购买方式["+BuyType.PURCHASE_BY_AMOUNT.value+"],起投金额范围在[1~1000]之间";
				
				return result;
			}
		} else if (t_product.BuyType.PURCHASE_BY_COPY.equals(BuyType.getEnum(buy_type))) {//按份数购买
			if (product.invest_copies!=10 && product.invest_copies!=20 && product.invest_copies!=50 && product.invest_copies!=100) {
				result.code = -1;
				result.msg = "当前选择的购买方式["+BuyType.PURCHASE_BY_COPY.value+"],拆分份数只能是[10、20、50、100]之间";
				
				return result;
			}
		} else {
			result.code = -1;
			result.msg = "借款产品购买方式填写错误!";
			
			return result;
		}
		
		if(product.bail_scale < 0.0 || product.bail_scale > 100.0 || product.bail_scale%1 != 0){
			result.code = -1;
			result.msg = "保证金占比填写错误,范围在[1~100]之间,只能为正整数!";
			
			return result;
		}
		
		String[] subjects = audit_subjects.split(",");
		for(String subject:subjects){
			if(Convert.strToInt(subject.trim(), -99) == -99){
				result.code = -1;
				result.msg = "请为借款产品添加正确的审核科目!";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		return result;
	} 
	
	/**
	 * 更新借款产品状态
	 *
	 * @param productId
	 * @param productName 产品名称，用于添加管理员事件时使用
	 * @param isUse true-当前状态为上架；false-当前状态为下架
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 */
	public static void updateProductStatus(long productId, String productName, boolean isUse){
		ResultInfo res = new ResultInfo();
		
		if(productId < 1){
			res.code = -1;
			res.msg = "参数错误";
			
			renderJSON(res);
		}
		
		Event ev = isUse ? Event.PRODUCT_DISABLED : Event.PRODUCT_ENABLE;
		
		boolean upd= productservice.updateProductStatus(productId, !isUse);
		if(!upd){
			LoggerUtil.error(true, "更新借款产品上下架状态失败!");
			
			res.code = -1;
			res.msg = "更新借款产品上下架状态失败!";
			renderJSON(res);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", ""+getCurrentSupervisorId());
		param.put("product_id", ""+productId);
		param.put("product_name", productName);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), ev, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败!");
			res.code = -1;
			res.msg = "保存管理员事件失败";
			
			renderJSON(res);
		}
		
		res.code = 1;
		res.msg = "";
		res.obj = !isUse;
		
		renderJSON(res);
	}

	/**
	 * 后台发标->上传产品图片
	 *
	 * @param imgFile
	 *
	 * @author yaoyi
	 * @createDate 2016年1月12日
	 */
	public static void uploadProductImg(File imgFile){
		ResultInfo res = new ResultInfo();
		
		if (imgFile == null) {
			res.code = -1;
			res.msg = "请选择要上传的图片";
			
			renderJSON(res);
		}
		
		res = FileUtil.uploadImgags(imgFile);
		if (res.code < 0) {
			
			renderJSON(res);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) res.obj;
		String staticFileName = (String) fileInfo.get("staticFileName");
		double imageSize = Convert.strToDouble(fileInfo.get("size")+"", 0.0) ;
		int imageHeight = Convert.strToInt(fileInfo.get("height")+"", 0) ;
		int imageWidth = Convert.strToInt(fileInfo.get("width")+"", 0) ;
		String imageFormat = (String) fileInfo.get("fileSuffix");
		JSONObject json = new JSONObject();
		json.put("staticFileName", staticFileName);
		json.put("imageSize", imageSize+"KB");
		json.put("imageFormat", imageFormat);
		json.put("imageResolution", imageWidth+"*"+imageHeight);
		json.put("error", res);
		
		renderText(json.toString());
	}
	
	/**
	 * 上传产品图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadProductImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
	
}
