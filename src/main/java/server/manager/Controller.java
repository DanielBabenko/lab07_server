package server.manager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import server.CurrentUser;
import server.commands.ExecuteScript;
import server.commands.Invoker;
import server.commands.noInputCommands.help.GetHelpCommand;
import server.databaseManager.ConnectionManager;
import server.databaseManager.LabWorksDatabaseManager;
import server.exceptions.NotJsonFile;
import server.inputCmdCollection.InputCommands;
import server.noInputCmdCollection.NoInputCommands;
import server.object.LabWork;
import server.parser.Root;
import server.parser.parserFromJson.ParserFromJson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @see Controller нужен для вызова команд. Ыз него уже происходит вся работа программы.
 * Ключевой класс программы.
 */

public class Controller {
    private final String file;
    private Map<String, Invoker> commands = new HashMap<>(); // Map для команд БЕЗ входных данных, не может быть null
    private Map<String, Invoker> inputCommands = new HashMap<>(); // Map для команд С входными данными, не может быть null
    private HashSet<LabWork> labWorks = new HashSet<>(); // Коллекция объектов, не может быть null
    private ParserFromJson parserFromJson = new ParserFromJson(); // Парсинг в коллекцию. Не может быть null
    private GetHelpCommand help = new GetHelpCommand(new HelperController()); // Не может быть null
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  // Не может быть null
    private HelperController helperController; // Не может быть null
    private Root root; // Не может быть null
    private ExecuteScript executeScript; // Не может быть null

    final String url = "jdbc:postgresql://pg:5432/studs";
    final String user = "s367069";
    final String password = "hB0KuLO460j8BNl8";

    private Server server = new Server();

    ConnectionManager connectionManager = new ConnectionManager(url,user,password);

    LabWorksDatabaseManager labWorksDatabaseManager = new LabWorksDatabaseManager(connectionManager);

    volatile boolean ready = false;

    /**
     * В конструкторе происходит автоматическая проверка json-файла.
     * Если в файле есть хотя бы один обьект класса LabWork он подгружается в коллекцию LabWorks класса
     *
     * @throws FileNotFoundException
     * @see LabWork
     */
    public Controller(String file) throws IOException {
        getExtensionByApacheCommonLib(file);
        this.file = file;
        if (parserFromJson.checkOnEmpty(this.file)) {
            root = parserFromJson.parse(this.file);
            labWorks = root.getLabWorkSet();
        } else {
            root = new Root();
            root.setValid(true);
        }

        this.helperController = new HelperController(this.file, getRoot(), getServer());
    }

//    private void authorize() throws NoSuchAlgorithmException, IOException, SQLException {
//        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Введите имя пользователя");
//        String user = b.readLine().trim();
//        System.out.println("Введите пароль");
//        String password = b.readLine().trim();
//
//
//        Login login = new Login(connectionManager);
//        boolean log = login.saveUser(user,password);
//        if (!log) authorize();
//        CurrentUser now = new CurrentUser(login.getUserID());
//        helperController.setCurrentUser(now);
//    }

