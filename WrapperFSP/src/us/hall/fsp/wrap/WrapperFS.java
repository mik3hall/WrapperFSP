package us.hall.fsp.wrap;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import us.hall.fsp.LoggingFSP;

public class WrapperFS extends FileSystem {
   	
    private final LoggingFSP loggingProvider;
    private final FileSystemProvider priorProvider ;
    private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
    private Set<Closeable> closeableObjects = new HashSet<Closeable>();
    private static URI rootURI;
    static {
    	try { 
    	   rootURI = new URI("file",null,"/",null);
    	}
    	catch (URISyntaxException use) { rootURI = null; }
    }

	// Shouldn't be invoked
	protected WrapperFS() { throw new UnsupportedOperationException(); }

	WrapperFS(WrapperFSP provider,LoggingFSP priorProvider) {
		this.loggingProvider = priorProvider;
		this.priorProvider = provider;
	}
	
    WrapperFS(LoggingFSP provider, Path fref) {
        this(provider, fref.toString(), "/");
    }

    WrapperFS(LoggingFSP provider, String path, String defaultDir) {
        this.loggingProvider = provider;
        this.priorProvider = WrapperFSP.getPrior();
    }
    
    @Override
    public FileSystemProvider provider() {       
        return loggingProvider;        
    }

    @Override
    public boolean isOpen() {  	
    	return loggingProvider.getFileSystem(rootURI).isOpen();
    }

    @Override
    public boolean isReadOnly() {    	
        return loggingProvider.getFileSystem(rootURI).isReadOnly();
    }

    @Override
    public String getSeparator() {   	
        return "/"; 
    }
    
    @Override
    public void close() throws IOException {    	
    	loggingProvider.getFileSystem(rootURI).close();
    }

    final void begin() {    	
        closeLock.readLock().lock();
        if (!isOpen()) {
            throw new ClosedFileSystemException();
        }        
    }

    final void end() {    	
        closeLock.readLock().unlock();        
    }
    
    boolean addCloseableObjects(Closeable obj) {  	
        return closeableObjects.add(obj);        
    }
    
    @Override
    public Iterable<Path> getRootDirectories() {    	
    	return priorProvider.getFileSystem(rootURI).getRootDirectories();
    }

    @Override
    public Path getPath(String first,String... more) {    	
    	return new WrapperPath(loggingProvider.getFileSystem(rootURI).getPath(first, more));
    }
		
    @Override
    public Iterable<FileStore> getFileStores() {
    	return loggingProvider.getFileSystem(rootURI).getFileStores();
    }
    
    @Override
    public Set<String> supportedFileAttributeViews() {    	
        return loggingProvider.getFileSystem(rootURI).supportedFileAttributeViews();
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {    	
    	return loggingProvider.getFileSystem(rootURI).getPathMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {   	
    	return loggingProvider.getFileSystem(rootURI).getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws IOException {    	
    	return priorProvider.getFileSystem(rootURI).newWatchService();
    }
}