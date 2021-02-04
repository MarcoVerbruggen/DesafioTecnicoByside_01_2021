package ood.fs;

import ood.fs.nodes.MyNode;
import ood.fs.users.MyGroup;

public interface IMyFileSystem {

    String getCurrentPath();

    void changeDirectory(String path);

    boolean isFile(String filename);

    boolean isNonEmptyDir(String filename);

    void newUser(String username, String password, String groupname);

    void newUserGroup(String groupname);

    void logout();

    void login(String username, String password);

    void newFile(String filename, String path, String permission, String content);

    void newDirectory(String filename, String path, String permission);

    String readFile(String filename);

    void editFile(String filename, String content);

    void deleteINode(String filename, String choice);

    void moveINode(String filename, String target, String option);

    void changePermission(String filename, String permission);

    void changeName(String filename, String newname);

    void newAdmin(String username, String password);

}
