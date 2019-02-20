package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import services.common.VoteActivityService;

/**
 * 投票活动奖品规则表
 * @author Administrator
 *
 */
@Entity
public class t_activity_prize extends Model{

	/** 活动id */
	public long activity_id;
	
	/** 奖品内容 */
	public String prize_describe;
	
	/** 范围低排名 */
	public int floor_num;
	
	/** 范围高排名 */
	public int upper_num;
	
	/** 活动 */
	@Transient
	public t_vote_activity voteActivity;
	
	public t_vote_activity getVoteActivity(){
		VoteActivityService voteActivityService = common.utils.Factory.getService(VoteActivityService.class);
		t_vote_activity vote = voteActivityService.findByID(this.activity_id);
		if(vote != null) {
			return vote;
		}
		return null;
	}
}
