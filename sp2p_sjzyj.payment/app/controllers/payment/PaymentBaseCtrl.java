package controllers.payment;

import common.utils.ResultInfo;
import play.mvc.Controller;

/**
 * 托管控制器 - 基类
 * 
 * 
 * 
 * 
 * @author niu
 * @create 2017.08.22
 */
public class PaymentBaseCtrl extends Controller {
	
	/**
	 * 
	 * 表单自动提交，请求第三方
	 * 
	 * @author niu
	 * @create 2017.08.22
	 * 
	 */
	public void submitForm(String html, int client) {
		renderHtml(html);
	}
	
	/**
	 * 
	 * 返回App处理,如果有需要可渲染成html
	 * 
	 * @author niu
	 * @create 2017.08.22
	 * 
	 */
	public static void redirectApp(ResultInfo result) {
		String html = "<title> 讴业普惠系统 </title> <meta name='keywords' content='讴业普惠系统' /> <meta name='description' content='讴业普惠系统' /> <meta content='text/html; charset=utf-8' http-equiv='Content-Type' /> <style type='text/css'> .emil-box{ width: 100%; font-family: 'Microsoft YaHei'; font-size: 4em; color: #333; background: #E7E8EB; word-break: break-all; } .emil-main{ background: #fff; height:25%; width:100%; text-align:center; }  .emil-notice{  height:100%; }  </style> <div class='emil-box'> <img src='http://www.niumail.com.cn/data/attachments/bd420f40-ab9e-4ea3-a611-6710a961a0ac' /> <div class='emil-main'> <p> <span style='font-size:1.2em;'>"+result.msg+"</span> </p> </div> <div class='emil-notice'>  </div> </div>";
		
		renderHtml(html);
	}
	
	
}