    /**
     * Самый главный метод класса, а может и всей программы.
     * Сперва в методе запускается статический метод help.execute
     * Переменная flag нужна чтобы контролировать цикл while
     * Проверяется наличие execute_script на вводе
     *
     * @throws IOException
     */
    public void start() throws IOException, ParseException, ClassNotFoundException, SQLException {
        if (getRoot().getValid()) {
            setExecuteScript(new ExecuteScript(getHelperController()));
            try {
                Connection conn = connectionManager.getConnection();
                if (connectionManager.isConnected()){
                    System.out.println("База данных подключена");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Thread haltedHook = new Thread(() -> {
                try {
                    getHelperController().save();
                    connectionManager.getConnection().close();
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            Runtime.getRuntime().addShutdownHook(haltedHook);
            boolean flag = false;


            helperController.setDbManager(labWorksDatabaseManager);

            while (!flag) {
                System.out.println("The SERVER is RUNNING:");
                String cmd = reformatCmd(getServer().dataFromClient());
                Runnable r = () -> {
                        try {
                            String[] arr = cmd.split(" ", 2);
                            if (arr[0].equals("execute_script")) {
                                getExecuteScript().execute(arr[1]);
                            } else if (arr[0].equals("Exit")) {
                                // close socket connection
                                getHelperController().save();
                                getServer().sentToClient("Работа сервера остановлена.");
                                getServer().getServerSocket().close();
                                System.exit(0);
                            } else if (arr[0].equals("Auth")) {
                                int user_id = Integer.parseInt(arr[1]);
                                CurrentUser currentUser = new CurrentUser(user_id);
                                getHelperController().setCurrentUser(currentUser);
                                getHelperController().userOnBase();
                            } else {
                                searchCommandInCollection(cmd);
                            }
                        } catch (IOException | ParseException e) {
                            throw new RuntimeException();
                        }
                };

                Thread thread = new Thread(r);
                thread.start();


                connectionManager.getConnection().close();
               // getServer().sentToClient("? Если возникли трудности, введите команду help");
            }
        }
    }

    /**
     * В параметры метода передается переменная типа String
     * Цикл foreach проходит по каждому обьекту коллекции commandArrayList, чтобы найти нужную команду
     *
     * @param
     */
    public void searchCommandInCollection(String cmd) throws IOException, ParseException {

        getHelperController().setReader(new BufferedReader(new InputStreamReader(System.in)));

        NoInputCommands noInputCommands = new NoInputCommands(helperController);
        setCommands(noInputCommands.getCommands());

        InputCommands inputCommands = new InputCommands(helperController);
        setInputCommands(inputCommands.getInputCommands());

        boolean flag = true;

        //  No input commands
        for (Map.Entry<String, Invoker> entry : getCommands().entrySet()) {
            String key = entry.getKey();
            if (cmd.equals(key)) {
                System.out.println("Активирована команда " + entry.getValue().getClass().getSimpleName());
                entry.getValue().doCommand(cmd);
                flag = false;
            }
        }

        //если не было совпадений в первом мапе, пробегаемся по мапу команд с аргументами
        for (Map.Entry<String, Invoker> entry : getInputCommands().entrySet()) {
            String commandValue = "";
            String commandKey = "";
            if (cmd.contains(" ")) {
                String[] arr = cmd.split(" ", 2);

                commandKey = arr[0];
                commandValue = arr[1];

            } else {
                commandKey = cmd;
            }
            String key = entry.getKey();
            if (commandKey.equals(key)) {
                System.out.println("Активирована команда " + entry.getValue().getClass().getSimpleName());
                entry.getValue().doCommand(commandValue);
                flag = false;
            }
        }
        if (flag == true){
            getServer().sentToClient("Невалидный ввод данных, повторите попытку.");
        }
    }

    /**
     * Метод форматирует введенные данные, и преобразовывает в нужную форму.
     *
     * @param cmd
     * @return
     */
    private String reformatCmd(String cmd) {
        if (cmd != null && !checkOnExecuteScript(cmd)) {
            if (cmd.contains(" ")) {
                String[] arr = cmd.split(" ", 2);
                cmd = arr[0].replaceAll("_", " ");
                cmd = WordUtils.capitalize(cmd);
                cmd = cmd.replaceAll(" ", "");
                cmd = cmd.concat(" " + arr[1]);
            } else {
                cmd = cmd.replaceAll("_", " ");
                cmd = WordUtils.capitalize(cmd);
                cmd = cmd.replaceAll(" ", "");
            }
        } else {
            return cmd;
        }
        return cmd;
    }

    /**
     * Метод проверяет наличие в введенных данных команду execute_script
     * Если execute_script, то выкидывается true, иначе false.
     */
    private boolean checkOnExecuteScript(String cmd) {
        if (cmd != null) {
            String[] arr = cmd.split(" ", 2);
            return Objects.equals(arr[0], "execute_script");
        }

        return false;
    }

    public void getExtensionByApacheCommonLib(String filename) {
        try {
            if (!FilenameUtils.getExtension(filename).equals("json")) {
                throw new NotJsonFile("Файл должен быть с расширением json");
            }
        } catch (NotJsonFile e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public Root getRoot() {
        return root;
    }

    public void setCommands(Map<String, Invoker> commands) {
        this.commands = commands;
    }

    public Map<String, Invoker> getCommands() {
        return commands;
    }

    public void setInputCommands(Map<String, Invoker> inputCommands) {
        this.inputCommands = inputCommands;
    }

    public Map<String, Invoker> getInputCommands() {
        return inputCommands;
    }

    public HelperController getHelperController() {
        return helperController;
    }

    public ExecuteScript getExecuteScript() {
        return executeScript;
    }

    public void setExecuteScript(ExecuteScript executeScript) {
        this.executeScript = executeScript;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "Controller{" +
                "commands=" + commands +
                ", inputCommands=" + inputCommands +
                ", labWorks=" + labWorks +
                ", parserFromJson=" + parserFromJson +
                ", help=" + help +
                ", reader=" + reader +
                ", helperController=" + helperController +
                ", root=" + root +
                ", executeScript=" + executeScript +
                '}';
    }
}