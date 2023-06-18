package server.commands.inputCommands;

import server.Command;
import server.manager.HelperController;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class AddIfMaxCommand implements Command {
    private HelperController helperController;

    public AddIfMaxCommand(HelperController helperController) {
        this.helperController = helperController;

    }

    @Override
    public void execute() {
        try {
            helperController.addIfMax();
        } catch (IOException | ParseException | ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
