package controllers.finance;

import common.utils.Factory;
import controllers.common.BackBaseController;
import services.finance.InvestmentContractService;

public class InvestmentContractCtrl extends BackBaseController{
	protected static final InvestmentContractService investmentContractService = Factory.getService(InvestmentContractService.class);
}
