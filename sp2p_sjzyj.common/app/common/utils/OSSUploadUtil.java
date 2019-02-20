package common.utils;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;  
import java.util.UUID;  
  
import com.aliyun.oss.ClientException;  
import com.aliyun.oss.OSSClient;  
import com.aliyun.oss.OSSException;  
import com.aliyun.oss.model.DeleteObjectsRequest;  
import com.aliyun.oss.model.DeleteObjectsResult;  
import com.aliyun.oss.model.GenericRequest;  
import com.aliyun.oss.model.ObjectMetadata;  
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.UploadFileRequest;

import org.jdom.input.JDOMParseException;

import common.constants.AliyunConsts;  

public class OSSUploadUtil {

	private static AliyunConsts config = null;  
	  
    /** 
     *  
     * @MethodName: uploadFile 
     * @Description: OSS单文件上传 
     * @param file   
     * @param fileType 文件后缀 
     * @return String 文件地址 
     */  
    public static String uploadFile(File file,String fileType){  
        config = config==null?new AliyunConsts():config;  
        String fileName = config.PICLOCATION+UUID.randomUUID().toString().toUpperCase().replace("-", "")+"."+fileType; //文件名，根据UUID来  
        return putObject(file,fileType,fileName);  
    }  
      
    /** 
     *  
     * @MethodName: updateFile 
     * @Description: 更新文件:只更新内容，不更新文件名和文件地址。 
     *      (因为地址没变，可能存在浏览器原数据缓存，不能及时加载新数据，例如图片更新，请注意) 
     * @param file 
     * @param fileType 
     * @param oldUrl 
     * @return String 
     */  
    /*public static String updateFile(File file,String fileType,String oldUrl){  
        String fileName = getFileName(oldUrl);  
        if(fileName==null) return null;  
        return putObject(file,fileType,fileName);  
    } */ 
      
    /** 
     *  
     * @MethodName: replaceFile 
     * @Description: 替换文件:删除原文件并上传新文件，文件名和地址同时替换 
     *      解决原数据缓存问题，只要更新了地址，就能重新加载数据) 
     * @param file 
     * @param fileType 文件后缀 
     * @param oldUrl 需要删除的文件地址 
     * @return String 文件地址 
     */  
    public static String replaceFile(File file, String newFileType, String oldUrl, String oldFileType){  
        boolean flag = deleteFile(oldUrl, oldFileType);      //先删除原文件  
        if(!flag){  
            //更改文件的过期时间，让他到期自动删除。  
        }  
        return uploadFile(file, newFileType);  
    }  
      
    /** 
     *  
     * @MethodName: deleteFile 
     * @Description: 单文件删除 
     * @param fileUrl 需要删除的文件url 
     * @return boolean 是否删除成功 
     */  
    public static boolean deleteFile(String fileUrl, String fileType){  
        config = config==null?new AliyunConsts():config;  
          
        String bucketName = OSSUploadUtil.getBucketName(fileUrl);       //根据url获取bucketName  
        String fileName = OSSUploadUtil.getFileName(fileUrl, fileType);           //根据url获取fileName  
        if(bucketName==null||fileName==null) return false;  
        OSSClient ossClient = null;   
        try {  
            ossClient = new OSSClient(config.ENDPOINT, config.ACCESSKEYID, config.ACCESSKEYSECRET);   
            GenericRequest request = new DeleteObjectsRequest(bucketName).withKey(fileName);  
            ossClient.deleteObject(request);  
        } catch (Exception oe) {  
            oe.printStackTrace();  
            return false;  
        } finally {  
            ossClient.shutdown();  
        }  
        return true;  
    }  
      
    /** 
     *  
     * @MethodName: batchDeleteFiles 
     * @Description: 批量文件删除(较快)：适用于相同endPoint和BucketName 
     * @param fileUrls 需要删除的文件url集合 
     * @return int 成功删除的个数 
     */  
    public static int deleteFile(List<String> fileUrls){  
        int deleteCount = 0;    //成功删除的个数  
        String bucketName = OSSUploadUtil.getBucketName(fileUrls.get(0));       //根据url获取bucketName  
        List<String> fileNames = OSSUploadUtil.getFileName(fileUrls);         //根据url获取fileName  
        if(bucketName==null||fileNames.size()<=0) return 0;  
        OSSClient ossClient = null;   
        try {  
            ossClient = new OSSClient(config.ENDPOINT, config.ACCESSKEYID, config.ACCESSKEYSECRET);   
            DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(fileNames);  
            DeleteObjectsResult result = ossClient.deleteObjects(request);  
            deleteCount = result.getDeletedObjects().size();  
        } catch (OSSException oe) {  
            oe.printStackTrace();  
            throw new RuntimeException("OSS服务异常:", oe);  
        } catch (ClientException ce) {  
            ce.printStackTrace();  
            throw new RuntimeException("OSS客户端异常:", ce);  
        } finally {  
            ossClient.shutdown();  
        }  
        return deleteCount;  
          
    }  
      
    /** 
     *  
     * @MethodName: batchDeleteFiles 
     * @Description: 批量文件删除(较慢)：适用于不同endPoint和BucketName 
     * @param fileUrls 需要删除的文件url集合 
     * @return int 成功删除的个数 
     */  
   /* public static int deleteFiles(List<String> fileUrls){  
        int count = 0;  
        for (String url : fileUrls) {  
            if(deleteFile(url)){  
                count++;  
            }  
        }  
        return count;  
    } */ 
      
