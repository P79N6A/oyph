package models.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.utils.DateUtil;
import models.common.entity.t_user;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_user;
import play.db.jpa.Model;

@Entity
public class t_mall_virtual_goods_records extends Model{

	/**user表id*/
	public Long user_id;
	
	/**商品信息表id*/
	public Long mall_goods_record_id;
	
	/**虚拟物品表id*/
	public Long goods_id;
	
	/**会员名称*/
	@Transient
	public String user_name;
	public String getUser_name () {
		if (user_id != null) {
			t_user user = t_user.findById(user_id);
			return user.name;
		}
		return null;
	}
	
	/**兑换物品*/
	@Transient
	public String goodsName;
	public String getGoodsName () {
		if (mall_goods_record_id != null) {
			t_mall_scroe_record goods = t_mall_scroe_record.findById(mall_goods_record_id);
			return goods.goodsName;
		}
		return null;
	}
	/**兑换日期*/
	@Transient
	public String dateTime;
	public String getDateTime () {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (goodsName != null) {
			if (goodsName.indexOf("红包") != -1) {
				t_red_packet_user redPacketUser = t_red_packet_user.findById(goods_id);
				return sdf.format(redPacketUser.time);
			}
			if (goodsName.indexOf("加息券") != -1) {
				t_add_rate_user addRateUser = t_add_rate_user.findById(goods_id);
				return sdf.format(addRateUser.stime);
			}
		}
		return null;
	}
	
	/**兑换数量*/
	@Transient
	public Double quantity;
	public Double getQuantity() {
		if (mall_goods_record_id != null) {
			t_mall_scroe_record goods = t_mall_scroe_record.findById(mall_goods_record_id);
			return goods.quantity;
		}
		return null;
	}
	
	/**所需积分*/
	@Transient
	public int score;
	public int getScore() {
		if (mall_goods_record_id != null) {
			t_mall_scroe_record goods = t_mall_scroe_record.findById(mall_goods_record_id);
			return goods.scroe;
		}
		return 0;
	}
	
	/**有效时间*/
	@Transient
	public String limitDay;
	public String getLimitDay () {
		String day = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (goodsName != null) {
			if (goodsName.indexOf("红包") != -1) {
				t_red_packet_user redPacketUser = t_red_packet_user.findById(goods_id);
				Date d = DateUtil.addDay(redPacketUser.limit_time, -redPacketUser.limit_day);
				day = sdf.format(d) + "~" + sdf.format(redPacketUser.limit_time);
			}
			if (goodsName.indexOf("加息券") != -1) {
				t_add_rate_user addRateUser = t_add_rate_user.findById(goods_id);
				day = addRateUser.stime + "~" + addRateUser.etime;
			}
		}
		return day;
	}
	
	/**使用状态*/
	@Transient
	public String status;
	public String getStatus() {
		String res = null;
		if (goodsName != null) {
			if (goodsName.indexOf("红包") != -1) {
				if (goods_id != null) {
					t_red_packet_user redPacketUser = t_red_packet_user.findById(goods_id);
					if (redPacketUser != null) {
						res = redPacketUser.getStatus().value;
					}
				}
				
			}
			if (goodsName.indexOf("加息券") != -1) {
				if (goods_id != null) {
					t_add_rate_user addRateUser = t_add_rate_user.findById(goods_id);
					if (addRateUser != null) {
						Integer sta = addRateUser.status;
						if (sta == 1) {
							res = "未使用";
						} else if (sta == 2) {
							res = "使用中";
						} else if (sta == 3) {
							res = "已使用";
						} else if (sta == 4) {
							res = "已失效";
						}
					}
				}
			}
		}
		return res;
	}
	
	/*public t_mall_virtual_goods_records () {}
	public t_mall_virtual_goods_records (Long id,Long user_id,Long mall_goods_record_id,Long goods_id,String user_name,String goodsName,Date dateTime,Double quantity,int score,String limitDay,String status) {
		this.id = id;
		this.user_id = user_id;
		this.mall_goods_record_id = mall_goods_record_id;
		this.goods_id = goods_id;
		this.user_name = user_name;
		this.goodsName = goodsName;
		this.dateTime = dateTime;
		this.quantity = quantity;
		this.score = score;
		this.limitDay = limitDay;
		this.status = status;
	}
	public t_mall_virtual_goods_records (Long id,Long user_id,Long mall_goods_record_id,Long goods_id) {
		this.id = id;
		this.user_id = user_id;
		this.mall_goods_record_id = mall_goods_record_id;
		this.goods_id = goods_id;
	}*/
}
