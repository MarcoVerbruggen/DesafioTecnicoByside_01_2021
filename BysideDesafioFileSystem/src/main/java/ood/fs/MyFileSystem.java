package ood.fs;

import ood.fs.nodes.MyDirectory;
import ood.fs.nodes.MyFile;
import ood.fs.nodes.MyNode;
import ood.fs.users.MyGroup;
import ood.fs.users.MyUser;

import java.util.regex.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class MyFileSystem implements IMyFileSystem {

    private ArrayList<MyDirectory> roots = new ArrayList<>();
    private ArrayList<MyUser> users = new ArrayList<>();
    private ArrayList<MyGroup> groups = new ArrayList<>();
    private MyDirectory currentDir;
    private MyUser currentUser;

    //real system would persist all files and directories in physical memory. This creates system from scratch
    public MyFileSystem() {
        //admin and guest users created. guest has no password as it is a profile used to login as other users only
        MyGroup adminGroup = new MyGroup("admin");
        MyGroup guestGroup = new MyGroup("guest");
        MyUser admin = new MyUser("admin", "adminpass", adminGroup);
        MyUser guest = new MyUser("guest", "", guestGroup);

        //create root directory and set current user as guest
        groups.add(adminGroup);
        groups.add(guestGroup);
        users.add(admin);
        users.add(guest);
        MyDirectory adminRoot = new MyDirectory("rwxr-xr-x", ZonedDateTime.now(), admin, null, "/", "");
        roots.add(adminRoot);
        currentDir = adminRoot;
        currentUser = guest;
    }

    //checks if current user has read permission for target node
    private boolean checkReadPermission(MyNode n) {
        //admins always have read permission
        if(checkAdminGroup()) return true;

        //finds owner of target node
        MyUser a = n.getOwner();
        //finds user group of owner of target node
        ArrayList<MyGroup> gs = a.getUserGroups();
        //finds permissions of target node
        String permission = n.getPermission();

        //gets user (owner) read permission
        String userPerm = Character.toString(permission.charAt(0));
        //checks if owner has read permission
        //if not, don't waste time checking if user is owner
        if(userPerm.equals("r")) {
            //checks if current user is owner
            if(currentUser.equals(a)) return true;
        }

        //gets group read permission
        String groupPerm = Character.toString(permission.charAt(3));
        //check if groups have read permission
        //if not, don't waste time checking if current user is in groups
        if(groupPerm.equals("r")) {
            //for each group the owner belongs to...
            for (MyGroup g : gs) {
                //... checks if user belongs to group
                if (currentUser.getUserGroups().contains(g)) return true;
            }
        }

        //gets others read permission
        String otherPerm = Character.toString(permission.charAt(6));
        //checks if others have read permission
        if(otherPerm.equals("r")) return true;

        //if none of these conditions are met, user does not have read premission
        return false;
    }

    //checks if current user has write permission for target node
    private boolean checkWritePermission(MyNode n) {
        //admins always have write permission
        if(checkAdminGroup()) return true;

        //finds owner of target node
        MyUser a = n.getOwner();
        //finds user group of owner of target node
        ArrayList<MyGroup> gs = a.getUserGroups();
        //finds permissions of target node
        String permission = n.getPermission();

        //gets user (owner) write permission
        String userPerm = Character.toString(permission.charAt(1));
        //checks if owner has write permission
        //if not, don't waste time checking if user is owner
        if(userPerm.equals("w")) {
            //checks if current user is owner
            if(currentUser.equals(a)) return true;
        }

        //gets group write permission
        String groupPerm = Character.toString(permission.charAt(4));
        //check if groups have write permission
        //if not, don't waste time checking if current user is in groups
        if(groupPerm.equals("w")) {
            //for each group the owner belongs to...
            for (MyGroup g : gs) {
                //... checks if current user belongs to group
                if (currentUser.getUserGroups().contains(g)) return true;
            }
        }

        //gets others write permission
        String otherPerm = Character.toString(permission.charAt(7));
        //checks if others have write permission
        if(otherPerm.equals("w")) return true;

        //if none of these conditions are met, user does not have write premission
        return false;
    }

    //checks if current user has execute permission for target node
    private boolean checkExecPermission(MyNode n) {
        //admins always have execute permission
        if(checkAdminGroup()) return true;

        //finds owner of target node
        MyUser a = n.getOwner();
        //finds user group of owner of target node
        ArrayList<MyGroup> gs = a.getUserGroups();
        //finds permissions of target node
        String permission = n.getPermission();

        //gets user (owner) execute permission
        String userPerm = Character.toString(permission.charAt(2));
        //checks if owner has execute permission
        //if not, don't waste time checking if user is owner
        if(userPerm.equals("x")) {
            //checks if current user is owner
            if(currentUser.equals(a)) return true;
        }

        //gets group execute permission
        String groupPerm = Character.toString(permission.charAt(5));
        //check if groups have execute permission
        //if not, don't waste time checking if current user is in groups
        if(groupPerm.equals("x")) {
            //for each group the owner belongs to...
            for (MyGroup g : gs) {
                //... checks if current user belongs to group
                if (currentUser.getUserGroups().contains(g)) return true;
            }
        }

        //gets others execute permission
        String otherPerm = Character.toString(permission.charAt(8));
        //checks if others have execute permission
        if(otherPerm.equals("x")) return true;

        //if none of these conditions are met, user does not have execute premission
        return false;
    }

    //checks if the next step of the path is the root directory ("/")
    private boolean isAbsolutePath(String path) {
        if(path.startsWith("/")) return true;
        /*
        String r = "/";
        if(path.charAt(0)==r.charAt(0)) return true;
         */
        return false;
    }

    //checks if the next step of the path is the current directory ("./")
    private boolean isCurrentDir(String path) {
        if(path.startsWith("./")) return true;
        return false;
    }

    //checks if the next step in the path is empty
    private boolean isEmptyPath(String path) {
        if(path.equals("")) return true;
        return false;
    }

    //checks if the next step of the path is the parent directory of the current directory ("../");
    private boolean isParentDir(String path) {
        if(path.startsWith("../")) return true;
        return false;
    }

    //follows a path until the destination is reached
    //recursive method
    //if destination is a directory, returns that directory
    //if destination is a file, returns that file's parent directory
    //if destination is not found, returns the current working directory (original directory)
    private MyNode followPath(String path, MyDirectory currentStep) {
        //starts at root if it is an absolute path
        if(isAbsolutePath(path)) {
            String nextPath = path.substring(1);
            return followPath(nextPath, roots.get(0));
        }

        //starts at current directory if current step is "./"
        if(isCurrentDir(path)) {
            String nextPath = path.substring(2);
            return followPath(nextPath, currentDir);
        }

        //moves to parent directory if current step is "../"
        if(isParentDir(path)) {
            String nextPath = path.substring(3);
            return followPath(nextPath, currentStep.getParent());
        }

        //returns current step if next step in path is empty (example: full path = ./ => returns current directory)
        if(isEmptyPath(path)) {
            return currentStep;
        }

        //looks for target if next step is a directory or file
        String[] pathSteps = path.trim().split("/");
        for(MyNode n : currentStep.getChildren()) {

            //if target is found and is directory...
            if(n.getFilename().equals(pathSteps[0]) && n.isDir()) {
                //... and there are no further steps in the path, target directory has been found
                if(pathSteps.length==1) {
                    //end of the path has been reached, return node n
                    return n;
                //... and there are further steps in the path, move into target and continue
                } else {
                    String nextPath = path.substring(pathSteps[0].length()+1);
                    return followPath(nextPath, (MyDirectory) n);
                }

            //if target is found and is a file, the end of the path has been reached
            } else if (n.getFilename().equals(pathSteps[0]) && !n.isDir()) {
                return currentStep;
            }
        }

        //returns null if path destination is not found
        System.out.println("Path Invalid - Target directory or file not found\n");
        return null;
    }

    //checks if current user belongs to Admin user group
    private boolean checkAdminGroup() {
        return currentUser.getUserGroups().contains(groups.get(0));
    }

    //checks if username and password are valid
    private boolean verifyUsernamePassword(String username, String password) {
        //usernames and passwords must be strings of 3 to 12 alphanumeric characters
        boolean valid = Pattern.matches("[A-Za-z0-9]{3,12}", username);
        if(!valid) {
            System.out.println("Username Invalid\n");
            return false;
        }

        valid = Pattern.matches("[A-Za-z0-9]{3,12}", password);
        if (!valid) {
            System.out.println("Password Invalid\n");
            return false;
        }

        //A real system would use a unique user code as ID rather than a username.
        // For simplicity, username is used as ID here, so all usernames must be unique in the system
        for(MyUser u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("Username already taken\n");
                return false;
            }
        }
        return true;
    }

    //checks if permission string is valid by matching to regular expression
    //file permission must be in symbolic format (example: rwxr-xr-x)
    private boolean verifyPermission(String permission) {
        if(Pattern.matches("^(((r|-)(w|-)(x|-)){3})$", permission)) {
            System.out.println("Permission code is not valid\n");
            return false;
        } else return true;
    }

    //checks if filename is valid
    //filename must be 1 or more characters and cannot contain: \ / : * ? < > |
    private boolean verifyFilename(String filename) {
        //if filename is not empty, and filename matches any sequence of characters except illegal characters, return true
        if(!filename.equals("") && Pattern.matches("^[^?*|<>:/\"\\ ]*$", filename) ) return true;

        //if filename does not pass check, return false
        System.out.println("Filename must be more than 0 characters, cannot contain: \\ / : * ? < > |\n");
        return false;
    }

    //checks if node is safe to be deleted
    //recursive method
    private boolean deleteINode(MyNode node) {
        //if node is a file, can be deleted, otherwise check children of node
        if(node.isDir()) {
            ArrayList<MyNode> list = ((MyDirectory) node).getChildren();
            for (MyNode n : list) {
                //check if user has write/execute permissions for child, check if child is safe to delete (recursive call)
                if(!checkWritePermission(n) || !checkExecPermission(n) || !deleteINode(n)) return false;
            }
        }

        return true;
    }

    @Override
    public String getCurrentPath() {
        return currentDir.getPath()+currentDir.getFilename();
    }

    //changes current working directory to target
    @Override
    public void changeDirectory(String path) {
        //finds destination node of given path
        MyNode destination = followPath(path, currentDir);
        if(destination == null) return;

        //checks if destination is a directory. If not, don't waste time checking permissions
        if(destination.isDir()) {
            //checks if current user has permission to access directory.
            //if so, set current working directory to destination, otherwise deny access and maintain current working directory
            if(checkReadPermission(destination)) {
                currentDir = (MyDirectory) destination;
                currentDir.setAccessed(ZonedDateTime.now());
            } else {
                System.out.println("You do not have read permission for this directory\n");
            }
            return;
        }

        //if destination was a file it cannot be set as current working directory
        System.out.println("Destination must be a valid directory\n");
        return;
    }

    //checks if the destination of a path is a file
    @Override
    public boolean isFile(String filename) {
        //finds destination node
        MyNode destination = followPath(filename, currentDir);
        if(destination == null) return false;
        //returns true if it is not a directory, false if it is a directory
        return !destination.isDir();
    }

    //checks if the destination of a path is a non-empty directory
    @Override
    public boolean isNonEmptyDir(String filename) {
        //finds destination
        MyNode destination = followPath(filename, currentDir);
        if(destination == null) return false;
        //checks if it is a directory
        if(destination.isDir()) {
            //returns true if it is not empty, false if it is empty
            return !((MyDirectory) destination).getChildren().isEmpty();
        }
        //returns false if it is not a directory
        return false;
    }

    //creates a new user using existing user group
    @Override
    public void newUser(String username, String password, String groupname) {
        MyGroup group = new MyGroup(groupname);

        //checks validity of username and password
        if(!verifyUsernamePassword(username, password)) return;

        //checks if new user is trying to be assigned to admin group.
        //only admins may create admins via a different method
        if(groups.get(0).equals(group)){
            System.out.println("Admin Users must be created by other Admins\n");
            return;
        }
        //checks if new user is trying to be assigned to guest group.
        //only one guest exists in the system for purposes of login/logout
        if(groups.get(1).equals(group)){
            System.out.println("Additional Guest users cannot be created\n");
            return;
        }

        //checks if new user's group exists in the system.
        if(!groups.contains(group)) {
            System.out.println("User Group not registered in System\n");
            return;
        }

        //if all checks pass, adds new user
        users.add(new MyUser(username, password, group));
        System.out.println("User added\n");
    }

    //creates new user group
    @Override
    public void newUserGroup(String groupname) {
        //checks if current user is system admin. only admins may create new groups
        if(!checkAdminGroup()) {
            System.out.println("Only System Admin group may create new User Groups\n");
            return;
        }

        //like username, a unique code should be used for identifying user groups, but name is used here for simplicity
        //user group name must then be unique in system
        for(MyGroup g : groups) {
            if(g.getGroupname().equals(groupname)) {
                System.out.println("User group already exists\n");
                return;
            }
        }

        //if all checks pass, adds new user group
        groups.add(new MyGroup(groupname));
        System.out.println("User Group Added\n");
    }

    //logout function
    @Override
    public void logout() {
        //sets current user to guest
        currentUser = users.get(1);
        System.out.println("Logged out, now set as Guest User\n");
    }

    //login function
    @Override
    public void login(String username, String password) {
        MyUser user = null;

        //must be logged out before logging in
        if(!(currentUser.getUserGroups().contains(groups.get(1)))) {
            System.out.println("Must logout before logging in as a different user\n");
            return;
        }
        //checks if intended login exists
        for(MyUser u : users) {
            if(u.getUsername().equals(username)) {
                user = u;
            }
        }

        //if target user does not exist, login fails
        if(user == null) {
            System.out.println("User does not exist\n");
            return;
        }

        //if input password does not match user password, login fails, otherwise log in as user
        if(user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Logged in as User "+user.getUsername()+"\n");
        } else {
            System.out.println("Incorrect Password\n");
        }
    }

    //creates new file in target directory
    //filename = name of the new file
    //path = path to target directory where file should be created. If empty, create file in current working directory
    //permission = permission code for new file. Must be in symbolic format. If empty, use default permissions "rwxr-xr-x"
    //content = content of the file. File content restricted to simple strings for simplicity in this system
    @Override
    public void newFile(String filename, String path, String permission, String content) {
        //if filename is not valid, return
        if(!verifyFilename(filename)) return;

        //set destination directory to current directory
        MyNode destination = currentDir;

        //if no permission is input, use default permission (rwxr-xr-x)
        if(!permission.equals("")) {
            //verifies permission is valid
            if (!verifyPermission(permission)) return;
        } else {
            permission = "rwxr-xr-x";
        }

        //if a path is input, set destination directory to the one pointed to by the path
        if(!path.equals("")) {
            destination = followPath(path, currentDir);
            if(destination == null) return;
            if(!destination.isDir()) {
                System.out.println("Command Cancelled - Path must point to a directory, " +
                        "or be left empty to create file in current directory\n");
                return;
            }
        }

        //file filename must be unique in directory
        for(MyNode n : ((MyDirectory) destination).getChildren()) {
            if(n.getFilename().equals(filename)) {
                System.out.println("File name must be unique in directory\n");
                return;
            }
        }

        //create path. If parent is root, do not add aditional '/' at the end.
        String newpath = destination.getPath()+destination.getFilename();
        if(!roots.contains(destination)) newpath+="/";

        //creates new file and adds it to destination directory's children
        ZonedDateTime time = ZonedDateTime.now();
        ((MyDirectory) destination).getChildren().add(new MyFile(permission, time, currentUser,
                ((MyDirectory) destination), filename, newpath, content));
        //changes last update time of destination directory
        destination.setUpdated(time);

    }

    //creates a new directory in target directory
    //filename = name of the new directory
    //path = path to target directory where directory should be created. If empty, create directory in current working directory
    //permission = permission code for new directory. Must be in symbolic format. If empty, use default permissions "rwxr-xr-x"
    @Override
    public void newDirectory(String filename, String path, String permission) {
        //if filename is not valid, return
        if(!verifyFilename(filename)) return;

        //set destination directory to current directory
        MyNode destination = currentDir;

        //if no permission is input, use default permission (rwxr-xr-x)
        if(!permission.equals("")) {
            //verifies permission is valid
            if (!verifyPermission(permission)) return;
        } else {
            permission = "rwxr-xr-x";
        }

        //if a path is input, set destination directory to the one pointed to by the path
        if(!path.equals("")) {
            destination = followPath(path, currentDir);
            if(destination == null) return;
            if(!destination.isDir()) {
                System.out.println("Command Cancelled - Path must point to a directory, " +
                        "or be left empty to create file in current directory\n");
                return;
            }
        }

        //directory filename name must be unique in directory
        for(MyNode n : ((MyDirectory) destination).getChildren()) {
            if(n.getFilename().equals(filename)) {
                System.out.println("Directory name must be unique in directory\n");
                return;
            }
        }

        //create path. If parent is root, do not add aditional '/' at the end.
        String newpath = destination.getPath()+destination.getFilename();
        if(!roots.contains(destination)) newpath+="/";

        //creates new empty directory and adds it to destination directory's children
        ZonedDateTime time = ZonedDateTime.now();
        MyDirectory dir = new MyDirectory(permission, time, currentUser, ((MyDirectory) destination), filename, newpath);
        ((MyDirectory) destination).getChildren().add(dir);
        //changes last update time of destination directory
        destination.setUpdated(time);
    }

    //returns the contents of a file
    //filename = name of the target file in current directory or path to file in another directory
    @Override
    public String readFile(String filename) {
        //check if input is valid
        if(filename.equals("")) {
            return "Command Canceled - no file name or path received\n";
        }
        String ret = "";

        //find target file and check permission
        MyNode destination = followPath(filename, currentDir);
        if(destination == null) return "File not found\n";
        if(!checkReadPermission(destination)) return "You do not have permission to access this file\n";

        //mark current time for updating node information
        ZonedDateTime time = ZonedDateTime.now();

        //if destination is directory...
        if(destination.isDir()) {
            MyDirectory dir = (MyDirectory) destination;
            //update access time
            dir.setAccessed(time);
            for(MyNode n : dir.getChildren()) {
                if(n.isDir() && checkReadPermission(n))
                    ret+=n.getFilename()+" | Directory | "+n.calcSize()+" bytes | owner: "+n.getOwner().getUsername()+"\n";
                if (!n.isDir() && checkReadPermission(n))
                    ret+=n.getFilename()+" | File | "+n.calcSize()+" bytes | owner: "+n.getOwner().getUsername()+"\n";
            }
            if(ret.equals("")) return "Directory is empty, or you are not authorized to view its contents\n";
            return ret+="\n";
        }
        destination.setAccessed(time);
        return ((MyFile) destination).getContent();
    }

    //replaces current contents of a file with new content
    //only usable on files, not directories
    //filename parameter can be name of a file in current working directory, or path to file elsewhere
    @Override
    public void editFile(String filename, String content) {
        //find target file and check permission
        MyFile destination = (MyFile) followPath(filename, currentDir);
        if(destination == null) return;
        if(!checkWritePermission(destination)) {
            System.out.println("You do not have permission to write to this file\n");
            return;
        }

        ZonedDateTime time = ZonedDateTime.now();
        //update content
        destination.setContent(content);
        //change access and update times
        destination.setUpdated(time);
        destination.setAccessed(time);
        //updating file in a directory updates the directory, as its contents have been, indirectly, changed
        currentDir.setUpdated(time);
    }

    //deletes node
    //if node is a non-empty directory, cancels command or deletes all children
    //filename parameter can be name of a file in current working directory, or path to file elsewhere
    @Override
    public void deleteINode(String filename, String option) {
        //cannot delete root
        if(filename.equals("/")) {
            System.out.println("Cannot delete root directory");
            return;
        }

        //check if option is valid
        if(!(option == null || option.equals("1") || option.equals("2") )) {
            System.out.println("Invalid Option - Please input option '1' or '2', as indicated, for handling non-empty directories\n");
            return;
        }

        //find file and check permissions
        MyNode destination = followPath(filename, currentDir);
        if(destination == null) return;
        if(!checkWritePermission(destination) || !checkExecPermission(destination)) {
            System.out.println("Must have write and execute permission to delete a file or directory\n");
            return;
        }

        //delete file
        MyDirectory d = destination.getParent();
        if(!destination.isDir()) {
            d.getChildren().remove(destination);
            d.setUpdated(ZonedDateTime.now());
            System.out.println("File Deleted\n");
            return;
        }
        //cancel if option 1
        if(option.equals("1")) {
            System.out.println("Cancelling command\n");
            return;
        }

        ArrayList<MyNode> list = ((MyDirectory) destination).getChildren();
        //delete all children if option 2
        if(option.equals("2")) {

            //for each child n of destination...
            for(MyNode n : list) {
                //... check if user has write/execute permission, and check if all children of n are safe to delete
                if(!checkWritePermission(n) || !checkExecPermission(n) || !deleteINode(n)) {
                    //if any child or sub-child of destination is not safe to delete, cancel command
                    System.out.println("Cancelling Command - You do not have permission to delete a file " +
                            "or sub-directory under the target\n");
                    return;
                }
            }

            //remove destination and all nodes under it, update parent of destination
            d.getChildren().remove(destination);
            d.setUpdated(ZonedDateTime.now());
            System.out.println("Directory Deleted - Contents deleted\n");
            return;
        }

    }

    //moves node to new location
    //if node is a non-empty directory, cancels command or moves all children
    //filename parameter can be name of a file in current working directory, or path to file elsewhere
    //path parameter must be path to another directory
    @Override
    public void moveINode(String filename, String path, String option) {
        //cannot move root
        if(filename.equals("/")) {
            System.out.println("Cannot move root directory");
            return;
        }

        //check if option is valid
        if(!(option == null || option.equals("1") || option.equals("2"))) {
            System.out.println("Invalid Option - Please input option '1' or '2', as indicated, for handling non-empty directories\n");
            return;
        }

        //find file and check permissions
        MyNode target = followPath(filename, currentDir);
        if(target == null) return;
        if(!checkWritePermission(target) || !checkExecPermission(target)) {
            System.out.println("Must have write and execute permission to move a file or directory\n");
            return;
        }

        //find destination and check permissions
        MyNode destination = followPath(path, currentDir);
        if(destination == null) return;
        if(!destination.isDir()) {
            System.out.println("Destination must be a directory\n");
            return;
        }
        if(!checkWritePermission(destination)) {
            System.out.println("Must have write permission on destination directory\n");
            return;
        }

        //move file
        MyDirectory d = target.getParent();
        MyDirectory dest = (MyDirectory) destination;
        ZonedDateTime time;
        if(!target.isDir()) {
            //remove target from its parent's children and change parent's update time
            d.getChildren().remove(target);
            time = ZonedDateTime.now();
            d.setUpdated(time);
            //add target to destination's children and change destination's update time
            dest.getChildren().add(target);
            dest.setUpdated(time);
            //update path of file
            target.setPath(dest.getPath()+dest.getFilename()+"/");
            System.out.println("File Moved\n");
            return;
        }
        //cancel if option 1
        if(option.equals("1")) {
            System.out.println("Cancelling command\n");
            return;
        }

        ArrayList<MyNode> list = ((MyDirectory) target).getChildren();
        //move all children if option 2
        if(option.equals("2")) {

            //for each child n of target...
            for(MyNode n : list) {
                //... check if user has write/execute permission, and check if all children of n are safe to move
                //the conditions for whether or not a node is safe to move are the same as whether or not it is safe to delete
                //in fact, in real systems, moving is just deleting and creating a copy in a new location
                if(!checkWritePermission(n) || !checkExecPermission(n) || !deleteINode(n)) {
                    //if any child or sub-child of target is not safe to move, cancel command
                    System.out.println("Cancelling Command - You do not have permission to move a file " +
                            "or sub-directory under the target\n");
                    return;
                }
            }

            time = ZonedDateTime.now();
            //remove target and all nodes under it from its parent's children, update parent of target
            d.getChildren().remove(target);
            d.setUpdated(time);
            //add target and all nodes under it to destination's children, change destination's update time
            dest.getChildren().add(target);
            dest.setUpdated(time);

            //create path. If parent is root, do not add aditional '/' at the end.
            String newpath = dest.getPath()+dest.getFilename();
            if(!roots.contains(dest)) newpath+="/";

            //update path of target node and all nodes under it
            target.setPath(newpath);
            ((MyDirectory)target).updateChildrenPath();
            System.out.println("Directory Moved - Contents moved\n");
            return;
        }
    }

    //changes permission of file
    //permission must be in symbolic format (example: rwxr-x-w-)
    //only file owner or admin may change permission
    @Override
    public void changePermission(String filename, String permission) {
        //check if new permissions are valid
        if(!verifyPermission(permission)) return;

        //find file
        MyNode target = followPath(filename, currentDir);
        if(target == null) return;

        //checks if current user is file owner, only owner or admin can change permissions
        MyUser owner = target.getOwner();
        if(!(currentUser.equals(owner) || checkAdminGroup())) {
            System.out.println("Cancelling Commands - Only file owner and admin users may edit file permissions\n");
        }

        //if all checks are passed, change file permissions
        target.setPermission(permission);
        //change file update time
        target.setUpdated(ZonedDateTime.now());
    }

    //changes filename
    //requires write permission
    @Override
    public void changeName(String filename, String newname) {
        //check if new name is valid
        if(!verifyFilename(newname)) return;

        //finds target
        MyNode target = followPath(filename, currentDir);
        if(target == null) return;

        //checks if user has write permission
        if(!checkWritePermission(target)) {
            System.out.println("Must have write perission on target file to change its name");
            return;
        }

        //change filename. If node is directory, update path of all its children
        target.setFilename(newname);
        if(target.isDir()) {
            ((MyDirectory) target).updateChildrenPath();
        }

        //change update time of target
        target.setUpdated(ZonedDateTime.now());

    }

    //creates a new user in admin group
    //username and password must be valid
    //can only be used by other admins
    @Override
    public void newAdmin(String username, String password) {
        //checks validity of username and password
        if(!verifyUsernamePassword(username, password)) return;

        //checks if current user is admin
        if(!checkAdminGroup()) {
            System.out.println("Only Admins may use this command\n");
            return;
        }

        //if all checks pass, adds new user
        users.add(new MyUser(username, password, groups.get(0)));
        System.out.println("User added\n");
    }

}
