package Commands.UserCommands;

import Interface.ConcreteCommand;

//import Models.User;


public class GetUserDataPaginated extends ConcreteCommand {

    @Override
    public void setParameters(){
        type = "findAll";
        model = "UserData";
        inputParams = new String[]{"limit","page"};
        collection  = "userData";
        outputName="users";
        useCache=true;
    }

}
