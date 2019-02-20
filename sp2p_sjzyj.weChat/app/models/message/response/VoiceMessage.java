package models.message.response;

/**
 * ClassName: VoiceMessage
 * @Description: 语音消息
 * @author liuyang
 * @date 2018年6月27日
 */
public class VoiceMessage {
	private Voice Voice;
	 
    public Voice getVoice() {
        return Voice;
    }
 
    public void setVoice(Voice voice) {
        Voice = voice;
    }
}
