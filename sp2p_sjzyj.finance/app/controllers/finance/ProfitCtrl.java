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
import models.finance.entity.t_current_assets;
import models.finance.entity.t_profit;
import services.finance.ProfitService;

public class ProfitCtrl extends BaseController{

	protected static ProfitService profitService = Factory.getService(ProfitService.class);
	
	public void createProfitXml(){
		profitService.createProfitXml();
	} 
}
