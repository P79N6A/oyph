package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.SupervisorEventLog;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_supervisor;
import models.common.entity.t_supervisor.LockStatus;

import org.apache.commons.lang.StringUtils;

import play.mvc.Http.Request;
import services.base.BaseService;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;

import daos.common.EventDao;
import daos.common.SupervisorDao;

/**
 * 后台管理员Service接口的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月15日
 */
public class SupervisorService extends BaseService<t_supervisor> {

	protected SupervisorDao supervisorDao = null;
	
	protected EventDao eventDao = Factory.getDao(EventDao.class);
	
	protected SupervisorService() {
		supervisorDao = Factory.getDao(SupervisorDao.class);
		super.basedao = this.supervisorDao;
	}

	/**
	 * 管理员登录,如果成功会将管理员的实体当做ResultInfo的entity返回
	 *
	 * @param supervisorName 管理员用户名
	 * @param password 管理员密码(这里拿到的是已经加密过的加密串)
	 * @param ip 
	 * @return 	Code取值<br>
	 * 			1:成功(entity中为该管理员的实体)<br>
	 *  		-1:用户名或者密码错误<br>
	 *  		-2:用户被锁定<br>
	 * 			-3:系统错误<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	public ResultInfo login(String supervisorName, String password, String ip) {
		ResultInfo result = new ResultInfo();
		
		t_supervisor supervisor = supervisorDao.findByColumn("name=?", supervisorName);
		if (supervisor == null) {
			result.code = -1;
			result.msg = "用户名不存在或者密码错误";

			return result;
		}
		if (!supervisor.password.equals(password)) {
			result.code = -1;
			result.msg = "用户名不存在或者密码错误";
			
			return result;
		}
		if( supervisor.getLock_status().equals(t_supervisor.LockStatus.STATUS_2_LOCKEDBYSYS) ) {
			result.code = -2;
			result.msg = "账号已被锁定!";
			
			return result;
		} else {
			
			supervisor.last_login_time = new Date();
			supervisor.last_login_ip = ip;
			supervisor.setLock_status(LockStatus.STATUS_0_NORMAL);
			supervisor.login_count += 1;
			supervisorDao.save(supervisor);
			
			result.code = 1;
			result.msg = "管理员登录成功";
			result.obj = supervisor;

			return result;
		}
	}

	/**
	 * 判断一个用户名是否存在，存在返回true，不存在返回false
	 *
	 * @param supervisorName 待判定的用户名(不区分大小写)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public boolean isNameExists(String name) {
		List<t_supervisor> listOfNames = findListByColumn(" name=? ", name);
		if (listOfNames != null && listOfNames.size() > 0) {

			return true;
		}
		
		return false;
	}

	/**
	 * 判断一个手机号是否已经存在，存在则返回true，不存在返回false
	 *
	 * @param mobile 待判定的手机号
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public boolean isMobileExists(String mobile) {
		List<t_supervisor> listOfMobiles = findListByColumn(" mobile=? ",mobile);
		if (listOfMobiles != null && listOfMobiles.size() > 0) {

			return true;
		}
		
		return false;
	}

	/**
	 * 添加一个管理员
	 *
	 * @param supervisor(name,mobile,password(已经经过加密的),reality_name,creater_id)
	 * @return Code取值<br>
	 * 			1:成功(entity中为该管理员的实体)<br>
	 *  		-1:用户名存在<br>
	 *  		-2:用户被锁定<br>
	 * 			-3:系统错误<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月15日
	 */
	public ResultInfo addSupervisor(t_supervisor supervisor) {
		ResultInfo result = new ResultInfo();
		
		if (isNameExists(supervisor.name)) {
			result.code = -1;
			result.msg = "该用户名已经存在，请重新填写!";

			return result;
		}
		
		if (isMobileExists(supervisor.mobile)) {
			result.code = -2;
			result.msg = "管理员的手机号不允许重复，请重新填写!";

			return result;
		}
		
		supervisor.time = new Date();
		supervisor.setLock_status(LockStatus.STATUS_0_NORMAL);
		boolean flag = supervisorDao.save(supervisor);
		if (!flag) {
			
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "系统错误";	
			
			return result;
		} 
		
		
		result.code = 1;
		result.msg="管理员添加成功!";
		result.obj = supervisor;
		
		return result;
	}
	
