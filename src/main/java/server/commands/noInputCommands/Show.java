package server.commands.noInputCommands;


import server.Command;
import server.commands.Invoker;

import java.text.ParseException;

public class Show extends Invoker {
    private Command showTheSummary;
    private static final String COMMAND_NAME = Show.class.getSimpleName();


    public static String getCommandName() {
        return COMMAND_NAME;
    }

    public Show(Command showTheSummary){
        this.showTheSummary = showTheSummary;
    }

    public void show() throws ParseException {
        showTheSummary.execute();
    }

    @Override
    public void doCommand(String e) throws ParseException {
        show();
    }
}
