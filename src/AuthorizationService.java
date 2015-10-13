import java.util.Scanner;
public class AuthorizationService {

    private UserStore userStore;

    public AuthorizationService(UserStore userStore) {
        this.userStore = userStore;
    }

    void startAuthorization()  {
        System.out.println("Type \"login\" or \"l\" to login. Type \"create\" or \"c\" to create new user. " +
                "Type \"q\" to quit");
        String line;
        Scanner scanner = new Scanner(System.in);
        line = scanner.nextLine();
        User user;
        switch(line.toLowerCase()) {
            case "l":
            case "login":
                user = login();
                if (user != null) {
                    work(user);
                }
                break;
            case "c":
            case "create":
                user = createUser();
                if (user != null) {
                    work(user);
                }
                break;
            case "q":
                close();
                break;
            default:
                System.out.println("Wrong input.");
                break;
        }
    }

    User login() {
        String name, pass;
        // 1. Ask for name
        Scanner scanner = new Scanner(System.in);
        System.out.print("Login:");
        name = scanner.nextLine();
        // 2. Ask for password
        System.out.print("Password:");
        pass = scanner.nextLine();
        // 3. Ask UserStore for user:  userStore.getUser(name, pass)
        User user = userStore.getUser(name, pass);
        if (user == null) {
            System.out.println("Wrong login or password.");
            return null;
        } else {
            return user;
        }
    }

    User createUser() {
        System.out.println("Creating new user...");
        System.out.print("Login: ");
        // 1. Ask for name
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        if (userStore.isUserExist(name)) {
            System.out.println("Failed to create new user: user with that name already exist");
            return null;
        }
        // 2. Ask for pass
        String pass;
        System.out.println("Note: Password must consist at least 5 characters.");
        System.out.print("Password:");
        pass = scanner.nextLine();
        //checking password length()
        if (pass.length() < 5) {
            System.out.println("Failed to create new user: password must consist at least 5 characters.");
            return null;
        }
        // 3. Add user to UserStore: userStore.addUser(user)
        User newUser = new User(name, pass);
        userStore.addUser(newUser);
        System.out.println("User " + name + " successfully created.");
        return newUser;
    }
    void work (User user) {
        System.out.println("Hello, " + user.getName());
        System.out.println("Type \"q\" to quit or \"logout\" to logout.");
        String line;
        while (true) {
            Scanner scanner = new Scanner(System.in);
            line = scanner.nextLine();
            switch(line.toLowerCase()) {
                case "q":
                    logout(user);
                    close();
                    break;
                case "logout":
                    logout(user);
                    return;
                default:
                    System.out.println("> " + line);
                    break;
            }
        }
    }
    void logout(User user) {
        System.out.println("Bye, " + user.getName());
    }
    void close() {
        System.out.println("Exit...");
        System.exit(0);
    }
    boolean isLogin() {
        return false;
    }
}
