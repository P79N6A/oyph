package common.tags;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import play.templates.FastTags;
import play.templates.GroovyTemplate;
import play.templates.GroovyTemplate.ExecutableTemplate;

import com.shove.Convert;
import common.utils.number.Arith;

/**
 * 自定义标签
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月4日
 */
public class MyTags extends FastTags{

	/**
	 * 短格式的金额格式化标签
	 * 
	 * @description 用法 #{formatShortMoney money:successFinance /}
	 * 
	 * @param params 标签名称money
	 * @param body
	 * @param out
	 * @param template
	 * @param fromLine
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatShortMoney(Map<String, Object> params, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {
			
			return;
		}
		
		String result = "";
		
		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money < 10000) {
			result = String.format("%.2f", money);
		} else if (10000 <= money && money < 100000000) {
			result = String.format("%.2f", Arith.round(money , 2)) ;
		} else if (100000000 <= money && money < 1000000000000.00) {
			result = String.format("%.2f", Arith.round(money , 2)) ;
		} else {
			result =  String.format("%.2f", Arith.round(money, 2)) ;
		}
		
		out.println(result);
	}
	
	/**
	 * 整型金额格式化标签
	 * <br>demo:#{formatIntMoney money:invest_amount}#{/formatIntMoney}
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void _formatIntMoney(Map<String, Object> params, Closure body, PrintWriter out, 
			ExecutableTemplate template, int fromLine) {
		if (params.get("money") == null) {
			
			return;
		}
		
		double money = Convert.strToDouble(params.get("money").toString(), 0);

		if (money%1 == 0) {
			NumberFormat formater = new DecimalFormat("#");
			out.println(formater.format(money));  
			
			return;
		}
		  
        out.println(money+"");  
    } 
	
	/**
	 * 图片。目前支持的可配置的img属性：src, data-original, width, height, class, style, alt
	 * <br>demo：
	 * <br>图片懒加载：#{img data_original:tp?.image_url, class:"lazy", width:"1920", height:"400" /}
	 * <br>图片加载：#{img src:tp?.image_url, width:"1920", height:"400"/}
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月25日
	 */
	public static void _img(Map args, Closure body, PrintWriter out, GroovyTemplate.ExecutableTemplate template, int fromLine){
		StringBuffer result = new StringBuffer("<img");
		
	    //必选参数图片路径：src（图片及时加载）或者data_original（图片懒加载）。
		boolean isSrc = args.containsKey("src");
		boolean isDataOriginal = args.containsKey("data_original");

	    if (isSrc) {
	    	result.append(" src=\"").append(args.get("src")).append("\"");
	    } else {
	    	result.append(" src=\"/public/common/imgloading.gif\"");  //图片懒加载
	    }
	    
	    if (isDataOriginal) {
	    	result.append(" data-original=\"").append(args.get("data_original")).append("\"");
	    }

	    result.append(" onerror=\"this.src='/public/common/default.jpg'\"");  //默认图片

	    if (args.containsKey("width")) {  //图片一定要给宽度和高度
	    	 result.append(" width=\"").append(args.get("width")).append("\""); 
	    }
	    
	    if (args.containsKey("height")) {
	    	result.append(" height=\"").append(args.get("height")).append("\""); 
	    }
	    
	    if (args.containsKey("class")) {
	    	result.append(" class=\"").append(args.get("class")).append("\""); 
	    }
	    
	    if (args.containsKey("style")) {
	    	result.append(" style=\"").append(args.get("style")).append("\""); 
	    }
	    
	    if (args.containsKey("alt")) {
	    	result.append(" alt=\"").append(args.get("alt")).append("\"");
	    }
	    
	    result.append("/>");
	    
	    out.print(result.toString());
	}
	
	/**
	 * 敏感信息星号处理 
	 * <br>demo:
	 * <br>用户名：#{asterisk str:user?.name, start:2, end:2, count:4/}
	 * <br>姓名：#{asterisk str:loanUserInfo?.reality_name, start:1, end:0, count:2/}
	 * <br>身份证：#{asterisk str:loanUserInfo?.idNumber, start:6, end:4, count:8/}
	 * <br>手机号：#{asterisk str:loanUserInfo?.mobile, start:3, end:4, count:4/}
	 * <br>银行卡号：#{asterisk str:loanUserInfo?.card, start:6, end:4, count:8/}
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月26日
	 */
	public static void _asterisk(Map<String, Object> params, Closure body, PrintWriter out, 
			ExecutableTemplate template, int fromLine) {
		if (params.get("str") == null) {
			
			return;
		}
		
		String str = params.get("str").toString();
		int length = str.length();
		if (length == 0) {
			
			return;
		}

		StringBuffer result = new StringBuffer();
		
		int start = Convert.strToInt(params.get("start").toString(), 0);
		if (start > 0) {
			if (start <= length ) {
				result.append(str.substring(0, start));
			} else {
				result.append(str.substring(0, length));
			}
		}
		
		int count = Convert.strToInt(params.get("count").toString(), 3);
		for (int i=0; i<count; i++) {
			result.append("*");
		}
		
		int end = Convert.strToInt(params.get("end").toString(), 0);
		if (end > 0) {
			if (end <= length ) {
				result.append(str.substring(length-end, length));
			} else {
				result.append(str.substring(0, length));
			}
		}
		
        out.println(result.toString());  
    } 
	
}
