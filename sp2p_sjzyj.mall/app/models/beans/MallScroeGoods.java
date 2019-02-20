package models.beans;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.jpa.Model;
import play.db.jpa.Transactional;

public class MallScroeGoods extends Model {
	public long good_id;
	public String name;
	public String goodsPicPath;
	public int scroe;
	public Double quantity;
	public Date time;
	public String remark;
	
	public MallScroeGoods(long id,long good_id,String name,String goodsPicPath,int scroe,Double quantity,Date time,String remark) {
		this.id = id;
		this.name = name;
		this.goodsPicPath = goodsPicPath;
		this.scroe = scroe;
		this.quantity = quantity;
		this.time = time;
		this.remark = remark;
	}
	
	public MallScroeGoods() {
	}
	
}
