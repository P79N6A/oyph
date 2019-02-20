package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_bank_card_user;
import models.common.entity.t_bank_card_user.Status;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.BankCardUserDao;

/**
 * 用户银行卡信息service实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
public class BankCardUserService extends BaseService<t_bank_card_user> {

	private BankCardUserDao bankCardUserDao = Factory.getDao(BankCardUserDao.class);
	
	protected BankCardUserService() {
		super.basedao = this.bankCardUserDao;
	}

	/**
	 * 查询用户银行卡列表
	 *
	 * @param pageSize
	 * @param currPage
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public PageBean<t_bank_card_user> pageOfUserCard(int currPage,
			int pageSize, long userId) {
		
		return bankCardUserDao.pageOfUserCard(currPage, pageSize, userId);
	}

	/**
	 * 添加用户银行卡
	 *
	 * @param userId  用户Id
	 * @param bankName 银行名称
	 * @param bankCode 银行code
	 * @param account 账号或卡号
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public boolean addUserCard(long userId, String bankName, String bankCode, String account) {
		t_bank_card_user.Status status = t_bank_card_user.Status.TYPE_DEFAULT;
		
		t_bank_card_user bcu = bankCardUserDao.findByColumn("user_id = ?", userId);
		if (bcu != null) {
			status = t_bank_card_user.Status.TYPE_NORMAL;
		}
		
		t_bank_card_user bankCard = new t_bank_card_user();
		bankCard.user_id = userId;
		bankCard.bank_name = bankName;
		bankCard.bank_code = bankCode;
		bankCard.account = account;
		bankCard.time = new Date();
		bankCard.setStatus(status);
		
		return bankCardUserDao.save(bankCard);
	}

	/**
	 * 删除用户银行卡
	 *
	 * @param id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public int delUserCard(long id) {
		
		return bankCardUserDao.delete(id);
	}


	/**
	 * 删除绑定银行卡
	 *
	 * @param userId
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public void delBank(long userId) {
		String sql = "DELETE FROM t_bank_card_user WHERE user_id = :userId";
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		bankCardUserDao.deleteBySQL(sql, args);
	}

	/**
	 * 修改用户银行卡状态
	 *
	 * @param id
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	public int updateUserCardStatus(long id, long userId) {

		return bankCardUserDao.updateCardStatus(id, userId);
	}

	/**
	 * 查询用户绑定银行卡
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 * @param userId
	 * @return
	 */
	public List<t_bank_card_user> queryCardByUserId(long userId) {
		
		return bankCardUserDao.findListByColumn("user_id = ? ORDER BY status", userId);
	}
	
	/***
	 * 查询用户默认卡号
	 * @param userId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-10
	 */
	public String queryDefaultCardAccount(long userId){
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("userId", userId);
		args.put("status", Status.TYPE_DEFAULT.code);

		return bankCardUserDao.findSingleStringBySQL("SELECT account FROM t_bank_card_user WHERE status =:status AND user_id =:userId ", null, args);
	}
	
	/**
	 * 查询最近的一个绑定的银行卡
	 * 
	 * 
	 * @param userId 用户id
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月19日
	 *
	 */
	public t_bank_card_user queryCard(long userId) {
		
		return bankCardUserDao.queryCard(userId);
	}

}
