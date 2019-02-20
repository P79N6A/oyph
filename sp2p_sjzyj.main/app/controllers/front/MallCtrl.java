package controllers.front;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.beans.MallAddress;
import models.beans.MallGoods;
import models.beans.MallScroeGoods;
import models.beans.MallScroeRecord;
import models.beans.MallScroeRule;
import models.common.bean.CostBean;
import models.common.bean.CurrUser;
import models.common.entity.t_advertisement;
import models.common.entity.t_information;
import models.common.entity.t_user;
import models.common.entity.t_user_vip_grade;
import models.common.entity.t_advertisement.Location;
import models.entity.t_mall_address;
import models.entity.t_mall_goods;
import models.entity.t_mall_goods_type;
import models.entity.t_mall_scroe_record;
import models.entity.t_mall_scroe_rule;
import models.entity.v_mall_goods_views;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_ticket_virtual_goods;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_virtual_goods;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import services.common.AdvertisementService;
import services.common.CostService;
import services.common.DevelopEventService;
import services.common.InformationService;
import services.common.UserService;
import services.common.UserVIPGradeService;

import com.google.gson.Gson;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.MallConstants;
import common.enums.Client;
import common.enums.InformationMenu;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;

/**
 * 积分商城
 * 
 * @author yuy
 * @time 2015-10-16 16:30
 *
 */

