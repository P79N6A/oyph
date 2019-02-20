package controllers.front.activity.shake;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.javaflow.bytecode.transformation.ResourceTransformer;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.FrontBaseController;
import controllers.front.LoginAndRegisteCtrl;
import models.activity.shake.entity.t_big_wheel;
import models.activity.shake.entity.t_turn_award_record;
import models.activity.shake.entity.t_user_gold;
import models.beans.MallScroeRecord;
import models.common.bean.CurrUser;
import models.entity.t_mall_scroe_record;
import services.activity.shake.BigWheelService;
import services.activity.shake.TurnAwardRecordService;
import services.activity.shake.UserGoldService;
import services.common.UserInfoService;


public class MyBigWheelActivityCtrl extends FrontBaseController{
	
	private static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	private static TurnAwardRecordService turnAwardRecordService = Factory.getService(TurnAwardRecordService.class);
		
	private static BigWheelService bigWheelService = Factory.getService(BigWheelService.class);
	
	/**
	 * 
	 * @Title: bigTurnaroundDrawPre
	 * 
	 * @description 进入大转盘活动页面
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-下午5:33:46
	 */
	public static void bigTurnaroundDrawPre(){
		ResultInfo error = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		Long userid = getCurrUser().id; 
		/** 查询用户可用积分 */
		int scroe = MallScroeRecord.queryScoreRecordByUser(userid, error);
		Integer goldResult = 0;
		/** 获得金币数 */
		t_user_gold result = userGoldService.getByUserId(userid);
		t_turn_award_record record = turnAwardRecordService.getInfoHistory(userid);
		/** 获取登陆人0.5元话费中奖数量 */
		t_turn_award_record onetelCount = turnAwardRecordService.findByUserId(userid);
		String userName = userInfoService.getReality_nameByuserId(userid).reality_name;
		/** 判断奖品表中是否存在0.5元话费记录，没有插入一条 */
		if (onetelCount == null) {
			Long awardId =(long) 18;
			t_turn_award_record t_turn_award_record = new t_turn_award_record();
			t_turn_award_record.setUser_id(userid);
			t_turn_award_record.setUser_name(userName);
			t_turn_award_record.setAward_id(awardId);
			t_turn_award_record.setOnetel_count(0);
			t_turn_award_record.setStatus(0);
			t_turn_award_record.setEnd_status(0);
			t_turn_award_record.is_grant = 0;	
			t_turn_award_record.save();
		}
		int onetelCountNum = 0;
		/**判断是否有18号奖品中奖记录*/
		onetelCount = turnAwardRecordService.findByUserId(userid);
		if (onetelCount != null) {
			onetelCountNum = onetelCount.onetel_count;
		}
		Long onetelCountId = onetelCount.id;
		/**如果中奖记录为空 且金币也为空 进入抽奖页面，只返回0金币*/
		if(record == null){
			if(result == null){
				render(goldResult);
				return;
			}
			goldResult = result.gold;
			render(goldResult);
			if(scroe <80000 ){
				int Scroe = scroe;
				goldResult = result.gold;
				render(Scroe,onetelCountNum,goldResult);
				return;
			}
			int Scroe = scroe;
			render(Scroe);
		}else{
			/** 如果中奖记录不为空 则进入大转盘后返回金币数量和地址信息*/
			String ship_address = null;
			if(record.ship_address!=null){
				ship_address = record.ship_address;
			}
			String tel = null;
			if (record.tel!=null) {
				tel = record.tel;
			}
			if(result == null){
				render(goldResult);
				return;
			}
			goldResult = result.gold;
			if(scroe <80000){
				int Scroe = scroe;
				goldResult = result.gold;
				render(goldResult,ship_address,tel,onetelCountNum,onetelCountId,Scroe);
				return;
			}
			int Scroe = scroe;
			render(goldResult,ship_address,tel,onetelCountNum,onetelCountId,Scroe);
		}
	}
	/**
	 * 
	 * @Title: addShipAddress
	 * 
	 * @description 添加收货地址
	 * @param turn_id 前台传过来的获得奖品对象id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月29日-下午3:16:44
	 */
	public static String addShipAddress(Long turn_id){
		ResultInfo result = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		t_turn_award_record award_record = turnAwardRecordService.findById(turn_id);
		String phone = params.get("phone");
		if (StringUtils.isBlank(phone)) {
			result.code=-1;
			result.msg="手机号码不能为空";
			
			return JSONObject.toJSONString(result);
		}else if(!StrUtil.isMobileNum(phone)){
			result.code=-1;
			result.msg="手机号格式不正确";
			
			return JSONObject.toJSONString(result);
		}else{
			award_record.tel = phone;
		}
		Long award_id = award_record.award_id;
		award_record.onetel_count = 1;
		//如果获取的是实物奖品则必填收货地址
		if(award_id == 1 || award_id == 10){
			String address = params.get("ship_address");
			if (StringUtils.isBlank(address)) {
				result.code=-1;
				result.msg="收货地址不能为空";
				return JSONObject.toJSONString(result);
			}else{
				award_record.ship_address = address;
			}
		}else{
			award_record.ship_address = null;
		}
		award_record.status = 1;
		award_record.is_grant = 0;
		award_record.save();
		result.code=0;
		result.msg="successfully";
		
		return JSONObject.toJSONString(result);
	}
	
