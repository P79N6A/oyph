package services.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_right_action;
import models.common.entity.t_right_supervisor;
import play.cache.Cache;
import services.base.BaseService;

import common.constants.Constants;
import common.interfaces.ICacheable;
import common.utils.Factory;

import daos.common.RightActionDao;
import daos.common.RightDao;
import daos.common.RightSupervisorDao;

public class RightService extends BaseService<t_right_action> implements ICacheable {


	protected RightActionDao rightActionDao = null;
	
	protected RightDao rightDao = Factory.getDao(RightDao.class);
			
	protected RightSupervisorDao rightSupervisorDao = Factory.getDao(RightSupervisorDao.class);
	
	protected RightService() {
		rightActionDao =Factory.getDao(RightActionDao.class);
		basedao = rightActionDao;
	}

	/**
	 * 更新用户的权限
	 *
	 * @param userId 用户的id
	 * @param finalActions 权限以键值对的形式出现(权限id-是否存在(false:表示原来有，现在需要删除;true,不管原来有没有都需要添加的))
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月8日
	 */
	public boolean updateRightsOfUser(Long userId,Map<Long, Boolean> finalActions){
		if(finalActions == null || finalActions.size() <1){
			
			return false;
		}
		for(Long rightId : finalActions.keySet()){
			boolean flag = finalActions.get(rightId);
			t_right_supervisor userRight = rightSupervisorDao.findByColumn(" supervisor_id=? and right_id=? ", userId,rightId);
			if (flag) {
				if(userRight != null){//存在就不管了
					
					continue;
				} else {
					userRight = new t_right_supervisor();
					userRight.supervisor_id = userId;
					userRight.right_id = rightId;
					if(rightSupervisorDao.save(userRight)){
						continue;
					} else {
						
						return false;
					}
				}
			} else {
				//需要删除权限
				if(userRight == null){//基本上可以判定数据错误，
					
					return false;
				} else {
					if(rightSupervisorDao.delete(userRight.id)>0){
						continue;
					}else {
						return false;
					}
				}
				
			}
		}
		
		return true;
	}
	
	/**
	 * 根据action查找对应的rightId
	 *
	 * @param action
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月26日
	 */
	public List<Long> queryIdsByAction(String action){
		List<t_right_action> actions = getCache();
		if (actions != null && actions.size() > 0) {
			List<Long> result = new ArrayList<Long>();

			for (t_right_action action2 : actions) {
				if (action.equals(action2.action)) {
					result.add(action2.right_id);
				}
			}

			return result;
		}
		
		return null;
	}
	
	/**
	 * 查询用户的所有的操作(是t_right_action中的id和action，而不是t_right中的id，right)
	 *
	 * @param supervisorId 管理员的ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月26日
	 */
	public Map<Long, String> queryActionssOfUser(Long supervisorId) {
		
		return rightSupervisorDao.queryActionssOfUser(supervisorId);
	}
	
	/**
	 * 查询某个管理员的所有权限(t_right )
	 *
	 * @param supervisorId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月9日
	 */
	public List<Long> queryRightsOfSupervisor(Long supervisorId){
		List<t_right_supervisor> rights = rightSupervisorDao.findListByColumn(" supervisor_id=? ", supervisorId);
		
		if(rights != null && rights.size() > 0){
			List<Long> righlist = new ArrayList<Long>();
			for(t_right_supervisor right : rights){
				righlist.add(right.right_id);
			}
			return righlist;
		}
		
		return null;
	}
	
	/**
	 * 查询某个管理员的所有权限(t_right,不是action)
	 *
	 * @param supervisorId 管理员id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月9日
	 */
	public Map<Long,t_right_supervisor> queryRightsOfUser(Long supervisorId) {
		
		List<t_right_supervisor> rights = rightSupervisorDao.findListByColumn(" supervisor_id=? ", supervisorId);
		if(rights != null && rights.size() > 0){
			Map<Long,t_right_supervisor> rightmap = new HashMap<Long, t_right_supervisor>();
			for(t_right_supervisor right : rights){
				rightmap.put(right.right_id, right);
			}
			return rightmap;
		}
		
		return null;
	}

	@Override
	public void addAFlushCache() {
	
		List<t_right_action> actions = rightActionDao.findAll();

		Cache.safeSet(this.getClass().getName(),actions,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public List<t_right_action> getCache() {
		List<t_right_action> actions = (List<t_right_action>) Cache.get(this.getClass().getName());
		if(actions == null || actions.size() == 0){
			addAFlushCache();
			actions = (List<t_right_action>) Cache.get(this.getClass().getName());
		}
		
		return actions;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
	
}
