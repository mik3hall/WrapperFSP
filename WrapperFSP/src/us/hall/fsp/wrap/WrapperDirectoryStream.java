package us.hall.fsp.wrap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

public class WrapperDirectoryStream implements DirectoryStream<Path> {

	DirectoryStream<Path> proxy;
    
	WrapperDirectoryStream(DirectoryStream<Path> proxy) {
		this.proxy = proxy;
	}
	
	 @Override
	    public void close()
	        throws IOException 
	 {		 
		 proxy.close();		 
	 }
	 
	 @Override
	 public Iterator<Path> iterator() {		 
		 return proxy.iterator();
	 }
}
