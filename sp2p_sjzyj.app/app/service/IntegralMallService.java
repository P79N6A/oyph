package service;

import java.util.List;
import java.util.Map;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import services.base.BaseService;
import models.app.bean.IntegralGoodsType;
import models.entity.t_mall_goods_type;
import common.utils.Factory;
import dao.IntegralMallDao;
import dao.InvestAppDao;

/**
 * 积分商城service
 *
 * @description 
 *
 * @author NiuDongFeng
 * @createDate 2017年2月21日
 */
public class IntegralMallService extends BaseService<t_mall_goods_type>{
	
	private IntegralMallDao integralMallDao = null; 
	
	private IntegralMallService() {
		integralMallDao = Factory.getDao(IntegralMallDao.class);
	}
	
	/**
	 * 查询所有商品类型
	 *
	 * @param 
	 * @return
	 *
	 * @author NiuDongFeng
	 * @createDate 2017年2月21日
	 */
	public List<Map<String, Object>> listOfGoodsTypes() {
		
		return integralMallDao.listOfGoodsTypes();
	}
	
	/**
	 * 按商品类型查找商品
	 *
	 * @param 
	 * @return
	 *
	 * @author NiuDongFeng 
	 * @createDate 2017年2月21日
	 */
	public String findGoodsByTypeId(Map<String, String> parameters) {
		
		return integralMallDao.typeGoodsList(parameters);
	}
}
