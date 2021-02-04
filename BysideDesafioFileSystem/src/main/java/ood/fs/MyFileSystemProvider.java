package ood.fs;

public class MyFileSystemProvider {

    public IMyFileSystem getMyFileSystem() {
        return new MyFileSystem();
    }

    //other implementations of IMyFileSystem would go here

}
