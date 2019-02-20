package services.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.common.UserInvestDao;
import daos.common.UserVIPGradeDao;
import models.beans.MallGoods;
import models.beans.MallScroeRecord;
import models.common.entity.t_user_info;
import models.common.entity.t_user_invest;
import models.common.entity.t_user_vip_grade;
import models.common.entity.t_user_vip_records;
import models.entity.t_mall_goods;
import services.base.BaseService;

public class UserVIPGradeService extends BaseService<t_user_vip_grade>{

	protected final UserVIPRecordsService userVIPRecordsService = Factory.getService(UserVIPRecordsService.class);
	
	protected final UserInvestDao userInvestDao = Factory.getDao(UserInvestDao.class);
	
	protected final UserInvestService userInvestService = Factory.getService(UserInvestService.class);
	
	protected final UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected final UserVIPGradeDao userVIPGradeDao = Factory.getDao(UserVIPGradeDao.class);
	
	protected UserVIPGradeService () {
		super.basedao = this.userVIPGradeDao;
	}
	
	/**
	 * 用户vip等级修改
	 * @exception 当月月底待收不达标准自动降级，连续3个月达标即可升级
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public Integer updateUserVipGrade (Long user_id ,Date date) {
		
		t_user_vip_records records = userVIPRecordsService.queryUserVIPRecordsByUserIdAndDate(user_id, date);
		
		if (records != null) {
			return -1;
		}
		Long vg = null;
		//用户等级查询
		t_user_info userInfo = userInfoService.findByColumn(" user_id = ? ", user_id);
		if (userInfo == null) {
			return -1;
		}
		t_user_vip_grade vipGrade = userVIPGradeDao.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? )",  user_id);
		
		//如果客户VIP等级为空， 则该客户为普通客户
		if (vipGrade == null) {
			t_user_vip_grade newVIP = this.findByColumn(" grade = ? ", 0);
			vg = newVIP.id;
			//更改客户VIP等级
			int res = userVIPGradeDao.updateUserVipGrade(vg, user_id);
			if (res > 0) {
				//添加VIP客户等级更新记录
				return userVIPRecordsService.addUserVIPRecord(user_id, vg, date)==false?0:1;
			}
		}
		
		/**降级*/
		//待收查询
		t_user_invest invest = userInvestDao.queryUserInvestByDate(user_id, DateUtil.addMonth(date, -1));
		if (invest == null) {
			invest = userInvestService.addCurrInvestByUserAndDate(user_id, DateUtil.addMonth(date, -1));
		}
		//通过待收判断客户的VIP等级（降级判断）
		t_user_vip_grade vip = this.judgeGradeByInvest(invest);
		if (vip != null && vipGrade != null ? vip.grade < vipGrade.grade :false) {
			if (vipGrade.grade > 0) {
				t_user_vip_grade newVIP = this.findByColumn("grade = ? ", vipGrade.grade-1 );
				vg= newVIP.id;
				
				//更改客户VIP等级
				int res = userVIPGradeDao.updateUserVipGrade(vg, user_id);
				if (res > 0) {
					//添加VIP客户等级更新记录
					return userVIPRecordsService.addUserVIPRecord(user_id, vg, date)==false?0:1;
				}
			}
			
		}
		
		/**升级*/
		//通过待收查询判断客户的VIP等级（升级判断）
		List<t_user_invest> iList = userInvestService.queryUserInvestListByDate(DateUtil.addMonth(date, -1), 3, user_id);
		//升级判断
		t_user_vip_grade result = this.judgeGradeByInvests(iList,user_id);
		if (result != null) {
			
			//如果该月是此用户的生日月 客户升级至至尊VIP，并赠送生日礼物
			if (DateUtil.dateToString(userInfo.birthday, "MM").equals(DateUtil.dateToString(new Date(), "MM"))) {
				//更改客户VIP等级
				int res = userVIPGradeDao.updateUserVipGrade(result.id, user_id);
				if (res > 0 && result.grade == 3 && result.birthday_gift_id != null) {
					
					t_mall_goods goods = MallGoods.queryGoodsById(result.birthday_gift_id);
					
					if (goods != null) {
						int ress = MallScroeRecord.saveRecord( goods ,new Double(1),user_id, 0, goods.name, "收货人:"+userInfo.reality_name+"</br>手机号码:"+userInfo.mobile+"</br>备注:" , new ResultInfo()).code;
						if (ress > 0) {
							//添加VIP客户等级更新记录
							return userVIPRecordsService.addUserVIPRecord(user_id, result.id, date)==false?0:1;
						}
					}
					
				}
				if (res > 0) {
					//添加VIP客户等级更新记录
					return userVIPRecordsService.addUserVIPRecord(user_id, result.id, date)==false?0:1;
				}
				
			//如果此月份不是生日月份，升级，不赠送生日礼物
			} else {
				//更改客户VIP等级
				int res = userVIPGradeDao.updateUserVipGrade(result.id, user_id);
				if (res > 0) {
					//添加VIP客户等级更新记录
					return userVIPRecordsService.addUserVIPRecord(user_id, result.id, date)==false?0:1;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * 通过待收判断客户VIP等级
	 * @param userInvest 用户待收
	 * 
	 * @author guoShiJie
	 * @createDate 2018.11.6
	 * */
	public t_user_vip_grade judgeGradeByInvest (t_user_invest userInvest) {
		t_user_vip_grade vipGrade = null;
		List<t_user_vip_grade> vipList = userVIPGradeDao.queryAll();
		if (vipList != null && vipList.size() > 0 && userInvest != null) {
			for (int i = 0 ; i < vipList.size() ; i++) {
				t_user_vip_grade vip = vipList.get(i);
				int a = userInvest.curr_total_invest_amounts.compareTo(new BigDecimal(vip.min_amount*10000));
				
				if (a >= 0 ) {
					vipGrade = vip;
					break;
				}
			}
		}
		
		return vipGrade;
	}
	
	/**
	 * 判断多个连续月待收是否达标
	 * @param userInvestList 用户指定月份的待收列表
	 * 
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public t_user_vip_grade judgeGradeByInvests (List<t_user_invest> userInvestList , Long user_id) {
		
		//客户的当前等级
		t_user_vip_grade grade = this.findByColumn("id = ( select vip_grade_id from t_user_info where user_id = ? )", user_id);
		
		Boolean flag = true;
		List<t_user_vip_grade> list = new ArrayList<t_user_vip_grade>();
		t_user_vip_grade result =null;
		//指定月份的待收是哪个等级
		if (userInvestList != null && userInvestList.size() > 0) {
			if (userInvestList.size() >= 3) {
				for(int i = 0 ; i < userInvestList.size() ; i++){
					t_user_vip_grade vipGrade = this.judgeGradeByInvest(userInvestList.get(i));
					if (vipGrade != null) {
						list.add(vipGrade);
					}
				}
			}
			if (userInvestList.size() < 3) {
				flag = false;
			}
		}
		//判断待收是否在这几个月达标
		if (list != null && list.size() > 0) {
			for (int i = 0 ; i < list.size() ; i++) {
				if (grade.grade >= list.get(i).grade) {
					flag = false;
					break;
				}
			}
		}
		
		//获取这个客户新的VIP等级
		if (flag) {
			if (grade.grade < 3) {
				result = this.findByColumn(" grade = ? ", grade.grade+1 );
			}
		}
		
		return result;
	}
}
