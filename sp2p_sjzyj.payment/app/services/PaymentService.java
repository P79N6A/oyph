package services;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;

import common.constants.RemarkPramKey;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.ThreadUtil;
import common.utils.OrderNoUtil;
import daos.PaymentCallBackDao;
import daos.PaymentRequstDao;
import models.bean.CallBackPrint;
import models.bean.PaymentRequestLogs;
import models.bean.UserFundCheck;
import models.common.bean.LockBean;
import models.common.entity.t_user_fund;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.mvc.Scope.Params;
import yb.YbUtils;
import yb.enums.ServiceType;


/**
 * 托管业务类
 * 
 * @author niu
 * @create 2017.08.24
 */
public class PaymentService {
	
	protected static PaymentRequstDao paymentRequstDao = Factory.getDao(PaymentRequstDao.class);
	
	protected static PaymentCallBackDao paymentCallBackDao = Factory.getDao(PaymentCallBackDao.class);
	
	/**
	 * 查询托管请求/备注参数(交易密码)
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月19日
	 */
	public Map<String, String> queryRequestParams(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}

		t_payment_request pr = paymentRequstDao.findByColumn("service_order_no = ? and service_type = ?", requestMark,19);

		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		
		if (StringUtils.isBlank(pr.req_map)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，备注参数为空");
			
			return null;
		}
		
		Map<String, String> requestPrams = new Gson().fromJson(pr.req_map,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());
		
		
		requestPrams.put(RemarkPramKey.SERVICE_ORDER_NO, pr.service_order_no);  //业务订单号，备用
	
