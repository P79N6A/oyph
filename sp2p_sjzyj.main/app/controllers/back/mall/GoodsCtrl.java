package controllers.back.mall;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import models.beans.MallGoods;
import models.entity.t_mall_goods;
import models.entity.t_mall_goods_type;
import models.ext.redpacket.entity.t_add_rate_ticket_virtual_goods;
import models.ext.redpacket.entity.t_red_packet_virtual_goods;
import play.db.jpa.JPA;

import org.apache.commons.lang.StringUtils;

import common.constants.MallConstants;
import common.constants.ext.RedpacketKey;
import common.utils.EmailUtil;
import common.utils.NumberUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;

/**
 * 积分商城：商品
 * 
 * @author yuy
 * @created 2015-10-14
 */
public class GoodsCtrl extends BackBaseController {

	/**
	 * 商品列表
	 */
	public static void goodsListPre() {
		ResultInfo resultInfo = new ResultInfo();
		
		String orderType = params.get("orderType");
		String orderStatus = params.get("orderStatus");
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		String name = params.get("name");
		String statusStr = params.get("status");
		String typeId = params.get("typeId");
		//String type =params.get("type");
		//String price=params.get("price");
		int currPage = 0;
		int pageSize = 0;
		int status = 0;
		int typeIds = -1;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		if (StringUtils.isNotBlank(statusStr)) {
			status = Integer.parseInt(statusStr);
		}
		if (StringUtils.isNotBlank(typeId)) {
			typeIds = Integer.parseInt(typeId);
		}
		PageBean<t_mall_goods> page = MallGoods.queryMallGoodsByPage(name, status, orderType, orderStatus, currPage, pageSize, resultInfo,typeIds);
		if(null!=page && null!=page.page){
			for(t_mall_goods p:page.page){
				t_mall_goods_type type=MallGoods.queryGoodsTypeDetailById(p.type_id);
				if(null==type){
					p.type_name="未分类";
				}else{
					p.type_name=type.goods_type;
					p.type = type.type;
				}
			}
		}
		if (resultInfo.code < 0) {
			flash.error("抱歉，系统出错，请联系管理员");
		}
		render(page);
	}
	/**
	 * 商品类型列表
	 * author：lihuijun
	 * date:2017-2-6
	 */
	public static void goodsTypeListPre(){
		ResultInfo resultInfo = new ResultInfo();
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
		PageBean<t_mall_goods_type> page=MallGoods.queryMallGoodsTypeByPage(currPage, pageSize);
		
		render(page);
	}

