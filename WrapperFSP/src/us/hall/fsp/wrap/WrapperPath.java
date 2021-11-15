package us.hall.fsp.wrap;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class WrapperPath implements Path {

	private final Path proxy;
   
    // package-private
    WrapperPath(Path proxy) {
    	if (proxy == null)
    		throw new IllegalArgumentException("WrapperPath with null"); 
    	this.proxy = proxy;
    }

    /**
     * @return default FileSystem Path that proxy's pass through Path operations for us
     */
    public Path getProxy() { return proxy; }
    
    public WrapperPath getRoot() {
        if (this.isAbsolute()) 
        	return new WrapperPath(proxy.getFileSystem().getPath("/")); 
        return null;
    }

    @Override
    public Path getFileName() {    	
    	return new WrapperPath(proxy.getFileName());
    }
    
    public Path getName(int index) {    	
    	return new WrapperPath(proxy.getName(index));
    }

    public Path getParent() {
    	if (proxy.getParent() == null) {
    		return null;
    	}
    	return new WrapperPath(proxy.getParent());
    }
    
    public int getNameCount() {    	
    	return proxy.getNameCount();
    }

	@Override
    public Path subpath(int beginIndex, int endIndex) {		
    	return new WrapperPath(proxy.subpath(beginIndex, endIndex));
    }

	@Override
    public Path toRealPath(LinkOption... options) throws IOException {		
    	return new WrapperPath(proxy.toRealPath(options));
    }

	@Override
    public WrapperPath toAbsolutePath() {		
    	return new WrapperPath(proxy.toAbsolutePath());
    }

	@Override
    public URI toUri() {		
    	return proxy.toUri();
    }
    
	@Override
    public Path relativize(Path other) {		
    	return new WrapperPath(proxy.relativize(other));
    }

    //@Override
    public FileSystem getFileSystem() {
    	return FileSystems.getDefault();
    }

    @Override
    public boolean isAbsolute() {    	
    	return proxy.isAbsolute();	
    }

    @Override
    public Path resolve(Path other) { 
    	if (other instanceof WrapperPath)
    		return new WrapperPath(proxy.resolve(((WrapperPath)other).proxy));
    	else
    		return new WrapperPath(proxy.resolve(other));
    }
    
    @Override
    public Path resolve(String other) {   	
        return resolve(getFileSystem().getPath(other));
    }

    @Override
    public boolean startsWith(Path other) {
    	if (other instanceof WrapperPath)
    		return proxy.startsWith(((WrapperPath)other).proxy);
    	else
    		return proxy.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
    	if (other instanceof WrapperPath)
    		return proxy.endsWith(((WrapperPath)other).proxy);
    	else
    		return proxy.endsWith(other);
    }
 
    @Override
    public String toString() {    	
    	return proxy.toString();
    }

    @Override
    public int hashCode() {	
    	return proxy.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof WrapperPath)
    		return proxy.equals(((WrapperPath)obj).proxy);
    	else
    		return proxy.equals(obj);
    }

    @Override
    public int compareTo(Path other) {
    	if (other instanceof WrapperPath)
    		return proxy.compareTo(((WrapperPath)other).proxy);
    	else
    		return proxy.compareTo(other);
    }

    @Override
    public final WatchKey register(WatchService watcher,
                                   WatchEvent.Kind<?>... events)
        throws IOException
    {
        return register(watcher, events, new WatchEvent.Modifier[0]);
    }
    
    @Override
    public WatchKey register(
            WatchService watcher,
            WatchEvent.Kind<?>[] events,
            WatchEvent.Modifier... modifiers) throws IOException 
    {
        if (watcher == null)
            throw new NullPointerException();
        
        return proxy.register(watcher,events,modifiers);       
    }

    @Override
    public Iterator<Path> iterator() {  	
    	return proxy.iterator();
    }
    
    @Override
    public Path normalize() {
    	return new WrapperPath(proxy.normalize());
    }

    @Override
    public final boolean endsWith(String other) {
        return proxy.endsWith(other);
    }
    
    @Override
    public Path resolveSibling(Path other) {
    	if (other instanceof WrapperPath)
    		return new WrapperPath(proxy.resolveSibling(((WrapperPath)other).proxy));
    	else
    		return new WrapperPath(proxy.resolveSibling(other));
    }
    
    @Override
    public final Path resolveSibling(String other) {
    	return new WrapperPath(proxy.resolveSibling(other));
    }
    
    @Override
    public final boolean startsWith(String other) {    	
    	return proxy.startsWith(other);
    }
    
    //@Override
    //public final File toFile() {
    //	return proxy.toFile();
    //}
}
