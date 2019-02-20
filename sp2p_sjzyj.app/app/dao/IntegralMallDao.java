package dao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.shove.Convert;

import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import models.app.bean.BillInvestListBean;
import models.app.bean.IntegralGoodsType;
import models.beans.MallGoods;
import models.entity.t_mall_goods;
import models.entity.t_mall_goods_type;
import daos.base.BaseDao;

/**
 * 积分商城到数据库的DAO
 *
 * @description 
 *
 * @author niudongfeng
 * @createDate 2016年4月5日
 */
public class IntegralMallDao extends BaseDao<t_mall_goods_type> {
	
	/***
	 * 
	 * 查找所有积分商品类型
	 * @param
	 * @return
	 *
	 * @author niudongfeng
	 * @createDate 2017-2-21
	 */
	public List<Map<String, Object>> listOfGoodsTypes() {
		String sql = "SELECT t.id AS id, t.goods_type AS goodsType, t.pic_path As picturePath FROM t_mall_goods_type t";
		
		return 	findListMapBySQL(sql, null);
	}
	
	/***
	 * 
	 * 按商品类型ID查找商品
	 * @param
	 * @return
	 *
	 * @author niudongfeng
	 * @createDate 2017-2-21
	 */
	public static String typeGoodsList(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		ResultInfo error = new ResultInfo();
		
		result.put("code", 1);
		result.put("msg", "成功");
		
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		String typeIdStr = parameters.get("typeId");
		
		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}

		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		int typeId = Convert.strToInt(typeIdStr, 0);

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		PageBean<t_mall_goods> page = MallGoods.queryMallGoodsByPage(null, 1, null, null, currPage, pageSize, new ResultInfo(),typeId);

		for (t_mall_goods goods : page.page) {
			t_mall_goods_type goodType = t_mall_goods_type.findById((long)goods.type_id);
			if (goodType != null) {
				goods.type_name = goodType.goods_type;
				goods.type = goodType.type;
			}
		}
		
		result.put("code", 1);
		result.put("msg", "查询成功");
		result.put("malls", page.page);
		
		return JSONObject.fromObject(result).toString();
	}
	
}
