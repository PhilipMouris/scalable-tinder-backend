package Commands.UserCommands;

import Interface.ConcreteCommand;

public class CreateUserData extends ConcreteCommand {
    @Override
     public void setParameters(){
          type="create";
          model = "UserData";
          inputParams= new String[]{"userData"};
          collection  = "users";
    }

}
