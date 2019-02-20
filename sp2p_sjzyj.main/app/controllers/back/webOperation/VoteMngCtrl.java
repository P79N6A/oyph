package controllers.back.webOperation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.common.entity.t_activity_prize;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_help_center;
import models.common.entity.t_participation;
import models.common.entity.t_vote_activity;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import play.mvc.With;
import services.common.ActivityPrizeService;
import services.common.ParticipationService;
import services.common.UserVoteService;
import services.common.VoteActivityService;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 投票活动管理控制器
 * @author Administrator
 *
 */
@With(SubmitRepeat.class)
public class VoteMngCtrl extends BackBaseController{

	protected static VoteActivityService voteActivityService = Factory.getService(VoteActivityService.class);
	
	protected static ActivityPrizeService activityPrizeService = Factory.getService(ActivityPrizeService.class);
	
	protected static UserVoteService userVoteService = Factory.getService(UserVoteService.class);
	
	protected static ParticipationService participationService = Factory.getService(ParticipationService.class);
	
	/**
	 * 投票活动列表页
	 * 
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void showVotesPre(int currPage,int pageSize,String voteTitle){
	    if(currPage<1){
	        currPage=1;
	    }
	    if(pageSize<1){
	        pageSize=10;
	    }
	    
	    PageBean<t_vote_activity> page = voteActivityService.queryBySearch(currPage, pageSize, voteTitle);
	    render(page,voteTitle,currPage,pageSize);
	} 

	/**
	 * 投票活动上下架
	 * 
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void voteIsUse(long voteId){
	    ResultInfo result = new ResultInfo();
                
        t_vote_activity vote = voteActivityService.findByID(voteId);
        
        if(vote==null){
            result.code=-1;
            result.msg="系统忙，请稍后再试！";
            renderJSON(result);
        }
        
        boolean isUseFlag = voteActivityService.updateVoteIsUse(voteId, !vote.getIs_use().code);
        if(!isUseFlag){
            result.code=-1;
            result.msg="上下架操作失败";
            renderJSON(result);
        }
        
        /** 添加管理员事件 */
        Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
        supervisorEventParam.put("vote_title", vote.title);  
 
