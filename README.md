# WrappingFSP

WrappingFSP is a DefaultFileSystemProvider meant to wrap a given FileSystemProvider around a target one. 

## Recent changes

Changes to sync with LoggingFSP and give it a simple ant build.

## Usage 

Currently to wrap a logging FileSystemProvider around a target one. 

The logging FileSystemProvider is a separate project [LoggingFSP](https://github.com/mik3hall/LoggingFSP). 

Invocation is something like...

```
java -Djava.nio.file.spi.DefaultFileSystemProvider=us.hall.fsp.wrap.WrapperFSP -DwrappedFSP=us.hall.trz.osx.MacFileSystemProvider -DlogParms=true -DlogReturn=true -cp . --module-path . --add-modules wrapperFSP,loggingFSP us.hall.fsp.Test
```
getting...

```
FSP test...
us.hall.fsp.wrap.WrapperFSP

us.hall.fsp.wrap.WrapperFS@9807454
file
Default FS us.hall.fsp.wrap.WrapperFS@9807454
[2021-10-24T19:45:51.901606] [FINER] entering us.hall.trz.osx.MacFileSystemProvider getFileSystem with file:/
[2021-10-24T19:45:51.906006] [FINER] exiting us.hall.trz.osx.MacFileSystemProvider getFileSystem returning us.hall.fsp.LoggingFS@7e6cbb7a
[2021-10-24T19:45:51.906115] [FINER] entering us.hall.trz.osx.MacFileSystem getPath with /Library/Java/JavaVirtualMachines/jdk-18.jdk/Contents/Home,[Ljava.lang.String;@7c3df479
[2021-10-24T19:45:51.906809] [FINER] exiting us.hall.trz.osx.MacFileSystem getPath returning /Library/Java/JavaVirtualMachines/jdk-18.jdk/Contents/Home/lib/modules
[2021-10-24T19:45:51.893496] [FINER] entering us.hall.trz.osx.MacFileSystem getPath with /Users/mjh,[Ljava.lang.String;@30dae81
[2021-10-24T19:45:51.910167] [FINER] exiting us.hall.trz.osx.MacFileSystem getPath returning /Users/mjh
[2021-10-24T19:45:51.910267] [FINER] entering us.hall.trz.osx.MacPath toAbsolutePath 
[2021-10-24T19:45:51.910322] [FINER] exiting us.hall.trz.osx.MacPath toAbsolutePath returning /Users/mjh
[2021-10-24T19:45:51.910437] [FINER] entering us.hall.trz.osx.MacPath isAbsolute 
[2021-10-24T19:45:51.910502] [FINER] exiting us.hall.trz.osx.MacPath isAbsolute returning true
/Users/mjh is absolute true
[2021-10-24T19:45:51.910605] [FINER] entering us.hall.trz.osx.MacPath toUri 
[2021-10-24T19:45:51.910707] [FINER] exiting us.hall.trz.osx.MacPath toUri returning file:///Users/mjh/
file:///Users/mjh/
```
The wrapped FileSystemProvider here is [trz](https://github.com/mik3hall/trz). Which is a DefaultFileSystemProvider of mine that provides some additional Mac FileAttributes. 

This works by making itself the DefaultFileSystemProvider. It makes the LoggingFileSystemProvider it's priorProvider. It makes the priorProvider for that the FileSystemProvider to be logged and the priorProvider for that is set to the platform provider. It is yet to be seen if that will work if the target provider isn't itself a DefaultFileSystemProvider but I think it should.

It misses some startup messages in the test case that the LoggingProvider against the platform provider shows. Some startup difference I haven't tracked down.
