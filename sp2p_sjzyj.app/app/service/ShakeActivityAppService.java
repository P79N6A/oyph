package service;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.qos.logback.core.subst.Token.Type;
import common.constants.ConfConst;
import common.constants.MallConstants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import dao.ShakeActivityAppDao;
import daos.activity.shake.ShakeRecordDao;
import daos.activity.shake.ShakeSetDao;
import daos.common.UserInfoDao;
import daos.ext.redpacket.AddRateUserDao;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;
import models.app.bean.InvestAddRateBean;
import models.app.bean.ShakeActivityListBean;
import models.beans.MallScroeRecord;
import models.common.entity.t_user_info;
import models.entity.t_mall_scroe_record;
import models.ext.redpacket.bean.AddRateTicketBean;
import net.sf.json.JSONObject;
import services.activity.shake.ShakeActivityService;
import services.activity.shake.ShakeSetService;
import services.ext.redpacket.RedpacketService;
import services.ext.redpacket.AddRateTicketService;

/**
 * 摇一摇活动Service
 * 
 * @author niu
 * @create 2017-12-12
 */
public class ShakeActivityAppService extends ShakeActivityService {

	private UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	
	private RedpacketService redpacketService = Factory.getService(RedpacketService.class);

	private ShakeSetService shakeSetService = Factory.getService(ShakeSetService.class);
	
	private AddRateTicketService addRateTicketService  = Factory.getService(AddRateTicketService.class);
	
	protected static AddRateUserDao addRateUserDao = Factory.getDao(AddRateUserDao.class);
	
	private ShakeRecordDao shakeRecordDao = Factory.getDao(ShakeRecordDao.class);
	
	private static ShakeActivityAppDao shakeActivityAppDao;
	
	private ShakeActivityAppService() {
		shakeActivityAppDao = Factory.getDao(ShakeActivityAppDao.class);
		this.basedao = shakeActivityAppDao;
	}
	
	/**
	 * 摇一摇活动列表
	 * 
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public List<ShakeActivityListBean> listOfShakeActivity() {
		
		return shakeActivityAppDao.listOfShakeActivity();
	}
	/**
	 * 摇一摇
	 * 
	 * @param userId
	 * @param randomNum
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public String shake(long userId, int randomNum) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//摇一摇准备
		t_user_info userInfo = userInfoDao.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.put("code", -3);
			result.put("msg", "请登录");
			
			return JSONObject.fromObject(result).toString();
		}
		
		t_shake_activity activity = shakeActivityDao.getOngoingActivity();
		
		if (activity == null) {
			result.put("code", -2);
			result.put("msg", "活动已经结束，请等待下一场");
			
			return JSONObject.fromObject(result).toString();
		}
		 
		Random random = new Random();
		//添加摇奖次数
		activity.shake_count = activity.shake_count + 1;
		activity.win_num = random.nextInt(activity.winrate);
		activity.save();
		
		//是否中奖
		if (randomNum != activity.win_num) {
			
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
			
			return JSONObject.fromObject(result).toString();
		}
		
		t_shake_set set = shakeSetService.getShakeSet(activity.id);
		
		if (set == null || set.number <= 0) {
			
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
			
			return JSONObject.fromObject(result).toString();
		}
		//通过查询中奖记录表判断该用户是否已经摇到过奖，如果没有查到即可中奖
		t_shake_record record3 = shakeRecordDao.findRecoredByUserId(userId);
		
		if (record3 != null) {
			LoggerUtil.info(false, "该用户已有中奖记录");
			
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
					
			return JSONObject.fromObject(result).toString();	
		}
		
		Date now =new Date();
		/**使用期限*/
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(activity.stime); 
		calendar.add(calendar.MINUTE, activity.ctime);
		/**结束时间*/
		Date end_time=calendar.getTime();
		 
		/**持续时间(秒)*/
		long continueTime = (end_time.getTime() - now.getTime())/1000;
		int type = set.type;
		if (continueTime > 40 && type == 5) {
			
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
			
			return JSONObject.fromObject(result).toString();
		}
		
		t_shake_record record = new t_shake_record();
		record.type = set.type;
		record.amount = set.amount;
		record.prize_name = set.prize_name;
		record.number = randomNum;
		record.activity_id = activity.id;
		record.use_rule = set.use_rule;
		record.user_id = userId;
		record.user_name = userInfo.reality_name;
		record.user_mobile = userInfo.mobile;
		record.gain_count = 1;
		record.gain_user = "";
		record.create_time = new Date();

		t_shake_record record2 = record.save();
		
		if (record2 == null) {
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
			return JSONObject.fromObject(result).toString();
		}
		
//		if (ConfConst.IS_ADD_SHAKE) {
//			boolean flag = false;
//			
//			switch (record2.type) {
//			case 1:						//红包业务		
//				flag = redpacketService.creatShakeRedPacket(userId,record2);
//				
//				break;
//			case 2:						//加息卷业务
//				flag = addRateTicketService.getAddRateTicketShakeActivity(record2.amount, record2.use_rule, userId, activity.id);
//				break;
//
//			case 3:						//积分业务
//				flag = MallScroeRecord.sharkScroeRecord(userId, userInfo.name, record.amount);
//				
//				break;
//			default:
//				flag = false;
//				break;
//			}
//			
//			if (!flag) {
//				System.out.println("添加积分红包失败，没中奖");
//				result.put("code", 1);
//				result.put("type", 0);
//				result.put("msg", "很遗憾！没有中奖");
//				result.put("amount", "");
//				result.put("prize_name", "");
//				result.put("redPacketId", "");
//				
//				return JSONObject.fromObject(result).toString();
//			}
//		}
		
		activity.win_count = activity.win_count + 1;
		activity = activity.save();

		set.number = set.number -1;
		set = set.save();
		if (activity == null || set == null) {
			LoggerUtil.info(true, "中奖号码产生失败");
			result.put("code", 1);
			result.put("type", 0);
			result.put("msg", "很遗憾！没有中奖");
			result.put("amount", "");
			result.put("prize_name", "");
			result.put("redPacketId", "");
			
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("code", 1);
		result.put("msg", "中奖了");
		result.put("redPacketId", record2.id);
		result.put("prize_name", record2.prize_name);
		result.put("type", record2.type);
		if (record2.type == 2) {
			result.put("amount", record2.amount / 100.0 + "");
		} else {
			result.put("amount", record2.amount + "");
		}
		
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * APP分页查询 用户加息券
	 * 
	 * @param userId 用户ID
	 * @param status 加息券状态
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-20
	 */
	public String pageOfAddRateTicket(long userId, int status, int currPage, int pageSize) {
		
		PageBean<AddRateTicketBean> pageBean = addRateUserDao.pageOfAddRateTicket(userId, status, currPage, pageSize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "加息券查询成功");
		map.put("tickets", pageBean.page);

		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 查询投资可用的加息券
	 * 
	 * @param userId 用户Id
	 * @param amount 投资大于等于多少可用
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-21
	 */
	public List<InvestAddRateBean> listOfInvestUseAddRate(long userId, double amount) {
		
		return shakeActivityAppDao.listOfInvestUseAddRate(userId, amount);
	}
	/**
	 * 
	 * @Title: pageOfMyShakeRecord
	 * 
	 * @description app显示年会个人中奖记录
	 * @param userId
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月29日-下午2:18:35
	 */
	public t_shake_record findMyShakeRecord(long userId){
		t_shake_record myShakeList = shakeRecordDao.findMyShakeRecord(userId);
		
		return myShakeList;
	}
	
}