	/**
	 * 
	 * @Title: winningRecordPagePre
	 *
	 * @description 判断是否登陆，修改到期状态并查询登陆人奖品信息
	 *
	 * @param currPage
	 * @param pageSize 
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年11月2日
	 */
	public static void winningRecordPagePre (int currPage, int pageSize) {
		/** 判断是否登陆 */
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		/** 获取登陆人ID */ 
		Long userid = getCurrUser().id; 
		/** 修改到期状态  */
		turnAwardRecordService.updateEndStatus();
		/** 查询登陆人奖品信息 */
		PageBean<t_turn_award_record> pageBean = turnAwardRecordService.findByUserId(currPage, pageSize, userid);
        render(pageBean);
	}
	/**
	 * 
	 * @Title: updateReciveStatus
	 * 
	 * @description 点击领取修改领取状态
	 * @return String    
	 * @param id主键 中奖信息表 
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午4:10:43
	 */
	public static String updateReciveStatus(Long id){
		ResultInfo result = new ResultInfo();
		try {
			int i = turnAwardRecordService.updateReciveStatus(id);
			if(i>=1){
				result.code=0;
				result.msg="领取成功！";
				
				return JSONObject.toJSONString(result);
			}else{
				result.code=-1;
				result.msg="领取失败！,请重试";
				
				return JSONObject.toJSONString(result);
			}
			
		} catch (Exception e) {
			result.code=-1;
			result.msg="网络异常！,请重试";
		}
		
		return JSONObject.toJSONString(result);
	}
	/**
	 * 
	 * @Title: addRecore
	 * 
	 * @description 大转盘用户中奖后添加积分
	 * @param nub 奖品号
	 * @param turn_id 前端传过来刚插入中奖信息表的id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午5:53:55
	 */
	public static String addRecore(Long nub,Long turn_id){
		ResultInfo result = new ResultInfo();
		CurrUser curruser = getCurrUser();
		if(curruser==null){
			LoginAndRegisteCtrl.loginPre();
		}
		Long user_id = curruser.id;
		/** 通过user_id查到用户名 */
		String user_name = userInfoService.findUserInfoByUserId(user_id).name;
		t_mall_scroe_record record = new t_mall_scroe_record();
		/** 通过id查询刚插入的那一条信息 */
		t_turn_award_record award_record = turnAwardRecordService.findById(turn_id);
		if(award_record==null){
			result.code = -1;
			result.msg = "没有查到数据";
			
			return JSONObject.toJSONString(result);
		}else{
			/** 领取到积分后修改领取状态 */
			award_record.status = 1;
			award_record.is_grant = 1;
			award_record.save();
		}
		/** 添加积分信息到积分记录表 */
		record.user_id = user_id;
		record.user_name = user_name;
		record.type = 4;
		record.description = "大转盘中奖";
		if(nub==9){
			record.scroe = 50000;
		}else if(nub==8){
			record.scroe = 100000;
		}
		record.status = 1;
		record.time = new Date();
		record.save();
		result.code = 0;
		result.msg = "领取成功";
		
		return JSONObject.toJSONString(result);
	}
	
