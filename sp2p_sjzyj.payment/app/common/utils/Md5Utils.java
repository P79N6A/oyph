package common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.type.descriptor.java.DataHelper;


public class Md5Utils {
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
              "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
  
     // return Hexadecimal
     private static String byteToArrayString(byte bByte) {
         int iRet = bByte;
         if (iRet < 0) {
             iRet += 256;
         }
         int iD1 = iRet / 16;
         int iD2 = iRet % 16;
         return strDigits[iD1] + strDigits[iD2];
    }
 
    // 转换字节数组为16进制字串
     private static String byteToString(byte[] bByte) {
         StringBuffer sBuffer = new StringBuffer();
         for (int i = 0; i < bByte.length; i++) {
             sBuffer.append(byteToArrayString(bByte[i]));
         }
         return sBuffer.toString().toUpperCase();
     }
 
     public static String GetMD5Code(String strObj) {
         String resultString = null;
         try {
             resultString = new String(strObj);
             MessageDigest md = MessageDigest.getInstance("MD5");
             // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
         } catch (NoSuchAlgorithmException ex) {
             ex.printStackTrace();
         }
         return resultString;
     }
     
     /**
     * 获取MD5加密
     * 
     * @param pwd 需要加密的字符串
     * @return String 字符串 加密后的字符串
     */
     public static String MD5(String pwd) {
    	 try {
    		 // 创建加密对象
    		 MessageDigest digest = MessageDigest.getInstance("md5");
 
    		 // 调用加密对象的方法，加密的动作已经完成
    		 byte[] bs = digest.digest(pwd.getBytes());
    		 // 接下来，我们要对加密后的结果，进行优化，按照mysql的优化思路走
    		 // mysql的优化思路：
    		 // 第一步，将数据全部转换成正数：
    		 String hexString = "";
    		 for (byte b : bs) {
    			 // 第一步，将数据全部转换成正数：
    			 // 解释：为什么采用b&255
    			 /*
    			  * b:它本来是一个byte类型的数据(1个字节) 255：是一个int类型的数据(4个字节)
    			  * byte类型的数据与int类型的数据进行运算，会自动类型提升为int类型 eg: b: 1001 1100(原始数据)
    			  * 运算时： b: 0000 0000 0000 0000 0000 0000 1001 1100 255: 0000
    			  * 0000 0000 0000 0000 0000 1111 1111 结果：0000 0000 0000 0000
    			  * 0000 0000 1001 1100 此时的temp是一个int类型的整数
    			  */
    			 int temp = b & 255;
    			 // 第二步，将所有的数据转换成16进制的形式
    			 // 注意：转换的时候注意if正数>=0&&<16，那么如果使用Integer.toHexString()，可能会造成缺少位数
    			 // 因此，需要对temp进行判断
    			 if (temp < 16 && temp >= 0) {
    				 // 手动补上一个“0”
    				 hexString = hexString + "0" + Integer.toHexString(temp);
    			 } else {
    				 hexString = hexString + Integer.toHexString(temp);
    			 }
    		 }
             return hexString;
          } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
          }
          return "";
      }
     
     //可逆加密
     public static String KL(String inStr) {


 		char[] a = inStr.toCharArray();

 		for (int i = 0; i < a.length; i++) {

 		a[i] = (char) (a[i] ^ 't');

 		}

 		String s = new String(a);

 		return s;

 	}
 	
     //解密
 	public static String JM(String inStr) {

 		char[] a = inStr.toCharArray();

 		for (int i = 0; i < a.length; i++) {

 		a[i] = (char) (a[i] ^ 't');

 		}

 		String k = new String(a);

 		return k;

 	}
}
