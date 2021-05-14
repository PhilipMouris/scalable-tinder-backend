package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class CreateReport extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "uspReportUser";
        inputParams = new String[]{"reportData.source_user_id","reportData.target_user_id","reportData.reason","reportData.created_at"};
        outputName = "report";
    }

}
