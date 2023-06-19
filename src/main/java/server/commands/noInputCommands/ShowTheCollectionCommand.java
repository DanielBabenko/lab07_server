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
        try {
            getHelperController().show();
        } catch (NullX e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidFieldY e) {
            throw new RuntimeException(e);
        }
    }

    public HelperController getHelperController() {
        return helperController;
    }
}