public class MallCtrl extends FrontBaseController {
	protected static UserService userService = Factory.getService(UserService.class);
	protected static UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	/** 广告图片 */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);

	/** 注入资讯管理services  */
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static CostService costService = Factory.getService(CostService.class);
	
	public static void home2Pre() {
		ResultInfo error = new ResultInfo();

		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		String typeIdStr = params.get("typeId");
		int currPage = 0;
		int pageSize = 0;
		int typeId = -1;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(typeIdStr)) {
			typeId = Integer.parseInt(typeIdStr);
		}
		if (pageSize == 0) {
			pageSize = 12;
		}
	
		PageBean<v_mall_goods_views> pageBean = MallGoods.queryHasExchangedGoodsOrder(currPage, pageSize, error);
		// 商品列表
		PageBean<t_mall_goods> page = MallGoods.queryMallGoodsByPage(null, 1, null, null, currPage, pageSize, error,typeId);
		
		int scroe = 0;
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
		}
		
		render(pageBean,page,typeId,scroe);
	}

	/**
	 * 积分商城首页
	 */
	public static void mallHomePre(int pageSizeInit,Integer typeId) {
		
		if(typeId==null){
			typeId = -1;
		}
		
		ResultInfo error = new ResultInfo();

		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		String typeIdStr = params.get("typeId");
		int currPage = 0;
		int pageSize = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(typeIdStr)) {
			typeId = Integer.parseInt(typeIdStr);
		}
		if (pageSizeInit != 0) {
			pageSize = pageSizeInit;
		}
		int scroe = 0;
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
		}
		
		//积分广告图片
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.SCORE_HOME_TURN_ADS, 3);
		renderArgs.put("banners",banners);

		// 商品兑换排行
		PageBean<v_mall_goods_views> pageBean = MallGoods.queryHasExchangedGoodsOrder(currPage, 6, error);
		// 兑换动态
		List<Map<String, Object>> newsList = MallGoods.queryHasExchangedGoodsNow(9, error);

		// 商品列表
		PageBean<t_mall_goods> page = MallGoods.queryMallGoodsByPage(null, 1, null, null, currPage, 12, error,typeId);
		
		// 商品类型
		List<t_mall_goods_type> goodssList = MallGoods.queryGoodsType();
		List<Map<String, Object>> goodsssList = MallGoods.queryGoodsTypes(6, error);

		//常见问题 35
		List<t_information> cpage = informationService.queryInformationFront(InformationMenu.COMMON_PROBLEM, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("cpage", cpage);
		
		List<t_information> mpage = informationService.queryInformationFront(InformationMenu.MALL_GUIDE, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("mpage", mpage);
		// 常见问题 35
		//PageBean<t_content_news> cpage = News.queryNewsByTypeId(String.valueOf(MallConstants.COMMEN_QUESTION), currPageStr, "8", null, error);
		//Cache.set("cpage", cpage);

		// 商家指引 36
		//PageBean<t_content_news> mpage = News.queryNewsByTypeId(String.valueOf(MallConstants.MALL_GUIDE), currPageStr, "10", null, error);

		t_user_vip_grade vip = getCurrUser() == null ? null : userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ",  getCurrUser().id);
		render(page,pageBean,scroe,newsList,goodssList,goodsssList,typeId,vip);
	}

	/**
	 * 商城指引/常见问题 详情
	 */
/*	public static void contentDetail(long newsId, int type) {

		ResultInfo error = new ResultInfo();

		List<v_news_types> types = NewsType.queryTypeAndCount(type, error);

		if (error.code < 0) {
			render(Constants.ERROR_PAGE_PATH_FRONT);
		}

		List<News> newses = News.queryNewsDetail(newsId + "", null, error);

		if (error.code < 0) {
			render(Constants.ERROR_PAGE_PATH_FRONT);
		}

		Map<String, String> newsCount = News.queryCount(error);

		if (error.code < 0) {
			render(Constants.ERROR_PAGE_PATH_FRONT);
		}

		render(types, type, newses, newsCount);
	}*/
	
	/* liuyang 2017-2-8 -------------begin----------------------- */
	/**
	 * 分页显示已兑换商品列表
	 */
	public static void showExchangedGoodsPre() {
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		ResultInfo error = new ResultInfo();
		
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		
		int currPage = 1;
		int pageSize = 6;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}

		PageBean<t_mall_scroe_record>  page = MallGoods.queryHasExchangedGoodsByList("",userId,currPage,6,error);
		if (error.code < 0) {
			flash.error("抱歉，系统出错，请联系管理员");
		}
		render(page);
	}
	/* liuyang 2017-2-8 --------------end------------------------ */
	
	/**
	 * 已兑换商品列表
	 */
	public static void exchangedGoodsPre() {

		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		ResultInfo error = new ResultInfo();
		
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		
		int currPage = 1;
		int pageSize = 6;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}

		PageBean<t_mall_scroe_record>  page = MallGoods.queryHasExchangedGoodsByList("",userId,currPage,pageSize,error);
		if (error.code < 0) {
			flash.error("抱歉，系统出错，请联系管理员");
		}
		
		int scroe = 0;
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
		}
		
		//积分广告图片
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.SCORE_HOME_TURN_ADS, 3);
		renderArgs.put("banners",banners);
		
		// 商品兑换排行
		PageBean<v_mall_goods_views> pageBean = MallGoods.queryHasExchangedGoodsOrder(1, 6, error);
		
		// 兑换动态
		List<Map<String, Object>> newsList = MallGoods.queryHasExchangedGoodsNow(9, error);
		
		//常见问题 35
		List<t_information> cpage = informationService.queryInformationFront(InformationMenu.COMMON_PROBLEM, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("cpage", cpage);
		
		List<t_information> mpage = informationService.queryInformationFront(InformationMenu.MALL_GUIDE, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("mpage", mpage);
		
		render(page,pageBean,newsList,scroe);
	}
	
	/**
	 * 显示个人积分记录
	 */
	public static void showScroeRecordPre() {
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		ResultInfo error = new ResultInfo();
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		int currPage = 0;
		int pageSize = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		PageBean<t_mall_scroe_record> page = MallScroeRecord.queryScroeRecordByPage(userId, null, 0, null, null, currPage, pageSize, 0, error,"","");
		render(page);
	}

	/**
	 * 个人积分记录
	 */
	public static void scroeRecordPre() {
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		ResultInfo error = new ResultInfo();
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		int currPage = 0;
		int pageSize = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		
		int scroe = 0;
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
		}
		// 商品兑换排行
		PageBean<v_mall_goods_views> pageBean = MallGoods.queryHasExchangedGoodsOrder(1, 5, error);
		
		PageBean<t_mall_scroe_record> page = MallScroeRecord.queryScroeRecordByPage(userId, null, 0, null, null, currPage, pageSize, 0, error,"","");
		// 兑换动态
		List<Map<String, Object>> newsList = MallGoods.queryHasExchangedGoodsNow(6, error);
		
		//常见问题 35
		List<t_information> cpage = informationService.queryInformationFront(InformationMenu.COMMON_PROBLEM, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("cpage", cpage);
		
		List<t_information> mpage = informationService.queryInformationFront(InformationMenu.MALL_GUIDE, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("mpage", mpage);
		
		render(page,pageBean,newsList,scroe);
	}

	/**
	 * 签到
	 */
	public static void sign() {
		
		if(getCurrUser()==null){
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		t_user user = t_user.findById(userId);
		
		ResultInfo error = new ResultInfo();
		JSONObject json = new JSONObject();
		Date today = new Date();

		// 验证签到积分规则
		t_mall_scroe_rule signRule = MallScroeRule.queryRuleDetailByType(MallConstants.SIGN);
		if (signRule == null) {
			error.code = MallConstants.COM_ERROR_CODE;
			error.msg = "对不起，签到送积分活动暂停";
			flash.error(error.msg);
			scroeRecordPre();
		}

		json.put("time", DateUtil.dateToString(today,"yyyy-MM-dd"));// yyyy年MM月dd日
		json.put("scroe", signRule.scroe);

		// 验证当日是否签过
		String dateStr = DateUtil.dateToString(today,"yyyy-MM-dd");// yyyy-MM-dd
		int count = MallScroeRecord.queryScoreRecordByDate(userId, dateStr, MallConstants.SIGN, error);
		if (error.code < 0) {
			error.msg = "对不起，签到失败，请联系客服";
			flash.error(error.msg);
			scroeRecordPre();
		}
		if (count > 0) {
			error.code = -100;
			error.msg = "亲，今天您已签过到了";
			flash.error(error.msg);
			scroeRecordPre();
		}
		
		MallScroeRecord.saveScroeSignRecord(user, signRule.scroe, error);
		if (error.code < 0) {
			error.msg = "对不起，签到失败，请联系客服";
		} else {
			error.msg = "亲，签到成功";
		}
		flash.error(error.msg);
		scroeRecordPre();
	}

	/**
	 * 商品详情
	 * 
	 * @param goods_id
	 */
	public static void goodsDetailPre(long goods_id) {
		ResultInfo error = new ResultInfo();
		
		int scroe = 0;
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
		}
		
		// 商品兑换排行
		PageBean<v_mall_goods_views> pageBean = MallGoods.queryHasExchangedGoodsOrder(1, 5, error);
		
		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		// 兑换动态
		List<Map<String, Object>> newsList = MallGoods.queryHasExchangedGoodsNow(6, error);
		
		//常见问题 35
		List<t_information> cpage = informationService.queryInformationFront(InformationMenu.COMMON_PROBLEM, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("cpage", cpage);
		
		List<t_information> mpage = informationService.queryInformationFront(InformationMenu.MALL_GUIDE, Constants.INFORMATION_ADS_NUM);
		renderArgs.put("mpage", mpage);
		t_user_vip_grade vip = getCurrUser() == null ? null : userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ",  getCurrUser().id);
		render(goods,scroe,pageBean,newsList,vip);
	}

	/**
	 * 开始兑换 查询收货地址
	 */
	public static void exchange() {
		ResultInfo error = new ResultInfo();
		JSONObject json = new JSONObject();
		long userId = getCurrUser().id;
		
		// 查询收货地址
		List<t_mall_address> addressList = MallAddress.queryAddressByList(userId, error);
		if (error.code < 0) {
			error.code = MallConstants.COM_ERROR_CODE;
			error.msg = "对不起，系统出错，请联系客服";
			json.put("error", error);
			renderJSON(json);
		}
		renderJSON(new Gson().toJson(addressList));
	}

	/**
	 * 添加地址
	 */
	public static void addAddress() {
		ResultInfo error = new ResultInfo();
		JSONObject json = new JSONObject();
		long userId = getCurrUser().id;

		String address_id_str = params.get("address_id");
		String receiver = params.get("receiver");
		String tel = params.get("tel");
		String where = params.get("address");
		String postcode = params.get("postcode");

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
		int result = MallAddress.saveAddress(address);
		if (result < 0) {
			error.code = MallConstants.COM_ERROR_CODE;
			error.msg = "对不起，系统出错，请联系客服";
			json.put("error", error);
			renderJSON(json);
		}
		error.code = MallConstants.SUCCESS_CODE;
		json.put("error", error);
		renderJSON(json);
	}

	/**
	 * 修改地址
	 */
	public static void modifyAddress() {
		JSONObject json = new JSONObject();

		String address_id_str = params.get("address_id");
		long address_id = 0;
		if (StringUtils.isNotBlank(address_id_str)) {
			address_id = Long.parseLong(address_id_str);
		}

		// 查询收货地址
		t_mall_address address = MallAddress.queryAddressById(address_id);
		json.put("address", address);
		renderJSON(json);
	}

	public static void updateAddress() {
		ResultInfo error = new ResultInfo();
		String address = params.get("address");
		String remark = params.get("remark");
		String rid = params.get("rid");
		String address3 = "";
		if (address != null && !address.equals("")) {
			String[] address2 = address.split(" ");
			for (int i = 0; i < address2.length; i++) {
				if (i == 0) {
					address3 += "收货地址:" + address2[0] + " </br>";
				}
				if (i == 1) {
					address3 += "收货人:" + address2[1] + " </br>";
				}
				if (i == 2) {
					address3 += "手机号码:" + address2[2] + " </br>";
				}
			}
		}
		address3 = address3 + " 备注：" + remark;
		int code = MallScroeRecord.updateAddess(rid, address3);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		renderJSON(jsonObject);
	}

	/**
	 * 立即兑换
	 */
	public static void submitExchange() {
		ResultInfo error = new ResultInfo();
		String address = params.get("address");
		String remark = params.get("remark");
		String goods_id_str = params.get("goods_id");
		String exchangeNum_str = params.get("exchangeNum");
		
		long goods_id = 0;

		int exchangeNum = 0;
		if (StringUtils.isNotBlank(goods_id_str)) {
			goods_id = Long.parseLong(goods_id_str);
		}
		if (StringUtils.isNotBlank(exchangeNum_str)) {
			exchangeNum = Integer.parseInt(exchangeNum_str);
		}

		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		if (goods == null) {
			error.code=-1;
			error.msg="对不起，商品已下架";
			renderJSON(error);
		}
		
		if(address ==null){
			error.code=-1;
			error.msg="对不起，请选择收货地址";
			renderJSON(error);
		}

		long userId = getCurrUser().id;
		String address3 = "";
		
		int scroe = 0;
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
			
			if (scroe < goods.exchange_scroe*10000*(vip==null?1:vip.use_discount_integral==null?1:vip.use_discount_integral)){
				
				error.code=-1;
				error.msg="对不起，您的积分不足，请兑换其他商品!";
				renderJSON(error);
			}
			
		}
		if (address != null && !address.equals("")) {
			String[] address2 = address.split(" ");
			for (int i = 0; i < address2.length; i++) {
				if (i == 0) {
					address3 += "收货地址：" + address2[0] + " </br>";
				}
				if (i == 1) {
					address3 += "收货人：" + address2[1] + " </br>";
				}
				if (i == 2) {
					address3 += "手机号码：" + address2[2] + " </br>";
				}
			}
		}

		String postDetail = address3 + " 备注：" + remark;

		t_user user = t_user.findById(userId);
		
		
		
		MallGoods.exchangeHandle(exchangeNum, user, goods, postDetail, error);
		if (error.code == -11) {// 没有更新到
			error.code=-1;
			error.msg="对不起，您的积分不足或者该商品已兑换完毕，请兑换其他商品";
			renderJSON(error);
		}
		if (error.code < 0) {
			error.code=-1;
			error.msg="对不起，兑换失败，请联系客服或稍候重试";
			renderJSON(error);
		}
		error.code=1;
		error.msg="兑换成功";
		renderJSON(error);
	}

	public static void logout() {
		ResultInfo result = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}

		result = userService.updateUserLoginOutInfo(currUser.id, Client.PC.code, getIp());
		if (result.code < 0) {
			flash.put("msg", "退出系统时出现异常");
			
			LoginAndRegisteCtrl.loginPre();
		}
		flash.put("msg", "成功退出");
		
		LoginAndRegisteCtrl.loginPre();
	}
	
	public static void jsonpCallback(String nu) {
		
		String datas = HttpUtil.getsMethod(nu);
		
		renderJSON(datas);
	}
	
	/**
	 * 兑换红包虚拟物品
	 * @author guoShiJie
	 * */
	public static void exchangeRedPacketVirtualGoods (String goods_id_str,String exchangeNum_str,String goods_type_name_str) {
		
		ResultInfo error = new ResultInfo();
		
		long goods_id = 0;

		int exchangeNum = 0;
		if (StringUtils.isNotBlank(goods_id_str)) {
			goods_id = Long.parseLong(goods_id_str);
		}
		if (StringUtils.isNotBlank(exchangeNum_str)) {
			exchangeNum = Integer.parseInt(exchangeNum_str);
		}
		
		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		if (goods == null) {
			error.code = -1;
			error.msg = "对不起，商品已下架";
			renderJSON(error);
		}else {
			if (goods.status == 2 || goods.visible == 2) {
				error.code = -1;
				error.msg = "对不起，商品已下架";
				renderJSON(error);
			}
		}

		long userId = getCurrUser().id;
		
		int scroe = 0;
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
			
			if (scroe < goods.exchange_scroe*10000*(vip==null?1:vip.use_discount_integral==null?1:vip.use_discount_integral)){
				
				error.code=-1;
				error.msg="对不起，您的积分不足，请兑换其他商品!";
				renderJSON(error);
			}
			
		}

		t_user user = t_user.findById(userId);
		
		if (!"红包".equals(goods_type_name_str)) {
			error.code = -1;
			error.msg = "对不起，红包未兑换！";
			renderJSON(error);
		}
		
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
			error.code = -1;
			error.msg = "对不起，兑换红包失败！";
			renderJSON(error);
		}
		
		error.code = 1;
		error.msg = "兑换成功";
		renderJSON(error);
		
	}
	
	/**
	 * 兑换加息券虚拟物品
	 * @author guoShiJie
	 * */
	public static void exchangeAddRateTicketVirtualGoods (String goods_id_str,String exchangeNum_str,String goods_type_name_str) {
		
		ResultInfo error = new ResultInfo();
		
		long goods_id = 0;

		int exchangeNum = 0;
		if (StringUtils.isNotBlank(goods_id_str)) {
			goods_id = Long.parseLong(goods_id_str);
		}
		if (StringUtils.isNotBlank(exchangeNum_str)) {
			exchangeNum = Integer.parseInt(exchangeNum_str);
		}
		
		t_mall_goods goods = MallGoods.queryGoodsDetailById(goods_id);
		if (goods == null) {
			error.code = -1;
			error.msg = "对不起，商品已下架";
			renderJSON(error);
		} else {
			if (goods.status == 2 || goods.visible == 2) {
				error.code = -1;
				error.msg = "对不起，商品已下架";
				renderJSON(error);
			}
		}

		long userId = getCurrUser().id;
		
		
		int scroe = 0;
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		if (getCurrUser()!=null) {
			// 查询用户可用积分
			 scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
			Cache.set("scroe_" + getCurrUser().id, scroe);
			
			if (scroe < goods.exchange_scroe*10000*(vip==null?1:vip.use_discount_integral==null?1:vip.use_discount_integral)){
				
				error.code = -1;
				error.msg = "对不起，您的积分不足，请兑换其他商品!";
				renderJSON(error);
			}
			
		}

		t_user user = t_user.findById(userId);
		
		if (!"加息券".equals(goods_type_name_str)) {
			error.code = -1;
			error.msg = "对不起，加息券未兑换！";
			renderJSON(error);
		}
		
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
			error.code = -1;
			error.msg = "对不起，兑换加息券失败！";
			renderJSON(error);
		}
		
		error.code = 1;
		error.msg = "兑换成功";
		renderJSON(error);
		
	}
	
	/**
	 * ajax获取类型名称
	 * @param
	 * @author guoShiJie
	 * @createDate 2018.05.30
	 * */
	public static void getTypeName(long id) {
		JSONObject json = new JSONObject();
		
		t_mall_goods_type goodsType = t_mall_goods_type.findById(id);
		if (goodsType != null) {
			json.put("goodsType", goodsType);
		}
		renderJSON(json);
	}
	
	/**
	 * 定时查询服务费列表信息
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public static void queryCostLists() {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        Calendar calendar = Calendar.getInstance();     
        String s = sdf.format(calendar.getTime());
		
		String beginTime = s+" 00:00:00";
		
		String endTime = s+" 23:59:59";
		
		List<CostBean> costs = costService.queryCosts(beginTime, endTime);
		
		map.put("costs", costs);
		
		String jobStr = Encrypt.encrypt3DES(JSONObject.fromObject(map).toString(), "fbetb4rJnLUUPVSx");
		
		renderJSON(jobStr);
	}
	
	/**
	 * 定时查询服务费列表信息
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public static void queryWithdrawMoney() {
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		String beginTime = DateUtil.getMonthFirstDay();
		
		String endTime = DateUtil.getMonthLastDay();
		
		double money = costService.countWithdraw(beginTime, endTime);
		
		String remark = beginTime.substring(0,beginTime.indexOf(" "))+"至"+endTime.substring(0,endTime.indexOf(" "));
		
		CostBean cost = new CostBean();
		
		cost.incomeAccount= "提现手续费";
		cost.money = money+"";
		cost.time = endTime;
		cost.id = 1;
		cost.remark = remark;
		
		List<CostBean> costs = new ArrayList();
		costs.add(cost);
		
		map.put("costs", costs);
		
		String jobStr = Encrypt.encrypt3DES(JSONObject.fromObject(map).toString(), "fbetb4rJnLUUPVSx");
		
		renderJSON(jobStr);
	}
}
