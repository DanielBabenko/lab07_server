package server.commands.noInputCommands;


import server.Command;
import server.manager.HelperController;

public class PrintUniqueTiWCommand implements Command {
    private HelperController helperController;

    public PrintUniqueTiWCommand(HelperController helperController) {
        this.helperController = helperController;
    }
    @Override
    public void execute() {
        helperController.printUniqueTunedInWorks();
    }
}
