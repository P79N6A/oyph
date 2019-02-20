package common.utils.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.w3c.dom.ElementTraversal;

import play.Play;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import common.utils.DateUtil;

/**
 * HTML导出成PDF工具类
 *
 * @description 本类提供了带水印和不带水印两种导出方式。<br>
 * 	三个静态变量中对应的文件需要按照对应的位置放置,其中simsun.ttc是宋体(导出工具对中文字体支持不是特别好)
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月23日
 */
public class PDFUtil {
	
	/** 日期格式:yyyyMMddHHmmss */
	public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

	/** 默认水印位置 */
	public static final String WATERMARKIMAGEPATH = Play.getFile("public"+File.separator+"pdf"+File.separator+"shuiyin.png").getAbsolutePath();
	
	/** 字体文件路径 */
	private static final String FONT_SIMSUM = Play.getFile("public"+File.separator+"pdf"+File.separator+"SIMSUN.TTC").getAbsolutePath();
	
	/** 文件缓存路径 */
	private static final String PATH = Play.getFile("tmp").getAbsolutePath();
	
	/**
	 * 导出HTML格式的PDF(不带水印)
	 *
	 * @param content HTMLbody的内容，包括完整的标签信息[flying saucer是不支持TD之类大写的标签的，注意全部标签使用小写]
	 * @param csses HTML文件中的CSS文件路径(例如/public/stylesheets/reset.css,建议全部放置到该文件夹中)，不要在content中嵌入stylesheets定义，如果需要写到单独CSS文件中，然后载入
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月23日
	 */
	public static File exportHTMLPdf(String content,String[] csses) throws DocumentException, IOException{
		Random random = new Random();
		Date now = new Date();
		File outputPDFfile = null;
		long startTime = System.currentTimeMillis(); // 获取开始时间

		String tempFile =  DateUtil.dateToString(now, yyyyMMddHHmmss + random.nextInt(1000)) + ".pdf";

		// 最终生成的PDF文件的会写入到这个流中
		OutputStream os = new FileOutputStream(PATH+File.separator+tempFile);

		ITextRenderer iTextRenderer = new ITextRenderer();
		ITextFontResolver iTextFontResolver = iTextRenderer.getFontResolver();

		// 这个是宋体的字体文件(SIMSUN.TTC)，这个是必须的(也可以载入其他的中文字体，但是这里载入的字体，必须在CSS定义中
		// 添加：body {font-family: SimSun;}，否则PDF将无法支持中文字体，这里在zhongwen.css文件中定义了)
		// 这里的"simsun.ttc"是大小写敏感的
		iTextFontResolver.addFont(FONT_SIMSUM, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

		StringBuffer html = new StringBuffer();

		// DOCTYPE 必需写，且需要符合W3C规范
		html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		html.append("<head>");
		html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");

		if (csses != null && csses.length > 0) {
			for (String stylesheet : csses) {
				html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + stylesheet + "\" />");
			}
		}
		
		// 这里添加中文字体的支持，注意和上面的对应
		
		html.append("<style type=\"text/css\" mce_bogus=\"1\">@charset \"utf-8\";body {font-family: SimSun;} @page { size: 8.5in 11in; @bottom-left {content: \"Eims focus on cloud computing\";}; @bottom-right {content: \"page \" counter(page) \" of  \" counter(pages);};}</style>");

		html.append("</head>").append("<body>");

		//flying saucer不支持一些大写的标签，故需要将部分大写标签转换成小写标签

		html.append(content);
		html.append("</body></html>");

		long endTime = System.currentTimeMillis(); // 获取结束时间

		System.err.println("PDF导出程序运行时间： " + (endTime - startTime) + "ms-----------");

		// 解析html生成pdf
		iTextRenderer.setDocumentFromString(html.toString());
		iTextRenderer.layout();
		iTextRenderer.createPDF(os);
		os.close();

		// 将生成的pdf读取成输出流，供客户端下载。
		outputPDFfile = new File(PATH+File.separator+tempFile); // 需要copy

		endTime = System.currentTimeMillis(); // 获取结束时间

		System.err.println("PDF导出程序运行时间： " + (endTime - startTime)+"ms------------");

		return outputPDFfile;
	}
	
