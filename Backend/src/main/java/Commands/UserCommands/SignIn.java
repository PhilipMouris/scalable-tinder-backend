package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import org.json.JSONObject;

public class SignIn extends ConcreteCommand {

    @Override
    protected HttpResponseTypes doCommand() {
        String res = true + "";
        JSONObject response  = new JSONObject();
        response.put("success",res);
        //responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
        return HttpResponseTypes._200;

    }
}