	/**
	 * 
	 * @Title: subtractRecore
	 *
	 * @description 使用积分抽奖后减去80000积分
	 *
	 * @param @param nub
	 * @param @param turn_id
	 * @param @return 
	 * 
	 * @return String    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月21日
	 */
	public static String subtractRecore(Long nub,Long turn_id){
		ResultInfo result = new ResultInfo();
		CurrUser curruser = getCurrUser();
		if(curruser==null){
			LoginAndRegisteCtrl.loginPre();
		}
		Long user_id = curruser.id;
		/** 通过user_id查到用户名 */
		String user_name = userInfoService.findUserInfoByUserId(user_id).name;
		t_mall_scroe_record record = new t_mall_scroe_record();
		/** 通过id查询刚插入的那一条信息 */
		t_turn_award_record award_record = turnAwardRecordService.findById(turn_id);
		if(award_record==null){
			result.code = -1;
			result.msg = "没有查到数据";
			
			return JSONObject.toJSONString(result);
		}else{
			/** 领取到积分后修改领取状态 */
			award_record.status = 1;
			award_record.is_grant = 1;
			award_record.save();
		}
		/** 添加积分信息到积分记录表 */
		record.user_id = user_id;
		record.user_name = user_name;
		record.type = 7;
		record.description = "大转盘抽奖";
		record.scroe = -80000;
		record.status = 1;
		record.time = new Date();
		record.save();
		result.code = 0;
		result.msg = "领取成功";
		
		return JSONObject.toJSONString(result);
	}
	/**
	 * 
	 * @Title: bigTurnaroundDrawAppPre
	 * 
	 * @description 进入app端大转盘抽奖活动页面
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月6日-下午2:01:22
	 */
	public static void bigTurnaroundDrawAppPre(){
        CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		Long userid = getCurrUser().id; 
		ResultInfo error = new ResultInfo();
		/** 查询用户可用积分 */
		int scroe = MallScroeRecord.queryScoreRecordByUser(getCurrUser().id, error);
		Integer goldResult = 0;
		t_user_gold result = userGoldService.getByUserId(userid);
		t_turn_award_record record = turnAwardRecordService.getInfoHistory(userid);
		
		/** 获取登陆人0.5元话费中奖数量 */
		t_turn_award_record onetelCount = turnAwardRecordService.findByUserId(userid);
		String userName = userInfoService.getReality_nameByuserId(userid).reality_name;
		if (onetelCount == null) {
			Long awardId =(long) 18;
			t_turn_award_record t_turn_award_record = new t_turn_award_record();
			t_turn_award_record.setUser_id(userid);
			t_turn_award_record.setUser_name(userName);
			t_turn_award_record.setAward_id(awardId);
			t_turn_award_record.setOnetel_count(0);
			t_turn_award_record.setStatus(0);
			t_turn_award_record.setEnd_status(0);
			t_turn_award_record.is_grant = 0;	
			
			t_turn_award_record.save();
		}
		int onetelCountNum = 0;
		/**判断是否有18号奖品中奖记录*/
		onetelCount = turnAwardRecordService.findByUserId(userid);
		if (onetelCount != null) {
			onetelCountNum = onetelCount.onetel_count;
		}
		Long onetelCountId = onetelCount.id;
		/**如果中奖记录为空 且金币也为空 进入抽奖页面，只返回0金币*/
		if(record == null){
			if(result == null){
				render(goldResult);
				return;
			}
			goldResult = result.gold;
			render(goldResult);
			if(scroe <80000 ){
				int Scroe = scroe;
				goldResult = result.gold;
				render(Scroe,onetelCountNum,goldResult);
				return;
			}
			int Scroe = scroe;
			render(Scroe);
			
		}else{
			/** 如果中奖记录不为空 则进入大转盘后返回金币数量和地址信息*/
			String ship_address = null;
			if(record.ship_address!=null){
				ship_address = record.ship_address;
			}
			String tel = null;
			if (record.tel!=null) {
				tel = record.tel;
			}
			if(result == null){
				render(goldResult);
				return;
			}
			goldResult = result.gold;
			if(scroe <80000){
				int Scroe = scroe;
				goldResult = result.gold;
				render(goldResult,ship_address,tel,onetelCountNum,onetelCountId,Scroe);
				return;
			}
			int Scroe = scroe;
			
			render(goldResult,ship_address,tel,onetelCountNum,onetelCountId,Scroe);
		}
	}
	
