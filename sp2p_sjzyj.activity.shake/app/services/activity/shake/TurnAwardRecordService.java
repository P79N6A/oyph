package services.activity.shake;

import java.util.List;

import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;

import common.utils.Factory;
import common.utils.PageBean;
import daos.activity.shake.TurnAwardRecordDao;
import models.activity.shake.entity.t_turn_award_record;
import services.base.BaseService;

public class TurnAwardRecordService extends BaseService <t_turn_award_record> {
	protected  TurnAwardRecordDao turnAwardRecordDao = Factory.getDao(TurnAwardRecordDao.class);
	
	/**
	 * 
	 * @Title: listOfTurn
	 *
	 * @description 分页显示
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页返回记录数
	 * 
	 * @param @return 
	 * 
	 * @return PageBean<t_turn_award_record>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月29日
	 */
	public PageBean<t_turn_award_record> listOfTurn(int currPage, int pageSize) {
		
		return turnAwardRecordDao.pageOfByTurn(currPage, pageSize);
	}
	
	/**
	 * 
	 * @Title: findByUserId
	 *
	 * @description 根据UserId查询本人中奖信息
	 *
	 * @param currPage 当前页数
	 * @param pageSize 每页返回记录数
	 * @param userid 登陆者Id
	 * 
	 * @return List<t_turn_award_record>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年10月26日
	 */
	public PageBean<t_turn_award_record> findByUserId (int currPage, int pageSize ,Long userid) {
		
		return turnAwardRecordDao.findByUserId(currPage, pageSize,userid);
	}
	/**
	 * 
	 * @Title: findById
	 * 
	 * @description 中奖后根据id修改收货地址 
	 * @param id
	 * @return t_turn_award_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月30日-上午11:00:50
	 */
	public t_turn_award_record findById(Long id){
		
		return turnAwardRecordDao.findByID(id);
	}
	/**
	 * 
	 * @Title: updateEndStatus
	 * 
	 * @description 修改到期状态
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月30日-下午1:40:53
	 */
	public int updateEndStatus(){
		
		return turnAwardRecordDao.updateEndStatus();
	}
	/**
	 * 
	 * @Title: insert
	 * 
	 * @description 插入中奖信息并返回当前id
	 * @param t_turn_award_record
	 * @return t_turn_award_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午2:55:32
	 */
	public t_turn_award_record insert(t_turn_award_record t_turn_award_record) {
		
		return turnAwardRecordDao.insert(t_turn_award_record);
	}
	/**
	 * 
	 * @Title: getInfoHistory
	 * 
	 * @description 查询回显地址和电话
	 * @param @param user_id
	 * @return t_turn_award_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午4:17:59
	 */
	public t_turn_award_record getInfoHistory(Long user_id) {
		
		return turnAwardRecordDao.getInfoHistory(user_id);
	}
	/**
	 * 
	 * @Title: updateReciveStatus
	 * 
	 * @description 点击领取修改领取状态
	 * @param @param award_id
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月1日-下午4:18:17
	 */
	public int updateReciveStatus(Long id) {
		
		return turnAwardRecordDao.updateReciveStatus(id);
		
	}
	
	/**
	 * 
	 * @Title: findByUserId
	 *
	 * @description 查询登陆人获得0.5元话费数量
	 *
	 * @param @param user_id
	 * @param @return 
	 * 
	 * @return t_turn_award_record    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月18日
	 */
	public t_turn_award_record findByUserId(Long user_id){
		
		return turnAwardRecordDao.findByUserId(user_id);
	}
	
	/**
	 * 
	 * @Title: updateOnetelCount
	 *
	 * @description 当0.5元话费20次兑换10元话费时插入一条10元话费中奖纪录
	 *
	 * @param @param user_id
	 * @param @return 
	 * 
	 * @return int    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月23日
	 */
	public int updateOnetelCount(Long user_id){
		
		return turnAwardRecordDao.updateOnetelCount(user_id);
	}
	
	
}