	/**
	 * 添加管理员事件
	 *
	 * @param supervisorId 操作管理员的id
	 * @param supervisorEvent 管理员事件枚举
	 * @param supervisorEventParam 管理员事件枚举中的描述参数
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public boolean addSupervisorEvent(long supervisorId, t_event_supervisor.Event supervisorEvent, Map<String, String> supervisorEventParam) {
		Request request = Request.current();
		String ip = "";
		if(request == null) {
			
			ip = "127.0.0.1";
		}
		ip = StringUtils.isBlank(request.remoteAddress.toString()) ? "127.0.0.1" : request.remoteAddress.toString();
		
		t_event_supervisor event_supervisor = new t_event_supervisor();
		event_supervisor.supervisor_id = supervisorId;
		event_supervisor.ip = ip;
		event_supervisor.setDescription(supervisorEvent, supervisorEventParam);
		
		return eventDao.save(event_supervisor);
	}	
	
	/**
	 * 更新管理员的密码
	 *
	 * @param supervisorId 管理员的id
	 * @param password 更新后的密码(已经经过加密处理)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateSupervisorPassword(long supervisorId, String password) {
		ResultInfo result = new ResultInfo();
		
		t_supervisor supervisor = supervisorDao.findByID(supervisorId);
		if (supervisor == null) {
			result.code = ResultInfo.ERROR_404;
			result.msg = "管理员员不存在";
			
			return result;
		}
		
		supervisor.password = password;
		boolean isPasswordUpdated = supervisorDao.save(supervisor);
		if ( !isPasswordUpdated ) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "系统错误!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "管理员密码已经更新，下次登录时会生效!";
		
		return result;
	}
	
	
	public ResultInfo updateSupervisorExtension(long supervisorId, String extension) {
		ResultInfo result = new ResultInfo();
		
		t_supervisor supervisor = supervisorDao.findByID(supervisorId);
		if (supervisor == null) {
			result.code = ResultInfo.ERROR_404;
			result.msg = "管理员员不存在";
			
			return result;
		}
		
		supervisor.extension = extension;
		boolean isExtensionUpdated = supervisorDao.save(supervisor);
		if ( !isExtensionUpdated ) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "系统错误!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "管理员推广码已更新";
		
		return result;
	}
	
	/**
	 * 更新管理
	 *
	 * @param supervisorId 管理员的id
	 * @param mobile 更新后的手机(该手机号不能和系统其它管理员的手机号冲突)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateSupervisorMobile(long supervisorId, String mobile) {
		ResultInfo result = new ResultInfo();
		
		t_supervisor supervisor = supervisorDao.findByID(supervisorId);
			
		supervisor.mobile = mobile;
		boolean flag = supervisorDao.save(supervisor);
		if (!flag) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "系统错误!";

			return result;
		}
		
		result.code = 1;
		result.msg = "管理员手机已经更新";
		
		return result;
		
	}

	/**
	 * 锁定一个管理员
	 *
	 * @param supervisorId 管理员的id
	 * @return Code取值<br>
	 * 			1:成功<br>
	 *  		-1:没有数据更新<br>
	 * 			-3:系统错误<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public boolean lockSupervisor(long supervisorId) {
		t_supervisor supervisor = findByID(supervisorId);
		supervisor.setLock_status(LockStatus.STATUS_2_LOCKEDBYSYS);
		
		return supervisorDao.save(supervisor);
	}

	/**
	 * 解锁一个管理员(只是针对被管理员的)
	 *
	 * @param supervisorId 管理员的id
	 * @return Code取值<br>
	 * 			1:成功<br>
	 *  		-1:没有数据更新<br>
	 * 			-3:系统错误<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public boolean unlockSupervisor(long supervisorId) {
		t_supervisor supervisor = findByID(supervisorId);
		supervisor.setLock_status(LockStatus.STATUS_0_NORMAL);
		
		return supervisorDao.save(supervisor);
	}
	
	/**
	 * 登录前验证云盾信息
	 *
	 * @param userName
	 * @param password
	 * @param sign
	 * @param time
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月14日
	 */
	public String checkUkey(String userName, String password, String sign, String keyTime) {
		String supervisorSign = "";
		String time = DateUtil.getHours();
		if(!keyTime.equalsIgnoreCase(time)) {

			return com.shove.security.Encrypt.encrypt3DES(Constants.CLOUD_SHIELD_SERVICE_TIME, ConfConst.ENCRYPTION_KEY_MD5);
		}
		
		String all = userName + password + time + ConfConst.ENCRYPTION_KEY_MD5;
		
		//把传过来的userName，password都3DES解密
		String userName2 = com.shove.security.Encrypt.decrypt3DES(userName, ConfConst.ENCRYPTION_KEY_MD5);
        String password2 = com.shove.security.Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_KEY_MD5);
        String MD5pass = com.shove.security.Encrypt.MD5(password2 + ConfConst.ENCRYPTION_KEY_MD5);

