package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import org.json.JSONObject;

public class SignIn extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "uspLogin";
        inputParams = new String[] {"userData.email"};
        outputName = "user";
    }

    @Override
    public void doCustomCommand() {
        if(responseJson.getJSONArray("user").getJSONObject(0).length() == 0) {
            sendUnauthorized();
            return;
        }
        String hashedPassword = responseJson.getJSONArray("user").getJSONObject(0).getString("password");
        String plainTextPassword = (String) message.getParameter("userData.password");
        BCrypt.Result result = BCrypt.verifyer().verify(plainTextPassword.toCharArray(), hashedPassword);
        if (result.verified) {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withClaim("userId", responseJson.getJSONArray("user").getJSONObject(0).getInt("id"))
                    .withIssuer("auth0")
                    .sign(algorithm);
            responseJson = new JSONObject();
            responseJson.put("token", token);
        }
        else {
            sendUnauthorized();
            return;
        }
    }
    
    public void sendUnauthorized() {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", "Invalid Credentials");
        responseJson = errorResponse;
        status = HttpResponseTypes._401;
    }
}
