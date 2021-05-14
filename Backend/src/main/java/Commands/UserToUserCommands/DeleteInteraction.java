package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class DeleteInteraction extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteInteraction\"";
        inputParams = new String[]{"interactionData.id"};
        outputName = "interaction";
    }

}
