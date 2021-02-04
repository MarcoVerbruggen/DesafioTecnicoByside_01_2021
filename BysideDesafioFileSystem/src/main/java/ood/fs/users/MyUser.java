package ood.fs.users;

import java.util.ArrayList;
import java.util.Objects;

public class MyUser {

    private String username;
    private String password;
    private ArrayList<MyGroup> userGroups;

    public MyUser(String username, String password, MyGroup userGroup) {
        this.username=username;
        this.password=password;
        userGroups = new ArrayList<>();
        userGroups.add(userGroup);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public ArrayList<MyGroup> getUserGroups() {
        return userGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyUser myUser = (MyUser) o;
        return username.equals(myUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
