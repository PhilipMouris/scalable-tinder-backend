package Commands.UserToUserCommands;

import Interface.ConcreteCommand;

public class DeleteProfileViews extends ConcreteCommand {
    @Override
    public void setParameters() {
        type = "delete";
        model = "ProfileViews";
        inputParams = new String[]{"profileViewsData.id"};
        collection = "profileViews";
    }
}