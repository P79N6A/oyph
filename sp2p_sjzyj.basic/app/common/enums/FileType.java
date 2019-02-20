package common.enums;

/**
 * 枚举类型：常见的文件类型
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月16日
 */
public enum FileType {
	
	FYPE_IMG(1,"图片文件","图片类型，可选的有gif,jpg,jpeg,png,bmp五种格式的文件",new String[]{"gif","jpg","jpeg","png","bmp"},new String[]{"image/gif","image/jpeg","image/png","application/x-MS-bmp"}),
	
	FYPE_TXT(2,"文本文件","文本文件，可选的为txt文件",new String[]{"txt"},new String[]{"text/plain"}),
	
	FYPE_VIDEO(3,"视频文件","视频文件类型，可选的有mp4,3gp,avi,wmv,rm,rmvb五种格式的文件",new String[]{"mp4","3gp","avi","wmv","rm","rmvb"},new String[]{"video/mp4","video/3gpp","video/x-msvideo","audio/x-ms-wmv","audio/x-pn-realaudio"}),
	
	FYPE_AUDIO(4,"音频文件","音频文件，可选的有mp3,wav,wma三种格式的文件",new String[]{"mp3","wav","wma"},new String[]{"audio/x-mpeg","audio/x-wav","audio/x-ms-wma"}),
	
	FYPE_XLS(5,"表格文件","表格文件，可选的有xls,xlsx两种格式的文件",new String[]{"xls","xlsx"},new String[]{"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}),
	
	FYPE_DOC(6,"Word文件","Word文件，可选的有doc,docx两种格式的文件",new String[]{"docx","doc"},new String[]{"docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword"}),
	
	FYPE_PPT(7,"演示文稿","演示文稿，可选的有ppt,pptx两种格式的文件",new String[]{"ppt","pptx"},new String[]{"application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation"});
	
	/** 文件类型代号 */
	public int code;
	
	/** 文件类型名称 */
	public String type;
	
	/** 文件类型描述 */
	public String description;
	
	/** 该文件类型常用后缀 */
	public String[] suffix;
	
	/** 该文件类型常用的mimeTyps */
	public String[] mime;
	
	private FileType(int code, String type, String description,String[] suffix,String[] mime) {
		this.code = code;
		this.type = type;
		this.suffix=suffix;
		this.mime=mime;
		this.description = description;
	}

	public static FileType getEnum(int code){
		FileType[] statuies = FileType.values();
		for (FileType stat : statuies) {
			if (stat.code == code) {
				
				return stat;
			}
		}
		
		return null;
	} 
	
	/** 根据文件MIME索引文件类型 */
	public static FileType getEnumByMime(String mime){
		FileType[] statuies = FileType.values();
		for (FileType type : statuies) {
			String[] legalMimes = type.mime;
			for (String legalMime : legalMimes) {
				if (legalMime.equalsIgnoreCase(mime)) {
					return type;
				}
			}
		}
		
		return null;
	}

}
