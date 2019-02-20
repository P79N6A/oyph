package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 积分商城 商品信息
 * 
 * @author yuy
 * @time 2015-10-13 16:10
 */
@Entity
public class t_mall_goods extends Model {
	public String name;
	public Date time;
	public String pic_path;// 图片
	public String introduction;// 商品介绍
	public int total;// 商品总数
	public int max_exchange_count;// 最大可兑换数量
	public int surplus;// 剩余可兑换数量
	public int exchange_scroe;// 兑换所需积分
	public int status;// 是否允许兑换，1：允许，2：不允许
	public int visible;// 是否可见，1：可见，2：隐藏
	public int type_id; //商品类型
	public String pic_format;
	public long virtual_goods_id;
	@Transient
	public String type_name;
	@Transient
	public int type;
	
	public t_mall_goods() {
		super();
	}

	public t_mall_goods(long id, String name, Date time, String pic_path, String introduction, int total, int max_exchange_count, int surplus,
			int exchange_scroe, int status, int type_id, String pic_format) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.pic_path = pic_path;
		this.introduction = introduction;
		this.total = total;
		this.max_exchange_count = max_exchange_count;
		this.surplus = surplus;
		this.exchange_scroe = exchange_scroe;
		this.status = status;
		this.type_id = type_id;
		this.pic_format = pic_format;
	}
}
