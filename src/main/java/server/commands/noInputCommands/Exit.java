package server.commands.noInputCommands;

import server.commands.Invoker;
import server.manager.HelperController;

public class Exit extends Invoker {
    private static final String COMMAND_NAME = Exit.class.getSimpleName();
    private HelperController helperController;
    public Exit(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void doCommand(String e) {
        execute();
    }

    public void execute(){
        System.exit(0);
    }

    public String getCommandName() {
        return COMMAND_NAME;
    }
}
