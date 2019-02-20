package services.ext.redpacket;

import common.utils.Factory;
import daos.ext.redpacket.RedpacketUserDao;
import models.ext.redpacket.entity.t_red_packet_user;
import services.base.BaseService;

/**
 * 红包用户service
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月20日
 */
public class RedpacketUserService extends BaseService<t_red_packet_user> {

	protected RedpacketUserDao redpacketUserDao = null;
	
	protected RedpacketUserService() {
		this.redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
		super.basedao = this.redpacketUserDao;
	}
}
