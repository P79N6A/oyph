package controllers.finance;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.utils.Factory;
import common.utils.TimeUtil;
import controllers.common.BaseController;
import models.finance.entity.t_cash_flow;
import models.finance.entity.t_current_assets;
import services.finance.CashFlowService;
import services.finance.CurrentAssetsService;
import services.finance.ProfitService;

public class CashFlowCtrl extends BaseController{
	
	protected CashFlowService cashFlowService = Factory.getService(CashFlowService.class);
	
	public void createCashFlowXml(){
		cashFlowService.createCashFlowXml();
	}
}
