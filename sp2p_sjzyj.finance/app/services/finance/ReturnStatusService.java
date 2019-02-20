package services.finance;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.finance.ReturnStatusDao;
import models.finance.entity.t_current_assets;
import models.finance.entity.t_return_status;
import models.finance.entity.t_return_status.ReportType;
import services.base.BaseService;

/**
 * 金融办返回存储表Service层
 * 
 * @createDate 2018.10.16
 * */
public class ReturnStatusService extends BaseService<t_return_status>{

	protected static ReturnStatusDao returnStatusDao = Factory.getDao(ReturnStatusDao.class);
	
	protected ReturnStatusService () {
		super.basedao = returnStatusDao;
	}
	
	/**添加记录*/
	public ResultInfo add (String report_name,ReportType reportType,JSONObject result ) {
		ResultInfo res = new ResultInfo();
		if (report_name == null) {
			res.code = -1;
			res.msg = "报文名为空，不能添加";
			return res;
		}
		if (reportType == null) {
			res.code = -2;
			res.msg = "报送类型为空，不能添加";
			return res;
		}
		if (result == null) {
			res.code = -3;
			res.msg = "返回值为空,不能添加";
			return res;
		}
		String resultCode = result.getString("result");
		String resultDetial = null;
		int code = 0;
		if (resultCode.equals("0002")) {
			code = 1;
			resultDetial = result.getString("details");
			
		}else if(resultCode.equals("0003")){
			code = 1;
			resultDetial = "请求错误";
		}else if(resultCode.equals("0004")){
			code = 1;
			resultDetial = "系统错误";
		}else if(resultCode.equals("0000")){
			resultDetial = "发送XML文件成功";
		}
		
		Boolean flag = returnStatusDao.addStatus(report_name, reportType.code, code, resultDetial);
		if (!flag) {
			res.code = -4;
			res.msg = "添加失败";
			return res;
		}
		res.code = 1;
		res.msg = "添加成功";
		return res;
	}
	/**
	 * 
	 * @Title: pageOfReturnStatus
	 * 
	 * @description 金融办报文列表显示
	 * @param currPage
	 * @param  pageSize
	 * @param @return
	 * @return PageBean<t_return_status>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月17日-上午11:30:39
	 */
	public PageBean<t_return_status> pageOfReturnStatus(int currPage,int pageSize) {
		
		return returnStatusDao.pageOfReturnStatus(currPage, pageSize);
	}
}
