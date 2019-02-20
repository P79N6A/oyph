package controllers.app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import com.shove.Convert;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import payment.impl.PaymentProxy;
import play.cache.Cache;
import models.app.bean.DebtTransferBean;
import models.app.bean.IntegralGoodsType;
import models.app.bean.InvestBidsBean;
import models.beans.MallAddress;
import models.beans.MallGoods;
import models.beans.MallScroeRecord;
import models.beans.MallScroeRule;
import models.common.entity.t_advertisement;
import models.common.entity.t_information;
import models.common.entity.t_user;
import models.common.entity.t_user_vip_grade;
import models.entity.t_mall_address;
import models.entity.t_mall_goods;
import models.entity.t_mall_goods_type;
import models.entity.t_mall_scroe_record;
import models.entity.t_mall_scroe_rule;
import models.common.entity.t_advertisement.Location;

import models.ext.experience.entity.t_experience_bid;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_ticket_virtual_goods;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_virtual_goods;
import net.sf.json.JSONObject;
import service.DebtAppService;
import service.IntegralMallService;
import service.InvestAppService;
import service.ext.experiencebid.ExperienceBidService;
import services.common.AdvertisementService;
import services.common.InformationService;
import services.common.UserVIPGradeService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.MallConstants;
import common.enums.Client;
import common.enums.InformationMenu;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import jobs.IndexStatisticsJob;


/**
 * app首页
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年6月30日
 */
public class HomeAction {
	protected static UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	protected static AdvertisementService advertisementService =  Factory.getService(AdvertisementService.class);
	
	protected static InformationService informationService =  Factory.getService(InformationService.class);
	
	protected static InvestAppService investAppService = Factory.getService(InvestAppService.class);
	
