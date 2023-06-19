package server.commands.noInputCommands;


import server.Command;
import server.exceptions.InvalidFieldY;
import server.exceptions.NullX;
import server.manager.HelperController;

import java.sql.SQLException;

public class ShowTheCollectionCommand implements Command {
    private HelperController helperController;

    public ShowTheCollectionCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    public void execute() {
        getHelperController().show();
    }

    public HelperController getHelperController() {
        return helperController;
    }
}
