package jobs;


import common.utils.AccessTokenThread;
import play.jobs.Every;
import play.jobs.Job;

@Every("600min")
public class AccessTokenJob extends Job {
	
    public void run() {  
    	new Thread(new AccessTokenThread()).start();
    }  
}
