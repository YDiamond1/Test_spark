import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter url of Postgres DataBase:");
        String url = scanner.nextLine().replace("\n", "").replace(" ", "").replace("\t", "");
        System.out.print("Enter user:");
        String user = scanner.nextLine().replace("\n", "").replace(" ", "").replace("\t", "");
        System.out.println("And password:");
        String pass = scanner.nextLine().replace("\n", "").replace(" ", "").replace("\t", "");

        String ip = args.length!=0 ? args[0] : "";

        Application app = new Application(url, user, pass);
        app.service(ip);
    }
}
