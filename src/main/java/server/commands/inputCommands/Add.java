package server.commands.inputCommands;

import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class Add extends Invoker {

    private static final String COMMAND_NAME = Add.class.getSimpleName();

    private Command add;

    public Add(Command elementCommand){
        this.add = elementCommand;
    }

    public static String getCommandName() {
        return COMMAND_NAME;
    }


    @Override
    public void doCommand(String e) throws ParseException {
       add.execute();
    }
}
