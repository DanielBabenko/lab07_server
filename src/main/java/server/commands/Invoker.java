package server.commands;

import java.io.IOException;
import java.text.ParseException;

/**
 * Абстрактный класс для всех invokerov
 */

public abstract class Invoker {

    /**
     * общий метод ля выполнения всех команд. Очень поможет нам для создания полиморфизма при вводе команд
     */
    public abstract void doCommand(String e) throws IOException, ParseException;
}
