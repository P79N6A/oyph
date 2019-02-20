package models.message.response;

/**
 * ClassName: TextMessage
 * @Description: 文本消息消息体
 * @author liuyang
 * @date 2018年6月27日
 */
public class TextMessage extends BaseMessage {
	
	private String Content;//回复的消息内容

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
	
}
