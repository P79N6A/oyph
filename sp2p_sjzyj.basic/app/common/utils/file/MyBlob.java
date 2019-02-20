package common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.IO;

public class MyBlob {

	private String UUID;
	private File file;
	private String folder=null;

	public MyBlob() {
	}

	public InputStream get() {
		if (exists()) {
			try {
				return new FileInputStream(getFile());
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
		return null;
	}

	public void set(InputStream is) {
		this.UUID = Codec.UUID();
		IO.write(is, getFile());
	}
	public void set(InputStream is,String folder) {
		this.UUID = Codec.UUID();
		this.folder = folder;
		IO.write(is, getFile());
	}

	public boolean exists() {
		return UUID != null && getFile().exists();
	}
	
	public File getFile() {
        if(file == null) {
        	if(StringUtils.isNotBlank(folder)){
        		file = new File(getStore()+folder+File.separator, UUID);
        	}else{
        		file = new File(getStore(), UUID);
        	}
            
        }
        return file;
    }
    
	
	public static File getStore() {
        String name = Play.configuration.getProperty("attachments.path", "attachments");
        File store = null;
        if(new File(name).isAbsolute()) {
            store = new File(name);
        } else {
            store = Play.getFile(name);
        }
        if(!store.exists()) {
            store.mkdirs();
        }
        return store;
    }
}
