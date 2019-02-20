package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import daos.common.BranchDao;
import models.common.entity.t_risk_report.Status;
import play.db.jpa.Model;

@Entity
public class t_risk_reception extends Model{
	
	/**接待人姓名*/
	public String reception_name;
	
	/**专员电话*/
	public String phone;
	
	/**专员所属分公司*/
	public long branch_id;
	
	@Transient
	public String branch_company;
	public String getBranch_company(){
		BranchDao branchDao=common.utils.Factory.getDao(BranchDao.class);
		t_company_branch branch=branchDao.findByID(this.branch_id);
		if(branch!=null){
			return branch.branch_name;
		}else{
			return null;
		}
	}
	
	/**专员类型（1.风控专员 2.风控经理）*/
	public int type;
	
	public Type getType(){
		Type type = Type.getEnum(this.type);
		return type;
	}
	
	public enum Type {
		
		Clerk(1,"风控专员"),
		
		Manager(2,"风控经理"),
		
		CEO(3,"风控总监");
		
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private Type(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Type getEnum(int code){
			Type[] types = Type.values();
			for(Type type:types){
				if(type.code == code){
					
					return type;
				}
			}
			
			return null;
		}
		
	}
			
}
