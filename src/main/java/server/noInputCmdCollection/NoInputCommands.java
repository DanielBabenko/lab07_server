package server.noInputCmdCollection;

import server.Command;
import server.commands.Invoker;
import server.commands.inputCommands.*;
import server.commands.noInputCommands.*;
import server.commands.noInputCommands.help.GetHelpCommand;
import server.commands.noInputCommands.help.Help;
import server.manager.HelperController;

import java.util.HashMap;
import java.util.Map;

/**
 * @see NoInputCommands служит хранилищем команд без входных данных.
 */

public class NoInputCommands {
    private Map<String, Invoker> commands = new HashMap<>(); // Map для хранения команд
    private HelperController helperController; // Объект который раскидывается на все команды.

    /**
     * Конструктор собирает команды в {@link #commands}
     * @param helperController
     */
    public NoInputCommands(HelperController helperController) {

        this.helperController = helperController;

        Command getHelp = new GetHelpCommand(helperController);
        Help help = new Help(getHelp);

        Command getInfo = new GetInfoCommand(helperController);
        Info info = new Info(getInfo);

        Command showLabs = new ShowTheCollectionCommand(helperController);
        Show s = new Show(showLabs);

        Command clearLabs = new ClearTheCollectionCommand(helperController);
        Clear c = new Clear(clearLabs);

        Command printTiW = new PrintUniqueTiWCommand(helperController);
        UniqueTiW unique = new UniqueTiW(printTiW);

        Command sortTiW = new PrintSortedTiWCommand(helperController);
        SortedTiW sort = new SortedTiW(sortTiW);

        Command maxByAuthor = new MaxByAuthorCommand(helperController);
        MaxByAuthor author = new MaxByAuthor(maxByAuthor);

        Command removeGreaterEl = new RemoveGreaterElementCommand(helperController);
        RemoveGreater greater = new RemoveGreater(removeGreaterEl);

        Command removeLowerEl = new RemoveLowerElementCommand(helperController);
        RemoveLower lower = new RemoveLower(removeLowerEl);

        Command addEl = new AddNewElementCommand(helperController);
        Add a = new Add(addEl);

        Command addElIfMax = new AddIfMaxCommand(helperController);
        AddIfMax addMax = new AddIfMax(addElIfMax);

        Save save = new Save(helperController);

        Exit exit = new Exit(helperController);

        commands.put(help.getCommandName(), help);
        commands.put(c.getCommandName(), c);
        commands.put(unique.getCommandName(), unique);
        commands.put(sort.getCommandName(), sort);
        commands.put(author.getCommandName(), author);
        commands.put(s.getCommandName(), s);
        commands.put(info.getCommandName(), info);
        commands.put(save.getCommandName(), save);
        commands.put(exit.getCommandName(), exit);
        commands.put(greater.getCommandName(), greater);
        commands.put(lower.getCommandName(), lower);
        commands.put(addMax.getCommandName(), addMax);
        commands.put(a.getCommandName(), a);
    }

    public Map<String, Invoker> getCommands() {
        return commands;
    }
}
