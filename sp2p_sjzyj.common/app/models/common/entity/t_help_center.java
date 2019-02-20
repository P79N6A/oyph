package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;

/**
 * 帮助中心
 *
 * @author liudong
 * @createDate 2016年1月9日
 */
@Entity
public class t_help_center extends Model {
	
	/** 添加时间 */
	public Date time;
	
	/** 栏目*/
	private int column_no;
	
	/** 标题 */
	public String title;
	
	/** 内容答案 */
	public String content;
	
	/** 排序时间*/
	public Date order_time;

	/** 0-下架 1-上架*/
	private boolean is_use;
		
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.HELPCENTER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}
	
	public Column getColumn() {
		Column column = Column.getEnum(this.column_no);
		
		return column;   
	}

	public void setColumn(Column column) {
		if(column!=null){
			this.column_no = column.code;
		}
		
	}
	
	/**
	 * 枚举：栏目名称
	 * 1-金融知识 2-借款专题 3-理财专题
	 *
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public enum Column {
		
		/** 金融知识  */
		FINANCIAL_KNOWLEDGE(1,"讴业指南"),
		
		/** 借款专题  */
		LOAN_PROJECT(2,"借款专题");

		public int code;
		public String value;

		private Column(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Column getEnum(int code) {
			Column[] columns = Column.values();
			for (Column column : columns) {
				if (column.code == code) {

					return column;
				}
			}

			return null;
		}
	}


}
