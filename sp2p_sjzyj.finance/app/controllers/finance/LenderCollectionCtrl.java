package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import models.finance.entity.t_borrower_repayment;
import models.finance.entity.t_lender_collection;
import services.finance.LenderCollectionService;

public class LenderCollectionCtrl extends BaseController{
	protected static  LenderCollectionService lenderCollectionService = Factory.getService(LenderCollectionService.class);
	/**
	 * 
	 *
	 * @Title: savaLenderCollection
	
	 * @description: 向出借人收款明细表中插入数据-控制器
	 *
	 * @param id
	 * @param service_order_no
	 * @param p_id
	 * @param user_id
	 * @param real_receive_time
	 * @param receive_corpus
	 * @param receive_interest 
	   
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月6日-下午2:39:48
	 */
	public static void savaLenderCollection(){
		ResultInfo result = new ResultInfo();
	
		 result = lenderCollectionService.savaLenderCollection();
		
		 if (result.code <0) {
			System.out.println("向出借人收款明细表中插入数据失败");
		}else{
			System.out.println("向出借人收款明细表中插入数据成功");
		}		
	}
	public static void getLenderCollection(){
		List<t_lender_collection> lendercollection =lenderCollectionService.getLenderCollection();
		
	}
	
	
}
