package common.utils.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class FileBase64ConvertUitl {
	/**  
	  * 将文件转成base64 字符串  
	  * @param path文件路径  
	  * @return     
	  * @throws Exception  
	  */  
	  
	 public static String encodeBase64File(String path) throws Exception {  
	   File file = new File(path);  
	   FileInputStream inputFile = new FileInputStream(file);  
	   byte[] buffer = new byte[(int) file.length()];  
	   inputFile.read(buffer);  
	   inputFile.close();  
	   return new BASE64Encoder().encode(buffer);  
	  
	 }  
	  
	 /**  
	  * 将base64字符解码保存文件  
	  * @param base64Code  
	  * @param targetPath  
	  * @throws Exception  
	  */  
	  
	 public static void decoderBase64File(String base64Code, String targetPath)  
	    throws Exception {  
	   byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);  
	   FileOutputStream out = new FileOutputStream(targetPath);  
	   out.write(buffer);  
	   out.close();  
	  
	 }  
	  
	 /**  
	  * 将base64字符保存文本文件  
	  * @param base64Code  
	  * @param targetPath  
	  * @throws Exception  
	  */  
	 public static File toFile(String base64Code, String fileName)throws Exception {  
	    
	    File file = null;
        //创建文件目录
        String filePath="D:\\image";
        File  dir=new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.decodeBase64(base64Code);
            file=new File(filePath+"\\"+fileName);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	    
	    
	    return file;
	  }
	 
	 public static String getURLImage(String imageUrl) throws Exception {  
        //new一个URL对象  
        URL url = new URL(imageUrl);  
        //打开链接  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        //设置请求方式为"GET"  
        conn.setRequestMethod("GET");  
        //超时响应时间为5秒  
        conn.setConnectTimeout(5 * 1000);  
        //通过输入流获取图片数据  
        InputStream inStream = conn.getInputStream();  
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        byte[] data = readInputStream(inStream);  
        BASE64Encoder encode = new BASE64Encoder();  
        String s = encode.encode(data);  
        return s;  
    }

	private static byte[] readInputStream(InputStream inStream) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getImgStr(String imgFile){
		//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		
		InputStream in = null;
		byte[] data = null;
		//读取图片字节数组
		try {
		    in = new FileInputStream(imgFile);        
		    data = new byte[in.available()];
		    in.read(data);
		    in.close();
		} 
		catch (IOException e) {
		    e.printStackTrace();
		}
		return new String(Base64.encodeBase64(data));
	}
}
