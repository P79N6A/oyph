package models.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

public class CallBackPrint implements Serializable {
	
	@Id
	public long id;
	
	public Date time;
	
	public Map<String, String> body = new HashMap<>();
	
}
