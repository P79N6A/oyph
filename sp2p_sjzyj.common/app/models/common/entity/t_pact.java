package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.PactType;
import common.utils.Security;

/**
 * 实体:合同
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
@Entity
public class t_pact extends Model {

	/** 合同生成时间 */
	public Date time;
	
	/** 主体的id(如债权转让合同对应的是债权id，散标投资对应的是bid) */
	public Long pid;
	
	/** 合同模板类型(后面可以扩展):0 散标投资，1 债权转让 */
	private int type;
	
	/** 合同的内容(生成好后的合同) */
	public String content;
	
	/** 水印图片路径 */
	public String image_url;
	/**投标人ID*/
	public Long user_id;
	/**上上签合同编号*/
	public String contract_id;
	@Transient
	public String sign;
	
	
	public PactType getType() {
		return PactType.getEnum(type);
	}

	public void setType(PactType type) {
		this.type = type.code;
	}

	public String getSign() {
		String signID = Security.addSign(id, Constants.MSG_PACT_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
}
