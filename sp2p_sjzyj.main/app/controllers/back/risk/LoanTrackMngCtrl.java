package controllers.back.risk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OSSUploadUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.common.entity.t_advertisement;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_loan_track;
import models.core.entity.t_bid;
import play.mvc.With;
import services.common.LoanTrackService;
import services.core.BidService;

/**
 * 后台-风控-贷后跟踪管理控制器
 * 
 * @description
 * 
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
@With({ SubmitRepeat.class })
public class LoanTrackMngCtrl extends BackBaseController {
	
	protected static LoanTrackService loanTrackService = Factory.getService(LoanTrackService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);

	/**
	 * 贷后跟踪列表查询
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public static void showLoanTrackPre(int currPage,int pageSize) {
		
		PageBean<t_loan_track> page = loanTrackService.pageOfLoanTracks(currPage, pageSize);
		
		render(page);
	}
	
	/**
	 * 进入添加贷后跟踪页面
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public static void toAddLoanTrackPre() {
		
		render();
	}
	
	/**
	 * 添加贷后跟踪信息
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public static void addLoanTrack(long bidId, String title, String content) {
		
		flash.put("title", title);
		flash.put("bidId", bidId);
		
		t_loan_track loanTrack = new t_loan_track();
		
		List<t_bid> bids = bidService.findAll();
		
		boolean flag = false;
		for (int i = 0; i < bids.size(); i++) {
			if(bids.get(i).id == bidId) {
				loanTrack.bid_id = bidId;
				flag = true;
				break;
			}
		}
		
		if(!flag) {
			flash.error("所添加的标号不存在");
        	toAddLoanTrackPre();
		}
		
        if(StringUtils.isBlank(content)){
        	flash.error("跟踪内容不能为空");
        	toAddLoanTrackPre();
        }
        
        loanTrack.content = content;
        
        loanTrack.title = title;
        
        String orderTime = params.get("time");
        flash.put("orderTime", orderTime);
        loanTrack.time = DateUtil.strToDate(orderTime);
        
        loanTrack.save();
        
        flash.success("添加成功");
		showLoanTrackPre(1,10);
	}
	
	/**
	 * 进入编辑贷后跟踪页面
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public static void toEditLoanTrackPre(long loanId) {
		
		t_loan_track loanTrack = loanTrackService.findByID(loanId);
		if(loanTrack == null) {
			error404();
		}
		
		render(loanTrack);
	}
	
	/**
	 * 编辑贷后跟踪信息
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public static void editLoanTrack(long ids, String title, String content) {

		flash.put("title", title);
		
		t_loan_track loanTrack = loanTrackService.findByID(ids);
		if(loanTrack == null) {
			flash.error("系统出错，请重试");
			showLoanTrackPre(1,10);
		}
		
        if(StringUtils.isBlank(content)){
        	flash.error("跟踪内容不能为空");
        	toEditLoanTrackPre(ids);
        }
        
        loanTrack.content = content;
        
        loanTrack.title = title;
        
        String orderTime = params.get("time");
        flash.put("orderTime", orderTime);
        loanTrack.time = DateUtil.strToDate(orderTime);
        
        loanTrack.save();
        
        flash.success("编辑成功");
		showLoanTrackPre(1,10);
	}
	
	/**
	 * 删除一条贷后追踪
	 * @rightID 203005
	 *
	 * @param long 贷后追踪id 
	 * @return
	 * 	
	 * @author liuyang
	 * @createDate 2018年05月21日
	 */
	public static void delLoanTrack(long loanId){
		ResultInfo result = new ResultInfo();
				
		boolean delFlag = loanTrackService.delLoanTrack(loanId);
		
		if(!delFlag){
			result.code=-1;
			result.msg="删除失败";
			
			renderJSON(result);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
}
