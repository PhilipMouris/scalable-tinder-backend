package Commands.UserCommands;

import Interface.ConcreteCommand;

public class CreateProfileView extends ConcreteCommand {
    @Override
    public void setParameters(){
        type="create";
        model = "ProfileViews";
        inputParams= new String[]{"profileViewsData"};
        collection  = "profileViews";
    }

}