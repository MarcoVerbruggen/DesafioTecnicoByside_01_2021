package ood.fs.nodes;

import ood.fs.users.MyUser;

import java.time.ZonedDateTime;

public class MyFile extends MyNode {

    private String content;

    public MyFile(String permission, ZonedDateTime created, MyUser author, MyDirectory parent,
                  String filename, String path, String content) {
        super(permission, created, author, parent, filename, path);
        this.content = content;
    }

    @Override
    public boolean isDir() {
        return false;
    }

    @Override
    public long calcSize() {
        return (long) content.getBytes().length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
