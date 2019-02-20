package common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class WxUploadImage {
	private final static Logger logger = Logger.getLogger("weixinupload");  
    
    /* 
     * 调微信接口上传永久图片 
     */  
    public static String postFile(String url, File imgFile) throws IOException {    
        File file = imgFile;    
        if (!file.exists()) {    
             throw new IOException("文件不存在");    
         }    
        String result = null;    
        try {    
            URL url1 = new URL(url);   
            // 连接    
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();    
            /**  
             * 设置关键值  
             */   
            conn.setConnectTimeout(5000);    
            conn.setReadTimeout(30000);    
            conn.setDoOutput(true);    
            conn.setDoInput(true);    
            conn.setUseCaches(false);  // post方式不能使用缓存  
            conn.setRequestMethod("POST");  // 以Post方式提交表单，默认get方式    
            // 设置请求头信息    
            conn.setRequestProperty("Connection", "Keep-Alive");    
            conn.setRequestProperty("Cache-Control", "no-cache");    
            // 设置边界   
            String boundary = "-----------------------------" + System.currentTimeMillis();    
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);    
            // 请求正文信息    
            OutputStream output = conn.getOutputStream();    
            output.write(("--" + boundary + "\r\n").getBytes());    
            output.write(    
                    String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", file.getName())    
                            .getBytes());    
            output.write("Content-Type: image/jpeg \r\n\r\n".getBytes());    
            byte[] data = new byte[1024];    
            int len = 0;    
            FileInputStream input = new FileInputStream(file);    
            while ((len = input.read(data)) > -1) {    
                output.write(data, 0, len);    
            }    
            output.write(("\r\n--" + boundary + "\r\n\r\n").getBytes());    
            output.flush();    
            output.close();    
            input.close();    
            InputStream resp = conn.getInputStream();    
            StringBuffer sb = new StringBuffer();    
            while ((len = resp.read(data)) > -1)    
                sb.append(new String(data, 0, len, "utf-8"));    
            resp.close();    
            result = sb.toString();    
            //System.out.println(result);    
        } catch (ClientProtocolException e) {    
            logger.error("postFile，不支持http协议", e);  
        } catch (IOException e) {    
            logger.error("postFile数据传输失败", e);  
        }    
         
        //将字符串转换成jsonObject对象  
        JSONObject jsonObject = JSONObject.fromObject(result);  
        String media_id = jsonObject.getString("media_id");  
        return media_id;    
    }
}
