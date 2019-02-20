package models.message.response;

/**
 * ClassName: Image
 * @Description: 图片回复消息体
 * @author liuyang
 * @date 2018年6月27日
 */
public class Image extends BaseMessage {

	private String MediaId;
	 
    public String getMediaId() {
        return MediaId;
    }
 
    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}