		return requestPrams;
	}
	
	/**
	 * 查询托管请求/备注参数(业务处理)
	 *
	 * @param requestMark
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月19日
	 */
	public Map<String, String> queryServiceRequestParam(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}

		t_payment_request pr = paymentRequstDao.findByColumn("service_order_no = ? and service_type<>19", requestMark);

		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		
		if (StringUtils.isBlank(pr.req_map)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，备注参数为空");
			
			return null;
		}
		
		Map<String, String> requestPrams = new Gson().fromJson(pr.req_map,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());
		
		
		requestPrams.put(RemarkPramKey.SERVICE_ORDER_NO, pr.service_order_no);  //业务订单号，备用
	
		return requestPrams;
	}
	
	
	
	/**
	 * 查询托管请求/备注参数
	 *
	 * @param businessSeqNo
	 * @return
	 *
	 * @author Liuyang
	 * @createDate 2017年9月25日
	 */
	public Map<String, String> queryRequestParamss(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}

		t_payment_request pr = paymentRequstDao.queryByRequestMark(requestMark);

		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		
		if (StringUtils.isBlank(pr.req_map)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，备注参数为空");
			
			return null;
		}
		
		Map<String, String> requestPrams = new Gson().fromJson(pr.req_map,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());
		
		
		requestPrams.put(RemarkPramKey.SERVICE_ORDER_NO, pr.service_order_no);  //业务订单号，备用
	
		return requestPrams;
	}
		
		
	/**
	 * 获取资金托管接口参数
	 *
	 * @param params
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月7日
	 */
	public Map<String,String> getRespParams(Params params){
		Map<String,String> paramMap = new HashMap<String, String>();
		String reqparams = null;
		try {
			//如果直接使用params.allSimple()则会出现乱码
			reqparams = params.urlEncode();
			
		} catch (Exception e) {
			
			LoggerUtil.error(false, e, "获取资金托管接口参数异常");
		}
		
		if (reqparams == null) {
			
			return paramMap;
		}
		
		String param[] = reqparams.split("&");
		for (int i = 0; i < param.length; i++) {
			String content = param[i];
			String key = content.substring(0, content.indexOf("="));
			String value = content.substring(content.indexOf("=") + 1, content.length());
			try {
				paramMap.put(key, URLDecoder.decode(value,"UTF-8"));
			} catch (Exception e) {
				
				LoggerUtil.error(false, e, "宜宾银行回调构造参数异常");
			}
		}
		
		return paramMap;
	}
	
	
	/**
	 * 托管资金校对分页列表(所有)
	 *
	 * @param name
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月14日
	 */
	public PageBean<UserFundCheck> pageOfUserFundCheck(String userName, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT user_id AS id, name AS userName, payment_account AS account, balance AS systemBlance, freeze AS systemFreeze FROM t_user_fund WHERE payment_account IS NOT NULL AND payment_account<>''");
	
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_user_fund WHERE payment_account IS NOT NULL AND payment_account<>''");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND name like :userName");
			countSQL.append(" AND name like :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		querySQL.append(" ORDER BY id DESC");

		PageBean<UserFundCheck> pageBean = paymentRequstDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(),
				querySQL.toString(), UserFundCheck.class, conditionArgs);
		
		LockBean lock =new LockBean();
		lock.size=pageBean.page.size();
		
		if (pageBean.page != null && pageBean.page.size() != 0){
			for (UserFundCheck ufc : pageBean.page){
				
				new ThreadUtil(ufc,lock).start();
				
				/*FundCheckThread checkThread =  new FundCheckThread(ufc);
				
				try {
					pageList.add(checkThread.call());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		}
		
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		pageBean.page = lock.fundList;

		return pageBean;
	}
	
	/**
	 * 托管资金校对列表(异常)
	 *
	 * @param name
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月18日
	 */
	public List<UserFundCheck> UserFundCheck(String userName, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT user_id AS id, name AS userName, payment_account AS account, balance AS systemBlance, freeze AS systemFreeze FROM t_user_fund WHERE payment_account IS NOT NULL AND payment_account<>''");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND name like :userName");
			
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		querySQL.append(" ORDER BY id DESC");
		
		List<UserFundCheck> pageList = new ArrayList<UserFundCheck>();
		
		List<UserFundCheck> pages = paymentRequstDao.findListBeanBySQL(querySQL.toString(), UserFundCheck.class, conditionArgs);

		if (pages != null && pages.size() != 0){
			int size = pages.size();
			
			int sizes = size/20;
			
			LockBean lock =new LockBean();
			lock.size = 20;
			
			for (int i = 0; i < sizes+1; i++) {
				
				if (pages != null && pages.size() != 0){
					
					for (UserFundCheck ufc : pages){
						
						new ThreadUtil(ufc,lock).start();
						
					}
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
				if(lock.fundList.size()>0) {
					for (int j = 0; j < lock.fundList.size(); j++) {
						if(lock.fundList.get(j).status == 1) {
							pageList.add(lock.fundList.get(j));
						}
					}
				}
				
			}
		}
		
		return pageList;
		
	}
	
	/**
	 * 资金托管请求记录
	 * 
	 * @param showType 筛选切换参数
	 * @param currPage 当前页
	 * @param pageSize 分页大小
	 * @param serviceType 按业务类型筛选
	 * @param userName 按用户名筛选
	 * @param serviceOrderNo 按业务订单号筛选
	 * @param orderNo 按交易订单号筛选
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月16日
	 */
	public PageBean<PaymentRequestLogs> pageOfRequestRecord(int showType, int currPage, int pageSize, Integer serviceType, 
			String userName, String serviceOrderNo, String orderNo) {
		/*
		 *  SELECT
				pr.id AS id,
				pr.mark AS mark,
				ui.name AS user_name,
				pr.service_order_no AS service_order_no,
				pr.order_no AS order_no,
				pr.service_type AS service_type,
				pr.pay_type AS pay_type,
				pr.time AS time,
				pr.status AS status
			FROM
				t_payment_request pr
			LEFT JOIN t_user_info ui ON ui.id = pr.user_id
			WHERE 1=1
		 */
		StringBuffer querySQL = new StringBuffer("SELECT pr.id AS id, pr.mark AS mark, ui.name AS user_name, pr.service_order_no AS service_order_no, pr.order_no AS order_no, pr.service_type AS service_type, pr.pay_type AS pay_type, pr.time AS time, pr.status AS status FROM t_payment_request pr LEFT JOIN t_user_info ui ON ui.user_id = pr.user_id WHERE 1=1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_payment_request pr LEFT JOIN t_user_info ui ON ui.user_id = pr.user_id WHERE 1=1");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if (showType == 1) {
			querySQL.append(" AND pr.status = ").append(t_payment_request.Status.ERROR.code);
			countSQL.append(" AND pr.status = ").append(t_payment_request.Status.ERROR.code);
		}
		if (showType == 2) {
			querySQL.append(" AND pr.status = ").append(t_payment_request.Status.FAILED.code);
			countSQL.append(" AND pr.status = ").append(t_payment_request.Status.FAILED.code);
		}
		
		if (serviceType != null) {
			querySQL.append(" AND pr.service_type = :serviceType");
			countSQL.append(" AND pr.service_type = :serviceType");
			conditionArgs.put("serviceType", serviceType);
		}
		
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND ui.name like :userName");
			countSQL.append(" AND ui.name like :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		if (StringUtils.isNotBlank(serviceOrderNo)) {
			querySQL.append(" AND pr.service_order_no = :serviceOrderNo");
			countSQL.append(" AND pr.service_order_no = :serviceOrderNo");
			conditionArgs.put("serviceOrderNo", serviceOrderNo);
		}
		
		if (StringUtils.isNotBlank(orderNo)) {
			querySQL.append(" AND pr.order_no = :orderNo");
			countSQL.append(" AND pr.order_no = :orderNo");
			conditionArgs.put("orderNo", orderNo);
		}
		 
		querySQL.append(" ORDER BY time DESC"); //按时间降序

		return paymentRequstDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), PaymentRequestLogs.class, conditionArgs);
	}
	
	/**
	 * 根据请求id查询请求实体
	 *
	 * @param requestId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public t_payment_request findPaymentRequestById (long requestId) {
		t_payment_request pr = paymentRequstDao.findByID(requestId);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求参数时，查询请求记录失败");
			
			return null;
		}
		
		return pr;
	}
	
	/**
	 * 查询托管回调参数列表
	 *
	 * @param requestId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public List<CallBackPrint> queryCallBackList(String requestMark, int serviceType) {
		List<t_payment_call_back> CBList = paymentCallBackDao.findListByColumn("request_mark = ?", requestMark);
		if (CBList == null || CBList.size() <= 0) {

			return null;
		}
		
		List<CallBackPrint> CBPList = new ArrayList<>();
		
		for (t_payment_call_back pcb : CBList) {
			CallBackPrint cbP = new CallBackPrint();
			cbP.time = pcb.time;
			
			if (serviceType == 1 ||serviceType == 46 || serviceType == 15 || serviceType == 14 || serviceType == 19 || serviceType == 16 || serviceType == 17 
					|| serviceType == 18 || serviceType == 5 ) {
				if (pcb.cb_params != null && !pcb.cb_params.equals("")) {
					Map<String, String> body = YbUtils.jsonToMap(pcb.cb_params);
					
					 Iterator<Map.Entry<String, String>> it3 = body.entrySet().iterator();
					  while (it3.hasNext()) {
					   Map.Entry<String, String> entry = it3.next();
					   cbP.body.put(entry.getKey() + " : " + entry.getValue(), "body");
					  }
				}
			} else {
				if (pcb.cb_params != null && !pcb.cb_params.equals("")) {
					JSONObject json = JSONObject.fromObject(pcb.cb_params);
					if (json.get("respHeader") != null) {
					 	Map<String, String> respHeader = YbUtils.jsonToMap(json.get("respHeader").toString());
					 	
					 	Iterator<Map.Entry<String, String>> it3 = respHeader.entrySet().iterator();
						  while (it3.hasNext()) {
						   Map.Entry<String, String> entry = it3.next();
						   cbP.body.put(entry.getKey() + " : " + entry.getValue(), "respHeader");
						  }
					} 

					if (json.get("outBody") != null) {
						Map<String, String> outBody = YbUtils.jsonToMap(json.get("outBody").toString());
						
						Iterator<Map.Entry<String, String>> it3 = outBody.entrySet().iterator();
						  while (it3.hasNext()) {
						   Map.Entry<String, String> entry = it3.next();
						   cbP.body.put(entry.getKey() + " : " + entry.getValue(), "outBody");
						  }
					}
					
				}
			}
			
			CBPList.add(cbP);
		}

		return CBPList;
	}
	
	/**
	 * 查询托管请求参数
	 * 
	 * @author niu
	 * @create 2017.10.20
	 */
	public JSONObject queryTrustReqParam(String requestMark) {
		
		if (StringUtils.isBlank(requestMark)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，requestMark为空");
			
			return null;
		}

		t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", requestMark);

		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求备注参数时，查询请求记录失败");
			
			return null;
		}
		
		if (StringUtils.isBlank(pr.req_params)) {
			LoggerUtil.info(false, "查询托管请求备注参数时，备注参数为空");
			
			return null;
		}
	
		
		if (pr.getService_type().code  == 19 || pr.getService_type().code  == 16 || pr.getService_type().code  == 17 || pr.getService_type().code  == 18 || pr.getService_type().code  == 5 ) {
			JSONObject json = new JSONObject();
			json.put("body", pr.req_params);
			return json;
		}
		
		return JSONObject.fromObject(pr.req_params);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