	/**
	 * 保存商品
	 */
	public static void saveGoods() {
		String id = params.get("id");
		String goodsTypeId=params.get("goodsType");
		String name = params.get("name");
		String picPath = params.get("filename");
		String introduction = params.get("introduction");
		String countStr = params.get("total");
		String maxExchangeCountStr = params.get("max_exchange_count");
		String exchangeScroeStr = params.get("exchange_scroe");
		String imageFormat = params.get("imageFormat");
		//String typeStr = params.get("type");
		//String priceStr = params.get("price");

		/*int type = 0 ;
		if(StringUtils.isBlank(typeStr)){
			flash.error("请选择礼品区");
			goodsListPre();
		}
		if(StringUtils.isNumeric(typeStr)){
			type = Integer.parseInt(typeStr) ;
			if(!(type== 1 || type==2 || type==3)){
				flash.error("请选择礼品区");
				goodsListPre() ;
			}
		}else{
			flash.error("请选择礼品区");
			goodsListPre() ;
		}*/
		
		/*double price = 0 ;
		if(StringUtils.isBlank(priceStr)){
			flash.error("请输入市场价");
			goodsListPre();
		}
		if(NumberUtil.isNumericDouble(priceStr)){
			price = Double.parseDouble(priceStr) ;
			if(price <0 || price >99999999){
				flash.error("请输入正确市场价");
				goodsListPre();
			}
		}else{
			flash.error("请输入正确市场价");
			goodsListPre();
		}
		*/
		try {
			introduction = URLDecoder.decode(introduction, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			flash.error("保存失败");
			goodsListPre();
		}
		if (StringUtils.length(introduction) > 16777215) {
			flash.error("对不起,商品介绍字数不能超过16777215个字符");
			goodsListPre();
		}
		introduction = EmailUtil.replaceAllHTML(introduction);

		if (StringUtils.isBlank(picPath) ) {
			flash.error("请选择上传商品图片");
			goodsListPre();
		}

		t_mall_goods goods = new t_mall_goods();
		goods.id = StringUtils.isNotBlank(id) ? Long.parseLong(id) : null;
		goods.type_id=Integer.parseInt(goodsTypeId);
		goods.name = name;
		goods.time = new Date();
		goods.pic_path = picPath;
		goods.introduction = introduction;
		goods.total = StringUtils.isNotBlank(countStr) ? Integer.parseInt(countStr) : 0;
		goods.max_exchange_count = StringUtils.isNotBlank(maxExchangeCountStr) ? Integer.parseInt(maxExchangeCountStr) : 0;
		goods.surplus = goods.max_exchange_count;
		goods.exchange_scroe = StringUtils.isNotBlank(exchangeScroeStr) ? Integer.parseInt(exchangeScroeStr) : 0;
		goods.status = MallConstants.STATUS_ENABLE;
		goods.visible = MallConstants.VISIBLE;
		goods.pic_format = imageFormat;
		//goods.type = type;
		//goods.price = price;

		int result = MallGoods.saveGoodsDetail(goods);
		if (result < 0) {
			flash.error("抱歉，保存失败，请联系管理员");
		} else {
			flash.error("保存成功");
		}
		goodsListPre();
	}
	/**
	 * 保存商品分类
	 * author：lihuijun
	 * date:2017-2-5
	 */
	public static void saveGoodsType(){
		String id = params.get("id");
		String goods_type=params.get("goods_type");
		String picPath = params.get("filename");
		String imageFormat = params.get("imageFormat");
		
		t_mall_goods_type goodsType=new t_mall_goods_type();
		goodsType.id = StringUtils.isNotBlank(id) ? Long.parseLong(id) : null;
		goodsType.goods_type=goods_type;
		goodsType.pic_path=picPath;
		goodsType.pic_format = imageFormat;
		int result=MallGoods.saveGoodsType(goodsType);
		goodsTypeListPre();
	}

	/**
	 * 编辑商品 页面
	 * 
	 * @param id
	 * @param flag
	 *            1:新增 2：修改
	 */
	public static void editGoodsPre(long id, int flag) {
		t_mall_goods goods = null;
		if (flag == MallConstants.MODIFY)
			goods = MallGoods.queryGoodsDetailById(id);
		List<t_mall_goods_type> typeList=MallGoods.queryAllGoodsType();
		render(goods, flag, typeList);
	}

	/**
	 * 去添加 修改商品分类
	 * author：lihuijun
	 * date:2017-2-5
	 *   flag  1:新增 2：修改
	 */
	public static void editGoodsTypePre(long id, int flag){
		t_mall_goods_type goods = null;
		if(flag == MallConstants.MODIFY)
			goods=MallGoods.queryGoodsTypeDetailById(id);
			render(goods,flag);
	}
	

	public static void updateGoodsTypePre(long id){
		
		render(id);
	}
	/**
	 * 删除商品
	 * 
	 * @param id
	 */
	public static void deleteGoods(long id) {
		int result = MallGoods.deleteGoodsDetail(id);
		if (result < 0) {
			flash.error("抱歉，删除失败，请联系管理员");
		} else {
			flash.error("删除成功");
		}
		goodsListPre();
	}
	/**
	 * 删除商品分类
	 * author：lihuijun
	 * date:2017-2-5
	 * @param id
	 */
	public static void deleteGoodsType(long id){
		int result = MallGoods.deleteGoodsTypeDetail(id);
		if(result <0){
			flash.error("抱歉，删除失败，请联系管理员");
		}else{
			flash.error("删除成功");
		}
		goodsTypeListPre();
	}
	
	/**
	 * 暂停/开启商品兑换
	 * 
	 * @param id
	 */
	public static void stopGoods(long id, int status) {
		int result = MallGoods.stopGoodsExchange(id, status);
		String opeStr = status == MallConstants.STATUS_ENABLE ? MallConstants.STR_ENABLE : MallConstants.STR_DISABLE;
		if (result < 0) {
			flash.error("抱歉，%s失败，请联系管理员", opeStr);
		} else {
			flash.error("%s成功", opeStr);
		}
		goodsListPre();
	}
	
	/**
	 * 一键开启
	 * 
	 * author：liuyang
	 */
	public static void startGoods() {
		
		List<t_mall_goods> goods = MallGoods.queryGoodss();
		
		if(goods.size()>0) {
			for (int i = 0; i < goods.size(); i++) {
				goods.get(i).status = 2;
				goods.get(i).save();
			}
		}
		
		goodsListPre();
	}
	
	/**
	 * 一键暂停
	 * 
	 * author：liuyang
	 */
	public static void endGoods() {
		List<t_mall_goods> goods = MallGoods.queryGoodss();
		
		if(goods.size()>0) {
			for (int i = 0; i < goods.size(); i++) {
				goods.get(i).status = 1;
				goods.get(i).save();
			}
		}
		
		goodsListPre();
	}
	/**
	 * 跳转至编辑虚拟商品
	 * @author guoShiJie
	 * @createDate 2018.05.24
	 * */
	public static void virtualGoodsPre(long id,int flag) {
		
		t_mall_goods goods = null;
		if (flag == MallConstants.MODIFY)
			goods = MallGoods.queryGoodsDetailById(id);
		List<t_mall_goods_type> typeList=MallGoods.queryAllVirtualGoodsType();
		t_red_packet_virtual_goods packet = null;
		t_add_rate_ticket_virtual_goods ticket = null;
		if (goods != null) {
			if (goods.name.indexOf("红包") != -1) {
				packet = t_red_packet_virtual_goods.findById(goods.virtual_goods_id);
			} else if (goods.name.indexOf("加息券") != -1) {
				ticket = t_add_rate_ticket_virtual_goods.findById(goods.virtual_goods_id);
			}
		}
		
		render(goods, flag, typeList, packet, ticket);
	}
	
	/**保存虚拟商品
	 * @author guoShiJie
	 * @createDate 2018.05.24
	 * */
	public static void saveVirtualGoods() {
		String id = params.get("id");//商品id
		String goodsTypeId=params.get("goodsType");//商品类型
		String name = params.get("name");//商品名称
		String picPath = params.get("filename");//图片路径
		String introduction = params.get("introduction");//商品介绍
		
		int saleCount = -1;
		String countTotal = params.get("goods.inventory");
		String countStr = params.get("unlimited_inven");//商品库存
		if ("0".equals(countStr)) {
			saleCount = Integer.parseInt(countTotal);
		}
		
		int maxNum = -1;
		String maxCount = params.get("goods.exchange_maximum");
		String maxExchangeCountStr = params.get("maximum");//兑换上限
		if ("0".equals(maxExchangeCountStr)) {
			maxNum = Integer.parseInt(maxCount);
		}
		
		String exchangeScroeStr = params.get("goods.spend_scroe");//兑换积分
		String imageFormat = params.get("image");//上传图片
		String goodsPropertyId = params.get("goods.attribute");//商品属性
		
		try {
			introduction = URLDecoder.decode(introduction, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			flash.error("保存失败");
			goodsListPre();
		}
		if (StringUtils.length(introduction) > 16777215) {
			flash.error("对不起,商品介绍字数不能超过16777215个字符");
			goodsListPre();
		}
		introduction = EmailUtil.replaceAllHTML(introduction);

		if (StringUtils.isBlank(picPath) ) {
			flash.error("请选择上传商品图片");
			goodsListPre();
		}
		Date today = new Date();
		
		String virtualType = MallGoods.queryVirtualGoodsTypeById(Integer.parseInt(goodsPropertyId));;
		long virtualId = 0;
		if ("红包".equals(virtualType) ) {
			String redPacketName = params.get("name");//红包名称
			String redPacketAmount = params.get("attributeValue1");//红包金额
			String redPacketUseRule = params.get("goods.min_invest_amount");//红包使用的最低金额
			String redPacketLimitDay = params.get("goods.limit_day");//红包的有效时长
			String redPacketBidLimit = params.get("goods.bid_limit");//标的期限(标的期限大于该期限)
			String redPacketId = params.get("virtualID");//红包id
			
			t_red_packet_virtual_goods redPacket = new t_red_packet_virtual_goods();
			
			redPacket.id = StringUtils.isNotBlank(redPacketId) ? Long.parseLong(redPacketId) : null;
			redPacket._key = RedpacketKey.INTEGRAL_MALL_REDPACKET;
			redPacket.red_packet_name = redPacketName;
			redPacket.limit_day = Integer.parseInt(redPacketLimitDay);
			redPacket.time = today;
			redPacket.use_rule = Double.parseDouble(redPacketUseRule);
			redPacket.amount = Double.parseDouble(redPacketAmount);
			redPacket.bid_period = Integer.parseInt(redPacketBidLimit);
			
			int saveRes = MallGoods.saveRedPacketDetail(redPacket);
			if (saveRes != 1) {
				JPA.setRollbackOnly();
				flash.error("添加虚拟商品红包失败");
				goodsListPre();
			}
			virtualId = MallGoods.queryLastVirtualGoodsId(virtualType);
		}
		
		if ("加息券".equals(virtualType)) {
			String couponConvert = params.get("attributeValue2");//加息券利率
			String couponLimitDay= params.get("goods.limit_day");//加息券有效期
			String couponUseRule = params.get("goods.min_invest_amount");//加息券使用的最低金额
			String couponId = params.get("virtualID");//加息券id
			
			t_add_rate_ticket_virtual_goods addRateTicket = new t_add_rate_ticket_virtual_goods();
			addRateTicket.id =  StringUtils.isNotBlank(couponId) ? Long.parseLong(couponId) : null;
			addRateTicket.apr = Double.parseDouble(couponConvert);
			addRateTicket.day = Integer.parseInt(couponLimitDay);
			addRateTicket.amount = Double.parseDouble(couponUseRule);
			addRateTicket.time = today;
			
			int saveRes = MallGoods.saveCouponDetail(addRateTicket);
			if (saveRes != 1) {
				JPA.setRollbackOnly();
				flash.error("添加虚拟商品加息券失败");
				goodsListPre();
			}
			virtualId = MallGoods.queryLastVirtualGoodsId(virtualType);
		}
		
		t_mall_goods goods = new t_mall_goods();
		goods.id = StringUtils.isNotBlank(id) ? Long.parseLong(id) : null;
		goods.type_id=Integer.parseInt(goodsPropertyId);
		goods.name = name;
		goods.time = today;
		goods.pic_path = picPath;
		goods.introduction = introduction;
		goods.total = saleCount;
		goods.max_exchange_count = maxNum;
		goods.surplus = goods.max_exchange_count;
		goods.exchange_scroe = StringUtils.isNotBlank(exchangeScroeStr) ? Integer.parseInt(exchangeScroeStr) : 0;
		goods.status = MallConstants.STATUS_ENABLE;
		goods.visible = MallConstants.VISIBLE;
		goods.pic_format = imageFormat;
		goods.virtual_goods_id = virtualId;
		
		int result = MallGoods.saveGoodsDetail(goods);
		if (result < 0) {
			JPA.setRollbackOnly();
			flash.error("抱歉，保存失败，请联系管理员");
		} else {
			flash.error("保存成功");
		}
		
		goodsListPre();
	}
}

