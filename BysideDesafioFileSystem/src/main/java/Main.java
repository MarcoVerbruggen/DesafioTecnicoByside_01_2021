import ood.fs.IMyFileSystem;
import ood.fs.MyFileSystemProvider;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //create Scanner to read input
        Scanner sc = new Scanner(System.in);

        //create file system
        MyFileSystemProvider fsp = new MyFileSystemProvider();
        IMyFileSystem fs = fsp.getMyFileSystem();

        //display console menu
        String path;
        String[] input = {"", "", "", ""};
        System.out.println("\nJava File System\nType 'help' for list of commands\n");
        do {
            //displays current working directory
            path = fs.getCurrentPath();
            System.out.print(path + ":");
            //scans input, divides command and parameters, assigns them in order to array positions
            String[] in = sc.nextLine().trim().split(" ");
            for(int i=0; i<in.length; i++) {
                input[i] = in[i];
            }
            //scans first character string of input and tries to match to a command
            switch (input[0]) {
                case "help":
                    System.out.println(
                            "Path specification follows standard convention (./ = current directory, ../ = parent directory, etc)\n" +
                            "Permissions are input in symbolic format (example: 'rwxr-xr--')\n\n" +
                            "Commands:\n" +
                            "login [username] [password]: Logs in as existing user. Must be logged out first.\n" +
                            "logout: Logs out current user, sets user to guest.\n" +
                            "newuser [username] [password] [user group name]: Creates a new user. " +
                                    "User group must already exist and cannot be admin or guest\n" +
                            "newgroup [groupname]: Creates new user group. Only admins may create new groups\n" +
                            "changedir [path]: Changes working directory to target. [path] must point to a directory. " +
                                    "User must have read permissions for target directory.\n"+
                            "newfile [filename] [optional: path] [optional:permission]: Creates a new file named [filename]. " +
                                    "If no path is specified or path does not point to a directory, " +
                                    "file is created in current working directory. " +
                                    "If no permissions are specified, uses default permission (rwxr-xr-x). " +
                                    "User must have write permission in target directory. " +
                                    "Executing the command allows input of file content, then creates file.\n" +
                            "newdir [filename] [optional:path] [optional:permission]: Creates a new empty directory. " +
                                    "Optional parameters and permission requirements equal to 'newfile' commmand.\n" +
                            "read [filename OR path]: If using [filename] parameter, reads matching file in current working directory. " +
                                    "If using [path] parameter, reads file at destination specified by path. " +
                                    "User must have read permissions in target file. " +
                                    "If target is a directory, displays list of files and directories in the target directory " +
                                    "rather than file contents.\n" +
                            "edit [filename OR path]: Changes file contents of the target. Parameters equal to 'read' command. " +
                                    "User must have write permissions for target file. " +
                                    "Only works on files, not directories.\n" +
                            "delete [filename OR path]: Removes target from filesystem. If target is a non-empty directory, " +
                                    "system will ask to cancel the command or delete all contents of target directory. " +
                                    "User must have write and execute permissions on target and all files affected.\n" +
                            "move [target filename OR target path] [destination path]: Moves target to destination. " +
                                    "If target is not specified by path, looks for target in current working directory. " +
                                    "Destination must be a path to a directory. " +
                                    "If target is a non-empty directory, system asks user to cancel or move all contents " +
                                    "of the target directory along with it. " +
                                    "User must have write and execute permissions on target and all affected files, " +
                                    "and write permission on destination directory.\n" +
                            "permission [filename OR path] [permission in symbolic format]: Change permissions on target " +
                                    "to be input permissions. User must have write permission on target to change permissions.\n" +
                            "rename [filename OR path] [new name]: Change name of target. User must have write permission on target.\n" +
                            "newadmin [username] [password]: Creates new user in admin group. Command restricted to existing admins.\n" +
                            "exit: exits the filesystem, ends program.\n"
                    );
                    resetInput(input);
                    break;
                case "login":
                    fs.login(input[1], input[2]);
                    resetInput(input);
                    break;
                case "logout":
                    fs.logout();
                    resetInput(input);
                    break;
                case "newuser":
                    fs.newUser(input[1], input[2], input[3]);
                    resetInput(input);
                    break;
                case "newgroup":
                    fs.newUserGroup(input[1]);
                    resetInput(input);
                    break;
                case "changedir":
                    fs.changeDirectory(input[1]);
                    resetInput(input);
                    break;
                case "newfile":
                    System.out.println("Contents:");
                    String content = sc.nextLine();
                    fs.newFile(input[1], input[2], input[3], content);
                    resetInput(input);
                    break;
                case "newdir":
                    fs.newDirectory(input[1], input[2], input[3]);
                    resetInput(input);
                    break;
                case "read":
                    System.out.println(fs.readFile(input[1]));
                    resetInput(input);
                    break;
                case "edit":
                    if(fs.isFile(input[1])) {
                        fs.readFile(input[1]);
                        System.out.println("New Contents:\n");
                        String newContent = sc.nextLine();
                        fs.editFile(input[1], newContent);
                    } else {
                        System.out.println("Target is a directory, must be file.\n");
                    }
                    resetInput(input);
                    break;
                case "delete":
                    if(fs.isNonEmptyDir(input[1])) {
                        System.out.println("Target is a non-empty directory. Input 1 to cancel or 2 " +
                                "to delete all contained files and directories.\n");
                        String option = sc.nextLine();
                        fs.deleteINode(input[1], option);
                    } else {
                        //choice can only be null if target is not a non-empty directory
                        fs.deleteINode(input[1], null);
                    }
                    resetInput(input);
                    break;
                case "move":
                    if(fs.isNonEmptyDir(input[1])) {
                        System.out.println("Target is a non-empty directory. Input 1 to cancel or 2 " +
                                "to move all contained files and directories.\n");
                        String option = sc.nextLine();
                        fs.moveINode(input[1], input[2], option);
                    } else {
                        //choice can only be null if target is not a non-empty directory
                        fs.moveINode(input[1], input[2], null);
                    }
                    resetInput(input);
                    break;
                case "permission":
                    fs.changePermission(input[1], input[2]);
                    resetInput(input);
                    break;
                case "rename":
                    fs.changeName(input[1], input[2]);
                    resetInput(input);
                    break;
                case "newadmin":
                    fs.newAdmin(input[1], input[2]);
                    resetInput(input);
                    break;
                case "exit":
                    System.out.println("Exitting...");
                    break;
                default:
                    System.out.println(input[0] + " command not found");
                    resetInput(input);
                    break;
            }
        } while (!input[0].equalsIgnoreCase("exit"));

    }

    private static void resetInput(String[] input) {
        input[0]="";
        input[1]="";
        input[2]="";
        input[3]="";
    }
}
