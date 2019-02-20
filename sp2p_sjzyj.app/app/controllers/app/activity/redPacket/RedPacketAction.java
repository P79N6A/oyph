package controllers.app.activity.redPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import controllers.front.account.MyRedpacketCtrl;
import daos.ext.redpacket.RedpacketUserDao;
import models.common.bean.CurrUser;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import services.common.NoticeService;
import services.common.RechargeUserService;
import services.common.UserInfoService;
import services.ext.redpacket.RedpacketService;

public class RedPacketAction extends BackBaseController{

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	/**
	 * 查看红包账户信息
	 * 
	 * @param parameters
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年12月19日
	 */
	public static String showMyRedpacket(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String userIdString = parameters.get("userId");
		String redPacketStatusString = parameters.get("redPacketStatus");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdString, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		int redPacketStatus = Integer.parseInt(redPacketStatusString);
		

		if (redPacketStatus != -1 && redPacketStatus != 2 && redPacketStatus != 3) {
			json.put("code",-1);
			json.put("msg", "红包查询：状态参数错误");
			 
			 return json.toString();
		}
		
		
		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
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
		
		List<t_red_packet_user> userRedpackets = redpacketService.queryRedpacketsByUserid(userId);
		
		if (userRedpackets == null || userRedpackets.size() == 0 ) {
			json.put("code", -1);
			json.put("msg", "账户无红包信息");
		}
		
		return redpacketService.pageOfRedPacket(userId, redPacketStatus, currPage, pageSize);
	
			
	}
	
	
	/**
	 * 查看投标时可用红包列表
	 * 
	 * @param parameters
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年12月25日
	 */
	public static String showInvestRedpacket(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String userIdString = parameters.get("userId");
		String investAmt = parameters.get("investAmt");
		
		if (!StrUtil.isNumericPositiveInt(investAmt)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的投标金额!");
			
			return json.toString();
		}
		double amt = Double.parseDouble(investAmt);
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdString, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		List<t_red_packet_user> userRedpackets = redpacketService.queryRedpacketsByUserid(userId);
		if (userRedpackets == null || userRedpackets.size() == 0 ) {
			json.put("code", -1);
			json.put("msg", "账户无红包信息");
		}
		
		/** 获得摇一摇活动红包  */
		List<t_red_packet_user> shakeRedPacket = redpacketService.queryInvestRedpacketsByUserid(userId,amt);
		
		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("shakeRedPacketList", shakeRedPacket);
		
	
		
		return json.toString();
			
	}
	
}
