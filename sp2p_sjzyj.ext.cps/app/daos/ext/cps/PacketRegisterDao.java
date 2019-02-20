package daos.ext.cps;

import java.util.List;

import daos.base.BaseDao;
import models.ext.cps.entity.t_red_packet_register;

public class PacketRegisterDao extends BaseDao<t_red_packet_register>{

	protected PacketRegisterDao () {}
	
	public List<t_red_packet_register> queryAll() {
		String sql = "select * from t_red_packet_register ";
		return this.findListBySQL(sql, null);
	}
	
}