        if(vote.getIs_use().code){
            supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.VOTE_DISUSE, supervisorEventParam);
        }else{
            supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.VOTE_USE, supervisorEventParam);

        }
        
        result.code=1;
        result.msg="上下架操作成功";
        result.obj=vote.getIs_use().code;
        
        renderJSON(result);
	}
	
	/**
	 * 编辑投票活动内容
	 * 
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void toEditVotePre(long voteId){
	    t_vote_activity vote = voteActivityService.findByID(voteId);
	    
	    List<t_activity_prize> activityPrizeList = activityPrizeService.findListByColumn("activity_id=?", voteId);
	    
	    if(activityPrizeList==null || activityPrizeList.size()<=0){
	        activityPrizeList=null;
	    }
	    
	    render(vote,activityPrizeList);
	}
	
	/**
	 * 编辑投票活动
	 * @param vote
	 * @createDate 2017年5月19日
	 * @author lvweihuan
	 */
	public static void editVote(t_vote_activity vote,String beginTime,String endTime,String[] lowNum,String[] uperNum,String[] jiangPin){
	    checkAuthenticity();
        if(getCurrentSupervisorId()==null){
            flash.error("系统忙，请稍后再试");
            showVotesPre(1, 10, null);
        }
        
        if(StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)){
            flash.error("时间不能为空");
            showVotesPre(1, 10, null);
        }
        
        Date startTime = DateUtil.strToDate(beginTime,  Constants.DATE_PLUGIN);
        Date nextTime = DateUtil.strToDate(endTime,  Constants.DATE_PLUGIN);
        if(!DateUtil.isDateBefore(startTime, nextTime)){
            flash.error("活动开始时间需小于结束时间！");
            toAddVotePre();
        }
        
        vote.begin_time = startTime;
        vote.end_time =  nextTime;
        
        vote.save();
        
        boolean flag=false;
        
        List<t_activity_prize> activity_prizes = activityPrizeService.findListByColumn("activity_id=?", vote.id);
        
        if(lowNum!=null){
            for(int i=0;i<lowNum.length;i++){
                if(StrUtil.isNumericInt(lowNum[i]) && StrUtil.isNumericInt(uperNum[i]) && !StringUtils.isBlank(jiangPin[i])){
                    
                    int lowNums=Integer.parseInt(lowNum[i]);
                    int uperNums=Integer.parseInt(uperNum[i]);
                    if(lowNums>0 && uperNums>0 && uperNums<lowNums){
                        continue;
                    }else{
                        flag=true;
                        t_activity_prize activity_prize = new t_activity_prize();
                        activity_prize.floor_num=lowNums;
                        activity_prize.upper_num=uperNums;
                        activity_prize.prize_describe=jiangPin[i];
                        activity_prize.activity_id=vote.id;
                        activity_prize.save();
                    }
                    
                }
            }
        }
        
        if(flag==true){
            if(activity_prizes!=null && activity_prizes.size()>0){
                for(t_activity_prize activity_prize:activity_prizes){
                    activity_prize.delete();
                }
            }
        }
        
        flash.success("修改投票活动成功！");
        
        showVotesPre(1, 10, null);
	}
	
	/**
	 * 添加投票活动
	 * 
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void toAddVotePre(){
	    render();
	}
	
	/**
	 * 添加投票活动
	 * 
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void addVote(t_vote_activity vote,String beginTime,String endTime,String[] lowNum,String[] uperNum,String[] jiangPin){
	    checkAuthenticity();
	    if(getCurrentSupervisorId()==null){
	        flash.error("系统忙，请稍后再试");
	        showVotesPre(1, 10, null);
	    }
	    
	    if(StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)){
	        flash.error("时间不能为空");
	        showVotesPre(1, 10, null);
	    }
	    
	    Date startTime = DateUtil.strToDate(beginTime,  Constants.DATE_PLUGIN);
	    Date nextTime = DateUtil.strToDate(endTime,  Constants.DATE_PLUGIN);
	    if(!DateUtil.isDateBefore(startTime, nextTime)){
	        flash.error("活动开始时间需小于结束时间！");
	        toAddVotePre();
	    }
	    
	    vote.begin_time = startTime;
        vote.end_time =  nextTime;
        vote.create_time= new Date();
        vote.supervisor_id=getCurrentSupervisorId();
	    
	    vote.save();
	    

        if(lowNum!=null){
            for(int i=0;i<lowNum.length;i++){
                if(StrUtil.isNumericInt(lowNum[i]) && StrUtil.isNumericInt(uperNum[i]) && !StringUtils.isBlank(jiangPin[i])){
                    
                    int lowNums=Integer.parseInt(lowNum[i]);
                    int uperNums=Integer.parseInt(uperNum[i]);
                    if(lowNums>0 && uperNums>0 && uperNums<lowNums){
                        continue;
                    }else{
                        t_activity_prize activity_prize = new t_activity_prize();
                        activity_prize.floor_num=lowNums;
                        activity_prize.upper_num=uperNums;
                        activity_prize.prize_describe=jiangPin[i];
                        activity_prize.activity_id=vote.id;
                        activity_prize.save();
                    }
                    
                }
            }
        }
        flash.success("保存投票活动成功！");
        
	    showVotesPre(1, 10, null);
	}
	
	/**
	 * 查看投票页
	 * @param voteId
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public static void toLookVotePre(long voteId){
	    int currPage = 1;
	    int pageSize = 10;
	    int currPageCheck = 1;
	    int pageSizeCheck = 10;
	    
	    String currPages = params.get("currPage");
	    String pageSizes = params.get("pageSize");
	    String currPageChecks = params.get("currPageCheck");
	    String pageSizeChecks = params.get("pageSizeCheck");
	    
	    if(currPages!=null && !("").equals(currPages)){
	        currPage = Integer.parseInt(currPages);
	    }
	    if(pageSizes!=null && !("").equals(pageSizes)){
	        pageSize = Integer.parseInt(pageSizes);
	    }
	    if(currPageChecks!=null && !("").equals(currPageChecks)){
	        currPageCheck  = Integer.parseInt(currPageChecks);
	    }
	    if(pageSizeChecks!=null && !("").equals(pageSizeChecks)){
	        pageSizeCheck = Integer.parseInt(pageSizeChecks);
	    }
	    
	    PageBean<t_participation> pageBean = participationService.queryParticipationsByPage(currPage, pageSize, 4, voteId, "");
	    
	    PageBean<t_participation> checkPage = participationService.queryPartCheck(currPageCheck, pageSizeCheck,voteId);
	    
	    t_vote_activity vote = voteActivityService.findByID(voteId);
	    
	    render(pageBean,checkPage,currPage,pageSize,currPageCheck,pageSizeCheck,vote);
	}
	
	/**
	 * 审核参与人信息
	 * @param partId
	 * @createDate 2017年5月22日
	 * @author lvweihuan
	 */
	public static void checkPartsPre(long partId,long voteId){
	    t_vote_activity vote = voteActivityService.findByID(voteId);
	    
	    if(vote==null){
	        flash.error("系统忙，请稍后再试");
	        showVotesPre(1, 10, "");
	    }
	    
	    t_participation participation = participationService.findByID(partId);
	    
	    if(participation==null){
	        flash.error("系统忙，请稍后再试");
	        toLookVotePre(voteId);
	    }
	    
	    render(participation,vote);
	}
	
	/**
	 * 审核参与信息
	 * 
	 * @createDate 2017年5月22日
	 * @author lvweihuan
	 */
	public static void checkParts(){
	    String partIds = params.get("partId");
	    String partStatuss = params.get("partStatus");
	    String partContents = params.get("partContent");
	    String voteIds = params.get("voteId");
	    
	    t_vote_activity vote=null;
	    t_participation participation = null;
	    long voteId=0;
	    if(getCurrSupervisor()==null){
	        showVotesPre(1, 10, "");
	    }
	    
	    if(StrUtil.isNumeric(voteIds)){
	        voteId = Long.parseLong(voteIds);
	        vote = voteActivityService.findByID(voteId);
	        if(vote==null){
	            flash.error("系统忙，请稍后再试!");
	            showVotesPre(1, 10, "");
	        }
	    }else{
	        flash.error("系统忙，请稍后再试!");
	        showVotesPre(1, 10, "");
	    }
	    
	    long partId=0;
	    
	    if(StrUtil.isNumeric(partIds)){
	        partId = Long.parseLong(partIds);
	        participation = participationService.findByID(partId);
	    }
	    
	    if(participation==null){
	        flash.error("系统忙，请稍后再试!");
	        toLookVotePre(voteId);
	    }
	    
	    if(StrUtil.isNumericInt(partStatuss) && !StringUtils.isBlank(partContents)){
	        participation.status = Integer.parseInt(partStatuss);
	        participation.verifier_idea = partContents;
	        participation.verifier_time = new Date();
	        participation.supervisor_id = getCurrentSupervisorId();
	        participation.save();
	    }else{
	        flash.error("请填写审核意见！");
	        checkPartsPre(partId, voteId);
	    }
	    
	    flash.success("审核完成！");
	    toLookVotePre(voteId);
	}
}
