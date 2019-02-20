package models.beans;

import java.util.List;

import javax.persistence.Query;

import models.entity.t_mall_address;
import common.constants.MallConstants;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPA;

/**
 * 积分商城 收货地址
 * 
 * @author yuy
 * @time 2015-10-13 17:17
 *
 */
public class MallAddress {

	/**
	 * 查询收货地址
	 * 
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public static List<t_mall_address> queryAddressByList(long user_id, ResultInfo resultInfo ) {
		String sql = "select new t_mall_address(id, user_id, time, receiver, tel, address, postcode) "
				+ "from t_mall_address t where user_id = ? order by time desc";
		List<t_mall_address> list = null;
		try {
			Query query = JPA.em().createQuery(sql, t_mall_address.class).setParameter(1, user_id);
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询收货地址时:" + e.getMessage());
			resultInfo.code = MallConstants.DML_ERROR_CODE;
			return null;
		}

		return list;
	}

	/**
	 * 查询收货地址
	 * 
	 * @param id
	 * @return
	 */
	public static t_mall_address queryAddressById(long id) {
		t_mall_address address = null;
		try {
			address = t_mall_address.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("查询收货地址时：" + e.getMessage());
		}
		return address;
	}

	/**
	 * 保存积分规则
	 * 
	 * @param goods
	 * @return
	 */
	public static int saveAddress(t_mall_address address) {
		if (address == null)
			return MallConstants.COM_ERROR_CODE;
		// update
		if (address.id != null) {
			address = clone(address);
		}

		try {
			address.save();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error("保存积分规则时：" + e.getMessage());
			return MallConstants.DML_ERROR_CODE;
		}
		return MallConstants.SUCCESS_CODE;
	}

	/**
	 * 克隆
	 * 
	 * @param goods
	 * @return
	 */
	private static t_mall_address clone(t_mall_address address) {
		if (address == null)
			return null;
		if (address.id == null)
			return address;

		t_mall_address address_ = queryAddressById(address.id);
		address_.receiver = address.receiver;
		address_.tel = address.tel;
		address_.address = address.address;
		address_.postcode = address.postcode;
		return address_;
	}
}
