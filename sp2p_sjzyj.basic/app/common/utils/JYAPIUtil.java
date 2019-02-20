package common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import play.Logger;

/**
 * Created by li4236 on 2017/11/21.
 * Authorization 工具类 可以直接使用
 */

public class JYAPIUtil {

	private static String appId = "e14987773b8848d748881718df5361fa";
	private static String appKey = "03741585e5d2be0f5990b151319a22ec";

    public static String Authorization(HashMap<String, Object> params) {

        String timestamp = getCanonicalTime();

        String canonicalRequest = JYAPIUtil.CanonicalRequest(params);


        String signingKey = JYAPIUtil.SigningKey(appKey, timestamp);


        String signature = JYAPIUtil.Signature(signingKey, canonicalRequest);



        StringBuilder result = new StringBuilder();

        result.append(appId).append("/").append(timestamp).append("/").append(signature);
        Logger.info("【API鉴权认证】:%s"+result.toString());

        return result.toString();

    }


    public static String CanonicalRequest(HashMap<String, Object> map) {

        StringBuilder result = new StringBuilder();

        ArrayList<String> sort = new ArrayList<String>();

        Object[] key_arr = map.keySet().toArray();
        Arrays.sort(key_arr);
        for (Object key : key_arr) {

           String temp = key + "=" + map.get(key);
            sort.add(temp);
        }

        Collections.sort(sort);

        for (String value : sort) {

            result = result.append(value + "&");
        }


        return result.toString().substring(0, result.length() - 1);
    }

    public static String SigningKey(String appKey, String timestamp) {


        String authStringPrefix = appId + "/" + timestamp;

        return HMACSHA256(appKey.getBytes(), authStringPrefix.getBytes()).toLowerCase();

    }

    public static String Signature(String signingKey, String canonicalRequest) {

        return HMACSHA256(signingKey.getBytes(), canonicalRequest.getBytes()).toLowerCase();

    }


    public static String HMACSHA256(byte[] key, byte[] data) {

        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }


    public static String getCanonicalTime() {
        SimpleDateFormat utcDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat utcHourFormat = new SimpleDateFormat("HH:mm:ss");
        utcDayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        utcHourFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        return String.format("%sT%sZ", utcDayFormat.format(now), utcHourFormat.format(now));
    }

}
