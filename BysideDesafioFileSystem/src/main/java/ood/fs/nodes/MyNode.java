package ood.fs.nodes;

import ood.fs.users.MyUser;
import java.time.ZonedDateTime;
import java.util.Objects;

public abstract class MyNode {

    private String permission;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private ZonedDateTime accessed;
    private MyUser owner;
    private MyDirectory parent;
    private String filename;
    private String path;

    public MyNode(String permission, ZonedDateTime created, MyUser owner, MyDirectory parent, String filename, String path) {
        this.permission = permission;
        this.created = created;
        this.owner = owner;
        this.parent = parent;
        this.filename = filename;
        this.path = path;
        updated = created;
        accessed = created;
    }

    public abstract boolean isDir();

    public abstract long calcSize();

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public MyDirectory getParent() {
        return parent;
    }

    public MyUser getOwner() {
        return owner;
    }

    public String getPermission() {
        return permission;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public void setAccessed(ZonedDateTime accessed) {
        this.accessed = accessed;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyNode myNode = (MyNode) o;
        return parent.equals(myNode.parent) &&
                filename.equals(myNode.filename) &&
                path.equals(myNode.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, filename, path);
    }
}
