package common.utils.pdf;

import java.io.File;
import java.io.IOException;

import com.itextpdf.text.DocumentException;


/**
 * PDF水印抽象类
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月23日
 */
public abstract class PdfWatermark {
	
	/** 水印图片路径 */
	protected String watermarkImagePath;
	
	/**
	 * 设置水印图片的路径:watermarkImagePath
	 * @param watermarkImagePath
	 */
	protected PdfWatermark(String watermarkImagePath){
		this.watermarkImagePath = watermarkImagePath;
	}

	/**
	 * 给InPdfFile文件添加水印，然后导出到outPdfFile的PDF格式文件中
	 * @param InPdfFile
	 * @param outPdfFile
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public abstract File addPdfMark(String InPdfFile, String outPdfFile) throws IOException, DocumentException;
}
