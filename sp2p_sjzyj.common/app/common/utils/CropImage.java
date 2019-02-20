package common.utils;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import play.Play;
import play.db.jpa.Blob;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import common.utils.file.FileUtil;
import common.utils.number.Arith;

/**
 * 图片裁剪
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月28日
 */
public class CropImage {
	
	/** 裁剪起点x坐标 */  
    private int x;  

    /** 裁剪起点y坐标 */
    private int y;  
  
    /** 裁剪宽 */
    private int w;
    
    /** 裁剪高 */
    private int h;
    
	public CropImage(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	/**
	 * 图片裁剪
	 *
	 * @param oldImageSrc
	 * @param newImageSrc
	 * @param imageType
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月9日
	 */
	public ResultInfo crop(String oldImageSrc, String imageType, boolean createNewImg) {
		ResultInfo result = new ResultInfo();
		
		FileInputStream fis = null;
		ImageInputStream iis = null;
		
		Map<String, Object> fileInfo = null;
		String newFileName = null;//裁剪生成的新文件名
		String newImageSrc = oldImageSrc;//裁剪后新文件路径
		if(createNewImg) {//是否生成新图片
			Blob blob = new Blob();
			newFileName = UUID.randomUUID().toString();
			newImageSrc = blob.getStore()+File.separator+newFileName;
		}
		try{
			
			fis = new FileInputStream(oldImageSrc);
			iis = ImageIO.createImageInputStream(fis);
			
			String mime = FileUtil.getMIME(new File(oldImageSrc));
			Iterator<ImageReader> iterator = ImageIO.getImageReadersByMIMEType(mime);//返回包含所有当前已注册ImageReader的Iterator，这些ImageReader声称能够解码指定格式。
			ImageReader reader = iterator.next();
			reader.setInput(iis, true);
			
			ImageReadParam param = reader.getDefaultReadParam();//返回一个适合此格式的默认ImageReadParam对象。
			Rectangle rect = new Rectangle(x, y, w, h);
			param.setSourceRegion(rect);//设置区域
			BufferedImage bi = reader.read(0, param);
			File newFile = new File(newImageSrc);
			ImageIO.write(bi, imageType, newFile);//写新图片
			
			if(createNewImg) {//是否生成新图片
				fileInfo = new HashMap<String, Object>();
				/* 裁剪后的大小 */
				fileInfo.put("size", Arith.div(newFile.length(), 1024, 2));
				/* 获取图片分辨率 */
				Image src = javax.imageio.ImageIO.read(newFile);
				/* 图片高度 */
				fileInfo.put("height", src.getHeight(null));
				/* 图片宽度 */
				fileInfo.put("width", src.getWidth(null));
				/* 图片尺寸 */
				fileInfo.put("imageResolution", src.getWidth(null)+"*"+src.getHeight(null));
				/* 以静态资源保存的文件名 */
				fileInfo.put("staticFileName", "/"+Play.configuration.getProperty("attachments.path", "data/attachments")+"/"+newFileName);//
			}
		}catch(IOException e){
			result.code = -5;
			LoggerUtil.error(false, e, "图片裁剪失败!");
			return result;
		}finally{
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					result.code = -5;
					e.printStackTrace();
					
					return result;
				}
			}
			if (iis != null) {
				try {
					iis.close();
				} catch (IOException e) {
					result.code = -5;
					e.printStackTrace();
					
					return result;
				}
			}
		}	
		
		result.code = 1;
		result.msg = "裁剪成功";
		result.obj = fileInfo;
		
		return result;
	}
	
	
}
