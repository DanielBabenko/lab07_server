package server.commands.noInputCommands;

import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class Info extends Invoker {

    private Command getInfoCommand;
    private static final String COMMAND_NAME = Info.class.getSimpleName();

    public Info(Command getInfoCommand) {
        this.getInfoCommand = getInfoCommand;
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void doCommand(String e) throws ParseException {
        getInfoCommand.execute();
    }


}
