package common.utils;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import common.utils.Constant;
import common.utils.Factory;
import common.utils.wx.WeixinUtil;
import models.AccessToken;
import models.entity.t_wechat_access_token;
import play.db.jpa.JPA;
import play.jobs.Job;
import service.AccessTokenService;

public class AccessTokenThread implements Runnable {
	
	protected static AccessTokenService accessTokenService = Factory.getService(AccessTokenService.class);
	
	private static Logger log =  Logger.getLogger("TokenThread");
	
    public void run() {
    	
    	if (JPA.local.get() == null) {
            EntityManager em = JPA.newEntityManager();
            final JPA jpa = new JPA();
            jpa.entityManager = em;
            JPA.local.set(jpa);
        }
        
        while (true) {
        	JPA.em().getTransaction().begin();
            try {
            	log.info("正在获取access_token...");
            	
            	//t_wechat_access_token accessToken = WeixinUtil.getAccessToken(Constant.APPID, Constant.APPSECRET);  
            	t_wechat_access_token accessToken = WeixinUtil.getAccessToken(Constant.appid, Constant.appsecret);  
                if (null != accessToken) {  
                    log.info("获取access_token成功");
                    log.info(accessToken.getToken()+","+accessToken.getExpire_in());
                    
                    //保存
                    t_wechat_access_token accessTokens = accessTokenService.queryAccessToken();
                    
                    accessTokens.expire_in = accessToken.getExpire_in();
                    accessTokens.token = accessToken.getToken();
                    accessTokens.time = new Date();
                    
                    accessTokens.save();
                    log.info("保存access_token成功");
                    JPA.em().getTransaction().commit();
                    // 休眠3600秒  
                    Thread.sleep((accessToken.getExpire_in() - 3600) * 1000);  
                } else {  
                    // 如果access_token为null，60秒后再获取  
                    Thread.sleep(60 * 1000);  
                }  
            } catch (InterruptedException e) {  
                try {  
                    Thread.sleep(60 * 1000);  
                } catch (InterruptedException e1) {  
                    log.error(e.getMessage(),e1);  
                }  
                log.error(e.getMessage(), e);  
            } catch(Exception e){
            	log.error("保存access_token失败！"+e.getMessage(),e);
            	try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e1) {				
					log.error(e.getMessage(),e1);
				}
            }
            
            
        } 
        
        
    }  
}
