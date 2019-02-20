package common.tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrSupervisor;
import play.cache.Cache;
import play.mvc.Scope.Session;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;
import common.constants.CacheKey;
import controllers.common.BackBaseController;

public class CommonTags extends FastTags {
	
	/**
	 * 权限管理标签
	 * <br>demo:
	 * <br>#{rightMng rightId:100}<i data-title="编辑" class="iconfont edit-btn">&#xe602;</i>#{/rightMng}
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月9日
	 */
	public static void _rightMng(Map<String, Object> params, Closure body, PrintWriter out, 
			ExecutableTemplate template, int fromLine) {
		if (params.get("rightId") == null) {
			
			return;
		}
		
		long rightId = Long.parseLong(params.get("rightId").toString());
		
		List<Long> rights = null;
		CurrSupervisor currSupervisor = BackBaseController.getCurrSupervisor();
		if(currSupervisor != null){
			rights = currSupervisor.rights;
		}
		
		
		boolean hasRight = false;
		if (rights != null && rights.size() > 0) {
			for(Long right : rights){
				if(right.longValue() == rightId){
					hasRight = true;
					break;
				}
			}
		}
		
		//权限判断，有权限，则输出标签体内容
		if (hasRight) {
			out.println(JavaExtensions.toString(body));
		} 
	} 

}
