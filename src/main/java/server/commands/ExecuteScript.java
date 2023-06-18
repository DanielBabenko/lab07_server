package server.commands;

import org.apache.commons.lang3.text.WordUtils;
import server.inputCmdCollection.InputCommands;
import server.manager.HelperController;
import server.noInputCmdCollection.NoInputCommands;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see ExecuteScript исполняет доступные команды из файла.
 */

public class ExecuteScript extends Invoker {
    private static final String COMMAND_NAME = ExecuteScript.class.getSimpleName(); // Название команды
    private HelperController helperController; // Это поле не может быть null
    Map<String, Invoker> commands = new HashMap<>();  // А этот мап для команд без входных данных
    Map<String, Invoker> inputCommands = new HashMap<>();  // А этот мап для команд С входными данными


    /**
     * Метод инициализирует {@link #helperController}
     * @param helperController
     */
    public ExecuteScript(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void doCommand(String e) {
        try {
            execute(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Метод ищет файл из которого нужно исполнить скрипт.
     * Блокирует любого вида рекурсию.
     * @param pathToFile
     * @throws IOException
     */

    public void execute(String pathToFile) throws IOException, ParseException {
        pathToFile = System.getProperty("user.dir") + "/" + pathToFile;
        System.out.println(pathToFile);
        if (new File(pathToFile).exists()) {
            // настраиваем поток ввода
            getHelperController().setReader(new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile))));

            NoInputCommands noInputCommands = new NoInputCommands(helperController);
            setCommands(noInputCommands.getCommands());

            InputCommands inputCommands = new InputCommands(helperController);
            setInputCommands(inputCommands.getInputCommands());


            // читаем первую строку из файла
            String cmd = getHelperController().getReader().readLine();
            boolean flag = false;

            if (cmd != null) {
                while (cmd != null) {
                    if (!cmd.contains(" ")) {
                        cmd = reformatCmd(cmd);
                        //  No input commands
                        for (Map.Entry<String, Invoker> entry : getCommands().entrySet()) {
                            String key = entry.getKey();
                            if (cmd.equals(key)) {
                                System.out.println("Активирована команда " + entry.getValue().getClass().getSimpleName());
                                entry.getValue().doCommand(cmd);
                                flag = true;
                            }
                        } if (flag == false) {
                            System.out.println("У команды в скрипте не должно быть входных данных!");
                            break;
                        }

                        for (Map.Entry<String, Invoker> entry : getInputCommands().entrySet()) {
                            String key = entry.getKey();
                            if (cmd.equals(key)) {
                                System.out.println("Активирована команда " + entry.getValue().getClass().getSimpleName());
                                entry.getValue().doCommand(cmd);
                            }
                        }
                    } else {
                        cmd = reformatCmd(cmd);
                        String[] command = cmd.split(" ", 2);
                        if (command[0].equals("ExecuteScript")) {
                            if (getHelperController().addToPaths(command[1])) {
                                doCommand(command[1]);
                                getHelperController().addToPaths(command[1]);
                            } else {
                                System.out.println("ты не сможешь сломать её!");
                            }
                        } else {
                            for (Map.Entry<String, Invoker> entry : getInputCommands().entrySet()) {
                                String key = entry.getKey();
                                String commandKey = command[0];
                                String commandValue = command[1];
                                if (commandKey.equals(key)) {
                                    System.out.println("Активирована команда " + entry.getValue().getClass().getSimpleName());
                                    entry.getValue().doCommand(commandValue);
                                }
                            }
                        }
                    }
                    cmd = this.helperController.getReader().readLine();
                }
            }
        } else {
            System.out.println("Файл не найден!");
        }

        // Очищаем коллекцию путей
        getHelperController().getPaths().clear();
    }


    private String reformatCmd(String cmd) {

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
        return cmd;
    }

    public HelperController getHelperController() {
        return helperController;
    }

    public Map<String, Invoker> getInputCommands() {
        return inputCommands;
    }

    public Map<String, Invoker> getCommands() {
        return commands;
    }

    public void setHelperController(HelperController helperController) {
        this.helperController = helperController;
    }

    public void setInputCommands(Map<String, Invoker> inputCommands) {
        this.inputCommands = inputCommands;
    }

    public void setCommands(Map<String, Invoker> commands) {
        this.commands = commands;
    }

    public static String getCOMMAND_NAME() {
        return COMMAND_NAME;
    }
}
