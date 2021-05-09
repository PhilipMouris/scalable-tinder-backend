package Commands.UserToUserCommands;

import Interface.ConcreteCommand;

public class GetAllProfileViews extends ConcreteCommand {
    @Override
    public void setParameters() {
        type = "findAll";
        model = "ProfileViews";
        inputParams = new String[]{"limit", "page"};
        collection = "profileViews";
        outputName = "profileViews";
        useCache=true;
    }
}
