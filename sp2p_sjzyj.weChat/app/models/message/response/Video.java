package models.message.response;

/**
 * ClassName: Video
 * @Description: 视频消息体
 * @author liuyang
 * @date 2018年6月27日
 */
public class Video {
	private String MediaId;
	
    private String Title;
    
    private String Description;
 
    public String getTitle() {
        return Title;
    }
 
    public void setTitle(String title) {
        Title = title;
    }
 
    public String getDescription() {
        return Description;
    }
 
    public void setDescription(String description) {
        Description = description;
    }
 
    public String getMediaId() {
        return MediaId;
    }
 
    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}
