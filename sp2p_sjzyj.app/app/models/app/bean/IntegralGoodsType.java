package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/***
 * 积分商品类型实体类
 *
 * @description 
 *
 * @author niudongfeng
 * @createDate 2017-2-21
 */
@Entity
public class IntegralGoodsType implements Serializable {
	
	@Id
	public long id;
	
	/** 积分商品类型  */
	public String goodsType;
	
	/** 图片路径  */
	public String picturePath;
}
