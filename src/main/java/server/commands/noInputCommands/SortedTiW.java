package server.commands.noInputCommands;


import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class SortedTiW extends Invoker {
    private Command sortTiW;
    private static final String COMMAND_NAME = "PrintFieldAscendingTunedInWorks";


    public static String getCommandName() {
        return COMMAND_NAME;
    }


    public SortedTiW(Command sortTiW){
        this.sortTiW = sortTiW;
    }

    public void printFieldAscendingTunedInWorks() throws ParseException {
        sortTiW.execute();
    }

    @Override
    public void doCommand(String e) throws ParseException {
        sortTiW.execute();
    }
}
