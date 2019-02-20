package common.utils.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * 默认的水印生成方案类
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月23日
 */
public class DefaultPdfWatermark extends PdfWatermark{



	/** 默认的水印生成策略: */
	public DefaultPdfWatermark(){
		super(PDFUtil.WATERMARKIMAGEPATH);
	}

	public DefaultPdfWatermark(String watermarkImagePath) {
		super(watermarkImagePath);
	}
	
	@Override
	public File addPdfMark(String InPdfFile, String outPdfFile) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(InPdfFile, "PDF".getBytes());
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));

		Image img = Image.getInstance(watermarkImagePath);// 插入水印
		img.setAbsolutePosition(180, 350);
		Image img2 = Image.getInstance(watermarkImagePath);// 插入水印
		img2.setAbsolutePosition(30, 100);
		Image img3 = Image.getInstance(watermarkImagePath);// 插入水印
		img3.setAbsolutePosition(300, 600);
	
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			PdfContentByte under = stamp.getUnderContent(i);
			under.addImage(img);
			under.addImage(img2);
			under.addImage(img3);
		}
		
		stamp.close();//关闭的顺序不能乱
		reader.close();

		File tempfile = new File(InPdfFile);
		if (tempfile.exists()) {
			tempfile.deleteOnExit();
		}
		
		return new File(outPdfFile);
	}
	
	
}
