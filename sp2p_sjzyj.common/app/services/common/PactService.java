package services.common;

import java.io.File;
import java.util.List;

import models.app.bean.MyInvestRecordBean;
import models.common.entity.t_pact;
import models.common.entity.t_template_pact;
import models.main.bean.LoanContract;

import org.apache.commons.lang.StringUtils;

import play.Play;
import services.base.BaseService;

import common.enums.PactType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.pdf.DefaultPdfWatermark;
import common.utils.pdf.PDFUtil;
import dao.main.UserLoanContractDao;

import com.itextpdf.text.DocumentException;

import daos.common.PactDao;
import daos.common.TemplatePactDao;

/**
 * 合同service的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
public class PactService extends BaseService<t_pact> {

	protected PactDao pactDao = null;
	
	protected TemplatePactDao templatePactDao = Factory.getDao(TemplatePactDao.class);
	
	private static UserLoanContractDao userLoanContractDao = Factory.getDao(UserLoanContractDao.class);
	
	protected PactService (){
		this.pactDao = Factory.getDao(PactDao.class);
		super.basedao = pactDao;
	}	
	
	/**
	 * 插入一条合同记录到数据库
	 *
	 * @param pact
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public ResultInfo createPact(t_pact pact) {
		ResultInfo result = new ResultInfo();
		boolean flag = pactDao.save(pact);
		if(!flag){
			result.code = -1;
			result.msg = "合同没有添加到数据库";
			
			return result;
		} 
		result.code = 1;
		result.msg = "合同添加成功";
		result.obj = pact;
	
		return result;
	}
	
	/**
	 * 更新一个合同模板的名称/内容、水印
	 *
	 * @param id 待更新模板的id
	 * @param name 更新后的名称
	 * @param content 合同模板的内容
	 * @param imageUrl 水印图片的路径
	 * @param imageResolution 水印图片吗的分辨率
	 * @param imageSize 水印图片的大小
	 * @param imageFormat 水印图片的后缀名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public boolean updatePactTemp(long id,String name,String content,String imageUrl,String imageResolution,String imageSize,String imageFormat) {
		t_template_pact pact = templatePactDao.findByID(id);
		pact.name = name;
		pact.content = content;
		pact.image_url = imageUrl;
		pact.image_resolution = imageResolution;
		pact.image_size = imageSize;
		pact.image_format = imageFormat;
		
		return templatePactDao.save(pact);
	}

	/**
	 * 根据合同类型查找合同模板
	 *
	 * @param pactType
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_template_pact findByType(PactType pactType) {
		t_template_pact temp = templatePactDao.findByColumn(" type=? ", pactType.code);
		
		return temp;
	}
	
	/**
	 * 查找bid对应的合同(一个bid只有一份合同)
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_pact findByBidid(long bidId) {
		t_pact pact = pactDao.findByColumn(" type=? and pid=?", PactType.BID.code,bidId);
		
		return  pact;
	}
	
	/**
	 * 查找债权对应的合同(一个债权只有一份合同)
	 *
	 * @param debtId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public t_pact findByDebtId(long debtId) {
		t_pact pact = pactDao.findByColumn(" type=? and pid=?", PactType.DEBT.code,debtId);
		
		return  pact;
	}
	
	/**
	 * 查询所有的合同模板
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public List<t_template_pact> queryAllTemps() {
		
		return templatePactDao.findAll();
	}

	/**
	 * 根据合同模板id查找合同
	 *
	 * @param tempId 合同id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public t_template_pact findByTempId(long tempId) {
		
		return templatePactDao.findByID(tempId);
	}

	/**
	 * 导出合同成PDF文档
	 *
	 * @param pactId 合同id
	 * @param withWater 是否添加水印
	 * @return 如果成功则obj中为导出后的file
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public ResultInfo exportPact(long pactId, boolean withWater) {
		ResultInfo result = new ResultInfo();
				
		t_pact pact = pactDao.findByID(pactId);
		if (pact == null) {
			result.code = -1;
			result.msg = "合同不存在";

			return result; 
		}
		
		File expFile = null;
		try {
			String image_url = pact.image_url;
			if(withWater && StringUtils.isNotBlank(image_url)){
				
				if (image_url.startsWith("/")) {
					image_url = image_url.substring(1);
				}
				File file = Play.getFile(image_url);
				String imgString = "";
				if(file.exists()){
					imgString = file.getAbsolutePath();
				} else {
					imgString = PDFUtil.WATERMARKIMAGEPATH;
				}
				
				expFile = PDFUtil.exportHTMLPdfWithWatermark(pact.content, null, new DefaultPdfWatermark(imgString));
			} else {
				expFile = PDFUtil.exportHTMLPdf(pact.content, null);
			}
			result.code = 1;
			result.msg = "导出成功";
			result.obj = expFile;
			
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.info(false, e, "合同导出失败【合同id:%s】", pactId+"");
			result.code = -1;
			result.msg = "合同导出失败!";
		}
		
		return result;
	}

	public List<t_pact> findListByType(int i) {
	
		return pactDao.findListByType(i);
	}
	/**
	 * 
	 * @Title: findByCondition
	 * @description 查询单个投资人的合同
	 * @param bidId
	 * @param userId
	 * @return
	 * t_pact
	 * @author likai
	 * @createDate 2018年11月21日 上午10:11:57
	 */
	public t_pact findByCondition(Long bidId, Long userId) {
		return pactDao.findByCondition(bidId,userId);
	}


	/**
	 * 
	 * @Title: findListLoanContract
	 * @description 查询一个标的所有合同
	 * @param bidId
	 * @return
	 * List<LoanContract>
	 * @author likai
	 * @createDate 2018年11月21日 上午10:12:52
	 */
	public List<LoanContract> findListLoanContract(Long bidId) {
		return userLoanContractDao.findListLoanContract(bidId);
	}
	
	/**
	 * 
	 * @Title: findLoanContract
	 * @description 查询一个标的合同（旧）
	 * @param bidId
	 * @return
	 * List<LoanContract>
	 * @author likai
	 * @createDate 2018年11月21日 上午10:13:45
	 */
	public List<LoanContract> findLoanContract(Long bidId) {
		return userLoanContractDao.findLoanContract(bidId);
	}
	/**
	 * 
	 * @Title: addcontractId
	 * @description 添加上上签合同号
	 * @param pact
	 * @param contractId
	 * @return
	 * boolean
	 * @author likai
	 * @createDate 2018年11月21日 下午2:43:21
	 */
	public boolean addcontractId(t_pact pact, String contractId) {
		pact.contract_id = contractId;
		return pactDao.save(pact);
	}

	public PageBean<LoanContract> PageFindLoanContract(long bidId, int currPage, int pageSize) {

		PageBean<LoanContract> page = userLoanContractDao.PageFindLoanContract(currPage, pageSize, bidId);

		return page;
	}

	public PageBean<LoanContract> PageFindListLoanContract(long bidId, int currPage, int pageSize) {

		PageBean<LoanContract> page = userLoanContractDao.PageFindListLoanContract(currPage, pageSize, bidId);

		return page;
	}

}
