package controllers.finance;

import common.utils.Factory;
import controllers.common.BackBaseController;
import services.finance.BorrowerRepaymentService;
import services.finance.LoanPactService;

public class LoanPactCtrl extends BackBaseController{
	protected static final LoanPactService loanPactService = Factory.getService(LoanPactService.class);
}
