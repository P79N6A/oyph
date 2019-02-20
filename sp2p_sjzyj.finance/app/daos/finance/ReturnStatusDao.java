package daos.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.finance.entity.t_return_status;

/**
 * 金融办返回存储表Dao层
 * 
 * @createDate 2018.10.16
 * */
public class ReturnStatusDao extends BaseDao<t_return_status>{

	public ReturnStatusDao () {}
	
	/**
	 * 金融办发送返回结果添加
	 * @author guoShiJie
	 * @createDate 2018.10.16
	 * */
	public Boolean addStatus ( String reportName , Integer reportType , Integer resultType, String remark ) {
		t_return_status status = new t_return_status();
		status.report_name = reportName;
		status.report_type = reportType;
		status.result_type = resultType;
		status.time = new Date();
		status.remark = remark;
		return this.save(status);
	}
	/**
	 * 
	 * @Title: pageOfReturnStatus
	 * 
	 * @description 金融办分页
	 * @param  currPage
	 * @param pageSize
	 * @param @return
	 * @return PageBean<t_return_status>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月17日-上午11:31:32
	 */
	public PageBean<t_return_status> pageOfReturnStatus(int currPage, int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_return_status ORDER BY time DESC");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_return_status");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(),sql.toString(), condition);
	}
	
	
}
