package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class GetAllInteractions extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadInteractions\"";
        inputParams = new String[]{"page","limit"};
        outputName = "interactions";
        useCache=true;
    }

}
