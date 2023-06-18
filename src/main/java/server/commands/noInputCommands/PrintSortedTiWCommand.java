package server.commands.noInputCommands;

import server.Command;
import server.manager.HelperController;

public class PrintSortedTiWCommand implements Command {
    private HelperController helperController;

    public PrintSortedTiWCommand(HelperController helperController) {
        this.helperController = helperController;
    }

    @Override
    public void execute()
    {
        helperController.printFieldAscendingTunedInWorks();
    }
}
