package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class UpdateReport extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspUpdateReport\"";
        inputParams = new String[]{"reportData.id","reportData.reason"};
        outputName = "report";
    }

}
