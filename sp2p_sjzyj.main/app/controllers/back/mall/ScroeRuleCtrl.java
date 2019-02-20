package controllers.back.mall;

import java.util.Date;

import models.beans.MallScroeRule;
import models.entity.t_mall_scroe_rule;

import org.apache.commons.lang.StringUtils;

import common.constants.MallConstants;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;


/**
 * 积分商城：规则
 * 
 * @author yuy
 * @created 2015-10-14
 */
public class ScroeRuleCtrl extends BackBaseController {

	/**
	 * 规则列表
	 */
	public static void scroeRuleListPre() {
		ResultInfo error = new ResultInfo();
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		int currPage = 0;
		int pageSize = 0;
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		t_mall_scroe_rule rule = t_mall_scroe_rule.find("type=1").first();
		t_mall_scroe_rule rule1 = t_mall_scroe_rule.find("type=2").first();
		t_mall_scroe_rule rule2 = t_mall_scroe_rule.find("type=3").first();

		double scroeLine = MallConstants.MALL_INVEST_SENDSCROE_LINE;
		render( scroeLine,rule,rule1,rule2);
	}

	/**
	 * 保存规则
	 */
	public static void saveScroeRule(int signScroe,int registerScore,int amount,int investScore) {
		/*String id = params.get("id");
		String typeStr = params.get("type");
		String scroeStr = params.get("scroe");

		t_mall_scroe_rule rule = new t_mall_scroe_rule();
		rule.id = StringUtils.isNotBlank(id) ? Long.parseLong(id) : null;
		rule.type = StringUtils.isNotBlank(typeStr) ? Integer.parseInt(typeStr) : 0;
		rule.time = new Date();
		rule.scroe = StringUtils.isNotBlank(scroeStr) ? Integer.parseInt(scroeStr) : 0;
		rule.status = MallConstants.STATUS_ENABLE;

		int result = MallScroeRule.saveRuleDetail(rule);
		if (result < 0) {
			if (result == MallConstants.DATA_DUPL_CODE)
				flash.error("抱歉，相同类型的规则已存在");
			else
				flash.error("抱歉，保存失败，请联系管理员");
		} else {
			flash.error("保存成功");
		}*/
		MallScroeRule.updateRules(signScroe, registerScore, amount, investScore);
		flash.error("保存成功");
		scroeRuleListPre();
	}

	/**
	 * 编辑规则 页面
	 * 
	 * @param id
	 * @param flag
	 *            1:新增 2：修改
	 */
	public static void editScroeRulePre(long id, int flag) {
		t_mall_scroe_rule rule = null;
		if (flag == MallConstants.MODIFY)
			rule = MallScroeRule.queryRuleDetailById(id);
		render(rule, flag);
	}

	/**
	 * 暂停规则
	 * 
	 * @param id
	 */
	public static void stopScroeRule(long id, int status) {
		int result = MallScroeRule.stopRule(id, status);
		String opeStr = status == MallConstants.STATUS_ENABLE ? MallConstants.STR_ENABLE : MallConstants.STR_DISABLE;
		if (result < 0) {
			flash.error("抱歉，%s失败，请联系管理员", opeStr);
		} else {
			flash.error("%s成功", opeStr);
		}
		scroeRuleListPre();
	}
}
