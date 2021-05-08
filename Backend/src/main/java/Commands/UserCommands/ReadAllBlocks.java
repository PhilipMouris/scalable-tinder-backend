package Commands.UserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class ReadAllBlocks extends  ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspReadAllBlocks\"";
        inputParams = new String[]{"page","limit"};
        outputName = "block";
        useCache=true;
    }
}
