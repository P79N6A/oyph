package controllers.front.activity.shake;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.omg.CORBA.Current;

import play.mvc.With;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.front.LoginAndRegisteCtrl;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_record;
import models.common.bean.CurrUser;
import services.activity.shake.ShakeActivityService;
import services.activity.shake.ShakeRecordService;
import sun.util.logging.resources.logging;

@With({AccountInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class MyShakeActivityCtrl extends FrontBaseController{
	
	protected static  ShakeRecordService shakeRecordService = Factory.getService(ShakeRecordService.class);
	
	protected static  ShakeActivityService shakeActivityService = Factory.getService(ShakeActivityService.class);
	
	/**
	 * 摇一摇活动界面
	 * @param activityId
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年12月27日
	 */
	
	 public static void activityDetailPre(long activityId) {
		 t_shake_activity shakeActivity = shakeActivityService.findByID(activityId);
		 int activityStatus = shakeActivity.status ;
		 long continueTime = 0;

		 if (activityStatus == 3 ){
			 Date now =new Date();
			 
			 /**使用期限*/
			 Calendar calendar = new GregorianCalendar(); 
			 calendar.setTime(shakeActivity.stime); 
			 calendar.add(calendar.MINUTE, shakeActivity.ctime);
			 /**结束时间*/
			 Date end_time=calendar.getTime();
			 
			 /**持续时间(秒)*/
			 continueTime = (end_time.getTime() - now.getTime())/1000;
			 
			 if (continueTime < 0){
				 
				 continueTime = 0;
			 }
		 }
		 
	 	 /** 活动持续时间 (分钟) */
	 	 int ctime = shakeActivity.ctime ;
	 	 renderArgs.put("ctime",ctime);
		 
		 render(activityId,activityStatus,continueTime);
		 
	}
	 
	 /**
	  * 摇一摇活动结束界面
	  * @param activityId
	  * 
	  * @author LiuPengwei
	  * @createDate 2017年12月27日
	  */
	 public static void endShakeActivityPre(long activityId) {
			
		 List<t_shake_record> shakeRecord = shakeRecordService.queryUserShakeRecord(activityId);
		 
		 render(shakeRecord);
		 
	}
	 
	 /**
	  * 摇一摇活动开始
	  * @param activityId
	  * 
	  * @author LiuPengwei
	  * @createDate 2017年12月27日
	  */
	 public static void startActivity(long activityId) {
		 ResultInfo result = new ResultInfo();
		 
				
		 long userId = getCurrUser().id;
		 if (userId == 528 && activityId > 0) {

			 if(shakeActivityService.startActivity(activityId)){
				result.code = 1;
				result.msg = "活动开始成功！";
				
				renderJSON(result);		
			 }		
		}
		
				
		result.code = -1;
		result.msg = "活动开始失败！";
		 
		renderJSON(result);		
	}
	 
	 /**
	  * 
	  * @param activityId
	  */

	 public static void endActivity(long activityId) {
			ResultInfo result = new ResultInfo();
			
			long userId = getCurrUser().id;
			
			if (userId == 528 && activityId > 0) {
				
				if (shakeActivityService.endActivity(activityId)){
					result.code = 1;
					result.msg = "活动结束成功！";
					
					renderJSON(result);
				}
			}
			
			result.code = -1;
			result.msg = "活动结束失败!";
			
			renderJSON(result);
		} 
	 
	 /**
	  * 活动获奖信息和弹幕
	  * 
	  * @param activityId
	  * @param currPage
	  * 
	  * @author LiuPengwei
	 * @throws ParseException 
	  * @createDate 2017年12月27日
	  */
	 public static void activityBarragePre(long activityId,  int currPage ,Long current_time) throws ParseException {
			ResultInfo result = new ResultInfo();
			
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			current_time=new Long(current_time);  
			String d = format.format(current_time);  
			Date date=format.parse(d);  
			 
			if(currPage < 1){
				currPage = 1;
			}
						
			PageBean<t_shake_record> shakeRecord = shakeRecordService.pageOfUserShakeRecord(activityId, currPage, 1, date);
			
			if(shakeRecord.totalCount < currPage){
				result.code = -1;
				renderJSON(result);
			}
			
			
			int amount = 0;
			String prize_name = null;
			String type = "";
			
			if(shakeRecord.page.get(0).type == 1){
				
				amount	= shakeRecord.page.get(0).amount;
				type ="红包";
				
			}else if (shakeRecord.page.get(0).type == 2){
				
				amount	= shakeRecord.page.get(0).amount;
				type ="加息券";
			}else if (shakeRecord.page.get(0).type == 3) {
				
				amount	= shakeRecord.page.get(0).amount;
				type = "积分";
			}else if (shakeRecord.page.get(0).type == 5) {
				
				prize_name = shakeRecord.page.get(0).prize_name;
				type = "一等奖";
			}else if (shakeRecord.page.get(0).type == 6) {
				
				prize_name = shakeRecord.page.get(0).prize_name;
				type = "二等奖";
			}else if (shakeRecord.page.get(0).type == 7) {
				
				prize_name = shakeRecord.page.get(0).prize_name;
				type = "三等奖";
			}else if (shakeRecord.page.get(0).type == 8) {
				
				prize_name = shakeRecord.page.get(0).prize_name;
				type = "纪念奖";
			}
			
			Map<String,String> map = new TreeMap<String, String>() ;
			map.put("amount",amount + "");
			map.put("prize_name", prize_name);
			map.put("type",type);
			map.put("user_mobile", shakeRecord.page.get(0).user_mobile);
			
			result.code = 1;
			result.msg = "查询成功";
			result.obj = map;
			
			renderJSON(result);
		}
	 
}
