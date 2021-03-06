import java.io.*;
import java.sql.*;
import java.util.*;
public class ManagerDB {

    public static void SendDB(String command) {
        // Try to send SQL command to DB.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            try (Connection conn = DriverManager.getConnection(ManagerProperties.getUrl(), ManagerProperties.getUsername(), ManagerProperties.getPassword())) {
                Statement statement = conn.createStatement();
                statement.executeUpdate(command);
            }
        } catch (Exception e) {
            System.out.println("Connection failed...");
            e.printStackTrace();
        }
    }

    public static void MakeTable(Scanner scr) throws IOException {
        // Try to send SQL command for create new batt table.
        try {
            ManagerDB.SendDB("CREATE TABLE rcscomponent (Id INT PRIMARY KEY AUTO_INCREMENT, Vendor VARCHAR(20), Form VARCHAR(20), Capacity INT, Sizes VARCHAR(20), Voltage DECIMAL(5,2), MinVoltage DECIMAL(5,2), Weight DECIMAL(5,2))");
        } catch (Exception e) {
            System.out.println("Tablet now created or DB unacceptable");
            Program.MenuDatabase(scr);
        }
    }

    public static void DropTable(Scanner scr) throws IOException {
        // Try to send SQL command for drop batt table.
        try {
            ManagerDB.SendDB("DROP TABLE `parsedbatt`.`rcscomponent`");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error, DB unacceptable or table now dropped");
            Program.MenuDatabase(scr);
        }
    }

    public static void PrintDB(Scanner scr) throws IOException {
        // Try to connect to DB and get all pos.from table to print.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(ManagerProperties.getUrl(), ManagerProperties.getUsername(), ManagerProperties.getPassword())) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM rcscomponent");
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String vend = resultSet.getString(2);
                    String form = resultSet.getString(3);
                    int cap = resultSet.getInt(4);
                    String sizes = resultSet.getString(5);
                    float volt = resultSet.getFloat(6);
                    float voltMin = resultSet.getFloat(7);
                    float weig = resultSet.getFloat(8);
                    System.out.printf(" %-4d %-14s - %-15s Capacity: %-3d mAh\t Sizes: %-16s Voltage %1.2fV\tMin %1.2fV\t weight %1.1fg \n", id, vend, form, cap, sizes, volt, voltMin, weig);
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Connection failed... Please check internet connection or try remake DB table");
            Program.MenuDatabase(scr);
        }
    }

    public static void Calc1(Scanner scr) {
        // Try construct ACB by user input settings and out result.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            try (Connection conn = DriverManager.getConnection(ManagerProperties.getUrl(), ManagerProperties.getUsername(), ManagerProperties.getPassword())) {
                Statement statement = conn.createStatement();
                PrintDB(scr);
                try {
                    System.out.println("Select cell`s id");
                    int u = scr.nextInt();
                    ResultSet resultSelected = statement.executeQuery("SELECT * FROM rcscomponent WHERE Id = " + u + "");
                    if (resultSelected.next()) {
                        String vend = resultSelected.getString(2);
                        int cap = resultSelected.getInt(4);
                        String sizes = resultSelected.getString(5);
                        float volt = resultSelected.getFloat(6);
                        float voltMin = resultSelected.getFloat(7);
                        float weig = resultSelected.getFloat(8);
                        System.out.printf("You select %s\tcapacity %d mAh\t format - %s\n", vend, cap, sizes);
                        System.out.println("select the number of cells connected in series");
                        int series = scr.nextInt();
                        System.out.println("Specify the number of parallel builds");
                        int parallel = scr.nextInt();
                        int cells = series * parallel;
                        volt = (series * volt);
                        voltMin = series * voltMin;
                        cap = cap * parallel;
                        weig = cells * weig;
                        try {
                            String[] sizeSplit = sizes.replace(',', '.').replace('??', ' ').replace('??', 'x').split("x");
                            Float[] sizeFloat = Arrays.stream(sizeSplit).map(Float::valueOf).toArray(Float[]::new);
                            Arrays.sort(sizeFloat);
                            sizes = ((sizeFloat[0] * cells) + " x " + sizeFloat[1] + " x " + sizeFloat[2] + " mm");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        System.out.print(
                                "\n" +
                                        "Settlement done!\n" +
                                        "Received battery\n" +
                                        "\n" +
                                        "================================\n" +
                                        "Li-pol," + series + "S " + cells + " cells\n" +
                                        "Voltage/MinVoltage\t" + volt + " v / " + voltMin + " v\n" +
                                        "Capacity\t\t\t" + cap + " mAh\n" +
                                        "Size\t\t\t\t" + sizes + "\n" +
                                        "Weight\t\t\t\t" + weig + " g\n" +
                                        "================================\n" +
                                        "\n"
                        );
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Wrong input! Input only integer numbers please...\n");
                    scr.next();
                    Calc1(scr);
                }

                Program.MenuCalculator(scr);
            }
        } catch (Exception e) {
            System.out.println("Connection failed...");
            e.printStackTrace();
        }

    }

    public static void Calc2() {
        try {
            // Try automated construct ACB by capacity and cells.
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            Scanner scr = new Scanner(System.in);
            try (Connection conn = DriverManager.getConnection(ManagerProperties.getUrl(), ManagerProperties.getUsername(), ManagerProperties.getPassword())) {
                Statement statement = conn.createStatement();
                try {
                    System.out.println("Enter capacity of required assembly (integer mAh)");
                    int uCap = scr.nextInt();
                    System.out.println("Enter the number of parallel cells in assembly (<0)");
                    int uCells = scr.nextInt();
                    ResultSet maxIdSelect = statement.executeQuery("SELECT MAX(Id) FROM rcscomponent");
                    maxIdSelect.next();
                    int maxId = maxIdSelect.getInt(1);
                    int[] cellsArr = new int[maxId + 1];

                    ResultSet capSelect = statement.executeQuery("SELECT (Capacity) FROM rcscomponent");
                    for (int c = 0; c < maxId; c++) {
                        capSelect.next();
                        int cap = capSelect.getInt(1);
                        cellsArr[c] = cap;
                    }

                    int min = Integer.MAX_VALUE;
                    int closestInt = uCap / uCells;
                    int closestId = 0;
                    for (int n = 0; n < maxId; n++) {
                        final int diff = Math.abs(cellsArr[n] - closestInt);
                        if (diff < min) {
                            min = diff;
                            closestId = n;
                        }
                    }

                    ResultSet nearestBattery = statement.executeQuery("SELECT * FROM rcscomponent WHERE Id = " + (closestId + 1) + "");
                    nearestBattery.next();
                    int id = nearestBattery.getInt(1);
                    String vend = nearestBattery.getString(2);
                    String form = nearestBattery.getString(3);
                    int cap = nearestBattery.getInt(4);
                    String sizes = nearestBattery.getString(5);
                    float volt = nearestBattery.getFloat(6);
                    float voltMin = nearestBattery.getFloat(7);
                    float weig = nearestBattery.getFloat(8);
                    if (cellsArr[closestId] * uCells == uCap) {
                        System.out.println("Nearest capacity for this = " + cellsArr[closestId] + "\n" +
                                "The most suitable battery is");
                        System.out.printf("%-4d %-14s - %-15s Capacity: %-3d mAh\t Sizes: %-16s Voltage %1.2fV\tMin %1.2fV\t weight %1.1fg \n", id, vend, form, cap, sizes, volt, voltMin, weig);
                    } else {
                        int offeredCap = 0;
                        int offeredCells = 0;
                        while (offeredCap < uCap) {
                            offeredCap = offeredCap + cellsArr[closestId];
                            offeredCells++;
                        }

                        System.out.println("There are no batteries to assemble with these conditions, the nearest possible have " + (cellsArr[closestId] * uCells) + " mAh");
                        System.out.println("Recommend " + offeredCap + " mAh from " + offeredCells + " parallel cells " + cellsArr[closestId] + "mAh");
                        System.out.printf("%-4d %-14s - %-15s Capacity: %-3d mAh\t Sizes: %-16s Voltage %1.2fV\tMin %1.2fV\t weight %1.1fg \n", id, vend, form, cap, sizes, volt, voltMin, weig);
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Wrong input! Input only integer numbers please...\n");
                    scr.next();
                    Calc2();
                }

                Program.MenuCalculator(scr);
            }
        } catch (Exception e) {
            System.out.println("Connection failed...");
            e.printStackTrace();
        }
    }
}
