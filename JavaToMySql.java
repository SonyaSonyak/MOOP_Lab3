package lab;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class JavaToMySql {

    public static void main(String[] args) throws Exception {

        runTestsLab3();
    }

    public static void lab1() throws Exception {
        System.out.println("Lab 1, Variant 5: Car Showroom");
        System.out.println("");

        runTestsLab2();
    }

    public static void runTestsLab2() throws Exception {
        System.out.println("Start testing manufacturers");
        testManufacturers();
        System.out.println("");

        System.out.println("Start testing models");
        testModels();
    }

    public static void runTestsLab3() throws Exception {
        System.out.println("Lab 3, Variant 5: Car Showroom");
        Socket clientSocket = null;
        BufferedReader reader;
        BufferedReader in = null;
        BufferedWriter out = null;

        while (true) {
            try {
                try {
                    clientSocket = new Socket("localhost", 8080);
                    reader = new BufferedReader(new InputStreamReader(System.in));
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    System.out.println("Input your query:");
                    String word = reader.readLine();

                    out.write(word + "\n");
                    out.flush();

                    if (word.equals("exit")) {
                        break;
                    }

                    String response = "";
                    String serverWord = in.readLine();
                    while (!serverWord.isEmpty() && !serverWord.contains("null")) {
                        response += serverWord;
                        serverWord = in.readLine();
                        serverWord = "\n" + serverWord;
                    }
                    System.out.println(response);
                } finally {
                    clientSocket.close();
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static void testModels() throws Exception {
        Model m = new Model("CarShowroom", "localhost", 3306);
        m.showModels();
        System.out.println("");

        Scanner in = new Scanner(System.in);

        // show models count by manufacturer
        {
            m.countModelsByManufacturer();
        }

        m.showModelsByManufacturer();

        // find models by manufacturer id
        {
            System.out.println("Do You want to find models by manufacturer id (y/n)?");
            String find = in.next();

            if (find.equals("y")) {
                System.out.println("Do You want to see list of manufacturers (y/n)?");
                String ans = in.next();

                if (ans.equals("y")) {
                    Manufacturer manufacturer = new Manufacturer("CarShowroom", "localhost", 3306);
                    manufacturer.showManufacturers();
                    System.out.println("");
                }

                System.out.println("Enter manufacturer id: ");
                int man_id = in.nextInt();

                m.findModelsByManID(man_id);
                System.out.println("");
            }
        }

        // add models
        {
            System.out.println("Do You want to add models (y/n)?");
            String add = in.next();

            if (add.equals("y")) {
                m.addModel("EC8", 14, 4, 2020, 18, 1);
                m.addModel("i50", 3, 1, 2020, 12, 3);

                m.showModels();
                System.out.println("");

            }
        }

        // update models
        {
            System.out.println("Do You want to update models (y/n)?");
            String s = in.next();
            if (s.equals("y")) {
                System.out.println("How many models you want to update?");
                int count_to_update = in.nextInt();

                for (int i = 0; i < count_to_update; ++i) {
                    System.out.println("Enter model id: ");
                    int id = in.nextInt();

                    System.out.println("Enter model`s new name (0 - for do not change name): ");
                    String name = in.next();

                    if (name.equals("0")) {
                        name = new String();
                    }

                    int manufacturer_id = 0, color_id = 0, year = 0, engine_capacity = 0, count = 0;

                    System.out.println("Enter new ManufacturerID (0 - for do not change): ");
                    manufacturer_id = in.nextInt();

                    System.out.println("Enter new ColorID (0 - for do not change): ");
                    color_id = in.nextInt();

                    System.out.println("Enter new Year (0 - for do not change): ");
                    year = in.nextInt();

                    System.out.println("Enter new EngineCapacity (0 - for do not change): ");
                    engine_capacity = in.nextInt();

                    System.out.println("Enter new Count ('-1' - for do not change): ");
                    count = in.nextInt();

                    m.updateModel(id, name, manufacturer_id, color_id, year, engine_capacity, count);
                }

                m.showModels();
                System.out.println("");
            }
        }

        // delete models
        {
            System.out.println("How many models you want to delete?");
            int count_to_delete = in.nextInt();

            if (count_to_delete != 0) {
                System.out.println("Which " + count_to_delete + " models you want to delete (id)?");
            }

            for (int i = 0; i < count_to_delete; ++i) {
                int model_id = in.nextInt();
                m.deleteModel(model_id);
            }
        }

        m.showModels();
        m.stop();
    }

    public static void testManufacturers() throws Exception {
        Manufacturer m = new Manufacturer("CarShowroom", "localhost", 3306);
        m.showManufacturers();
        System.out.println("");

        Scanner in = new Scanner(System.in);

        // add manufacturers
        {
            System.out.println("Do You want to add manufacturers (y/n)?");
            String add = in.nextLine();
            if (add.equals("y")) {
                Calendar calendar = new GregorianCalendar(1920, 1, 30);
                m.addManufacturer("Mazda", calendar);
                calendar = new GregorianCalendar(1997, 3, 18);
                m.addManufacturer("Cherry", calendar);

                m.showManufacturers();
            }
        }

        // update manufacturers
        {
            System.out.println("Do You want to update manufacturers (y/n)?");
            String s = in.nextLine();
            if (s.equals("y")) {
                System.out.println("How many manufacturers you want to update?");
                int count_to_update = in.nextInt();

                for (int i = 0; i < count_to_update; ++i) {
                    System.out.println("Enter manufacturer id: ");
                    int id = in.nextInt();

                    System.out.println("Enter manufacturer new name (0 - for do not change name): ");
                    String name = in.next();

                    if (name.equals("0")) {
                        name = new String();
                    }

                    String date;
                    int year, month, day;
                    System.out.println("Do You want to set new foundation date (y/n): ");
                    String q = in.next();
                    if (q.equals("y")) {
                        System.out.println("Enter new foundation year: ");
                        year = in.nextInt();

                        System.out.println("Enter new foundation month: ");
                        month = in.nextInt();

                        System.out.println("Enter new foundation day: ");
                        day = in.nextInt();

                        String month_q;
                        if (month < 10) {
                            month_q = "0" + String.valueOf(month);
                        } else {
                            month_q = String.valueOf(month);
                        }

                        String day_q;
                        if (day < 10) {
                            day_q = "0" + String.valueOf(day);
                        } else {
                            day_q = String.valueOf(day);
                        }

                        date = String.valueOf(year) + '-' + month_q + '-' + day_q;
                    } else {
                        date = m.getManufacturerFoundationDate(id);
                    }

                    m.updateManufacturer(id, name, date);
                }

                m.showManufacturers();
            }
        }

        // delete manufacturers
        {
            System.out.println("How many manufacturers you want to delete?");
            int count_to_delete = in.nextInt();

            if (count_to_delete != 0) {
                System.out.println("Which " + count_to_delete + " manufacturers you want to delete (id)?");
            }

            for (int i = 0; i < count_to_delete; ++i) {
                int man_id = in.nextInt();
                m.deleteManufacturer(man_id);
            }

            m.showManufacturers();
        }

        m.stop();
    }
}
