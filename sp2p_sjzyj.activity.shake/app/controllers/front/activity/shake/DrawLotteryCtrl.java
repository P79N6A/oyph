package controllers.front.activity.shake;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.TimeUtil;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import controllers.front.LoginAndRegisteCtrl;
import daos.activity.shake.AwardTurnplateDao;
import models.activity.shake.entity.t_award_turnplate;
import models.activity.shake.entity.t_turn_award_record;
import models.activity.shake.entity.t_user_gold;
import models.beans.MallScroeRecord;
import models.common.bean.CurrUser;
import models.entity.t_mall_scroe_record;
import play.mvc.Http.StatusCode;
import services.activity.shake.AwardTurnplateService;
import services.activity.shake.TurnAwardRecordService;
import services.activity.shake.UserGoldService;
import services.common.UserInfoService;

public class DrawLotteryCtrl extends FrontBaseController {

	private static AwardTurnplateService awardTurnplateService = Factory.getService(AwardTurnplateService.class);
	
	private static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	private static TurnAwardRecordService turnAwardRecordService = Factory.getService(TurnAwardRecordService.class);
	/**
	 * 
	 * @Title: drawLottery
	 * @description 转盘抽奖
	 * @return Long
	 * @author likai
	 * @createDate 2018年10月25日 上午10:21:05
	 */
	public static String drawLottery() {
		ResultInfo resultJson = new ResultInfo();
		ResultInfo error = new ResultInfo();
		CurrUser currUser = getCurrUser();
		if (currUser == null) {
			resultJson.code = -1;
			resultJson.msg = "未登录";
			
			return JSONObject.toJSONString(resultJson);
		}
		Long userid = currUser.id;
		t_user_gold result = userGoldService.getByUserId(userid);
		/** 查询用户可用积分 */
		int scroe = MallScroeRecord.queryScoreRecordByUser(userid, error);
		List<Object> list = new ArrayList<Object>(); 
		/** 创建奖品ID */
		long awardId;
		if (result.gold<=0 && scroe<80000) {
			resultJson.code = -2;
			resultJson.msg = "金币或积分不足";
			
			return JSONObject.toJSONString(resultJson);
		}
		if (result.gold > 0) {//金币抽奖
			// 获取登陆信息
			String userName = userInfoService.getReality_nameByuserId(userid).reality_name;
			// 获取登陆用户的金币信息
			// 扣除用户金币
			result.gold = result.gold - 1;
			// 查询所有奖品
			List<t_award_turnplate> awardTurnplateList = awardTurnplateService.findAllOrderByAward();
			// 所有奖品概率
			int[] prob = new int[12];
			// 所有奖品ID
			Long[] id = new Long[12];
			int i = 0;
			for (t_award_turnplate awardTurnplate : awardTurnplateList) {
				prob[i] = awardTurnplate.getProb().multiply(new BigDecimal(10000)).intValue();
				id[i] = awardTurnplate.getId();
				i++;
			}
			// 产生随机数
			Random rand = new Random();
			int num = rand.nextInt(10000) + 1;
			if (0 < num && num <= prob[0]) {
				awardId = id[0];
			} else if (prob[0] < num && num <= prob[1]) {
				awardId = id[1];
			} else if (prob[1] < num && num <= prob[2]) {
				awardId = id[2];
			} else if (prob[2] < num && num <= prob[3]) {
				awardId = id[3];
			} else if (prob[3] < num && num <= prob[4]) {
				awardId = id[4];
			} else if (prob[4] < num && num <= prob[5]) {
				awardId = id[5];
			} else if (prob[5] < num && num <= prob[6]) {
				awardId = id[6];
			} else if (prob[6] < num && num <= prob[7]) {
				awardId = id[7];
			} else if (prob[7] < num && num <= prob[8]) {
				awardId = id[8];
			} else if (prob[8] < num && num <= prob[9]) {
				awardId = id[9];
			} else if (prob[9] < num && num <= prob[10]) {
				awardId = id[10];
			}  else {
				awardId = id[11];
			}
			result.save();
			Date now = new Date();
			/** 获取奖品ID，根据奖品ID判断是否中了0.5元话费(awardId==18) */
			if (awardId == 18) {
				/** 判断奖品表中是否有0.5元话费记录，有的话数量直接+1 */
				t_turn_award_record onetelCount = turnAwardRecordService.findByUserId(userid);
				if (onetelCount != null){
					onetelCount.onetel_count = onetelCount.onetel_count + 1;
					onetelCount.save();
					list.add(awardId);
					list.add(onetelCount.id);
					resultJson.code = 0;
					resultJson.msg = "抽奖成功";
					resultJson.objs = list;
					
					return JSONObject.toJSONString(resultJson);
				}else {
					/** 判断奖品表中是否有0.5元话费记录，没有的话插入一条0.5元话费中奖信息 */
					t_turn_award_record t_turn_award_record = new t_turn_award_record();
					t_turn_award_record.setUser_id(userid);
					t_turn_award_record.setUser_name(userName);
					t_turn_award_record.setAward_id(awardId);
					t_turn_award_record.setOnetel_count(1);
					t_turn_award_record.setStatus(0);
					t_turn_award_record.setEnd_status(0);
					t_turn_award_record.is_grant = 0;	
					t_turn_award_record.setTime(now);
					t_turn_award_record.setEnd_time(DateUtil.addYear(now, 1));	
					t_turn_award_record recordId = turnAwardRecordService.insert(t_turn_award_record);
					list.add(awardId);
					list.add(recordId.id);
					resultJson.code = 0;
					resultJson.msg = "抽奖成功";
					resultJson.objs = list;
					
					return JSONObject.toJSONString(resultJson);
				}
			}else {
				/** 如果中的不是0.5元话费，直接插入一条相对应中奖信息 */
			    t_turn_award_record t_turn_award_record = new t_turn_award_record();
				t_turn_award_record.setUser_id(userid);
				t_turn_award_record.setUser_name(userName);
				t_turn_award_record.setAward_id(awardId);
				t_turn_award_record.setStatus(0);
				t_turn_award_record.setEnd_status(0);
				t_turn_award_record.is_grant = 0;	
				t_turn_award_record.setTime(now);
				t_turn_award_record.setEnd_time(DateUtil.addDay(now, 1));
				t_turn_award_record recordId = turnAwardRecordService.insert(t_turn_award_record);
				list.add(awardId);
				list.add(recordId.id);
				resultJson.code = 0;
				resultJson.msg = "抽奖成功";
				resultJson.objs = list;
				
				return JSONObject.toJSONString(resultJson);
			}
		}else if (result.gold<=0 && scroe>=80000) {//积分抽奖
			// t_mall_scroe_record存的ti15188888888格式
			String userName = (userInfoService.findUserInfoByUserId(userid)).name;
			/** 如果金币为0 每次抽奖扣除80000积分 */
			scroe = scroe - 80000;
			Date now = new Date();
			t_mall_scroe_record t_mall_scroe_record = new t_mall_scroe_record();
			t_mall_scroe_record.user_id = userid;
			/** 查询为空时保存空--防止报错 */
			if(userName == null) {
				t_mall_scroe_record.user_name = "";
			}
			t_mall_scroe_record.user_name = userName;
			t_mall_scroe_record.time = now;
			t_mall_scroe_record.type = 7;
			t_mall_scroe_record.scroe = -80000;
			t_mall_scroe_record.status = 1;
			t_mall_scroe_record.description = "大转盘抽奖";
			
			t_mall_scroe_record.save();
			// 查询所有奖品
			List<t_award_turnplate> awardTurnplateList = awardTurnplateService.findAllOrderByAward();
			// 所有奖品概率
			int[] prob = new int[12];
			// 所有奖品ID
			Long[] id = new Long[12];
			int i = 0;
			for (t_award_turnplate awardTurnplate : awardTurnplateList) {
				prob[i] = awardTurnplate.getProb().multiply(new BigDecimal(10000)).intValue();
				id[i] = awardTurnplate.getId();
				i++;
			}
			// 产生随机数
			Random rand = new Random();
			int num = rand.nextInt(10000) + 1;
			if (0 < num && num <= prob[0]) {
				awardId = id[0];
			} else if (prob[0] < num && num <= prob[1]) {
				awardId = id[1];
			} else if (prob[1] < num && num <= prob[2]) {
				awardId = id[2];
			} else if (prob[2] < num && num <= prob[3]) {
				awardId = id[3];
			} else if (prob[3] < num && num <= prob[4]) {
				awardId = id[4];
			} else if (prob[4] < num && num <= prob[5]) {
				awardId = id[5];
			} else if (prob[5] < num && num <= prob[6]) {
				awardId = id[6];
			} else if (prob[6] < num && num <= prob[7]) {
				awardId = id[7];
			} else if (prob[7] < num && num <= prob[8]) {
				awardId = id[8];
			} else if (prob[8] < num && num <= prob[9]) {
				awardId = id[9];
			} else if (prob[9] < num && num <= prob[10]) {
				awardId = id[10];
			}  else {
				awardId = id[11];
			}
			result.save();
			/** 获取奖品ID，根据奖品ID判断是否中了0.5元话费(awardId==18) */
			if (awardId==18) {
				/** 判断奖品表中是否有0.5元话费记录，有的话数量直接+1 */
				t_turn_award_record onetelCount = turnAwardRecordService.findByUserId(userid);
				if (onetelCount !=null){
					onetelCount.onetel_count = onetelCount.onetel_count + 1;
					onetelCount.save();
					list.add(awardId);
					list.add(onetelCount.id);
					resultJson.code = 0;
					resultJson.msg = "抽奖成功";
					resultJson.objs = list;
					
					return JSONObject.toJSONString(resultJson);
				}else {
					/** 判断奖品表中是否有0.5元话费记录，没有的话插入一条0.5元话费中奖信息 */
					t_turn_award_record t_turn_award_record = new t_turn_award_record();
					t_turn_award_record.setUser_id(userid);
					t_turn_award_record.setUser_name(userName);
					t_turn_award_record.setAward_id(awardId);
					t_turn_award_record.setOnetel_count(1);
					t_turn_award_record.setStatus(0);
					t_turn_award_record.setEnd_status(0);
					t_turn_award_record.is_grant = 0;	
					t_turn_award_record.setTime(now);
					if (awardId==18) {
						t_turn_award_record.setEnd_time(DateUtil.addYear(now, 1));
					}else{
						t_turn_award_record.setEnd_time(DateUtil.addDay(now, 1));
					}
					t_turn_award_record recordId = turnAwardRecordService.insert(t_turn_award_record);
					list.add(awardId);
					list.add(recordId.id);
					resultJson.code = 0;
					resultJson.msg = "抽奖成功";
					resultJson.objs = list;
					
					return JSONObject.toJSONString(resultJson);
				}
			}else {
				/** 如果中的不是0.5元话费，直接插入一条相对应中奖信息 */
				t_turn_award_record t_turn_award_record = new t_turn_award_record();
				t_turn_award_record.setUser_id(userid);
				/** 获取登陆人姓名-汉字形式 */
				String realityName = (userInfoService.findUserInfoByUserId(userid)).reality_name;
				if (realityName == null) {
					t_turn_award_record.setUser_name("");
				}
				t_turn_award_record.setUser_name(realityName);
				t_turn_award_record.setAward_id(awardId);
				t_turn_award_record.setStatus(0);
				t_turn_award_record.setEnd_status(0);
				t_turn_award_record.is_grant = 0;	
				t_turn_award_record.setTime(now);
				t_turn_award_record.setEnd_time(DateUtil.addDay(now, 1));
				t_turn_award_record recordId = turnAwardRecordService.insert(t_turn_award_record);
				list.add(awardId);
				list.add(recordId.id);
				resultJson.code = 0;
				resultJson.msg = "抽奖成功";
				resultJson.objs = list;
				
				return JSONObject.toJSONString(resultJson);
			}
		}
		
		return JSONObject.toJSONString(resultJson);
	}
}