	/**
	 * 导出HTML格式的PDF(带水印)
	 *
	 * @param content HTMLbody的内容，包括完整的标签信息[flying saucer是不支持TD之类大写的标签的，注意全部标签使用小写]
	 * @param csses HTML文件中的CSS文件路径(例如/public/stylesheets/reset.css,建议全部放置到该文件夹中)，不要在content中嵌入stylesheets定义，如果需要写到单独CSS文件中，然后载入
	 * @param watermark 水印生成方案(如果该值为空着使用默认的水印方案)
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月23日
	 */
	public static File exportHTMLPdfWithWatermark(String content,String[] csses,PdfWatermark watermark) throws DocumentException, IOException{
		Random random = new Random();
		Date now = new Date();
		String outputPDF = DateUtil.dateToString(now, yyyyMMddHHmmss+random.nextInt(1000))+".pdf";
		File outputPDFfile = null;
		
		long startTime = System.currentTimeMillis(); // 获取开始时间  
		
        String tempFile = DateUtil.dateToString(now, yyyyMMddHHmmss+random.nextInt(1000))+".pdf";
        
      //最终生成的PDF文件的会写入到这个流中
        OutputStream os = new FileOutputStream(PATH+"/"+tempFile);
        
        ITextRenderer iTextRenderer = new ITextRenderer();
        ITextFontResolver iTextFontResolver = iTextRenderer.getFontResolver();  
        
        
        //这个是宋体的字体文件(SIMSUN.TTC)，这个是必须的(也可以载入其他的中文字体，但是这里载入的字体，必须在CSS定义中
        //添加：body {font-family: SimSun;}，否则PDF将无法支持中文字体，这里在zhongwen.css文件中定义了)
        //这里的"simsun.ttc"是大小写敏感的

        iTextFontResolver.addFont(FONT_SIMSUM, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        
        StringBuffer html = new StringBuffer();  
        
        // DOCTYPE 必需写，且需要符合W3C规范
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");  
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        html.append("<head>");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        
        //添加CSS
        if(csses != null && csses.length>0){
        	for(String stylesheet:csses){
        		html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+stylesheet+"\" />");
        	}
        }
        //这里添加中文字体的支持，注意和上面的对应
        
        html.append("<style type=\"text/css\" mce_bogus=\"1\">@charset \"utf-8\";body {font-family: SimSun;} @page { size: 8.5in 11in; @bottom-left {content: \"Eims focus on cloud computing\";}; @bottom-right {content: \"page \" counter(page) \" of  \" counter(pages);};}</style>");

        html.append("</head>").append("<body>");  
        
        //flying saucer不支持一些大写的标签，故需要将部分大写标签转换成小写标签
        
        html.append(content);  
        html.append("</body></html>");  
  
        long endTime = System.currentTimeMillis(); // 获取结束时间  
  
        System.err.println("程序运行时间： " + (endTime - startTime) + "ms-----------");  
       
        // 解析html生成pdf  
        iTextRenderer.setDocumentFromString(html.toString());  
        iTextRenderer.layout();  
        iTextRenderer.createPDF(os);  
        os.close();  

		if(watermark == null){
			watermark = new DefaultPdfWatermark();
		}
		outputPDFfile = watermark.addPdfMark(PATH+File.separator+tempFile,PATH+File.separator+outputPDF);

        endTime = System.currentTimeMillis(); // 获取结束时间  
  
        System.err.println("程序运行时间： " + (endTime - startTime) + "ms------------");

        File temp = new File(PATH+File.separator+tempFile);
        if(temp.exists()){
        	temp.deleteOnExit();
        }
        
        return outputPDFfile;
	}

}
