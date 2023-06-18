package server.commands.noInputCommands;

import server.Command;
import server.commands.Invoker;

import java.text.ParseException;


public class MaxByAuthor extends Invoker {
    private Command maxAuthorCommand;
    private static final String COMMAND_NAME = MaxByAuthor.class.getSimpleName();

    public static String getCommandName() {
        return COMMAND_NAME;
    }


    public MaxByAuthor(Command maxAuthorCommand){
        this.maxAuthorCommand = maxAuthorCommand;
    }

    public void maxByAuthor() throws ParseException {
       maxAuthorCommand.execute();
    }

    @Override
    public void doCommand(String e) throws ParseException {
        maxAuthorCommand.execute();
    }
}
