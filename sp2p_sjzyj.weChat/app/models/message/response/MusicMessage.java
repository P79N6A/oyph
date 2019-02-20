package models.message.response;

/**
 * ClassName: MusicMessage
 * @Description: 音乐消息
 * @author liuyang
 * @date 2018年6月27日
 */
public class MusicMessage extends BaseMessage {
	
	private Music Music;//音乐

	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}

}
