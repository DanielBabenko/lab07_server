package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;

public class ClearTheCollectionCommand implements Command {
    private HelperController helperController;

    public ClearTheCollectionCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    public void execute()
    {
        try {
            helperController.clearCollection();
        } catch (SQLException | IOException | ConcurrentModificationException e) {
            throw new RuntimeException(e);
        }
    }
}