		//把传过来的userName，password，time 用MD5加密验证签名
		String sign2 = com.shove.security.Encrypt.MD5(all.trim());
		
		if(!sign.equalsIgnoreCase(sign2)) {
			return com.shove.security.Encrypt.encrypt3DES(Constants.CLOUD_SHIELD_SIGN_FAULT, ConfConst.ENCRYPTION_KEY_MD5);
		}
		
		t_supervisor supervisor = findByColumn("name=?", userName2);
		
		//查询管理员是否存在
		if(supervisor == null) {

			return com.shove.security.Encrypt.encrypt3DES(Constants.CLOUD_SHIELD_NOUSER, ConfConst.ENCRYPTION_KEY_MD5);
		}
		
		if (!MD5pass.equals(supervisor.password)) {
			
			return com.shove.security.Encrypt.encrypt3DES(Constants.CLOUD_SHIELD_PASSWORD_ERROR, ConfConst.ENCRYPTION_KEY_MD5);
		}
	
		supervisorSign = com.shove.security.Encrypt.encrypt3DES(Long.toString(supervisor.id), ConfConst.ENCRYPTION_KEY_MD5);//3DES加密用于传到控件
		
		return supervisorSign;
	}
	
	/**
	 * 分页查询所有的管理员
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的数据量
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月16日
	 */
	public PageBean<t_supervisor> pageOfAllSupervisors(int currPage, int pageSize, int status, String userName) {
		
		return supervisorDao.querySupervisor(currPage, pageSize, status, userName);
	}

	/**
	 * 根据管理员id和操作类型分页查询所有的管理员日志记录(管理员真实姓名模糊检索)
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的数据量
	 * @param orderType 0,按编号，2,按栏目
	 * @param orderValue 0,降序，1,升序
	 * @param supervisorId 管理员id(如果为null，则不加这个管理员这个条件)
	 * @param item 操作类型(如果为null，则不加操作类型这个条件)
	 * @param supervisorName 管理员真实姓名关键字
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public PageBean<SupervisorEventLog> pageOfAllEventLogs(int currPage,int pageSize,int orderType,int orderValue,Long supervisorId,t_event_supervisor.Item item,String supervisorName) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(es.id) FROM t_event_supervisor es LEFT JOIN t_supervisor s ON es.supervisor_id = s.id ");
		StringBuffer querySQL = new StringBuffer("SELECT es.*,IFNULL(s.reality_name,'') as supervisor_name FROM t_event_supervisor es LEFT JOIN t_supervisor s ON es.supervisor_id=s.id ");
		
		Map<String, Object> condition = null;
		if (supervisorId != null || item != null || StringUtils.isNotBlank(supervisorName)) {
			countSQL.append(" WHERE 1=1 ");
			querySQL.append(" WHERE 1=1 ");
			condition = new HashMap<String, Object>();
			
			if (supervisorId != null) {
				countSQL.append(" AND es.supervisor_id=:supervisor_id ");
				querySQL.append(" AND es.supervisor_id=:supervisor_id ");
				condition.put("supervisor_id", supervisorId);
			}
			if (item != null) {
				countSQL.append(" AND es.item=:item ");
				querySQL.append(" AND es.item=:item ");
				condition.put("item", item.code);
			}
			
			if (StringUtils.isNotBlank(supervisorName)) {
				countSQL.append(" AND s.reality_name LIKE :supervisorName ");
				querySQL.append(" AND s.reality_name LIKE :supervisorName  ");
				condition.put("supervisorName", "%"+supervisorName+"%");
			}
		}
		
		//0,按编号，2,按栏目
		if(orderType == 2){
			querySQL.append(" ORDER BY es.item ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		} else {
			querySQL.append(" ORDER BY es.id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		} 
		PageBean<SupervisorEventLog> page = eventDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), SupervisorEventLog.class,condition);
		
		return page;
	}
	
	
	/**
	 * 查询管理员
	 * @param extension
	 * @return
	 */
	public  long findSupervisorByExtension(String extension){
		 
		 t_supervisor supervisor = findByColumn("extension=?", extension);
		
		 if(supervisor==null){
			 
			 return 0;
		 }else{
			return supervisor.id; 
		 }
	}
	
}
