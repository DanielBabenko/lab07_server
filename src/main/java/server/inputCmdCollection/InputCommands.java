package server.inputCmdCollection;

import server.UpdateCommand;
import server.commands.ExecuteScript;
import server.commands.Invoker;
import server.commands.inputCommands.RemoveById;
import server.commands.inputCommands.Update;
import server.commands.inputCommands.UpdateElementCommand;
import server.manager.HelperController;

import java.util.HashMap;
import java.util.Map;

/**
 * @see InputCommands нужен для хранения команд с входными параметрами.
 */

public class InputCommands {
    private Map<String, Invoker> inputCommands = new HashMap<>(); // Map для хранения команд
    private HelperController helperController; // Объект который раскидывается на все команды.

    /**
     * Конструктор собирает команды в {@link #inputCommands}
     * @param helperController
     */
    public InputCommands(HelperController helperController) {
        this.helperController = helperController;

        //создаём команды и объекты всех наших input - команд

        UpdateCommand updateEl = new UpdateElementCommand(helperController);
        Update update = new Update(updateEl);

        RemoveById removeEl = new RemoveById(helperController);

        ExecuteScript executeScript = new ExecuteScript(helperController);

        //добавляем сюда все команды без входных элементов
        inputCommands.put(removeEl.getCommandName(), removeEl);
        inputCommands.put(update.getCommandName(), update);
        inputCommands.put(executeScript.getCOMMAND_NAME(), executeScript);
    }

    public Map<String, Invoker> getInputCommands() {
        return inputCommands;
    }



    public HelperController getHelperController() {
        return helperController;
    }

    public void setHelperController(HelperController helperController) {
        this.helperController = helperController;
    }
}
