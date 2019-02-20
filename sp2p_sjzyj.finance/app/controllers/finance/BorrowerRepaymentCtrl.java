package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.postgresql.jdbc2.optional.SimpleDataSource;

import common.utils.Factory;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import models.finance.entity.t_borrower_repayment;
import services.finance.BorrowerRepaymentService;

public class BorrowerRepaymentCtrl extends BaseController{
	
	protected   BorrowerRepaymentService borrowerRepaymentService = Factory.getService(BorrowerRepaymentService.class);
	
	
	
	/**
	 * 
	 *
	 * @Title: saveBorrowerRepayment
	
	 * @description: 借款人还款明细表 插入当月数据Ctrl
	 *
	 * @param id
	 * @param service_order_no 流水号
	 * @param real_repayment_time  还款日期
	 * @param p_id  贷款合同号
	 * @param user_id 借款人客户号
	 * @param repayment_corpus 还款本金
	 * @param repayment_interest 还款利息
	   
	 * @return void   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月6日-下午5:35:45
	 */
	public void saveBorrowerRepayment(){
		ResultInfo result = new ResultInfo();
		result = borrowerRepaymentService.saveBorrowerRepayment();
		
		System.out.println("result:"+result);
		if (result.code <0) {
				System.out.println("向借款人还款明细表中插入数据失败Ctrl");
			}else{
				System.out.println("向借款人还款明细表中插入数据成功Ctrl");
				borrowerRepaymentService.createBorrowerRepaymentXml();
			}
			
	}
	

	
}
