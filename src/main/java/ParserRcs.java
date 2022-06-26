import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserRcs {

    public static void main(Scanner scr) throws IOException {
        try {
            ManagerDB.DropTable(scr);
            System.out.println("\t\tTable dropped");
        } catch (Exception ignored) {
        } finally {
            ManagerDB.MakeTable(scr);
            System.out.println("\t\tNew table created!");
        }

        System.out.println("\t\tParsing...");

        for (int i = 0; i < 3; i++) {
            Document rcscomponents = Jsoup.connect("https://www.rcscomponents.kiev.ua/modules.php?name=Asers_Shop&s_op=search&query=li-pol%20mAh&page=" + i).get();
            Element table = rcscomponents.select("#main_view > div > div.panel-body > div:nth-child(2) > div.productlist_view > table > tbody").first();

            try {
                assert table != null;
                Elements rows = table.select("tr");

                for (int j = 1; j < rows.size(); j++) {
                    Elements info = rcscomponents.select("#main_view > div > div.panel-body > div:nth-child(2) > div.productlist_view2 > div:nth-child(" + j + ") > div");
                    Pattern p = Pattern.compile("(Виробник|Типорозмір / габарити|Ємність|Форма|Напруга, V|Максимальний струм розряду|Вага):\\s*([^\\s\"']+|\"([^\"]*)\"|'([^']*)')");
                    Matcher m = p.matcher((Objects.requireNonNull(info.first())).text());
                    String vend = "0";
                    String sizeString = "0";
                    String form = "0";
                    int cap = 0;
                    float weig = 0.0f;
                    float vol = 0.0f;
                    float minVolt = 0.0f;

                    for (int k = 0; k < info.size(); k++) {

                        while (m.find()) {

                            switch (m.group(1)) {
                                case ("Виробник"):
                                    vend = m.group(2);
                                    break;
                                case ("Типорозмір / габарити"):
                                    sizeString = m.group(2);
                                    break;
                                case ("Ємність"):
                                    cap = Integer.parseInt(m.group(2).replaceAll("[a-z][A-Z]", " "));
                                    break;
                                case ("Форма"):
                                    form = (m.group(2));
                                    break;
                                case ("Напруга, V"):
                                    vol = Float.parseFloat(m.group(2).replace(',', '.'));
                                    break;
                                case ("Максимальний струм розряду"):
                                    minVolt = Float.parseFloat(m.group(2).replace(',', '.'));
                                    break;
                                case ("Вага"):
                                    weig = Float.parseFloat(m.group(2).replace(',', '.').replace('м', ' '));
                                    break;
                            }
                        }

                        if (!Objects.equals(form, "0")) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("INSERT parsedbatt.rcscomponent (Vendor, Form , Capacity , Sizes, Voltage, MinVoltage, Weight) VALUES ('");
                            sb.append(vend).append("', '").append(form).append("', ").append(cap).append(", '").append(sizeString).append("', ");
                            sb.append(vol).append(", ").append(minVolt).append(", ").append(weig).append(")");
                            ManagerDB.SendDB(sb.toString());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("\tInvalid parse value");
            }
        }
        System.out.println("\n\t\tDB updated!\n");
        Program.MenuDatabase(scr);
    }
}

