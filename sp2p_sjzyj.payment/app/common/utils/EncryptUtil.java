package common.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import yb.YbConsts;

import java.io.*;

/**
 *
 * AES加密解密
 *
 * @author : Niu Dong Feng
 * @create : 2017.08.08
 *
 */
public class EncryptUtil {

    private static EncryptUtil instance = null;

    /**
     *  构造方法
     */
    private EncryptUtil() {
    }

    /**
     * 单例模式（懒汉单例）
     *
     * @author : Niu Dong Feng
     * @create : 2017.08.08
     */
    public static EncryptUtil getInstance() {

        return instance == null ? new EncryptUtil() : instance;
    }

    /**
     * 加密
     *
     * @author : Niu Dong Feng
     * @create : 2017.08.08
     */
    public static String encrypt(String data) {
    	
    	BASE64 decoder = new BASE64();
    	byte[] result = null;
    	
    	try {
    		
    		 byte[] keyBytes = new byte[16];
    	        byte[]  ivBytes = new byte[16];

    	        byte[] k = YbConsts.keyStr.getBytes("UTF-8");
    	        byte[] i =  YbConsts.ivStr.getBytes("UTF-8");

    	        int lenK = k.length;
    	        int lenI = i.length;

    	        if (lenK > keyBytes.length) lenK = keyBytes.length;
    	        if (lenI >  ivBytes.length) lenI =  ivBytes.length;

    	        System.arraycopy(k, 0, keyBytes, 0, lenK);//复制数组
    	        System.arraycopy(i, 0,  ivBytes, 0, lenI);

    	        SecretKeySpec  keySpec = new SecretKeySpec(keyBytes, "AES");//根据给定的keyBytes字节数组构造一个用AES算法加密的密钥
    	        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);//设置向量

    	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//创建密码器
    	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);//初始化

    	        result = cipher.doFinal(data.getBytes("UTF8"));//加密
			
		} catch (Exception e) {
			LoggerUtil.error(false, e.getStackTrace() + "\n" + "敏感信息加密失败", data);
		}

       

        return new String(decoder.encode(result));//将加密后的数据转换为字符串
    }

    /**
     * 解密
     *
     * @author : Niu Dong Feng
     * @create : 2017.08.08
     */
    public static String decrypt(String data) {
    	
    	BASE64 decoder = new BASE64();
    	byte[] result = null;
    	String decryptString = "";
    	
    	try {
			
    		 byte[] keyBytes = new byte[16];
    	        byte[]  ivBytes = new byte[16];

    	        byte[] k = YbConsts.keyStr.getBytes("UTF-8");
    	        byte[] i =  YbConsts.ivStr.getBytes("UTF-8");

    	        int lenK = k.length;
    	        int lenI = i.length;

    	        if (lenK > keyBytes.length) lenK = keyBytes.length;
    	        if (lenI >  ivBytes.length) lenI =  ivBytes.length;

    	        System.arraycopy(k, 0, keyBytes, 0, lenK);//复制数组
    	        System.arraycopy(i, 0,  ivBytes, 0, lenI);

    	        SecretKeySpec  keySpec = new SecretKeySpec(keyBytes, "AES");//根据给定的keyBytes字节数组构造一个用AES算法加密的密钥
    	        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);//设置向量

    	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//创建密码器
    	        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);//初始化

    	        result = cipher.doFinal(decoder.decode(data));//解密
    	        
    	        decryptString = new String(result, "UTF-8");//将解密后的数据转换为字符串
    		
		} catch (Exception e) {
			LoggerUtil.error(false, e.getStackTrace() + "\n" + "敏感信息解密失败", data);
		}


        return decryptString;
    }

    /**
     * 文件加密
     *
     * @author : Niu Dong Feng
     * @create : 2017.08.08
     */
    public static void fileEncrypt(String sourceFilePath, String secretFilePath, String sourceFileName) throws Exception {

        FileInputStream   fis = new FileInputStream(new File(sourceFilePath + sourceFileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        StringBuilder strBud = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            strBud.append(temp).append("\n");
        }
        String plain = strBud.toString();

        byte[] keyBytes = new byte[16];
        byte[]  ivBytes = new byte[16];

        byte[] k = YbConsts.keyStr.getBytes("UTF-8");
        byte[] i =  YbConsts.ivStr.getBytes("UTF-8");

        int lenK = k.length;
        int lenI = i.length;

        if (lenK > keyBytes.length) lenK = keyBytes.length;
        if (lenI >  ivBytes.length) lenI =  ivBytes.length;

        System.arraycopy(k, 0, keyBytes, 0, lenK);//复制数组
        System.arraycopy(i, 0,  ivBytes, 0, lenI);

        SecretKeySpec  keySpec = new SecretKeySpec(keyBytes, "AES");//根据给定的keyBytes字节数组构造一个用AES算法加密的密钥
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);//设置向量

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);//初始化

        BASE64 decoder = new BASE64();
        byte[] result = cipher.doFinal(plain.getBytes("UTF8"));
        String files = decoder.encode(result);

        FileOutputStream fos = new FileOutputStream(new File(secretFilePath + sourceFileName));
        fos.write(files.getBytes("UTF-8"));
        fos.flush();
        fos.close();

        System.out.println("文件："+secretFilePath+sourceFileName+"加密成功！");

    }

    /**
     * 文件解密
     *
     * @author : Niu Dong Feng
     * @create : 2017.08.08
     */
    public static void fileDecrypt(String sourceFilePath, String secretFilePath, String sourceFileName) throws Exception {

        FileInputStream   fis = new FileInputStream(new File(sourceFilePath + sourceFileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        StringBuilder strBud = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            strBud.append(temp).append("\n");
        }
        String plain = strBud.toString();

        byte[] keyBytes = new byte[16];
        byte[]  ivBytes = new byte[16];

        byte[] k = YbConsts.keyStr.getBytes("UTF-8");
        byte[] i =  YbConsts.ivStr.getBytes("UTF-8");

        int lenK = k.length;
        int lenI = i.length;

        if (lenK > keyBytes.length) lenK = keyBytes.length;
        if (lenI >  ivBytes.length) lenI =  ivBytes.length;

        System.arraycopy(k, 0, keyBytes, 0, lenK);//复制数组
        System.arraycopy(i, 0,  ivBytes, 0, lenI);

        SecretKeySpec  keySpec = new SecretKeySpec(keyBytes, "AES");//根据给定的keyBytes字节数组构造一个用AES算法加密的密钥
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);//设置向量

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//创建密码器
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);//初始化

        BASE64 decoder = new BASE64();
        byte[] result = cipher.doFinal(decoder.decode(plain));

        String files = new String(result, "UTF-8");

        FileOutputStream fos = new FileOutputStream(new File(secretFilePath + sourceFileName));
        fos.write(files.getBytes("UTF-8"));
        fos.flush();
        fos.close();

        System.out.println("文件："+secretFilePath+sourceFileName+"解密成功！");
    }

}
