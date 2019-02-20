package common.utils.captcha;

import java.awt.Color;
import java.util.Random;

import play.libs.Images;

public class CaptchaImages extends  Images {
	/**
	 * Create a captche image
	 */
	public static CaptchaExt captcha(int width, int height) {
		return new CaptchaExt(width, height);
	}

	/**
	 * Create a 150x150 captcha image
	 */
	public static CaptchaExt captcha() {
		return captcha(150, 50);
	}

	static class CaptchaExt extends Captcha {
		
		public static String nums = "123456789";
		public static String operators = "+-";

		public CaptchaExt(int w, int h) {
			super(w, h);
		}

		/**
		 * Tell the captche to draw a text of the specified size and retrieve it
		 */
		public String getText(int length) {
			return getText(length, nums);
		}

		/**
		 * Tell the captche to draw a text of the specified size using the
		 * specified color (ex. #000000) and retrieve it
		 */
		public String getText(String color, int length) {

			this.textColor = Color.decode(color);
			return getText(length);
		}

		/* (non-Javadoc)
		 * @see play.libs.Images.Captcha#getText(int, java.lang.String)
		 */
		public String getText(int length, String chars) {
			char[] charsNumArray = chars.toCharArray();
            int num1 = Integer.parseInt(randomNum(1, charsNumArray));
            int num2 = Integer.parseInt(randomNum(1, charsNumArray));
            
            char[] charsSignArray = operators.toCharArray();
            String sign = randomNum(1, charsSignArray);
            if(sign.equals("-") && num1 < num2)
            	text = num2 + sign + num1;
            else 
            	text = num1 + sign + num2;
            text = text + "=?";
            
            if(sign.equals("×"))
            	return String.valueOf(num1 * num2);
            else if(sign.equals("+"))
            	return String.valueOf(num1 + num2);
            else if(sign.equals("-"))
            	return String.valueOf(Math.abs(num1 - num2));
            return "";
		}

		/**
		 * 抽取一位随机数
		 * 
		 * @param length
		 * @param charsArray
		 * @return
		 */
		private String randomNum(int length, char[] charsArray) {
			char cha = charsArray[new Random().nextInt(charsArray.length)];
			return String.valueOf(cha);
		}

		/* (non-Javadoc)
		 * @see play.libs.Images.Captcha#getText(java.lang.String, int, java.lang.String)
		 */
		public String getText(String color, int length, String chars) {
			this.textColor = Color.decode(color);
			return getText(length, chars);
		}
	}
}