	/**
	 * 
	 * @Title: winningRecordPageAppPre
	 * 
	 * @description app进入大转盘，查看个人中奖信息页面
	 * @param currPage
	 * @param pageSize
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月6日-下午2:20:19
	 */
	public static void winningRecordPageAppPre (int currPage, int pageSize) {
		CurrUser curruser = getCurrUser();
		if(curruser==null){
			LoginAndRegisteCtrl.loginPre();
		}
		/** 获取登陆人ID */ 
		Long userid = getCurrUser().id; 
		/** 修改到期状态  */
		turnAwardRecordService.updateEndStatus();
		/** 查询登陆人奖品信息 */
		PageBean<t_turn_award_record> pageBean = turnAwardRecordService.findByUserId(currPage, pageSize, userid);
        render(pageBean);
	}
	/**
	 * 
	 * @Title: appGetUrl
	 * @description: 
	 *         APP获取Url
	 * @param parameters
	 * @return    
	 * @return String   
	 *
	 * @author likai
	 * @createDate 2018年11月9日-上午11:52:30
	 */
	public static String appGetUrl(Map<String, String> parameters) {
		JSONObject json=new JSONObject();
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		/** 正式环境时地址 */
		String url = "https://www.ouyepuhui.com/front/activity/shake/MyBigWheelActivityCtrl/bigturnarounddrawapp.html";
		/** 测试环境时地址 */
		//String url = "http://192.168.1.118:80/front/activity/shake/MyBigWheelActivityCtrl/bigturnarounddrawapp.html";
		json.put("code", 1);
		json.put("msg", "获取URL成功");
		json.put("url", url);
		
		return json.toJSONString();
	}
	/**
	 * 
	 * @Title: listOfBigWheel
	 * 
	 * @description 查询大转盘活动列表显示OTP:904
	 * @param @param parameters
	 * @param @return
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月12日-上午10:20:55
	 */
	public static String listOfBigWheel(Map<String, String> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<t_big_wheel> bigWheelList = bigWheelService.getBigWheel();
		result.put("code", 1);
		result.put("msg", "大转盘列表查询成功");
		if (bigWheelList.isEmpty()) {
			result.put("bigWheelList", null);
		}else{
			result.put("bigWheelList", bigWheelList);
		}
		
		return JSONObject.toJSONString(result);
	}
	
	/**
	 * 
	 * @Title: getTurnId
	 *
	 * @description 0.5元话费数量满20个兑换10元话费
	 *
	 * @param @param turn_id
	 * @param @return 
	 * 
	 * @return String    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月23日
	 */
	public static String getTurnId(Long turn_id){
		ResultInfo result = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		t_turn_award_record award_record = turnAwardRecordService.findById(turn_id);
		String phone = params.get("phone");
		if (StringUtils.isBlank(phone)) {
			result.code=-1;
			result.msg="手机号码不能为空";
			
			return JSONObject.toJSONString(result);
		}else if(!StrUtil.isMobileNum(phone)){
			result.code=-1;
			result.msg="手机号格式不正确";
			
			return JSONObject.toJSONString(result);
		}else{
			award_record.tel = phone;
		}
		Long award_id = award_record.award_id;
		//如果获取的是实物奖品则必填收货地址
		if(award_id == 1 || award_id == 10){
			String address = params.get("ship_address");
			if (StringUtils.isBlank(address)) {
				result.code=-1;
				result.msg="收货地址不能为空";
				
				return JSONObject.toJSONString(result);
			}else{
				award_record.ship_address = address;
			}
		}else{
			award_record.ship_address = null;
		}
		if (award_id == 18) {
			award_record.onetel_count = award_record.onetel_count-10;
		}
		award_record.status = 1;
		award_record.is_grant = 0;
		award_record.save();
		result.code=0;
		result.msg="successfully";
		
		return JSONObject.toJSONString(result);
	}
	
	public static String insert (){
		Date now = new Date();
		ResultInfo result = new ResultInfo();
		/** 获取登陆人ID */ 
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		Long userid = getCurrUser().id;
		String userName = userInfoService.getReality_nameByuserId(userid).reality_name;
		t_turn_award_record award_record = new t_turn_award_record();
		award_record.user_id = userid;
		award_record.user_name = userName;
		award_record.award_id = 6l;
		award_record.onetel_count = 1;
		String phone = params.get("phone");
		if (StringUtils.isBlank(phone)) {
			result.code=-1;
			result.msg="手机号码不能为空";
			
			return JSONObject.toJSONString(result);
		}else if(!StrUtil.isMobileNum(phone)){
			result.code=-1;
			result.msg="手机号格式不正确";
			
			return JSONObject.toJSONString(result);
		}else{
			award_record.tel = phone;
		}
		    award_record.status = 1;
		    award_record.end_status = 0;
			award_record.is_grant = 0;
			award_record.time = (now);
			award_record.end_time = (DateUtil.addYear(now, 1));
			award_record.save();
			turnAwardRecordService.updateOnetelCount(userid);
			result.code=0;
			result.msg="successfully";
			
			return JSONObject.toJSONString(result);
	}
}
