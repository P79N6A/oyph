package common.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.alipay.fc.csplatform.common.crypto.Base64Util;

public class OnlinePubKeyUtil {
	private static String pub_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtTJ9Oo/gvo6476PYi4zADnCVhtLxI8q9HsN1EkRJfna85p95WrNOxjdH5E3PfDBQolTnvNzdxGeetYA6UFczndXbeWfHeijQQyuBJ57hKYbCtLmG10K8VcpekgDf76NCETmX3HN4HQAYioLojn3MmwzP+95K89lLzB2XdJz4gjNQDcdILS3CIpsj1hjroGnZSIkluwfELjJNDW6sU019lHMJ7OB6KPbFEcEEsahLmtMP2ua/C5rZjbRyP24cAbTc2yrjm3A1M8WoUbcvcIIpRw93BoYfK985vbI4M6WNfYl0/wOpYrhom6oo19dGJRlBBeKnjiW2Wa5MFUvBOHEXTQIDAQAB";
	//private static String pub_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgId9uP1iFx3HYGmI6drXEkMWllvou3kQR0/T97Vz5AF2lKFSdKZlbiNF142hq1iPZdx9Zm0qOz7Ug46QgXqBxHXvrus3Rs0iWQeEUX4QHZeYTH0NXHvrX4ePmp823+IyEfcddoOef4QAjx3Ta4rVEwhqmYaOVOZahoOzz5k1LAkVJtKKvJMNJ2ll7nB72lR9YtTxv66uKyKltErs+AX7b57KI/pFj49Q2CCQgQwnHDlWKVaM1G3ATD2qAykr8rVY1bdADo7Jahve/f3MgVtTP7ZsEnMsomh9lTjT0qCxXhLMc2HZDPl4xpfZcLCU24UNaJQB0zwrXYZ+gaP4gbEawIDAQAB";
	//static String pub_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgId9uP1iFx3HYGmI6drXEkMWllvou3kQR0/T97Vz5AF2lKFSdKZlbiNF142hq1iPZdx9Zm0qOz7Ug46QgXqBxHXvrus3Rs0iWQeEUX4QHZeYTH0NXHvrX4ePmp823+IyEfcddoOef4QAjx3Ta4rVEwhqmYaOVOZahoOzz5k1LAkVJtKKvJMNJ2Kll7nB72lR9YtTxv66uKyKltErs+AX7b57KI/pFj49Q2CCQgQwnHDlWKVaM1G3ATD2qAykr8rVY1bdADo7Jahve/f3MgVtTP7ZsEnMsomh9lTjT0qCxXhLMc2HZDPl4xpfZcLCU24UNaJQB0zwrXYZ+gaP4gbEawIDAQAB";
	/**MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp5khaVZIP+x+n+ari3c1dcYRuNG7BUL0VVt1X2+KDcOpTHtfdHUQIzrHdbikZSyaCKyHLbAuLsU24gulCKmMGtjFGS6OiavUFFikVnVnOyEzcnhSPBiJNdlUo5TNYOVN1SUCutJUxvibQ1za6IcnHf4okgPTgcrXVHyG5ntCbE9ippKLe7q0z0TUIkRxesbKZiQPBDOgBukJUiFMk95zqCdESCe6kCSp2GdIojyVAelU11JIcAm/4OjCCFMz6Jcnse7rdScxRsoMHU89tDmXG9mo3PhUXyfQJzyESlotKek99eHPkSr7EW/SBj3xMc+ysrBZd+4tFOZJIRIlFf/eSQIDAQAB
	 * 还原出RSA公钥对象
	 * @author guoShiJie
	 * */
	public static PublicKey getPubKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(pub_key)/*BASE64.decode(pub_key)*/);
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        return key;
    }
}
