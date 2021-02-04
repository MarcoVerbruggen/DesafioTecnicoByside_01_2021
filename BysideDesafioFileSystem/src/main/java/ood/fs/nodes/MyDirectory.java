package ood.fs.nodes;

import ood.fs.users.MyUser;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class MyDirectory extends MyNode {

    private ArrayList<MyNode> children;

    public MyDirectory(String permission, ZonedDateTime created, MyUser author, MyDirectory parent, String filename, String path) {
        super(permission, created, author, parent, filename, path);
        children=new ArrayList<>();
    }

    public ArrayList<MyNode> getChildren() {
        return children;
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public long calcSize() {
        long size = 0;
        for(MyNode n : children) {
            size += n.calcSize();
        }
        return size;
    }

    public void updateChildrenPath() {
        for(MyNode n : children) {
            n.setPath(this.getPath()+this.getFilename()+"/");
            if(n.isDir()) {
                ((MyDirectory) n).updateChildrenPath();
            }
        }
    }

}
