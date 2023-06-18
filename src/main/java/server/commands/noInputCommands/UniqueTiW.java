package server.commands.noInputCommands;


import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class UniqueTiW extends Invoker {
    private Command printTiW;
    private static final String COMMAND_NAME = "PrintUniqueTunedInWorks";

    public static String getCommandName() {
        return COMMAND_NAME;
    }


    public UniqueTiW(Command printTiW){
        this.printTiW = printTiW;
    }

    public void printUniqueTunedInWorks() throws ParseException {
        printTiW.execute();
    }

    @Override
    public void doCommand(String e) throws ParseException {
        printTiW.execute();
    }
}
