package server.commands.noInputCommands;

import server.commands.Invoker;
import server.manager.HelperController;

import java.io.IOException;

public class Save extends Invoker {
    private HelperController helperController;

    private static final String COMMAND_NAME = Save.class.getSimpleName();

    public Save(HelperController helperController) {
        this.helperController = helperController;
    }

    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void doCommand(String e) {
        try {
            helperController.save();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
