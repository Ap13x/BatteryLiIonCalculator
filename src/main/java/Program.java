import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Program {
    public static void MainMenu(Scanner scr) throws IOException {
        int choice;
        try {
            do {
                System.out.print("""
                        \t\tMain menu
                                        
                        \t1- Calculator
                        \t2- Database
                        \t3- About
                        \t4- Exit
                        """);
                choice = scr.nextInt();
                if (choice < 1 || choice > 4) {
                    System.out.println("Non-existent menu item\n");
                }
            }
            while (choice < 1 || choice > 4);

            switch (choice) {
                case 1:
                    Program.MenuCalculator(scr);
                    break;
                case 2:
                    Program.MenuDatabase(scr);
                    break;
                case 3:
                    Program.About(scr);
                    break;
                case 4:
                    System.exit(1);
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            Program.MainMenu(scr);
        }
    }

    public static void MenuCalculator(Scanner scr) throws IOException {
        int choice;
        try {
            do {
                System.out.print("""
                        \t\tCalculator menu
                                    
                        \t1- Constructor ACB
                        \t2- Construct by criteria
                        \t3- Back to main menu
                            """);
                choice = scr.nextInt();
                if (choice < 1 || choice > 3) {
                    System.out.println("Non-existent menu item\n");
                }
            }
            while (choice < 1 || choice > 3);

            switch (choice) {
                case 1:
                    ManagerDB.Calc1(scr);
                    break;
                case 2:
                    ManagerDB.Calc2();
                    break;
                case 3:
                    Program.MainMenu(scr);
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            Program.MenuCalculator(scr);
        }
    }

    public static void MenuDatabase(Scanner scr) throws IOException {
        int choice;
        try {
            do {
                System.out.print("""
                        \t\tDatabase menu
                                    
                        \t1- Print DB
                        \t2- Properties
                        \t3- Make DB table
                        \t4- Drop DB table
                        \t5- Get values from the site
                        \t6- Back to main menu
                            """);
                choice = scr.nextInt();
                if (choice < 1 || choice > 6) {
                    System.out.println("Non-existent menu item");
                }
            }
            while (choice < 1 || choice > 6);

            switch (choice) {
                case 1:
                    ManagerDB.PrintDB(scr);
                    break;
                case 2:
                    Program.MenuProperties(scr);
                    break;
                case 3:
                    ManagerDB.MakeTable(scr);
                    break;
                case 4:
                    ManagerDB.DropTable(scr);
                    break;
                case 5:
                    ParserRcs.main();
                    break;
                case 6:
                    Program.MainMenu(scr);
                    break;
            }

        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            Program.MenuDatabase(scr);
        }
    }

    public static void MenuProperties(Scanner scr) throws IOException {
        int choice;
        try {
            do {
                System.out.print("""
                        \t\tProperties menu
                                    
                        \t1- Select
                        \t2- Add
                        \t3- Remove
                        \t4- Back to Database menu
                            """);
                choice = scr.nextInt();
                if (choice < 1 || choice > 4) {
                    System.out.println("Non-existent menu item");
                }
            }
            while (choice < 1 || choice > 4);

            switch (choice) {
                case 1:
                    ManagerDB.SelectProperties(scr);
                    break;
                case 2:
                    ManagerDB.AddProperties(scr);
                    break;
                case 3:
                    ManagerDB.RemoveProperties(scr);
                    break;
                case 4:
                    Program.MenuDatabase(scr);
                    break;
            }

        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            Program.MenuProperties(scr);
        }
    }

    public static void About(Scanner scr) throws IOException {
        int choice;
        try {
            do {
                System.out.print("""
                        \t\tAbout
                                    
                        \tBattery calculator 2022
                        \tcalculator that counts battery assemblies from cells presented on the rcscomponent website created by Fillfacks.
                                    
                        \t1- Back to main menu
                            """);
                choice = scr.nextInt();

                if (choice != 1) {
                    System.out.println("Non-existent menu item");
                }
            }
            while (choice != 1);

            Program.MainMenu(scr);
        } catch (InputMismatchException e) {
            System.out.println("Wrong input! Input only integer numbers please...\n");
            scr.next();
            Program.About(scr);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scr = new Scanner(System.in);
        while (true) {
            Program.MainMenu(scr);
        }
    }
}

