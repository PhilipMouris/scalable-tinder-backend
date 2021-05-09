package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class GetSourceInteractions extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadUserSourceInteractions\"";
        inputParams = new String[]{"interactionData.source_id","page","limit"};
        outputName = "interactions";
        useCache=true;
    }

}
