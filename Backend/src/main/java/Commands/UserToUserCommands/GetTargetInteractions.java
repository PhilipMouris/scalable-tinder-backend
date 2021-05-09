package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class GetTargetInteractions extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadUserTargetInteractions\"";
        inputParams = new String[]{"interactionData.target_id","page","limit"};
        outputName = "interactions";
        useCache=true;
    }

}
