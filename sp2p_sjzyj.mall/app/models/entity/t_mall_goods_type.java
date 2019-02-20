package models.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class t_mall_goods_type extends Model{
	
	public String goods_type;
	
	public String pic_path;
	
	public String pic_format;
	
	public int type;
	
	public t_mall_goods_type() {
		super();
	}

	public t_mall_goods_type(long id, String goods_type, String pic_path, String pic_format) {
		this.id=id;
		this.goods_type = goods_type;
		this.pic_path = pic_path;
		this.pic_format = pic_format;
	}
	
	
	public t_mall_goods_type(long id, String goods_type, String pic_path, String pic_format,int type) {
		this.id=id;
		this.goods_type = goods_type;
		this.pic_path = pic_path;
		this.pic_format = pic_format;
		this.type = type;
	}
	
	public enum Type {
		/**实物*/
		MATERIAL(0,"实物"),
		/**虚拟*/
		VIRTUAL(1,"虚拟");
		
		int code;
		String value;
		
		private Type (int code,String value) {
			this.code = code;
			this.value = value;
		}
		
		public static Type getEnum (int code) {
			Type[] typeArray = Type.values();
			for (Type type : typeArray) {
				type.code = code;
				return type;
			}
			return null;
		}
	} 
}
