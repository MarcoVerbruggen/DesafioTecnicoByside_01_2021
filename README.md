# DesafioTecnicoByside_01_2021

Perfil de utilizador admin, instanciado pelo sistema:
Username: admin
Password: adminpass

Comando "help" na aplicação apresenta a seguinte informação:

    Path specification follows standard convention (./ = current directory, ../ = parent directory, etc)
    Permissions are input in symbolic format (example: 'rwxr-xr--')

    Commands:
    login [username] [password]: Logs in as existing user. Must be logged out first.
    logout: Logs out current user, sets user to guest.
    newuser [username] [password] [user group name]: Creates a new user. User group must already exist and cannot be admin or guest
    newgroup [groupname]: Creates new user group. Only admins may create new groups
    changedir [path]: Changes working directory to target. [path] must point to a directory. User must have read permissions for target directory.
    newfile [filename] [optional: path] [optional:permission]: Creates a new file named [filename]. If no path is specified or path does not point to a directory, file is created in current working directory. If no permissions are specified, uses default permission (rwxr-xr-x). User must have write permission in target directory. Executing the command allows input of file content, then creates file.
    newdir [filename] [optional:path] [optional:permission]: Creates a new empty directory. Optional parameters and permission requirements equal to 'newfile' commmand.
    read [filename OR path]: If using [filename] parameter, reads matching file in current working directory. If using [path] parameter, reads file at destination specified by path. User must have read permissions in target file. If target is a directory, displays list of files and directories in the target directory rather than file contents.
    edit [filename OR path]: Changes file contents of the target. Parameters equal to 'read' command. User must have write permissions for target file. Only works on files, not directories.
    delete [filename OR path]: Removes target from filesystem. If target is a non-empty directory, system will ask to cancel the command or delete all contents of target directory. User must have write and execute permissions on target and all files affected.
    move [target filename OR target path] [destination path]: Moves target to destination. If target is not specified by path, looks for target in current working directory. Destination must be a path to a directory. If target is a non-empty directory, system asks user to cancel or move all contents of the target directory along with it. User must have write and execute permissions on target and all affected files, and write permission on destination directory.
    permission [filename OR path] [permission in symbolic format]: Change permissions on target to be input permissions. User must have write permission on target to change permissions.
    rename [filename OR path] [new name]: Change name of target. User must have write permission on target.
    newadmin [username] [password]: Creates new user in admin group. Command restricted to existing admins.
    exit: exits the filesystem, ends program.

Implementação de sistema de ficheiros em design OO, linguagem Java
Quase todas as funcionalidades requerem que o utilizador esteja logged in no sistema como utilizador registado, várias requerem que o utilizador seja administrador
Requerimentos eram vagos no que toca a funcionalidade necessária. Em vez de uma implementação totalmente funcional, utilizando por exemplo java.nio.FileSystem e java.nio.FileSystemProvider, optei por criar uma aplicação simples para por enfase no design OO e construção de código.
Gostaria de ter implementado mais funcionalidades (possibilidade de vários diretórios root para simular diferentes file stores, utilizadores poderem pertencer a diversos grupos, alterar credenciais de utilizadores, persistir o sistema entre usos da aplicação) e polido mais o projeto, mas entre restrição de tempo, e querer evitar bloating, decidi restringir-me a estas funcionalidades finais.
