package models.ext.cps.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * cps奖项结算统计
 *
 * @author liuyang
 * @createDate 2018年6月21日
 */
@Entity
public class CpsAwardRecord {

	@Id
	public Long id;
	
	public int spread_num;
	
}
