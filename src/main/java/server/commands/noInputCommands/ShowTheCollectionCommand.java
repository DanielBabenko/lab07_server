package server.commands.noInputCommands;


import server.Command;
import server.manager.HelperController;

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
