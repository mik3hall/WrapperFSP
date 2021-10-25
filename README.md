# LoggingFSP

LoggingFSP is a default java FileSystemProvider meant to provide logging for file system accesses of another FileSystemProvider. This could be the platform provider or another default 'file' FileSystemProvider.

This is just starting. If possible I would like to get it to support provider schemes other than 'file'. Or multiple FileSystem's for different URI's. There is currently no support for that.

The following shows what it looks like run against the platform provider...

```
java -Djava.nio.file.spi.DefaultFileSystemProvider=us.hall.fsp.LoggingFSP -DlogParms=true -DlogReturn=true -cp .:loggingFSP.jar --module-path . Test
FSP test...
us.hall.fsp.LoggingFSP

us.hall.fsp.LoggingFS@b4c966a
[2021-10-24T18:26:14.024210] [FINER] entering sun.nio.fs.MacOSXFileSystem getPath with /Library/Java/JavaVirtualMachines/jdk-18.jdk/Contents/Home,[Ljava.lang.String;@34c45dca
[2021-10-24T18:26:14.029271] [FINER] exiting sun.nio.fs.MacOSXFileSystem getPath returning /Library/Java/JavaVirtualMachines/jdk-18.jdk/Contents/Home/lib/modules
[2021-10-24T18:26:14.017877] [FINER] entering sun.nio.fs.MacOSXFileSystem provider 
[2021-10-24T18:26:14.033676] [FINER] exiting sun.nio.fs.MacOSXFileSystem provider returning us.hall.fsp.LoggingFSP@66a29884
[2021-10-24T18:26:14.033788] [FINER] entering sun.nio.fs.MacOSXFileSystemProvider getScheme() 
[2021-10-24T18:26:14.033840] [FINER] exiting sun.nio.fs.MacOSXFileSystemProvider getScheme returning file
file
Default FS us.hall.fsp.LoggingFS@b4c966a
[2021-10-24T18:26:14.034503] [FINER] entering sun.nio.fs.MacOSXFileSystem getPath with /Users/mjh,[Ljava.lang.String;@cc34f4d
[2021-10-24T18:26:14.034596] [FINER] exiting sun.nio.fs.MacOSXFileSystem getPath returning /Users/mjh
[2021-10-24T18:26:14.034717] [FINER] entering sun.nio.fs.UnixPath toAbsolutePath 
[2021-10-24T18:26:14.034776] [FINER] exiting sun.nio.fs.UnixPath toAbsolutePath returning /Users/mjh
[2021-10-24T18:26:14.034853] [FINER] entering sun.nio.fs.UnixPath isAbsolute 
[2021-10-24T18:26:14.034898] [FINER] exiting sun.nio.fs.UnixPath isAbsolute returning true
/Users/mjh is absolute true
[2021-10-24T18:26:14.041180] [FINER] entering sun.nio.fs.UnixPath toUri 
[2021-10-24T18:26:14.041330] [FINER] exiting sun.nio.fs.UnixPath toUri returning file:///Users/mjh/
file:///Users/mjh/
```

Which shows on OS/X the platform provider is a mix of custom Mac and generic Unix.

To run it against a different FileSystem you would use ![WrapperFSP](https://github.com/mik3hall/WrapperFSP). This is modular and I wasn't sure how to have two different module-info.java with only one project.

To run about the same test modular I think would be something like...

```
java -Djava.nio.file.spi.DefaultFileSystemProvider=us.hall.fsp.LoggingFSP -DlogParms=true -DlogReturn=true -cp . --module-path . -m loggingFSP/us.hall.fsp.Test
```
It is currently a Eclipse project for me so no build is included. It isn't that complicated. I can probably add at least an ant build later.

To override the DefaultFileSystemProvider involves the code into the JDK bootstrap process. This has seemed to perturb fairly easily. I am currently running a JDK18 early access and it works there. I had to take some care not to do any logging before the filesystem bootstrapping was complete. Logging involves obtaining dates which end up getting into service loader code and resource bundles which error if done before bootstrapping is complete. 

The main code concerned is the DefaultFileSystemHolder class embedded in FileSystems. That makes two calls into the FileSystemProvider that must complete before logging can be done. 

There are other issues where it seems to accumulate extra logger instances causing duplicate messages.

To follow would be getting the logging into more classes. Currently it is only FileSystemProvider, FileSystem and Path. Also, possibly getting it to work with other FileSystemProvider's with different scheme's. There are also FileSystemProviders with different FileSystem's for different URI's. Like jarfs it appears. It would have to work with that without duplicating log messages.

