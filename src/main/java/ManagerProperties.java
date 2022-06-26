import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Stream;

public class ManagerProperties {
    private static String url = "jdbc:mysql://localhost/parsedbatt?serverTimezone=Europe/Moscow&allowPublicKeyRetrieval=true&useSSL=false";

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String u) {
        if (u != null) {
            url = u;
        }
    }

    private static String username = "root";

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String u) {
        if (u != null) {
            username = u;
        }
    }

    private static String password = "rootroot";

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String p) {
        if (p != null) {
            password = p;
        }
    }

    public static void SelectProperties(Scanner scr) throws IOException {
        // Try set selected properties profile to DB access methods
        PrintProperties(scr);
        try (Stream<String> stream = Files.lines(Paths.get("properties"))) {
            String[] strArr = stream.toArray(String[]::new);
            System.out.println("Select profile or '0' for return");
            int exit = scr.nextInt();
            if (exit != 0) {
                String[] profile = strArr[exit - 1].split(" ");
                setUrl(profile[0]);
                setUsername(profile[1]);
                setPassword(profile[2]);

            }
        } catch (Exception ignored) {
        }
        Program.MenuProperties(scr);
    }

    public static void AddProperties(Scanner scr) throws IOException {
        // Try to write one new string profile to "Properties" file.

        try {
            StringBuilder sb = new StringBuilder();
            System.out.println("Input DB url");
            sb.append(scr.next());
            sb.append(" ");
            System.out.println("Input DB login");
            sb.append(scr.next());
            sb.append(" ");
            System.out.println("Input DB password");
            sb.append(scr.next());
            sb.append("\n");
            Writer writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("properties", true));
                writer.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            AddProperties(scr);
        }
        Program.MenuProperties(scr);
    }

    public static void RemoveProperties(Scanner scr) throws IOException {
        // Call print "Properties" and try to remove one from profiles in file.
        PrintProperties(scr);
        System.out.println("Select number of removed profile, 0 return");
        int removeId = scr.nextInt();
        if (removeId != 0) try {
            Stream<String> stream = Files.lines(Paths.get("properties"));
            String[] stringArr = stream.toArray(String[]::new);
            ArrayList<String> newStrArr = new ArrayList<String>();
            removeId = removeId - 1;
            for (int i = 0; i < stringArr.length; i++) {
                if (i != removeId) {
                    newStrArr.add(stringArr[i]);
                }
            }
            stream.close();


            Writer writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("properties", false));
                StringBuilder sb = new StringBuilder();
                for (String s : newStrArr) {
                    sb.append(s).append("\n");
                }
                writer.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) try {
                    writer.close();
                    System.out.println("Profile " + (removeId + 1) + " removed!");
                } catch (IOException ignore) {
                }
            }
            Program.MenuProperties(scr);
        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            RemoveProperties(scr);
        }
    }

    public static void PrintProperties(Scanner scr) throws IOException {
        // Try streamer file with db properties and out.
        try {
            Stream<String> stream = Files.lines(Paths.get("properties"));
            String[] strArr = stream.toArray(String[]::new);
            for (int i = 0; i < strArr.length; i++) {
                String[] profile = strArr[i].split(" ");
                System.out.println("Profile :\t" + (i + 1));
                System.out.println("Url :\t" + profile[0]);
                System.out.println("User :\t" + profile[1]);
                System.out.println("Password :\t" + profile[2] + "\n");
            }
        } catch (IOException ex) {
            File properties = new File("properties");
            properties.createNewFile(); // if file already exists will do nothing
            FileOutputStream oFile = new FileOutputStream(properties, false);
            System.out.println("Properties have not profiles, please add one");
            AddProperties(scr);
        }
    }
}
