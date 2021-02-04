package ood.fs.users;

import java.util.Objects;

public class MyGroup {

    private String groupname;

    public MyGroup(String groupname) {
        this.groupname=groupname;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname=groupname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyGroup myGroup = (MyGroup) o;
        return groupname.equals(myGroup.groupname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupname);
    }
}
