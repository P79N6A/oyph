package common.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import play.Logger;

public class PageBean <T> implements Serializable{

	private static final long serialVersionUID = -8652652596351231066L;
	
	public static final Integer DEFAULT_PAGE_SIZE=10;
	
	public static final Integer DEFAULT_CURRENT_PAGE=1;
	
	public int selMonth;
	
	/**
	 * 当前页
	 */
	public int currPage = 1;
	/**
	 * 总记录数
	 */
	public int totalCount;
	/**
	 * 总页数
	 */
	public int totalPageCount;
	/**
	 * 每页的记录条数
	 */
	public int pageSize;
	/**
	 * 显示的统计标题
	 */
	public String pageTitle = "";
	/**
	 * 当前页的记录
	 */
	public List<T> page;
	/**
	 * 搜索条件
	 */
	public Map<String,Object> conditions;
	
	public PageBean(int pageSize) {
		this.currPage = 1;
		this.pageSize = pageSize;
	}

	public PageBean() {
		this(DEFAULT_PAGE_SIZE);
	}
	
	public void setCurrPage(int currPage) {
		this.currPage = currPage<= 0 ? DEFAULT_CURRENT_PAGE : currPage;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE: pageSize;
	}
	
	public void setPageNum(Object pageNum) {
		
		if(pageNum instanceof String[]){
			String[] pageStr = (String[]) pageNum;
			
			try {
				this.currPage = Integer.parseInt(pageStr[0]);
				this.currPage = this.currPage <= 0 ? 1 : this.currPage;
			} catch (Exception e) {
				Logger.error(e.getMessage());
				this.currPage = 1;
			}
		}
		
		if (pageNum instanceof String) {
			String pageStr = (String) pageNum;
			
			try {
				this.currPage = Integer.parseInt(pageStr);
				this.currPage= this.currPage <=0 ? 1 : this.currPage;
			} catch (Exception e) {
				Logger.error(e.getMessage());
				this.currPage = 1;
			}
		}

		if (pageNum instanceof Integer) {
			
			try {
				this.currPage = (Integer) pageNum;
				this.currPage = this.currPage <=0 ? 1 : this.currPage;
			} catch (Exception e) {
				Logger.error(e.getMessage());
				this.currPage = 1;
			}
		}
	}
	
	public void setPageNum(int pageNum) {
		
		this.currPage = this.currPage <=0 ? 1 : this.currPage;
	}

	/**
	 * 得到总记录条数
	 * @param totalNum
	 * @return 如果不存在记录，返回false
	 */
	public boolean setTotalNum(int totalCount) {
		this.totalCount = totalCount;

		if (this.totalCount == 0) {
			this.totalPageCount = 0;
			
			return false;
		} else {
			this.totalPageCount = (this.totalCount -1) / this.pageSize + 1;
		}

		this.currPage = this.currPage > this.totalPageCount ? this.totalPageCount: this.currPage;
		
		return true;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	@Override
	public String toString() {
		return "PageBean [currPage=" + currPage + ", totalCount=" + totalCount
				+ ", totalPageCount=" + totalPageCount + ", pageSize="
				+ pageSize + ", pageTitle=" + pageTitle + ", page=" + page
				+ ", conditions=" + conditions + "]";
	}

	public int getTotalPageCount() {
		return (this.totalCount - 1) / this.pageSize + 1;
	}
	
}