	protected static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);
	
	private static IntegralMallService integralMallService = Factory.getService(IntegralMallService.class);
			
	public static String index() throws Exception {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		//广告图片
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.APP_HOME_TURN_ADS, 8);
		if (banners == null) {
			result.put("banners", null);
		} else {
			List<Map<String, Object>> bannerList = new ArrayList<Map<String,Object>>();
			for (t_advertisement banner : banners) {
				Map<String, Object> bannerMap = new HashMap<String, Object>();
				bannerMap.put("image_url", banner.image_url);
				bannerMap.put("url", banner.url);
				
				bannerList.add(bannerMap);
			}
			
			result.put("banners", bannerList);
		}
		
		// 体验标
		t_experience_bid expBid = experienceBidService.findExperienceBidFront();
		if (expBid == null) {
			result.put("expBid", null);
			
		} else {
			Map<String, Object> expBidMap = new HashMap<String, Object>();
			expBidMap.put("expBidId", expBid.id);
			expBidMap.put("title", expBid.title);
			expBidMap.put("apr", expBid.apr);
			expBidMap.put("period", expBid.period);
			expBidMap.put("min_invest_amount", expBid.min_invest_amount);
			expBidMap.put("repayment_type", expBid.repayment_type);
			expBidMap.put("is_overdue", expBid.is_overdue);
			
			result.put("expBid", expBidMap);
		}

		
		//推荐标-散标投资
		List<InvestBidsBean> invests = investAppService.listOfInvestBids(2);
		result.put("invests", invests);
		
		//推荐标-债权
		PageBean<DebtTransferBean> debts = debtAppService.pageOfDebts(1, 2);
		result.put("debts", debts.page);
		
		/**app首页显示四个数据*/
		Map<String, Object> fiveList = new HashMap<>();
		
		//1.累计用户数
		String userCount = IndexStatisticsJob.userCount;
		//2.累计交易额
		String totalBorrowAmount = IndexStatisticsJob.totalBorrowAmount;
		//3.累计为用户赚取收益
		String userBenefit = IndexStatisticsJob.income;
		//4.到期还款率
		double expireRepaymentRate = IndexStatisticsJob.expireRepaymentRate;
		//5.运营天数
		int serviceTime = IndexStatisticsJob.serviceTime;
		
		fiveList.put("userCount", userCount);
		fiveList.put("totalBorrowAmount", totalBorrowAmount);
		fiveList.put("userBenefit", userBenefit);
		fiveList.put("expireRepaymentRate", new DecimalFormat("0.0").format(expireRepaymentRate));
		fiveList.put("serviceTime", serviceTime);
		
		result.put("fiveList", fiveList);
		
		return JSONObject.fromObject(result).toString();
	}
	
	
	/**
	 * 积分商城首页
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static String Mallindex(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		
		result.put("code", 1);
		result.put("msg", "成功");
		
		/** 积分商城轮播广告图片 */
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.APP_INTEGRAL_TURN_ADS, 4);
		if (banners == null) {
			result.put("banners", null);
			
		} else {
			List<Map<String, Object>> bannerList = new ArrayList<Map<String,Object>>();
			for (t_advertisement banner : banners) {
				Map<String, Object> bannerMap = new HashMap<String, Object>();
				bannerMap.put("image_url", banner.image_url);
				bannerMap.put("url", banner.url);
				
				bannerList.add(bannerMap);
			}
			
			result.put("banners", bannerList);
		}
		
		/** 积分商城商品类型 */
		result.put("goodsType", integralMallService.listOfGoodsTypes());
		
		
		String currentPageStr = parameters.get("currPage");
		String keywords = parameters.get("keywords");
		String signUserId = parameters.get("userId");
		
		int scroe = 0;
		
		if(StringUtils.isNotBlank(signUserId)){
			
			error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
			}
			long userId = Long.parseLong((String)error.obj);
			
			scroe = MallScroeRecord.queryScoreRecordByUser(userId, error);
			
		}
		
		result.put("scroe", scroe);
		
		String pageNumStr = parameters.get("pageSize");


		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}

		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		Integer typeId = -1;
		// 商品列表
		PageBean<t_mall_goods> page = MallGoods.queryMallGoodsByPage(keywords, 1, null, null, currPage, pageSize, new ResultInfo(),typeId);
		if (page.page == null) {
			result.put("code", 1);
			result.put("msg", "查询为空");
			result.put("malls", null);
		}else{
			
			for (t_mall_goods goods : page.page) {
				t_mall_goods_type goodType = t_mall_goods_type.findById((long)goods.type_id);
				if (goodType != null) {
					goods.type_name = goodType.goods_type;
					goods.type = goodType.type;
				}
			}
			result.put("code", 1);
			result.put("msg", "查询成功");
			result.put("malls", page.page);
		}
		
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 商品详情
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static String mallDetails(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		
		String goodIdStr = parameters.get("goodId");
		
		long goodId = Long.parseLong(goodIdStr);
		
		if(goodId<=0){
			json.put("code", -1);
			json.put("msg", "商品id有误");
			return json.toString();
		}
		
		t_mall_goods goods = MallGoods.queryGoodsDetailById(goodId);
		goods.introduction = "<div style = 'text-align:center;' >"+goods.introduction+"</div>";
		
		result.put("code", 1);
		result.put("msg", "查询成功");
		result.put("goods", goods);
		return JSONObject.fromObject(result).toString();
	}
	
	public static String receiptAddres(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		
		String signUserId = parameters.get("userId");
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
				}
		long userId = Long.parseLong((String)error.obj);
		
		// 查询收货地址
		List<t_mall_address> addressList = MallAddress.queryAddressByList(userId, error);
		if (error.code < 0) {
			error.code = -1;
			error.msg = "对不起，查询失败";
			return json.toString();
		}
		result.put("code", 1);
		result.put("msg", "查询成功");
		result.put("addressList", addressList);
		return JSONObject.fromObject(result).toString();
	}

	public static String exchangeGoods(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		
		String signUserId = parameters.get("userId");
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
				}
		long userId = Long.parseLong((String)error.obj);
		
		String address = parameters.get("address");
		String remark = parameters.get("remark");
		String goods_id_str = parameters.get("goods_id");
		String exchangeNum_str = parameters.get("exchangeNum");

		long goods_id = 0;
		long address_id =0;

		int exchangeNum = 0;
		if (StringUtils.isNotBlank(goods_id_str)) {
			goods_id = Long.parseLong(goods_id_str);
		}
		if (StringUtils.isNotBlank(exchangeNum_str)) {
			exchangeNum = Integer.parseInt(exchangeNum_str);
		}
		if (StringUtils.isNotBlank(address)) {
			address_id = Integer.parseInt(address);
		}
		
		if(address_id<=0 || exchangeNum <=0 || goods_id<=0){
			
			json.put("code", -1);
			json.put("msg", "参数有误");
			return json.toString();
		}
		
		

		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		if (goods == null) {
			json.put("code", -1);
			json.put("msg", "对不起，商品已下架");
			return json.toString();
		} else {
			if (goods.status == 2 || goods.visible == 2) {
				json.put("code", -1);
				json.put("msg", "对不起，商品已下架");
				return json.toString();
			}
		}
		
		t_mall_address address1 = MallAddress.queryAddressById(address_id);
		
		if(address1 ==null){
			json.put("code", -1);
			json.put("msg", "对不起，请选择收货地址");
			return json.toString();
		}

		String address3 = "收货地址："+address1.address+" </br>"+"收货人：" + address1.receiver + " </br>"+"手机号码：" + address1.tel + " </br>";

		String postDetail = address3 + " 备注：" + remark;

		t_user user = t_user.findById(userId);

		int scroe = MallScroeRecord.queryScoreRecordByUser(user.id, error);
		
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		
		if ( scroe < goods.exchange_scroe*10000*(vip==null?1:vip.use_discount_integral==null?1:vip.use_discount_integral)) {
			json.put("code", -1);
			json.put("msg", "对不起，您的积分不足或者该商品已兑换完毕，请兑换其他商品");
			return json.toString();
		}
		MallGoods.exchangeHandle(exchangeNum, user, goods, postDetail, error);
		
		scroe = MallScroeRecord.queryScoreRecordByUser(user.id, error);
		Cache.set("scroe_" + user.id, scroe);
				
		if (error.code == -11) {// 没有更新到
			json.put("code", -1);
			json.put("msg", "对不起，您的积分不足或者该商品已兑换完毕，请兑换其他商品");
			return json.toString();
		}
		if (error.code < 0) {
			json.put("code", -1);
			json.put("msg", "对不起，兑换失败，请联系客服或稍候重试");
			return json.toString();
		}
		json.put("code", 1);
		json.put("msg", "兑换成功");
		return json.toString();
	} 
	
	public static String  updateAdress(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		String signUserId = parameters.get("userId");
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
				}
		long userId = Long.parseLong((String)error.obj);

		String address_id_str = parameters.get("address_id");
		String receiver = parameters.get("receiver");
		String tel = parameters.get("tel");
		String where = parameters.get("address");
		String postcode = parameters.get("postcode");

		long address_id = 0;
		if (StringUtils.isNotBlank(address_id_str)) {
			address_id = Long.parseLong(address_id_str);
		}
		// 查询收货地址
		t_mall_address address = new t_mall_address();
		address.id =  address_id==0?null:address_id ;
		address.user_id = userId;
		address.time = new Date();
		address.receiver = receiver;
		address.tel = tel;
		address.address = where;
		address.postcode = postcode;
		int results = MallAddress.saveAddress(address);
		if (results < 0) {
			
			json.put("code", -1);
			json.put("msg", "对不起，系统出错，请联系客服");
			return json.toString();
		}
		json.put("code", 1);
		json.put("msg", "修改成功");
		return json.toString();
	}
	
	public static String integralRecord(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		String signUserId = parameters.get("userId");
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
				}
		long userId = Long.parseLong((String)error.obj);

		String currPageStr = parameters.get("currPage");
		String pageSizeStr = parameters.get("pageSize");
		String startTimeStr = parameters.get("startTime");
		String endTimdStr = parameters.get("endTimd");
		String typeStr = parameters.get("type");
		
		int type = Convert.strToInt(typeStr, 0);
		
		int currPage = 0;
		int pageSize = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		
		
		PageBean<t_mall_scroe_record> page = MallScroeRecord.queryScroeRecordByPage(userId, null, type, null, null, currPage, pageSize, 0, error,startTimeStr,endTimdStr);
		
		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("page", page.page);
		return json.toString();
		
	}
	
	public static String alreadyConvertible(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		String signUserId = parameters.get("userId");
		String keywords = parameters.get("keywords");
		String currPageStr = parameters.get("currPage");
		String pageSizeStr = parameters.get("pageSize");
		int currPage = 1;
		int pageSize = 10;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
			if (error.code < 1) {
				json.put("code", ResultInfo.LOGIN_TIME_OUT);
				json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
				return json.toString();
				}
		long userId = Long.parseLong((String)error.obj);
		

		PageBean<t_mall_scroe_record> exgoodsList = MallGoods.queryHasExchangedGoodsByList(keywords,userId,currPage,pageSize, error);
		if (error.code < 0) {
			json.put("code", -1);
			json.put("msg", "抱歉，系统出错，请联系管理员");
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("exgoodsList", exgoodsList);
		return json.toString();
	}
	
	/**
	 * 登录汇付
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static String hfLogin(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		String signUserId = parameters.get("userId");
		
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (error.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong((String)error.obj);
		
		error = PaymentProxy.getInstance().login(Client.ANDROID.code, userId, null);
		
		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("html", error.obj.toString());
		return json.toString();
	}
	
	
	public static String sign(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		String signUserId = parameters.get("userId");
		
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (error.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong((String)error.obj);
		t_user user = t_user.findById(userId);
		Date today = new Date();

		// 验证签到积分规则
		t_mall_scroe_rule signRule = MallScroeRule.queryRuleDetailByType(MallConstants.SIGN);
		if (signRule == null) {
			json.put("code", -1);
			json.put("msg", "对不起，签到送积分活动暂停");
			return json.toString();
		}

		json.put("time", DateUtil.dateToString(today,"yyyy-MM-dd"));// yyyy年MM月dd日
		json.put("scroe", signRule.scroe);

		// 验证当日是否签过
		String dateStr = DateUtil.dateToString(today,"yyyy-MM-dd");// yyyy-MM-dd
		int count = MallScroeRecord.queryScoreRecordByDate(userId, dateStr, MallConstants.SIGN, error);
		if (error.code < 0) {
			json.put("code", -1);
			json.put("msg", "对不起，签到失败，请联系客服");
			return json.toString();
		}
		if (count > 0) {
			json.put("code", -1);
			json.put("msg", "亲，今天您已签过到了");
			return json.toString();
		}
		
		MallScroeRecord.saveScroeSignRecord(user, signRule.scroe, error);
		if (error.code < 0) {
			json.put("code", -1);
			json.put("msg", "对不起，签到失败，请联系客服");
			return json.toString();
			
		} else {
			json.put("code", 1);
			json.put("msg", "亲，签到成功");
			return json.toString();
		}
	}
	
	public static String exchangeVirtualGoods(Map<String,String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		ResultInfo error = new ResultInfo();
		
		String signUserId = parameters.get("userId");
		error = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (error.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
		}
		long userId = Long.parseLong((String)error.obj);
		
		String typeName = parameters.get("type_name");
		String goods_id_str = parameters.get("goods_id");
		String exchangeNum_str = parameters.get("exchangeNum");
		
		long goods_id = StringUtils.isNotBlank(goods_id_str)?Long.parseLong(goods_id_str):0;
		int exchangeNum = StringUtils.isNotBlank(exchangeNum_str)?Integer.parseInt(exchangeNum_str):0;
		if (typeName == null||goods_id <= 0 || exchangeNum <= 0) {
			json.put("code", -1 );
			json.put("msg", "参数错误");
			return json.toString();
		}
		
		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		if (goods == null) {
			json.put("code", -1);
			json.put("msg", "对不起，商品已下架");
			return json.toString();
		} else {
			if (goods.status == 2 || goods.visible == 2) {
				json.put("code", -1);
				json.put("msg", "对不起，商品已下架");
				return json.toString();
			}
		}
		
		t_user user = t_user.findById(userId);
		int scroe = MallScroeRecord.queryScoreRecordByUser(user.id, error);
		
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		
		if ( scroe < goods.exchange_scroe*10000*(vip==null?1:vip.use_discount_integral==null?1:vip.use_discount_integral)) {
			json.put("code", -1);
			json.put("msg", "对不起，您的积分不足或者该商品已兑换完毕，请兑换其他商品");
			return json.toString();
		}
		if (typeName.equals("红包")) {
			t_red_packet_virtual_goods redPacket = t_red_packet_virtual_goods.findById(goods.virtual_goods_id);
			Date today = new Date();
			t_red_packet_user redpacket_user = new t_red_packet_user();
			redpacket_user.time = today;
			redpacket_user._key = redPacket._key;
			redpacket_user.red_packet_name = redPacket.red_packet_name;
			redpacket_user.user_id = userId;
			redpacket_user.amount = redPacket.amount;
			redpacket_user.use_rule = redPacket.use_rule;
			redpacket_user.limit_day = redPacket.limit_day;
			redpacket_user.limit_time = DateUtil.addDay(today, redPacket.limit_day);
			redpacket_user.lock_time = null;
			redpacket_user.invest_id = 0;
			redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.RECEIVED);
			MallGoods.exchangeRedPacket(redpacket_user,exchangeNum,user,goods,error);
			if (error.code < 0){
				json.put("code", -1);
				json.put("msg", "对不起，兑换红包失败！");
				return json.toString();
			}
			json.put("code", 1);
			json.put("msg", "兑换红包成功");
			return json.toString();
		}
		if (typeName.equals("加息券")) {
			t_add_rate_ticket_virtual_goods rateTicket = t_add_rate_ticket_virtual_goods.findById(goods.virtual_goods_id);
			t_add_rate_ticket addRate = new t_add_rate_ticket();
			addRate.act_id = 0;
			addRate.apr = rateTicket.apr;
			addRate.day = rateTicket.day;
			addRate.amount = rateTicket.amount;
			addRate.small = 0;
			addRate.large = 0;
			addRate.type = 0;
			
			MallGoods.exchangeAddRateTicket(addRate,exchangeNum,user,goods,error);
			if (error.code < 0){
				json.put("code", -1);
				json.put("msg", "对不起，兑换加息券失败！");
				return json.toString();
			}
			json.put("code", 1);
			json.put("msg", "兑换加息券成功");
			return json.toString();
		}
		json.put("code", -1);
		json.put("msg", "对不起，虚拟商品未兑换");
		return json.toString();
		
	}
}
