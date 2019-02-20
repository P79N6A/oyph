package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;
import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_wechat_image_text_message
 *
 * @description 
 *
 * @author HanMeng
 * @createDate 2018年12月25日-上午10:49:42
 */
@Entity
public class t_wechat_image_text_message extends Model {

	public String keywords;
	
	public int matchs;
	
	/** is_use '0-下架\r\n1-上架'  */
	private boolean is_use;
	
	public String title;
	
	public String author;
	
	public String show_content;
	
	public String cover_img;
	
	public String cover_format;
	
	public int is_likenum;
	
	public int is_pageview;
	
	public int is_show;
	
	public int is_attention;
	
	public String content;
	
	public String outreach_url;
	
	public Date time;
	
	public int like_num;
	
	public int view_count;
	
	public int visitors;
	
	public long type_id;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.INFORMATION_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	@Transient
	public t_wechat_type wechatT;
	
	public t_wechat_type getWechatT() {
		
		if(this.wechatT == null) {
			this.wechatT = t_wechat_type.findById(type_id);
		}
		return this.wechatT;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getMatchs() {
		return matchs;
	}

	public void setMatchs(int matchs) {
		this.matchs = matchs;
	}

	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getShow_content() {
		return show_content;
	}

	public void setShow_content(String show_content) {
		this.show_content = show_content;
	}

	public String getCover_img() {
		return cover_img;
	}

	public void setCover_img(String cover_img) {
		this.cover_img = cover_img;
	}

	public String getCover_format() {
		return cover_format;
	}

	public void setCover_format(String cover_format) {
		this.cover_format = cover_format;
	}

	public int getIs_likenum() {
		return is_likenum;
	}

	public void setIs_likenum(int is_likenum) {
		this.is_likenum = is_likenum;
	}

	public int getIs_pageview() {
		return is_pageview;
	}

	public void setIs_pageview(int is_pageview) {
		this.is_pageview = is_pageview;
	}

	public int getIs_show() {
		return is_show;
	}

	public void setIs_show(int is_show) {
		this.is_show = is_show;
	}

	public int getIs_attention() {
		return is_attention;
	}

	public void setIs_attention(int is_attention) {
		this.is_attention = is_attention;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOutreach_url() {
		return outreach_url;
	}

	public void setOutreach_url(String outreach_url) {
		this.outreach_url = outreach_url;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getLike_num() {
		return like_num;
	}

	public void setLike_num(int like_num) {
		this.like_num = like_num;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getVisitors() {
		return visitors;
	}

	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}

	public long getType_id() {
		return type_id;
	}

	public void setType_id(long type_id) {
		this.type_id = type_id;
	}
	
	
	
}
