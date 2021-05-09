package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class GetAllReports extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "uspViewReported";
        inputParams = new String[]{"page","limit"};
        outputName = "reports";
        useCache=true;
    }

}
