package us.hall.fsp.wrap.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import java.lang.reflect.Method;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class WrapperFileChannel extends FileChannel {

	private final FileChannel proxy;
    
	public WrapperFileChannel(FileChannel proxy) {
		this.proxy = proxy;
	}
	
    public static FileChannel open(Path path,
            Set<? extends OpenOption> options,
            FileAttribute<?>... attrs) throws IOException 
    {        
        return path.getFileSystem().provider().newFileChannel(path, options, attrs);       
	}
    
    public static FileChannel open(Path path, OpenOption... options)
            throws IOException
    {
    	
        Set<OpenOption> set;
        if (options.length == 0) {
            set = Collections.emptySet();
        } else {
            set = new HashSet<>();
            Collections.addAll(set, options);
        }
    	return open(path, set, new FileAttribute[0]);    	
    }
    
    public int read(ByteBuffer dst) throws IOException {   	
    	return proxy.read(dst);    	
    }
    
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {    	
    	return proxy.read(dsts, offset, length);
    }
    
    public int write(ByteBuffer src) throws IOException {    	
    	return proxy.write(src);
    }
    
    public long write(ByteBuffer[] srcs, int offset, int length)
            throws IOException {
    	    	
    	return proxy.write(srcs, offset, length);   	
    }
    
    public long position() throws IOException {    	
    	return proxy.position();    	
    }
    
    public FileChannel position(long newPosition) throws IOException {    	
    	return proxy.position(newPosition);    	
    }
    
    public long size() throws IOException {    	
    	return proxy.size();
    }
    
    public FileChannel truncate(long size) throws IOException {   	
    	return proxy.truncate(size);
    }
    
    public void force(boolean metaData) throws IOException {
    	proxy.force(metaData);
    }
    
    public long transferTo(long position, long count,
            WritableByteChannel target) throws IOException {
    	
    	return proxy.transferTo(position, count, target);
    }
    
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
    	
    	return proxy.transferFrom(src, position, count);
    }
    
    public int read(ByteBuffer dst, long position) throws IOException {	
    	return proxy.read(dst, position);   	
    }
    
    public int write(ByteBuffer src, long position) throws IOException {
    	
    	return proxy.write(src, position);
    }
    
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
    	
    	return proxy.map(mode, position, size);   	
    }
    
    public FileLock lock(long position, long size, boolean shared) throws IOException {
    	
    	return proxy.lock(position, size, shared);
    }
    
    public FileLock tryLock(long position, long size, boolean shared)
            throws IOException {
    	
    	return proxy.lock(position, size, shared);
    }
    
    protected void implCloseChannel() throws IOException {    	
    	final Object target = proxy;
    	AccessController.doPrivileged(new PrivilegedAction() {
    		Class<?> cl = target.getClass();
    		public Object run() {
    			try {
    				Method m = cl.getDeclaredMethod("implCloseChannel",new Class[0]);
    				m.setAccessible(true);
    				m.invoke(target,new Object[0]);
    			}
    			catch (Exception ex) { ex.printStackTrace(); }
    			return null;
    		}
    	});
    	// This is actually protected implemented in FileChannelImpl not FileChannel
    	// so the following can't be made to work
//    	proxy.implCloseChannel();
    }
}
