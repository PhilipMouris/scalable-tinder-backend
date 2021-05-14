package Commands.UserToUserCommands;

import Interface.ConcreteCommand;

public class UpdateProfileViews extends ConcreteCommand {
    @Override
    public void setParameters() {
        type = "update";
        model = "ProfileViews";
        inputParams = new String[]{"profileViewsData.id","profileViewsData"};
        collection = "profileViews";
    }
}