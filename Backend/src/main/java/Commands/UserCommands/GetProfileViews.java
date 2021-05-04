package Commands.UserCommands;

import Interface.ConcreteCommand;

public class GetProfileViews extends ConcreteCommand {
    @Override
    public void setParameters(){
        type="find";
        model = "ProfileViews";
        inputParams= new String[]{"profileViewsData.id"};
        collection  = "profileViews";
    }

}
