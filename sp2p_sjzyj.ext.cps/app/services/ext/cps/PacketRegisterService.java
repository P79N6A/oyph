package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import daos.ext.cps.PacketRegisterDao;
import models.ext.cps.entity.t_red_packet_register;
import services.base.BaseService;

public class PacketRegisterService extends BaseService<t_red_packet_register>{

	protected PacketRegisterDao packetRegisterDao = null;
	
	protected PacketRegisterService () {
		packetRegisterDao = Factory.getDao(PacketRegisterDao.class);
	}
	
	public List<t_red_packet_register> queryAll() {
		return packetRegisterDao.queryAll();
	}
}