    /** 
     *  
     * @MethodName: putObject 
     * @Description: 上传文件 
     * @param file 
     * @param fileType 
     * @param fileName 
     * @return String 
     */  
    public static String putObject(File file,String fileType,String fileName){  
        config = config==null?new AliyunConsts():config;  
        OSSClient ossClient = null; 
        URL urls = null;
        StringBuffer url = new StringBuffer("https://");
        String transfer = null;
        try {  
            ossClient = new OSSClient(config.ENDPOINT, config.ACCESSKEYID, config.ACCESSKEYSECRET); 
            
	         // 设置断点续传请求
	         UploadFileRequest uploadFileRequest = new UploadFileRequest(config.BUCKETNAME, fileName);
	         // 指定上传的本地文件
	         uploadFileRequest.setUploadFile(file.toString());
	         // 指定上传并发线程数
	         uploadFileRequest.setTaskNum(5);
	         // 指定上传的分片大小
	         uploadFileRequest.setPartSize(1 * 1024 * 1024);
	         // 开启断点续传
	         uploadFileRequest.setEnableCheckpoint(true);
	         // 断点续传上传
	         try {
				ossClient.uploadFile(uploadFileRequest);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null; 
			}

            
            
            
            /*InputStream input = new FileInputStream(file);    
            ObjectMetadata meta = new ObjectMetadata();             // 创建上传Object的Metadata  
            meta.setContentType(OSSUploadUtil.contentType(fileType));       // 设置上传内容类型  
            meta.setCacheControl("no-cache");                   // 被下载时网页的缓存行为    
            PutObjectRequest request = new PutObjectRequest(config.BUCKETNAME, fileName,input,meta);  
            ossClient.putObject(request); */
            
            Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 20);
            // 生成URL
            urls = ossClient.generatePresignedUrl(config.BUCKETNAME, fileName, expiration);
            
        } catch (OSSException oe) {  
            oe.printStackTrace();  
            return null;  
        } catch (ClientException ce) {  
            ce.printStackTrace();  
            return null;  
        } /*catch (FileNotFoundException e) {  
            e.printStackTrace();  
            return null;  
        }*/ finally {  
            ossClient.shutdown();  
        }  
        
        transfer = urls.toString();
        int beginIndex = transfer.indexOf(url.toString());
        String zhi = transfer.substring(beginIndex+url.toString().length());
        
        return url.append(zhi).toString();  
    }  
      
    /** 
     *  
     * @MethodName: contentType 
     * @Description: 获取文件类型 
     * @param FileType 
     * @return String 
     */  
    private static String contentType(String fileType){  
        fileType = fileType.toLowerCase();  
        String contentType = "";  
        switch (fileType) {  
        case "bmp": contentType = "image/bmp";  
                    break;  
        case "gif": contentType = "image/gif";  
                    break;  
        case "png":   
        case "jpeg":      
        case "jpg": contentType = "image/jpeg";  
                    break;  
        case "html":contentType = "text/html";  
                    break;  
        case "txt": contentType = "text/plain";  
                    break;  
        case "vsd": contentType = "application/vnd.visio";  
                    break;  
        case "ppt":   
        case "pptx":contentType = "application/vnd.ms-powerpoint";  
                    break;  
        case "doc":   
        case "docx":contentType = "application/msword";  
                    break;  
        case "xml":contentType = "text/xml";  
                    break;  
        case "mp4":contentType = "video/mp4";  
                    break;  
        default: contentType = "application/octet-stream";  
                    break;  
        }  
        return contentType;  
     }    
      
    /** 
     *  
     * @MethodName: getBucketName 
     * @Description: 根据url获取bucketName 
     * @param fileUrl 文件url 
     * @return String bucketName 
     */  
    private static String getBucketName(String fileUrl){  
        String http = "http://";  
        String https = "https://";  
        int httpIndex = fileUrl.indexOf(http);  
        int httpsIndex = fileUrl.indexOf(https);  
        int startIndex  = 0;  
        if(httpIndex==-1){  
            if(httpsIndex==-1){  
                return null;  
            }else{  
                startIndex = httpsIndex+https.length();  
            }  
        }else{  
            startIndex = httpIndex+http.length();  
        }  
        int endIndex = fileUrl.indexOf(".oss-");   
        return fileUrl.substring(startIndex, endIndex);  
    }  
      
    /** 
     *  
     * @MethodName: getFileName 
     * @Description: 根据url获取fileName 
     * @param fileUrl 文件url 
     * @param fileType 文件后缀 
     * @return String fileName 
     */  
    private static String getFileName(String fileUrl, String fileType){  
        String str = "aliyuncs.com/";  
        int beginIndex = fileUrl.indexOf(str); 
        
        if(fileType == null) return null;
        
        int endIndex = fileUrl.indexOf("."+fileType);
        if(endIndex==-1) return null;
        if(beginIndex==-1) return null;  
        
        if(fileUrl.length()<=100) {
        	return fileUrl.substring(beginIndex+str.length());
        }else {
        	return fileUrl.substring(beginIndex+str.length(), endIndex+1+fileType.length());
        }
    }  
      
    /** 
     *  
     * @MethodName: getFileName 
     * @Description: 根据url获取fileNames集合 
     * @param fileUrl 文件url 
     * @return List<String>  fileName集合 
     */  
    private static List<String> getFileName(List<String> fileUrls){  
        List<String> names = new ArrayList<>();  
        for (String url : fileUrls) {  
            //names.add(getFileName(url));  
        }  
        return names;  
    }
}
