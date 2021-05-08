package Commands.UserCommands;

import Interface.ConcreteCommand;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.json.JSONObject;

public class SignUp extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "uspSignUp";
        inputParams = new String[] {"userData.email", "userData.password", "userData.first_name", "userData.last_name"};
        outputName = "user";
        String plainTextPassword = (String) message.getParameter("userData.password");
        String hashedPassword = BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());
        JSONObject newParameters = message.getParameters();
        newParameters.getJSONObject("userData").put("password", hashedPassword);
        message.setParameters(newParameters);
    }
}
