package services.common;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.LoanTrackDao;
import models.common.entity.t_loan_track;
import services.base.BaseService;

public class LoanTrackService extends BaseService<t_loan_track> {

	LoanTrackDao loanTrackDao = Factory.getDao(LoanTrackDao.class);
	
	protected LoanTrackService() {
		
		this.basedao = loanTrackDao;
	}
	
	/**
	 *  分页查询，跟踪列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public PageBean<t_loan_track> pageOfLoanTracks(int currPage, int pageSize) {
		
		return loanTrackDao.pageOfLoanTracks(currPage, pageSize);
	}
	
	/**
	 *  分页查询，跟踪列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public PageBean<t_loan_track> pageOfLoanTracksByBidId(int currPage, int pageSize, long bidId) {
		
		return loanTrackDao.pageOfLoanTracksByBidId(currPage, pageSize, bidId);
	}
	
	public List<t_loan_track> listOfLoanTracks(long bidId) {
		
		return loanTrackDao.listOfLoanTracks(bidId);
	}
	
	/**
	 * 删除贷后追踪
	 *
	 * @param loanId 广告图片loanId 
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年05月21日
	 */
	public boolean delLoanTrack(long loanId) {
		int row = loanTrackDao.delete(loanId);
		if(row<=0){
			return false;
		}
		
		return true;
	}
}
