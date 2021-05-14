package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class DeleteReport extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteReport\"";
        inputParams = new String[]{"reportData.id"};
        outputName = "report";
    }

}
