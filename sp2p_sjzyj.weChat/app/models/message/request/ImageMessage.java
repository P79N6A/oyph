package models.message.request;

/**
 * 图片消息
 * @author 51452
 *
 */
public class ImageMessage extends BaseMessage {
	
	private String PicUrl;//图片链接

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

}
