package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

import java.io.IOException;

public class GetInfoCommand implements Command {
    private HelperController helperController;

    public GetInfoCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void execute() {
        try {
            this.helperController.getInfo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
