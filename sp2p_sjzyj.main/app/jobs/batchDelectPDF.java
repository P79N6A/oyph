package jobs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import play.Logger;
import play.Play;
import services.common.SettingService;

/**
 * PDF文件定时删除
 * 
 * @author LiuPengwei
 * @createDate 2018年4月12日16:00:40
 *
 */

public class batchDelectPDF {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	    
		/** 文件缓存路径 */
		static final String PATH = Play.getFile("tmp").getAbsolutePath();
		static int countFolders = 0;// 声明统计文件夹下包含关键字的变量
		 
	    public static File[] searchFile(File folder, final String keyWord) {// 递归查找包含关键字的文件
	 
	        File[] subFolders = folder.listFiles(new FileFilter() {// 运用内部匿名类获得文件
	            @Override
	            public boolean accept(File pathname) {// 实现FileFilter类的accept方法
	            	
	                    countFolders++;
	                if (pathname.isFile() && pathname.getName().toLowerCase().contains(keyWord.toLowerCase())){// 文件夹下关键字
	                    
	                	return true;
	                }
	                return false;
	            }
	        });
	 
	        List<File> result = new ArrayList<File>();// 声明一个集合
	        for (int i = 0; i < subFolders.length; i++) {// 循环显示文件
	                result.add(subFolders[i]);
	        }
	        
	        File files[] = new File[result.size()];// 声明文件数组，长度为集合的长度
	        result.toArray(files);// 集合数组化
	        return files;
	    }
	 
	    public void doJob(){// java程序的主入口处
	    	if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	    		Logger.info("--------定时清理PDF文件，开始--------");
			}
	    	
	    	
	    	File folder = new File(PATH);// 默认目录
	        String keyword = ".pdf";
	        if (!folder.exists()) {// 如果文件夹不存在
	        	
	            return;
	        }
	        File[] result = searchFile(folder, keyword);// 调用方法获得文件数组
	        for (int i = 0; i < result.length; i++) {// 循环删除文件
	            File file = result[i];
	            if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	            	Logger.info(file.getAbsolutePath());
	    		}
	            
	            if (file.getName().length() ==21 || file.getName().length() ==20 ||
	            		file.getName().length() ==19){//合同名称长度
	            	deleteFile(file.getAbsolutePath());
	            }
	            
	        }
	        if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	        	Logger.info("--------定时清理PDF文件，结束--------");
			}
	        
	    }
	    
	    /**
	     * 删除单个文件
	     *
	     * @param fileName
	     *            要删除的文件的文件名
	     * @return 单个文件删除成功返回true，否则返回false
	     */
	    public static boolean deleteFile(String fileName) {
	        File file = new File(fileName);
	        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
	        if (file.exists() && file.isFile()) {
	            if (file.delete()) {

	                return true;
	            } else {
	                System.out.println("删除单个文件" + fileName + "失败！");
	                return false;
	            }
	        } else {
	           
	            return false;
	        }
	    }
	    
}

