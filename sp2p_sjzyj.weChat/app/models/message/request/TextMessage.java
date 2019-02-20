package models.message.request;

/**
 * 文本消息
 * @author 51452
 *
 */
public class TextMessage extends BaseMessage {
	
	private String Content;//消息内容

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

}
