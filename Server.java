package lab;

import lab.Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class Server {

    private static Socket clientSocket;
    private static ServerSocket server;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) throws Exception {
        try {
            try {
                server = new ServerSocket(8080);

                System.out.println("Server is running!");

                while (true) {
                    clientSocket = server.accept();
                    try {
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                        String word = in.readLine();
                        System.out.println("Server received msg: " + word);

                        if (word.equals("exit")) {
                            break;
                        }

                        out.write(processClientMessage(word) + "\n");
                        out.flush();
                    } finally {
                        clientSocket.close();
                        in.close();
                        out.close();
                    }
                }
            } finally {
                System.out.println("Shutting down the server!");
                server.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static String processClientMessage(String mes) throws Exception {
        Operation type;
        int index = 0;
        System.out.println("Operation: ");
        if (mes.indexOf("Add") == 0) {
            type = Operation.Add;
            index += "Add".length();
            System.out.println("Add");
        } else if (mes.indexOf("Delete") == 0) {
            type = Operation.Delete;
            index += "Delete".length();
            System.out.println("Delete");
        } else if (mes.indexOf("Update") == 0) {
            type = Operation.Update;
            index += "Update".length();
            System.out.println("Update");
        } else if (mes.indexOf("Calculate") == 0) {
            type = Operation.Calculate;
            index += "Calculate".length();
            System.out.println("Calculate");
        } else if (mes.indexOf("Show") == 0) {
            type = Operation.Show;
            index += "Show".length();
            System.out.println("Show");
        } else {
            type = Operation.Unknown;
            System.out.println("Unknown");
        }
        if (type != Operation.Calculate) {
            ++ index; // space after operation type in client msg
        }
        System.out.println("index = " + index);

        System.out.println("Object is ");
        Object obj = Object.Unknown;
        ShowType show_type = ShowType.Unknown;
        switch (type) {
            case Add:
            case Update:
            case Delete:
                if (mes.indexOf("Model", index) == index) {
                    obj = Object.Model;
                    index += "Model".length();
                    System.out.println("Model");
                } else if (mes.indexOf("Manufacturer", index) == index) {
                    obj = Object.Manufacturer;
                    index += "Manufacturer".length();
                    System.out.println("Manufacturer");
                }
                ++ index; // space after object type in client msg
                break;
            case Calculate:
                break;
            case Show:
                System.out.println("Show type is ");
                if (mes.indexOf("ManufacturerList", index) == index) {
                    show_type = ShowType.ManufacturerList;
                    index += "ManufacturerList".length();
                    System.out.println("ManufacturerList");
                } else if (mes.indexOf("ModelsWithManufacturer", index) == index) {
                    show_type = ShowType.ModelsWithManufacturer;
                    index += "ModelsWithManufacturer".length();
                    System.out.println("ModelsWithManufacturer");
                } else if (mes.indexOf("ModelsByManufacturer", index) == index) {
                    show_type = ShowType.ModelsByManufacturer;
                    index += "ModelsByManufacturer".length();
                    ++index;
                    System.out.println("ModelsByManufacturer");
                }
                break;
            case Unknown:
                return "Wrong operation type provided. Try again.";
        }

        if (obj == Object.Unknown && show_type == ShowType.Unknown && type != Operation.Calculate) {
            return "Wrong client message. Try again";
        }

        if (type == Operation.Add || type == Operation.Update || type == Operation.Delete
                || show_type == ShowType.ModelsByManufacturer) {
            return makeQuery(type, obj, show_type, mes.substring(index));
        } else {
            return makeQuery(type, obj, show_type, "");
        }
    }

    public static String makeQuery(Operation type, Object obj, ShowType show_type, String query) throws Exception {
        Model model = new Model("CarShowroom", "localhost", 3306);
        Manufacturer manufacturer = new Manufacturer("CarShowroom", "localhost", 3306);
        switch (obj) {
            case Model:
                switch (type) {
                    case Add:
                    {
                        // String name, int man_id, int col_id, int year, int eng_cap, int count
                        int index = 0;
                        String name = "";
                        // man_id, col_id, year, eng_cap, count;
                        Vector<Integer> vals = new Vector<>();

                        // read query
                        {
                            int ind = query.indexOf(" ");
                            if (ind != -1) {
                                name = query.substring(0, ind);
                                index += ind;
                                ++ index;
                            }
                            System.out.println("index = " + index);

                            try {
                                for (int i = 0; i < 4; ++i) {
                                    ind = query.indexOf(" ", index);
                                    if (ind != -1) {
                                        vals.add(Integer.parseInt(query.substring(index, ind)));
                                        index = ind + 1;
                                    }

                                    if (i == 3) {
                                        // add last number (no space after last number)
                                        vals.add(Integer.parseInt(query.substring(index)));
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                return "Error while processing query: " + e.getMessage();
                            }
                        }

                        boolean is_ok = model.addModel(name, vals.elementAt(0), vals.elementAt(1),
                                vals.elementAt(2), vals.elementAt(3), vals.elementAt(4));

                        if (is_ok) {
                            return "Model added successfully";
                        } else {
                            return "Error while adding model";
                        }
                    }
                    case Update:
                    {
                        // String name, int man_id, int col_id, int year, int eng_cap, int count
                        int index = 0;
                        String name = "";
                        // man_id, col_id, year, eng_cap, count;
                        Vector<Integer> vals = new Vector<>();

                        // read query
                        {
                            int ind = query.indexOf(" ");
                            if (ind != -1) {
                                vals.add(Integer.parseInt(query.substring(0, ind)));
                                index = (ind + 1);
                            }

                            ind = query.indexOf(" ", index);
                            if (ind != -1) {
                                name = query.substring(index, ind);
                                index = (ind + 1);
                            }

                            try {
                                for (int i = 0; i < 4; ++i) {
                                    ind = query.indexOf(" ", index);
                                    if (ind != -1) {
                                        vals.add(Integer.parseInt(query.substring(index, ind)));
                                        index = (ind + 1);
                                    }

                                    if (i == 3) {
                                        // add last number (no space after last number)
                                        vals.add(Integer.parseInt(query.substring(index)));
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                return "Error while processing query";
                            }
                        }

                        boolean is_ok = model.updateModel(vals.elementAt(0), name, vals.elementAt(1), vals.elementAt(2),
                                vals.elementAt(3), vals.elementAt(4), vals.elementAt(5));

                        if (is_ok) {
                            return "Model updated successfully";
                        } else {
                            return "Error while updating model";
                        }
                    }
                    case Delete:
                    {
                        int index = 0;
                        int id = 0;

                        // read query
                        {
                            int ind = query.indexOf(" ");
                            if (ind != -1) {
                                id = Integer.parseInt(query.substring(0, ind));
                            } else {
                                id = Integer.parseInt(query);
                            }
                        }

                        boolean is_ok = model.deleteModel(id);

                        if (is_ok) {
                            return "Model deleted succsessfully";
                        } else {
                            return "Error while deleting model";
                        }
                    }
                    default:
                        return "Wrong operation for Model object.";
                }
            case Manufacturer:
                switch (type) {
                    case Add:
                    {
                        // String name, Calendar foundation_date (year, month, day)
                        int index = 0;
                        String name = "";
                        Vector<Integer> vals = new Vector<>();

                        // read query
                        {
                            int ind = query.indexOf(" ");
                            if (ind != -1) {
                                name = query.substring(0, ind);
                                index += ind;
                                ++ index;
                            }
                            System.out.println("index = " + index);

                            try {
                                for (int i = 0; i < 3; ++i) {
                                    ind = query.indexOf(" ", index);
                                    if (ind != -1) {
                                        vals.add(Integer.parseInt(query.substring(index, ind)));
                                        index = ind + 1;
                                    }

                                    if (i == 2) {
                                        // add last number (no space after last number)
                                        vals.add(Integer.parseInt(query.substring(index)));
                                        break;
                                    }
                                }

                                System.out.println("Foundation date:");
                                for (var el : vals) {
                                    System.out.println(el);
                                }

                            } catch (Exception e) {
                                return "Error while processing query: " + e.getMessage();
                            }
                        }

//                        System.out.println("");
                        Calendar calendar = new GregorianCalendar(vals.elementAt(0), vals.elementAt(1) - 1,
                                vals.elementAt(2));
                        boolean is_ok = manufacturer.addManufacturer(name, calendar);

                        if (is_ok) {
                            return "Manufacturer added successfully";
                        } else {
                            return "Error while adding manufacturer";
                        }

                    }
                    case Delete:
                    {
                        int id = 0;

                        // read query
                        {
                            int ind = query.indexOf(" ");
                            if (ind != -1) {
                                id = Integer.parseInt(query.substring(0, ind));
                            } else {
                                id = Integer.parseInt(query);
                            }
                        }

                        boolean is_ok = manufacturer.deleteManufacturer(id);

                        if (is_ok) {
                            return "Manufacturer deleted successfully";
                        } else {
                            return "Error while deleting manufacturer";
                        }
                    }
                    default:
                        return "Wrong operation for Manufacturer object.";
                }
            case Unknown:
                switch (type) {
                    case Show:
                        switch (show_type) {
                            case ManufacturerList:
                                return manufacturer.showManufacturers();
                            case ModelsByManufacturer:
                            {
                                int man_id = 0;

                                // read query
                                {
                                    int ind = query.indexOf(" ");
                                    if (ind != -1) {
                                        man_id = Integer.parseInt(query.substring(0, ind));
                                    } else {
                                        man_id = Integer.parseInt(query);
                                    }
                                }

                                return model.findModelsByManID(man_id);
                            }
                            case ModelsWithManufacturer:
                                return model.showModelsByManufacturer();
                            default:
                                return "Wrong show type provided";
                        }
                    case Calculate:
                        return model.countModelsByManufacturer();
                    default:
                        return "Wrong operation for non-selected object.";
                }
            default:
                return "Something went wrong";
        }
    }

    public static enum Operation {
        Add,
        Delete,
        Update,
        Calculate,
        Show,
        Unknown
    };

    public static enum Object {
        Model,
        Manufacturer,
        Unknown
    };

    public static enum ShowType {
        ManufacturerList,
        ModelsWithManufacturer,
        ModelsByManufacturer,
        Unknown
    }
}