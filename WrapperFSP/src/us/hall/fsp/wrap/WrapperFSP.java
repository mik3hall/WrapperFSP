package us.hall.fsp.wrap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

import us.hall.fsp.LoggingFSP;

/**
 * Wrapping file system provider
 * 
 * @author mjh
 *
 */
public class WrapperFSP extends FileSystemProvider {

    private static FileSystemProvider priorProvider;
    private static LoggingFSP loggingProvider;

    /**
     * This may be used as a 'helper' provider for attributes or whatever
     * so the no arg constructor may be used and simply sets priorProvider to the 
     * first found 'file' scheme provider - which should be the 'default' platform
     * provider.
     */
    public WrapperFSP() {
    	if (priorProvider == null)
    		throw new IllegalStateException("WrapperFSP no args with no prior provider");
    }
	
    public WrapperFSP(FileSystemProvider fsp) { 
    	priorProvider = fsp; 
    	String prop = "wrappedFSP";
        String propValue = System.getProperty(prop);
        if (propValue != null) {
            try {
                Class<?> c = Class
                    .forName(propValue, true, ClassLoader.getSystemClassLoader());
            	Constructor<?>[] ctors = c.getDeclaredConstructors();
            	Constructor<?> bestCtor = null;
            	for (Constructor<?> ctor : ctors) {
            		if (ctor.getParameterCount() > 0 || bestCtor == null)
            			bestCtor = ctor;
            	}
                FileSystemProvider wrappedProvider;
                if (bestCtor.getParameterCount() > 0)
                	wrappedProvider = (FileSystemProvider)bestCtor.newInstance(fsp);
                else 
                	wrappedProvider = (FileSystemProvider)bestCtor.newInstance();
                loggingProvider = new LoggingFSP(wrappedProvider);
            } catch (Exception x) {
                throw new Error(x);
            }
        }
              
    }
	
    static FileSystemProvider getPrior() {
    	return priorProvider;
    }
    
    /**
     * Splits the given attribute name into the name of an attribute view and
     * the attribute. If the attribute view is not identified then it assumed
     * to be "basic".
     */
    private static String[] split(String attribute) {
        String[] s = new String[2];
        int pos = attribute.indexOf(':');
        if (pos == -1) {
            s[0] = "basic";
            s[1] = attribute;
        } else {
            s[0] = attribute.substring(0, pos++);
            s[1] = (pos == attribute.length()) ? "" : attribute.substring(pos);
        }
        return s;
    }
    
    @Override
    public void setAttribute(Path path, String attribute,
                             Object value, LinkOption... options)
        throws IOException
    {
        String[] s = split(attribute);
        if (s[0].length() == 0)
            throw new IllegalArgumentException(attribute);

        loggingProvider.setAttribute(path,attribute,options);
    }
    
    @Override
    public <A extends BasicFileAttributes> A
        readAttributes(Path path, Class<A> type, LinkOption... options)
        throws IOException
    {    	
        return loggingProvider.readAttributes(path,type,options);
    }
 
    @Override
    public Map<String, Object>
        readAttributes(Path path, String attributes, LinkOption... options)
        throws IOException
    {   	
        return loggingProvider.readAttributes(path,attributes,options);
    }
    
    @Override
    public <V extends FileAttributeView> V
        getFileAttributeView(Path path, Class<V> type, LinkOption... options)
    {    	
        return loggingProvider.getFileAttributeView(path,type,options);
    }
    
    boolean followLinks(LinkOption... options) {
        boolean followLinks = true;
        for (LinkOption option: options) {
            if (option == LinkOption.NOFOLLOW_LINKS) {
                followLinks = false;
                continue;
            }
            if (option == null)
                throw new NullPointerException();
            throw new AssertionError("Should not get here");
        }
        return followLinks;
    }
    
    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
    	loggingProvider.checkAccess(path,modes);
    }
    
    @Override
    public FileStore getFileStore(Path path) throws IOException {
    	return loggingProvider.getFileStore(path);
    }
    
    @Override
    public boolean isHidden(Path path) throws IOException {
        return priorProvider.isHidden(path);
    }
    
    @Override
    public boolean isSameFile(Path path, Path other) throws IOException {    	
        return loggingProvider.isSameFile(path,other);
    }
    
    @Override
    public void move(Path src, Path target, CopyOption... options)
        throws IOException
    {    	
    	loggingProvider.move(src,target,options);    	
    }
    
    @Override
    public void copy(Path src, Path target, CopyOption... options)
        throws IOException
    {	
    	loggingProvider.copy(src,target,options);
    }
    
    @Override
    public final void delete(Path path) throws IOException {
    	loggingProvider.delete(path);
    }
    
    @Override
    public void createDirectory(Path path, FileAttribute<?>... attrs)
        throws IOException
    {
    	loggingProvider.createDirectory(path,attrs);
    }
    
    @Override
    public DirectoryStream<Path> newDirectoryStream(
        Path path, Filter<? super Path> filter) throws IOException
    {	
        return new WrapperDirectoryStream(priorProvider.newDirectoryStream(path,filter));
    }
    
    @Override
    public SeekableByteChannel newByteChannel(Path path,
                                              Set<? extends OpenOption> options,
                                              FileAttribute<?>... attrs) throws IOException
    {    	
    	return loggingProvider.newByteChannel(path,options,attrs);
    }
    
    @Override
    public Path getPath(URI uri) {   	
		return loggingProvider.getPath(uri);
    }

    @Override
    public FileSystem getFileSystem(URI uri) {   	
       return new WrapperFS(this, loggingProvider); 
    }
    
    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env)
        throws IOException
    { 
    	return loggingProvider.newFileSystem(uri,env);
    }
    
    @Override
    public String getScheme() {   	
    	return loggingProvider.getScheme(); 
    }
}