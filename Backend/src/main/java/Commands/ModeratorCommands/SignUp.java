package Commands.ModeratorCommands;

import Interface.ConcreteCommand;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.json.JSONObject;

public class SignUp extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "uspModSignUp";
        inputParams = new String[] {"moderatorData.email", "moderatorData.password"};
        outputName = "moderator";
        String plainTextPassword = (String) message.getParameter("moderatorData.password");
        String hashedPassword = BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());
        JSONObject newParameters = message.getParameters();
        newParameters.getJSONObject("moderatorData").put("password", hashedPassword);
        message.setParameters(newParameters);
    }
}
