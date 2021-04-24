package Commands.UserCommands;

import Interface.ConcreteCommand;
//import Models.User;
import com.google.gson.Gson;
import org.json.JSONObject;

public class SignIn extends ConcreteCommand {

    @Override
    protected void doCommand() {
        String res = true + "";
        JSONObject response  = new JSONObject();
        response.put("success",res);
        responseJson = jsonParser.parse(response.toString());
        System.out.println(response);
    }
}
