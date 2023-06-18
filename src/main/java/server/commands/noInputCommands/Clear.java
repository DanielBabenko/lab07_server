package server.commands.noInputCommands;

import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class Clear extends Invoker {
    private Command clearUpCommand;
    private static final String COMMAND_NAME = Clear.class.getSimpleName();


    public static String getCommandName() {
        return COMMAND_NAME;
    }


    public Clear(Command clearUpCommand){
        this.clearUpCommand = clearUpCommand;
    }

    public void clear() throws ParseException {
        clearUpCommand.execute();
    }

    @Override
    public void doCommand(String e) throws ParseException {
        clearUpCommand.execute();
    }
}
