package controllers.back.weChat;

import controllers.common.BackBaseController;

/**
 * 后台-微信-多图文回复-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月17日
 */
public class MoreGraphicMessageCtrl extends BackBaseController {

	/**
	 * 进入多图文列表界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月17日
	 */
	public static void showMoreGraphicListPre(){
		
		render();
	
	}
	/**
	 * 进入多图文添加界面
	 *
	 * @return
	 *
	 * @author gengjincang
	 * @createDate 2018年5月29日14:06:32
	 */
	public static void toAddMoreGraphicPre(){
			
		render();
		
	}
	/**
	 * 进入多图文编辑界面
	 *
	 * @return
	 *
	 * @author gengjincang
	 * @createDate 2018年5月29日14:06:32
	 */
	public static void toEditMoreGraphicPre(){
			
		render();
		
	}
}
