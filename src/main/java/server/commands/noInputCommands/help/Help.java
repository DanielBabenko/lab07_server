package server.commands.noInputCommands.help;


import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class Help extends Invoker {
    private Command getHelpCommand;
    private static final String COMMAND_NAME = Help.class.getSimpleName();

    public static String getCommandName() {
        return COMMAND_NAME;
    }
    public Help(Command getHelpCommand){
        this.getHelpCommand = getHelpCommand;
    }


    @Override
    public void doCommand(String e) throws ParseException {
        getHelpCommand.execute();
    }
}
