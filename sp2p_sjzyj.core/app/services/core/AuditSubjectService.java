package services.core;

import java.util.List;

import models.core.entity.t_audit_subject;
import services.base.BaseService;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.core.AuditSubjectDao;

/**
 * 审核科目的Service
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月8日
 */
public class AuditSubjectService extends BaseService<t_audit_subject> {
	
	protected AuditSubjectDao auditSubjectDao;
	
	protected AuditSubjectService() {
		auditSubjectDao = Factory.getDao(AuditSubjectDao.class);
		super.basedao = auditSubjectDao;
	}
	
	/**
	 * 创建审核科目
	 *
	 * @param name 审核科目名称
	 * @param description 审核科目描述
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public ResultInfo createAuditSubject(String name, String description){
		ResultInfo result = new ResultInfo();
		
		t_audit_subject tas = new t_audit_subject(name, description);
		boolean save = auditSubjectDao.save(tas);
		
		if (save) {
			result.code = 1;
			result.msg = "";
			result.obj = tas;
		} else {
			result.code = -1;
			result.msg = "添加失败";
		}
		
		return result;
	}
	
	/**
	 * 检查是否已经有这个审核科目
	 *
	 * @param name 审核科目名称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public boolean checkHasAuditSubject(String name){
		List<t_audit_subject> list = super.findListByColumn("name=?", name);
		
		if(list == null || list.size() == 0){
			
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 根据id更新审核科目描述
	 *
	 * @param id 审核科目id
	 * @param description 审核科目描述
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public boolean updateAuditSubject(long id, String description) {
		t_audit_subject tas = super.findByID(id);
		if(tas == null){
			
			return false;
		}
		
		tas.description = description;
		
		return auditSubjectDao.save(tas);
	}
	
	/**
	 * 根据审核科目id查询对象
	 *
	 * @param id 审核科目id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public t_audit_subject findAuditSubject(long id){
		
		return super.findByID(id);
	}
	
	/**
	 * 分页查询审核科目
	 *
	 * @param currPage 当前页
	 * @param pageSize 页大小
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public PageBean<t_audit_subject> pageOfAuditSubject(int currPage, int pageSize){
		
		return auditSubjectDao.pageOfAuditSubject(currPage, pageSize);
	}
	
	/**
	 * 查询一列
	 *
	 * @param ids 审核科目id列
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public List<t_audit_subject> queryAuditSubject(List<Integer> ids){
		if(ids == null || ids.size() == 0){
			
			return null;
		}
		
		return auditSubjectDao.findAuditSubject(ids);
	}
}
