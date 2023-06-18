package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

import java.sql.SQLException;

public class ClearTheCollectionCommand implements Command {
    private HelperController helperController;

    public ClearTheCollectionCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    public void execute()
    {
        try {
            helperController.clearCollection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
