package applet;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class JsFsApplet extends Applet {
	public JsFsApplet(){
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				m_cwd = m_home = System.getProperty("user.home") + "/";
				return null;
			}
		});
	}
	
	public String cwd(){
		return m_cwd;
	}
	
	public boolean cd(){
		return cd(m_home);
	}
	
	public boolean cd(final String path){
		if(path == null){
			return false;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				File dir = new File(getAbsolutePath(path));
				
				if(dir.exists()){
					m_cwd = dir.toString() + "/";
					return true;
				}
				
				return false;
			}
		});
	}
	
	public String[] ls(){
		return ls("");
	}
	
	public String[] ls(final String path){	
		if(path == null){
			return null;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<String[]>() {
			public String[] run() {
				return new File(getAbsolutePath(path)).list();
			}
		});
	}
	
	public boolean mkdir(final String path){
		if(path == null){
			return false;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				return new File(getAbsolutePath(path)).mkdir();
			}
		});
	}
	
	public boolean rm(final String path){
		if(path == null){
			return false;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {			
			public Boolean run() {
				File file = new File(getAbsolutePath(path));
				
				if(file.isDirectory()){
					for(File c : file.listFiles()){
						rm(c.getAbsolutePath());
					}
				}
				
				return file.delete();
			}
		});
	}
	
	public String read(final String path) {
		if(path == null){
			return null;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				FileInputStream stream = null;

				try {
					stream = new FileInputStream(new File(getAbsolutePath(path)));
					FileChannel fc = stream.getChannel();
					MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
					return Charset.defaultCharset().decode(bb).toString();
				} catch (IOException e) {
					return null;
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
					}
				}
			}
		});
	}
	
	public boolean write(final String path, final String content) {
		if(path == null || content == null){
			return false;
		}
		
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				PrintWriter out = null;
				
				try {
					out = new PrintWriter(getAbsolutePath(path));
					out.write(content);
				} catch (IOException e) {
					return false;
				} finally {
					out.close();
				}
				
				return true;
			}
		});
	}
	
	public boolean cp(final String source, final String destination){
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				FileChannel in = null;
				FileChannel out = null;
				
				try{
					in = new FileInputStream(getAbsolutePath(source)).getChannel();
					try{
						out = new FileInputStream(getAbsolutePath(destination)).getChannel();
					}catch(FileNotFoundException e){
						new File(getAbsolutePath(destination)).createNewFile();
						out = new FileOutputStream(getAbsolutePath(destination)).getChannel();
					}
					
					out.transferFrom(in, 0, in.size());
				}catch(Exception e){
					return false;
				}finally{
					try {
						in.close();
						out.close();
					} catch (IOException e) {
						return false;
					}
				}
				
				return true;
			}
		});
	}
	
	private String getAbsolutePath(String path){
		return path.startsWith("/") ? path : m_cwd + path;
	}
	
	private String m_cwd;
	private String m_home;
}
