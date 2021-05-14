package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class GetBan extends ConcreteCommand {
   @Override
    public void setParameters() {
        storedProcedure = "\"uspReadBan\"";
        inputParams = new String[]{"banData.id"};
        outputName = "ban";
    }
}
