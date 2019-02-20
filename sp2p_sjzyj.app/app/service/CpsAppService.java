package service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.utils.Factory;
import common.utils.PageBean;
import dao.CpsAppDao;
import models.ext.cps.bean.CpsSpreadRecord;
import net.sf.json.JSONObject;
import play.mvc.Http.Request;
import services.ext.cps.CpsUserService;

public class CpsAppService extends CpsUserService{

	private CpsAppDao cpsAppDao = null;
	private CpsAppService () {
		cpsAppDao = Factory.getDao(CpsAppDao.class);
	}
	
	public String pageOfCpsRecords(int currPage,int pageSize,long userId) {
		
		PageBean<CpsSpreadRecord> spreadRecord = cpsAppDao.queryCpsRecord(currPage, pageSize, userId);
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("code",1 );
		map.put("msg", "查询成功");
		map.put("cpsRecords", spreadRecord );
		
		return JSONObject.fromObject(map).toString();
	}
	
	public String queryMoblie (long userId) {
		
		String mobile =  cpsAppDao.queryMobileById(userId);
		Map<String,Object> map = new HashMap<String,Object>();
		if (mobile == null){
			map.put("code", -1);
			map.put("msg", "查询失败");
		} 
		String mobileSign = Encrypt.encrypt3DES(mobile, ConfConst.ENCRYPTION_KEY_DES);
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("mobile",mobile);
//		map.put("url", "account/register/toRegister.html?mobileSign="+mobileSign);
		map.put("url", "account/register/toInvite.html?mobileSign="+mobileSign);
		return JSONObject.fromObject(map).toString();
	}
}
