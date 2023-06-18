package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

public class MaxByAuthorCommand implements Command {
    private HelperController helperController;

    public MaxByAuthorCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void execute() {
        helperController.maxByAuthor();
    }
}
