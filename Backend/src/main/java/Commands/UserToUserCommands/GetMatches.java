package Commands.UserToUserCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;


public class GetMatches extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspSeeMatchesChronological\"";
        inputParams = new String[]{"userData.id"};
        outputName = "matches";
        useCache=true;
    }

}
